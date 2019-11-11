package company.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@Getter
@EqualsAndHashCode
public class UserId implements Identity, Serializable {

    private static final long serialVersionUID = -8237819283729203284L;

    @NonNull
    private String id;

    public UserId(String id) {
        if (!StringUtils.hasText(id)) {
            throw new IllegalArgumentException(id);
        }
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }
}
