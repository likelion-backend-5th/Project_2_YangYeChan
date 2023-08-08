package com.example.project_2_yangyechan.repository;

import com.example.project_2_yangyechan.entity.UserEntity;
import com.example.project_2_yangyechan.entity.User_FriendsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface Users_FriendsRepository extends JpaRepository<User_FriendsEntity, Long> {
    Optional<User_FriendsEntity> findByToUserAndFromUser(UserEntity to_user, UserEntity from_user);
}
