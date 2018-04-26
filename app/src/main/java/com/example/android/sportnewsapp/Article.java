package com.example.android.sportnewsapp;

public class Article {
    /** title of article */
    private String mTitle;
    /** date of article */
    private String mDate;
    /** author of article */
    private String mAuthor;
    /* section of the article */
    private String mSection;
    /* url of article */
    private String mUrl;

    /**
     * Create a new Article object.
     * @param title
     *
     * @param date
     *
     * @param author
     *
     * @param section
     *
     * @param url
     */
    public Article(String title, String date, String author, String section, String url) {
        mTitle = title;
        mDate = date;
        mAuthor = author;
        mSection = section;
        mUrl = url;
    }

    // Get the title of article
    public String getTitle() {
        return mTitle;
    }

    // Get the date of the article
    public String getDate() {
        return mDate;
    }

    // Get the author of the article
    public String getAuthor() {
        return mAuthor;
    }

    // Get the section of the article
    public String getSection() { return mSection; }

    // Get the url of the article
    public String getUrl() { return mUrl; }
}

