package com.example.project_2_yangyechan.repository;

import com.example.project_2_yangyechan.entity.UserEntity;
import com.example.project_2_yangyechan.entity.User_FollowsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface Users_FollowsRepository extends JpaRepository<User_FollowsEntity, Long> {
    List<User_FollowsEntity> findAllByFollowing(UserEntity following);
    Optional<User_FollowsEntity> findByFollowerAndFollowing(UserEntity follower, UserEntity following);
}
