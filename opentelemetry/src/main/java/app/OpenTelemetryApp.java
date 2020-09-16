package app;

import com.google.cloud.opentelemetry.trace.TraceConfiguration;
import com.google.cloud.opentelemetry.trace.TraceExporter;
import exporter.OtelExporter;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.config.TraceParams;
import io.opencensus.trace.samplers.Samplers;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.context.Scope;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;
import java.io.IOException;
import java.time.Duration;
import library.OpenCensusLibrary;

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

  private void myUseCase() throws IOException {
    Span span = this.tracer.spanBuilder("OpenTelemetry: Start my use case").startSpan();
    try (Scope scope = tracer.withSpan(span)) {
      span.addEvent("OpenTelemetry: Event 0");
      doWork();
      OpenCensusLibrary.getCalled();
      span.addEvent("OpenTelemetry: Event 1");
    } finally {
      span.end();
    }
  }

  private void doWork() {
    try {
      Thread.sleep((int) (Math.random() * 1000) + 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private static void setupOpenCensus() throws IOException {
//    StackdriverTraceExporter.createAndRegister(
//        StackdriverTraceConfiguration.builder().setDeadline(io.opencensus.common.Duration.create(60, 0))
//            .build());
//    TraceConfig traceConfig = Tracing.getTraceConfig();
//    TraceParams activeTraceParams = traceConfig.getActiveTraceParams();
//    traceConfig.updateActiveTraceParams(
//        activeTraceParams.toBuilder().setSampler(
//            Samplers.alwaysSample()).build());
    OtelExporter.createAndRegister();
    System.out.println("Finished setting up opencensus.");
  }

  public static void main(String[] args) throws IOException {
    OpenTelemetryApp example = new OpenTelemetryApp();
    if (System.getenv("OPENCENSUS_SETUP") != null && System.getenv("OPENCENSUS_SETUP").equalsIgnoreCase("true")) {
      setupOpenCensus();
    }
    example.setupTraceExporter();
    example.myUseCase();
  }
}
