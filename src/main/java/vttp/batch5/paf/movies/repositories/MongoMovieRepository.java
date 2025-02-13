package vttp.batch5.paf.movies.repositories;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import vttp.batch5.paf.movies.models.Errors;
import vttp.batch5.paf.movies.models.MongoMovie;
import vttp.batch5.paf.movies.models.Movie;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class MongoMovieRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public static final int batchSize = 25;

    // TODO: Task 2.3
    // You can add any number of parameters and return any type from the method
    // You can throw any checked exceptions from the method
    // Write the native Mongo query you implement in the method in the comments
    //
    //    db.movies.insertMany
    //
    public void batchInsertMovies(List<Movie> movies) {

        List<MongoMovie> mongoMovies = new ArrayList<>();

        for (Movie movie : movies) {
            MongoMovie mongoMovie = new MongoMovie();
            mongoMovie.setImdb_id(movie.getImdb_id());
            mongoMovie.setTitle(movie.getTitle());
            mongoMovie.setOverview(movie.getOverview());
            mongoMovie.setDirectors(movie.getDirector());
            mongoMovie.setGenres(movie.getGenres());
            mongoMovie.setTagline(movie.getTagline());
            mongoMovie.setImdb_rating(movie.getImdb_rating());
            mongoMovie.setImdb_votes(movie.getImdb_votes());
            mongoMovies.add(mongoMovie);
        }

        List<MongoMovie> batch = null;
        try {
            for (int i = 0; i < mongoMovies.size(); i += batchSize) {
                batch = mongoMovies.subList(i, Math.min(i + batchSize, movies.size()));
                mongoTemplate.insert(batch, "imdb");
            }
        } catch (Exception e) {
            assert batch != null;
            List<String> imdbIds = new ArrayList<>();
            for (MongoMovie mongoMovie : batch) {
                String id = mongoMovie.getImdb_id();
                imdbIds.add(id);
            }
            logError(e, imdbIds);
        }


    }

    // TODO: Task 2.4
    // You can add any number of parameters and return any type from the method
    // You can throw any checked exceptions from the method
    // Write the native Mongo query you implement in the method in the comments
    //
    //
    //
    public void logError(Exception e, List<String> imdbIds) {
        Query query = new Query(Criteria.where("message").is(e.getMessage()));

        Update update = new Update()
                .addToSet("imdbIds").each(imdbIds)  // Correctly adds values without array issues
                .set("timestamp", new Date(System.currentTimeMillis()));

        mongoTemplate.upsert(query, update, Errors.class);
    }

    // TODO: Task 3
    // Write the native Mongo query you implement in the method in the comments
    //
    //    db.imdb.aggregate([
    //    {
    //        $match: { directors: { $ne: "" } }
    //    },
    //    {
    //        $group: {
    //            _id: "$directors",
    //            count: { $sum: 1 },
    //            imdb_id: { $push: "$_id" }
    //        }
    //    },
    //    {
    //        $sort: { count: -1 }
    //    }
    //    ]);
    //
    public List<Document> getTopDir(int count){
        MatchOperation matchOperation = Aggregation.match(Criteria.where("directors").ne(""));
        GroupOperation groupOperation = Aggregation.group("directors").count().as("movies_count").push("_id").as("imdb_ids");
        SortOperation sortOperation = Aggregation.sort(Sort.Direction.DESC, "movies_count");
        LimitOperation limitOperation = Aggregation.limit(count);
        Aggregation aggregation = Aggregation.newAggregation(groupOperation, sortOperation, limitOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "imdb", Document.class);
        return results.getMappedResults();
    }

}
