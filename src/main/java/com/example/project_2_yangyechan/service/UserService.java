package com.example.project_2_yangyechan.service;

import com.example.project_2_yangyechan.entity.UserEntity;
import com.example.project_2_yangyechan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final JpaUserDetailsManager manager;
    private final UserRepository userRepository;

    // UPDATE Image
    public void updateUserAvatar(MultipartFile avatarImage, Authentication authentication) {
        // 사용자가 프로필 이미지를 업로드 한다.

        // 1. 유저 존재 확인
        String username = authentication.getName();
        Optional<UserEntity> optionalUser = userRepository.findByUsername(username);

        if (manager.userExists(username)) {

            // media/filename.png
            // media/<업로드 시각>.png
            // 2. 파일을 어디에 업로드 할건지
            // media/{userId}/profile.{파일 확장자}

            // 2-1. 폴더만 만드는 과정
            String profileDir = String.format("media/%s/", username);
            try {
                Files.createDirectories(Path.of(profileDir));
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 2-2. 확장자를 포함한 이미지 이름 만들기 (profile.{확장자})
            String originalFilename = avatarImage.getOriginalFilename();
            // queue.png => fileNameSplit = {"queue", "png"}
            String[] fileNameSplit = originalFilename.split("\\.");
            String extension = fileNameSplit[fileNameSplit.length - 1];
            String profileFilename = "profile." + extension;

            // 2-3. 폴더와 파일 경로를 포함한 이름 만들기
            String profilePath = profileDir + profileFilename;


            // 3. MultipartFile 을 저장하기
            try {
                avatarImage.transferTo(Path.of(profilePath));
            } catch (IOException e) {

                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
            }

            // 4. UserEntity 업데이트 (정적 프로필 이미지를 회수할 수 있는 URL)
            // http://localhost:8080/static/username/profile.png

            UserEntity userEntity = optionalUser.get();
            userEntity.setProfile_img(String.format("/static/%s/%s", username, profileFilename));
            userRepository.save(userEntity);

        }else {
            throw new UsernameNotFoundException(username);
        }
    }
}

