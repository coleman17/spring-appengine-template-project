package company.infrastructure.datastore.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class JacksonConfiguration extends AbstractJacksonConfig {

    @Override
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonBuilderCustomizer(ApplicationContext ctx) {
        return baseConfig(objectMapperBuilder ->
                        objectMapperBuilder.modulesToInstall(
                                new JodaModule()),
                ctx);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        return objectMapper;
    }
}
