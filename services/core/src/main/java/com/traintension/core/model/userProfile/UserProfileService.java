package com.traintension.core.model.userProfile;

import com.traintension.common.dto.CreateUserProfileRequest;
import com.traintension.common.exception.custom.ConflictException;
import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.jooq.exception.DataAccessException;
import org.postgresql.util.PSQLException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.traintension.core.model.userProfile.UserProfileDTO.*;

import java.util.UUID;

import static com.traintension.core.generated.Tables.USER_PROFILE;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {
    private final DSLContext dsl;

    @Transactional(readOnly = true)
    public Response getById(UUID userId) {
        return dsl.selectFrom(USER_PROFILE)
                .where(USER_PROFILE.ID.eq(userId))
                .fetchOne(UserProfileMapper::fromRecord);
    }

    @Transactional
    public Response updateProfile(UpdateProfileRequest request, UUID userId) {
        return dsl.update(USER_PROFILE)
                .set(UserProfileMapper.fromDTO(request))
                .where(USER_PROFILE.ID.eq(userId))
                .returning()
                .fetchOne(UserProfileMapper::fromRecord);
    }

    @Transactional
    public Response createUserProfile(CreateUserProfileRequest request) {
        for (int attempt = 0; attempt < 6; attempt++) {
            try {
                String generatedUsername = UsernameGenerator.generate(request.firstName(), request.lastName(), attempt);
                return dsl.insertInto(USER_PROFILE)
                        .set(UserProfileMapper.fromDTO(request, generatedUsername))
                        .returning()
                        .fetchOne(UserProfileMapper::fromRecord);
            } catch (DataAccessException e) {
                if (e.getCause() instanceof PSQLException psql && psql.getSQLState().equals("23505")) {
                    if (attempt == 5) throw new ConflictException();
                } else {
                    throw e;
                }
            }
        }

        throw new ConflictException();
    }

    @Transactional(readOnly = true)
    public Boolean existsById(UUID userId) {
        return dsl.fetchExists(USER_PROFILE, USER_PROFILE.ID.eq(userId));
    }

    @Transactional
    public TimezoneResponse updateTimezone(ChangeTimezoneRequest request, UUID userId) {
        String updatedTimezone = dsl.update(USER_PROFILE)
                .set(USER_PROFILE.TIMEZONE, request.timezone())
                .where(USER_PROFILE.ID.eq(userId))
                .returning(USER_PROFILE.TIMEZONE)
                .fetchOne(USER_PROFILE.TIMEZONE);

        return new TimezoneResponse(updatedTimezone);
    }

    @Transactional
    public UsernameResponse updateUsername(ChangeUsernameRequest request, UUID userId) {
        String updatedUsername = dsl.update(USER_PROFILE)
                .set(USER_PROFILE.USERNAME, request.username())
                .where(USER_PROFILE.ID.eq(userId))
                .returning(USER_PROFILE.USERNAME)
                .fetchOne(USER_PROFILE.USERNAME);

        return new UsernameResponse(updatedUsername);
    }

    @Transactional
    public void deleteById(UUID id) {
        dsl.deleteFrom(USER_PROFILE)
                .where(USER_PROFILE.ID.eq(id))
                .execute();
    }

    @Transactional(readOnly = true)
    public Boolean isUsernameAvailable(String username) {
        return !dsl.fetchExists(USER_PROFILE, USER_PROFILE.USERNAME.eq(username));
    }
}
