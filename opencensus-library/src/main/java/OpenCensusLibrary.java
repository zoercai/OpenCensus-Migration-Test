import io.opencensus.common.Scope;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.Span;
import io.opencensus.trace.Span.Kind;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;

public class OpenCensusLibrary {

  private static final Tracer tracer = Tracing.getTracer();

  public static void scopedSpans() {
    try (Scope scope =
        tracer
            .spanBuilder("OpenCensusSpan")
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .setSpanKind(Kind.SERVER)
            .startScopedSpan()) {
      Span span = tracer.getCurrentSpan();
      span.putAttribute("zoe", AttributeValue.stringAttributeValue("TestZoeValue"));
      span.addAnnotation("OpenCensus: Doing initial work");
      innerScopedSpan();
      span.addAnnotation("OpenCensus: Finished initial work");
      span.addAnnotation("OpenCensus: Hello world!");
    }
    Tracing.getExportComponent().shutdown();
  }

  private static void innerScopedSpan() {
    try (Scope scope =
        tracer
            .spanBuilder("OpenCensusSpan Inner")
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startScopedSpan()) {
      Span span = tracer.getCurrentSpan();
      span.addAnnotation("OpenCensus Inner: Doing initial work");
      span.addAnnotation("OpenCensus Inner: Finished initial work");
      span.addAnnotation("OpenCensus Inner: Hello world!");
    }
  }
}
