package com.example.lrfinalproject;

import com.example.lrfinalproject.databases.Article;

import java.io.IOException;
import java.util.ArrayList;

public class Query {

    private final String type;
    private final String startYear;
    private final String endYear;
    private final String searchTerm;
    private final ArrayList<Article> results;

    public Query(String type, String startYear,
                 String endYear, String searchTerm, ArrayList<Article> results) throws IOException {
        this.type = type;
        this.startYear = startYear;
        this.endYear = endYear;
        this.searchTerm = searchTerm;
        this.results = results;
    }

    public void setResults() {

    }

    public ArrayList<Article> getResults() {
        return results;
    }
}
