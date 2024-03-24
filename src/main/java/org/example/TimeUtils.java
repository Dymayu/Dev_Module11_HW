package org.example;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class TimeUtils {
    public String timeZoneHandler(String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        TimeZone.getTimeZone(zoneId);

        // Define the date-time formatter
        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z").withZone(zoneId);

        // Convert the current time to the specified timezone
        ZonedDateTime utcDateTime = LocalDateTime.now().atZone(ZoneId.systemDefault()).withZoneSameInstant(zoneId);
        String formattedDateTime = utcDateTime.format(targetFormatter);

        return formattedDateTime;
    }
}
