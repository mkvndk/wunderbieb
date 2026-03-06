package nl.wunderbieb.kms.commons.api;

import java.time.Instant;
import java.util.Map;

public record ApiErrorResponse(
    String code,
    String message,
    Map<String, Object> details,
    String traceId,
    Instant timestamp
) {
}
