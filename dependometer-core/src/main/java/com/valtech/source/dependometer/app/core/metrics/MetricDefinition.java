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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;

import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.provider.MetricDefinitionIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class MetricDefinition implements MetricDefinitionIf
{
   private static Logger s_Logger = Logger.getLogger(MetricDefinition.class.getName());

   private final static String LINE_SEPARATOR = System.getProperty("line.separator");

   private final static Map<String, MetricDefinition> s_NameToMetricDefinition = new TreeMap<String, MetricDefinition>();

   private final int m_HashCode;

   private final String m_Name;

   private final String m_ValueType;

   private final String m_Description;

   private final String m_IndexType;

   private final List<String> m_RelatedMetricNames = new ArrayList<String>();

   static
   {
      MetricsReader reader = new MetricsReader();
      s_Logger.debug("Try to load metrics from metrics.xml with classloader from "
         + MetricDefinition.class.getPackage());
      InputStream input = MetricDefinition.class.getResourceAsStream("metrics.xml");
      if (input == null)
      {
         s_Logger.error("Loading metric definition failed");
      }
      assert input != null;
      try
      {
         MetricDefinitionData[] defData = reader.readMetrics(new InputSource(input));
         s_Logger.debug("Loading " + defData.length + " metrics from metrics.xml");
         for (int i = 0; i < defData.length; i++)
         {
            MetricDefinitionData next = defData[i];
            s_Logger.debug("Loading metric '" + next.getName() + "'");
            MetricDefinition created = createDefinition(next.getName(), next.getValueType(), next.getIndexType(), next
               .getDescription());
            String[] relatedMetricNames = next.getRelatedMetricName();
            for (int j = 0; j < relatedMetricNames.length; j++)
            {
               created.addRelatedMetricName(relatedMetricNames[j]);
            }
         }

         MetricDefinition[] allDefs = MetricDefinition.getMetricDefinitions();
         for (int i = 0; i < allDefs.length; i++)
         {
            allDefs[i].verifyRelatedMetrics();
         }
      }
      catch (IOException e)
      {
    	 s_Logger.fatal(e.getMessage());
         e.printStackTrace();
         assert false;
      }
   }

   private static MetricDefinition createDefinition(String name, String valueType, String indexType, String description)
   {
      assert name != null;
      assert name.length() > 0;
      assert !s_NameToMetricDefinition.containsKey(name);
      MetricDefinition def = new MetricDefinition(name, valueType, indexType, description);
      s_NameToMetricDefinition.put(name, def);
      return def;
   }

   private void verifyRelatedMetrics()
   {
      String[] relatedMetricNames = getRelatedMetricNames();
      for (int i = 0; i < relatedMetricNames.length; i++)
      {
         String next = relatedMetricNames[i];
         MetricDefinition def = getDefinition(MetricEnum.parseByDisplayName(next));
         assert def != null;
      }
   }

   public static MetricDefinition[] getMetricDefinitions()
   {
      return s_NameToMetricDefinition.values().toArray(new MetricDefinition[0]);
   }

   static MetricDefinition getDefinition(MetricEnum metric)
   {
      assert metric != null;
      String name=metric.getDisplayName();
      assert s_NameToMetricDefinition.containsKey(name) : "Undefined metric = " + name;
      return s_NameToMetricDefinition.get(name);
   }

   private MetricDefinition(String name, String valueType, String indexType, String description)
   {
      assert name != null;
      assert name.length() > 0;
      assert TYPE_NUMBER.equals(valueType) || TYPE_TEXT.equals(valueType);
      assert INDEX_NONE.equals(indexType) || INDEX.equals(indexType) || INDEX_ASCENDING.equals(indexType)
         || INDEX_DESCENDING.equals(indexType);
      assert description != null;

      m_Name = name;
      m_ValueType = valueType;
      m_IndexType = indexType;
      m_Description = description;
      m_HashCode = m_Name.hashCode();
   }

   void addRelatedMetricName(String name)
   {
      assert name != null;
      assert name.length() > 0;
      assert !m_RelatedMetricNames.contains(name);
      m_RelatedMetricNames.add(name);
   }

   public String[] getRelatedMetricNames()
   {
      return m_RelatedMetricNames.toArray(new String[0]);
   }

   public String getName()
   {
      return m_Name;
   }

   public String getValueType()
   {
      return m_ValueType;
   }

   public String getIndexType()
   {
      return m_IndexType;
   }

   public String getDescription()
   {
      return m_Description;
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
      else if (obj instanceof MetricDefinition)
      {
         if (m_Name.equals(((MetricDefinition)obj).m_Name))
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
      return m_HashCode;
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer(getClass().getName());

      buffer.append(LINE_SEPARATOR);
      buffer.append("Name = ");
      buffer.append(m_Name);
      buffer.append(LINE_SEPARATOR);
      buffer.append("Index type = ");
      buffer.append(m_IndexType);
      buffer.append(LINE_SEPARATOR);
      buffer.append("Value type = ");
      buffer.append(m_ValueType);
      buffer.append(LINE_SEPARATOR);
      buffer.append("Description = ");
      buffer.append(m_Description);

      return buffer.toString();
   }
}