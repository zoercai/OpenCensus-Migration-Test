package migration;

import io.opencensus.common.Clock;
import io.opencensus.impl.internal.DisruptorEventQueue;
import io.opencensus.impl.trace.internal.ThreadLocalRandomHandler;
import io.opencensus.implcore.common.MillisClock;
import io.opencensus.implcore.trace.TraceComponentImplBase;
import io.opencensus.trace.TraceComponent;
import io.opencensus.trace.Tracer;
import io.opencensus.trace.config.TraceConfig;
import io.opencensus.trace.export.ExportComponent;
import io.opencensus.trace.propagation.PropagationComponent;

/** Java 7 and 8 implementation of the {@link TraceComponent}. */
public final class TraceComponentImplOtel extends TraceComponent {
  private final TraceComponentImplBase traceComponentImplBase;

  /** Public constructor to be used with reflection loading. */
  public TraceComponentImplOtel() {
    traceComponentImplBase =
        new TraceComponentImplBase(
            MillisClock.getInstance(),
            new ThreadLocalRandomHandler(),
            DisruptorEventQueue.getInstance(),
            true
        );
  }

  @Override
  public Tracer getTracer() {
    return traceComponentImplBase.getTracer();
  }

  @Override
  public PropagationComponent getPropagationComponent() {
    return traceComponentImplBase.getPropagationComponent();
  }

  @Override
  public Clock getClock() {
    return traceComponentImplBase.getClock();
  }

  @Override
  public ExportComponent getExportComponent() {
    return traceComponentImplBase.getExportComponent();
  }

  @Override
  public TraceConfig getTraceConfig() {
    return traceComponentImplBase.getTraceConfig();
  }
}
