package company.application;

import company.domain.User;
import company.domain.UserId;
import company.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;

    public Optional<User> find(UserId userId) {
        return userRepository.find(userId);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
