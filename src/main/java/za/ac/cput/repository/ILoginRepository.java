package za.ac.cput.repository;




import za.ac.cput.domain.Login;
import java.util.List;

    public interface ILoginRepository {

        Login save(Login login);

        Login findById(String loginId);

        List<Login> findAll();

        Login update(Login login);

        boolean delete(String loginId);
    }

