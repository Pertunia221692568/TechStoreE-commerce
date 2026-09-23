package za.ac.cput.repository;

import za.ac.cput.domain.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IPaymentDetailsRepository extends JpaRepository<PaymentDetails, String> {
}