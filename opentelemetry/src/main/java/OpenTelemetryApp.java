import com.google.cloud.opentelemetry.trace.TraceConfiguration;
import com.google.cloud.opentelemetry.trace.TraceExporter;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;
import java.io.IOException;
import java.time.Duration;

public class OpenTelemetryApp {
  private TraceExporter traceExporter;

  private Tracer tracer = OpenTelemetry.getTracer("io.opentelemetry.example.TraceExporterExample");

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
    Span span = this.tracer.spanBuilder("OpenTelemetry: Start my use case").startSpan();
    span.addEvent("OpenTelemetry: Event 0");
    doWork();
    OpenTelemetryApp2.getCalled();
    span.addEvent("OpenTelemetry: Event 1");
    span.end();
  }

  private void doWork() {
    try {
      Thread.sleep((int) (Math.random() * 1000) + 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws IOException {
    OpenTelemetryApp example = new OpenTelemetryApp();
    example.setupTraceExporter();
    example.myUseCase();
//    OpenCensusApp.getCalled();
  }
}
