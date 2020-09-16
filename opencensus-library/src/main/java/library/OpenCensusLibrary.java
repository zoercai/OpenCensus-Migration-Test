package library;

import io.opencensus.trace.Span;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;

public class OpenCensusLibrary {

  private static final Tracer tracer = Tracing.getTracer();

  public static void getCalled() throws IOException {
    Span span = tracer.spanBuilder("OpenCensusSpan")
        .setRecordEvents(true)
        .setSampler(Samplers.alwaysSample())
        .startSpan();
    span.addAnnotation("OpenCensus: Doing initial work");
    doWork();
    span.addAnnotation("OpenCensus: Finished initial work");
    span.addAnnotation("OpenCensus: Hello world!");
    span.end();
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
