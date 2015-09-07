/*
 * Copyright 2009 Valtech GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.valtech.source.dependometer.app.core.metrics;

import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.provider.MetricDefinitionIf;
import com.valtech.source.dependometer.app.core.provider.MetricIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract class Metric implements MetricIf
{
   private final MetricDefinition m_Definition;

   protected Metric(MetricEnum name)
   {
      m_Definition = MetricDefinition.getDefinition(name);
      assert m_Definition != null;
   }

   public final String getName()
   {
      return m_Definition.getName();
   }

   public final String getDescription()
   {
      return m_Definition.getDescription();
   }

   public abstract String getValueAsString();

   public final boolean hasIndex()
   {
      return !m_Definition.getIndexType().equals(MetricDefinitionIf.INDEX_NONE);
   }

   /**
    * Note: this class has a natural ordering that is inconsistent with equals.
    */
   public final int compareTo(MetricIf metricIf)
   {
      assert metricIf != null;
      assert metricIf instanceof Metric;

      String indexType = m_Definition.getIndexType();
      assert indexType != null;
      int result = 1;

      if (!indexType.equals(MetricDefinitionIf.INDEX_NONE) && !indexType.equals(MetricDefinitionIf.INDEX))
      {
         int tempResult = -2;
         if (this.getClass().equals(metricIf.getClass()))
         {
            tempResult = compareToMetric((Metric)metricIf);
         }
         else
         {
            tempResult = compareStringRepresentation((Metric)metricIf);
         }

         if (indexType.equals(MetricDefinitionIf.INDEX_DESCENDING))
         {
            tempResult *= -1;
         }
         if (tempResult != 0)
         {
            result = tempResult;
         }
      }

      return result;
   }

   protected abstract int compareToMetric(Metric metric);

   private int compareStringRepresentation(Metric metric)
   {
      assert metric != null;
      return getValueAsString().compareTo(metric.getValueAsString());
   }

   public final String toString()
   {
      StringBuffer buffer = new StringBuffer(getName());
      buffer.append(" = ");
      buffer.append(getValueAsString());
      return buffer.toString();
   }
}