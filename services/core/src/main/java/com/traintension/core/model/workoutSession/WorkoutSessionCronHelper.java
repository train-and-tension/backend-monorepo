package com.traintension.core.model.workoutSession;

import com.traintension.core.generated.enums.SessionType;
import com.traintension.core.generated.enums.WorkoutStatus;
import com.traintension.core.model.workoutPeriod.WorkoutPeriodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.traintension.core.generated.Tables.WORKOUT_SESSION;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkoutSessionCronHelper {

    private final DSLContext dsl;
    private final WorkoutSessionService workoutSessionService;
    private final WorkoutPeriodService workoutPeriodService;

    /**
     * Geçmiş tarihli bir session için aşağıdaki adımları uygular:
     *
     * 1. Status güncelleme:
     *    - OFF tipindeki session → FINISHED
     *    - WORKOUT tipindeki session → MISSED
     *
     * 2. set-next-workout-session() çağrısı:
     *    Değiştirilen session'ın bilgilerine göre bir sonraki günün session'ını oluşturur.
     *
     * 3. Son 3 session kontrolü:
     *    Kullanıcının aktif periyodundaki son 3 WORKOUT/OFF session'ının hepsi MISSED ise
     *    kullanıcı programı terk etmiş sayılır → end-old-period() çağrılır.
     */
    @Transactional
    public void processExpiredSession(UUID sessionId, UUID userId, UUID periodId, SessionType type, String timezone) {
        // 1. Durumu güncelle: OFF ise FINISHED, WORKOUT ise MISSED
        WorkoutStatus updatedStatus = (type == SessionType.OFF) ? WorkoutStatus.FINISHED : WorkoutStatus.MISSED;

        dsl.update(WORKOUT_SESSION)
                .set(WORKOUT_SESSION.STATUS, updatedStatus)
                .where(WORKOUT_SESSION.ID.eq(sessionId))
                .execute();

        log.info("Session {} status updated to {} for user {}", sessionId, updatedStatus, userId);

        // 2. Bir sonraki session'ı oluştur (ID, DB default uuidv7() ile üretilir)
        workoutSessionService.setNextWorkoutSession(sessionId, userId, timezone);

        // 3. Kullanıcının aktif periyottaki son 3 WORKOUT/OFF session'ını kontrol et
        //    (PLANNED hariç — sadece sonuçlanmış olanlar)
        var recentStatuses = dsl.select(WORKOUT_SESSION.STATUS)
                .from(WORKOUT_SESSION)
                .where(WORKOUT_SESSION.USER_PROFILE_ID.eq(userId))
                .and(WORKOUT_SESSION.WORKOUT_PERIOD_ID.eq(periodId))
                .and(WORKOUT_SESSION.TYPE.in(SessionType.WORKOUT, SessionType.OFF))
                .and(WORKOUT_SESSION.STATUS.notEqual(WorkoutStatus.PLANNED))
                .orderBy(WORKOUT_SESSION.START_DATE.desc(), WORKOUT_SESSION.CREATED_AT.desc())
                .limit(3)
                .fetch();

        if (recentStatuses.size() == 3) {
            boolean allMissed = recentStatuses.stream()
                    .allMatch(r -> r.get(WORKOUT_SESSION.STATUS) == WorkoutStatus.MISSED);

            if (allMissed) {
                log.warn("User {} has 3 consecutive MISSED sessions in period {}. Ending period.", userId, periodId);
                workoutPeriodService.endOldPeriod(periodId, userId);
            }
        }
    }
}
