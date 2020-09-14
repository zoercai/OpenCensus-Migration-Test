import com.google.cloud.opentelemetry.trace.TraceConfiguration;
import com.google.cloud.opentelemetry.trace.TraceExporter;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;
import java.io.IOException;
import java.time.Duration;

public class OpenTelemetryApp2 {

  private TraceExporter traceExporter;

  private Tracer tracer = OpenTelemetry.getTracer("io.opentelemetry.example.TraceExporterExample2");

  private void setupTraceExporter() {
    TraceConfiguration configuration =
        TraceConfiguration.builder().setDeadline(Duration.ofMillis(120000)).build();

    try {
      this.traceExporter = TraceExporter.createWithConfiguration(configuration);
      OpenTelemetrySdk.getTracerProvider()
          .addSpanProcessor(SimpleSpanProcessor.newBuilder(this.traceExporter).build());
    } catch (IOException e) {
      System.out.println("Uncaught Exception");
    }
  }

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
    OpenTelemetryApp2 example = new OpenTelemetryApp2();
    example.setupTraceExporter();
    example.myUseCase();
  }
}
