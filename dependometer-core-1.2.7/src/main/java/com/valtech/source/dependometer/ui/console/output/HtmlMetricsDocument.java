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
package com.valtech.source.dependometer.ui.console.output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.MetricIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlMetricsDocument extends HtmlDocument
{
   private final static class Entry implements Comparable<Entry>
   {
      private final MetricIf metric;

      private final DependencyElementIf element;

      Entry(MetricIf metric, DependencyElementIf element)
      {
         assert metric != null;
         assert element != null;
         this.metric = metric;
         this.element = element;
      }

      public int compareTo(Entry entry)
      {
         assert entry != null;

         return metric.compareTo(entry.metric );
      }

      @Override
      public boolean equals( Object o ) {
          if( this == o ) return true;
          if( !( o instanceof Entry ) ) return false;

          Entry entry = (Entry) o;
          if( metric != null ? !metric.equals( entry.metric ) : entry.metric != null ) return false;
          return true;
      }

      @Override
      public int hashCode() {
          return metric != null ? metric.hashCode() : 0;
      }

      DependencyElementIf getDependencyElement()
      {
         return element;
      }

      public String getMetricValueAsString()
      {
         return metric.getValueAsString();
      }
   }

   private final String type;

   private final DependencyElementIf[] elements;

   private final String metricDescriptionsDocumentName;

   private final Map<String, Set<Entry>> metricNameToEntries = new TreeMap<String, Set<Entry>>();

   HtmlMetricsDocument(File directory, String typeName, DependencyElementIf[] elements,
      String metricDescriptionsDocumentName) throws IOException
   {
      super(directory);
      assert typeName != null;
      assert typeName.length() > 0;
      assert AssertionUtility.checkArray(elements);
      assert metricDescriptionsDocumentName != null;
      assert metricDescriptionsDocumentName.length() > 0;

      type = typeName;
      this.elements = Arrays.copyOf( elements, elements.length );
      this.metricDescriptionsDocumentName = metricDescriptionsDocumentName;

      for (int i = 0; i < this.elements.length; i++)
      {
         DependencyElementIf nextElement = this.elements[i];
         MetricIf[] nextMetrics = nextElement.getMetrics();
         for (int j = 0; j < nextMetrics.length; j++)
         {
            MetricIf nextMetric = nextMetrics[j];
            if (nextMetric.hasIndex())
            {
               String metricName = nextMetric.getName();
               Set<Entry> entries = metricNameToEntries.get(metricName);
               if (entries == null)
               {
                  entries = new TreeSet<Entry>();
                  metricNameToEntries.put( metricName, entries );
               }

               entries.add(new Entry(nextMetric, nextElement));
            }
         }
      }

      open(typeName + "-metrics" + HTML_FILE_EXTENSION);
      writeTitle();
      writeLinks();
      writeMetricIndexes();
      close();
   }

   private void writeLinks()
   {
      Iterator metricNameIter = metricNameToEntries.keySet().iterator();
      PrintWriter writer = getWriter();
      writer.println("<table>");
      while (metricNameIter.hasNext())
      {
         String metricName = (String)metricNameIter.next();
         writer.println("<tr>");
         writer.println("<td>");
         writeHRef(getFileName() + "#" + metricName, "[" + metricName + "]");
         writer.println("</td>");
         writer.println("<td>");
         writeHRef( metricDescriptionsDocumentName + "#" + metricName, "[description]");
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");
   }

   private void writeMetricIndexes()
   {
      PrintWriter writer = getWriter();
      Iterator metricNameIter = metricNameToEntries.keySet().iterator();
      while (metricNameIter.hasNext())
      {
         String metricName = (String)metricNameIter.next();
         Entry[] entries = metricNameToEntries.get(metricName).toArray(new Entry[0]);
         writer.println("<a name=\"" + metricName + "\"/><h3>'" + metricName + "' for " + type + "s ("
            + entries.length + ") " + getHRef( metricDescriptionsDocumentName + "#" + metricName, "[description]")
            + "</h3>");

         writer.println("<table>");
         for (int i = 0; i < entries.length; i++)
         {
            Entry nextEntry = entries[i];
            DependencyElementIf nextElement = nextEntry.getDependencyElement();
            String metricAsString = nextEntry.getMetricValueAsString();
            writer.println("<tr>");
            writer.println("<td>");
            writeHRef(nextElement.hashCode() + ".html#element", nextElement.getFullyQualifiedName());
            writer.println("</td>");
            writer.println("<td>");
            writer.println("=");
            writer.println("</td>");
            writer.println("<td>");
            writer.println(metricAsString);
            writer.println("</td>");
            writer.println("</tr>");
         }

         writer.println("</table>");
      }
   }

   private void writeTitle()
   {
      writeAnchoredTitle("index", type + " metrics index");
   }
}