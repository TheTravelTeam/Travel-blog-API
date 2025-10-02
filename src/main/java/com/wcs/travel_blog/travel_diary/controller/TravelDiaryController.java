package com.wcs.travel_blog.travel_diary.controller;

import com.wcs.travel_blog.travel_diary.dto.CreateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.TravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.dto.UpdateTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.service.TravelDiaryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.wcs.travel_blog.user.model.User;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/travel-diaries")
public class TravelDiaryController {

    private final TravelDiaryService travelDiaryService;

    public TravelDiaryController(TravelDiaryService travelDiaryService) {
        this.travelDiaryService = travelDiaryService;
    }

    @GetMapping
    public ResponseEntity<List<TravelDiaryDTO>> getAllTravelDiaries(){
        List<TravelDiaryDTO> travelDiaries = travelDiaryService.getAllTravelDiaries();
        if(travelDiaries.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(travelDiaries);
    }

//    @GetMapping("/users/{userId}")
    /**
     * REST entrypoint dedicated to a user's diaries. Consumers retrieve the profile via {@code /users/{id}} and call
     * this endpoint for the diary collection. Ownership/admin privileges are derived from the Spring Security
     * {@link Authentication}: owners or admins (ROLE_ADMIN) see every diary, visitors only the public/published ones.
     *
     * <p>We inspect {@link Authentication#getPrincipal()} directly instead of relying on
     * {@code @AuthenticationPrincipal(expression = "id")}. When the principal is a simple {@link String} (e.g.
     * "anonymousUser"), the SpEL expression would throw. Manual inspection keeps the endpoint accessible to anonymous
     * visitors while still resolving the ID for authenticated users.</p>
     */
    @GetMapping("/users/{userId}")
    public ResponseEntity<List<TravelDiaryDTO>> getTravelDiariesByUser(
            @PathVariable Long userId,
            Authentication authentication
    ) {
        Long currentUserId = resolveUserId(authentication);
        boolean isAdmin = isAdmin(authentication);

        List<TravelDiaryDTO> travelDiaries = travelDiaryService.getTravelDiariesForUser(userId, currentUserId, isAdmin);
        if (travelDiaries.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(travelDiaries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TravelDiaryDTO> getTravelDiaryById(@PathVariable Long id){
        TravelDiaryDTO travelDiary = travelDiaryService.getTravelDiaryById(id);
        if(travelDiary==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(travelDiary);
    }

    @PostMapping
    public ResponseEntity<TravelDiaryDTO> createTravelDiary(@Valid @RequestBody CreateTravelDiaryDTO createTravelDiaryRequest){

        TravelDiaryDTO travelDiaryResponse = travelDiaryService.createTravelDiary(createTravelDiaryRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(travelDiaryResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TravelDiaryDTO> updateTravelDiary(@PathVariable Long id , @RequestBody UpdateTravelDiaryDTO updateTravelDiaryRequest){
        TravelDiaryDTO updateTravelDiaryResponse = travelDiaryService.updateTravelDiary(id,updateTravelDiaryRequest);
        if(updateTravelDiaryResponse==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updateTravelDiaryResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTravelDiary(@PathVariable Long id){
        travelDiaryService.deleteTravelDiary(id);
        return ResponseEntity.ok("Carnet de voyage supprimé avec succès");
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
