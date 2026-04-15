package com.smstar.user;

import com.smstar.storage.OracleStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final OracleStorageService storageService;

    public UserDto getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    public UserDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return toDto(user);
    }

    @Transactional
    public UserDto updateProfile(UpdateProfileRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (request.getDisplayName() != null) user.setDisplayName(request.getDisplayName());
        if (request.getBio() != null) user.setBio(request.getBio());

        return toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto uploadAvatar(MultipartFile file) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String objectName = "avatars/" + user.getId() + "/" + file.getOriginalFilename();
        String url = storageService.uploadFile(objectName, file.getInputStream(), file.getContentType());
        user.setProfilePicUrl(url);

        return toDto(userRepository.save(user));
    }

    private UserDto toDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .profilePicUrl(user.getProfilePicUrl())
                .bio(user.getBio())
                .role(user.getRole().name())
                .coinBalance(user.getCoinBalance())
                .diamondBalance(user.getDiamondBalance())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
