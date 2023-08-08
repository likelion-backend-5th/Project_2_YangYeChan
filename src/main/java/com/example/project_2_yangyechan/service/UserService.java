package com.example.project_2_yangyechan.service;

import com.example.project_2_yangyechan.dto.RequestDto;
import com.example.project_2_yangyechan.dto.ResponseDto;
import com.example.project_2_yangyechan.dto.ResponseUserDto;
import com.example.project_2_yangyechan.entity.UserEntity;
import com.example.project_2_yangyechan.entity.User_FollowsEntity;
import com.example.project_2_yangyechan.entity.User_FriendsEntity;
import com.example.project_2_yangyechan.repository.Users_FriendsRepository;
import com.example.project_2_yangyechan.repository.UserRepository;
import com.example.project_2_yangyechan.repository.Users_FollowsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
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
    private final Users_FollowsRepository usersFollowsRepository;
    private final Users_FriendsRepository userFriendsRepository;

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

    // 유저 정보 조회
    public ResponseUserDto userInformView (Long user_id) {
        // 유저 존재 확인
        Optional<UserEntity> optionalUser = userRepository.findById(user_id);

        if (optionalUser.isPresent()) {
            UserEntity entity = optionalUser.get();
            return ResponseUserDto.fromEntity(entity);
        }else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // 유저 팔로우, 언팔로우
    public ResponseDto followOrUnfollow(Long user_id, Authentication authentication) {
        String username = authentication.getName();
        Optional<UserEntity> user = userRepository.findByUsername(username);

        ResponseDto response = new ResponseDto();

        if (user.isPresent()) {
            UserEntity follower = user.get();
            Optional<UserEntity> optionalFollowing = userRepository.findById(user_id);
            UserEntity following = optionalFollowing.get();

            Optional<User_FollowsEntity> follow = usersFollowsRepository.findByFollowerAndFollowing(follower, following);

            if (follow.isPresent()) {
                usersFollowsRepository.delete(follow.get());
                response.setMessage("unfollow");
                return response;
            }else {
                User_FollowsEntity newFollow = new User_FollowsEntity();
                newFollow.setFollower(follower);
                newFollow.setFollowing(following);
                usersFollowsRepository.save(newFollow);
                response.setMessage("follow");
                return response;
            }
        }else {
            throw new UsernameNotFoundException(username);
        }
    }

    // 친구 신청
    public ResponseDto friendRequest(Long user_id, Authentication authentication) {
        String username = authentication.getName();
        Optional<UserEntity> user = userRepository.findByUsername(username);

        ResponseDto response = new ResponseDto();

        if (user.isPresent()) {
            UserEntity requestUser = user.get();
            Optional<UserEntity> optionalRequest = userRepository.findById(user_id);
            UserEntity targetUser = optionalRequest.get();

            Optional<User_FriendsEntity> request = userFriendsRepository.findByToUserAndFromUser(targetUser, requestUser);

            // 이미 친구신청을 보낸 경우
            if (request.isPresent()) {
                userFriendsRepository.delete(request.get());
                response.setMessage("친구 신청을 취소합니다.");
            }else if (targetUser.getId().equals(requestUser.getId())) {
                // 자신에게 친구신청을 보낸 경우
                response.setMessage("자신에게 친구신청을 보낼 수 없습니다.");
            }else {
                // 새로운 친구신청을 보내는 경우
                User_FriendsEntity newRequest = new User_FriendsEntity();
                newRequest.setFromUser(requestUser);
                newRequest.setToUser(targetUser);
                userFriendsRepository.save(newRequest);
                response.setMessage("친구신청을 보냈습니다.");
            }
        }else {
            throw new UsernameNotFoundException(username);
        }
        return response;
    }

    // 친구 수락 or 거절
//    public ResponseDto friendRespond(Long user_id, RequestDto dto, Authentication authentication) {
//        String username = authentication.getName();
//        Optional<UserEntity> user = userRepository.findByUsername(username);
//
//        ResponseDto response = new ResponseDto();
//
//        if (user.isPresent()) {
//
//        }else {
//            throw new UsernameNotFoundException(username);
//        }
//    }
}

