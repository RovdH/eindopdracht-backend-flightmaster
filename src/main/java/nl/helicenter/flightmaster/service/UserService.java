package nl.helicenter.flightmaster.service;

import nl.helicenter.flightmaster.dto.UserRequestDto;
import nl.helicenter.flightmaster.model.User;
import nl.helicenter.flightmaster.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository) {
//        PasswordEncoder passwordEncoder achter userRepo terugzetten hierboven als we de encoder aanzetten na testen
        this.userRepository = userRepository;
//        this.passwordEncoder = passwordEncoder;
    }

    public String registerUser(UserRequestDto dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("E-mailadres is al in gebruik.");
        }

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());

        userRepository.save(user);
        return user.getEmail();
    }
}
