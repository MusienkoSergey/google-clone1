package com.serg.service;

import com.serg.exception.SearchException;
import com.serg.model.DocumentInfo;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.serg.service.DocumentField.*;

@Service
public class SearchService {
    private final Logger log = LoggerFactory.getLogger(SearchService.class);
    private final String indexDirectoryPath;

    public SearchService(@Value("${index.directory.path}") String indexDirectoryPath) {
        this.indexDirectoryPath = indexDirectoryPath;
    }

    public List<DocumentInfo> searchDocuments(String query, Pageable page) {
        try (Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath))) {
            HtmlSearcher searcher = new HtmlSearcher(createSearcher(indexDirectory));
            return searcher.getPageByContent(query, page.getPageNumber() * page.getPageSize(), page.getPageSize()).stream()
                    .map(this::composeDocumentInfo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error during searching by query {}, {}", query, e.toString());
            throw new SearchException(e);
        }
    }

    private DocumentInfo composeDocumentInfo(Document document) {
        DocumentInfo documentInfo = new DocumentInfo();
        documentInfo.setUrl(getValue(document, URL));
        documentInfo.setTitle(getValue(document, TITLE));
        documentInfo.setContents(getValue(document, CONTENTS));
        return documentInfo;
    }

    private String getValue(Document document, DocumentField field) {
        return document.get(field.getName());
    }

    private IndexSearcher createSearcher(Directory indexDirectory) throws IOException {
        IndexReader reader = DirectoryReader.open(indexDirectory);
        return new IndexSearcher(reader);
    }
}
