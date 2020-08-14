package com.serg.service;

import com.serg.service.fakeurlconnection.HttpUrlStreamHandler;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.serg.service.DocumentField.*;
import static org.apache.lucene.document.Field.Store.YES;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlSearcherTest {

    private static final String TEST_URL = "https://my.test.com/index/index.html";
    private Directory dir;
    private IndexWriter directoryWriter;
    private HtmlSearcher htmlSearcher;

    @BeforeEach
    public void setupIndex() throws IOException {
        dir = new ByteBuffersDirectory();
        directoryWriter = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));
        addDocument("https://www.ukr.net/", "title one", "how now brown cow woc");
        addDocument("https://learning.ua/", "title two", "won woh nworb");
        addDocument("https://www.lampa.ua/", "title three", "woc");
        IndexReader reader = DirectoryReader.open(dir);
        htmlSearcher = new HtmlSearcher(new IndexSearcher(reader));
    }

    private Document addDocument(String url, String title, String content) throws IOException {
        Document document = new Document();
        document.add(new StringField(DocumentField.URL.getName(), url, YES));
        document.add(new TextField(DocumentField.TITLE.getName(), title, YES));
        document.add(new TextField(DocumentField.CONTENTS.getName(), content, YES));
        directoryWriter.addDocument(document);
        directoryWriter.commit();
        return document;
    }

    @BeforeEach
    public void setupUrlResponse() throws IOException {
        HttpUrlStreamHandler httpUrlStreamHandler = HttpUrlStreamHandler.getInstants();
        httpUrlStreamHandler.resetConnections();
        httpUrlStreamHandler.addFakeResponse(TEST_URL, "index/index.html");
        httpUrlStreamHandler.addFakeResponse("https://my.test.com/test-link", "index/test_link.html");
    }

    @Test
    public void shouldFindAllDocumentsOnFirstPageIfResultIsLessThenPageSize() throws IOException, ParseException {
        List<Document> documents = htmlSearcher.getPageByContent("woc", 0, 10);

        assertEquals("https://www.lampa.ua/", documents.get(0).get(URL.getName()));
        assertEquals("title three", documents.get(0).get(TITLE.getName()));
        assertEquals("woc", documents.get(0).get(CONTENTS.getName()));
        assertEquals("https://www.ukr.net/", documents.get(1).get(URL.getName()));
        assertEquals("title one", documents.get(1).get(TITLE.getName()));
        assertEquals("how now brown cow woc", documents.get(1).get(CONTENTS.getName()));
    }

    @Test
    public void shouldFindDocumentsIfResultIsMoreThenPageSize() throws IOException, ParseException {
        List<Document> documents = htmlSearcher.getPageByContent("woc", 0, 1);

        assertThat(documents, hasSize(1));
        assertEquals("https://www.lampa.ua/", documents.get(0).get(URL.getName()));
        assertEquals("title three", documents.get(0).get(TITLE.getName()));
        assertEquals("woc", documents.get(0).get(CONTENTS.getName()));
    }

    @Test
    public void shouldNotFindDocumentsIfFromParamsIsSpecifiedMoreThanContainsResult() throws IOException, ParseException {
        List<Document> documents = htmlSearcher.getPageByContent("woc", 3, 1);

        assertThat(documents, empty());
    }
}