package com.wcs.travel_blog.media.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wcs.travel_blog.exception.ResourceNotFoundException;
import com.wcs.travel_blog.media.dto.MediaDTO;
import com.wcs.travel_blog.media.dto.CreateMediaDTO;
import com.wcs.travel_blog.media.dto.UpdateMediaDTO;
import com.wcs.travel_blog.media.service.MediaService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.wcs.travel_blog.media.model.MediaType.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Tests d'intégration Web MVC pour Media
@WebMvcTest(MediaController.class)
@AutoConfigureMockMvc(addFilters = false)
class MediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MediaService mediaService;

    @Autowired
    private ObjectMapper objectMapper;

    private MediaDTO media1() {
        MediaDTO media = new MediaDTO();
        media.setId(1L);
        media.setFileUrl("https://cdn/img1.jpg");
        media.setMediaType(PHOTO);
        media.setStepId(10L);
        return media;
    }

    private MediaDTO media2() {
        MediaDTO media = new MediaDTO();
        media.setId(2L);
        media.setFileUrl("https://cdn/vid1.mp4");
        media.setMediaType(VIDEO);
        media.setStepId(10L);
        return media;
    }

    @Test
    void getAllMedia_shouldReturn200WithList() throws Exception {
        when(mediaService.getAllMedias()).thenReturn(List.of(media1(), media2()));

        mockMvc.perform(get("/medias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].mediaType").value(PHOTO.name()))
                .andExpect(jsonPath("$[1].mediaType").value(VIDEO.name()));
    }

    @Test
    void getMediaById_shouldReturnExistingMedia() throws Exception {
        when(mediaService.getMediaById(1L)).thenReturn(media1());

        mockMvc.perform(get("/medias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileUrl").value("https://cdn/img1.jpg"));
    }

    @Test
    void getMediaById_shouldReturn404WhenNotFound() throws Exception {
        when(mediaService.getMediaById(99L)).thenThrow(new ResourceNotFoundException("Media non trouvé"));

        mockMvc.perform(get("/medias/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getMediaByStep_shouldReturnListForStep() throws Exception {
        when(mediaService.getMediaByStep(10L)).thenReturn(List.of(media1(), media2()));

        mockMvc.perform(get("/medias/step/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].stepId").value(10));
    }

    @Test
    void createMedia_shouldReturnCreatedMedia() throws Exception {
        CreateMediaDTO createMediaDTO = new CreateMediaDTO();
        createMediaDTO.setFileUrl("https://cdn/new.jpg");
        createMediaDTO.setMediaType(PHOTO);
        createMediaDTO.setStepId(11L);

        MediaDTO mediaDTO = new MediaDTO();
        mediaDTO.setId(3L);
        mediaDTO.setFileUrl("https://cdn/new.jpg");
        mediaDTO.setMediaType(PHOTO);
        mediaDTO.setStepId(11L);

        when(mediaService.createMedia(Mockito.any(CreateMediaDTO.class))).thenReturn(mediaDTO);

        mockMvc.perform(post("/medias")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createMediaDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3));
    }

    @Test
    void updateMedia_shouldReturnUpdatedMedia() throws Exception {
        UpdateMediaDTO updateMediaDTO = new UpdateMediaDTO();
        updateMediaDTO.setFileUrl("https://cdn/updated.jpg");
        updateMediaDTO.setMediaType(PHOTO);
        updateMediaDTO.setStepId(10L);

        MediaDTO mediaDTO = media1();
        mediaDTO.setFileUrl("https://cdn/updated.jpg");

        when(mediaService.updateMedia(Mockito.eq(1L), Mockito.any(UpdateMediaDTO.class))).thenReturn(mediaDTO);

        mockMvc.perform(put("/medias/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMediaDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileUrl").value("https://cdn/updated.jpg"));
    }

    @Test
    void deleteMedia_shouldReturnOkWithMessage() throws Exception {
        mockMvc.perform(delete("/medias/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Média supprimé avec succès"));

        Mockito.verify(mediaService).deleteMediaById(1L);
    }
}
