package com.example.android.sportnewsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

public class ArticleLoader extends AsyncTaskLoader<List<Article>> {
    // Tag for log messages
    private static final String LOG_TAG = ArticleLoader.class.getName();
    // Query URL
    private String mUrl;
    // the List variable for Article objects
    private List<Article> articles;

    /**
     * Constructs a new {@link ArticleLoader}.
     * @param context of the activity
     * @param url     to load data from
     */
    public ArticleLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        if (articles != null) {// if you have articles in the list - use that list
            // Use cached data
            deliverResult(articles);
        }
        else  {
            forceLoad();
        }
    }

    // This is on a background thread.
    @Override
    public List<Article> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        articles = QueryUtils.fetchArticles(mUrl);
        return articles;
    }

    @Override
    public void deliverResult(List<Article> data) {
        // We’ll save the data for later retrieval
        articles = data;
        super.deliverResult(data);
    }

}
