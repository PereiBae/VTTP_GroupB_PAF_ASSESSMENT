package vttp.batch5.paf.movies.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "imdb")
public class MongoMovie {

    @Id
    private String imdb_id;
    private String title;
    private String directors;
    private String overview;
    private String tagline;
    private String genres;
    private int imdb_rating;
    private int imdb_votes;

    public String getImdb_id() {
        return imdb_id;
    }

    public void setImdb_id(String imdb_id) {
        this.imdb_id = imdb_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirectors() {
        return directors;
    }

    public void setDirectors(String directors) {
        this.directors = directors;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }

    public int getImdb_rating() {
        return imdb_rating;
    }

    public void setImdb_rating(int imdb_rating) {
        this.imdb_rating = imdb_rating;
    }

    public int getImdb_votes() {
        return imdb_votes;
    }

    public void setImdb_votes(int imdb_votes) {
        this.imdb_votes = imdb_votes;
    }
}
