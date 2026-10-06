package so.somqr.payment.tracing;

import org.slf4j.MDC;

import java.util.UUID;

public final class TraceId {

    public static final String HEADER_NAME = "X-Trace-Id";
    public static final String MDC_KEY = "traceId";
    public static final String REQUEST_ATTRIBUTE = TraceId.class.getName() + ".traceId";

    private TraceId() {
    }

    public static String current() {
        return MDC.get(MDC_KEY);
    }

    public static String generate() {
        return UUID.randomUUID().toString();
    }

    public static boolean isValid(String traceId) {
        return traceId != null
                && traceId.length() <= 128
                && traceId.matches("[A-Za-z0-9._-]+");
    }
}
