package vttp.batch5.paf.movies.controllers;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import vttp.batch5.paf.movies.services.MovieService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringReader;

@Controller
@RequestMapping("/api")
public class MainController {

    @Autowired
    private MovieService movieService;

    // TODO: Task 3
    @GetMapping(path = "/summary")
    @ResponseBody
    public String getDirectors(@RequestParam("count") int count, HttpSession session) {
        JsonArray payload = movieService.getProlificDirectors(count);
        session.setAttribute("directors", payload.toString());
        return payload.toString();
    }


    // TODO: Task 4
    @GetMapping("/summary/pdf")
    @ResponseBody
    public ResponseEntity<InputStreamSource> generatePdf(HttpSession session) throws JRException, FileNotFoundException {
        JsonReader reader = Json.createReader(new StringReader((String) session.getAttribute("directors")));
        JsonArray jsonObject = reader.readArray();
        movieService.generatePDFReport(jsonObject);
        File pdfFile = new File("output_report.pdf"); // Ensure this path matches your generated file
        InputStreamResource resource = new InputStreamResource(new FileInputStream(pdfFile));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

}
