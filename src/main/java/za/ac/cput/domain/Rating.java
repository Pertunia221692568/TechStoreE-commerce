package za.ac.cput.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ratings")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer score;
    private String comment;
    private LocalDateTime ratingDate;

    /** Product this rating belongs to */
    @Column(nullable = false)
    private Long productId;

    public Rating() {
    }

    public Rating(Integer score, String comment, LocalDateTime ratingDate, Long productId) {
        this.score = score;
        this.comment = comment;
        this.ratingDate = ratingDate;
        this.productId = productId;
    }

    public Long getId() {
        return id;
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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}