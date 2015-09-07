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

import com.valtech.source.dependometer.app.core.provider.ThresholdIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract class Threshold implements ThresholdIf
{
   private final String m_QueryId;

   private final double m_Threshold;

   private double m_Value;

   private boolean m_ValueSet;

   private boolean m_IsSupported;

   Threshold(String queryId, double threshold)
   {
      assert queryId != null;
      assert queryId.length() > 0;
      m_QueryId = queryId;
      m_Threshold = threshold;
   }

   final void isSupported(boolean isSupported)
   {
      m_IsSupported = isSupported;
   }

   public final boolean isSupported()
   {
      return m_IsSupported;
   }

   public final String getQueryId()
   {
      return m_QueryId;
   }

   public final double getThreshold()
   {
      return m_Threshold;
   }

   final void setValue(double value)
   {
      assert !m_ValueSet;
      m_Value = value;
      m_ValueSet = true;
   }

   private double round(double toRound)
   {
      toRound = toRound * 100;
      double temp = (int)toRound;
      toRound = temp / 100.0;
      return toRound;
   }

   public final double getValue()
   {
      assert m_ValueSet;
      return round(m_Value);
   }

   public final boolean wasAnalyzed()
   {
      return m_ValueSet;
   }

   public abstract boolean wasViolated();

   public abstract boolean isUpperThreshold();

   public abstract boolean isLowerThreshold();
}
