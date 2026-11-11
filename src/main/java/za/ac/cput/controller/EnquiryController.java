/* EnquiryController.java
 * Public endpoint used by the Contact page form.
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.ac.cput.domain.Enquiry;
import za.ac.cput.repository.IEnquiryRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/enquiries")
public class EnquiryController {

    private final IEnquiryRepository enquiryRepository;

    public EnquiryController(IEnquiryRepository enquiryRepository) {
        this.enquiryRepository = enquiryRepository;
    }

    /** POST /api/enquiries — anyone can send an enquiry */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, String> body) {
        String name    = clean(body.get("name"), 100);
        String email   = clean(body.get("email"), 150);
        String subject = clean(body.get("subject"), 150);
        String message = clean(body.get("message"), 2000);
        String order   = clean(body.get("orderNumber"), 30);

        if (name.isEmpty() || subject.isEmpty() || message.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name, subject and message are required."));
        }
        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Please enter a valid email address."));
        }

        Enquiry e = new Enquiry();
        e.setName(name);
        e.setEmail(email);
        e.setSubject(subject);
        e.setMessage(message);
        e.setOrderNumber(order.isEmpty() ? null : order);
        enquiryRepository.save(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Enquiry received"));
    }

    private static String clean(String v, int max) {
        if (v == null) return "";
        String t = v.trim();
        return t.length() > max ? t.substring(0, max) : t;
    }
}

