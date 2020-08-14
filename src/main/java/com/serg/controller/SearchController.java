package com.serg.controller;

import com.serg.model.DocumentInfo;
import com.serg.service.SearchService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api("Search API")
@RestController
@RequestMapping("api")
public class SearchController {
    private final SearchService searchDocuments;

    public SearchController(SearchService searchDocuments) {
        this.searchDocuments = searchDocuments;
    }

    @ApiOperation("Returns a list of indexed sites that contain the search text")
    @PostMapping(path = "search", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Page<DocumentInfo>> search(
            @PageableDefault Pageable page,
            @RequestParam(value = "q") String query
    ) {
        List<DocumentInfo> content = searchDocuments.searchDocuments(query, page);
        return ResponseEntity.ok(new PageImpl<>(content));
    }
}
