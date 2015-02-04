package com.pentaho.model.metrics.contributor.metricManager;

import com.clearspring.analytics.stream.quantile.TDigest;
import com.pentaho.profiling.api.metrics.NVLOperation;

import java.util.Date;

/**
 * Created by bryan on 2/3/15.
 */
public final class NVLOperations {
  public static final NVLOperation<Double> DOUBLE_MIN = new DoubleMin();
  public static final NVLOperation<Double> DOUBLE_MAX = new DoubleMax();
  public static final NVLOperation<Double> DOUBLE_SUM = new DoubleSum();
  public static final NVLOperation<Long> LONG_MIN = new LongMin();
  public static final NVLOperation<Long> LONG_MAX = new LongMax();
  public static final NVLOperation<Long> LONG_SUM = new LongSum();
  public static final NVLOperation<Date> DATE_MIN = new DateMin();
  public static final NVLOperation<Date> DATE_MAX = new DateMax();
  public static final NVLOperation<TDigest> TDIGEST_MERGE = new TDigestMerge();

  /**
   * UNIT TEST ONLY
   */
  protected NVLOperations() {

  }

  private static class DoubleMin implements NVLOperation<Double> {
    @Override public Double perform( Double first, Double second ) {
      return Math.min( first, second );
    }
  }

  private static class DoubleMax implements NVLOperation<Double> {
    @Override public Double perform( Double first, Double second ) {
      return Math.max( first, second );
    }
  }

  private static class DoubleSum implements NVLOperation<Double> {
    @Override public Double perform( Double first, Double second ) {
      return first + second;
    }
  }

  private static class LongMin implements NVLOperation<Long> {
    @Override public Long perform( Long first, Long second ) {
      return Math.min( first, second );
    }
  }

  private static class LongMax implements NVLOperation<Long> {
    @Override public Long perform( Long first, Long second ) {
      return Math.max( first, second );
    }
  }

  private static class LongSum implements NVLOperation<Long> {
    @Override public Long perform( Long first, Long second ) {
      return first + second;
    }
  }

  private static class DateMin implements NVLOperation<Date> {
    @Override public Date perform( Date first, Date second ) {
      return new Date( Math.min( first.getTime(), second.getTime() ) );
    }
  }

  private static class DateMax implements NVLOperation<Date> {
    @Override public Date perform( Date first, Date second ) {
      return new Date( Math.max( first.getTime(), second.getTime() ) );
    }
  }

  private static class TDigestMerge implements NVLOperation<TDigest> {
    @Override public TDigest perform( TDigest first, TDigest second ) {
      first.add( second );
      return first;
    }
  }
}
