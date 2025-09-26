package com.wcs.travel_blog.search.controller;

import com.wcs.travel_blog.search.dto.SearchResponseDTO;
import com.wcs.travel_blog.search.service.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

@RestController
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * Expose la recherche globale pour le front-office.
     * @param query chaîne saisie dans la barre de recherche (obligatoire)
     * @return carnets et étapes correspondant au texte recherché
     */
    @GetMapping
    public ResponseEntity<SearchResponseDTO> search(@RequestParam(name = "query") String query) {
        if (!StringUtils.hasText(query)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "query parameter must not be empty");
        }

        SearchResponseDTO response = searchService.search(query);
        return ResponseEntity.ok(response);
    }
}
