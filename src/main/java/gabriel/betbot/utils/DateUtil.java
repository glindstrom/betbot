
package gabriel.betbot.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 *
 * @author gabriel
 */
public class DateUtil {
    
    public final static String TIME_ZONE = "Europe/Helsinki";
    
    public static LocalDateTime milliSecondsToLocalDateTime(final long milliSeconds) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(milliSeconds), ZoneId.of(TIME_ZONE));
    }

}
