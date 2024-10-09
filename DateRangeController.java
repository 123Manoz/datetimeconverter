import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class DateRangeController {

    @GetMapping("/date-range")
    public Map<String, Object> getDateRange(
            @RequestParam("from") @NotNull(message = "From date is mandatory") OffsetDateTime fromStartOfDay,
            @RequestParam(value = "to", required = false) OffsetDateTime toEndOfDay
    ) {
        Map<String, Object> response = new HashMap<>();
        response.put("from", fromStartOfDay);
        
        if (toEndOfDay == null) {
            // If `to` is not provided, default to the end of the current day
            toEndOfDay = OffsetDateTime.now().with(LocalTime.of(23, 59, 59));
        }
        response.put("to", toEndOfDay);
        
        return response;
    }
}
