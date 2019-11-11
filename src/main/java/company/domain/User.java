package company.domain;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Index;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import static lombok.AccessLevel.PRIVATE;
import static org.springframework.util.StringUtils.hasText;

@Getter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor(access = PRIVATE) // for objectify
public class User extends IdentifiedDomainObject {

    private String name;
    @Index
    private DateTime createdDate;

    public User(UserId userId, String name, DateTime createdDate) {
        super(userId);

        if (!hasText(name)) {
            throw new IllegalArgumentException("user name must have a value");
        } else {
            this.name = name;
        }

        this.createdDate = Preconditions.checkNotNull(createdDate, "createdDate");
    }

    public UserId getUserId() {
        return new UserId(id());
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + id() + '\'' +
                ", name='" + name + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
