package com.atquil.seatsagaplatform.configure;

import com.atquil.seatsagaplatform.entity.AppUser;
import com.atquil.seatsagaplatform.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * @author atquil
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Load the user from Google
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 2. Extract details
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String googleSub = oAuth2User.getAttribute("sub"); // Unique ID from Google

        log.info("Google User Login: {}", email);

        // 3. Sync with our DB
        processUserLogin(email, name, googleSub);

        return oAuth2User;
    }

    private void processUserLogin(String email, String name, String googleSub) {
        AppUser user = userRepository.findByEmail(email)
                .orElseGet(() -> AppUser.builder().email(email).build());

        user.setName(name);
        user.setGoogleSub(googleSub);

        // Manual Fallback: If Auditing fails/is off, ensure we don't crash the DB
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }

        // Use saveAndFlush to force the DB write IMMEDIATELY so we catch errors here
        userRepository.saveAndFlush(user);

        log.info("User successfully synced to database: {}", email);
    }
}
