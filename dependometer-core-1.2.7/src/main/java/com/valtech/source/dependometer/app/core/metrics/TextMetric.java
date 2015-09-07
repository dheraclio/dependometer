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
final class TextMetric extends Metric
{
   private final String m_Value;

   protected TextMetric(MetricEnum shortName, String value)
   {
      super(shortName);
      assert value != null;
      assert value.length() > 0;
      m_Value = value;
   }

   public String getValueAsString()
   {
      return m_Value;
   }

   protected int compareToMetric(Metric metric)
   {
      assert metric != null;
      assert metric instanceof TextMetric;
      return m_Value.compareTo(((TextMetric)metric).m_Value);
   }
}
