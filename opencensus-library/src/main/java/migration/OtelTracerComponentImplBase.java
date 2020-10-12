package migration;

import io.opencensus.common.Clock;
import io.opencensus.implcore.internal.EventQueue;
import io.opencensus.implcore.internal.SimpleEventQueue;
import io.opencensus.implcore.trace.RecordEventsSpanImpl.StartEndHandler;
import io.opencensus.implcore.trace.TracerImpl;
import io.opencensus.implcore.trace.config.TraceConfigImpl;
import io.opencensus.implcore.trace.export.ExportComponentImpl;
import io.opencensus.implcore.trace.internal.RandomHandler;
import io.opencensus.implcore.trace.propagation.PropagationComponentImpl;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.export.ExportComponent;
import io.opencensus.trace.propagation.PropagationComponent;

public final class OtelTracerComponentImplBase {
  private final ExportComponentImpl exportComponent;
  private final PropagationComponent propagationComponent = new PropagationComponentImpl();
  private final Clock clock;
  private final TraceConfig traceConfig = new TraceConfigImpl();
  private final Tracer tracer;

  /**
   * Creates a new {@code TraceComponentImplBase}.
   *
   * @param clock the clock to use throughout tracing.
   * @param randomHandler the random number generator for generating trace and span IDs.
   * @param eventQueue the queue implementation.
   */
  public OtelTracerComponentImplBase(Clock clock, RandomHandler randomHandler, EventQueue eventQueue) {
    this.clock = clock;
    if (eventQueue instanceof SimpleEventQueue) {
      exportComponent = ExportComponentImpl.createWithoutInProcessStores(eventQueue);
    } else {
      exportComponent = ExportComponentImpl.createWithInProcessStores(eventQueue);
    }
    StartEndHandler startEndHandler = new OtelStartEndHandler();
    tracer = new TracerImpl(randomHandler, startEndHandler, clock, traceConfig);
  }

  public Tracer getTracer() {
    return tracer;
  }

  public PropagationComponent getPropagationComponent() {
    return propagationComponent;
  }

  public final Clock getClock() {
    return clock;
  }

  public ExportComponent getExportComponent() {
    return exportComponent;
  }

  public TraceConfig getTraceConfig() {
    return traceConfig;
  }
}
