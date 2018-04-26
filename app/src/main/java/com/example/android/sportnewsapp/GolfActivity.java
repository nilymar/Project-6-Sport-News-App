package com.example.android.sportnewsapp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

// this activity create and display list of article from section sport/golf in Guardian api
public class GolfActivity extends AppCompatActivity implements LoaderCallbacks<List<Article>> {
    // Adapter for the list of news articles
    private ArticlesAdapter mAdapter;
    // Tag for the log messages
    public static final String LOG_TAG = GolfActivity.class.getName();
    // URL to query the Guardian api
    private static final String GUARDIAN_GOLF_REQUEST_URL =
            "http://content.guardianapis.com/sport/golf?show-tags=contributor&api-key=test";
    // Constant value for the article loader ID
    private static final int ARTICLE_LOADER_ID = 5;
    // Binding the views from the layout file (article_activity.xml) using ButterKnife
    @BindView(R.id.empty_view)
    TextView emptyView;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.loading_spinner)
    ProgressBar progressBar;
    @BindView(R.id.current_image)
    ImageView currentImage;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mySwipeRefreshLayout;
    // loadManager to be used in this activity
    private LoaderManager loaderManager;
    private static final String SHARED_PREFERENCES = "newsappshared"; // name for sharedPreferences location

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.articles_activity);
        ButterKnife.bind(this);// actual binding of the Views using ButterKnife
        // checking which drawable image to use - different for portrait and landscape modes
        Display mDisplay= this.getWindowManager().getDefaultDisplay();
        if(mDisplay.getRotation()== Surface.ROTATION_0 || mDisplay.getRotation()==Surface.ROTATION_180) {//portrait
        currentImage.setImageResource(R.drawable.golf2); //set your portrait drawable
        } else {//Landscape
        currentImage.setImageResource(R.drawable.golf21); //set your landscape drawable
        }
        // set a new adapter for the list
        mAdapter = new ArticlesAdapter(this, new ArrayList<Article>());
        listView.setEmptyView(emptyView);
        // set the adapter on the listView
        listView.setAdapter(mAdapter);
        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();
        // set the onItemClick actions for the listView (i.e. opening the article in a browser)
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current article that was clicked on
                Article currentArticle = mAdapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri ArticleUri = Uri.parse(currentArticle.getUrl());
                // Create a new intent to view the Article URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, ArticleUri);
                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
        // Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
        // performs a swipe-to-refresh gesture.
        mySwipeRefreshLayout.setRefreshing(false);
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() { // this create a new list of articles when you refresh
                        refreshData(); // using the method to reload data from the internet
                    }
                }
        );
        requestOperation(); // requesting data from the Guardian api
    }

    // method for re-loading the data from the internet
    private void refreshData() {
        loaderManager.destroyLoader(ARTICLE_LOADER_ID); // so that the list will re-create
        mAdapter.clear(); // clearing current list from the adapter
        requestOperation(); // requesting data from the Guardian api
        mySwipeRefreshLayout.setRefreshing(false); // make sure the refresh spinner disappears when using with swipeRefresh
    }

    // this method checks if the internet is on - if so - starts the loader
    private void requestOperation() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        if (!isConnected) { // if there is no internet connection - don't show progress bar and set
            // the no_internet_connection message
            progressBar.setVisibility(View.GONE);
            emptyView.setText(R.string.no_internet_connection);
        } else {
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(ARTICLE_LOADER_ID, null, this);
        }
    }

    // the loader constructor - creates the uri according to user preferences
    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));
        String articleNumber = restorePreferences(getString(R.string.settings_articles_number_key));
        if (articleNumber.isEmpty())
            articleNumber = getString(R.string.settings_articles_number_default);
        String dateString = restorePreferences(getString(R.string.settings_start_date_key));
        String startDate;
        SimpleDateFormat format1 = new SimpleDateFormat(getString(R.string.xml_date_format));
        SimpleDateFormat format2 = new SimpleDateFormat(getString(R.string.query_date_format));
        if (dateString.isEmpty()) {
            dateString = getString(R.string.settings_start_date_default);
            Date date = format1.parse(dateString, new ParsePosition(0));
            startDate = format2.format(date);
        } else {
            Date date = format1.parse(dateString, new ParsePosition(0));
            startDate = format2.format(date);
        }
        Uri baseUri = Uri.parse(GUARDIAN_GOLF_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter(getString(R.string.query_order_by), orderBy);
        uriBuilder.appendQueryParameter(getString(R.string.query_article_number), articleNumber);
        uriBuilder.appendQueryParameter(getString(R.string.query_start_date), startDate);
        return new ArticleLoader(this, uriBuilder.toString());
    }

    // This method to restore the custom preferences data
    public String restorePreferences(String key) {
        SharedPreferences myPreferences = getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE);
        if (myPreferences.contains(key))
            return myPreferences.getString(key, "");
        else return "";
    }

    // what happens when the loading finished
    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> articles) {
        // Clear the adapter of previous articles
        mAdapter.clear();
        // after loading is over - don't show the progress indicator
        progressBar.setVisibility(View.GONE);
        // If there is a valid list of {@link Article}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll(articles);
        } else {
            emptyView.setText(R.string.no_data_available);
        }
    }

    // what to do on loader reset
    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        mAdapter.clear();
    }
}


