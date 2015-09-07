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
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.SubsystemFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
class HtmlVerticalSlicesInfoDocument extends HtmlDocument
{
   private final ProjectIf m_Project;

   private final DependencyElementIf[] m_VerticalSlices;

   private final DependencyElementIf[] m_Layers;

   private final SubsystemFilterIf m_Filter;

   protected HtmlVerticalSlicesInfoDocument(File directory, ProjectIf project) throws IOException
   {
      super(directory);
      assert project != null;

      m_Project = project;
      m_VerticalSlices = project.getVerticalSlices();
      m_Layers = project.getRelevantLayersForVerticalSlices();
      m_Filter = ProviderFactory.getInstance().getConfigurationProvider().getSubsystemFilter();

      assert AssertionUtility.checkArray(m_VerticalSlices);
      assert AssertionUtility.checkArray(m_Layers);
      assert m_Filter != null;

      open(m_Project.getVerticalSliceElementName() + "s-info" + HTML_FILE_EXTENSION);
      writeTitle();
      writeFilter();
      writeVerticalSlicesOverview();
   }

   private void writeFilter()
   {
      PrintWriter writer = getWriter();

      String[] excludePatterns = m_Filter.getExcludePatterns();
      String[] skippedSubsystems = m_Filter.getSkippedSubsystems();

      writer.println("<a name=\"filter\"/><h3>" + m_Project.getVerticalSliceElementName() + "s filter</h3>");
      writer.println("<table>");

      for (int i = 0; i < excludePatterns.length; i++)
      {
         writer.println("<tr><td>");
         writer.print("exclude pattern = " + excludePatterns[i]);
         writer.println("</td></tr>");
      }

      writer.println("<tr><td>");
      writer.println("skipped subsystems (" + skippedSubsystems.length + ")");
      writer.println("</td></tr>");
      for (int i = 0; i < skippedSubsystems.length; i++)
      {
         writer.println("<tr><td>");
         writer.print("-- " + skippedSubsystems[i]);
         writer.println("</td></tr>");
      }

      writer.println("</table>");
   }

   private void writeVerticalSlicesOverview()
   {
      PrintWriter writer = getWriter();
      writer.println("<a name=\"overview\"/><h3>" + m_Project.getVerticalSliceElementName() + "s overview</h3>");
      writer.println("<table>");

      writer.println("<tr>");
      writer.println("<td>");
      writer.println(m_Project.getVerticalSliceElementName() + "s");
      writer.println("</td>");
      for (int i = 0; i < m_VerticalSlices.length; i++)
      {
         writer.println("<td>");
         DependencyElementIf next = m_VerticalSlices[i];
         writeHRef(next.hashCode() + ".html#element", next.getFullyQualifiedName());
         writer.println("</td>");
      }
      writer.println("</tr>");

      writer.println("<tr>");
      writer.println("<td>");
      writer.println(m_Project.getLayerElementName() + "s");
      writer.println("</td>");
      writer.println("</tr>");

      for (int i = 0; i < m_Layers.length; i++)
      {
         DependencyElementIf nextLayer = m_Layers[i];
         writer.println("<tr>");
         writer.println("<td>");
         writeHRef(nextLayer.hashCode() + ".html#element", nextLayer.getFullyQualifiedName());
         writer.println("</td>");
         for (int j = 0; j < m_VerticalSlices.length; j++)
         {
            writer.println("<td><p align=\"center\">");
            DependencyElementIf subsystemOnLayer = hasPortionOnLayer(m_VerticalSlices[j], nextLayer);
            if (subsystemOnLayer != null)
            {
               String link = null;
               if (subsystemOnLayer.hasProjectInternalTypes())
               {
                  link = "x";
               }
               else
               {
                  link = "x(!)";
               }

               writeHRef(subsystemOnLayer.hashCode() + ".html#element", link);
            }
            else
            {
               writer.println("-");
            }
            writer.println("</td>");
         }

         writer.println("</tr>");
      }
      writer.println("</table>");
   }

   private DependencyElementIf hasPortionOnLayer(DependencyElementIf verticalSlice, DependencyElementIf layer)
   {
      assert verticalSlice != null;
      assert layer != null;

      DependencyElementIf[] subsystems = verticalSlice.containsDependencyElements();
      for (int i = 0; i < subsystems.length; i++)
      {
         if (layer.equals(subsystems[i].belongsToDependencyElement()))
         {
            return subsystems[i];
         }
      }
      return null;
   }

   private void writeTitle()
   {
      writeAnchoredTitle("info", m_Project.getVerticalSliceElementName() + "s info");
   }
}