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
final class DoubleMetric extends Metric
{
   private final Double m_Value;

   private final String m_ValueAsString;

   protected DoubleMetric(MetricEnum shortName, double value)
   {
      super(shortName);
      m_Value = new Double(value);
      m_ValueAsString = Double.toString(round(value));
   }

   public String getValueAsString()
   {
      return m_ValueAsString;
   }

   private double round(double toRound)
   {
      toRound = toRound * 100;
      double temp = (int)toRound;
      toRound = temp / 100.0;
      return toRound;
   }

   protected int compareToMetric(Metric metric)
   {
      assert metric != null;
      assert metric instanceof DoubleMetric;
      return m_Value.compareTo(((DoubleMetric)metric).m_Value);
   }
}
