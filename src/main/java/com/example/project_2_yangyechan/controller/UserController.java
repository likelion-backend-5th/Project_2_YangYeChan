package com.example.project_2_yangyechan.controller;

import com.example.project_2_yangyechan.dto.ResponseDto;
import com.example.project_2_yangyechan.entity.CustomUserDetails;
import com.example.project_2_yangyechan.jwt.JwtRequestDto;
import com.example.project_2_yangyechan.jwt.JwtTokenDto;
import com.example.project_2_yangyechan.jwt.JwtTokenUtils;
import com.example.project_2_yangyechan.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController  // 로그인 페이지를 보여줄려고
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserDetailsManager manager;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtils jwtTokenUtils;
    private final UserService userService;

    // 1. login 페이지로 온다.
    // 2. login 페이지에 아이디 비밀번호를 입력한다.
    // 3. 성공하면 my-profile 로 이동한다.

    // 로그인 페이지를 위한 GetMapping
    @GetMapping("/login")
    public String loginForm() {
        return "login-form";
    }

    @PostMapping("/login")
    public JwtTokenDto loginForm(@RequestBody JwtRequestDto dto) {
        // 사용자 정보 회수
        UserDetails userDetails
                = manager.loadUserByUsername(dto.getUsername());
        // 기록된 비밀번호와 실제 비밀번호가 다를때
        // passwordEncoder.matches(rawPassword, encodedPassword)
        // 평문 비밀번호와 암호화 비밀번호를 비교할 수 있다.
        if (!passwordEncoder.matches(dto.getPassword(), userDetails.getPassword()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);

        JwtTokenDto response = new JwtTokenDto();
        response.setToken(jwtTokenUtils.generateToken(userDetails));
        return response;
    }

    // 로그인 성공 후 로그인 여부를 판단하기 위한 GetMapping
    @GetMapping("/my-profile")
    public String myProfile(
            Authentication authentication
    ) {
//        log.info(authentication.getName());
//        log.info(((User) authentication.getPrincipal()).getUsername());
        CustomUserDetails userDetails
                = (CustomUserDetails) authentication.getPrincipal();
        log.info(userDetails.getUsername());
        log.info(userDetails.getEmail());
//        log.info(SecurityContextHolder.getContext().getAuthentication().getName());
        return "my-profile";
    }

    // 1. 사용자가 register 페이지로 온다.
    // 2. 사용자가 register 페이지에 ID, 비밀번호, 비밀번호 확인을 입력
    // 3. register 페이지에서 /users/register 로 POST 요청
    // 4. UserDetailsManager 에 새로운 사용자 정보 추가

    @GetMapping("/register")
    public String registerForm() {
        return "register-form";
    }

    // 어떻게 사용자를 관리하는지는
    // interface 기반으로 의존성 주입


        @PostMapping("/register")
    public JwtTokenDto registerPost(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("password-check") String passwordCheck,
            @RequestParam("profile_img") String profile_img,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone
    ) {
        JwtTokenDto response = new JwtTokenDto();
        if (password.equals(passwordCheck)) {
            UserDetails details = CustomUserDetails
                    .builder()
                    .username(username)
                    .password(passwordEncoder.encode(password))
                    .profile_img(profile_img)
                    .email(email)
                    .phone(phone)
                    .build();
            log.info("password match!");
            manager.createUser(details);
            response.setToken("login");
            return response;
        }
        log.warn("password does not match...");
        response.setToken("fail");
        return response;
    }

    // PUT /users/{id}/avatar
    // 사용자 프로필 이미지 설정
    @PutMapping(
            value = "/profile/image",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseDto avatar(
            @RequestParam("image") MultipartFile avatarImage,
            Authentication authentication
    ) {
        userService.updateUserAvatar(avatarImage, authentication);
        ResponseDto response = new ResponseDto();
        response.setMessage("이미지가 등록되었습니다.");
        return response;
    }


}
