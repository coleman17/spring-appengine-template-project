package company.interfaces.web;

import company.application.UserApplicationService;
import company.domain.User;
import company.domain.UserId;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/users")
@ExposesResourceFor(User.class)
@RequiredArgsConstructor
public class UserController {

    @NonNull
    private final UserApplicationService userApplicationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void saveUser(@RequestBody User user) {
        log.info("saving new user [{}]", user.toString());
        userApplicationService.save(user);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") String userId) {
        log.info("retrieving user [{}]", userId);
        Optional<User> user = userApplicationService.find(new UserId(userId));
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
