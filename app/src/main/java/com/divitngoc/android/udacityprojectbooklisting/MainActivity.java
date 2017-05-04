package com.divitngoc.android.udacityprojectbooklisting;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final int BOOK_LOADER_ID = 1;

    private TextView mEmptyStateTextView;

    //base URL
    private static final String BASE_URL = " https://www.googleapis.com/books/v1/volumes?q=";

    //max results 15 per query
    private static final String END_URL = "&maxResults=15";

    private String userQuery = "";

    private View loadingIndicator;

    private BookArrayAdapter bookArrayAdapter;

    private LoaderManager loaderManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView bookListView = (ListView) findViewById(R.id.listview_books);

        //set different empty text view depending if internet is connected when starting up
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        if (isConnected()) {
            mEmptyStateTextView.setText(getString(R.string.starting_view));
        } else {
            mEmptyStateTextView.setText(getString(R.string.no_internet_connection));
            Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        }


        bookListView.setEmptyView(mEmptyStateTextView);

        // Create a new adapter that takes an empty list of books as input
        bookArrayAdapter = new BookArrayAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(bookArrayAdapter);

        //Expanding and collapsing "description" listview
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                TextView descriptionText = (TextView) view.findViewById(R.id.description);

                if(descriptionText.getLineCount() == 2) {
                    descriptionText.setMaxLines(Integer.MAX_VALUE);
                } else {
                    descriptionText.setMaxLines(2);
                }
            }
        });

        //to load the data after activity was destroyed
        loaderManager = getSupportLoaderManager();
        if (loaderManager.getLoader(BOOK_LOADER_ID) != null) {
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        }
    }

    /*
    Search for books
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem item = menu.findItem(R.id.search);

        SearchView menuSearch = (SearchView) item.getActionView();

        menuSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (!isConnected()) {
                    Toast.makeText(MainActivity.this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                    return false;
                }

                userQuery = BASE_URL + query.trim() + END_URL;

                //shows load the loading indicator after query is submitted
                loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                loaderManager = getSupportLoaderManager();
                loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    private boolean isConnected() {
        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo.isConnected();
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int id, Bundle args) {
        return new BookLoader(MainActivity.this, userQuery);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> data) {
        // Hide loading indicator because the data has been loaded
        loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_books);
        bookArrayAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (data != null && !data.isEmpty()) {
            bookArrayAdapter.addAll(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
       bookArrayAdapter.clear();
    }

}
