package vttp.batch5.paf.movies.bootstrap;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import vttp.batch5.paf.movies.models.Movie;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;
import vttp.batch5.paf.movies.services.MovieService;

import java.io.*;
import java.text.ParseException;
import java.util.List;
import java.util.logging.Logger;

@Component
public class Dataloader implements CommandLineRunner {

    @Value("${movies.post.zip}")
    private String zip;

    @Autowired
    private MongoMovieRepository mongoMovieRepo;

    @Autowired
    private MySQLMovieRepository mySQLMovieRepo;

    @Autowired
    private MovieService movieService;

    private final Logger logger = Logger.getLogger(Dataloader.class.getName());

    public Dataloader() throws ParseException {
    }

    //TODO: Task 2
    @Override
    public void run(String... args) throws Exception {
        logger.info(">>> In Dataloader...");

        // Load the zip file from resources
        File zipFile = new ClassPathResource(zip).getFile();

        try{
            logger.info("Loading movies from database...");
            movieService.extractZip(zipFile.getAbsolutePath(),"/Users/brandonpereira/Code/PAF/paf_b5_assessment_template/movies/src/main/resources");
            logger.info("Files Extracted successfully!");
        } catch (Exception e){
            e.printStackTrace();
        }

        File jsonFile = new ClassPathResource("movies_post_2010.json").getFile();
        List<Movie> movies = movieService.processJsonFile(jsonFile);
        logger.info("Movies: " + movies.size());

        mySQLMovieRepo.batchInsertMovies(movies);
        logger.info("Movies have been inserted into MySQL!");

        mongoMovieRepo.batchInsertMovies(movies);
        logger.info("Movies have been inserted into Mongo!");

    }



}
