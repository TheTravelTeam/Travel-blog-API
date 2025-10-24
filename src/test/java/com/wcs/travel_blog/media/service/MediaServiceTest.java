package com.wcs.travel_blog.media.service;

import com.wcs.travel_blog.article.model.Article;
import com.wcs.travel_blog.article.repository.ArticleRepository;
import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.media.dto.CreateMediaDTO;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.dto.UpdateMediaDTO;
import com.wcs.travel_blog.media.mapper.MediaMapper;
import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.media.model.MediaType;
import com.wcs.travel_blog.media.repository.MediaRepository;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static com.wcs.travel_blog.media.model.MediaType.PHOTO;
import static com.wcs.travel_blog.media.model.MediaType.VIDEO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private MediaRepository mediaRepository;

    @Mock
    private StepRepository stepRepository;

    @Mock
    private TravelDiaryRepository travelDiaryRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MediaMapper mediaMapper;

    @InjectMocks
    private MediaService mediaService;

    // -------- helpers -------------------------------------------------------

    private Step step(Long id) {
        Step step = new Step();
        // en test unitaire, pas besoin des champs NOT NULL (pas de DB)
        // on met un id si nécessaire pour les asserts
        try {
            Field idField = Step.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(step, id);
        } catch (Exception ignored) {}
        return step;
    }

    private Media mediaEntity(Long id, String url, MediaType type, Long stepId) {
        Media mediaEntity = new Media();
        mediaEntity.setId(id);
        mediaEntity.setFileUrl(url);
        mediaEntity.setMediaType(type);
        mediaEntity.setStep(step(stepId));
        mediaEntity.setArticle(null);
        return mediaEntity;
    }

    private Article article(Long id) {
        Article article = new Article();
        try {
            Field idField = Article.class.getDeclaredField("Id");
            idField.setAccessible(true);
            idField.set(article, id);
        } catch (Exception ignored) {}
        return article;
    }

    private MediaDTO mediaDTO(Long id, String url, MediaType type, Long stepId) {
        MediaDTO mediaDTO = new MediaDTO();
        mediaDTO.setId(id);
        mediaDTO.setFileUrl(url);
        mediaDTO.setMediaType(type);
        mediaDTO.setStepId(stepId);
        mediaDTO.setArticleId(null);
        return mediaDTO;
    }

    // -------- tests ---------------------------------------------------------

    @Test
    void getAllMedias_shouldReturnListOfDTO() {
        Media media1 = mediaEntity(1L, "https://a.jpg", PHOTO, 10L);
        Media media2 = mediaEntity(2L, "https://b.mp4", VIDEO, 10L);
        when(mediaRepository.findAll()).thenReturn(List.of(media1, media2));
        when(mediaMapper.toDto(media1)).thenReturn(mediaDTO(1L, "https://a.jpg", PHOTO, 10L));
        when(mediaMapper.toDto(media2)).thenReturn(mediaDTO(2L, "https://b.mp4", VIDEO, 10L));

        List<MediaDTO> res = mediaService.getAllMedias();

        assertThat(res).hasSize(2);
        assertThat(res.get(0).getMediaType()).isEqualTo(PHOTO);
        verify(mediaRepository).findAll();
        verify(mediaMapper, times(2)).toDto(any(Media.class));
    }

    @Test
    void getMediaById_shouldReturnDTO() {
        Media media = mediaEntity(1L, "https://a.jpg", PHOTO, 10L);
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(media));
        when(mediaMapper.toDto(media)).thenReturn(mediaDTO(1L, "https://a.jpg", PHOTO, 10L));

        MediaDTO res = mediaService.getMediaById(1L);

        assertThat(res.getId()).isEqualTo(1L);
        assertThat(res.getFileUrl()).isEqualTo("https://a.jpg");
    }

    @Test
    void getMediaById_shouldThrowWhenNotFound() {
        when(mediaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.getMediaById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void getMediaByStep_shouldReturnListDTO() {
        Media media1 = mediaEntity(1L, "https://a.jpg", PHOTO, 10L);
        Media media2 = mediaEntity(2L, "https://b.mp4", VIDEO, 10L);
        when(mediaRepository.findByStep_Id(10L)).thenReturn(List.of(media1, media2));
        when(mediaMapper.toDto(media1)).thenReturn(mediaDTO(1L, "https://a.jpg", PHOTO, 10L));
        when(mediaMapper.toDto(media2)).thenReturn(mediaDTO(2L, "https://b.mp4", VIDEO, 10L));

        List<MediaDTO> res = mediaService.getMediaByStep(10L);

        assertThat(res).hasSize(2);
        assertThat(res).anyMatch(d -> d.getMediaType() == VIDEO);
    }

    @Test
    void getMediaByArticle_shouldReturnListDTO() {
        Media media1 = mediaEntity(1L, "https://a.jpg", PHOTO, null);
        Media media2 = mediaEntity(2L, "https://b.mp4", VIDEO, null);
        when(mediaRepository.findByArticle_Id(5L)).thenReturn(List.of(media1, media2));
        MediaDTO dto1 = mediaDTO(1L, "https://a.jpg", PHOTO, null);
        dto1.setArticleId(5L);
        MediaDTO dto2 = mediaDTO(2L, "https://b.mp4", VIDEO, null);
        dto2.setArticleId(5L);

        when(mediaMapper.toDto(media1)).thenReturn(dto1);
        when(mediaMapper.toDto(media2)).thenReturn(dto2);

        List<MediaDTO> res = mediaService.getMediaByArticle(5L);

        assertThat(res).hasSize(2);
        assertThat(res).allMatch(m -> m.getArticleId().equals(5L));
    }

    @Test
    void createMedia_shouldMapSaveReturnDTO() {
        CreateMediaDTO in = new CreateMediaDTO();
        in.setFileUrl("https://new.jpg");
        in.setMediaType(PHOTO);
        in.setStepId(11L);
        in.setArticleId(5L);

        Media toSave = mediaEntity(null, "https://new.jpg", PHOTO, 11L);
        Media saved  = mediaEntity(3L, "https://new.jpg", PHOTO, 11L);
        MediaDTO out = mediaDTO(3L, "https://new.jpg", PHOTO, 11L);
        out.setArticleId(5L);

        when(mediaMapper.toEntity(in)).thenReturn(toSave);
        when(mediaRepository.save(toSave)).thenReturn(saved);
        when(mediaMapper.toDto(saved)).thenReturn(out);

        MediaDTO res = mediaService.createMedia(in);

        assertThat(res.getId()).isEqualTo(3L);
        assertThat(res.getMediaType()).isEqualTo(PHOTO);
        verify(mediaMapper).toEntity(in);
        verify(mediaRepository).save(toSave);
        verify(mediaMapper).toDto(saved);
    }

    @Test
    void updateMedia_shouldFindUpdateSaveReturnDTO() {
        UpdateMediaDTO updateMediaDTO = new UpdateMediaDTO();
        updateMediaDTO.setFileUrl("https://upd.jpg");
        updateMediaDTO.setMediaType(PHOTO);
        updateMediaDTO.setStepId(10L);
        updateMediaDTO.setArticleId(null);

        Media existing = mediaEntity(1L, "https://a.jpg", VIDEO, 9L);
        Media after    = mediaEntity(1L, "https://upd.jpg", PHOTO, 10L);
        MediaDTO mediaDTO = mediaDTO(1L, "https://upd.jpg", PHOTO, 10L);

        when(mediaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(stepRepository.findById(10L)).thenReturn(Optional.of(step(10L))); // <-- AJOUT
        when(mediaRepository.save(existing)).thenReturn(after);
        when(mediaMapper.toDto(after)).thenReturn(mediaDTO);

        MediaDTO res = mediaService.updateMedia(1L, updateMediaDTO);

        assertThat(res.getFileUrl()).isEqualTo("https://upd.jpg");
        assertThat(res.getMediaType()).isEqualTo(PHOTO);
        assertThat(res.getStepId()).isEqualTo(10L);

        // On vérifie que le service a bien persisté les changements
        verify(mediaRepository).save(existing);
    }

    @Test
    void updateMedia_shouldThrowWhenNotFound() {
        UpdateMediaDTO updateMediaDTO = new UpdateMediaDTO();
        when(mediaRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.updateMedia(123L, updateMediaDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Média non trouvé");
    }

    @Test
    void updateMedia_shouldAttachArticleWhenProvided() {
        UpdateMediaDTO dto = new UpdateMediaDTO();
        dto.setArticleId(7L);

        Media existing = mediaEntity(1L, "https://a.jpg", PHOTO, null);
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(articleRepository.findById(7L)).thenReturn(Optional.of(article(7L)));
        when(mediaRepository.save(existing)).thenReturn(existing);

        MediaDTO expected = mediaDTO(1L, "https://a.jpg", PHOTO, null);
        expected.setArticleId(7L);
        when(mediaMapper.toDto(existing)).thenReturn(expected);

        MediaDTO res = mediaService.updateMedia(1L, dto);

        assertThat(res.getArticleId()).isEqualTo(7L);
        assertThat(existing.getArticle()).isNotNull();
    }

    @Test
    void deleteMediaById_shouldFindAndDelete() {
        Media existing = mediaEntity(1L, "https://a.jpg", PHOTO, 10L);
        when(mediaRepository.findById(1L)).thenReturn(Optional.of(existing));

        mediaService.deleteMediaById(1L);

        verify(mediaRepository).delete(existing);
    }

    @Test
    void deleteMediaById_shouldThrowWhenNotFound() {
        when(mediaRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> mediaService.deleteMediaById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Media non trouvé");
    }
}
