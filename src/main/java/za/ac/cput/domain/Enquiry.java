/* Enquiry.java
 * Customer enquiry sent from the Contact page and answered by the admin.
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enquiries")
public class Enquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 30)
    private String orderNumber;

    @Column(nullable = false, length = 150)
    private String subject;

    @Column(nullable = false, length = 2000)
    private String message;

    /** NEW, IN_PROGRESS or RESOLVED */
    @Column(nullable = false, length = 20)
    private String status = "NEW";

    @Column(length = 2000)
    private String adminReply;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime repliedAt;

    public Enquiry() { }

    public Long getId()                  { return id; }
    public String getName()              { return name; }
    public void setName(String v)        { this.name = v; }
    public String getEmail()             { return email; }
    public void setEmail(String v)       { this.email = v; }
    public String getOrderNumber()       { return orderNumber; }
    public void setOrderNumber(String v) { this.orderNumber = v; }
    public String getSubject()           { return subject; }
    public void setSubject(String v)     { this.subject = v; }
    public String getMessage()           { return message; }
    public void setMessage(String v)     { this.message = v; }
    public String getStatus()            { return status; }
    public void setStatus(String v)      { this.status = v; }
    public String getAdminReply()        { return adminReply; }
    public void setAdminReply(String v)  { this.adminReply = v; }
    public LocalDateTime getCreatedAt()  { return createdAt; }
    public LocalDateTime getRepliedAt()  { return repliedAt; }
    public void setRepliedAt(LocalDateTime v) { this.repliedAt = v; }
}
