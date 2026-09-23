package za.ac.cput.repository;
import za.ac.cput.domain.Registration;
import java.util.List;

public interface IRegistrationRepository {




        Registration save(Registration registration);

        Registration findById(String registrationId);

        List<Registration> findAll();

        Registration update(Registration registration);

        boolean delete(String registrationId);
    }

