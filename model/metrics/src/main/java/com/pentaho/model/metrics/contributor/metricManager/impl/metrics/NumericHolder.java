package com.pentaho.model.metrics.contributor.metricManager.impl.metrics;

import com.pentaho.profiling.api.ValueTypeMetrics;
import com.pentaho.profiling.api.action.ProfileActionException;

/**
 * Created by bryan on 4/30/15.
 */
public class NumericHolder implements ValueTypeMetrics {
  private Double min;
  private Double max;
  private Double sum;
  private Double sumOfSquares;
  private Double mean;
  private Double variance;
  private Double stdDev;

  public NumericHolder() {
  }

  public NumericHolder( Double min, Double max, Double sum, Double sumOfSquares, Double mean, Double variance,
                        Double stdDev ) {
    this.min = min;
    this.max = max;
    this.sum = sum;
    this.sumOfSquares = sumOfSquares;
    this.mean = mean;
    this.variance = variance;
    this.stdDev = stdDev;
  }

  public void offer( Number numberValue ) {
    if ( numberValue == null ) {
      return;
    }
    double value = numberValue.doubleValue();
    if ( min == null ) {
      min = value;
    }
    if ( max == null ) {
      max = value;
    }
    if ( sum == null ) {
      sum = 0D;
    }
    if ( sumOfSquares == null ) {
      sumOfSquares = 0D;
    }
    min = Math.min( min, value );
    max = Math.max( max, value );
    sum = sum + value;
    sumOfSquares = sumOfSquares + ( value * value );
  }

  public void offer( NumericHolder otherHolder ) {
    if ( otherHolder.min != null ) {
      min = min == null ? otherHolder.min : Math.min( min, otherHolder.min );
    }
    if ( otherHolder.max != null ) {
      max = max == null ? otherHolder.max : Math.max( max, otherHolder.max );
    }
    if ( otherHolder.sum != null ) {
      sum = sum == null ? otherHolder.sum : sum + otherHolder.sum;
    }
    if ( otherHolder.sumOfSquares != null ) {
      sumOfSquares = sumOfSquares == null ? otherHolder.sumOfSquares : sumOfSquares + otherHolder.sumOfSquares;
    }
  }

  @Override public Object clone() {
    return new NumericHolder( min, max, sum, sumOfSquares, mean, variance, stdDev );
  }

  public void setDerived( long count ) throws ProfileActionException {
    if ( sum != null ) {
      mean = sum / count;
      if ( count > 1 ) {
        if ( sumOfSquares != null ) {
          variance = sumOfSquares - ( sum * sum ) / count;
          variance = variance / ( count - 1L );
        }
        Double stdDev = variance;
        if ( !Double.isNaN( stdDev ) ) {
          stdDev = Math.sqrt( stdDev );
          this.stdDev = stdDev;
        }
      }
    }
  }

  public Double getMin() {
    return min;
  }

  public void setMin( Double min ) {
    this.min = min;
  }

  public Double getMax() {
    return max;
  }

  public void setMax( Double max ) {
    this.max = max;
  }

  public Double getSum() {
    return sum;
  }

  public void setSum( Double sum ) {
    this.sum = sum;
  }

  public Double getSumOfSquares() {
    return sumOfSquares;
  }

  public void setSumOfSquares( Double sumOfSquares ) {
    this.sumOfSquares = sumOfSquares;
  }

  public Double getMean() {
    return mean;
  }

  public void setMean( Double mean ) {
    this.mean = mean;
  }

  public Double getVariance() {
    return variance;
  }

  public void setVariance( Double variance ) {
    this.variance = variance;
  }

  public Double getStdDev() {
    return stdDev;
  }

  public void setStdDev( Double stdDev ) {
    this.stdDev = stdDev;
  }
}
