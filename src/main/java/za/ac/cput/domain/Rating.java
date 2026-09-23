package za.ac.cput.domain;

import java.time.LocalDateTime;

public class Rating {

    private Integer score;
    private String comment;
    private LocalDateTime ratingDate;
    private String ratingId;

    public Rating() {
    }

    public Rating(Integer score,
                  String comment,
                  LocalDateTime ratingDate,
                  String ratingId) {

        this.score = score;
        this.comment = comment;
        this.ratingDate = ratingDate;
        this.ratingId = ratingId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDateTime ratingDate) {
        this.ratingDate = ratingDate;
    }

    public String getRatingId() {
        return ratingId;
    }

    public void setRatingId(String ratingId) {
        this.ratingId = ratingId;
    }
}