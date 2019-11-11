package company.support;

import com.google.common.base.Preconditions;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * Produces Clocks for every situation.
 * <p>
 * ClockFactory is thread safe.
 * <p>
 * The most common usage is via {@link #CLOCK_FACTORY}.
 */
public class ClockFactory {

    public static final ClockFactory CLOCK_FACTORY = new ClockFactory();
    /**
     * The default timezone for this piece of software.
     */
    public static final DateTimeZone TIME_ZONE = DateTimeZone.UTC;
    private static final Clock SYSTEM_CLOCK = new SystemClock();

    /**
     * Suitable for general usage.
     *
     * @return a Clock that uses the system's time with timezone {@link ClockFactory#TIME_ZONE}.
     */
    public Clock systemClock() {
        return SYSTEM_CLOCK;
    }

    /**
     * Provides a Clock implementation with the specified timezone,
     * which is suitable for lab specific date/time calculations.
     */
    public Clock from(DateTimeZone timeZone) {
        return new ConfigurableTimezoneClock(timeZone);
    }

    /**
     * A Clock implementation that uses the timezone {@link ClockFactory#TIME_ZONE}
     */
    private static class SystemClock implements Clock {
        @Override
        public DateTime currentDateTime() {
            return DateTime.now(TIME_ZONE);
        }
    }

    /**
     * A Clock implementation that uses the specified timezone.
     */
    private static class ConfigurableTimezoneClock implements Clock {

        private final DateTimeZone timeZone;

        private ConfigurableTimezoneClock(DateTimeZone timeZone) {
            this.timeZone = Preconditions.checkNotNull(timeZone, "timeZone");
        }

        @Override
        public DateTime currentDateTime() {
            return DateTime.now(timeZone);
        }
    }
}