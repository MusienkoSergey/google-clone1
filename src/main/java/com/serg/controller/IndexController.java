package com.serg.controller;

import com.serg.service.IndexService;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api("Index API")
@RestController
@RequestMapping("api")
public class IndexController {

    private final IndexService indexService;

    public IndexController(IndexService indexService) {
        this.indexService = indexService;
    }

    @ApiOperation("Starting indexing process")
    @PostMapping("index")
    public ResponseEntity<Void> startIndexing(
            @ApiParam("Url for indexing") @RequestParam(value = "q") String url,
            @ApiParam("Depth of indexing") @RequestParam(value = "depth") Integer depthIndexing
    ) {
        indexService.indexDocument(url, depthIndexing);
        return ResponseEntity.accepted().build();
    }
}
