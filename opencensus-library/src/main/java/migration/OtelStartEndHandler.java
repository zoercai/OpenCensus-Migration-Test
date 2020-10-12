package migration;

import static io.opentelemetry.trace.TracingContextUtils.currentContextWith;

import io.opencensus.common.Function;
import io.opencensus.implcore.trace.RecordEventsSpanImpl;
import io.opencensus.implcore.trace.RecordEventsSpanImpl.StartEndHandler;
import io.opencensus.trace.AttributeValue;
import io.opencensus.trace.export.SpanData;
import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.context.Scope;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Span.Builder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class OtelStartEndHandler implements StartEndHandler {

  private Map<RecordEventsSpanImpl, OtelSpanWithScope> openCensusToOtelMap = new HashMap<>();

  @Override
  public void onStart(RecordEventsSpanImpl span) {
    SpanData spanData = span.toSpanData();
    Builder builder =
        OpenTelemetry.getTracer("io.opentelemetry.example.TraceExporterExample")
            .spanBuilder(span.getName())
            .setStartTimestamp(
                (long)
                    (spanData.getStartTimestamp().getSeconds() * 1e9
                        + spanData.getStartTimestamp().getNanos()));
    if (spanData.getAttributes() != null) {
      for (Entry<String, AttributeValue> attribute :
          spanData.getAttributes().getAttributeMap().entrySet()) {
        builder.setAttribute(
            attribute.getKey(),
            attribute
                .getValue()
                .match(
                    stringAttributeConverter,
                    booleanAttributeConverter,
                    longAttributeConverter,
                    doubleAttributeConverter,
                    defaultAttributeConverter));
      }
    }
    Span otelSpan = builder.startSpan();
    Scope otelScope = currentContextWith(otelSpan);
    openCensusToOtelMap.put(span, new OtelSpanWithScope(otelSpan, otelScope));
  }

  @Override
  public void onEnd(RecordEventsSpanImpl span) {
    if (openCensusToOtelMap.containsKey(span)) {
      OtelSpanWithScope otelSpanWithScope = openCensusToOtelMap
          .get(span);
      otelSpanWithScope.otelScope.close();
      otelSpanWithScope.otelSpan
          .end(
              io.opentelemetry.trace.EndSpanOptions.builder()
                  .setEndTimestamp(span.getEndNanoTime())
                  .build());
    }
  }

  private static class OtelSpanWithScope {
    Span otelSpan;
    Scope otelScope;

    private OtelSpanWithScope (Span otelSpan, Scope otelScope) {
      this.otelSpan = otelSpan;
      this.otelScope = otelScope;
    }
  }

  private static final Function<? super String, io.opentelemetry.common.AttributeValue>
      stringAttributeConverter =
          (Function<String, io.opentelemetry.common.AttributeValue>)
              io.opentelemetry.common.AttributeValue::stringAttributeValue;

  private static final Function<? super Boolean, io.opentelemetry.common.AttributeValue>
      booleanAttributeConverter =
          (Function<Boolean, io.opentelemetry.common.AttributeValue>)
              io.opentelemetry.common.AttributeValue::booleanAttributeValue;

  private static final Function<? super Long, io.opentelemetry.common.AttributeValue>
      longAttributeConverter =
          (Function<Long, io.opentelemetry.common.AttributeValue>)
              io.opentelemetry.common.AttributeValue::longAttributeValue;

  private static final Function<? super Double, io.opentelemetry.common.AttributeValue>
      doubleAttributeConverter =
          (Function<Double, io.opentelemetry.common.AttributeValue>)
              io.opentelemetry.common.AttributeValue::doubleAttributeValue;

  private static final Function<Object, io.opentelemetry.common.AttributeValue>
      defaultAttributeConverter =
          value -> io.opentelemetry.common.AttributeValue.stringAttributeValue(value.toString());
}
