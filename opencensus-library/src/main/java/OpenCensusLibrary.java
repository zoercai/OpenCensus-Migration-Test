import io.opencensus.common.Scope;
import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;

public class OpenCensusLibrary {

  private static final Tracer tracer = Tracing.getTracer();

  public static void unscopedSpans() throws IOException {
    Span span =
        tracer
            .spanBuilder("OpenCensusSpan")
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startSpan();
    span.addAnnotation("OpenCensus: Doing initial work");
    doWork();
    Span span2 =
        tracer
            .spanBuilder("OpenCensusSpan2")
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startSpan();
    span2.addAnnotation("OpenCensus2: Some work");
    span2.end();
    span.addAnnotation("OpenCensus: Finished initial work");
//    OpenTelemetryLibrary.getCalled();
    span.addAnnotation("OpenCensus: Hello world!");
    span.end();
    Tracing.getExportComponent().shutdown();
  }

  public static void scopedSpans() {
    try (Scope scope =
        tracer
            .spanBuilder("OpenCensusSpan")
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startScopedSpan()) {
      Span span = tracer.getCurrentSpan();
      span.addAnnotation("OpenCensus: Doing initial work");
      span.addAnnotation("OpenCensus: Finished initial work");
      OpenTelemetryLibrary.scopedSpans();
      span.addAnnotation("OpenCensus: Hello world!");
    }
    Tracing.getExportComponent().shutdown();
  }

  private static void doWork() {
    try {
      Thread.sleep((int) (Math.random() * 100) + 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
