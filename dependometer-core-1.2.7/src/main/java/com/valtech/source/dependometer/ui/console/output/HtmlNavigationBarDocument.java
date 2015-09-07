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
import java.util.Date;

import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.ThresholdIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlNavigationBarDocument extends HtmlDocument
{
   private static String FILE_NAME = "navigation-bar" + HTML_FILE_EXTENSION;

   private final ProjectIf m_Project;

   private final ConfigurationProviderIf m_ConfigurationProvider;

   private int m_LayerCycles;

   private int m_VerticalSliceCycles;

   private int m_SubsystemCycles;

   private int m_PackageCycles;

   private int m_CompilationUnitCycles;

   private int m_TypeCycles;

   private int m_LayerTangles;

   private int m_VerticalSliceTangles;

   private int m_SubsystemTangles;

   private int m_PackageTangles;

   private int m_CompilationUnitTangles;

   private int m_TypeTangles;

   private int m_LayerLevels = -1;

   private int m_SubsystemLevels = -1;

   private int m_PackageLevels = -1;

   private int m_CompilationUnitLevels = -1;

   private int m_TypeLevels = -1;

   private int m_VerticalSliceLevels = -1;

   private volatile boolean m_WritePending = false; // another thread currently writes

   private volatile long m_LastTime = 0;

   protected HtmlNavigationBarDocument(File directory, ProjectIf project)
   {
      super(directory);
      assert project != null;
      m_Project = project;
      m_ConfigurationProvider = ProviderFactory.getInstance().getConfigurationProvider();
   }

   void setCycleNumbers(int layer, int verticalSlice, int subsystem, int pack, int compilationUnit, int type)
   {
      m_LayerCycles = layer;
      m_VerticalSliceCycles = verticalSlice;
      m_SubsystemCycles = subsystem;
      m_PackageCycles = pack;
      m_CompilationUnitCycles = compilationUnit;
      m_TypeCycles = type;
   }

   void setLevelizationNumbers(int layer, int verticalSlice, int subsystem, int pack, int compilationUnit, int type)
   {
      m_LayerLevels = layer;
      m_VerticalSliceLevels = verticalSlice;
      m_SubsystemLevels = subsystem;
      m_PackageLevels = pack;
      m_CompilationUnitLevels = compilationUnit;
      m_TypeLevels = type;
   }

   private void writeProjectNavigation()
   {
      PrintWriter writer = getWriter();
      writer.write("<tr><td>");
      writeAnchoredTitle("project", "&lt;&lt;" + m_Project.getFullyQualifiedName() + "&gt;&gt;");
      writer.write("</td></tr>");
      writer.write("<tr><td>");
      writeHRefWithTarget("project.html#project", "info", "details");
      writer.write("<tr><td>");
      writeHRefWithTarget("project.html#metrics", "metrics", "details");
      writer.write("</td></tr>");

      ThresholdIf[] thresholds = m_Project.getThresholds();
      boolean haveALook = false;
      boolean violation = false;
      for (int i = 0; i < thresholds.length; i++)
      {
         ThresholdIf next = thresholds[i];
         if (!next.isSupported() || (next.isSupported() && !next.wasAnalyzed()))
         {
            haveALook = true;
         }
         else if (next.wasViolated())
         {
            violation = true;
         }
      }

      StringBuffer info = new StringBuffer();
      if (violation || haveALook)
      {
         info.append(" (");
         if (violation)
         {
            info.append('!');
         }
         if (haveALook)
         {
            if (violation)
            {
               info.append('/');
            }
            info.append('?');
         }
         info.append(')');
      }

      writer.write("<tr><td>");
      writeHRefWithTarget("project.html#thresholds", "thresholds " + info, "details");
      writer.write("</td></tr>");
      writer.write("<tr><td>");
      writeHRefWithTarget("project.html#filter", "filter", "details");
      writer.write("</td></tr>");
      writer.write("<tr><td>");
      writeHRefWithTarget("project.html#manipulation", "manipulated dependencies", "details");
      writer.write("</td></tr>");
      writer.write("<tr><td>");
      writeHRefWithTarget("project.html#configuration-provider", "configuration provider", "details");
      writer.write("</td></tr>");
      writer.write("<tr><td>");
      writeHRefWithTarget("project.html#type-definition-provider", "type definition provider", "details");
      writer.write("</td></tr>");
      writer.write("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
   }

   private void writeVerticalSliceNavigation()
   {
      String elementName = m_Project.getVerticalSliceElementName();
      PrintWriter writer = getWriter();
      writer.write("<tr><td>");
      writeAnchoredTitle(elementName, elementName + "s (" + m_Project.getNumberOfVerticalSlices() + "/"
         + m_ConfigurationProvider.getSubsystemFilter().getSkippedSubsystems().length + ")");
      writer.write("</td></tr>");
      writer.println("<tr><td>");
      writeHRefWithTarget("index-vertical-slices.html#all", "all", "details");
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writeHRefWithTarget("vertical-slices-info.html#filter", "filter", "details");
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writeHRefWithTarget("vertical-slices-info.html#overview", "overview", "details");
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.checkSubsystemDependencies()
         && m_ConfigurationProvider.checkVerticalSliceDependencies())
      {
         writer.println("<tr><td>");
         writeHRefWithTarget("vertical-slice-violations.html#violations", "violations ("
            + m_Project.getNumberOfForbiddenEfferentVerticalSliceDependencies() + ")", "details");
         writer.println("</td></tr>");
      }
      else
      {
         writer.println("<tr><td>");
         writer.println("dependency check disabled");
         writer.println("</td></tr>");
      }

      if (m_ConfigurationProvider.getMaxVerticalSliceCycles() >= -1)
      {
         if (m_Project.existVerticalSliceCycles())
         {
            writer.println("<tr><td>");
            if (m_ConfigurationProvider.getMaxVerticalSliceCycles() >= 0)
            {
               writeHRefWithTarget("vertical-slice-cycles.html#cycles", "cycles exist ("
                  + (m_VerticalSliceCycles == 0 ? "?" : Integer.toString(m_VerticalSliceCycles)) + ")", "details");
               writer.println("</td></tr>");
               writer.println("<tr><td>");
               writeHRefWithTarget("vertical-slice-cycle-participation.html#participation", "cycle participation",
                  "details");
            }
            else
            {
               writer.println("cycles exist");
            }
            writer.println("</td></tr>");

            // tangles
            writer.println("<tr><td>");
            writeHRefWithTarget("vertical-slice-tangles.html#cycles", "tangle score ("
                  + Integer.toString(m_VerticalSliceTangles) + ")", "details");
            writer.println("</td></tr>");
         }
         if (m_ConfigurationProvider.getMaxVerticalSliceCycles() >= 0)
         {
            writer.println("<tr><td>");
            if (m_VerticalSliceLevels > 0)
            {
               writeHRefWithTarget("vertical-slice-levels.html#levels", "levelization ("
                  + (getLevelAsString(m_VerticalSliceLevels)) + ")", "details");
            }
            else
            {
               writer.println("levelization (" + (getLevelAsString(m_VerticalSliceLevels)) + ")");
            }
            writer.println("</td></tr>");
         }

         writer.println("<tr><td>");
         writeHRefWithTarget("vertical-slice-metrics.html#index", "metrics", "details");
         writer.println("</td></tr>");
      }
      else
      {
         writer.println("<tr><td>");
         writer.println("cycle detection disabled");
         writer.println("</td></tr>");
      }

      writer.write("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
   }

   private void writeLayerNavigation()
   {
      String elementName = m_Project.getLayerElementName();
      PrintWriter writer = getWriter();
      writer.write("<tr><td>");
      writeAnchoredTitle(elementName, elementName + "s (" + m_Project.getNumberOfProjectInternalLayers() + "/"
         + m_Project.getNumberOfProjectExternalLayers() + ")");
      writer.write("</td></tr>");
      writer.println("<tr><td>");
      writeHRefWithTarget("index-layers.html#all", "all", "details");
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writeHRefWithTarget("layer-violations.html#violations", "violations ("
         + m_Project.getNumberOfForbiddenEfferentLayerDependencies() + ")", "details");
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.checkLayerDependencies())
      {
         writer.println("<tr><td>");
         writeHRefWithTarget("layer-unused-dependencies.html#unused", "unused defined dependencies ("
            + m_Project.getNumberOfUnusedDefinedEfferentLayerDependencies() + ")", "details");
         writer.println("</td></tr>");
      }

      if (m_ConfigurationProvider.getMaxLayerCycles() >= -1)
      {
         if (m_Project.existLayerCycles())
         {
            writer.println("<tr><td>");
            if (m_ConfigurationProvider.getMaxLayerCycles() >= 0)
            {
               writeHRefWithTarget("layer-cycles.html#cycles", "cycles exist ("
                  + (m_LayerCycles == 0 ? "?" : Integer.toString(m_LayerCycles)) + ")", "details");
            }
            else
            {
               writer.println("cycles exist");
            }
            writer.println("</td></tr>");

            // tangles
            writer.println("<tr><td>");
            writeHRefWithTarget("layer-tangles.html#cycles", "tangle score ("
                  + Integer.toString(m_LayerTangles) + ")", "details");
            writer.println("</td></tr>");
         }
         if (m_ConfigurationProvider.getMaxLayerCycles() >= 0)
         {
            writer.println("<tr><td>");
            if (m_LayerLevels >= 0)
            {
               if (m_Project.existLayerCycles())
               {
                  writeHRefWithTarget("layer-cycle-participation.html#participation", "cycle participation", "details");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }

               if (m_LayerLevels > 0)
               {
                  writeHRefWithTarget("layer-levels.html#levels", "levelization (" + (getLevelAsString(m_LayerLevels))
                     + ")", "details");
               }
               else
               {
                  writer.println("levelization (" + (getLevelAsString(m_LayerLevels)) + ")");
               }
            }
            else
            {
               if (m_Project.existLayerCycles())
               {
                  writer.println("cycle participation (?)");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }
               writer.println("levelization (" + (getLevelAsString(m_LayerLevels)) + ")");
            }
            writer.println("</td></tr>");
         }
      }
      else
      {
         writer.println("<tr><td>");
         writer.println("cycle detection disabled");
         writer.println("</td></tr>");
      }
      writer.println("<tr><td>");
      writeHRefWithTarget("layer-metrics.html#index", "metrics", "details");
      writer.println("</td></tr>");

      writer.write("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
   }

   private String getLevelAsString(int level)
   {
      assert level >= -1;
      String levelAsString = null;

      if (level == -1)
      {
         levelAsString = "?";
      }
      else if (level == 0)
      {
         levelAsString = "0";
      }
      else
      {
         levelAsString = Integer.toString(level);
      }

      return levelAsString;
   }

   private void writeSubsystemNavigation()
   {
      String elementName = m_Project.getSubsystemElementName();
      PrintWriter writer = getWriter();
      writer.write("<tr><td>");
      writeAnchoredTitle(elementName, elementName + "s (" + m_Project.getNumberOfProjectInternalSubsystems() + "/"
         + m_Project.getNumberOfProjectExternalSubsystems() + ")");
      writer.write("</td></tr>");
      writer.write("<tr><td>");
      writeHRefWithTarget("index-subsystems.html#all", "all", "details");
      writer.write("</td></tr>");

      writer.println("<tr><td>");
      writeHRefWithTarget("subsystem-violations.html#violations", "violations ("
         + m_Project.getNumberOfForbiddenEfferentSubsystemDependencies() + ")", "details");
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.checkSubsystemDependencies())
      {
         writer.println("<tr><td>");
         writeHRefWithTarget("subsystem-unused-dependencies.html#unused", "unused defined dependencies ("
            + m_Project.getNumberOfUnusedDefinedEfferentSubsystemDependencies() + ")", "details");
         writer.println("</td></tr>");
      }

      writer.println("<tr><td>");
      writeHRefWithTarget("project.html#not-implemented-subsystems", "not implemented ("
         + m_Project.getNumberOfProjectInternalNotImplementedSubsystems() + "/"
         + m_Project.getNumberOfProjectExternalNotImplementedSubsystems() + ")", "details");

      writer.println("</td></tr>");

      if (m_ConfigurationProvider.getMaxSubsystemCycles() >= -1)
      {
         if (m_Project.existSubsystemCycles())
         {
            writer.println("<tr><td>");
            if (m_ConfigurationProvider.getMaxSubsystemCycles() >= 0)
            {
               writeHRefWithTarget("subsystem-cycles.html#cycles", "cycles exist ("
                  + (m_SubsystemCycles == 0 ? "?" : Integer.toString(m_SubsystemCycles)) + ")", "details");
            }
            else
            {
               writer.println("cycles exist");
            }
            writer.println("</td></tr>");
            
            // tangles
            writer.println("<tr><td>");
            writeHRefWithTarget("subsystem-tangles.html#cycles", "tangle score ("
                  + Integer.toString(m_SubsystemTangles) + ")", "details");
            writer.println("</td></tr>");

         }
         if (m_ConfigurationProvider.getMaxSubsystemCycles() >= 0)
         {
            writer.println("<tr><td>");
            if (m_SubsystemLevels >= 0)
            {
               if (m_Project.existSubsystemCycles())
               {
                  writeHRefWithTarget("subsystem-cycle-participation.html#participation", "cycle participation",
                     "details");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }
               if (m_SubsystemLevels > 0)
               {
                  writeHRefWithTarget("subsystem-levels.html#levels", "levelization ("
                     + (getLevelAsString(m_SubsystemLevels)) + ")", "details");
               }
               else
               {
                  writer.println("levelization (" + (getLevelAsString(m_LayerLevels)) + ")");
               }
            }
            else
            {
               if (m_Project.existSubsystemCycles())
               {
                  writer.println("cycle participation (?)");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }
               writer.println("levelization (" + (getLevelAsString(m_SubsystemLevels)) + ")");
            }
            writer.println("</td></tr>");
         }
      }
      else
      {
         writer.println("<tr><td>");
         writer.println("cycle detection disabled");
         writer.println("</td></tr>");
      }
      writer.println("<tr><td>");
      writeHRefWithTarget("subsystem-metrics.html#index", "metrics", "details");
      writer.println("</td></tr>");

      writer.write("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
   }

   private void writePackageNavigation()
   {
      String elementName = m_Project.getPackageElementName();
      PrintWriter writer = getWriter();
      writer.write("<tr><td>");
      writeAnchoredTitle(elementName, elementName + "s (" + m_Project.getNumberOfProjectInternalPackages() + "/"
         + m_Project.getNumberOfProjectExternalPackages() + ")");
      writer.write("</td></tr>");
      writer.write("<tr><td>");
      writeHRefWithTarget("index-packages.html#all", "all", "details");
      writer.write("</td></tr>");

      writer.println("<tr><td>");
      writeHRefWithTarget("package-violations.html#violations", "violations ("
         + m_Project.getNumberOfForbiddenEfferentPackageDependencies() + ")", "details");
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.checkPackageDependencies())
      {
         writer.println("<tr><td>");
         writeHRefWithTarget("package-unused-dependencies.html#unused", "unused defined dependencies ("
            + m_Project.getNumberOfUnusedDefinedEfferentPackageDependencies() + ")", "details");
         writer.println("</td></tr>");
      }

      writer.println("<tr><td>");
      writeHRefWithTarget("project.html#not-assigned-packages", "not assigned ("
         + m_Project.getNumberOfProjectInternalNotAssignedPackages() + "/"
         + m_Project.getNumberOfProjectExternalNotAssignedPackages() + ")", "details");
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.getMaxPackageCycles() >= -1)
      {
         if (m_Project.existPackageCycles())
         {
            writer.println("<tr><td>");
            if (m_ConfigurationProvider.getMaxPackageCycles() >= 0)
            {
               writeHRefWithTarget("package-cycles.html#cycles", "cycles exist ("
                  + (m_PackageCycles == 0 ? "?" : Integer.toString(m_PackageCycles)) + ")", "details");
            }
            else
            {
               writer.println("cycles exist");
            }
            writer.println("</td></tr>");
            
            // tangles
            writer.println("<tr><td>");
            writeHRefWithTarget("package-tangles.html#cycles", "tangle score ("
                  + Integer.toString(m_PackageTangles) + ")", "details");
            writer.println("</td></tr>");
         }

         if (m_ConfigurationProvider.getMaxPackageCycles() >= 0)
         {
            writer.println("<tr><td>");
            if (m_PackageLevels >= 0)
            {
               if (m_Project.existPackageCycles())
               {
                  writeHRefWithTarget("package-cycle-participation.html#participation", "cycle participation",
                     "details");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }
               if (m_PackageLevels > 0)
               {
                  writeHRefWithTarget("package-levels.html#levels", "levelization ("
                     + (getLevelAsString(m_PackageLevels)) + ")", "details");
               }
               else
               {
                  writer.println("levelization (" + (getLevelAsString(m_LayerLevels)) + ")");
               }
            }
            else
            {
               if (m_Project.existPackageCycles())
               {
                  writer.println("cycle participation (?)");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }
               writer.println("levelization (" + (getLevelAsString(m_PackageLevels)) + ")");
            }
            writer.println("</td></tr>");
         }
      }
      else
      {
         writer.println("<tr><td>");
         writer.println("cycle detection disabled");
         writer.println("</td></tr>");
      }
      writer.println("<tr><td>");
      writeHRefWithTarget("package-metrics.html#index", "metrics", "details");
      writer.println("</td></tr>");

      writer.write("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
   }

   private void writeCompilationUnitNavigation()
   {
      String elementName = m_Project.getCompilationUnitElementName();
      PrintWriter writer = getWriter();
      writer.write("<tr><td>");
      writeAnchoredTitle(elementName, elementName + "s (" + m_Project.getNumberOfProjectInternalCompilationUnits()
         + "/" + m_Project.getNumberOfProjectExternalCompilationUnits() + ")");

      writer.write("</td></tr>");
      writer.write("<tr><td>");
      writeHRefWithTarget("index-compilation-units.html#all", "all", "details");
      writer.write("</td></tr>");

      writer.println("<tr><td>");
      writeHRefWithTarget("compilation-unit-violations.html#violations", "violations ("
         + m_Project.getNumberOfForbiddenEfferentCompilationUnitDependencies() + ")", "details");
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.getMaxCompilationUnitCycles() >= -1)
      {
         if (m_Project.existCompilationUnitCycles())
         {
            writer.println("<tr><td>");
            if (m_ConfigurationProvider.getMaxCompilationUnitCycles() >= 0)
            {
               writeHRefWithTarget("compilation-unit-cycles.html#cycles", "cycles exist ("
                  + (m_CompilationUnitCycles == 0 ? "?" : Integer.toString(m_CompilationUnitCycles)) + ")", "details");
            }
            else
            {
               writer.println("cycles exist");
            }
            writer.println("</td></tr>");
            
            // tangles
            writer.println("<tr><td>");
            writeHRefWithTarget("compilation-unit-tangles.html#cycles", "tangle score ("
                  + Integer.toString(m_CompilationUnitTangles) + ")", "details");
            writer.println("</td></tr>");
         }
         if (m_ConfigurationProvider.getMaxCompilationUnitCycles() >= 0)
         {
            writer.println("<tr><td>");
            if (m_CompilationUnitLevels >= 0)
            {
               if (m_Project.existCompilationUnitCycles())
               {
                  writeHRefWithTarget("compilation-unit-cycle-participation.html#participation", "cycle participation",
                     "details");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }
               if (m_CompilationUnitLevels > 0)
               {
                  writeHRefWithTarget("compilation-unit-levels.html#levels", "levelization ("
                     + (getLevelAsString(m_CompilationUnitLevels)) + ")", "details");
               }
               else
               {
                  writer.println("levelization (" + (getLevelAsString(m_LayerLevels)) + ")");
               }
            }
            else
            {
               if (m_Project.existCompilationUnitCycles())
               {
                  writer.println("cycle participation (?)");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }
               writer.println("levelization (" + (getLevelAsString(m_CompilationUnitLevels)) + ")");
            }
            writer.println("</td></tr>");
         }
      }
      else
      {
         writer.println("<tr><td>");
         writer.println("cycle detection disabled");
         writer.println("</td></tr>");
      }

      writer.println("<tr><td>");
      writeHRefWithTarget("compilation-unit-metrics.html#index", "metrics", "details");
      writer.println("</td></tr>");

      writer.write("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
   }

   private void writeTypeNavigation()
   {
      String elementName = m_Project.getTypeElementName();
      PrintWriter writer = getWriter();
      writer.write("<tr><td>");
      writeAnchoredTitle(elementName, elementName + "s (" + m_Project.getNumberOfProjectInternalTypes() + "/"
         + m_Project.getNumberOfProjectExternalTypes() + ")");
      writer.write("</td></tr>");
      writer.write("<tr><td>");
      writeHRefWithTarget("index-types.html#all", "all", "details");
      writer.write("</td></tr>");

      writer.println("<tr><td>");
      writeHRefWithTarget("type-violations.html#violations", "violations ("
         + m_Project.getNumberOfForbiddenEfferentTypeDependencies() + ")", "details");
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.getMaxTypeCycles() >= -1)
      {
         if (m_Project.existTypeCycles())
         {
            writer.println("<tr><td>");
            if (m_ConfigurationProvider.getMaxTypeCycles() >= 0)
            {
               writeHRefWithTarget("type-cycles.html#cycles", "cycles exist ("
                  + (m_TypeCycles == 0 ? "?" : Integer.toString(m_TypeCycles)) + ")", "details");
            }
            else
            {
               writer.println("cycles exist");
            }
            writer.println("</td></tr>");

            // tangles
            writer.println("<tr><td>");
            writeHRefWithTarget("type-tangles.html#cycles", "tangle score ("
                  + Integer.toString(m_TypeTangles) + ")", "details");
            writer.println("</td></tr>");
         }
         if (m_ConfigurationProvider.getMaxTypeCycles() >= 0)
         {
            writer.println("<tr><td>");
            if (m_TypeLevels >= 0)
            {
               if (m_Project.existTypeCycles())
               {
                  writeHRefWithTarget("type-cycle-participation.html#participation", "cycle participation", "details");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }
               if (m_TypeLevels > 0)
               {
                  writeHRefWithTarget("type-levels.html#levels", "levelization (" + (getLevelAsString(m_TypeLevels))
                     + ")", "details");
               }
               else
               {
                  writer.println("levelization (" + (getLevelAsString(m_LayerLevels)) + ")");
               }
            }
            else
            {
               if (m_Project.existTypeCycles())
               {
                  writer.println("cycle participation (?)");
                  writer.println("</td></tr>");
                  writer.println("<tr><td>");
               }
               writer.println("levelization (" + (getLevelAsString(m_TypeLevels)) + ")");
            }
            writer.println("</td></tr>");
         }
      }
      else
      {
         writer.println("<tr><td>");
         writer.println("cycle detection disabled");
         writer.println("</td></tr>");
      }

      writer.println("<tr><td>");
      writeHRefWithTarget("type-metrics.html#index", "metrics", "details");
      writer.println("</td></tr>");
   }

   private void writeDependometerInfo()
   {
      PrintWriter writer = getWriter();
      writer
         .println("<tr><td><a href=\"http://www.valtech.de\" target=\"_blank\"><img border=\"0\" src=\"logo.gif\"></a></td></tr>");
      writer.write("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");

      StringBuffer versionString = new StringBuffer();
      boolean infoAvailable = false;
      Package pack = getClass().getPackage();
      if (pack != null)
      {
         String info = pack.getImplementationTitle();
         if (info != null)
         {
            infoAvailable = true;
            versionString.append(info);
         }

         info = pack.getImplementationVersion();
         if (info != null)
         {
            infoAvailable = true;
            if (versionString.length() > 0)
            {
               versionString.append(' ');
            }
            versionString.append(info);
         }
      }

      if (infoAvailable)
      {
         writer.write("<tr><td>");
         writeHRefWithTarget("README", "README " + versionString.toString(), "details");
         writer.write("</td></tr>");
         writer.write("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
      }
      else
      {
         writer.write("<tr><td>");
         writeHRefWithTarget("README", "README", "details");
         writer.write("</td></tr>");
         writer.write("<tr><td></td></tr><tr><td></td></tr><tr><td></td></tr><tr><td></td></tr>");
      }
   }

   /**
    * Call this method if the actual execution of the write operation is of low importance, because another write
    * operation will follow anyway.
    * 
    * @throws IOException on write error
    */
   public void writeLikely() throws IOException
   {
      // need not be too thread save here
      if (!m_WritePending)
      {
         long currentTime = new Date().getTime();
         long milliSeconds = currentTime - m_LastTime;
         if (milliSeconds > 5000)
         {
            m_LastTime = currentTime;
            write();
         }
      }
   }

   public void write() throws IOException
   {
      synchronized (this)
      {
         m_WritePending = true;

         open(FILE_NAME);
         PrintWriter writer = getWriter();
         writer.println("<table>");
         writeDependometerInfo();
         writeProjectNavigation();
         if (m_ConfigurationProvider.analyzeVerticalSlices())
         {
            writeVerticalSliceNavigation();
         }
         writeLayerNavigation();
         writeSubsystemNavigation();
         writePackageNavigation();
         writeCompilationUnitNavigation();
         writeTypeNavigation();
         writer.println("</table>");
         super.close();

         m_WritePending = false;
      }
   }

   protected void close() throws IOException
   {
      write();
   }

   public void setTangleNumbers(int layerTangles, int verticalSliceTangles, int subsystemTangles, int packageTangles,
      int compilationUnitTangles, int typeTangles)
   {
         m_LayerTangles = layerTangles;
         m_VerticalSliceTangles = verticalSliceTangles;
         m_SubsystemTangles = subsystemTangles;
         m_PackageTangles = packageTangles;
         m_CompilationUnitTangles = compilationUnitTangles;
         m_TypeTangles = typeTangles;
  }
}