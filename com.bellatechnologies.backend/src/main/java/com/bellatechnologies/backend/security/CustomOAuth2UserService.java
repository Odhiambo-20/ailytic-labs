package com.bellatechnologies.backend.security;

import com.bellatechnologies.backend.model.User;
import com.bellatechnologies.backend.repository.UserRepository;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            log.error("Error processing OAuth2 user", ex);
            throw new OAuth2AuthenticationException(ex.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();

            // Update user if provider is different
            if (!user.getProvider().equals(registrationId)) {
                throw new OAuth2AuthenticationException(
                    "Looks like you're signed up with " + user.getProvider() +
                    " account. Please use your " + user.getProvider() + " account to login."
                );
            }

            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(userRequest, oAuth2UserInfo);
        }

        // FIXED: Pass OAuth2 attributes to CustomUserDetails
        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest userRequest, OAuth2UserInfo oAuth2UserInfo) {
        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .email(oAuth2UserInfo.getEmail())
                .username(oAuth2UserInfo.getEmail())
                .firstName(oAuth2UserInfo.getFirstName())
                .lastName(oAuth2UserInfo.getLastName())
                .provider(userRequest.getClientRegistration().getRegistrationId())
                .providerId(oAuth2UserInfo.getId())
                .profilePictureUrl(oAuth2UserInfo.getImageUrl())
                .roles(Arrays.asList("ROLE_USER"))
                .enabled(true)
                .emailVerified(true)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .lastLoginAt(Instant.now())
                .build();

        User savedUser = userRepository.save(user);
        log.info("New OAuth2 user registered: {} with provider: {}",
                 savedUser.getEmail(),
                 savedUser.getProvider());
        return savedUser;
    }

    private User updateExistingUser(User existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setFirstName(oAuth2UserInfo.getFirstName());
        existingUser.setLastName(oAuth2UserInfo.getLastName());
        existingUser.setProfilePictureUrl(oAuth2UserInfo.getImageUrl());
        existingUser.setUpdatedAt(Instant.now());
        existingUser.setLastLoginAt(Instant.now());

        User savedUser = userRepository.save(existingUser);
        log.info("OAuth2 user updated: {}", savedUser.getEmail());
        return savedUser;
    }
}
