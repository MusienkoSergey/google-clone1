package com.serg.service;

import com.serg.service.fakeurlconnection.HttpUrlStreamHandler;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.serg.service.DocumentField.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HtmlIndexerTest {

    private static final String TEST_URL = "https://my.test.com/index/index.html";
    private Directory dir;
    private IndexReader reader;
    private HtmlIndexer indexer;

    @BeforeEach
    public void setupIndex() throws IOException {
        dir = new ByteBuffersDirectory();
        IndexWriter directoryWriter = new IndexWriter(dir, new IndexWriterConfig(new StandardAnalyzer()));
        indexer = new HtmlIndexer(directoryWriter);
        directoryWriter.commit();
    }

    @BeforeEach
    public void setupUrlResponse() throws IOException {
        HttpUrlStreamHandler httpUrlStreamHandler = HttpUrlStreamHandler.getInstants();
        httpUrlStreamHandler.resetConnections();
        httpUrlStreamHandler.addFakeResponse(TEST_URL, "index/index.html");
        httpUrlStreamHandler.addFakeResponse("https://my.test.com/test-link", "index/test_link.html");
    }

    @AfterEach
    public void closeStuff() throws IOException {
        reader.close();
        indexer.close();
        dir.close();
    }

    @Test
    public void shouldIndexDocumentByUrl() throws Exception {
        indexer.startIndexing(TEST_URL, 3);

        reader = DirectoryReader.open(dir);
        assertEquals(2, reader.maxDoc());
        assertIndexContainsAllData(reader);
    }

    @Test
    public void shouldNotThrownExceptionWhenIndexingDocumentByInvalidUrl() throws Exception {
        indexer.startIndexing("invalid-url", 3);

        reader = DirectoryReader.open(dir);
        assertEquals(0, reader.maxDoc());
    }

    @Test
    public void shouldNotThrownExceptionWhenIndexingDocumentByNonExistUrl() throws Exception {
        indexer.startIndexing("https://my.test.com/index/non-exist.html", 3);

        reader = DirectoryReader.open(dir);
        assertEquals(0, reader.maxDoc());
    }

    public static void assertIndexContainsAllData(IndexReader reader) throws Exception {
        IndexSearcher searcher = new IndexSearcher(reader);
        QueryParser qp = new QueryParser(CONTENTS.getName(), new StandardAnalyzer());
        Query urlQuery = qp.parse("Web");
        TopDocs topDocs = searcher.search(urlQuery, 100);
        List<Document> documents = Stream.of(topDocs.scoreDocs)
                .map(e -> getDocument(searcher, e))
                .collect(Collectors.toList());

        assertEquals("https://my.test.com/test-link", documents.get(0).get(URL.getName()));
        assertEquals("Web Test Link Content", documents.get(0).get(TITLE.getName()));
        assertEquals("Web Test Link Content TEST", documents.get(0).get(CONTENTS.getName()));
        assertEquals("https://my.test.com/index/index.html", documents.get(1).get(URL.getName()));
        assertEquals("Web Content", documents.get(1).get(TITLE.getName()));
        assertEquals("Web Content Test Link here Test Google Link and here", documents.get(1).get(CONTENTS.getName()));
    }

    private static Document getDocument(IndexSearcher searcher, ScoreDoc e) {
        try {
            return searcher.doc(e.doc);
        } catch (Exception ex) {
            return null;
        }
    }
}