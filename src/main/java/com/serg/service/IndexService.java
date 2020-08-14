package com.serg.service;

import com.serg.exception.IndexProcessingException;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Paths;

@Service
public class IndexService {
    private final Logger log = LoggerFactory.getLogger(HtmlIndexer.class);
    private final String indexDirectoryPath;

    public IndexService(@Value("${index.directory.path}") String indexDirectoryPath) {
        this.indexDirectoryPath = indexDirectoryPath;
    }

    public void indexDocument(String url, Integer depth) {
        try (IndexWriter indexWriter = createWriter();
             HtmlIndexer htmlIndexer = new HtmlIndexer(indexWriter)) {
            htmlIndexer.startIndexing(url, depth);
        } catch (IOException e) {
            log.error("Error during indexing by url {}, {}", url, e.toString());
            throw new IndexProcessingException(e);
        }
    }

    private IndexWriter createWriter() throws IOException {
        Directory indexDirectory = FSDirectory.open(Paths.get(indexDirectoryPath));
        StandardAnalyzer standardAnalyzer = new StandardAnalyzer();
        IndexWriterConfig config = new IndexWriterConfig(standardAnalyzer);
        return new IndexWriter(indexDirectory, config);
    }
}
