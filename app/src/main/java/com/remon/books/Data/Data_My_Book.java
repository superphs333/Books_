package com.remon.books.Data;

public class Data_My_Book {
    String unique_book_value, title, authors, publisher,thumbnail, contents, from_ ,isbn,review;
    int status;
    float rating;

    // 생성자
    public Data_My_Book(){}

    // 생성자
    public Data_My_Book(String unique_book_value, String title, String authors
            , String publisher, String thumbnail, String contents
            , String from_, String isbn, int status, float rating,String review)
    {
        this.unique_book_value = unique_book_value;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.thumbnail = thumbnail;
        this.contents = contents;
        this.from_ = from_;
        this.isbn = isbn;
        this.status = status;
        this.rating = rating;
        this.review = review;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
