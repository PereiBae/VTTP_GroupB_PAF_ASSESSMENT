package vttp.batch5.paf.movies.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.json.data.JsonDataSource;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vttp.batch5.paf.movies.models.Movie;
import vttp.batch5.paf.movies.repositories.MongoMovieRepository;
import vttp.batch5.paf.movies.repositories.MySQLMovieRepository;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static vttp.batch5.paf.movies.Utils.Utility.stringToDate;

@Service
public class MovieService {

    private static final Date cutOffDate;

    static {
        try {
            cutOffDate = stringToDate("2017-12-31");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private MongoMovieRepository mongoMovieRepo;

    @Autowired
    private MySQLMovieRepository mySQLMovieRepo;

    // TODO: Task 2
    public static void extractZip(String zipFilePath, String outputDir) throws IOException {
        byte[] buffer = new byte[1024];

        // Ensure output directory exists
        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(outputDir, zipEntry.getName());

                // Write file if it's not a directory
                if (!zipEntry.isDirectory()) {
                    try (FileOutputStream fos = new FileOutputStream(newFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                }
                zis.closeEntry();
            }
        } catch (IOException e) {
            System.out.println("Error while extracting zip file: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static List<Movie> processJsonFile(File jsonFile) {
        List<Movie> movies = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                Movie movie = objectMapper.readValue(line, Movie.class);

                // Validate & clean incorrect values
                if (movie.getTitle() == null || movie.getTitle().trim().isEmpty()) {
                    movie.setTitle("");
                }
                if (movie.getRuntime() < 0) {
                    movie.setRuntime(0);
                }
                if (movie.getStatus() == null || movie.getStatus().trim().isEmpty()) {
                    movie.setStatus("");
                }
                if (movie.getOriginal_language() == null || movie.getOriginal_language().trim().isEmpty()) {
                    movie.setOriginal_language("");
                }
                if (movie.getPoster_path() == null || movie.getPoster_path().trim().isEmpty()) {
                    movie.setPoster_path("");
                }
                if (movie.getOverview() == null || movie.getOverview().trim().isEmpty()) {
                    movie.setOverview("");
                }

                if (movie.getPopularity() < 0) {
                    movie.setPopularity(0);
                }
                if (movie.getTagline() == null || movie.getTagline().trim().isEmpty()) {
                    movie.setTagline("");
                }
                if (movie.getGenres() == null || movie.getGenres().trim().isEmpty()) {
                    movie.setGenres("");
                }
                if (movie.getSpoken_languages() == null || movie.getSpoken_languages().trim().isEmpty()) {
                    movie.setSpoken_languages("");
                }

                if (movie.getCasts() == null || movie.getCasts().trim().isEmpty()) {
                    movie.setCasts("");
                }

                if (movie.getDirector() == null || movie.getDirector().trim().isEmpty()) {
                    movie.setDirector("");
                }

                if (movie.getImdb_rating() < 0) {
                    movie.setImdb_rating(0);
                }

                if (movie.getImdb_votes() < 0) {
                    movie.setImdb_votes(0);
                }

                if (movie.getVote_average() < 0) {
                    movie.setVote_average(0);
                }

                if (movie.getVote_count() < 0) {
                    movie.setVote_count(0);
                }

                if (movie.getImdb_id() == null || movie.getImdb_id().trim().isEmpty() || movie.getImdb_id().length() > 16) {
                    continue;  // Skip invalid records if `imdb_id` is missing
                }

                if (movie.getRelease_date() == null) {
                    movie.setRelease_date(java.sql.Date.valueOf("1970-01-01"));
                }

                if (movie.getRelease_date().compareTo(cutOffDate) > 0) {
                    movies.add(movie);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return movies;
    }

    // TODO: Task 3
    // You may change the signature of this method by passing any number of parameters
    // and returning any type
    public JsonArray getProlificDirectors(int count) {

        // Get the top directors from MongoDB
        List<Document> topDir = mongoMovieRepo.getTopDir(count);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        // Iterate through each director's document
        for (Document d : topDir) {
            String directorName = d.getString("_id"); // Director's name
            int moviesCount = d.getInteger("movies_count"); // Number of movies
            List<String> imdbIds = d.getList("imdb_ids", String.class); // List of IMDb IDs

            // Fetch financial data from MySQL
            List<BigDecimal> finances = mySQLMovieRepo.getFinances(imdbIds);

            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalBudget = BigDecimal.ZERO;

            if (finances.size() >= 2) {
                totalRevenue = finances.get(0); // Revenue
                totalBudget = finances.get(1); // Budget
            }

            // Construct JSON object
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("director_name", directorName)
                    .add("movies_count", moviesCount)
                    .add("imdb_ids", Json.createArrayBuilder(imdbIds)) // Ensure array format
                    .add("total_revenue", totalRevenue != null ? totalRevenue.toString() : "0")
                    .add("total_budget", totalBudget != null ? totalBudget.toString() : "0")
                    .build();

            jsonArrayBuilder.add(jsonObject);
        }

        return jsonArrayBuilder.build();
    }

    public JsonArray getTopDirectors(int count) {
        // Get the top directors from MongoDB
        List<Document> topDir = mongoMovieRepo.getTopDir(count);

        JsonArrayBuilder jsonArrayBuilder = Json.createArrayBuilder();

        // Iterate through each director's document
        for (Document d : topDir) {
            String directorName = d.getString("_id"); // Director's name
            int moviesCount = d.getInteger("movies_count"); // Number of movies
            List<String> imdbIds = d.getList("imdb_ids", String.class); // List of IMDb IDs

            // Fetch financial data from MySQL
            List<BigDecimal> finances = mySQLMovieRepo.getFinances(imdbIds);

            BigDecimal totalRevenue = BigDecimal.ZERO;
            BigDecimal totalBudget = BigDecimal.ZERO;

            if (finances.size() >= 2) {
                totalRevenue = finances.get(0); // Revenue
                totalBudget = finances.get(1); // Budget
            }

            // Construct JSON object
            JsonObject jsonObject = Json.createObjectBuilder()
                    .add("director_name", directorName)
                    .add("movies_count", moviesCount)
                    .add("imdb_ids", Json.createArrayBuilder(imdbIds)) // Ensure array format
                    .add("total_revenue", totalRevenue != null ? totalRevenue.toString() : "0")
                    .add("total_budget", totalBudget != null ? totalBudget.toString() : "0")
                    .build();

            jsonArrayBuilder.add(jsonObject);
        }
        System.out.println("Top directors: " + jsonArrayBuilder.build());
        return jsonArrayBuilder.build();

    }


    // TODO: Task 4
    // You may change the signature of this method by passing any number of parameters
    // and returning any type
    public void generatePDFReport(JsonArray obj) throws FileNotFoundException, JRException {
        String filename = obj.toString();
        ByteArrayInputStream baos = new ByteArrayInputStream(filename.getBytes(StandardCharsets.UTF_8));
        JsonDataSource directorData = new JsonDataSource(baos);

        // Create the report's parameters
      Map<String, Object> params = new HashMap<>();
      params.put("DIRECTOR_TABLE_DATA", directorData);

      // Load the Data
        InputStream reportStream = getClass().getClassLoader().getResourceAsStream("director_movies_report.jrxml");
        if (reportStream == null) {
            throw new FileNotFoundException("JRXML template not found in resources!");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

      // populate the report with the JSON data sources
      JasperPrint print = JasperFillManager.fillReport(jasperReport, params, directorData);

      // Generate the report as PDF
      JasperExportManager.exportReportToPdfFile(print, "output_report.pdf");
    }

}
