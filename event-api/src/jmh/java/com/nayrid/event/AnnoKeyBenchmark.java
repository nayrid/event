package com.nayrid.event;

import com.nayrid.event.testdata.CountingEvent;
import com.nayrid.event.annotation.AnnotationUtil;
import java.util.concurrent.TimeUnit;
import org.jspecify.annotations.NullMarked;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@NullMarked
@State(Scope.Benchmark)
public class AnnoKeyBenchmark {

    @Setup(Level.Iteration)
    public void setup() {
        AnnotationUtil.clearCache();
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    public void benchmarkAnnotationUtilKey() {
        AnnotationUtil.key(CountingEvent.class);
    }

}
