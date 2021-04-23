package com.example.lrfinalproject.databases;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XmlDocParse {

  public final ArrayList<Article> ARTICLES;
  public ArrayList<Article> bruteForceSearchResults;
  public ArrayList<Article> luceneSearchResults;
  private Lucene lucene;


  public XmlDocParse() throws IOException {
    this.ARTICLES = new ArrayList<>();
    this.bruteForceSearchResults = new ArrayList<>();
    this.luceneSearchResults = new ArrayList<>();
    this.lucene = new Lucene("src/main/resources/luceneIndex");

  }

  public ArrayList<Article> getBruteForceSearchResults() {
    return bruteForceSearchResults;
  }


  public void addArticles() throws IOException, ParseException, SQLException {
    addArticlesToLucene();
  }


  public ArrayList<Article> getLuceneSearchResults() {
    return luceneSearchResults;
  }

  private void addArticlesToLucene() throws IOException {

    for (Article article : ARTICLES) {
      lucene.addDoc(article);
    }
    System.out.println("Added " + ARTICLES.size() + " to lucene");
    lucene.writer.close();
  }

  private void parse(File inputFile)
      throws ParserConfigurationException, IOException, SAXException {

    String tagName = "PubmedArticle";

    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    Document document = documentBuilder.parse(inputFile);
    NodeList nodeList = document.getElementsByTagName(tagName);

    for (int i = 0; i < nodeList.getLength(); i++) {

      // get the node
      Node node = nodeList.item(i);
      // cast to element to get the tags
      Element elem = (Element) node;
      // find and print the title
      NodeList title = elem.getElementsByTagName("ArticleTitle");
      String articleTitle = title.item(0).getTextContent();

      String articleYear = "";
      NodeList year = elem.getElementsByTagName("ArticleDate");
      Element dateParts = (Element) year.item(0);
      if (dateParts != null) {
        year = dateParts.getChildNodes();
        if (year.getLength() > 0) {
          // pull out the year from XML
          StringBuilder builder = new StringBuilder();
          String data = year.item(1).getTextContent();
          builder.append(data);
          // remove spaces from the date
          String cleanYear = builder.toString().replace("  ", "");
          cleanYear = cleanYear.replaceAll("[\\n\\t ]", "");
          articleYear = cleanYear;
        }
        // add to the list of articles
        ARTICLES.add(new Article(articleTitle, articleYear));
      }
    }
  }

  public void parseDirectory(String inputFiles)
      throws IOException, SAXException, ParserConfigurationException {

    File[] files = new File(inputFiles).listFiles();

    assert files != null;
    for (File file : files) {
      String rootFileExtension = file.getPath()
          .substring(file.getPath().length() - 4, file.getPath().length());
      // if it is XML, then add
      if (rootFileExtension.equals(".xml")) {
        parse(file);
        //  System.out.println("Added Doc");
      }
    }
  }

  public ArrayList<Article> search(String database, String searchTerm, String startYear,
      String endYear) throws ParseException, SQLException, IOException {

    ArrayList<Article> result = new ArrayList<>();

    // clear out the old search results
    bruteForceSearchResults.clear();
    luceneSearchResults.clear();

    // run the searches

    // bruteforce
    // search
    bruteForceSearch(searchTerm, startYear, endYear);

    //lucene
    // search
    luceneSearchResults = lucene.search(searchTerm, startYear, endYear);

    switch (database) {
      case "bruteforce":
        System.out.println("Bruteforce Results: " + bruteForceSearchResults.size());
        result = bruteForceSearchResults;
        break;
      case "lucene":
        System.out.println("Lucene Results: " + luceneSearchResults.size());
        result = luceneSearchResults;
        break;
    }
    return result;
  }

  private void bruteForceSearch(String searchTerm, String fromDate, String toDate) {
    if (ARTICLES.size() > 0) {
      searchTerm = searchTerm.toLowerCase();
      for (Article article : ARTICLES) {
        String articleTitle = article.articleTitle.toLowerCase();

        if (articleTitle.contains(searchTerm)) {

          int from = Integer.parseInt(fromDate);
          int to = Integer.parseInt(toDate);

          int articleDate = Integer.parseInt(article.articleYear);
          if (articleDate >= from && articleDate <= to) {
            bruteForceSearchResults.add(article);
          }
        }
      }
    } else {
      System.out.println("No items to search");
    }
  }

  public void printSearchResults(ArrayList<Article> results) {
    for (Article article : results) {
      System.out
          .println("Title: " + article.getArticleTitle() + "\nYear: " + article.getArticleYear());
    }
  }
}