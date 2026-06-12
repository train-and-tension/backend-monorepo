package com.traintension.identity.models;

import com.traintension.common.utils.user.UserRole;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document("users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndex(name = "isDelete_createdAt_idx", def = "{'isDelete': 1, 'createdAt': -1}")
public class User {
    @Id
    @Setter(AccessLevel.NONE)
    private UUID id;

    @Indexed(unique = true)
    private String email;

    private List<UserRole> roles;

    private Instant createdAt = Instant.now();

    private Boolean isDelete = false;

    @Indexed(unique = true, sparse = true)
    private String googleId;

    @Indexed(unique = true, sparse = true)
    private String appleId;

    private Instant deletedAt;
}
