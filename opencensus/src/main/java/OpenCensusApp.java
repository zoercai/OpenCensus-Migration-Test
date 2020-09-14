import io.opencensus.common.Duration;
import io.opencensus.common.Scope;
import io.opencensus.exporter.trace.logging.LoggingTraceExporter;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceConfiguration;
import io.opencensus.exporter.trace.stackdriver.StackdriverTraceExporter;
import io.opencensus.trace.Span;
import io.opencensus.trace.SpanBuilder;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.config.TraceParams;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OpenCensusApp {

  private static final Tracer tracer = Tracing.getTracer();

  public static void main(String[] args) throws IOException {
    Logger logger = Logger.getLogger(OpenCensusApp.class.getName());
    logger.log(Level.INFO, "heilllo");

    LoggingTraceExporter.register();
//    StackdriverTraceExporter.createAndRegister(
//        StackdriverTraceConfiguration.builder()
//            .setDeadline(Duration.create(90, 0))
//            .setProjectId("google.com:zoe-opentelemetry-sandbox")
//            .build());
    TraceConfig traceConfig = Tracing.getTraceConfig();
    TraceParams activeTraceParams = traceConfig.getActiveTraceParams();
    traceConfig.updateActiveTraceParams(
        activeTraceParams.toBuilder().setSampler(
            Samplers.alwaysSample()).build());

    Span span = tracer.spanBuilder("OpenCensusSpan")
        .setRecordEvents(true)
        .setSampler(Samplers.alwaysSample())
        .startSpan();
    span.addAnnotation("OpenCensus: Doing initial work");
    doWork();
    span.addAnnotation("OpenCensus: Finished initial work");
    span.addAnnotation("OpenCensus: Hello world!");
    span.end();
  }

  private static void doWork() {
    try {
      Thread.sleep((int) (Math.random() * 1000) + 6000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
