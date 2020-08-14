package com.serg.service;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HtmlSearcher {

    private IndexSearcher searcher;

    public HtmlSearcher(IndexSearcher searcher) {
        this.searcher = searcher;
    }

    public List<Document> getPageByContent(String content, int from, int size) throws IOException, ParseException {
        List<Document> documents = new ArrayList<>();
        QueryParser parser = new QueryParser(DocumentField.CONTENTS.getName(), new StandardAnalyzer());
        Query query = parser.parse(content);
        TopDocs hits = searcher.search(query, from + size);
        long end = Math.min(hits.totalHits.value, size);
        for (int i = from; i < end; i++) {
            int docId = hits.scoreDocs[i].doc;

            Document doc = searcher.doc(docId);
            documents.add(doc);
        }
        return documents;
    }
}
