/* IEnquiryRepository.java
 * Author: Pertunia Sifunda (221692568)
 */
package za.ac.cput.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import za.ac.cput.domain.Enquiry;

import java.util.List;

@Repository
public interface IEnquiryRepository extends JpaRepository<Enquiry, Long> {

    List<Enquiry> findAllByOrderByCreatedAtDesc();

    long countByStatus(String status);
}

