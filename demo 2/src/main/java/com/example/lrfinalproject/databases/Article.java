package com.example.lrfinalproject.databases;

public class Article {

    String articleTitle;
    String articleYear;

    public Article(String articleTitle, String articleYear) {
        this.articleTitle = articleTitle;
        this.articleYear = articleYear;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getArticleYear() {
        return articleYear;
    }
}
