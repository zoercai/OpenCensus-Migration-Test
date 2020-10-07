import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.context.Scope;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;

public class OpenTelemetryLibrary {

  private static Tracer tracer =
      OpenTelemetry.getTracer("io.opentelemetry.example.TraceExporterExample2");

  public static void scopedSpans() {
    Span span = tracer.spanBuilder("OpenTelemetry2: Start my use case").startSpan();
    try (Scope scope = tracer.withSpan(span)) {
      span.addEvent("OpenTelemetry2: Event 0");
      doWork();
      span.addEvent("OpenTelemetry2: Event 1");
    } finally {
      span.end();
    }
  }

  public static void nonScopedSpans() {
    Span span = tracer.spanBuilder("OpenTelemetry2: Start my use case").startSpan();
    span.addEvent("OpenTelemetry2: Event 0");
    Span span2 = tracer.spanBuilder("OpenTelemetry2: Start my use case").startSpan();
    span2.end();
    span.end();
  }

  private static void doWork() {
    try {
      Thread.sleep((int) (Math.random() * 1000) + 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
