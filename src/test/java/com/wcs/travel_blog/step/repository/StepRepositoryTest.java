package com.wcs.travel_blog.step.repository;

import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StepRepositoryTest {

    @Autowired
    private StepRepository stepRepository;

    @Autowired
    private TravelDiaryRepository travelDiaryRepository;

    @Test
    @DisplayName("searchVisibleSteps exclut les étapes des carnets privés ou brouillons")
    void shouldExcludeStepsFromHiddenDiaries() {
        TravelDiary publicDiary = buildDiary(false, true, "Public diary");
        travelDiaryRepository.save(publicDiary);
        Step publicStep = buildStep(publicDiary, "Adventure step");
        stepRepository.save(publicStep);

        TravelDiary privateDiary = buildDiary(true, true, "Private diary");
        travelDiaryRepository.save(privateDiary);
        Step privateStep = buildStep(privateDiary, "Adventure step");
        stepRepository.save(privateStep);

        TravelDiary draftDiary = buildDiary(false, false, "Draft diary");
        travelDiaryRepository.save(draftDiary);
        Step draftStep = buildStep(draftDiary, "Adventure step");
        stepRepository.save(draftStep);

        TravelDiary disabledDiary = buildDiary(false, true, "Disabled diary");
        disabledDiary.setStatus(TravelStatus.DISABLED);
        travelDiaryRepository.save(disabledDiary);
        Step disabledStep = buildStep(disabledDiary, "Adventure step");
        stepRepository.save(disabledStep);

        List<Step> results = stepRepository.searchVisibleSteps("adventure");

        assertThat(results)
                .hasSize(1)
                .first()
                .extracting(Step::getTravelDiary)
                .isEqualTo(publicDiary);
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

    private Step buildStep(TravelDiary diary, String title) {
        Step step = new Step();
        step.setTitle(title);
        step.setDescription(title + " description");
        step.setCreatedAt(LocalDateTime.now());
        step.setUpdatedAt(LocalDateTime.now());
        step.setCity("Paris");
        step.setCountry("France");
        step.setContinent("Europe");
        step.setTravelDiary(diary);
        return step;
    }
}
