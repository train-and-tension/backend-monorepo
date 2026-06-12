package com.traintension.core.model.userProfile;

import com.traintension.common.dto.CreateUserProfileRequest;
import com.traintension.core.generated.tables.records.UserProfileRecord;
import com.traintension.core.model.userProfile.UserProfileDTO.*;

public class UserProfileMapper {

    public static Response fromRecord(UserProfileRecord record) {
        return UserProfileDTO.Response.builder()
                .id(record.getId())
                .firstName(record.getFirstName())
                .lastName(record.getLastName())
                .username(record.getUsername())
                .profilePicURL(record.getProfilePicUrl())
                .timezone(record.getTimezone())
                .createdAt(record.getCreatedAt())
                .version(record.getVersion())
                .build();
    }

    public static UserProfileRecord fromDTO(UpdateProfileRequest request) {
        UserProfileRecord record = new UserProfileRecord();
        if (request.firstName() != null) record.setFirstName(request.firstName());
        if (request.lastName() != null) record.setLastName(request.lastName());
        if (request.profilePicUrl() != null) record.setProfilePicUrl(request.profilePicUrl());
        return record;
    }

    public static UserProfileRecord fromDTO(CreateUserProfileRequest request, String username) {
        UserProfileRecord record = new UserProfileRecord();
        record.setId(request.userId());
        record.setUsername(username);
        if (request.firstName() != null) record.setFirstName(request.firstName());
        if (request.lastName() != null) record.setLastName(request.lastName());
        if (request.profilePicUrl() != null) record.setProfilePicUrl(request.profilePicUrl());
        return record;
    }
}
