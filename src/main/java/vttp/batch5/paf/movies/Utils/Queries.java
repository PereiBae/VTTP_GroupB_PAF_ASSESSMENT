package vttp.batch5.paf.movies.Utils;

public class Queries {

    public static final String BATCH_INSERT = "INSERT INTO imdb(imdb_id, vote_average, vote_count, release_date, revenue, budget, runtime) VALUES (?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_REVENUE_BUDGET = "SELECT imdb_id, revenue, budget FROM imdb WHERE imdb_id = ?";

    public static final String FINANCE_QUERY = "SELECT revenue, budget FROM imdb WHERE imdb_id = ?";

}
