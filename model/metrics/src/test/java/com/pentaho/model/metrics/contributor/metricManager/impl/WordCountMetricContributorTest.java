package com.pentaho.model.metrics.contributor.metricManager.impl;

import com.pentaho.profiling.api.metrics.MetricContributorUtils;
import com.pentaho.profiling.api.metrics.MetricMergeException;
import com.pentaho.profiling.api.metrics.field.DataSourceFieldValue;
import com.pentaho.profiling.api.metrics.field.DataSourceMetricManager;
import com.pentaho.profiling.api.action.ProfileActionException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mhall on 27/01/15.
 */
public class WordCountMetricContributorTest {

  @Test public void testUpdateTypeInitial() throws ProfileActionException {
    DataSourceMetricManager metricManager = new DataSourceMetricManager();
    metricManager.setValue( 1L, MetricContributorUtils.COUNT );
    String test = "a test string";
    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test );
    new WordCountMetricContributor().process( metricManager, dataSourceFieldValue );

    assertEquals( 3L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MIN ) );
    assertEquals( 3L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MAX ) );
    assertEquals( 3L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_SUM ) );
    assertEquals( 3d, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MEAN ) );
  }

  @Test public void testUpdateTypeWithMultiple() throws ProfileActionException {
    DataSourceMetricManager metricManager = new DataSourceMetricManager();
    metricManager.setValue( 3L, MetricContributorUtils.COUNT );
    String test1 = "a test string";
    String test2 = "second";
    String test3 = "another test string with stuff";

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test1 );

    new WordCountMetricContributor().process( metricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test2 );
    new WordCountMetricContributor().process( metricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test3 );
    new WordCountMetricContributor().process( metricManager, dataSourceFieldValue );

    assertEquals( 1L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MIN ) );
    assertEquals( 5L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MAX ) );
    assertEquals( 9L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_SUM ) );
    assertEquals( 3d, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MEAN ) );
  }

  @Test public void testMerge() throws ProfileActionException, MetricMergeException {
    DataSourceMetricManager metricManager = new DataSourceMetricManager();
    DataSourceMetricManager metricManager2 = new DataSourceMetricManager();
    metricManager.setValue( 2L, MetricContributorUtils.COUNT );
    metricManager2.setValue( 1L, MetricContributorUtils.COUNT );
    String test1 = "a test string";
    String test2 = "second";
    String test3 = "another test string with stuff";

    DataSourceFieldValue dataSourceFieldValue = new DataSourceFieldValue( test1 );

    WordCountMetricContributor wordCountMetricContributor = new WordCountMetricContributor();
    wordCountMetricContributor.process( metricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test2 );
    wordCountMetricContributor.process( metricManager, dataSourceFieldValue );
    dataSourceFieldValue.setFieldValue( test3 );
    wordCountMetricContributor.process( metricManager2, dataSourceFieldValue );
    metricManager.setValue( 3L, MetricContributorUtils.COUNT );
    wordCountMetricContributor.merge( metricManager, metricManager2 );

    assertEquals( 1L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MIN ) );
    assertEquals( 5L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MAX ) );
    assertEquals( 9L, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_SUM ) );
    assertEquals( 3d, metricManager.getValueNoDefault( WordCountMetricContributor.WORD_COUNT_KEY_MEAN ) );
  }

  @Test
  public void testGetTypes() {
    assertNotNull( new WordCountMetricContributor().getTypes() );
  }

  @Test
  public void testGetProfileFieldProperties() {
    assertNotNull( new WordCountMetricContributor().getProfileFieldProperties() );
  }

  @Test
  public void testClear() {
    DataSourceMetricManager dataSourceMetricManager = mock( DataSourceMetricManager.class );
    new WordCountMetricContributor().clear( dataSourceMetricManager );
    verify( dataSourceMetricManager ).clear( WordCountMetricContributor.CLEAR_LIST );
  }
}
