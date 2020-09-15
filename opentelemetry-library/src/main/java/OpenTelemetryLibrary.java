import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;

public class OpenTelemetryLibrary {

  private Tracer tracer = OpenTelemetry.getTracer("io.opentelemetry.example.TraceExporterExample2");

  private void myUseCase() {
    Span span = this.tracer.spanBuilder("OpenTelemetry2: Start my use case").startSpan();
    span.addEvent("OpenTelemetry2: Event 0");
    doWork();
    span.addEvent("OpenTelemetry2: Event 1");
    span.end();
  }

  private void doWork() {
    try {
      Thread.sleep((int) (Math.random() * 1000) + 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void getCalled() {
    OpenTelemetryLibrary example = new OpenTelemetryLibrary();
    example.myUseCase();
  }
}
