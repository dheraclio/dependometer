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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class MetricDefinitionData
{
   private final List<String> m_RelatedMetricNames = new ArrayList<String>();

   private String m_Name;

   private String m_ValueType;

   private String m_Description;

   private String m_IndexType;

   String getDescription()
   {
      return m_Description;
   }

   void appendDescription(String description)
   {
      assert description != null;

      if (description.length() > 0)
      {
         if (m_Description == null)
         {
            m_Description = description;
         }
         else
         {
            m_Description = m_Description + description;
         }
      }
   }

   String getIndexType()
   {
      return m_IndexType;
   }

   void setIndexType(String indexType)
   {
      m_IndexType = indexType;
   }

   String getName()
   {
      return m_Name;
   }

   void setName(String name)
   {
      m_Name = name;
   }

   String getValueType()
   {
      return m_ValueType;
   }

   void setValueType(String valueType)
   {
      m_ValueType = valueType;
   }

   void addRelatedMetricName(String name)
   {
      assert name != null;
      assert name.length() > 0;
      assert !m_RelatedMetricNames.contains(name);
      m_RelatedMetricNames.add(name);
   }

   String[] getRelatedMetricName()
   {
      return m_RelatedMetricNames.toArray(new String[0]);
   }
}