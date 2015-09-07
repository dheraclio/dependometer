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
package com.valtech.source.dependometer.app.core.queryinfo;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum;
import com.valtech.source.dependometer.app.core.provider.ThresholdDefinitionIf;
import com.valtech.source.dependometer.app.core.provider.ThresholdIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class ThresholdManager
{
   private final Map<String, Threshold> m_Thresholds = new TreeMap<String, Threshold>();

   public ThresholdIf[] getThresholds()
   {
      Iterator iter = m_Thresholds.keySet().iterator();
      while (iter.hasNext())
      {
         String id = (String)iter.next();
         Threshold threshold = m_Thresholds.get(id);
         ProjectMetricsEnum metric = ProjectMetricsEnum.getById(id);
         if (metric != null)
         {
            threshold.isSupported(true);
            if (metric.wasValueSet())
            {
               threshold.setValue(metric.getValue());
            }
         }
         else
         {
            threshold.isSupported(false);
         }
      }

      return m_Thresholds.values().toArray(new ThresholdIf[0]);
   }

   public void createLowerThresholds(ThresholdDefinitionIf[] lower)
   {
      assert AssertionUtility.checkArray(lower);

      for (int i = 0; i < lower.length; ++i)
      {
         ThresholdDefinitionIf next = lower[i];
         assert !m_Thresholds.containsKey(next.getQueryId());
         m_Thresholds.put(next.getQueryId(), new LowerThreshold(next.getQueryId(), next.getThreshold()));
      }
   }

   public void createUpperThresholds(ThresholdDefinitionIf[] upper)
   {
      assert AssertionUtility.checkArray(upper);

      for (int i = 0; i < upper.length; ++i)
      {
         ThresholdDefinitionIf next = upper[i];
         assert !m_Thresholds.containsKey(next.getQueryId());
         m_Thresholds.put(next.getQueryId(), new UpperThreshold(next.getQueryId(), next.getThreshold()));
      }
   }
}