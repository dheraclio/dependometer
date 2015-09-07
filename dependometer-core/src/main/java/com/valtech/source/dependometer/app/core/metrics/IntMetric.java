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

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class IntMetric extends Metric
{
   private final Integer m_Value;

   private final String m_ValueAsString;

   protected IntMetric(MetricEnum shortName, int value)
   {
      super(shortName);
      m_Value = new Integer(value);
      m_ValueAsString = m_Value.toString();
   }

   public String getValueAsString()
   {
      return m_ValueAsString;
   }

   protected int compareToMetric(Metric metric)
   {
      assert metric != null;
      assert metric instanceof IntMetric;
      return m_Value.compareTo(((IntMetric)metric).m_Value);
   }
}
