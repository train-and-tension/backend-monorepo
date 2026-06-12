UPDATE workout_program
SET is_active = FALSE
WHERE user_profile_id IS NULL
  AND is_active = TRUE;

ALTER TABLE workout_program
    ADD CONSTRAINT chk_workout_program_system_not_active
        CHECK (user_profile_id IS NOT NULL OR is_active = FALSE);
