package company.infrastructure.datastore;

import company.domain.User;
import company.domain.UserId;
import company.domain.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Component
public class ObjectifyUserRepository implements UserRepository {

    @Override
    public void save(User user) {
        ofy().save().entity(user).now();
    }

    @Override
    public Optional<User> find(UserId userId) {
        return Optional.of(ofy().load().type(User.class).id(userId.getId()).now());
    }
}
