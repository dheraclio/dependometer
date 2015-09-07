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
package com.valtech.source.dependometer.app.configprovider.filebased.xml;

import com.valtech.source.dependometer.app.core.provider.ThresholdDefinitionIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class ThresholdNode implements ThresholdDefinitionIf
{
   private final String m_Id;

   private final double m_Value;

   public ThresholdNode(String id, String value)
   {
      assert id != null;
      assert id.length() > 0;
      assert value != null;
      assert value.length() > 0;
      m_Id = id;
      m_Value = Double.parseDouble(value);
   }

   public String getQueryId()
   {
      return m_Id;
   }

   public double getThreshold()
   {
      return m_Value;
   }

   public boolean equals(Object obj)
   {
      if (obj == null)
      {
         return false;
      }
      else if (obj == this)
      {
         return true;
      }
      else if (obj instanceof ThresholdNode)
      {
         if (getQueryId().equals(((ThresholdNode)obj).getQueryId()))
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   public int hashCode()
   {
      return m_Id.hashCode();
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer(getClass().getName());
      buffer.append(" # ");
      buffer.append(m_Id);
      buffer.append('/');
      buffer.append(m_Value);
      return buffer.toString();
   }
}
