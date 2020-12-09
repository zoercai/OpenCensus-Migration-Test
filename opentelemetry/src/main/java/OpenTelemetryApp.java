import com.google.cloud.opentelemetry.trace.TraceConfiguration;
import com.google.cloud.opentelemetry.trace.TraceExporter;
//import com.google.cloud.spanner.DatabaseClient;
//import com.google.cloud.spanner.DatabaseId;
//import com.google.cloud.spanner.ResultSet;
//import com.google.cloud.spanner.Spanner;
//import com.google.cloud.spanner.SpannerOptions;
//import com.google.cloud.spanner.Statement;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.exporters.logging.LoggingSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.trace.SpanProcessor;
import io.opentelemetry.sdk.trace.export.SimpleSpanProcessor;
import java.io.IOException;
import java.time.Duration;

public class OpenTelemetryApp {

  private TraceExporter traceExporter;

  private Tracer tracer =
      OpenTelemetry.getGlobalTracer("io.opentelemetry.example.TraceExporterExample");
  private SpanProcessor spanProcessor;

  private void setupOtelExporter() {
    TraceConfiguration configuration =
        TraceConfiguration.builder().setDeadline(Duration.ofMillis(900000)).build();
    try {
      this.traceExporter = TraceExporter.createWithConfiguration(configuration);
      spanProcessor = SimpleSpanProcessor.builder(this.traceExporter).build();
      OpenTelemetrySdk.getGlobalTracerManagement().addSpanProcessor(spanProcessor);
    } catch (IOException e) {
      System.out.println("Uncaught Exception");
    }
//        LoggingSpanExporter spanExporter = new LoggingSpanExporter();
//        spanProcessor = SimpleSpanProcessor.builder(spanExporter).build();
//        OpenTelemetrySdk.getGlobalTracerManagement().addSpanProcessor(spanProcessor);
  }

  private void myUseCase() throws IOException {
    Span span = tracer.spanBuilder("OpenTelemetry App: Start my use case").startSpan();
    try (Scope scope = Context.current().with(span).makeCurrent()) {
      span.addEvent("OpenTelemetry App: Event 0");
      OpenCensusLibrary.scopedSpans();
      span.addEvent("OpenTelemetry App: Event 1");
    } finally {
      span.end();
      spanProcessor.forceFlush();
      spanProcessor.shutdown();
    }
  }

//  private void doSpannerWork() {
//    SpannerOptions options = SpannerOptions.newBuilder().build();
//    Spanner spanner = options.getService();
//
//    String instanceId = "test-instance";
//    String databaseId = "example-db";
//
//    try {
//      // Creates a database client
//      DatabaseClient dbClient =
//          spanner.getDatabaseClient(DatabaseId.of(options.getProjectId(), instanceId, databaseId));
//
//      ResultSet resultSet =
//          dbClient
//              .singleUse() // Execute a single read or query against Cloud Spanner.
//              .executeQuery(Statement.of("SELECT SingerId, AlbumId, AlbumTitle FROM Albums"));
//      while (resultSet.next()) {
//        System.out.printf(
//            "%d %d %s\n", resultSet.getLong(0), resultSet.getLong(1), resultSet.getString(2));
//      }
//    } finally {
//      // Closes the client which will free up the resources used
//      spanner.close();
//    }
//  }

  public static void main(String[] args) throws IOException {
    OpenTelemetryApp example = new OpenTelemetryApp();
    example.setupOtelExporter();
    example.myUseCase();
  }
}
