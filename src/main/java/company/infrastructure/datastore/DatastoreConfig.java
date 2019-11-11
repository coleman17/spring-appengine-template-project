package company.infrastructure.datastore;

import com.google.appengine.api.utils.SystemProperty;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.impl.Path;
import com.googlecode.objectify.impl.TypeUtils;
import com.googlecode.objectify.impl.translate.*;
import com.googlecode.objectify.impl.translate.opt.joda.DateTimeZoneTranslatorFactory;
import com.googlecode.objectify.impl.translate.opt.joda.ReadablePartialTranslatorFactory;
import company.domain.User;
import company.infrastructure.datastore.objectify.GaeMemcacheService;
import company.support.ClockFactory;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.lang.invoke.MethodHandle;

@Configuration
public class DatastoreConfig implements InitializingBean {

    public static void configureObjectify() {
        ObjectifyService.factory().getTranslators().add(new ReadableInstantTranslatorFactory(ClockFactory.TIME_ZONE));
        ObjectifyService.factory().getTranslators().add(new ReadablePartialTranslatorFactory());
        ObjectifyService.factory().getTranslators().add(new DateTimeZoneTranslatorFactory());

        ObjectifyService.register(User.class);
    }

    @Bean
    @Profile("cloud")
    public Datastore datastore() {
        return DatastoreOptions.newBuilder()
                .setProjectId(SystemProperty.applicationId.get())
                .build()
                .getService();
    }

    @Override
    public void afterPropertiesSet() {
        ObjectifyFactory objectifyFactory = new ObjectifyFactory(datastore(), new GaeMemcacheService());
        ObjectifyService.init(objectifyFactory);
        configureObjectify();
    }

    /**
     * The {@link com.googlecode.objectify.impl.translate.opt.joda.ReadableInstantTranslatorFactory} implementation
     * changes the timezone on read to the default system timezone, which is not awesome for developing locally
     * (where we generally have the local timezone as the default system timezone. This implementation enforces the
     * provided timezone.
     */
    private static class ReadableInstantTranslatorFactory extends ValueTranslatorFactory<ReadableInstant, Timestamp> {

        private final DateTimeZone timeZone;

        ReadableInstantTranslatorFactory(DateTimeZone timeZone) {
            super(ReadableInstant.class);
            this.timeZone = timeZone;
        }

        @Override
        protected ValueTranslator<ReadableInstant, Timestamp> createValueTranslator(final TypeKey<ReadableInstant> tk,
                                                                                    final CreateContext ctx, final Path path) {
            final Class<?> clazz = tk.getTypeAsClass();

            final MethodHandle ctor = TypeUtils.getConstructor(clazz, long.class, DateTimeZone.class);

            return new ValueTranslator<>(ValueType.TIMESTAMP) {
                @Override
                protected ReadableInstant loadValue(final Value<Timestamp> value, final LoadContext ctx, final Path path) {
                    return TypeUtils.invoke(ctor, value.get().toSqlTimestamp().getTime(), timeZone);
                }

                @Override
                protected Value<Timestamp> saveValue(final ReadableInstant value, final SaveContext ctx, final Path path) {
                    return TimestampValue.of(Timestamp.of(value.toInstant().toDate()));
                }
            };
        }
    }
}
