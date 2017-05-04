package com.divitngoc.android.udacityprojectbooklisting;

/**
 * Created by DxAlchemistv1 on 02/05/2017.
 */

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving book data from googleapis.
 */
public class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //keys value for JSONObject and JSONArray
    private static final String ITEMS_KEY = "items";
    private static final String VOLUMEINFO_KEY = "volumeInfo";
    private static final String TITLE_KEY = "title";
    private static final String DESCRIPTION_KEY = "description";
    private static final String AUTHORS = "authors";

    private QueryUtils(){
        //prevent an instance of this class being created
    }

    /**
     * Query the googleapis dataset and return a list of {@link Book} objects.
     */
    public static List<Book> fetchBookdata(String requestUrl) {
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Book> bookList = extractItemsFromJson(jsonResponse);
        return bookList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        try {
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setReadTimeout(5000);
            httpURLConnection.setConnectTimeout(5500);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.connect();

            if(httpURLConnection.getResponseCode() == 200) {
                inputStream = httpURLConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + httpURLConnection.getResponseCode());
            }

        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem with retrieving from " + url);
        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }

        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Extracts from the json "items" object
     * @param bookJson takes in a json response
     * @return List<Book>
     */
    private static List<Book> extractItemsFromJson(String bookJson) {
        if (TextUtils.isEmpty(bookJson)) {
            return null;
        }

        List<Book> listBook = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(bookJson);
            if (!baseJsonResponse.has(ITEMS_KEY)) {
                return null;
            }

            JSONArray itemArrayResponse = baseJsonResponse.getJSONArray(ITEMS_KEY);
            for(int i = 0; i < itemArrayResponse.length(); i++) {

                JSONObject bookResponse = itemArrayResponse.getJSONObject(i);
                JSONObject volumeInfoResponse = bookResponse.getJSONObject(VOLUMEINFO_KEY);

                String title = "No title found.";
                if (volumeInfoResponse.has(TITLE_KEY)) {
                    title = volumeInfoResponse.getString(TITLE_KEY);
                }

                String description = "No description found.";
                if (volumeInfoResponse.has(DESCRIPTION_KEY)) {
                    description = volumeInfoResponse.getString(DESCRIPTION_KEY);
                }

                String authors = "";
                if (volumeInfoResponse.has(AUTHORS)) {
                    JSONArray authorResponse = volumeInfoResponse.getJSONArray(AUTHORS);

                    //get first author to add , after each author
                    authors = authorResponse.getString(0);
                    for(int k = 1; k < authorResponse.length(); k++) {
                        authors += ", " + authorResponse.getString(k);
                    }
                }

                Book book = new Book(authors, title, description);
                listBook.add(book);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }

        return listBook;
    }

}
