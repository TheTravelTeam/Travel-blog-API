package com.wcs.travel_blog.media.repository;


import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.media.model.MediaType;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import java.util.List;
import java.util.Optional;

import static com.wcs.travel_blog.media.model.MediaType.PHOTO;
import static com.wcs.travel_blog.media.model.MediaType.VIDEO;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.sql.init.mode=never")
@ActiveProfiles("test")
class MediaRepositoryTest {

    @Autowired
    private MediaRepository mediaRepository;
    @Autowired
    private StepRepository stepRepository;

    @BeforeEach
    void resetDatabase() {
        mediaRepository.deleteAll();
        stepRepository.deleteAll();
    }

    private Step createStep() {
        Step step = new Step();
        step.setTitle("Step de test");
        step.setLatitude(48.8566);
        step.setLongitude(2.3522);
        step.setCity("Berlin");
        step.setCountry("Berlin");
        step.setContinent("Berlin");
        // mets ici les champs obligatoires de ton entity Step (dates, status, etc.)
        return stepRepository.save(step);
    }

    private Media media1(Step createStep) {
        Media media = new Media();
        media.setFileUrl("https://cdn/a.jpg");
        media.setMediaType(PHOTO); // ou enum.name()
        media.setStep(createStep);   // si relation, remplace par setStep(...)
        return media;
    }

    private Media media2(Step createStep) {
        Media media = new Media();
        media.setFileUrl("https://cdn/b.mp4");
        media.setMediaType(VIDEO);
        media.setStep(createStep);
        return media;
    }

    @Test
    void saveAndFindAll_shouldReturnSaved() {
        Step createdStep = createStep();
        mediaRepository.saveAll(List.of(media1(createdStep), media2(createdStep)));
        List<Media> medias = mediaRepository.findAll();
        assertThat(medias).hasSize(2);
        assertThat(medias.get(0).getMediaType()).isIn(MediaType.PHOTO, MediaType.VIDEO);
        assertThat(medias.get(0).getStep()).isNotNull();
    }

    @Test
    void findByStepId_shouldReturnOnlyStepMedia() {
        Step createdStep = createStep();
        Step createdStep2 = createStep();
        Media media1 = media1(createdStep);
        Media media2 = media2(createdStep);
        Media media3 = media1(createdStep2);

        mediaRepository.saveAll(List.of(media1, media2, media3));

        List<Media> step1Medias = mediaRepository.findByStep_Id(createdStep.getId());
        assertThat(step1Medias).hasSize(2);
        assertThat(step1Medias).allMatch(media -> media.getStep().getId().equals(createdStep.getId()));
    }

    @Test
    void delete_shouldRemoveFromDatabase() {
        Step createdStep = createStep();
        Media saved = mediaRepository.save(media1(createdStep));
        Long id = saved.getId();

        assertThat(mediaRepository.findById(id)).isPresent();

        mediaRepository.delete(saved);

        Optional<Media> after = mediaRepository.findById(id);
        assertThat(after).isEmpty();
    }
}
