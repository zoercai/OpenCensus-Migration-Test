package library;

import io.opentelemetry.OpenTelemetry;
import io.opentelemetry.trace.Span;
import io.opentelemetry.trace.Tracer;

public class OpenTelemetryLibrary {

  private Tracer tracer = OpenTelemetry.getTracer("io.opentelemetry.example.TraceExporterExample2");

  private void myUseCase() {
    Span span = this.tracer.spanBuilder("OpenTelemetry-Library: Start my use case").startSpan();
    span.addEvent("OpenTelemetry-Library: Event 0");
    doWork();
    span.addEvent("OpenTelemetry-Library: Event 1");
    span.end();
  }

  private void doWork() {
    try {
      Thread.sleep((int) (Math.random() * 1000) + 1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void getCalled() {
    OpenTelemetryLibrary example = new OpenTelemetryLibrary();
    example.myUseCase();
  }
}
