import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class ConverterConfig {

    private final StringToStartOfDayOffsetDateTimeConverter startOfDayConverter;
    private final StringToEndOfDayOffsetDateTimeConverter endOfDayConverter;

    public ConverterConfig(StringToStartOfDayOffsetDateTimeConverter startOfDayConverter,
                           StringToEndOfDayOffsetDateTimeConverter endOfDayConverter) {
        this.startOfDayConverter = startOfDayConverter;
        this.endOfDayConverter = endOfDayConverter;
    }

    @Bean
    public ConversionService conversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(startOfDayConverter);
        conversionService.addConverter(endOfDayConverter);
        return conversionService;
    }
}
