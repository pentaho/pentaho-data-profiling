/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2015 Pentaho Corporation (Pentaho). All rights reserved.
 *
 * NOTICE: All information including source code contained herein is, and
 * remains the sole property of Pentaho and its licensors. The intellectual
 * and technical concepts contained herein are proprietary and confidential
 * to, and are trade secrets of Pentaho and may be covered by U.S. and foreign
 * patents, or patents in process, and are protected by trade secret and
 * copyright laws. The receipt or possession of this source code and/or related
 * information does not convey or imply any rights to reproduce, disclose or
 * distribute its contents, or to manufacture, use, or sell anything that it
 * may describe, in whole or in part. Any reproduction, modification, distribution,
 * or public display of this information without the express written authorization
 * from Pentaho is strictly prohibited and in violation of applicable laws and
 * international treaties. Access to the source code contained herein is strictly
 * prohibited to anyone except those individuals and entities who have executed
 * confidentiality and non-disclosure agreements or other agreements with Pentaho,
 * explicitly covering such access.
 */

package com.pentaho.model.metrics.contributor.metricManager;

import com.clearspring.analytics.stream.quantile.TDigest;
import com.pentaho.profiling.api.metrics.NVLOperation;

import java.util.Date;

/**
 * Created by bryan on 2/3/15.
 */
public final class NVLOperations {
  public static final NVLOperation<Number> DOUBLE_MIN = new DoubleMin();
  public static final NVLOperation<Number> DOUBLE_MAX = new DoubleMax();
  public static final NVLOperation<Number> DOUBLE_SUM = new DoubleSum();
  public static final NVLOperation<Number> LONG_MIN = new LongMin();
  public static final NVLOperation<Number> LONG_MAX = new LongMax();
  public static final NVLOperation<Number> LONG_SUM = new LongSum();
  public static final NVLOperation<Date> DATE_MIN = new DateMin();
  public static final NVLOperation<Date> DATE_MAX = new DateMax();
  public static final NVLOperation<TDigest> TDIGEST_MERGE = new TDigestMerge();

  /**
   * UNIT TEST ONLY
   */
  protected NVLOperations() {

  }

  private static class DoubleMin implements NVLOperation<Number> {
    @Override public Double perform( Number first, Number second ) {
      return Math.min( first.doubleValue(), second.doubleValue() );
    }
  }

  private static class DoubleMax implements NVLOperation<Number> {
    @Override public Double perform( Number first, Number second ) {
      return Math.max( first.doubleValue(), second.doubleValue() );
    }
  }

  private static class DoubleSum implements NVLOperation<Number> {
    @Override public Double perform( Number first, Number second ) {
      return first.doubleValue() + second.doubleValue();
    }
  }

  private static class LongMin implements NVLOperation<Number> {
    @Override public Long perform( Number first, Number second ) {
      return Math.min( first.longValue(), second.longValue() );
    }
  }

  private static class LongMax implements NVLOperation<Number> {
    @Override public Long perform( Number first, Number second ) {
      return Math.max( first.longValue(), second.longValue() );
    }
  }

  private static class LongSum implements NVLOperation<Number> {
    @Override public Long perform( Number first, Number second ) {
      return first.longValue() + second.longValue();
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
