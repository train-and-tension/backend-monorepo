package com.traintension.core.common.config;

public final class ApiConstants {


    private ApiConstants() {
    }

    // Temel Yollar (Base Paths)
    private static final String CORE = "/api/core";
    private static final String ADMIN_CORE = "/api/core/admin";
    private static final String LINK = "/api/core/link";
    private static final String ADMIN_LINK = "/api/core/admin/link";

    //SYNC
    public static final String SYNC = CORE + "/sync";

    // Model Yolları
    public static final String EXERCISE = CORE + "/exercises";
    public static final String EXERCISE_MUSCLE = CORE + "/exercise-muscle";
    public static final String EQUIPMENT = CORE + "/equipments";
    public static final String MUSCLE = CORE + "/muscles";
    public static final String WORKOUT_PROGRAM = CORE + "/workout-programs";
    public static final String WORKOUT_DAY = CORE + "/workout-days";
    public static final String WORKOUT_SESSION = CORE + "/workout-sessions";
    public static final String TARGET_SET = CORE + "/target-sets";
    public static final String SET_RESULT = CORE + "/set-results";
    public static final String BODY_INFORMATION = CORE + "/body-information";
    public static final String USER_PROFILE = CORE + "/user-profiles";
    public static final String SAVED_EXERCISE = CORE + "/saved-exercise";

    // Admin Model Yolları
    public static final String ADMIN_EXERCISE = ADMIN_CORE + "/exercises";
    public static final String ADMIN_EQUIPMENT = ADMIN_CORE + "/equipments";
    public static final String ADMIN_EXERCISE_MUSCLE = ADMIN_CORE + "/exercise-muscle";
    public static final String ADMIN_MUSCLE = ADMIN_CORE + "/muscles";
    public static final String ADMIN_WORKOUT_PROGRAM = ADMIN_CORE + "/workout-programs";
    public static final String ADMIN_WORKOUT_DAY = ADMIN_CORE + "/workout-days";
    public static final String ADMIN_TARGET_SET = ADMIN_CORE + "/target-sets";
    public static final String ADMIN_USER_PROFILE = ADMIN_CORE + "/user-profiles";
}
