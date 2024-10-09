// CustomDateRange.java
package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomDateRange {
    String message() default "Invalid date format. Use MM-dd-yyyy";
}

// DateRangeModel.java
package com.example.demo.model;

import java.time.OffsetDateTime;

public class DateRangeModel {
    private final OffsetDateTime from;
    private final OffsetDateTime to;

    public DateRangeModel(OffsetDateTime from, OffsetDateTime to) {
        this.from = from;
        this.to = to;
    }

    public OffsetDateTime getFrom() {
        return from;
    }

    public OffsetDateTime getTo() {
        return to;
    }
}

// CustomDateRangeConverter.java
package com.example.demo.converter;

import com.example.demo.model.DateRangeModel;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class CustomDateRangeConverter implements Converter<String, DateRangeModel> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Override
    public DateRangeModel convert(String source) {
        String[] dates = source.split(",");
        if (dates.length != 2) {
            throw new IllegalArgumentException("Invalid date range format");
        }

        OffsetDateTime from = LocalDate.parse(dates[0], DATE_FORMATTER)
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);

        OffsetDateTime to = LocalDate.parse(dates[1], DATE_FORMATTER)
                .plusDays(1)
                .atStartOfDay()
                .minusNanos(1)
                .atOffset(ZoneOffset.UTC);

        return new DateRangeModel(from, to);
    }
}

// CustomDateRangeValidator.java
package com.example.demo.validator;

import com.example.demo.annotation.CustomDateRange;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomDateRangeValidator implements ConstraintValidator<CustomDateRange, String> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return false;
        }

        String[] dates = value.split(",");
        if (dates.length != 2) {
            return false;
        }

        try {
            DATE_FORMATTER.parse(dates[0]);
            DATE_FORMATTER.parse(dates[1]);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}

// DateRangeController.java
package com.example.demo.controller;

import com.example.demo.annotation.CustomDateRange;
import com.example.demo.model.DateRangeModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DateRangeController {

    @GetMapping("/date-range")
    public ResponseEntity<String> getDateRange(
            @RequestParam @CustomDateRange DateRangeModel dateRange) {

        return ResponseEntity.ok(String.format("From: %s, To: %s", 
                dateRange.getFrom(), dateRange.getTo()));
    }

    @GetMapping("/another-endpoint")
    public ResponseEntity<String> anotherEndpoint(
            @RequestParam @CustomDateRange DateRangeModel dateRange) {

        // Use dateRange.getFrom() and dateRange.getTo() as needed
        return ResponseEntity.ok("Another endpoint using the same date range conversion");
    }
}
