package com.traintension.core.model.savedExercise;

import static com.traintension.core.generated.Tables.*;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import com.traintension.core.model.savedExercise.SavedExerciseDTO.*;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SavedExerciseService {
    private final DSLContext dsl;

    public List<Response> createAll(CreatePersonalsRequest request, UUID userId) {
        return dsl.insertInto(SAVED_EXERCISE)
                .set(SavedExerciseMapper.fromDTO(request, userId))
                .returning()
                .fetch(SavedExerciseMapper::fromRecord);
    }

    public List<UUID> deleteByIds(Set<UUID> exerciseIds, UUID userId) {
        if (exerciseIds == null || exerciseIds.isEmpty()) {
            return Collections.emptyList();
        }

        return dsl.deleteFrom(SAVED_EXERCISE)
                .where(SAVED_EXERCISE.EXERCISE_ID.in(exerciseIds))
                .and(SAVED_EXERCISE.USER_PROFILE_ID.eq(userId))
                .returning(SAVED_EXERCISE.EXERCISE_ID)
                .fetch(SAVED_EXERCISE.EXERCISE_ID);
    }
}
