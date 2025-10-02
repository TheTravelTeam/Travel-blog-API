package com.wcs.travel_blog.user.mapper;

import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.travel_diary.dto.SummaryTravelDiaryDTO;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.model.TravelStatus;
import com.wcs.travel_blog.user.dto.UserWithDiariesDTO;
import com.wcs.travel_blog.user.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper(mock(PasswordEncoder.class));
    }

    @Test
    void shouldFilterDiariesForVisitor() {
        User user = createUserWithDiaries();

        UserWithDiariesDTO dto = userMapper.converToDtoWithDiaries(user, false);

        assertThat(dto.getTravelDiaries()).hasSize(1);
        SummaryTravelDiaryDTO visibleDiary = dto.getTravelDiaries().getFirst();
        assertThat(visibleDiary.getId()).isEqualTo(1L);
    }

    @Test
    void shouldReturnAllDiariesForOwner() {
        User user = createUserWithDiaries();

        UserWithDiariesDTO dto = userMapper.converToDtoWithDiaries(user, true);

        assertThat(dto.getTravelDiaries()).extracting(SummaryTravelDiaryDTO::getId)
                .containsExactlyInAnyOrder(1L, 2L, 3L, 4L);
    }

    private User createUserWithDiaries() {
        TravelDiary publicDiary = buildDiary(1L, false, true, List.of(buildStep(10L)));
        TravelDiary privateDiary = buildDiary(2L, true, true, List.of(buildStep(20L)));
        TravelDiary draftDiary = buildDiary(3L, false, false, List.of(buildStep(30L)));
        TravelDiary diaryWithoutSteps = buildDiary(4L, false, true, List.of());

        User user = new User();
        user.setId(99L);
        user.setPseudo("traveler");
        user.setEmail("traveler@example.com");
        user.setPassword("password123!");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setTravel_diaries(List.of(publicDiary, privateDiary, draftDiary, diaryWithoutSteps));
        return user;
    }

    private TravelDiary buildDiary(Long id, Boolean isPrivate, Boolean isPublished, List<Step> steps) {
        TravelDiary diary = new TravelDiary();
        diary.setId(id);
        diary.setTitle("Diary " + id);
        diary.setDescription("Description " + id);
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());
        diary.setIsPrivate(isPrivate);
        diary.setIsPublished(isPublished);
        diary.setStatus(TravelStatus.COMPLETED);
        diary.setCanComment(Boolean.TRUE);
        diary.setLatitude(1.0);
        diary.setLongitude(2.0);
        diary.setSteps(steps);
        return diary;
    }

    private Step buildStep(Long id) {
        Step step = new Step();
        step.setId(id);
        step.setTitle("Step " + id);
        step.setCity("City");
        step.setCountry("Country");
        step.setContinent("Continent");
        step.setCreatedAt(LocalDateTime.now());
        step.setUpdatedAt(LocalDateTime.now());
        return step;
    }
}
