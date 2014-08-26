/*!
 * PENTAHO CORPORATION PROPRIETARY AND CONFIDENTIAL
 *
 * Copyright 2002 - 2014 Pentaho Corporation (Pentaho). All rights reserved.
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

package com.pentaho.profiling.api.stats;

import java.util.Map;

/**
 * Interface to something that can compute a derived statistic given the values from a set of producers
 * 
 * @author bryan
 * @author Mark Hall (mhall{[at]}pentaho{[dot]}com)
 */
public interface ValueCalculator extends ValueProducer {

  /**
   * Compute the derived statistic given the map of base stats
   * 
   * @param producerMap
   *          the base stat producers required
   * @throws CalculationException
   *           if the calculation cannot be performed
   */
  public void process( Map<String, ValueProducer> producerMap ) throws CalculationException;
}
