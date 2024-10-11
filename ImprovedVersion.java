// CustomDate.java
package com.example.demo.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomDate {
    String message() default "Invalid date format. Use MM-dd-yyyy";
    boolean optional() default false;
    boolean endOfDay() default false;
}

// CustomDateConverter.java
package com.example.demo.converter;

import com.example.demo.annotation.CustomDate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Component
public class CustomDateConverter implements Converter<String, OffsetDateTime> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private final CustomDate annotation;

    public CustomDateConverter(CustomDate annotation) {
        this.annotation = annotation;
    }

    @Override
    public OffsetDateTime convert(String source) {
        if (source == null || source.isEmpty()) {
            if (annotation.optional() && annotation.endOfDay()) {
                return OffsetDateTime.now(ZoneOffset.UTC)
                        .withHour(23).withMinute(59).withSecond(59).withNano(999999999);
            }
            return null;
        }

        LocalDate date = LocalDate.parse(source, DATE_FORMATTER);
        if (annotation.endOfDay()) {
            return date.plusDays(1).atStartOfDay().minusNanos(1).atOffset(ZoneOffset.UTC);
        } else {
            return date.atStartOfDay().atOffset(ZoneOffset.UTC);
        }
    }
}

// CustomDateValidator.java
package com.example.demo.validator;

import com.example.demo.annotation.CustomDate;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomDateValidator implements ConstraintValidator<CustomDate, String> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private boolean optional;

    @Override
    public void initialize(CustomDate constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (optional && (value == null || value.isEmpty())) {
            return true;
        }
        if (!optional && (value == null || value.isEmpty())) {
            return false;
        }
        try {
            DATE_FORMATTER.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}

// DateRangeController.java
package com.example.demo.controller;

import com.example.demo.annotation.CustomDate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;

@RestController
public class DateRangeController {

    @GetMapping("/date-range")
    public ResponseEntity<String> getDateRange(
            @RequestParam @CustomDate OffsetDateTime from,
            @RequestParam(required = false) @CustomDate(optional = true, endOfDay = true) OffsetDateTime to) {

        return ResponseEntity.ok(String.format("From: %s, To: %s", from, to));
    }

    @GetMapping("/another-endpoint")
    public ResponseEntity<String> anotherEndpoint(
            @RequestParam @CustomDate OffsetDateTime from,
            @RequestParam(required = false) @CustomDate(optional = true, endOfDay = true) OffsetDateTime to) {

        return ResponseEntity.ok("Another endpoint using the same date conversion");
    }
}


package com.example.demo.validator;

import com.example.demo.annotation.CustomDate;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomDateValidator implements ConstraintValidator<CustomDate, String> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");
    private boolean optional;

    @Override
    public void initialize(CustomDate constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (optional && (value == null || value.isEmpty())) {
            return true;
        }
        if (!optional && (value == null || value.isEmpty())) {
            return false;
        }
        try {
            DATE_FORMATTER.parse(value);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
