package company.infrastructure.datastore.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;

public abstract class AbstractJacksonConfig {

    public static final String MISSING_FIELD_VALUE_KEY = "missingFieldValue";

    public abstract Jackson2ObjectMapperBuilderCustomizer jacksonBuilderCustomizer(ApplicationContext ctx);

    /**
     * Builds a base Jackson Builder with field level items configured, but not modules installed.
     *
     * @param ctx
     * @return
     */
    public Jackson2ObjectMapperBuilderCustomizer baseConfig(Jackson2ObjectMapperBuilderCustomizer customizer, ApplicationContext ctx) {
        return objectMapperBuilder -> {
            objectMapperBuilder.applicationContext(ctx);
            objectMapperBuilder.featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            objectMapperBuilder.serializationInclusion(JsonInclude.Include.NON_NULL);
            customizer.customize(objectMapperBuilder);
        };
    }
}
