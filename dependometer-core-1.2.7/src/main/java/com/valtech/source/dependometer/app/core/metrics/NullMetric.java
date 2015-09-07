package com.valtech.source.dependometer.app.core.metrics;

import com.valtech.source.dependometer.app.core.common.MetricEnum;

/**
 * According to the Null Object pattern this can be used instead of null for <code>Metric</code>s that are not
 * available; getValueAsString() returns "n/a";
 * 
 * @author tobias.krause
 * 
 */
public class NullMetric extends Metric
{
   public NullMetric(MetricEnum metricEnum)
   {
      super(metricEnum);
   }

   @Override
   public String getValueAsString()
   {
      return "n/a";
   }

   @Override
   protected int compareToMetric(Metric metric)
   {
      return 0;
   }

}
