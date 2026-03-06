package nl.wunderbieb.kms.api.web;

import java.time.Instant;
import java.util.Map;

public record ApiError(
    String code,
    String message,
    Map<String, Object> details,
    String traceId,
    Instant timestamp
) {
}
