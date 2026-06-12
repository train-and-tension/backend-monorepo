package com.traintension.core.model.workoutPeriod;

import com.traintension.core.generated.enums.SessionType;
import com.traintension.core.generated.enums.WorkoutStatus;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static com.traintension.core.generated.Tables.WORKOUT_PERIOD;
import static com.traintension.core.generated.Tables.WORKOUT_SESSION;

@Service
@RequiredArgsConstructor
public class WorkoutPeriodService {
    private final DSLContext dsl;

    //---------------------------------------------------------------------------------------------------//
    //                                         🛠️ADMIN METHODS🛠️
    //---------------------------------------------------------------------------------------------------//

    //---------------------------------------------------------------------------------------------------//
    //                                         🧑️USER METHODS🧑️
    //---------------------------------------------------------------------------------------------------//

    @Transactional
    public void endOldPeriod(UUID workoutPeriodId, UUID userId) {
        if (workoutPeriodId == null) {
            return;
        }

        dsl.deleteFrom(WORKOUT_SESSION)
                .where(WORKOUT_SESSION.WORKOUT_PERIOD_ID.eq(workoutPeriodId))
                .and(WORKOUT_SESSION.USER_PROFILE_ID.eq(userId))
                .and(WORKOUT_SESSION.STATUS.eq(WorkoutStatus.PLANNED).and(WORKOUT_SESSION.TYPE.in(
                        SessionType.WORKOUT, SessionType.OFF, SessionType.QUICK_WORKOUT
                )))
                .execute();

        dsl.update(WORKOUT_PERIOD)
                .set(WORKOUT_PERIOD.IS_ACTIVE, Boolean.FALSE)
                .set(WORKOUT_PERIOD.END_DATE, OffsetDateTime.now())
                .where(WORKOUT_PERIOD.ID.eq(workoutPeriodId))
                .and(WORKOUT_PERIOD.USER_PROFILE_ID.eq(userId))
                .execute();
    }
}
