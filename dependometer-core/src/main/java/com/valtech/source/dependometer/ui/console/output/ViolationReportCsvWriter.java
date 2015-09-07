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

import java.io.IOException;
import java.io.PrintWriter;

import org.apache.log4j.Logger;

import com.valtech.source.dependometer.app.controller.project.HandleProjectInfoCollectedEventIf;
import com.valtech.source.dependometer.app.controller.project.ProjectInfoCollectedEvent;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf.CouplingIf;
import com.valtech.source.dependometer.ui.console.Dependometer;

/**
 * @author Klaus Mirschenz (klaus.mirschenz@valtech.de)
 */
public final class ViolationReportCsvWriter extends SingleFileWriter implements HandleProjectInfoCollectedEventIf
{
   private static final String SEPARATOR = ",";

   private static final String[] TITLES = {
      "From Layer" + SEPARATOR + "To Layer", "From Subsystem" + SEPARATOR + "To Subsystem",
      "From Package" + SEPARATOR + "To Package", "From Comp Unit" + SEPARATOR + "To Comp Unit",
      "From Type" + SEPARATOR + "To Type" };

   private static Logger s_Logger = Logger.getLogger(ViolationReportCsvWriter.class.getName());

   private ProjectIf m_Project;

   private String m_RequestedDetailLevel;

   // the level to expand the dependency tree to
   // (1 = show only layer violations, 2 = show layer + subsystem violations,
   // ...
   private int m_DetailLevel = 4; // default = compilation unit

   private String m_DetailLevelName;

   private int m_LineCounter = 0;

   public ViolationReportCsvWriter(String[] arguments) throws IOException
   {
      super(arguments);
      if (arguments.length > 2)
      {
         throw new IllegalArgumentException("may only process one mandatory and one additional optional argument");
      }

      if (arguments.length == 2)
      {
         m_RequestedDetailLevel = arguments[1];
         assert m_RequestedDetailLevel != null;
         assert m_RequestedDetailLevel.length() > 0;
      }

      Dependometer.getContext().getProjectManager().attach(this);
   }

   private void setDetailLevel()
   {
      assert m_Project != null;

      if (m_RequestedDetailLevel != null)
      {
         if (m_RequestedDetailLevel.equalsIgnoreCase(m_Project.getLayerElementName()))
         {
            m_DetailLevel = 1;
            m_DetailLevelName = m_Project.getLayerElementName();
         }
         if (m_RequestedDetailLevel.equalsIgnoreCase(m_Project.getLayerElementName()))
         {
            m_DetailLevel = 1;
            m_DetailLevelName = m_Project.getLayerElementName();
         }
         else if (m_RequestedDetailLevel.equalsIgnoreCase(m_Project.getSubsystemElementName()))
         {
            m_DetailLevel = 2;
            m_DetailLevelName = m_Project.getSubsystemElementName();
         }
         else if (m_RequestedDetailLevel.equalsIgnoreCase(m_Project.getPackageElementName()))
         {
            m_DetailLevel = 3;
            m_DetailLevelName = m_Project.getPackageElementName();
         }
         else if (m_RequestedDetailLevel.equalsIgnoreCase(m_Project.getCompilationUnitElementName()))
         {
            m_DetailLevel = 4;
            m_DetailLevelName = m_Project.getCompilationUnitElementName();
         }
         else if (m_RequestedDetailLevel.equalsIgnoreCase(m_Project.getTypeElementName()))
         {
            m_DetailLevel = 5;
            m_DetailLevelName = m_Project.getTypeElementName();
         }
      }
      if (m_DetailLevelName == null) // default
      {
         if (m_RequestedDetailLevel != null)
         {
            s_Logger.warn("detail level setting '" + m_RequestedDetailLevel + "' supported - using default!");
         }
         assert m_DetailLevel == 4;
         m_DetailLevelName = m_Project.getCompilationUnitElementName();
      }
   }

   private String getDetailLevelName()
   {
      assert m_DetailLevelName != null;
      assert m_DetailLevelName.length() > 0;
      return m_DetailLevelName;
   }

   public void handleEvent(ProjectInfoCollectedEvent event)
   {
      assert event != null;
      m_Project = event.getProject();
      PrintWriter writer = getWriter();
      setDetailLevel();

      writer.println("### " + getClass().getName() + " - " + getTimestamp() + " ###");
      writer.println();
      writer.println("# settings #");
      writer.println();
      writer.println("detail level = " + getDetailLevelName());
      writer.println();

      if (m_DetailLevel == 1)
      {
         DirectedDependencyIf[] layerDeps = m_Project.getForbiddenEfferentLayerDependencies();
         if (layerDeps.length > 0)
         {
            writeHeader();

            for (int i = 0; i < layerDeps.length; i++)
            {
               DependencyElementIf fromLayer = layerDeps[i].getFrom();
               DependencyElementIf toLayer = layerDeps[i].getTo();
               writer.println(fromLayer.getName() + SEPARATOR + toLayer.getName());
               m_LineCounter++;
            }

            writer.println();
         }
      }
      else
      {
         int startLevel = 2; // subsystems
         DirectedDependencyIf[] subDeps = m_Project.getForbiddenEfferentSubsystemDependencies();
         if (subDeps.length > 0)
         {
            writeHeader();

            for (int i = 0; i < subDeps.length; i++)
            {
               DependencyElementIf fromSub = subDeps[i].getFrom();
               DependencyElementIf toSub = subDeps[i].getTo();
               DependencyElementIf fromLayer = fromSub.belongsToDependencyElement();
               DependencyElementIf toLayer = toSub.belongsToDependencyElement();
               String prefix = fromLayer.getName() + SEPARATOR + toLayer.getName() + SEPARATOR + fromSub.getName()
                  + SEPARATOR + toSub.getName();
               writeForbiddenDependencies(writer, prefix, fromSub, toSub, startLevel);
            }

            writer.println();
         }
      }

      writer.println("# summary #");
      writer.println();
      writer.println("" + m_LineCounter + " violation(s) found");
      getLogger().info("writing violation report csv file ...");
      close();
   }

   private void writeHeader()
   {
      PrintWriter writer = getWriter();
      writer.println("# violations #");
      writer.println();

      for (int k = 0; k < m_DetailLevel; k++)
      {
         if (k > 0)
         {
            writer.print(SEPARATOR);
         }
         writer.print(TITLES[k]);
      }
      writer.println();
   }

   private void writeForbiddenDependencies(PrintWriter writer, String prefix, DependencyElementIf fromElement,
      DependencyElementIf toElement, int level)
   {
      assert level > 0;
      assert writer != null;
      assert prefix != null;
      assert fromElement != null;
      assert toElement != null;

      int currentlevel = level + 1;
      CouplingIf[] containedElementsFrom = fromElement.getEfferentCouplings(toElement);

      // end condition for recursion:
      // no more children or level is deeper than detail level
      if ((containedElementsFrom.length == 0) || (currentlevel > m_DetailLevel))
      {
         writer.println(prefix);
         m_LineCounter++;
         return;
      }

      for (int i = 0; i < containedElementsFrom.length; i++)
      {
         DependencyElementIf fromContainedElement = containedElementsFrom[i].getContained();
         DependencyElementIf[] toNotContainedElements = containedElementsFrom[i].getNotContainedRelatedElements();
         for (int j = 0; j < toNotContainedElements.length; j++)
         {
            if (fromContainedElement.isForbiddenEfferent(toNotContainedElements[j]))
            {
               String output = prefix + SEPARATOR + fromContainedElement.getName() + SEPARATOR
                  + toNotContainedElements[j].getName();
               writeForbiddenDependencies(writer, output, fromContainedElement, toNotContainedElements[j], currentlevel);
            }
         }
      }
   }
}