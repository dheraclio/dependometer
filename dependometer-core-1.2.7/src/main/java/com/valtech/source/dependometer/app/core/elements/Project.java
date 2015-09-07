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
package com.valtech.source.dependometer.app.core.elements;

import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.ACD;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.AverageNumberOfAssertionsPerProjectInternalClass;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.CompilationUnitCyclesExist;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.LayerCyclesExist;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.MaxDepthOfInheritance;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.MaxDepthOfPackageHierarchy;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfDefinedLayerDependencies;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfDefinedPackageDependencies;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfDefinedSubsystemDependencies;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfForbiddenEfferentPackageDependencies;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfNotAssignedPackages;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfNotImplementedSubsystems;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfProjectExternalLayers;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfProjectExternalPackages;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfProjectExternalSubsystems;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfProjectInternalLayers;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfProjectInternalSubsystems;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.NumberOfProjectInternalTypes;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.PackageCyclesExist;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.PercentageOfLayersWithRcNotLessThanOne;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.PercentageOfPackagesWithRcNotLessThanOne;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.PercentageOfSubsystemsWithRcNotLessThanOne;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.PercentageOfVerticalSlicesWithRcNotLessThanOne;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.SubsystemCyclesExist;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.TypeCyclesExist;
import static com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum.VerticalSliceCyclesExist;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.metrics.MetricDefinition;
import com.valtech.source.dependometer.app.core.metrics.MetricsProvider;
import com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;
import com.valtech.source.dependometer.app.core.provider.MetricDefinitionIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.QueryInfoIf;
import com.valtech.source.dependometer.app.core.provider.SubsystemFilterIf;
import com.valtech.source.dependometer.app.core.provider.ThresholdIf;

import java.util.Arrays;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class Project extends MetricsProvider implements ProjectIf
{
   public static final String ELEMENT_NAME = "project";

   private ThresholdIf[] thresholds;

   private int m_NumberOfAssertions = INITIAL_NUMBER_OF_ASSERTIONS;

   public static QueryInfoIf[] getProvidedQueryInfo()
   {
      return ProjectMetricsEnum.values();
   }

   public Project(String name, Object source)
   {
      super(name, source);
   }

   public int getContainmentLevel()
   {
      // TODO this is hardcoded, why?
      return 5;
   }

   public String getElementName()
   {
      return ELEMENT_NAME;
   }

   public void collectMetrics()
   {
      super.collectMetrics();

      // assertions
      m_NumberOfAssertions = ParsedClass.getTotalNumberOfAssertions();
      int numberOfParsedClasses = ParsedClass.getNumberOfParsedClasses();

      if (numberOfParsedClasses > 0)
      {
         if (m_NumberOfAssertions > -1)
         {
            addMetric(MetricEnum.NUMBER_OF_ASSERTIONS, m_NumberOfAssertions);
            addMetric(MetricEnum.AVERAGE_USAGE_OF_ASSERTIONS_PER_CLASS, (double)m_NumberOfAssertions
               / (double)ParsedClass.getNumberOfParsedClasses());

            AverageNumberOfAssertionsPerProjectInternalClass.setValue((double)m_NumberOfAssertions
               / (double)ParsedClass.getNumberOfParsedClasses());
         }
         else
         {
            addMetricWithNotAnalyzedInfo(MetricEnum.NUMBER_OF_ASSERTIONS );
            addMetricWithNotAnalyzedInfo(MetricEnum.AVERAGE_USAGE_OF_ASSERTIONS_PER_CLASS );
         }
      }
      else
      {
         addMetricWithInfo(MetricEnum.NUMBER_OF_ASSERTIONS, "contains no types that may use assertions");
         addMetricWithInfo(MetricEnum.AVERAGE_USAGE_OF_ASSERTIONS_PER_CLASS, "contains no types that may use assertions");
      }

      // vertical slice
      if (ProviderFactory.getInstance().getConfigurationProvider().analyzeVerticalSlices())
      {
         double rcPercentageNotLessThanOneForVerticalSlices = calculateRcPercentageNotLessThanOne(VerticalSlice
            .getRelationalCohesionValues());
         addMetric(MetricEnum.PERCENTAGE_OF_PROJECT_INTERNAL_VERTICAL_SLICES_WITH_A_RELATIONAL_COHESION_GREATER_THAN_ONE,
            rcPercentageNotLessThanOneForVerticalSlices);
         PercentageOfVerticalSlicesWithRcNotLessThanOne.setValue(rcPercentageNotLessThanOneForVerticalSlices);
         addMetric(MetricEnum.NUMBER_OF_PROJECT_INTERNAL_VERTICAL_SLICES, VerticalSlice.getNumberOfVerticalSlices());
         addMetric(MetricEnum.NUMBER_OF_FORBIDDEN_OUTGOING_VERTICAL_SLICE_DEPENDENCIES, VerticalSlice
            .getNumberOfForbiddenEfferentVerticalSliceDependencies());
         addMetric(MetricEnum.NUMBER_OF_OUTGOING_VERTICAL_SLICE_DEPENDENCIES, VerticalSlice
            .getTotalNumberOfEfferentDependencies());
         if (VerticalSliceCyclesExist.wasValueSet())
         {
            addMetric(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_VERTICAL_SLICES,
               VerticalSliceCyclesExist.getValue() == 1.0 ? true : false);
         }
         else
         {
            addMetricWithNotAnalyzedInfo(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_VERTICAL_SLICES );
         }

      }
      else
      {
         addMetricWithNotAnalyzedInfo(MetricEnum.PERCENTAGE_OF_PROJECT_INTERNAL_VERTICAL_SLICES_WITH_A_RELATIONAL_COHESION_GREATER_THAN_ONE );
         addMetricWithNotAnalyzedInfo(MetricEnum.NUMBER_OF_PROJECT_INTERNAL_VERTICAL_SLICES );
         addMetricWithNotAnalyzedInfo(MetricEnum.NUMBER_OF_FORBIDDEN_OUTGOING_VERTICAL_SLICE_DEPENDENCIES );
         addMetricWithNotAnalyzedInfo(MetricEnum.NUMBER_OF_OUTGOING_VERTICAL_SLICE_DEPENDENCIES );
         addMetricWithNotAnalyzedInfo(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_VERTICAL_SLICES );
      }

      // layer
      if (LayerCyclesExist.wasValueSet())
      {
         addMetric(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_LAYERS, LayerCyclesExist.getValue() == 1.0 ? true
            : false);
      }
      else
      {
         addMetricWithNotAnalyzedInfo(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_LAYERS );
      }

      addMetric(MetricEnum.NUMBER_OF_OUTGOING_LAYER_DEPENDENCIES, Layer.getTotalNumberOfEfferentDependencies());
      addMetric(MetricEnum.NUMBER_OF_FORBIDDEN_OUTGOING_LAYER_DEPENDENCIES, Layer
         .getNumberOfForbiddenEfferentLayerDependencies());
      NumberOfProjectExternalLayers.setValue(getNumberOfProjectExternalLayers());
      addMetric(MetricEnum.NUMBER_OF_PROJECT_EXTERNAL_LAYERS, getNumberOfProjectExternalLayers());
      NumberOfProjectInternalLayers.setValue(getNumberOfProjectInternalLayers());
      addMetric(MetricEnum.NUMBER_OF_PROJECT_INTERNAL_LAYERS, getNumberOfProjectInternalLayers());
      NumberOfDefinedLayerDependencies.setValue(Layer.getNumberOfAllowedEfferents());
      addMetric(MetricEnum.NUMBER_OF_ALLOWED_OUTGOING_LAYER_DEPENDENCIES, Layer.getNumberOfAllowedEfferents());
      NumberOfProjectInternalSubsystems.setValue(getNumberOfProjectInternalSubsystems());
      double rcPercentageNotLessThanOneForLayers = calculateRcPercentageNotLessThanOne(Layer
         .getRelationalCohesionValues());
      addMetric(MetricEnum.PERCENTAGE_OF_PROJECT_INTERNAL_LAYERS_WITH_A_RELATIONAL_COHESION_GREATER_THAN_ONE,
         rcPercentageNotLessThanOneForLayers);
      PercentageOfLayersWithRcNotLessThanOne.setValue(rcPercentageNotLessThanOneForLayers);

      // subsystem
      if (SubsystemCyclesExist.wasValueSet())
      {
         addMetric(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_SUBSYSTEM, SubsystemCyclesExist.getValue() == 1.0 ? true
            : false);
      }
      else
      {
         addMetricWithNotAnalyzedInfo(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_SUBSYSTEM );
      }

      addMetric(MetricEnum.NUMBER_OF_FORBIDDEN_OUTGOING_SUBSYSTEM_DEPENDENCIES, Subsystem
         .getNumberOfForbiddenEfferentSubsystemDependencies());
      addMetric(MetricEnum.NUMBER_OF_OUTGOING_SUBSYSTEM_DEPENDENCIES, Subsystem.getTotalNumberOfEfferentDependencies());
      addMetric(MetricEnum.NUMBER_OF_PROJECT_INTERNAL_SUBSYSTEMS, getNumberOfProjectInternalSubsystems());
      NumberOfProjectExternalSubsystems.setValue(getNumberOfProjectExternalSubsystems());
      addMetric(MetricEnum.NUMBER_OF_PROJECT_EXTERNAL_SUBSYSTEMS, getNumberOfProjectExternalSubsystems());
      int notImplementedSubsystems = getNumberOfProjectInternalNotImplementedSubsystems()
         + getNumberOfProjectExternalNotImplementedSubsystems();
      NumberOfNotImplementedSubsystems.setValue(notImplementedSubsystems);
      addMetric(MetricEnum.NUMBER_OF_NOT_IMPLEMENTED_SUBSYSTEMS, notImplementedSubsystems);
      NumberOfDefinedSubsystemDependencies.setValue(Subsystem.getNumberOfAllowedEfferents());
      addMetric(MetricEnum.NUMBER_OF_ALLOWED_OUTGOING_SUBSYSTEM_DEPENDENCIES, Subsystem.getNumberOfAllowedEfferents());
      double rcPercentageNotLessThanOneForSubsystems = calculateRcPercentageNotLessThanOne(Subsystem
         .getRelationalCohesionValues());
      addMetric(MetricEnum.PERCENTAGE_OF_PROJECT_INTERNAL_SUBSYSTEMS_WITH_A_RELATIONAL_COHESION_GREATER_THAN_ONE,
         rcPercentageNotLessThanOneForSubsystems);

      PercentageOfSubsystemsWithRcNotLessThanOne.setValue(rcPercentageNotLessThanOneForSubsystems);

      // package
      if (PackageCyclesExist.wasValueSet())
      {
         addMetric(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_PACKAGES, PackageCyclesExist.getValue() == 1.0 ? true
            : false);
      }
      else
      {
         addMetricWithNotAnalyzedInfo(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_PACKAGES );
      }
      NumberOfProjectExternalPackages.setValue(NotParsedPackage.getNumberOfNotParsedPackages());
      NumberOfDefinedPackageDependencies.setValue(Package.getNumberOfAllowedEfferents());

      addMetric(MetricEnum.NUMBER_OF_PROJECT_INTERNAL_PACKAGES, getNumberOfProjectInternalPackages());
      addMetric(MetricEnum.NUMBER_OF_PROJECT_EXTERNAL_PACKAGES, getNumberOfProjectExternalPackages());
      addMetric(MetricEnum.NUMBER_OF_ALLOWED_OUTGOING_PACKAGE_DEPENDENCIES, Package.getNumberOfAllowedEfferents());
      addMetric(MetricEnum.NUMBER_OF_OUTGOING_PACKAGE_DEPENDENCIES, Package.getTotalNumberOfEfferentDependencies());
      NumberOfForbiddenEfferentPackageDependencies.setValue(Package.getNumberOfForbiddenEfferentPackageDependencies());
      addMetric(MetricEnum.NUMBER_OF_FORBIDDEN_OUTGOING_PACKAGE_DEPENDENCIES, Package
         .getNumberOfForbiddenEfferentPackageDependencies());
      int notAssignedPackages = Package.getNumberOfProjectInternalNotAssignedPackages()
         + getNumberOfProjectExternalNotAssignedPackages();
      NumberOfNotAssignedPackages.setValue(notAssignedPackages);
      addMetric(MetricEnum.NUMBER_OF_NOT_ASSIGNED_PACKAGES, notAssignedPackages);
      double rcPercentageNotLessThanOneForPackages = calculateRcPercentageNotLessThanOne(Package
         .getRelationalCohesionValues());
      PercentageOfPackagesWithRcNotLessThanOne.setValue(rcPercentageNotLessThanOneForPackages);
      addMetric(MetricEnum.PERCENTAGE_OF_PROJECT_INTERNAL_PACKAGES_WITH_A_RELATIONAL_COHESION_GREATER_THAN_ONE,
         rcPercentageNotLessThanOneForPackages);
      MaxDepthOfPackageHierarchy.setValue(Package.getMaxDepthOfPackageHierarchy());
      addMetric(MetricEnum.MAX_DEPTH_OF_PACKAGE_HIERARCHY, Package.getMaxDepthOfPackageHierarchy());

      // compilation unit
      if (CompilationUnitCyclesExist.wasValueSet())
      {
         addMetric(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_COMPILATION_UNITS,
            CompilationUnitCyclesExist.getValue() == 1.0 ? true : false);
      }
      else
      {
         addMetricWithNotAnalyzedInfo(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_COMPILATION_UNITS );
      }
      calculateComponentMetrics(ParsedCompilationUnit.getParsedCompilationUnits(), true);

      addMetric(MetricEnum.NUMBER_OF_PROJECT_INTERNAL_COMPILATION_UNITS, getNumberOfProjectInternalCompilationUnits());
      addMetric(MetricEnum.NUMBER_OF_PROJECT_EXTERNAL_COMPILATION_UNITS, getNumberOfProjectExternalCompilationUnits());
      addMetric(MetricEnum.NUMBER_OF_OUTGOING_COMPILATION_UNIT_DEPENDENCIES, CompilationUnit
         .getTotalNumberOfEfferentDependencies());
      addMetric(MetricEnum.NUMBER_OF_FORBIDDEN_OUTGOING_COMPILATION_UNIT_DEPENDENCIES, CompilationUnit
         .getNumberOfForbiddenEfferentCompilationUnitDependencies());

      // type
      if (TypeCyclesExist.wasValueSet())
      {
         addMetric(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_TYPES, TypeCyclesExist.getValue() == 1.0 ? true : false);
      }
      else
      {
         addMetricWithNotAnalyzedInfo(MetricEnum.CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_TYPES );
      }

      addMetric(MetricEnum.NUMBER_OF_OUTGOING_TYPE_DEPENDENCIES, Type.getTotalNumberOfEfferentDependencies());
      addMetric(MetricEnum.NUMBER_OF_FORBIDDEN_OUTGOING_TYPE_DEPENDENCIES, Type
         .getNumberOfForbiddenEfferentTypeDependencies());
      addMetric(MetricEnum.NUMBER_OF_PROJECT_INTERNAL_TYPES, getNumberOfProjectInternalTypes());
      addMetric(MetricEnum.NUMBER_OF_PROJECT_EXTERNAL_TYPES, getNumberOfProjectExternalTypes());
      addMetric(MetricEnum.MAX_DEPTH_OF_TYPE_INHERITANCE, Type.getMaxDepthOfInheritance());

      MaxDepthOfInheritance.setValue(Type.getMaxDepthOfInheritance());
      NumberOfProjectInternalTypes.setValue(ParsedType.getNumberOfParsedTypes());
   }

   protected void setAverageComponentDependency(double acd)
   {
      ACD.setValue(acd);
   }

   public void setThresholds(ThresholdIf[] thresholds)
   {
      assert AssertionUtility.checkArray(thresholds);
      this.thresholds = Arrays.copyOf( thresholds, thresholds.length );
   }

   public ThresholdIf[] getThresholds()
   {
      assert thresholds != null;
      return thresholds;
   }

   public DependencyElementIf[] getVerticalSlices()
   {
      return VerticalSlice.getVerticalSlices();
   }

   public boolean existLayerCycles()
   {
      assert ProviderFactory.getInstance().getConfigurationProvider().getMaxLayerCycles() >= -1;
      if (LayerCyclesExist.wasValueSet())
      {
         return LayerCyclesExist.getValue() == 1.0;
      }
      else
      {
         return false;
      }
   }

   public boolean existVerticalSliceCycles()
   {
      assert ProviderFactory.getInstance().getConfigurationProvider().analyzeVerticalSlices();
      assert ProviderFactory.getInstance().getConfigurationProvider().getMaxVerticalSliceCycles() >= -1;
      if (VerticalSliceCyclesExist.wasValueSet())
      {
         return VerticalSliceCyclesExist.getValue() == 1.0;
      }
      else
      {
         return false;
      }
   }

   public boolean existSubsystemCycles()
   {
      assert ProviderFactory.getInstance().getConfigurationProvider().getMaxSubsystemCycles() >= -1;
      if (SubsystemCyclesExist.wasValueSet())
      {
         return SubsystemCyclesExist.getValue() == 1.0;
      }
      else
      {
         return false;
      }
   }

   public boolean existPackageCycles()
   {
      return PackageCyclesExist.getValue() == 1.0;
   }

   public boolean existCompilationUnitCycles()
   {
      return CompilationUnitCyclesExist.getValue() == 1.0;
   }

   public boolean existTypeCycles()
   {
      return TypeCyclesExist.getValue() == 1.0;
   }

   public void setLayerCyclesExist(boolean cyclesExist)
   {
      LayerCyclesExist.setValue(cyclesExist ? 1.0 : 0.0);
   }

   public void setVerticalSliceCyclesExist(boolean cyclesExist)
   {
      VerticalSliceCyclesExist.setValue(cyclesExist ? 1.0 : 0.0);
   }

   public void setSubsystemCyclesExist(boolean cyclesExist)
   {
      SubsystemCyclesExist.setValue(cyclesExist ? 1.0 : 0.0);
   }

   public void setPackageCyclesExist(boolean cyclesExist)
   {
      PackageCyclesExist.setValue(cyclesExist ? 1.0 : 0.0);
   }

   public void setCompilationUnitCyclesExist(boolean cyclesExist)
   {
      CompilationUnitCyclesExist.setValue(cyclesExist ? 1.0 : 0.0);
   }

   public void setTypeCyclesExist(boolean cyclesExist)
   {
      TypeCyclesExist.setValue(cyclesExist ? 1.0 : 0.0);
   }

   public int getNumberOfProjectInternalLayers()
   {
      return Layer.getProjectInternalLayers().length;
   }

   public int getNumberOfProjectExternalLayers()
   {
      return Layer.getNumberOfLayers() - Layer.getProjectInternalLayers().length;
   }

   public int getNumberOfProjectInternalNotImplementedSubsystems()
   {
      return Subsystem.getNumberOfProjectInternalNotImplementedSubsystems();
   }

   public int getNumberOfProjectExternalNotImplementedSubsystems()
   {
      return Subsystem.getNumberOfProjectExternalNotImplementedSubsystems();
   }

   public int getNumberOfProjectInternalSubsystems()
   {
      return Subsystem.getProjectInternalSubsystems().length;
   }

   public int getNumberOfProjectExternalSubsystems()
   {
      return Subsystem.getNumberOfSubsystems() - Subsystem.getProjectInternalSubsystems().length;
   }

   public DependencyElementIf[] getLayers()
   {
      return Layer.getLayers();
   }

   public int getNumberOfForbiddenEfferentLayerDependencies()
   {
      return Layer.getNumberOfForbiddenEfferentLayerDependencies();
   }

   public int getNumberOfForbiddenEfferentVerticalSliceDependencies()
   {
      assert ProviderFactory.getInstance().getConfigurationProvider().analyzeVerticalSlices();
      return VerticalSlice.getNumberOfForbiddenEfferentVerticalSliceDependencies();
   }

   public DirectedDependencyIf[] getForbiddenEfferentLayerDependencies()
   {
      return Layer.getForbiddenEfferentLayerDependencies();
   }

   public DependencyElementIf[] getSubsystems()
   {
      return Subsystem.getSubsystems();
   }

   public PackageFilterIf getSubsystemPackageFilter(DependencyElementIf subsystem)
   {
      assert subsystem != null;
      return Subsystem.getPackageFilter(subsystem);
   }

   public int getNumberOfProjectInternalPackages()
   {
      return ParsedPackage.getNumberOfParsedPackages();
   }

   public int getNumberOfForbiddenEfferentSubsystemDependencies()
   {
      return Subsystem.getNumberOfForbiddenEfferentSubsystemDependencies();
   }

   public DirectedDependencyIf[] getForbiddenEfferentSubsystemDependencies()
   {
      return Subsystem.getForbiddenEfferentSubsystemDependencies();
   }

   public int getNumberOfProjectExternalPackages()
   {
      return NotParsedPackage.getNumberOfNotParsedPackages();
   }

   public int getNumberOfProjectInternalNotAssignedPackages()
   {
      return Package.getNumberOfProjectInternalNotAssignedPackages();
   }

   public int getNumberOfProjectExternalNotAssignedPackages()
   {
      return Package.getNumberOfProjectExternalNotAssignedPackages();
   }

   public DependencyElementIf[] getPackages()
   {
      return Package.getPackages();
   }

   public DependencyElementIf[] getNotAssignedPackages()
   {
      return Package.getNotAssignedPackages();
   }

   public int getNumberOfProjectInternalCompilationUnits()
   {
      return ParsedCompilationUnit.getNumberOfParsedCompilationUnits();
   }

   public int getNumberOfProjectExternalCompilationUnits()
   {
      return NotParsedCompilationUnit.getNumberOfNotParsedCompilationUnits();
   }

   public DependencyElementIf[] getCompilationUnits()
   {
      return CompilationUnit.getCompilationUnits();
   }

   public int getNumberOfProjectInternalTypes()
   {
      return ParsedType.getNumberOfParsedTypes();
   }

   public int getNumberOfProjectExternalTypes()
   {
      return NotParsedType.getNumberOfNotParsedTypes();
   }

   public DependencyElementIf[] getTypes()
   {
      return Type.getTypes();
   }

   public DependencyElementIf[] getNotImplementedProjectInternalSubsystems()
   {
      return Subsystem.getNotImplementedSubsystems();
   }

   public int getNumberOfForbiddenEfferentPackageDependencies()
   {
      return Package.getNumberOfForbiddenEfferentPackageDependencies();
   }

   public DirectedDependencyIf[] getForbiddenEfferentPackageDependencies()
   {
      return Package.getForbiddenEfferentPackageDependencies();
   }

   public int getNumberOfForbiddenEfferentCompilationUnitDependencies()
   {
      return CompilationUnit.getNumberOfForbiddenEfferentCompilationUnitDependencies();
   }

   public DirectedDependencyIf[] getForbiddenEfferentCompilationUnitDependencies()
   {
      return CompilationUnit.getForbiddenEfferentCompilationUnitDependencies();
   }

   public int getNumberOfForbiddenEfferentTypeDependencies()
   {
      return Type.getNumberOfForbiddenEfferentTypeDependencies();
   }

   public DirectedDependencyIf[] getForbiddenEfferentTypeDependencies()
   {
      return Type.getForbiddenEfferentTypeDependencies();
   }

   public int getNumberOfUnusedDefinedEfferentLayerDependencies()
   {
      return Layer.getNumberOfUnusedEfferentLayerDependencies();
   }

   public DirectedDependencyIf[] getUnusedDefinedEfferentLayerDependencies()
   {
      return Layer.getUnusedEfferentLayerDependencies();
   }

   public int getNumberOfUnusedDefinedEfferentSubsystemDependencies()
   {
      return Subsystem.getNumberOfUnusedEfferentSubsystemDependencies();
   }

   public DirectedDependencyIf[] getUnusedDefinedEfferentSubsystemDependencies()
   {
      return Subsystem.getUnusedEfferentSubsystemDependencies();
   }

   public int getNumberOfUnusedDefinedEfferentPackageDependencies()
   {
      return Package.getNumberOfUnusedEfferentPackageDependencies();
   }

   public DirectedDependencyIf[] getUnusedDefinedEfferentPackageDependencies()
   {
      return Package.getUnusedEfferentPackageDependencies();
   }

   public int getNumberOfVerticalSlices()
   {
      assert ProviderFactory.getInstance().getConfigurationProvider().analyzeVerticalSlices();
      return VerticalSlice.getNumberOfVerticalSlices();
   }

   public DependencyElementIf[] getRelevantLayersForVerticalSlices()
   {
      assert ProviderFactory.getInstance().getConfigurationProvider().analyzeVerticalSlices();
      return VerticalSlice.getRelevantLayers();
   }

   public DirectedDependencyIf[] getForbiddenEfferentVerticalSliceDependencies()
   {
      assert ProviderFactory.getInstance().getConfigurationProvider().analyzeVerticalSlices();
      return VerticalSlice.getForbiddenEfferentVerticalSliceDependencies();
   }

   public String getLayerElementName()
   {
      return EntityTypeEnum.LAYER.getEntityName();
   }

   public String getVerticalSliceElementName()
   {
      return EntityTypeEnum.VERTICAL_SLICE.getEntityName();
   }

   public String getSubsystemElementName()
   {
      return EntityTypeEnum.SUBSYSTEM.getEntityName();
   }

   public String getPackageElementName()
   {
      return EntityTypeEnum.PACKAGE.getEntityName();
   }

   public String getCompilationUnitElementName()
   {
      return EntityTypeEnum.COMPILATION_UNIT.getEntityName();
   }

   public String getTypeElementName()
   {
      return EntityTypeEnum.TYPE.getEntityName();
   }

   public MetricDefinitionIf[] getMetricDefinitions()
   {
      return MetricDefinition.getMetricDefinitions();
   }

   public SubsystemFilterIf getSubsystemFilter()
   {
      return ProviderFactory.getInstance().getConfigurationProvider().getSubsystemFilter();
   }

   public String getName()
   {
      return super.getFullyQualifiedName();
   }

   public void setCycleMetricForEntity(EntityTypeEnum type, int numberOfCycles)
   {
      switch (type)
      {
         case TYPE:
            addMetric(MetricEnum.NUMBER_OF_TYPE_CYCLES, numberOfCycles);
            break;
         case COMPILATION_UNIT:
            addMetric(MetricEnum.NUMBER_OF_COMPILATION_UNIT_CYCLES, numberOfCycles);
            break;
         case PACKAGE:
            addMetric(MetricEnum.NUMBER_OF_PACKAGE_CYCLES, numberOfCycles);
            break;
         case SUBSYSTEM:
            addMetric(MetricEnum.NUMBER_OF_SUBSYSTEM_CYCLES, numberOfCycles);
            break;
         case LAYER:
            addMetric(MetricEnum.NUMBER_OF_LAYER_CYCLES, numberOfCycles);
            break;
         default:
            assert false : "no cycles handled for " + type.getDisplayName();
      }
   }

   public void setTangleMetricForEntity(EntityTypeEnum type, int numberOfTangles)
   {
      switch (type)
      {
         case TYPE:
            addMetric(MetricEnum.NUMBER_OF_TYPE_TANGLES, numberOfTangles);
            break;
         case COMPILATION_UNIT:
            addMetric(MetricEnum.NUMBER_OF_COMPILATION_UNIT_TANGLES, numberOfTangles);
            break;
         case PACKAGE:
            addMetric(MetricEnum.NUMBER_OF_PACKAGE_TANGLES, numberOfTangles);
            break;
         case SUBSYSTEM:
            addMetric(MetricEnum.NUMBER_OF_SUBSYSTEM_TANGLES, numberOfTangles);
            break;
         case LAYER:
            addMetric(MetricEnum.NUMBER_OF_LAYER_TANGLES, numberOfTangles);
            break;
         default:
            assert false : "no tangles handled for " + type.getDisplayName();
      }
   }
}