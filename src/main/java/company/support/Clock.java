package company.support;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

/**
 * THE way to get the current date/time in order entry
 */
public interface Clock {

    /**
     * @return the current date according to this clock.
     */
    default LocalDate currentLocalDate() {
        return currentDateTime().toLocalDate();
    }

    /**
     * @return The current time according to this clock.
     */
    DateTime currentDateTime();

}