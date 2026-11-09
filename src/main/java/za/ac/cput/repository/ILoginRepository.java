package za.ac.cput.repository;

import za.ac.cput.domain.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ILoginRepository extends JpaRepository<Login, Long> {
    Optional<Login> findByUsername(String username);
}


