package com.wcs.travel_blog.travel_diary.repository;

import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.user.model.User;
import com.wcs.travel_blog.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TravelDiaryRepositoryTest {

    @Autowired
    private TravelDiaryRepository travelDiaryRepository;

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("findAllPublishedPublicWithSteps ne retourne que les carnets publics, publiés et avec étapes")
    void shouldReturnOnlyPublicPublishedDiariesWithSteps() {
        TravelDiary publicDiary = buildDiary(false, true, "Public diary");
        TravelDiary privateDiary = buildDiary(true, true, "Private diary");
        TravelDiary draftDiary = buildDiary(false, false, "Draft diary");
        TravelDiary noStepDiary = buildDiary(false, true, "No step diary");

        travelDiaryRepository.saveAll(List.of(publicDiary, privateDiary, draftDiary, noStepDiary));

        createStep(publicDiary, "Paris");
        createStep(privateDiary, "Berlin");
        createStep(draftDiary, "Madrid");

        List<TravelDiary> visibleDiaries = travelDiaryRepository.findAllPublishedPublicWithSteps();

        assertThat(visibleDiaries)
                .hasSize(1)
                .first()
                .extracting(TravelDiary::getTitle)
                .isEqualTo("Public diary");
    }

    @Test
    @DisplayName("findPublishedPublicByUserId ne remonte que les carnets publics/publies d'un utilisateur")
    void shouldReturnOnlyPublicPublishedDiariesForGivenUser() {
        User owner = buildUser("owner@example.com");
        userRepository.save(owner);

        TravelDiary publicDiary = buildDiary(false, true, "Owner public");
        publicDiary.setUser(owner);
        TravelDiary privateDiary = buildDiary(true, true, "Owner private");
        privateDiary.setUser(owner);
        TravelDiary draftDiary = buildDiary(false, false, "Owner draft");
        draftDiary.setUser(owner);

        travelDiaryRepository.saveAll(List.of(publicDiary, privateDiary, draftDiary));

        createStep(publicDiary, "Paris");
        createStep(privateDiary, "Berlin");

        List<TravelDiary> visibleDiaries = travelDiaryRepository.findPublishedPublicByUserId(owner.getId());

        assertThat(visibleDiaries)
                .hasSize(1)
                .first()
                .extracting(TravelDiary::getTitle)
                .isEqualTo("Owner public");
    }

    @Test
    @DisplayName("findAllPublishedPublicWithSteps exclut les carnets désactivés")
    void shouldExcludeDisabledDiariesFromPublicListings() {
        TravelDiary visibleDiary = buildDiary(false, true, "Visible diary");
        travelDiaryRepository.save(visibleDiary);

        TravelDiary disabledDiary = buildDiary(false, true, "Disabled diary");
        disabledDiary.setStatus(TravelStatus.DISABLED);
        travelDiaryRepository.save(disabledDiary);

        createStep(visibleDiary, "Paris");
        createStep(disabledDiary, "Lyon");

        List<TravelDiary> visibleDiaries = travelDiaryRepository.findAllPublishedPublicWithSteps();

        assertThat(visibleDiaries)
                .hasSize(1)
                .first()
                .extracting(TravelDiary::getTitle)
                .isEqualTo("Visible diary");
    }

    @Test
    @DisplayName("findAllByUserIdOrderByUpdatedAtDesc remonte tous les carnets de l'utilisateur")
    void shouldReturnAllDiariesForOwner() {
        User owner = buildUser("collector@example.com");
        userRepository.save(owner);

        TravelDiary publicDiary = buildDiary(false, true, "Public");
        publicDiary.setUser(owner);
        TravelDiary privateDiary = buildDiary(true, true, "Private");
        privateDiary.setUser(owner);
        TravelDiary draftDiary = buildDiary(false, false, "Draft");
        draftDiary.setUser(owner);

        travelDiaryRepository.saveAll(List.of(publicDiary, privateDiary, draftDiary));

        createStep(publicDiary, "Paris");

        List<TravelDiary> diaries = travelDiaryRepository.findAllByUserIdOrderByUpdatedAtDesc(owner.getId());

        assertThat(diaries)
                .extracting(TravelDiary::getTitle)
                .containsExactlyInAnyOrder("Public", "Private", "Draft");
    }

    @Test
    @DisplayName("searchVisibleDiaries recherche aussi sur les villes/pays/continents des étapes")
    void shouldSearchOnStepLocationFields() {
        TravelDiary matchingDiary = buildDiary(false, true, "City hunter");
        travelDiaryRepository.save(matchingDiary);
        createStep(matchingDiary, "Lisbonne");

        TravelDiary otherDiary = buildDiary(false, true, "Other trip");
        travelDiaryRepository.save(otherDiary);
        createStep(otherDiary, "Berlin");

        List<TravelDiary> results = travelDiaryRepository.searchVisibleDiaries("lisbo");

        assertThat(results)
                .hasSize(1)
                .first()
                .extracting(TravelDiary::getTitle)
                .isEqualTo("City hunter");
    }

    @Test
    @DisplayName("searchVisibleDiaries exclut les carnets privés ou non publiés")
    void shouldExcludePrivateOrDraftDiaries() {
        TravelDiary publicDiary = buildDiary(false, true, "Summer trip");
        travelDiaryRepository.save(publicDiary);
        createStep(publicDiary, "Paris");

        TravelDiary privateDiary = buildDiary(true, true, "Secret getaway");
        travelDiaryRepository.save(privateDiary);
        createStep(privateDiary, "Paris");

        TravelDiary draftDiary = buildDiary(false, false, "Draft journey");
        travelDiaryRepository.save(draftDiary);
        createStep(draftDiary, "Paris");

        List<TravelDiary> results = travelDiaryRepository.searchVisibleDiaries("paris");

        assertThat(results)
                .extracting(TravelDiary::getTitle)
                .containsExactly("Summer trip");
    }

    private TravelDiary buildDiary(Boolean isPrivate, Boolean isPublished, String title) {
        TravelDiary diary = new TravelDiary();
        diary.setTitle(title);
        diary.setDescription(title + " description");
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());
        diary.setIsPrivate(isPrivate);
        diary.setIsPublished(isPublished);
        diary.setStatus(TravelStatus.COMPLETED);
        return diary;
    }

    private User buildUser(String email) {
        User user = new User();
        user.setEmail(email);
        user.setPassword("Password123!");
        user.getRoles().add("ROLE_USER");
        return user;
    }

    private void createStep(TravelDiary diary, String city) {
        Step step = new Step();
        step.setTitle("Step for " + diary.getTitle());
        step.setDescription("Description for " + diary.getTitle());
        step.setCity(city);
        step.setCountry("Country " + city);
        step.setContinent("Europe");
        step.setTravelDiary(diary);
        stepRepository.save(step);
    }
}
