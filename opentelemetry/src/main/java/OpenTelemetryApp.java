import com.google.cloud.opentelemetry.trace.TraceConfiguration;
import com.google.cloud.opentelemetry.trace.TraceExporter;
import io.opencensus.exporter.trace.logging.LoggingTraceExporter;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.config.TraceParams;
import io.opencensus.trace.samplers.Samplers;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporters.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;
import java.io.IOException;
import java.time.Duration;

public class OpenTelemetryApp {

  private TraceExporter traceExporter;

  private Tracer tracer = OpenTelemetry.getTracer("io.opentelemetry.example.TraceExporterExample");
  private SpanProcessor spanProcessor;

  private void setupOtelExporter() {
    TraceConfiguration configuration =
        TraceConfiguration.builder().setDeadline(Duration.ofMillis(900000)).build();

//    try {
//      this.traceExporter = TraceExporter.createWithConfiguration(configuration);
//      spanProcessor = SimpleSpanProcessor.newBuilder(this.traceExporter).build();
//      OpenTelemetrySdk.getTracerProvider().addSpanProcessor(spanProcessor);
//    } catch (IOException e) {
//      System.out.println("Uncaught Exception");
//    }
    LoggingSpanExporter spanExporter = new LoggingSpanExporter();
    spanProcessor = SimpleSpanProcessor.newBuilder(spanExporter).build();
    OpenTelemetrySdk.getTracerProvider().addSpanProcessor(spanProcessor);
  }

  private static void setupOpenCensusExporter() throws IOException {
    LoggingTraceExporter.register();
//    StackdriverTraceExporter.createAndRegister(
//        StackdriverTraceConfiguration.builder()
//            .setDeadline(io.opencensus.common.Duration.create(60, 0))
//            .build());
//    TraceConfig traceConfig = Tracing.getTraceConfig();
//    TraceParams activeTraceParams = traceConfig.getActiveTraceParams();
//    traceConfig.updateActiveTraceParams(
//        activeTraceParams.toBuilder().setSampler(Samplers.alwaysSample()).build());
//    System.out.println("Finished setting up opencensus.");
  }

  private void myUseCase() throws IOException {
    Span span = this.tracer.spanBuilder("OpenTelemetry App: Start my use case").startSpan();
    try (Scope scope = tracer.withSpan(span)) {
      span.addEvent("OpenTelemetry App: Event 0");
      doWork();
      OpenTelemetryLibrary.scopedSpans();
      OpenCensusLibrary.scopedSpans();
      span.addEvent("OpenTelemetry App: Event 1");
    } finally {
      span.end();
      spanProcessor.forceFlush();
      spanProcessor.shutdown();
    }
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
    setupOpenCensusExporter();
    example.setupOtelExporter();
    example.myUseCase();
  }
}
