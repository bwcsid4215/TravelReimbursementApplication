package com.rentkaro.userservice.repository;

import com.rentkaro.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.userName = :usernameOrEmail OR u.email = :usernameOrEmail")
    Optional<User> findByUserNameOrEmail(@Param("usernameOrEmail") String usernameOrEmail);

    Boolean existsByUserName(String userName);
    Boolean existsByEmail(String email);

}