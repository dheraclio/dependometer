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

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public interface ProjectIf extends MetricsProviderIf
{
   // metrics
   public MetricDefinitionIf[] getMetricDefinitions();

   public ThresholdIf[] getThresholds();

   // layers
   public String getLayerElementName();

   public boolean existLayerCycles();

   public int getNumberOfProjectInternalLayers();

   public int getNumberOfProjectExternalLayers();

   public int getNumberOfForbiddenEfferentLayerDependencies();

   public DirectedDependencyIf[] getForbiddenEfferentLayerDependencies();

   public int getNumberOfUnusedDefinedEfferentLayerDependencies();

   public DirectedDependencyIf[] getUnusedDefinedEfferentLayerDependencies();

   public DependencyElementIf[] getLayers();

   // vertical slices
   public String getVerticalSliceElementName();

   public SubsystemFilterIf getSubsystemFilter();

   public int getNumberOfVerticalSlices();

   public boolean existVerticalSliceCycles();

   public DependencyElementIf[] getVerticalSlices();

   public DependencyElementIf[] getRelevantLayersForVerticalSlices();

   public int getNumberOfForbiddenEfferentVerticalSliceDependencies();

   public DirectedDependencyIf[] getForbiddenEfferentVerticalSliceDependencies();

   // subsystems
   public String getSubsystemElementName();

   public boolean existSubsystemCycles();

   public int getNumberOfProjectInternalNotImplementedSubsystems();

   public int getNumberOfProjectExternalNotImplementedSubsystems();

   public int getNumberOfProjectInternalSubsystems();

   public int getNumberOfProjectExternalSubsystems();

   public int getNumberOfForbiddenEfferentSubsystemDependencies();

   public DirectedDependencyIf[] getForbiddenEfferentSubsystemDependencies();

   public int getNumberOfUnusedDefinedEfferentSubsystemDependencies();

   public DirectedDependencyIf[] getUnusedDefinedEfferentSubsystemDependencies();

   public DependencyElementIf[] getSubsystems();

   public DependencyElementIf[] getNotImplementedProjectInternalSubsystems();

   public PackageFilterIf getSubsystemPackageFilter(DependencyElementIf subsystem);

   // packages
   public String getPackageElementName();

   public boolean existPackageCycles();

   public int getNumberOfProjectInternalPackages();

   public int getNumberOfProjectExternalPackages();

   public int getNumberOfProjectInternalNotAssignedPackages();

   public int getNumberOfProjectExternalNotAssignedPackages();

   public int getNumberOfForbiddenEfferentPackageDependencies();

   public DirectedDependencyIf[] getForbiddenEfferentPackageDependencies();

   public int getNumberOfUnusedDefinedEfferentPackageDependencies();

   public DirectedDependencyIf[] getUnusedDefinedEfferentPackageDependencies();

   public DependencyElementIf[] getPackages();

   public DependencyElementIf[] getNotAssignedPackages();

   // compilation units
   public String getCompilationUnitElementName();

   public boolean existCompilationUnitCycles();

   public int getNumberOfProjectInternalCompilationUnits();

   public int getNumberOfProjectExternalCompilationUnits();

   public int getNumberOfForbiddenEfferentCompilationUnitDependencies();

   public DirectedDependencyIf[] getForbiddenEfferentCompilationUnitDependencies();

   public DependencyElementIf[] getCompilationUnits();

   // types
   public String getTypeElementName();

   public boolean existTypeCycles();

   public int getNumberOfProjectInternalTypes();

   public int getNumberOfProjectExternalTypes();

   public int getNumberOfForbiddenEfferentTypeDependencies();

   public DirectedDependencyIf[] getForbiddenEfferentTypeDependencies();

   public DependencyElementIf[] getTypes();
   
   public String getName();
}