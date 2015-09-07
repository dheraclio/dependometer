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

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.provider.MetricDefinitionIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlMetricDescriptionsDocument extends HtmlDocument
{
   HtmlMetricDescriptionsDocument(File directory, MetricDefinitionIf[] metricDefinitions) throws IOException
   {
      super(directory);
      open("metric-descriptions" + HTML_FILE_EXTENSION);
      writeMetricDescriptions(metricDefinitions);
      close();
   }

   private void writeMetricDescriptions(MetricDefinitionIf[] metricDefinitions)
   {
      assert AssertionUtility.checkArray(metricDefinitions);
      PrintWriter writer = getWriter();
      for (int i = 0; i < metricDefinitions.length; i++)
      {
         MetricDefinitionIf next = metricDefinitions[i];
         String name = next.getName();
         writer.println("<a name=\"" + name + "\"/><h2>'" + name + "'</h2>");
         writer.println("<h3>description</h3>");
         writer.println("<p>");
         writer.println(transformDescription(next.getDescription()));
         writer.println("</p>");

         String[] relMetricNames = next.getRelatedMetricNames();
         if (relMetricNames.length > 0)
         {
            writer.println("<h3>related metrics</h3>");
            writer.println("<table>");
            for (int j = 0; j < relMetricNames.length; j++)
            {
               writer.println("<tr><td>");
               writeHRef(getFileName() + "#" + relMetricNames[j], "[" + relMetricNames[j] + "]");
               writer.println("</td></tr>");
            }
            writer.println("</table>");
         }
      }
   }

   private String transformDescription(String description)
   {
      assert description != null;
      description = description.trim();
      description = description.replaceAll("\n", "</br>");
      return description;
   }
}