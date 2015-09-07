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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import com.valtech.source.dependometer.app.core.provider.CompilationUnitFilterIf;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.ListenerIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;
import com.valtech.source.dependometer.app.core.provider.RefactoringIf;
import com.valtech.source.dependometer.app.core.provider.RegexprDirectedTypeDependencyIf;
import com.valtech.source.dependometer.app.core.provider.SkipExternalIf;
import com.valtech.source.dependometer.app.core.provider.SubsystemFilterIf;
import com.valtech.source.dependometer.app.core.provider.ThresholdDefinitionIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class ConfigurationProvider implements ConfigurationProviderIf
{
   private ProjectNode m_ProjectNode;

   private PackageDescriptionAnalyzer m_Analyzer;

   private File m_Source;

   //TODO read from configuration?
   private boolean cycleAnalysisEnabled = true;

   public void setCycleAnalysisEnabled(boolean cycleAnalysisEnabled)
   {
      this.cycleAnalysisEnabled = cycleAnalysisEnabled;
   }

   public File resolveRelativePath(String relPath) throws IOException
   {
      return Node.getPath(relPath);
   }

   public boolean readConfiguration(File file) throws Exception
   {
      assert file != null;

      m_ProjectNode = new ConfigurationReader().readConfiguration(file);
      if (m_ProjectNode != null)
      {
         m_Source = m_ProjectNode.getSource();
      }

      return m_ProjectNode != null;
   }

   private void analyzePackageDescriptions()
   {
      if (m_Analyzer == null)
      {
         m_Analyzer = new PackageDescriptionAnalyzer();
         m_Analyzer.setInputDirectories(m_ProjectNode.getInputDirectories());
         m_Analyzer.setPackageFilter(m_ProjectNode.getPackageFilter());
         m_Analyzer.analyzePackageDescriptions();
      }
   }

   public ListenerIf[] getListeners()
   {
      return m_ProjectNode.getListenerNodes();
   }

   public PackageFilterIf getPackageFilter()
   {
      return m_ProjectNode.getPackageFilter();
   }

   public CompilationUnitFilterIf getCompilationUnitFilter()
   {
      return m_ProjectNode.getCompilationUnitFilter();
   }

   public File[] getInputDirectories()
   {
      return m_ProjectNode.getInputDirectories();
   }

   public File[] getSystemIncludeDirectories()
   {
      return m_ProjectNode.getSystemIncludeDirectories();
   }

   public Map<String, String> getSystemDefines()
   {
      return m_ProjectNode.getSystemDefines();
   }

   public SkipExternalIf[] getSkipPatterns()
   {
      return m_ProjectNode.getSkipPatterns();
   }

   public String getAssertionPattern()
   {
      return m_ProjectNode.getAssertionPattern();
   }

   public RefactoringIf[] getRefactorings()
   {
      return m_ProjectNode.getRefactorings();
   }

   public RegexprDirectedTypeDependencyIf[] getIgnore()
   {
      return m_ProjectNode.getIgnore();
   }

   public String getProjectName()
   {
      return m_ProjectNode.getProjectName();
   }

   public boolean checkLayerDependencies()
   {
      return m_ProjectNode.checkLayerDependencies();
   }

   public String[] getLayers()
   {
      return m_ProjectNode.getLayers();
   }

   public String[] layerDependsUpon(String layer)
   {
      return m_ProjectNode.layerDependsUpon(layer);
   }

   public boolean checkSubsystemDependencies()
   {
      return m_ProjectNode.checkSubsystemDependencies();
   }

   public String[] getSubsystems()
   {
      return m_ProjectNode.getSubsystems();
   }

   public String[] subsystemDependsUpon(String subsystem)
   {
      return m_ProjectNode.subsystemDependsUpon(subsystem);
   }

   public PackageFilterIf getSubsystemPackageFilter(String subsystem)
   {
      return m_ProjectNode.getSubsystemPackageFilter(subsystem);
   }

   public boolean checkPackageDependencies()
   {
      return m_ProjectNode.checkPackageDependencies();
   }

   public String[] getPackages()
   {
      analyzePackageDescriptions();
      return m_Analyzer.getPackages();
   }

   public String[] packageDependsUpon(String packageName)
   {
      assert m_Analyzer != null;
      return m_Analyzer.packageDependsUpon(packageName);
   }

   public String getPackageDescription(String packageName)
   {
      return m_Analyzer.getPackageDescription(packageName);
   }

   public int getCycleFeedback()
   {
      return m_ProjectNode.getCycleFeedback();
   }

   public int getMaxCompilationUnitCycles()
   {
      return m_ProjectNode.getMaxCompilationUnitCycles();
   }

   public int getMaxLayerCycles()
   {
      return m_ProjectNode.getMaxLayerCycles();
   }

   public int getMaxPackageCycles()
   {
      return m_ProjectNode.getMaxPackageCycles();
   }

   public int getMaxSubsystemCycles()
   {
      return m_ProjectNode.getMaxSubsystemCycles();
   }

   public int getMaxTypeCycles()
   {
      return m_ProjectNode.getMaxTypeCycles();
   }

   public int getMaxVerticalSliceCycles()
   {
      return m_ProjectNode.getMaxVerticalSliceCycles();
   }

   
   public ThresholdDefinitionIf[] getLowerThresholds()
   {
      return m_ProjectNode.getLowerThresholds();
   }

   public ThresholdDefinitionIf[] getUpperThresholds()
   {
      return m_ProjectNode.getUpperThresholds();
   }

   public Object getSource()
   {
      return m_Source;
   }

   public String getLayerDescription(String layer)
   {
      return m_ProjectNode.getLayerDescription(layer);
   }

   public String getSubsystemDescription(String subsystem)
   {
      return m_ProjectNode.getSubsystemDescription(subsystem);
   }

   public boolean analyzeVerticalSlices()
   {
      return m_ProjectNode.isVerticalSlicesNodePresent();
   }

   public SubsystemFilterIf getSubsystemFilter()
   {
      return m_ProjectNode.getSubsystemFilter();
   }

   public boolean checkVerticalSliceDependencies()
   {
      return m_ProjectNode.checkVerticalSliceDependencies();
   }

   public boolean cumulateVerticalSliceDependencies()
   {
      return m_ProjectNode.cumulateVerticalSliceDependencies();
   }

   public boolean cumulateLayerDependencies()
   {
      return m_ProjectNode.cumulateLayerDependencies();
   }

   public boolean cumulateSubsystemDependencies()
   {
      return m_ProjectNode.cumulateSubsystemDependencies();
   }

   public boolean cumulatePackageDependencies()
   {
      return m_ProjectNode.cumulatePackageDependencies();
   }

   public boolean cumulateCompilationUnitDependencies()
   {
      return m_ProjectNode.cumulateCompilationUnitDependencies();
   }

   public boolean cumulateTypeDependencies()
   {
      return m_ProjectNode.cumulateTypeDependencies();
   }

   public int getCycleAnalysisProgressFeedback()
   {
      return m_ProjectNode.getCycleAnalysisProgressFeedback();
   }

   public boolean ignorePhysicalStructure()
   {
      return m_ProjectNode.ignorePhysicalStructure();
   }

   public String[] getAdditionalInfo()
   {
      if (m_Analyzer != null)
      {
         return m_Analyzer.getAdditionalInfo();
      }
      else
      {
         return new String[0];
      }
   }

   public Charset getFileEncoding()
   {
      return m_ProjectNode.getFileEncoding();
   }

   public boolean isCycleAnalysisEnabled()
   {
      return cycleAnalysisEnabled;
   }

   public long getTimeoutMinutes() {

	   return m_ProjectNode.getTimeoutMinutes();
   }
}