package com.traintension.core.model.workoutSession;

import com.traintension.common.exception.custom.NotFoundException;
import com.traintension.core.generated.enums.SessionType;
import com.traintension.core.generated.enums.WorkoutStatus;
import com.traintension.core.generated.tables.records.SetResultRecord;
import com.traintension.core.model.setResult.SetResultDTO;
import com.traintension.core.model.setResult.SetResultService;
import com.traintension.core.model.workoutSession.WorkoutSessionDTO.*;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

import static com.traintension.core.generated.Tables.*;

@Service
@RequiredArgsConstructor
public class WorkoutSessionService {
    private final DSLContext dsl;
    private final SetResultService setResultService;
    @Transactional
    public SaveQuickExerciseResponse saveQuickExercise(SaveQuickExerciseRequest request, UUID userId) {
        UUID activePeriodId = dsl.select(WORKOUT_PERIOD.ID)
                .from(WORKOUT_PERIOD)
                .where(WORKOUT_PERIOD.USER_PROFILE_ID.eq(userId))
                .and(WORKOUT_PERIOD.IS_ACTIVE.isTrue())
                .fetchOptional(WORKOUT_PERIOD.ID)
                .orElseThrow(NotFoundException::new);

        String timezone = dsl.select(USER_PROFILE.TIMEZONE)
                .from(USER_PROFILE)
                .where(USER_PROFILE.ID.eq(userId))
                .fetchOptional(USER_PROFILE.TIMEZONE)
                .orElse("UTC");

        OffsetDateTime now = OffsetDateTime.now(ZoneId.of(timezone));
        String name = request.name() == null || request.name().isBlank()
                ? "Quick Workout"
                : request.name().trim();
        int exerciseCount = (int) request.setResults().stream()
                .map(SetResultDTO.CreateRequest::exerciseId)
                .distinct()
                .count();

        UUID workoutSessionId = dsl.insertInto(WORKOUT_SESSION)
                .set(WORKOUT_SESSION.USER_PROFILE_ID, userId)
                .set(WORKOUT_SESSION.WORKOUT_PERIOD_ID, activePeriodId)
                .set(WORKOUT_SESSION.START_DATE, now.toLocalDate())
                .set(WORKOUT_SESSION.START_TIME, now)
                .set(WORKOUT_SESSION.END_TIME, now)
                .set(WORKOUT_SESSION.STATUS, WorkoutStatus.FINISHED)
                .set(WORKOUT_SESSION.TYPE, SessionType.QUICK_WORKOUT)
                .set(WORKOUT_SESSION.WORKOUT_DAY_NAME_SNAPSHOT, name)
                .set(WORKOUT_SESSION.EXERCISE_COUNT_SNAPSHOT, exerciseCount)
                .returning(WORKOUT_SESSION.ID)
                .fetchOne(WORKOUT_SESSION.ID);

        List<SetResultRecord> setResults = request.setResults().stream()
                .map(item -> setResultService.fromCreateRequest(item, workoutSessionId, userId))
                .toList();

        dsl.insertInto(SET_RESULT)
                .set(setResults)
                .execute();

        return new SaveQuickExerciseResponse(workoutSessionId);
    }

    //---------------------------------------------------------------------------------------------------//
    //                                         🧑️USER METHODS🧑️
    //---------------------------------------------------------------------------------------------------//


    //---------------------------------------------------------------------------------------------------//
    //                                         💽BACKGROUND METHODS️💽
    //---------------------------------------------------------------------------------------------------//

    /**
     * Verilen (tamamlanmış/kaçırılmış) session'ın workout day sırasına göre
     * bir sonraki günün WorkoutSession'ını PLANNED olarak oluşturur.
     *
     * @param previousSessionId Durumu yeni değiştirilmiş olan önceki session'ın ID'si
     * @param userId            Kullanıcı ID'si
     * @param userTimezone      Kullanıcının timezone string'i (örn: "Europe/Istanbul")
     */
    @Transactional
    public void setNextWorkoutSession(UUID previousSessionId, UUID userId, String userTimezone) {
        var row = dsl.select(
                        WORKOUT_SESSION.WORKOUT_PERIOD_ID,
                        WORKOUT_DAY.ORDER_NUMBER,
                        WORKOUT_PERIOD.WORKOUT_PROGRAM_ID
                )
                .from(WORKOUT_SESSION)
                .join(WORKOUT_DAY).on(WORKOUT_DAY.ID.eq(WORKOUT_SESSION.WORKOUT_DAY_ID))
                .join(WORKOUT_PERIOD).on(WORKOUT_PERIOD.ID.eq(WORKOUT_SESSION.WORKOUT_PERIOD_ID))
                .where(WORKOUT_SESSION.ID.eq(previousSessionId))
                .and(WORKOUT_SESSION.USER_PROFILE_ID.eq(userId))
                .fetchOne();

        if (row == null) throw new NotFoundException();

        UUID periodId = row.value1();
        int currentOrder = row.value2();
        UUID workoutProgramId = row.value3();
        Integer maxOrder = dsl.select(DSL.max(WORKOUT_DAY.ORDER_NUMBER))
                .from(WORKOUT_DAY)
                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                .and(WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                .fetchOne(0, Integer.class);
        if (maxOrder == null) throw new NotFoundException();

        int nextOrder = currentOrder < maxOrder ? currentOrder + 1 : 1;

        var nextDay = dsl.select(
                        WORKOUT_DAY.ID,
                        WORKOUT_DAY.IS_OFF,
                        WORKOUT_DAY.NAME
                )
                .from(WORKOUT_DAY)
                .where(WORKOUT_DAY.WORKOUT_PROGRAM_ID.eq(workoutProgramId))
                .and(WORKOUT_DAY.USER_PROFILE_ID.eq(userId))
                .and(WORKOUT_DAY.ORDER_NUMBER.eq(nextOrder))
                .fetchOne();

        if (nextDay == null) throw new NotFoundException();

        int exerciseCount = dsl.fetchCount(
                dsl.selectFrom(TARGET_SET)
                        .where(TARGET_SET.WORKOUT_DAY_ID.eq(nextDay.value1()))
                        .and(TARGET_SET.USER_PROFILE_ID.eq(userId))
        );

        dsl.insertInto(WORKOUT_SESSION)
                .set(WORKOUT_SESSION.USER_PROFILE_ID, userId)
                .set(WORKOUT_SESSION.WORKOUT_PERIOD_ID, periodId)
                .set(WORKOUT_SESSION.WORKOUT_DAY_ID, nextDay.value1())
                .set(WORKOUT_SESSION.STATUS, WorkoutStatus.PLANNED)
                .set(WORKOUT_SESSION.START_DATE, LocalDate.now(ZoneId.of(userTimezone)))
                .set(WORKOUT_SESSION.TYPE, Boolean.TRUE.equals(nextDay.value2()) ? SessionType.OFF : SessionType.WORKOUT)
                .set(WORKOUT_SESSION.WORKOUT_DAY_NAME_SNAPSHOT, nextDay.value3())
                .set(WORKOUT_SESSION.EXERCISE_COUNT_SNAPSHOT, exerciseCount)
                .execute();
    }

}
