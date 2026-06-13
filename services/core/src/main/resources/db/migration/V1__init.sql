-- ==========================================
-- 1. ENUM TİPLERİ
-- ==========================================
CREATE TYPE unit_system AS ENUM (
    'METRIC',
    'IMPERIAL'
    );

CREATE TYPE training_goal AS ENUM (
    'HYPERTROPHY',
    'STRENGTH',
    'ENDURANCE',
    'GENERAL_FITNESS'
    );

CREATE TYPE weight_goal AS ENUM (
    'LOSE_WEIGHT',
    'MAINTAIN_WEIGHT',
    'GAIN_WEIGHT'
    );

CREATE TYPE gender AS ENUM (
    'MALE',
    'FEMALE'
    );

CREATE TYPE activity_level AS ENUM (
    'SEDENTARY',
    'LIGHT',
    'MODERATE',
    'ACTIVE',
    'VERY_ACTIVE',
    'EXTRA_ACTIVE'
    );

CREATE TYPE workout_status AS ENUM (
    'PLANNED',
    'FINISHED',
    'MISSED'
    );

CREATE TYPE session_type AS ENUM (
    'WORKOUT',
    'OFF',
    'QUICK_WORKOUT'
    );

CREATE TYPE activation_level AS ENUM (
    'PRIMARY',
    'SECONDARY'
    );

-- ==========================================
-- 2. MERKEZİ SAYAÇ
-- ==========================================
CREATE SEQUENCE global_version_seq START 1 CACHE 100;

-- ==========================================
-- 3. TABLOLAR VE SYNC İNDEKSLERİ
-- ==========================================
CREATE TABLE user_profile
(
    id              UUID PRIMARY KEY NOT NULL UNIQUE,
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    username        VARCHAR(50)      NOT NULL UNIQUE,
    profile_pic_url VARCHAR(2048),
    timezone        VARCHAR(50)      NOT NULL DEFAULT 'UTC',
    version         BIGINT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ      NOT NULL DEFAULT now()
);

CREATE TABLE body_information
(
    id              UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id UUID             NOT NULL REFERENCES user_profile (id) ON DELETE CASCADE,
    training_goal   training_goal    NOT NULL,
    weight_goal     weight_goal      NOT NULL,
    weight_kg       DECIMAL(5, 2)    NOT NULL,
    height_cm       DECIMAL(5, 2)    NOT NULL,
    birth_date      DATE             NOT NULL,
    gender          gender           NOT NULL,
    activity_level  activity_level   NOT NULL,
    unit            unit_system      NOT NULL,
    version         BIGINT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ      NOT NULL DEFAULT now()
);
CREATE INDEX idx_body_information_sync ON body_information (user_profile_id, version ASC);

CREATE TABLE measurement_history
(
    id              UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id UUID             NOT NULL REFERENCES user_profile (id) ON DELETE CASCADE,
    weight_kg       DECIMAL(5, 2)    NOT NULL,
    height_cm       DECIMAL(5, 2)    NOT NULL,
    unit            unit_system      NOT NULL,
    version         BIGINT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ      NOT NULL DEFAULT now()
);
CREATE INDEX idx_measurement_history_sync ON measurement_history (user_profile_id, version ASC);

CREATE TABLE muscle
(
    id          UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    name        VARCHAR(100)     NOT NULL UNIQUE,
    description VARCHAR(1000),
    media_url   VARCHAR(2048),
    version     BIGINT           NOT NULL DEFAULT 0,
    created_at  TIMESTAMPTZ      NOT NULL DEFAULT now()
);
CREATE INDEX idx_muscle_sync ON muscle (version ASC);

CREATE TABLE equipment
(
    id              UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id UUID REFERENCES user_profile (id) ON DELETE CASCADE,
    name            VARCHAR(100)     NOT NULL,
    description     VARCHAR(1000),
    media_url       VARCHAR(2048),
    version         BIGINT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ      NOT NULL DEFAULT now()
);
-- Sistem ekipmanları kendi aralarında, kullanıcı ekipmanları kendi aralarında unique
CREATE UNIQUE INDEX uq_equipment_name_system ON equipment (name) WHERE user_profile_id IS NULL;
CREATE UNIQUE INDEX uq_equipment_name_user ON equipment (name, user_profile_id) WHERE user_profile_id IS NOT NULL;
CREATE INDEX idx_equipment_sync ON equipment (user_profile_id, version ASC);

CREATE TABLE exercise
(
    id              UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id UUID REFERENCES user_profile (id) ON DELETE CASCADE,
    equipment_id    UUID             REFERENCES equipment (id) ON DELETE SET NULL,
    name            VARCHAR(150)     NOT NULL,
    description     VARCHAR(1000),
    media_url       VARCHAR(2048),
    version         BIGINT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ      NOT NULL DEFAULT now()
);
CREATE INDEX idx_exercise_sync ON exercise (user_profile_id, version ASC);
CREATE INDEX idx_exercise_equipment_id ON exercise (equipment_id);

-- exercise_muscle'ın kendi id'si var; (exercise_id, muscle_id) unique constraint olarak kalıyor
CREATE TABLE exercise_muscle
(
    id               UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id  UUID REFERENCES user_profile (id) ON DELETE CASCADE,
    exercise_id      UUID             NOT NULL REFERENCES exercise (id) ON DELETE CASCADE,
    muscle_id        UUID             NOT NULL REFERENCES muscle (id) ON DELETE CASCADE,
    activation_level activation_level NOT NULL,
    version          BIGINT           NOT NULL DEFAULT 0,

    CONSTRAINT uq_exercise_muscle UNIQUE (exercise_id, muscle_id)
);
CREATE INDEX idx_exercise_muscle_sync ON exercise_muscle (user_profile_id, version ASC);
CREATE INDEX idx_exercise_muscle_exercise_id ON exercise_muscle (exercise_id);
CREATE INDEX idx_exercise_muscle_muscle_id ON exercise_muscle (muscle_id, activation_level);

-- saved_exercise'ın kendi id'si var; (user_profile_id, exercise_id) unique constraint olarak kalıyor
CREATE TABLE saved_exercise
(
    id              UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id UUID   NOT NULL REFERENCES user_profile (id) ON DELETE CASCADE,
    exercise_id     UUID   NOT NULL REFERENCES exercise (id) ON DELETE CASCADE,
    version         BIGINT NOT NULL DEFAULT 0,

    CONSTRAINT uq_saved_exercise UNIQUE (user_profile_id, exercise_id)
);
CREATE INDEX idx_saved_exercise_sync ON saved_exercise (user_profile_id, version ASC);
CREATE INDEX idx_saved_exercise_exercise_id ON saved_exercise (exercise_id);

CREATE TABLE workout_program
(
    id              UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id UUID REFERENCES user_profile (id) ON DELETE CASCADE,
    duplicated_id   UUID                      DEFAULT NULL,
    is_edited       BOOLEAN          NOT NULL DEFAULT FALSE,
    is_active       BOOLEAN          NOT NULL DEFAULT FALSE,
    name            VARCHAR(100)     NOT NULL,
    description     VARCHAR(1000),
    version         BIGINT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ      NOT NULL DEFAULT now()
);
CREATE UNIQUE INDEX uq_workout_program_active_user
    ON workout_program (user_profile_id)
    WHERE is_active = TRUE;
CREATE INDEX idx_workout_program_sync ON workout_program (user_profile_id, version ASC);

CREATE TABLE workout_day
(
    id                 UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id    UUID REFERENCES user_profile (id) ON DELETE CASCADE,
    workout_program_id UUID REFERENCES workout_program (id) ON DELETE CASCADE,
    is_off             BOOLEAN          NOT NULL DEFAULT FALSE,
    order_number       INTEGER          NOT NULL,
    name               VARCHAR(100)     NOT NULL,
    version            BIGINT           NOT NULL DEFAULT 0,
    created_at         TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ      NOT NULL DEFAULT now()
);
CREATE INDEX idx_workout_day_workout_program_id ON workout_day (workout_program_id);
CREATE INDEX idx_workout_day_sync ON workout_day (user_profile_id, version ASC);

CREATE TABLE target_set
(
    id              UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id UUID REFERENCES user_profile (id) ON DELETE CASCADE,
    exercise_id     UUID             NOT NULL REFERENCES exercise (id) ON DELETE RESTRICT,
    workout_day_id  UUID REFERENCES workout_day (id) ON DELETE CASCADE,
    weight_kg       DECIMAL(6, 2)    NOT NULL DEFAULT 0.00,
    duration        INTEGER,
    unit            unit_system      NOT NULL,
    rest_duration   INTEGER          NOT NULL DEFAULT 0,
    rep_count       INTEGER,
    order_number    INTEGER          NOT NULL,
    version         BIGINT           NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at      TIMESTAMPTZ      NOT NULL DEFAULT now(),
    CONSTRAINT chk_rep_or_duration CHECK (
        (rep_count IS NULL) != (duration IS NULL)
        )
);
CREATE INDEX idx_target_set_exercise_id ON target_set (exercise_id);
CREATE INDEX idx_target_set_workout_day_id ON target_set (workout_day_id);
CREATE INDEX idx_target_set_sync ON target_set (user_profile_id, version ASC);

CREATE TABLE workout_period
(
    id                            UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id               UUID             NOT NULL REFERENCES user_profile (id) ON DELETE CASCADE,
    workout_program_id            UUID REFERENCES workout_program (id),
    is_active                     BOOLEAN          NOT NULL DEFAULT TRUE,
    start_date                    TIMESTAMPTZ      NOT NULL,
    end_date                      TIMESTAMPTZ,
    workout_program_name_snapshot VARCHAR(100)     NOT NULL,
    version                       BIGINT           NOT NULL DEFAULT 0,
    created_at                    TIMESTAMPTZ      NOT NULL DEFAULT now()
);
-- Oluşturulduğu anda diğer aktifliği true olanlar false olacak.
CREATE INDEX idx_workout_period_workout_program_id ON workout_period (workout_program_id);
CREATE INDEX idx_workout_period_sync ON workout_period (user_profile_id, version ASC);

CREATE TABLE workout_session
(
    id                        UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id           UUID             NOT NULL REFERENCES user_profile (id) ON DELETE CASCADE,
    workout_period_id         UUID             NOT NULL REFERENCES workout_period (id) ON DELETE CASCADE,
    workout_day_id            UUID             REFERENCES workout_day (id) ON DELETE SET NULL, -- PLANNED SESSION ise sonraki workout_day order'ına göre atanır
    start_date                DATE             NOT NULL,
    start_time                TIMESTAMPTZ,
    end_time                  TIMESTAMPTZ,
    status                    workout_status   NOT NULL,
    type                      session_type     NOT NULL,
    workout_day_name_snapshot VARCHAR(100)     NOT NULL,
    exercise_count_snapshot   INTEGER          NOT NULL,
    version                   BIGINT           NOT NULL DEFAULT 0,
    created_at                TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at                TIMESTAMPTZ      NOT NULL DEFAULT now()
);
CREATE INDEX idx_workout_session_workout_period_id ON workout_session (workout_period_id);
CREATE INDEX idx_workout_session_workout_day_id ON workout_session (workout_day_id);
CREATE INDEX idx_workout_session_sync ON workout_session (user_profile_id, version ASC);

CREATE TABLE set_result
(
    id                     UUID PRIMARY KEY NOT NULL DEFAULT uuidv7(),
    user_profile_id        UUID             NOT NULL REFERENCES user_profile (id) ON DELETE CASCADE,
    workout_session_id     UUID REFERENCES workout_session (id) ON DELETE CASCADE,
    exercise_id            UUID             REFERENCES exercise (id) ON DELETE SET NULL,
    order_number           INTEGER          NOT NULL,
    duration               INTEGER,
    rest_duration          INTEGER          NOT NULL,
    rep_count              INTEGER,
    weight_kg              DECIMAL(6, 2)    NOT NULL DEFAULT 0.00,
    unit                   unit_system      NOT NULL,
    exercise_name_snapshot VARCHAR(100)     NOT NULL,
    targeted_rep_count     INTEGER          NOT NULL,
    targeted_weight        DECIMAL(6, 2)    NOT NULL DEFAULT 0.00,
    targeted_duration      INTEGER,
    version                BIGINT           NOT NULL DEFAULT 0,
    created_at             TIMESTAMPTZ      NOT NULL DEFAULT now(),
    updated_at             TIMESTAMPTZ      NOT NULL DEFAULT now(),

        CONSTRAINT chk_rep_or_duration CHECK (
            (rep_count IS NULL) != (duration IS NULL)
            )
);
CREATE INDEX idx_set_result_workout_session_id ON set_result (workout_session_id);
CREATE INDEX idx_set_result_exercise_id ON set_result (exercise_id);
CREATE INDEX idx_set_result_sync ON set_result (user_profile_id, version ASC);

-- ==========================================
-- DELETED HISTORY
-- ==========================================
CREATE TABLE deleted_history
(
    version         BIGINT PRIMARY KEY    DEFAULT nextval('global_version_seq'),
    user_profile_id UUID,
    record_id       UUID         NOT NULL,
    table_name      VARCHAR(100) NOT NULL,
    created_at      TIMESTAMPTZ  NOT NULL DEFAULT now()
);
CREATE INDEX idx_deleted_history_sync ON deleted_history (user_profile_id, version ASC);
CREATE INDEX idx_deleted_history_created_at ON deleted_history (created_at);


-- ==========================================
-- 4. FONKSİYONLAR VE TRIGGERLAR
-- ==========================================

-- A) Otomatik "updated_at" Güncelleme Fonksiyonu
CREATE OR REPLACE FUNCTION set_updated_at()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.updated_at = now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_updated_at_workout_program
    BEFORE UPDATE
    ON workout_program
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_updated_at_workout_day
    BEFORE UPDATE
    ON workout_day
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_updated_at_target_set
    BEFORE UPDATE
    ON target_set
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_updated_at_workout_session
    BEFORE UPDATE
    ON workout_session
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();

CREATE TRIGGER trg_updated_at_set_result
    BEFORE UPDATE
    ON set_result
    FOR EACH ROW
EXECUTE FUNCTION set_updated_at();


-- B) Insert/Update Anında Versiyon Atama
CREATE OR REPLACE FUNCTION set_global_version()
    RETURNS TRIGGER AS
$$
BEGIN
    NEW.version = nextval('global_version_seq');
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_version_user_profile
    BEFORE INSERT OR UPDATE
    ON user_profile
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_body_information
    BEFORE INSERT OR UPDATE
    ON body_information
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_measurement_history
    BEFORE INSERT OR UPDATE
    ON measurement_history
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_muscle
    BEFORE INSERT OR UPDATE
    ON muscle
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_equipment
    BEFORE INSERT OR UPDATE
    ON equipment
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_exercise
    BEFORE INSERT OR UPDATE
    ON exercise
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_exercise_muscle
    BEFORE INSERT OR UPDATE
    ON exercise_muscle
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_saved_exercise
    BEFORE INSERT OR UPDATE
    ON saved_exercise
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_workout_program
    BEFORE INSERT OR UPDATE
    ON workout_program
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_workout_day
    BEFORE INSERT OR UPDATE
    ON workout_day
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_target_set
    BEFORE INSERT OR UPDATE
    ON target_set
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_workout_period
    BEFORE INSERT OR UPDATE
    ON workout_period
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_workout_session
    BEFORE INSERT OR UPDATE
    ON workout_session
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();

CREATE TRIGGER trg_version_set_result
    BEFORE INSERT OR UPDATE
    ON set_result
    FOR EACH ROW
EXECUTE FUNCTION set_global_version();


-- C) Silme Anında Log Atama
-- Tüm tablolarda artık kendi id'si olduğu için OLD.id her tablo için geçerli.
CREATE OR REPLACE FUNCTION log_deleted_record()
    RETURNS TRIGGER AS
$$
DECLARE
    v_user_profile_id UUID  := NULL;
    v_old_data        JSONB := to_jsonb(OLD);
BEGIN
    IF v_old_data ? 'user_profile_id' THEN
        v_user_profile_id := (v_old_data ->> 'user_profile_id')::UUID;
    ELSIF TG_TABLE_NAME = 'user_profile' THEN
        v_user_profile_id := OLD.id;
    END IF;

    INSERT INTO deleted_history (table_name, record_id, user_profile_id)
    VALUES (TG_TABLE_NAME, OLD.id, v_user_profile_id);

    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION cleanup_deleted_history()
    RETURNS INTEGER AS
$$
DECLARE
    v_deleted_count INTEGER;
BEGIN
    DELETE FROM deleted_history
    WHERE created_at < now() - INTERVAL '60 days';

    GET DIAGNOSTICS v_deleted_count = ROW_COUNT;
    RETURN v_deleted_count;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION cleanup_deleted_history_after_insert()
    RETURNS TRIGGER AS
$$
BEGIN
    PERFORM cleanup_deleted_history();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_cleanup_deleted_history
    AFTER INSERT
    ON deleted_history
    FOR EACH STATEMENT
EXECUTE FUNCTION cleanup_deleted_history_after_insert();

CREATE TRIGGER trg_delete_user_profile
    BEFORE DELETE
    ON user_profile
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_body_information
    BEFORE DELETE
    ON body_information
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_measurement_history
    BEFORE DELETE
    ON measurement_history
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_muscle
    BEFORE DELETE
    ON muscle
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_equipment
    BEFORE DELETE
    ON equipment
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_exercise
    BEFORE DELETE
    ON exercise
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_exercise_muscle
    BEFORE DELETE
    ON exercise_muscle
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_saved_exercise
    BEFORE DELETE
    ON saved_exercise
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_workout_program
    BEFORE DELETE
    ON workout_program
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_workout_day
    BEFORE DELETE
    ON workout_day
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_target_set
    BEFORE DELETE
    ON target_set
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_workout_period
    BEFORE DELETE
    ON workout_period
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_workout_session
    BEFORE DELETE
    ON workout_session
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();

CREATE TRIGGER trg_delete_set_result
    BEFORE DELETE
    ON set_result
    FOR EACH ROW
EXECUTE FUNCTION log_deleted_record();
