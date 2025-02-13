package vttp.batch5.paf.movies.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import vttp.batch5.paf.movies.Utils.Queries;
import vttp.batch5.paf.movies.models.Movie;

import java.math.BigDecimal;
import java.util.*;

import static vttp.batch5.paf.movies.Utils.Queries.*;

@Repository
public class MySQLMovieRepository {

    @Autowired
    private MongoMovieRepository mongoMovieRepo;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static final int batchSize = 25;

    // TODO: Task 2.3
    // You can add any number of parameters and return any type from the method
    public void batchInsertMovies(List<Movie> movies) {
        List<Movie> batch = null;
        try {
            for (int i = 0; i < movies.size(); i += batchSize) {
                batch = movies.subList(i, Math.min(i + batchSize, movies.size()));
                jdbcTemplate.batchUpdate(BATCH_INSERT, batch, batch.size(), ((ps, movie) -> {
                    ps.setString(1, movie.getImdb_id());
                    ps.setFloat(2, movie.getVote_average());
                    ps.setInt(3, movie.getVote_count());
                    ps.setDate(4, movie.getRelease_date());
                    ps.setBigDecimal(5, movie.getRevenue());
                    ps.setBigDecimal(6, movie.getBudget());
                    ps.setInt(7, movie.getRuntime());
                }));
            }
        } catch (Exception e) {
            e.printStackTrace();

            List<String> failedImdbIds = new ArrayList<>();
            for (Movie movie : batch) {
                failedImdbIds.add(movie.getImdb_id());
            }

            mongoMovieRepo.logError(e, failedImdbIds);
        }
    }

    // TODO: Task 3
    public List<BigDecimal> getFinances(List<String> id) {
        List<BigDecimal> finances = null;
        for (String imdbId : id) {
            SqlRowSet rs = jdbcTemplate.queryForRowSet(FINANCE_QUERY, imdbId);
            finances = new ArrayList<>();
            while (rs.next()) {
                BigDecimal revenue = rs.getBigDecimal("revenue");
                BigDecimal budget = rs.getBigDecimal("budget");
                finances.add(revenue);
                finances.add(budget);
            }
        }


        return finances;
    }


}

