package com.example.lrfinalproject.databases;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public class Lucene {

  StandardAnalyzer analyzer;
  FSDirectory index;
  String indexLocation;
  IndexWriterConfig config;
  IndexWriter writer;


  public Lucene(String indexDirectory) throws IOException {
    // Specify the analyzer for tokenizing text.
    analyzer = new StandardAnalyzer();

    // Create the index
    indexLocation = indexDirectory;
    indexCleanup();
    index = FSDirectory.open(Paths.get(indexLocation));
    config = new IndexWriterConfig(analyzer);
    writer = new IndexWriter(index, config);
  }


  public ArrayList<Article> search(String searchTerm, String startDate, String endDate)
      throws IOException {
    ArrayList<Article> results = new ArrayList<>();

    // initialize the boolean query
    BooleanQuery.Builder query = new BooleanQuery.Builder();
    // must include the search term
    query.add(new TermQuery(new Term("title", searchTerm)), BooleanClause.Occur.MUST);

    // add the date range
    int start = Integer.parseInt(startDate);
    int end = Integer.parseInt(endDate);
    do {
      query.add(new TermQuery(new Term("year", Integer.toString(start))),
          BooleanClause.Occur.SHOULD);
      start++;
    } while (start <= end);
    // must include at least one of the dates in the range
    query.setMinimumNumberShouldMatch(0);

    // Search
    int hitsPerPage = 1000000000;
    IndexReader reader = DirectoryReader.open(index);
    IndexSearcher searcher = new IndexSearcher(reader);

    TopDocs docs = searcher.search(query.build(), hitsPerPage);

    // Add results
    ScoreDoc[] hits = docs.scoreDocs;

    for (int i = 0; i < hits.length; ++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      Article article = new Article(d.get("title"), d.get("year"));
      results.add(article);
    }
    reader.close();
    return results;
  }

  public void addDoc(Article article) throws IOException {

    Document doc = new Document();
    doc.add(new TextField("title", article.articleTitle, Field.Store.YES));
    doc.add(new TextField("year", article.articleYear, Field.Store.YES));
    writer.addDocument(doc);
  }

  public void indexCleanup() {
    File[] indexCleanup = new File(indexLocation).listFiles();
    // clean up the index for the next run
    assert indexCleanup != null;
    for (File file : indexCleanup) {
      file.delete();
    }
  }
}


