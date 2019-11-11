package company.domain;

import java.util.Optional;

public interface UserRepository {

    void save(User user);

    Optional<User> find(UserId userId);
}
