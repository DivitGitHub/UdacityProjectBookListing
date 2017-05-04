package com.divitngoc.android.udacityprojectbooklisting;

/**
 * Created by DxAlchemistv1 on 02/05/2017.
 */

public class Book {
    private String author;
    private String title;
    private String description;

    public Book(String author, String title, String description) {
        this.author = author;
        this.title = title;
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "Book{" +
                "author='" + author + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
