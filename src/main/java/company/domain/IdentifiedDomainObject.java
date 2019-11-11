package company.domain;

import com.google.common.base.Preconditions;
import com.googlecode.objectify.annotation.Id;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Layer supertype for objects in the domain that require identity for the purposes for persistence. Access to
 * this id is only appropriate for the infrastructure layer, however, at the time of this writing, java doesn't give us
 * a way to limit access to only that layer.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED) //for objectify
public abstract class IdentifiedDomainObject implements DomainObject {

    @Id
    private String id;

    protected IdentifiedDomainObject(Identity identity) {
        checkNotNull(identity, "identity");
        setId(identity.getId());
    }

    protected IdentifiedDomainObject(String id) {
        setId(id);
    }

    /**
     * @return the id for this value object to be used when a persistence strategy requires a unique id
     */
    public String id() {
        return id;
    }

    public String getId() {
        return id();
    }

    private void setId(String id) {
        Preconditions.checkArgument(StringUtils.hasText(id), "id must not be empty");
        this.id = id;
    }
}
