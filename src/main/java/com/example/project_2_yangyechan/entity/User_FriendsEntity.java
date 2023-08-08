package com.example.project_2_yangyechan.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "user_friends")
public class User_FriendsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_user")
    private UserEntity fromUser;

    @ManyToOne
    @JoinColumn(name = "to_user")
    private UserEntity toUser;
}
