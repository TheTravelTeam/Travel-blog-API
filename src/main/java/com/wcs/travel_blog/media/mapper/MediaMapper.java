package com.wcs.travel_blog.media.mapper;

import com.wcs.travel_blog.article.repository.ArticleRepository;
import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.media.dto.CreateMediaDTO;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.model.Media;
import com.wcs.travel_blog.step.model.Step;
import com.wcs.travel_blog.step.repository.StepRepository;
import com.wcs.travel_blog.travel_diary.model.TravelDiary;
import com.wcs.travel_blog.travel_diary.repository.TravelDiaryRepository;
import org.springframework.stereotype.Component;

@Component
public class MediaMapper {
    private final StepRepository stepRepository;
    private final TravelDiaryRepository travelDiaryRepository;
    private final ArticleRepository articleRepository;

    public MediaMapper(StepRepository stepRepository,
                       TravelDiaryRepository travelDiaryRepository,
                       ArticleRepository articleRepository){
        this.stepRepository = stepRepository;
        this.travelDiaryRepository = travelDiaryRepository;
        this.articleRepository = articleRepository;
    }

    public MediaDTO toDto (Media media){
        if (media == null) return null;

        MediaDTO dto = new MediaDTO();
        Boolean isVisible = media.getIsVisible() != null
                ? media.getIsVisible() : Boolean.TRUE;
        Long stepId = media.getStep() != null
                ? media.getStep().getId()
                : null;
        Long articleId = media.getArticle() != null
                ? media.getArticle().getId()
                : null;
        Long diaryId = media.getTravelDiary() != null
                ? media.getTravelDiary().getId()
                : null;

        dto.setId(media.getId());
        dto.setFileUrl(media.getFileUrl());
        dto.setPublicId(media.getPublicId());
        dto.setFolder(media.getFolder());
        dto.setResourceType(media.getResourceType());
        dto.setFormat(media.getFormat());
        dto.setBytes(media.getBytes());
        dto.setWidth(media.getWidth());
        dto.setHeight(media.getHeight());
        dto.setMediaType(media.getMediaType());
        dto.setIsVisible(isVisible);
        dto.setStepId(stepId);
        dto.setArticleId(articleId);
        dto.setTravelDiaryId(diaryId);
        dto.setCreatedAt(media.getCreatedAt());
        dto.setUpdatedAt(media.getUpdatedAt());
        return dto;
    }

    public Media toEntity (CreateMediaDTO dto){
        Media media = new Media();

        media.setFileUrl(dto.getFileUrl());
        media.setPublicId(dto.getPublicId());
        media.setFolder(dto.getFolder());
        media.setResourceType(dto.getResourceType());
        media.setFormat(dto.getFormat());
        media.setBytes(dto.getBytes());
        media.setWidth(dto.getWidth());
        media.setHeight(dto.getHeight());
        media.setMediaType(dto.getMediaType());
        media.setIsVisible(dto.getIsVisible() != null ? dto.getIsVisible() : Boolean.TRUE);

        if(dto.getStepId() != null){
            Step step = stepRepository.findById(dto.getStepId())
                    .orElseThrow(() -> new ResourceNotFoundException("Etape non trouvée"));
            media.setStep(step);
        }

        if(dto.getTravelDiaryId() != null){
            TravelDiary travelDiary =
                    travelDiaryRepository.findById(dto.getTravelDiaryId()).orElseThrow(() -> new ResourceNotFoundException("Carnet non trouvé"));
            media.setTravelDiary(travelDiary);
        }

        if (dto.getArticleId() != null) {
            media.setArticle(articleRepository.findById(dto.getArticleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Article non trouvé")));
        }

        return media;

    }
}
