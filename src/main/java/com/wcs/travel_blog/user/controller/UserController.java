package com.wcs.travel_blog.user.controller;

import com.wcs.travel_blog.user.dto.UpdateUserRolesDTO;
import com.wcs.travel_blog.user.dto.UpsertUserDTO;
import com.wcs.travel_blog.user.dto.UserDTO;
import com.wcs.travel_blog.user.dto.UserWithDiariesDTO;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers(){
        List<UserDTO> users = userService.getAllUsers();
        if(users.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userId}")
    /**
     * Returns a user profile enriched with diary summaries. Ownership/admin checks rely on the Spring Security
     * {@link Authentication}: owners or admins (ROLE_ADMIN) get every diary, visitors only see public/published ones.
     *
     * <p>We purposefully inspect {@link Authentication#getPrincipal()} instead of using
     * {@code @AuthenticationPrincipal(expression = "id")}. Some security providers expose the principal as a raw
     * {@link String} (e.g. "anonymousUser"), which triggers a SpEL evaluation error when no user is logged in. Reading
     * the principal directly lets us support anonymous visitors safely while still handling authenticated users.</p>
     */
    public ResponseEntity<UserWithDiariesDTO> getUserById(
            @PathVariable Long userId,
            Authentication authentication
    ){
        Long currentUserId = resolveUserId(authentication);
        boolean isAdmin = isAdmin(authentication);

        UserWithDiariesDTO user = userService.getUserById(userId, currentUserId, isAdmin);
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    // authentication.getName() -> returns the user's unique identifier (usually the email or username)
    @PreAuthorize("isFullyAuthenticated() and (hasRole('ROLE_ADMIN') or #email == authentication.name)")
    @GetMapping("/email")
    public ResponseEntity<?> getUserByEmail(
            @RequestParam  String email,
            @RequestParam(required = false, defaultValue = "false") boolean withDiaries
    ) {
        if(withDiaries){
            UserWithDiariesDTO user = userService.getUserWithDiariesByEmail(email);
            if (user == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(user);
        } else {
            UserDTO user = userService.getUserByEmail(email);
            if (user == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(user);
        }
    }

    // authentication.getPrincipal().getPseudo() -> returns the user's pseudo from the UserDetails object
    @PreAuthorize("isFullyAuthenticated() and (hasRole('ROLE_ADMIN') or #pseudo == authentication.principal.pseudo)")
    @GetMapping("/pseudo")
    public ResponseEntity<UserWithDiariesDTO> getUserByPseudo(@RequestParam  String pseudo) {
        UserWithDiariesDTO user = userService.getUserByPseudo(pseudo);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser( @PathVariable Long userId, @Valid @RequestBody UpsertUserDTO upsertUserDTO){
       UserDTO updatedUser = userService.updateUser(userId, upsertUserDTO);
        if(updatedUser == null){
            return ResponseEntity.notFound().build();
        }
       return ResponseEntity.ok(updatedUser);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{userId}/roles")
    public ResponseEntity<UserDTO> updateUserRoles(@PathVariable Long userId, @Valid @RequestBody UpdateUserRolesDTO updateUserRolesDTO) {
        UserDTO updatedUser = userService.updateUserRoles(userId, updateUserRolesDTO.getAdmin());
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<String> deleteUserById(@PathVariable Long userId){
        userService.deleteUserById(userId);
        return ResponseEntity.ok("Utilisateur supprimé avec succès.");
    }

    private Long resolveUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User user) {
            return user.getId();
        }

        return null;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority()));
    }


}
