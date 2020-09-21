package library;

import io.opencensus.common.Scope;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.Span;
import io.opencensus.trace.Status;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.samplers.Samplers;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class OpenCensusLibrary {

  private static final Tracer tracer = Tracing.getTracer();

  public static void getCalled() throws IOException {
    Span span =
        tracer
            .spanBuilder("OpenCensusSpan")
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startSpan();
    span.addAnnotation("OpenCensus: Doing initial work");
    doWork(span);
    span.addAnnotation("OpenCensus: Finished initial work");
    span.addAnnotation("OpenCensus: Hello world!");
    span.end();

    try (Scope scope =
        tracer
            .spanBuilder("OpenCensus: Main")
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startScopedSpan()) {
      for (int i = 0; i < 3; i++) {
        doWork();
      }
    }

    Tracing.getExportComponent().shutdown();
  }

  private static void doWork(Span parent) {
    try (Scope scope =
        tracer
            .spanBuilderWithExplicitParent("OpenCensus: doWork", parent)
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startScopedSpan()) {
      // Simulate some work.
      Span span = tracer.getCurrentSpan();
      try {
        System.out.println("doing busy work");
        Thread.sleep(100L);
      } catch (InterruptedException e) {
        span.setStatus(Status.INTERNAL.withDescription(e.toString()));
      }
      Map<String, AttributeValue> attributes = new HashMap<String, AttributeValue>();
      attributes.put("use", AttributeValue.stringAttributeValue("demo"));
      span.addAnnotation("OpenCensus: Invoking doWork", attributes);
    }
  }

  private static void doWork() {
    try (Scope scope =
        tracer
            .spanBuilder("OpenCensus: doWork")
            .setRecordEvents(true)
            .setSampler(Samplers.alwaysSample())
            .startScopedSpan()) {
      Span span = tracer.getCurrentSpan();
      try {
        System.out.println("doing busy work");
        Thread.sleep(100L);
      } catch (InterruptedException e) {
        span.setStatus(Status.INTERNAL.withDescription(e.toString()));
      }
      Map<String, AttributeValue> attributes = new HashMap<>();
      attributes.put("use", AttributeValue.stringAttributeValue("demo"));
      span.addAnnotation("OpenCensus: Invoking doWork", attributes);
    }
  }
}
