package com.example.lrfinalproject;

import com.example.lrfinalproject.databases.Article;
import com.example.lrfinalproject.databases.XmlDocParse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

@RestController
public class Controller {

    public XmlDocParse docParse;

    Controller() throws ParserConfigurationException, SAXException, IOException, ParseException, SQLException {
        String inputFiles = "src/main/resources/Data";
        this.docParse = new XmlDocParse();
        docParse.parseDirectory(inputFiles);
        docParse.addArticles();
    }

    // Example: http://localhost:8081/query?type=mongo&start=2018&end=2020&term=heart
    @CrossOrigin(origins = "http://localhost:8888")
    @GetMapping("/query")
    public Query query(@RequestParam(value = "type", defaultValue = "mongo") String type,
                       @RequestParam(value = "start", defaultValue = "1900") String startYear,
                       @RequestParam(value = "end", defaultValue = "2100") String endYear,
                       @RequestParam(value = "term", defaultValue = "cancer") String searchTerm
    ) throws ParseException, SQLException, IOException {
        searchTerm = searchTerm.replace("_", " ");
        ArrayList<Article> search = docParse.search(type, searchTerm, startYear, endYear);
        return new Query(type, startYear, endYear, searchTerm, search);
    }
}