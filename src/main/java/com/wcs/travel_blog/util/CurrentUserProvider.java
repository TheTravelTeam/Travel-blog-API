package com.wcs.travel_blog.util;

import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final UserRepository userRepository;

    public User requireCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new AccessDeniedException("Utilisateur non authentifié");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }

        if (principal instanceof UserDetails userDetails) {
            return userRepository.findByEmail(userDetails.getUsername())
                    .orElseThrow(() -> new AccessDeniedException("Utilisateur non trouvé"));
        }

        if (principal instanceof String username
                && StringUtils.hasText(username)
                && !"anonymousUser".equals(username)) {
            return userRepository.findByEmail(username)
                    .orElseThrow(() -> new AccessDeniedException("Utilisateur non trouvé"));
        }

        throw new AccessDeniedException("Utilisateur non authentifié");
    }

    public Long requireCurrentUserId() {
        return requireCurrentUser().getId();
    }
}
