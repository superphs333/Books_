package com.remon.books.Data;

public class Data_Search_Book {
    String unique_book_value, title, authors, publisher,thumbnail, contents, from_,isbn;


    // 생성자
    public Data_Search_Book(){}

    // 생성자
    public Data_Search_Book(String unique_book_value, String title, String authors
            , String publisher, String thumbnail, String contents, String isbn)
    {
        this.unique_book_value = unique_book_value;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
        this.contents = contents;
        this.isbn = isbn;
    }

    public String getUnique_book_value() {
        return unique_book_value;
    }

    public void setUnique_book_value(String unique_book_value) {
        this.unique_book_value = unique_book_value;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getFrom_() {
        return from_;
    }

    public void setFrom_(String from_) {
        this.from_ = from_;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}
