package com.example.project_2_yangyechan.dto;

import com.example.project_2_yangyechan.entity.UserEntity;
import lombok.Data;

@Data
public class ResponseUserDto {
    private String username;

    private String profile_img;

    public static ResponseUserDto fromEntity (UserEntity entity) {
        ResponseUserDto dto = new ResponseUserDto();
        dto.setUsername(entity.getUsername());
        dto.setProfile_img(entity.getProfile_img());
        return dto;
    }
}
