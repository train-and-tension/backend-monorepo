package com.traintension.core.model.workoutSession;

import com.traintension.core.generated.enums.SessionType;
import com.traintension.core.generated.enums.WorkoutStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.jooq.Record5;
import org.jooq.impl.DSL;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static com.traintension.core.generated.Tables.USER_PROFILE;
import static com.traintension.core.generated.Tables.WORKOUT_PERIOD;
import static com.traintension.core.generated.Tables.WORKOUT_SESSION;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutSessionScheduler {

    private final DSLContext dsl;
    private final WorkoutSessionCronHelper cronHelper;

    /**
     * set-sessions — 30 dakikada bir çalışır (xx:00 ve xx:30).
     *
     * Her kullanıcı için aktif WorkoutPeriod'a bağlı, WORKOUT veya OFF tipinde,
     * PLANNED statüsünde ve start_date'i geçmişte kalan EN SON (tek) WorkoutSession'ı bulur.
     *
     * - WORKOUT tipindekinin statüsünü MISSED yapar.
     * - OFF tipindekinin statüsünü FINISHED yapar.
     *
     * Ardından set-next-workout-session() mantığını çalıştırır.
     * Son olarak son 3 session kontrolü ile end-old-period() kararını verir.
     */
    @Scheduled(cron = "0 0,30 * * * *") // Her saatin 00. ve 30. dakikasında çalışır
    public void checkAndSetSessions() {
        log.info("Starting scheduled task: checkAndSetSessions");

        // Aktif periyottaki PLANNED + (WORKOUT|OFF) session'ları kullanıcı bilgisiyle çek
        var allCandidates = dsl.select(
                        WORKOUT_SESSION.ID,
                        WORKOUT_SESSION.USER_PROFILE_ID,
                        WORKOUT_SESSION.WORKOUT_PERIOD_ID,
                        WORKOUT_SESSION.TYPE,
                        WORKOUT_SESSION.START_DATE
                )
                .from(WORKOUT_SESSION)
                .join(WORKOUT_PERIOD).on(WORKOUT_SESSION.WORKOUT_PERIOD_ID.eq(WORKOUT_PERIOD.ID))
                .where(WORKOUT_SESSION.STATUS.eq(WorkoutStatus.PLANNED))
                .and(WORKOUT_PERIOD.IS_ACTIVE.isTrue())
                .and(WORKOUT_SESSION.TYPE.in(SessionType.WORKOUT, SessionType.OFF))
                .orderBy(WORKOUT_SESSION.START_DATE.desc())
                .fetch();

        // Kullanıcı bazında timezone bilgisini çekmek için bir map hazırla
        // Her kullanıcı için yalnızca start_date'i en büyük olan (en son planlanan) session'ı işliyoruz
        // LinkedHashMap ile kullanıcı başına tek kayıt tutuyoruz (ilk gelen → en yeni, çünkü DESC sıralı)
        Map<UUID, Record5<UUID, UUID, UUID, SessionType, LocalDate>> perUser = new LinkedHashMap<>();
        for (var record : allCandidates) {
            UUID userId = record.get(WORKOUT_SESSION.USER_PROFILE_ID);
            perUser.putIfAbsent(userId, record);
        }

        for (var entry : perUser.entrySet()) {
            UUID userId = entry.getKey();
            var record = entry.getValue();

            UUID sessionId = record.get(WORKOUT_SESSION.ID);
            UUID periodId = record.get(WORKOUT_SESSION.WORKOUT_PERIOD_ID);
            SessionType sessionType = record.get(WORKOUT_SESSION.TYPE);
            LocalDate sessionStartDate = record.get(WORKOUT_SESSION.START_DATE);

            if (sessionStartDate == null) continue;

            // Kullanıcının timezone bilgisini çek
            String userTimezone = dsl.select(USER_PROFILE.TIMEZONE)
                    .from(USER_PROFILE)
                    .where(USER_PROFILE.ID.eq(userId))
                    .fetchOneInto(String.class);

            ZoneId zoneId;
            try {
                zoneId = ZoneId.of(userTimezone != null ? userTimezone : "UTC");
            } catch (Exception e) {
                log.warn("Invalid timezone '{}' for user {}, defaulting to UTC", userTimezone, userId);
                zoneId = ZoneId.of("UTC");
            }

            LocalDate currentUserDate = ZonedDateTime.now(zoneId).toLocalDate();

            // Kullanıcının yerel saati gece yarısını geçmiş olmalı VE session tarihi geçmişte kalmalı
            if (sessionStartDate.isBefore(currentUserDate)) {
                log.info("Processing passed session {} for user {} (User Date: {}, Session Date: {})",
                        sessionId, userId, currentUserDate, sessionStartDate);
                try {
                    cronHelper.processExpiredSession(sessionId, userId, periodId, sessionType, zoneId.getId());
                } catch (Exception e) {
                    log.error("Error processing passed session {} for user {}: {}", sessionId, userId, e.getMessage(), e);
                }
            }
        }

        log.info("Finished scheduled task: checkAndSetSessions");
    }
}
