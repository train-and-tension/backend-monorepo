package com.traintension.identity.repositories;

import com.traintension.identity.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends MongoRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByGoogleId(String googleId);

    Optional<User> findByAppleId(String appleId);

    Boolean existsByEmail(String email);

    Boolean existsByGoogleId(String googleId);

    Boolean existsByAppleId(String appleId);

    Slice<User> findAllByIsDeleteFalse(Pageable pageable);

    Slice<User> findAllByIsDeleteTrue(Pageable pageable);

    Slice<User> findByEmailRegex(String email, Pageable pageable);
}
