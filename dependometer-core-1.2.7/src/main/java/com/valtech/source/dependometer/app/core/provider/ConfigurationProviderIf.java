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
package com.valtech.source.dependometer.app.core.provider;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

/**
 * Implement this interface to provide the core subsystem with needed configuration information
 * 
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public interface ConfigurationProviderIf extends ProviderIf
{
   public boolean readConfiguration(File file) throws Exception;

   public File resolveRelativePath(String relativePath) throws IOException;

   public Object getSource();

   public File[] getInputDirectories();

   public File[] getSystemIncludeDirectories();

   public Map<String, String> getSystemDefines();

   public ListenerIf[] getListeners();

   public ThresholdDefinitionIf[] getUpperThresholds();

   public ThresholdDefinitionIf[] getLowerThresholds();

   public String getAssertionPattern();

   public SkipExternalIf[] getSkipPatterns();

   public RegexprDirectedTypeDependencyIf[] getIgnore();

   public RefactoringIf[] getRefactorings();

   public String getProjectName();

   public PackageFilterIf getPackageFilter();

   public CompilationUnitFilterIf getCompilationUnitFilter();

   public int getCycleFeedback();

   public int getCycleAnalysisProgressFeedback();

   public int getMaxVerticalSliceCycles();

   public boolean analyzeVerticalSlices();

   public boolean cumulateVerticalSliceDependencies();

   public boolean checkVerticalSliceDependencies();

   public SubsystemFilterIf getSubsystemFilter();

   public int getMaxLayerCycles();

   public boolean cumulateLayerDependencies();

   public boolean checkLayerDependencies();

   public String[] getLayers();

   public String[] layerDependsUpon(String layer);

   public String getLayerDescription(String layer);

   public int getMaxSubsystemCycles();

   public boolean checkSubsystemDependencies();

   public boolean cumulateSubsystemDependencies();

   public String[] getSubsystems();

   public String[] subsystemDependsUpon(String subsystem);

   public String getSubsystemDescription(String layer);

   public PackageFilterIf getSubsystemPackageFilter(String subsystem);

   public int getMaxPackageCycles();

   public boolean checkPackageDependencies();

   public boolean cumulatePackageDependencies();

   public String[] getPackages();

   public String[] packageDependsUpon(String packageName);

   public String getPackageDescription(String packageName);

   public int getMaxCompilationUnitCycles();

   public boolean cumulateCompilationUnitDependencies();

   public int getMaxTypeCycles();

   public boolean cumulateTypeDependencies();

   public boolean ignorePhysicalStructure();
   
   public Charset getFileEncoding();

   public boolean isCycleAnalysisEnabled();

   public long getTimeoutMinutes();
}