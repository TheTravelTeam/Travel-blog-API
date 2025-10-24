package com.wcs.travel_blog.search.controller;

import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.media.model.MediaType;
import com.wcs.travel_blog.media.repository.MediaRepository;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SearchControllerIT {

    private static final String OWNER_EMAIL = "owner@example.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TravelDiaryRepository travelDiaryRepository;

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private MediaRepository mediaRepository;

    private User owner;
    private TravelDiary publicDiary;

    @BeforeEach
    void setUp() {
        stepRepository.deleteAll();
        mediaRepository.deleteAll();
        travelDiaryRepository.deleteAll();
        userRepository.deleteAll();

        owner = createUser(OWNER_EMAIL, "owner");
        User publicAuthor = createUser("public@example.com", "public");

        publicDiary = createDiary(publicAuthor, "Public Completed Diary", "Voyage public incroyable", false, true, TravelStatus.COMPLETED);
        attachCover(publicDiary, "https://cdn.example.com/public.jpg", true);
        createStep(publicDiary, "Public Step", "Etape publique du voyage", TravelStatus.COMPLETED);

        TravelDiary privateDiary = createDiary(publicAuthor, "Private Diary", "Voyage prive confidentiel", true, true, TravelStatus.COMPLETED);
        createStep(privateDiary, "Private Step", "Etape privee du voyage", TravelStatus.COMPLETED);

        TravelDiary inProgressDiary = createDiary(publicAuthor, "In Progress Diary", "Voyage toujours en cours", false, true, TravelStatus.IN_PROGRESS);
        createStep(inProgressDiary, "In Progress Step", "Etape publique encore en preparation", TravelStatus.COMPLETED);

        TravelDiary disabledDiary = createDiary(publicAuthor, "Disabled Diary", "Carnet temporairement masqué", false, true, TravelStatus.DISABLED);
        attachCover(disabledDiary, "https://cdn.example.com/disabled.jpg", true);
        createStep(disabledDiary, "Disabled Step", "Etape masquée", TravelStatus.COMPLETED);

        TravelDiary ownerDiary = createDiary(owner, "Owner Draft Diary", "Carnet perso en cours", true, false, TravelStatus.IN_PROGRESS);
        createStep(ownerDiary, "Owner Step", "Etape perso encore a planifier", TravelStatus.IN_PROGRESS);
    }

    @Test
    void shouldReturnPublicResultsForAnonymousUser() throws Exception {
        mockMvc.perform(get("/search")
                        .param("query", "voyage")
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diaries", hasSize(2)))
                .andExpect(jsonPath("$.diaries[*].title", containsInAnyOrder("Public Completed Diary", "In Progress Diary")))
                .andExpect(jsonPath("$.steps", hasSize(1)))
                .andExpect(jsonPath("$.steps[0].title").value("Public Step"))
                .andExpect(jsonPath("$.steps[0].diaryId").value(publicDiary.getId()))
                .andExpect(jsonPath("$.steps[0].diaryTitle").value("Public Completed Diary"));
    }

    @Test
    void shouldReturnOwnerSpecificResultsWhenAuthenticated() throws Exception {
        User persistedOwner = userRepository.findByEmail(OWNER_EMAIL).orElseThrow();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                persistedOwner,
                persistedOwner.getPassword(),
                persistedOwner.getAuthorities()
        );

        mockMvc.perform(get("/search")
                        .param("query", "perso")
                        .with(authentication(authToken))
                        .accept(org.springframework.http.MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.diaries", hasSize(1)))
                .andExpect(jsonPath("$.diaries[0].title").value("Owner Draft Diary"))
                .andExpect(jsonPath("$.diaries[0].description").value("Carnet perso en cours"))
                .andExpect(jsonPath("$.steps", hasSize(1)))
                .andExpect(jsonPath("$.steps[0].title").value("Owner Step"))
                .andExpect(jsonPath("$.steps[0].diaryTitle").value("Owner Draft Diary"));
    }

    @Test
    void shouldReturn400WhenQueryMissing() throws Exception {
        mockMvc.perform(get("/search"))
                .andExpect(status().isBadRequest());
    }

    private User createUser(String email, String pseudo) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("$2a$10$abcdefghijklmnopqrstuvabcd1234567890");
        user.setPseudo(pseudo);
        user.setRoles(Set.of("ROLE_USER"));
        user.setBiography("Bio de " + pseudo);
        user.setAvatar("https://cdn.example.com/avatar.png");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    private TravelDiary createDiary(User owner, String title, String description, boolean isPrivate, boolean isPublished, TravelStatus status) {
        TravelDiary diary = new TravelDiary();
        diary.setTitle(title);
        diary.setDescription(description);
        diary.setUser(owner);
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());
        diary.setIsPrivate(isPrivate);
        diary.setIsPublished(isPublished);
        diary.setStatus(status);
        diary.setCanComment(true);
        diary.setLatitude(0.0);
        diary.setLongitude(0.0);
        return travelDiaryRepository.save(diary);
    }

    private Step createStep(TravelDiary diary, String title, String description, TravelStatus status) {
        Step step = new Step();
        step.setTitle(title);
        step.setDescription(description);
        step.setTravelDiary(diary);
        step.setStatus(status);
        step.setCity("Paris");
        step.setCountry("France");
        step.setContinent("Europe");
        step.setLatitude(0.0);
        step.setLongitude(0.0);
        step.setStartDate(LocalDate.now());
        step.setEndDate(LocalDate.now());
        step.setCreatedAt(LocalDateTime.now());
        step.setUpdatedAt(LocalDateTime.now());
        return stepRepository.save(step);
    }

    private void attachCover(TravelDiary diary, String fileUrl, boolean visible) {
        Media media = new Media();
        media.setFileUrl(fileUrl);
        media.setMediaType(MediaType.PHOTO);
        media.setIsVisible(visible);
        media.setTravelDiary(diary);
        mediaRepository.save(media);
        diary.setMedia(media);
        travelDiaryRepository.save(diary);
    }
}
