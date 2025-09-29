package nl.helicenter.flightmaster.repository;

import nl.helicenter.flightmaster.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByUserPhoto_FileName(String fileName);
    boolean existsByEmail(String email);
}
