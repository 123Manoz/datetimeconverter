import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class StringToEndOfDayOffsetDateTimeConverter implements Converter<String, OffsetDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Override
    public OffsetDateTime convert(String source) {
        try {
            // Parse the string using the "MM-dd-yyyy" pattern
            LocalDate date = LocalDate.parse(source, FORMATTER);
            // Convert to OffsetDateTime at the end of the day (23:59:59) with UTC timezone
            return OffsetDateTime.of(date, LocalTime.of(23, 59, 59), ZoneOffset.UTC);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid date format. Please use MM-dd-yyyy.");
        }
    }
}
