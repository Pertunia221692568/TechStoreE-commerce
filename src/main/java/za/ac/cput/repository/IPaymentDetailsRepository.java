package za.ac.cput.repository;

import za.ac.cput.domain.PaymentDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPaymentDetailsRepository extends JpaRepository<PaymentDetails, Long> {

    Optional<PaymentDetails> findByPaymentId(String paymentId);

    Optional<PaymentDetails> findFirstByOrderIdOrderByIdDesc(Long orderId);
}