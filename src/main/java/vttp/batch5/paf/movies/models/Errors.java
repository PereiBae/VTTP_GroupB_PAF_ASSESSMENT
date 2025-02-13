package vttp.batch5.paf.movies.models;

import org.springframework.data.mongodb.core.mapping.Document;

import java.sql.Date;
import java.util.List;

@Document(collection = "errors")
public class Errors {

    private List<String> id;
    private String message;
    private Date timestamp;

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
