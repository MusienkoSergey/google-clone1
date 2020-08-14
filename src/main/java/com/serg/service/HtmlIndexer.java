package com.serg.service;

import com.serg.model.HtmlInfo;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static org.apache.lucene.document.Field.Store.YES;

public class HtmlIndexer implements Closeable {

    private final Logger log = LoggerFactory.getLogger(HtmlIndexer.class);
    private final Set<String> urls = new HashSet<>();
    private final IndexWriter writer;

    public HtmlIndexer(IndexWriter indexWriter) {
        writer = indexWriter;
    }

    public void startIndexing(String url, Integer depthIndexing) {
        if (--depthIndexing < 0) {
            return;
        }
        if (!urls.add(url)) {
            return;
        }
        try {
            log.info("Start indexing by url: {}", url);
            HtmlInfo htmlInfo = HtmlParser.parse(url);
            indexDocument(url, htmlInfo.getTitle(), htmlInfo.getContent());
            log.info("End indexing by url: {}", url);

            Set<String> links = htmlInfo.getChildUrls();
            for (String link : links) {
                startIndexing(link, depthIndexing);
            }
        } catch (Exception e) {
            log.warn(e.toString());
        }
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }

    private void indexDocument(String url, String title, String contents) throws IOException {
        Document document = createDocument(url, title, contents);

        writer.addDocument(document);
        writer.commit();
    }

    private Document createDocument(String url, String title, String contents) {
        Document document = new Document();
        document.add(new StringField(DocumentField.URL.getName(), url, YES));
        document.add(new TextField(DocumentField.TITLE.getName(), title, YES));
        document.add(new TextField(DocumentField.CONTENTS.getName(), contents, YES));
        return document;
    }
}
