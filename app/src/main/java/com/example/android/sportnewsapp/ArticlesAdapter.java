package com.example.android.sportnewsapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ArticlesAdapter extends ArrayAdapter<Article> {
    // Binding the views from the layout file (list_item.xml) using ButterKnife
    @BindView(R.id.title_of_article)
    TextView titleView;
    @BindView(R.id.date_of_article)
    TextView dateView;
    @BindView(R.id.author_of_article)
    TextView authorView;
    @BindView(R.id.section_of_article)
    TextView sectionView;

    // this is the constructor for the Article object adapter
    public ArticlesAdapter(Activity context, ArrayList<Article> articles) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter the adapter is not going to use this second argument,
        // so it can be any value. Here, we used 0.
        super(context, 0, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        ButterKnife.bind(this, convertView); // actual view binding using ButterKnife
        // get the Article object in the current position in the array
        final Article currentArticle = getItem(position);
        // set the title string
        String titleOfArticle = currentArticle.getTitle();
        titleView.setText(titleOfArticle);
        // set the author string
        String authorOfArticle = currentArticle.getAuthor();
        if (!authorOfArticle.isEmpty()) authorView.setText(authorOfArticle);
        else authorView.setText(getContext().getResources().getString(R.string.no_author));
        // set the section string
        String sectionOfArticle = currentArticle.getSection();
        sectionView.setText(sectionOfArticle);
        // set the date and time of article on screen
        String dateOfArticle = currentArticle.getDate();
        long dateTime = getCreatedAt(dateOfArticle);
        Date dateObject = new Date(dateTime);
        // the format of your date and time on screen
        String formattedDate = formatDate(dateObject);
        String formattedTime = formatTime(dateObject);
        String formattedDateTime = formattedDate + " " + formattedTime;
        dateView.setText(formattedDateTime);
        return convertView;
    }

    // Return the formatted date string (i.e. "Jan 14, 1978") from a Date object.
    private String formatDate(Date dateObject) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    // Return the formatted time string (i.e. "4:30 PM") from a Date object.
    private String formatTime(Date dateObject) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

    // create a date format from the string of dateTime json
    public long getCreatedAt(String createdAt){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date createDate = null;
        try {
            createDate = formatter.parse(createdAt);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return createDate.getTime();
    }
}
