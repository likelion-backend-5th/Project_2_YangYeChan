package com.example.project_2_yangyechan.repository;

import com.example.project_2_yangyechan.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
}
