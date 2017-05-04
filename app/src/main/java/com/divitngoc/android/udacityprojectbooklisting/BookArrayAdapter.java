package com.divitngoc.android.udacityprojectbooklisting;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DxAlchemistv1 on 02/05/2017.
 */

public class BookArrayAdapter extends ArrayAdapter<Book> {

    public BookArrayAdapter(Context context, List<Book> objects) {
        super(context, 0, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        //Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_listing_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView author = (TextView) listItemView.findViewById(R.id.author);
        author.setText(getContext().getString(R.string.by) + " " + currentBook.getAuthor());

        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(currentBook.getTitle());

        TextView publishedDate = (TextView) listItemView.findViewById(R.id.description);
        publishedDate.setText(currentBook.getDescription());

        return listItemView;
    }
}
