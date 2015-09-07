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

import com.valtech.source.dependometer.app.core.provider.CompilationUnitFilterIf;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.FullyQualifiedDirectedTypeDependencyIf;
import com.valtech.source.dependometer.app.core.provider.MetricIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.RefactoringIf;
import com.valtech.source.dependometer.app.core.provider.RegexprDirectedTypeDependencyIf;
import com.valtech.source.dependometer.app.core.provider.SkipExternalIf;
import com.valtech.source.dependometer.app.core.provider.ThresholdIf;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionProviderIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlProjectDocument extends HtmlDocument
{
   private final ProjectIf m_Project;

   private final String m_Creation;

   private final ConfigurationProviderIf m_ConfigurationProvider;

   private final TypeDefinitionProviderIf m_TypeDefinitionProvider;

   private final String m_MetricDescriptionsDocumentName;

   HtmlProjectDocument(File directory, ProjectIf project, String creation, String metricDescriptionsDocumentName)
      throws IOException
   {
      super(directory);
      assert project != null;
      assert creation != null;
      assert creation.length() > 0;
      assert metricDescriptionsDocumentName != null;
      assert metricDescriptionsDocumentName.length() > 0;

      m_Project = project;
      m_Creation = creation;
      m_ConfigurationProvider = ProviderFactory.getInstance().getConfigurationProvider();
      m_TypeDefinitionProvider = ProviderFactory.getInstance().getTypeDefinitionProvider();
      m_MetricDescriptionsDocumentName = metricDescriptionsDocumentName;
      open("project" + HTML_FILE_EXTENSION);
      writeTitle();
      writeMetrics();
      writeThresholds();
      writeFilter();
      writeManipulatedDependencies();
      writeProviderInfo();
      writeNotAssignedPackages();
      writeNotImplementedSubsystems();
      close();
   }

   private void writeTitle()
   {
      writeAnchoredTitle("project", "project info &lt;&lt;" + m_Project.getFullyQualifiedName() + "&gt;&gt; ("
         + m_Creation + ")");
   }

   private void writeMetrics()
   {
      MetricIf[] metrics = m_Project.getMetrics();
      PrintWriter writer = getWriter();
      writer.println("<a name=\"metrics\"/><h3>project metrics</h3>");

      writer.println("<table>");
      for (int i = 0; i < metrics.length; i++)
      {
         MetricIf nextMetric = metrics[i];
         writer.println("<tr>");
         writer.println("<td>");
         writer.println(nextMetric.getName());
         writer.println("</td>");
         writer.println("<td>");
         writer.println("=");
         writer.println("</td>");
         writer.println("<td>");
         writer.println(nextMetric.getValueAsString());
         writer.println("</td>");
         writer.println("<td>");
         writeHRef(m_MetricDescriptionsDocumentName + "#" + nextMetric.getName(), "[description]");
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");
   }

   private void writeFilter()
   {
      PackageFilterIf filter = m_ConfigurationProvider.getPackageFilter();
      CompilationUnitFilterIf compilationUnitFilter = m_ConfigurationProvider.getCompilationUnitFilter();

      PrintWriter writer = getWriter();
      writer.println("<a name=\"filter\"/><h3>project filter</h3>");
      writer.println("<table>");

      String[] filterInfo = filter.getIncludePatterns();
      for (int i = 0; i < filterInfo.length; i++)
      {
         writer.println("<tr><td>");
         writer.println("include package = " + filterInfo[i]);
         writer.println("</td></tr>");
      }

      filterInfo = filter.getExcludePatterns();
      for (int i = 0; i < filterInfo.length; i++)
      {
         writer.println("<tr><td>");
         writer.println("exclude package = " + filterInfo[i]);
         writer.println("</td></tr>");
      }

      String[] skipped = m_ConfigurationProvider.getPackageFilter().getSkippedPackages();
      writer.println("<tr><td>");
      writer.println("skipped packages from direct input (" + skipped.length + ")");
      writer.println("</td></tr>");
      for (int i = 0; i < skipped.length; i++)
      {
         writer.println("<tr><td>");
         writer.println("-- " + skipped[i]);
         writer.println("</td></tr>");
      }

      filterInfo = compilationUnitFilter.getFilterInformation();
      for (int i = 0; i < filterInfo.length; i++)
      {
         writer.println("<tr><td>");
         writer.println("exclude compilation unit = " + filterInfo[i]);
         writer.println("</td></tr>");
      }

      if (filterInfo.length > 0)
      {
         skipped = m_ConfigurationProvider.getCompilationUnitFilter().getExcludedCompilationUnits();
         writer.println("<tr><td>");
         writer.println("excluded compilation units from direct input (" + skipped.length + ")");
         writer.println("</td></tr>");
         for (int i = 0; i < skipped.length; i++)
         {
            writer.println("<tr><td>");
            writer.println("-- " + skipped[i]);
            writer.println("</td></tr>");
         }
      }

      writer.println("</table>");
   }

   private void writeManipulatedDependencies()
   {
      PrintWriter writer = getWriter();
      writer.println("<a name=\"manipulation\"/><h3>project manipulated dependencies</h3>");
      writer.println("<table>");

      SkipExternalIf[] skip = m_ConfigurationProvider.getSkipPatterns();
      writer.println("<tr><td>");
      writer.println("skipping dependencies to types starting with (" + skip.length + ")");
      writer.println("</td></tr>");
      for (int i = 0; i < skip.length; i++)
      {
         String[] matched = skip[i].getSkippedTypes();
         writer.println("<tr><td>");
         writer.println("-- skip = " + skip[i].getSkipPattern() + " (" + matched.length + " type(s) matched)");
         writer.println("</td></tr>");
         for (int j = 0; j < matched.length; j++)
         {
            writer.println("<tr><td>");
            writer.println("---- matched type = " + matched[j]);
            writer.println("</td></tr>");
         }
      }

      RegexprDirectedTypeDependencyIf[] ignore = m_ConfigurationProvider.getIgnore();
      writer.println("<tr><td>");
      writer.println("ignore dependencies (" + ignore.length + ")");
      writer.println("</td></tr>");
      for (int i = 0; i < ignore.length; i++)
      {
         FullyQualifiedDirectedTypeDependencyIf[] matched = ignore[i].getMatchedDependencies();
         writer.println("<tr><td>");
         writer.println("-- ignore dependencies = " + ignore[i].getFrom() + " -> " + ignore[i].getTo() + " ("
            + matched.length + " type(s) matched)");
         writer.println("</td></tr>");
         for (int j = 0; j < matched.length; j++)
         {
            writer.println("<tr><td>");
            writer.println("---- matched dependency = " + matched[j].getFrom() + " -> " + matched[j].getTo());
            writer.println("</td></tr>");
         }
      }

      RefactoringIf[] refactorings = m_ConfigurationProvider.getRefactorings();
      writer.println("<tr><td>");
      writer.println("refactorings (" + refactorings.length + ")");
      writer.println("</td></tr>");
      for (int i = 0; i < refactorings.length; i++)
      {
         String[] refactored = refactorings[i].getRefactoredTypes();
         writer.println("<tr><td>");
         writer.println("-- " + refactorings[i].getRefactoringInformation() + " (" + refactored.length
            + " type(s) matched)");
         writer.println("</td></tr>");
         for (int j = 0; j < refactored.length; j++)
         {
            writer.println("<tr><td>");
            writer.println("---- refactored type = " + refactored[j]);
            writer.println("</td></tr>");
         }
      }

      writer.println("</table>");
   }

   private void writeThresholds()
   {
      ThresholdIf[] thresholds = m_Project.getThresholds();
      PrintWriter writer = getWriter();
      writer.println("<a name=\"thresholds\"/><h3>project thresholds</h3>");

      writer.println("<table>");
      writer.println("<tr>");
      writer.println("<td>");
      writer.println("<b>name</b>");
      writer.println("</td>");
      writer.println("<td>");
      writer.println("<b>type</b>");
      writer.println("</td>");
      writer.println("<td>");
      writer.println("<b>threshold</b>");
      writer.println("</td>");
      writer.println("<td>");
      writer.println("<b>supported</b>");
      writer.println("</td>");
      writer.println("<td>");
      writer.println("<b>analyzed</b>");
      writer.println("</td>");
      writer.println("<td>");
      writer.println("<b>value</b>");
      writer.println("</td>");
      writer.println("</tr>");

      for (int i = 0; i < thresholds.length; i++)
      {
         ThresholdIf next = thresholds[i];
         if (next.isSupported())
         {
            String bgcolor = COLOR_WHITE;
            if (!next.wasAnalyzed())
            {
               bgcolor = COLOR_WHITE;
            }
            else if (next.wasViolated())
            {
               bgcolor = COLOR_ORANGE;
            }
            if (next.wasAnalyzed())
               writer.println("<tr>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println(next.getQueryId());
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println(next.isLowerThreshold() ? "lower" : "upper");
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println(next.getThreshold());
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println("yes");
            writer.println("</td>");
            if (next.wasAnalyzed())
            {
               writer.println("<td bgcolor=\"" + bgcolor + "\">");
               writer.println("yes");
               writer.println("</td>");
               writer.println("<td bgcolor=\"" + bgcolor + "\">");
               writer.println(next.getValue());
               writer.println("</td>");
               writer.println("</tr>");
            }
            else
            {
               writer.println("<td bgcolor=\"" + bgcolor + "\">");
               writer.println("no");
               writer.println("</td>");
               writer.println("<td bgcolor=\"" + bgcolor + "\">");
               writer.println("--");
               writer.println("</td>");
               writer.println("</tr>");
            }
         }
         else
         {
            String bgcolor = COLOR_WHITE;
            writer.println("<tr>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println(next.getQueryId());
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println(next.isLowerThreshold() ? "lower" : "upper");
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println(next.getThreshold());
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println("no");
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println("no");
            writer.println("</td>");
            writer.println("<td bgcolor=\"" + bgcolor + "\">");
            writer.println("--");
            writer.println("</td>");
            writer.println("</tr>");
         }
      }
      writer.println("</table>");
   }

   private void writeProviderInfo()
   {
      PrintWriter writer = getWriter();
      writer.println("<a name=\"configuration-provider\"/><h3>project configuration provider info</h3>");

      writer.println("<table>");

      writer.println("<tr><td>");
      writer.println("configuration provider = " + m_ConfigurationProvider.getClass().getName());
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("input source = " + m_Project.getSource());
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.analyzeVerticalSlices())
      {
         writer.println("<tr><td>");

         String info = "";
         if (!m_ConfigurationProvider.checkSubsystemDependencies())
         {
            info = " (but internally disabled due to " + m_Project.getSubsystemElementName()
               + " dependency check setting)";
         }
         writer.println(m_Project.getVerticalSliceElementName() + " dependency check = "
            + (m_ConfigurationProvider.checkVerticalSliceDependencies() ? "enabled" + info : "disabled"));
         writer.println("</td></tr>");
      }

      writer.println("<tr><td>");
      writer.println(m_Project.getLayerElementName() + " dependency check = "
         + (m_ConfigurationProvider.checkLayerDependencies() ? "enabled" : "disabled"));
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println(m_Project.getSubsystemElementName() + " dependency check = "
         + (m_ConfigurationProvider.checkSubsystemDependencies() ? "enabled" : "disabled"));
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println(m_Project.getPackageElementName() + " dependency check = "
         + (m_ConfigurationProvider.checkPackageDependencies() ? "enabled" : "disabled"));
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.analyzeVerticalSlices())
      {
         writer.println("<tr><td>");
         writer.println("cumulation of " + m_Project.getVerticalSliceElementName() + " dependencies = "
            + (m_ConfigurationProvider.cumulateVerticalSliceDependencies() ? "enabled" : "disabled"));
         writer.println("</td></tr>");
      }

      writer.println("<tr><td>");
      writer.println("cumulation of " + m_Project.getLayerElementName() + " dependencies = "
         + (m_ConfigurationProvider.cumulateLayerDependencies() ? "enabled" : "disabled"));
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("cumulation of " + m_Project.getSubsystemElementName() + " dependencies = "
         + (m_ConfigurationProvider.cumulateSubsystemDependencies() ? "enabled" : "disabled"));
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("cumulation of " + m_Project.getPackageElementName() + " dependencies = "
         + (m_ConfigurationProvider.cumulatePackageDependencies() ? "enabled" : "disabled"));
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("cumulation of " + m_Project.getCompilationUnitElementName() + " dependencies = "
         + (m_ConfigurationProvider.cumulateCompilationUnitDependencies() ? "enabled" : "disabled"));
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("cumulation of " + m_Project.getTypeElementName() + " dependencies = "
         + (m_ConfigurationProvider.cumulateTypeDependencies() ? "enabled" : "disabled"));
      writer.println("</td></tr>");

      if (m_ConfigurationProvider.analyzeVerticalSlices())
      {
         writer.println("<tr><td>");
         writer.println("max " + m_Project.getVerticalSliceElementName() + " cycles = "
            + m_ConfigurationProvider.getMaxVerticalSliceCycles());
         writer.println("</td></tr>");
      }

      writer.println("<tr><td>");
      writer.println("max " + m_Project.getLayerElementName() + " cycles = "
         + m_ConfigurationProvider.getMaxLayerCycles());
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("max " + m_Project.getSubsystemElementName() + " cycles = "
         + m_ConfigurationProvider.getMaxSubsystemCycles());
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("max " + m_Project.getPackageElementName() + " cycles = "
         + m_ConfigurationProvider.getMaxPackageCycles());
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("max " + m_Project.getCompilationUnitElementName() + " cycles = "
         + m_ConfigurationProvider.getMaxCompilationUnitCycles());
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("max " + m_Project.getTypeElementName() + " cycles = "
         + m_ConfigurationProvider.getMaxTypeCycles());
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer.println("cycle feedback = " + m_ConfigurationProvider.getCycleFeedback());
      writer.println("</td></tr>");

      writer.println("<tr><td>");
      writer
         .println("cycle analysis progress feedback = " + m_ConfigurationProvider.getCycleAnalysisProgressFeedback());
      writer.println("</td></tr>");

      String[] additionalProviderInfo = m_ConfigurationProvider.getAdditionalInfo();
      for (int i = 0; i < additionalProviderInfo.length; i++)
      {
         writer.println("<tr>");
         writer.println("<td>");
         writer.println(additionalProviderInfo[i]);
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");

      additionalProviderInfo = m_TypeDefinitionProvider.getAdditionalInfo();
      writer.println("<a name=\"type-definition-provider\"/><h3>project type definition provider info</h3>");
      writer.println("<table>");

      writer.println("<tr><td>");
      writer.println("type definition provider = " + m_TypeDefinitionProvider.getClass().getName());
      writer.println("</td></tr>");

      for (int i = 0; i < additionalProviderInfo.length; i++)
      {
         writer.println("<tr>");
         writer.println("<td>");
         writer.println(additionalProviderInfo[i]);
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");
   }

   private void writeNotAssignedPackages()
   {
      DependencyElementIf[] notAssigned = m_Project.getNotAssignedPackages();
      PrintWriter writer = getWriter();
      writer.println("<a name=\"not-assigned-packages\"/><h3>not assigned packages ("
         + m_Project.getNumberOfProjectInternalNotAssignedPackages() + "/"
         + m_Project.getNumberOfProjectExternalNotAssignedPackages() + ")</h3>");

      writer.println("<table>");
      for (int i = 0; i < notAssigned.length; i++)
      {
         DependencyElementIf next = notAssigned[i];
         writer.println("<tr>");
         HtmlDependencyElementDocument.writeCharacteristics(writer, next);
         writer.println("<td>");
         writeHRef(next.hashCode() + ".html#element", next.getFullyQualifiedName());
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");
   }

   private void writeNotImplementedSubsystems()
   {
      DependencyElementIf[] notImplemented = m_Project.getNotImplementedProjectInternalSubsystems();
      PrintWriter writer = getWriter();
      writer.println("<a name=\"not-implemented-subsystems\"/><h3>not implemented subsystems ("
         + m_Project.getNumberOfProjectInternalNotImplementedSubsystems() + "/"
         + m_Project.getNumberOfProjectExternalNotImplementedSubsystems() + ")</h3>");

      writer.println("<table>");
      for (int i = 0; i < notImplemented.length; i++)
      {
         DependencyElementIf next = notImplemented[i];
         writer.println("<tr>");
         HtmlDependencyElementDocument.writeCharacteristics(writer, next);
         writer.println("<td>");
         writeHRef(next.hashCode() + ".html#element", next.getFullyQualifiedName());
         writer.println("</td>");
         writer.println("</tr>");
      }
      writer.println("</table>");
   }
}