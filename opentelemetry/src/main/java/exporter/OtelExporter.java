package exporter;

import io.opencensus.trace.Annotation;
import io.opencensus.trace.SpanContext;
import io.opencensus.trace.SpanId;
import io.opencensus.trace.Tracing;
import io.opencensus.trace.export.SpanData;
import io.opencensus.trace.export.SpanData.TimedEvent;
import io.opencensus.trace.export.SpanExporter;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import library.OpenTelemetryLibrary;

public class OtelExporter extends SpanExporter.Handler {

  private Tracer tracer = OpenTelemetry.getTracer("io.opentelemetry.opencensus.OtelExporter");


  @Override
  public void export(Collection<SpanData> spanDataList) {
    List<SpanData> queue = new LinkedList<>();
    Map<SpanId, Span> spanIdToSpan = new HashMap<>();

    for (SpanData sd : spanDataList) {
      SpanContext sc = sd.getContext();

//      System.out.printf(
//          "Name: %s\nTraceID: %s\nSpanID: %s\nParentSpanID: %s\nStartTime: %d\nEndTime: %d\nAnnotations:\n",
//          sd.getName(), sc.getTraceId(), sc.getSpanId(), sd.getParentSpanId(), sd.getStartTimestamp().getSeconds(),
//          sd.getEndTimestamp().getSeconds());

      if (sd.getParentSpanId() == null) {
        Span span = this.tracer.spanBuilder(sd.getName()).setStartTimestamp(
            (long) (sd.getStartTimestamp().getSeconds() * 1e9 + sd.getStartTimestamp().getNanos())).startSpan();

        for (TimedEvent<Annotation> event : sd.getAnnotations().getEvents()) {
          System.out.println(event.getEvent().getDescription());
          span.addEvent(event.getEvent().getDescription());
        }
        spanIdToSpan.put(sc.getSpanId(), span);
        span.end();
      } else {
        queue.add(sd);
      }
    }

    while (!queue.isEmpty()) {
      SpanData sd = queue.remove(0);

      if (spanIdToSpan.containsKey(sd.getParentSpanId())) {
        SpanContext sc = sd.getContext();
        Span span = this.tracer.spanBuilder(sd.getName()).setStartTimestamp(
            (long) (sd.getStartTimestamp().getSeconds() * 1e9 + sd.getStartTimestamp().getNanos())).setParent(spanIdToSpan.get(sd.getParentSpanId())).startSpan();
        for (TimedEvent<Annotation> event : sd.getAnnotations().getEvents()) {
          System.out.println(event.getEvent().getDescription());
          span.addEvent(event.getEvent().getDescription());
        }
        spanIdToSpan.put(sc.getSpanId(), span);
        span.end();
      } else {
        queue.add(sd);
      }
    }
  }

  public static void createAndRegister() {
    // Please remember to register your exporter
    // so that it can receive exportered spanData.
    Tracing.getExportComponent().getSpanExporter().registerHandler(OtelExporter.class.getName(), new OtelExporter());
  }
}
