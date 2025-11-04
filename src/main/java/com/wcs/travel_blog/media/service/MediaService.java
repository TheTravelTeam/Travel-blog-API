package com.wcs.travel_blog.media.service;

import com.wcs.travel_blog.article.repository.ArticleRepository;
import com.wcs.travel_blog.cloudinary.dto.CloudinaryAssetRequest;
import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.media.dto.CreateMediaDTO;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.dto.UpdateMediaDTO;
import com.wcs.travel_blog.media.mapper.MediaMapper;
import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.media.model.MediaType;
import com.wcs.travel_blog.media.repository.MediaRepository;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MediaService {
    private final MediaRepository mediaRepository;
    private final MediaMapper mediaMapper;
    private final TravelDiaryRepository travelDiaryRepository;
    private final StepRepository stepRepository;
    private final ArticleRepository articleRepository;

    public MediaService(MediaRepository mediaRepository,
                        MediaMapper mediaMapper,
                        TravelDiaryRepository travelDiaryRepository,
                        StepRepository stepRepository,
                        ArticleRepository articleRepository) {
        this.mediaRepository = mediaRepository;
        this.mediaMapper = mediaMapper;
        this.travelDiaryRepository = travelDiaryRepository;
        this.stepRepository = stepRepository;
        this.articleRepository = articleRepository;
    }

    public List<MediaDTO> getAllMedias() {
        return mediaRepository.findAll().stream()
                .map(mediaMapper::toDto)
                .toList();
    }

    public MediaDTO getMediaById(Long id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Le média avec l'id " + id + " n'a pas été trouvé"));
        return mediaMapper.toDto(media);
    }

    public MediaDTO createMedia(CreateMediaDTO dto) {
        Media media = mediaMapper.toEntity(dto);
        media.setCreatedAt(LocalDateTime.now());
        media.setUpdatedAt(LocalDateTime.now());
        Media saved = mediaRepository.save(media);
        return mediaMapper.toDto(saved);
    }

    public MediaDTO updateMedia(Long id, UpdateMediaDTO dto) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Média non trouvé"));

        if (StringUtils.hasText(dto.getFileUrl()) && !dto.getFileUrl().equals(media.getFileUrl())) {
            media.setFileUrl(dto.getFileUrl());
        }
        if (StringUtils.hasText(dto.getPublicId()) && !dto.getPublicId().equals(media.getPublicId())) {
            media.setPublicId(dto.getPublicId());
        }
        if (dto.getMediaType() != null && !dto.getMediaType().equals(media.getMediaType())) {
            media.setMediaType(dto.getMediaType());
        }
        if (dto.getIsVisible() != null && !dto.getIsVisible().equals(media.getIsVisible())) {
            media.setIsVisible(dto.getIsVisible());
        }

        applyRelations(media, dto.getStepId(), dto.getTravelDiaryId(), dto.getArticleId());

        media.setUpdatedAt(LocalDateTime.now());

        Media saved = mediaRepository.save(media);
        return mediaMapper.toDto(saved);
    }

    public MediaDTO saveFromCloudinary(CloudinaryAssetRequest request) {
        Media media = mediaRepository.findByPublicId(request.getPublicId())
            .orElseGet(Media::new);

        if (media.getId() == null) {
            media.setCreatedAt(LocalDateTime.now());
        }

        media.setFileUrl(request.getSecureUrl());
        media.setPublicId(request.getPublicId());
        media.setIsVisible(request.getIsVisible() != null ? request.getIsVisible() : Boolean.TRUE);

        media.setMediaType(request.getMediaType() != null ? request.getMediaType() : MediaType.PHOTO);

        applyRelations(media, request.getStepId(), request.getTravelDiaryId(), request.getArticleId());

        media.setUpdatedAt(LocalDateTime.now());
        Media saved = mediaRepository.save(media);
        return mediaMapper.toDto(saved);
    }

    public MediaDTO getMediaByPublicId(String publicId) {
        Media media = mediaRepository.findByPublicId(publicId)
            .orElseThrow(() -> new ResourceNotFoundException("Aucun média trouvé pour le publicId " + publicId));
        return mediaMapper.toDto(media);
    }

    public void deleteMediaById(Long id) {
        Media media = mediaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Media non trouvé"));
        mediaRepository.delete(media);
    }

    public List<MediaDTO> getMediaByStep(Long stepId) {
        return mediaRepository.findByStep_Id(stepId).stream()
                .map(mediaMapper::toDto)
                .toList();
    }

    public List<MediaDTO> getMediaByArticle(Long articleId) {
        return mediaRepository.findByArticle_Id(articleId).stream()
                .map(mediaMapper::toDto)
                .toList();
    }

    public MediaDTO getMediaByTravelDiary(Long diaryId) {
        Media media = mediaRepository.findByTravelDiary_Id(diaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucun média lié au carnet " + diaryId));
        return mediaMapper.toDto(media);
    }

    private void applyRelations(Media media, Long stepId, Long travelDiaryId, Long articleId) {
        media.setStep(stepId != null ? stepRepository.findById(stepId)
                .orElseThrow(() -> new ResourceNotFoundException("Étape non trouvée")) : null);

        media.setTravelDiary(travelDiaryId != null ? travelDiaryRepository.findById(travelDiaryId)
                .orElseThrow(() -> new ResourceNotFoundException("Carnet non trouvé")) : null);

        media.setArticle(articleId != null ? articleRepository.findById(articleId)
                .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé")) : null);
    }
}
