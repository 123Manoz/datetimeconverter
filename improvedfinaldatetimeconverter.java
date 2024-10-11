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


package com.example.demo.resolver;

import com.example.demo.annotation.CustomDate;
import com.example.demo.converter.CustomDateConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.OffsetDateTime;

public class CustomDateArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private CustomDateConverter customDateConverter;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CustomDate.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        CustomDate customDate = parameter.getParameterAnnotation(CustomDate.class);
        String paramValue = webRequest.getParameter(parameter.getParameterName());

        return customDateConverter.convert(paramValue, customDate.optional(), customDate.endOfDay());
    }
}


package com.example.demo.config;

import com.example.demo.resolver.CustomDateArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final CustomDateArgumentResolver customDateArgumentResolver;

    public WebConfig(CustomDateArgumentResolver customDateArgumentResolver) {
        this.customDateArgumentResolver = customDateArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(customDateArgumentResolver);
    }
}

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


