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
package com.valtech.source.dependometer.app.controller.main;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.controller.compilationunit.CompilationUnitListener;
import com.valtech.source.dependometer.app.controller.layer.LayerListener;
import com.valtech.source.dependometer.app.controller.pack.PackageListener;
import com.valtech.source.dependometer.app.controller.project.AnalysisFinishedEvent;
import com.valtech.source.dependometer.app.controller.project.DependencyAnalysisListener;
import com.valtech.source.dependometer.app.controller.project.ProjectInfoCollectedEvent;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemListener;
import com.valtech.source.dependometer.app.controller.type.TypeListener;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceListener;
import com.valtech.source.dependometer.app.core.dependencyanalysis.DependencyAnalysisIf;
import com.valtech.source.dependometer.app.core.elements.CompilationUnit;
import com.valtech.source.dependometer.app.core.elements.DependencyElement;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;
import com.valtech.source.dependometer.app.core.elements.Layer;
import com.valtech.source.dependometer.app.core.elements.NotParsedCompilationUnit;
import com.valtech.source.dependometer.app.core.elements.NotParsedPackage;
import com.valtech.source.dependometer.app.core.elements.NotParsedType;
import com.valtech.source.dependometer.app.core.elements.Package;
import com.valtech.source.dependometer.app.core.elements.ParsedClass;
import com.valtech.source.dependometer.app.core.elements.ParsedCompilationUnit;
import com.valtech.source.dependometer.app.core.elements.ParsedInterface;
import com.valtech.source.dependometer.app.core.elements.ParsedPackage;
import com.valtech.source.dependometer.app.core.elements.ParsedType;
import com.valtech.source.dependometer.app.core.elements.Project;
import com.valtech.source.dependometer.app.core.elements.Subsystem;
import com.valtech.source.dependometer.app.core.elements.Type;
import com.valtech.source.dependometer.app.core.elements.VerticalSlice;
import com.valtech.source.dependometer.app.core.fts14.FTSReductor;
import com.valtech.source.dependometer.app.core.metrics.MetricsProvider;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.IProviderFactory;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.ThresholdIf;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionProviderIf;
import com.valtech.source.dependometer.app.core.queryinfo.ThresholdManager;

/**
 * Controls the main flow of dependency element creation and analysis
 * 
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class DependencyManager extends Thread
{
   private static Logger s_Logger = Logger.getLogger(DependencyManager.class.getName());

   private final List<CycleAnalysisThread> m_CycleAnalysisThreads = new ArrayList<CycleAnalysisThread>(6);

   private ConfigurationProviderIf m_ConfigurationProvider;

   private TypeDefinitionProviderIf m_TypeDefinitionProvider;

   private Project project;

   private boolean m_IsFirstAnalysisStepFinished;

   private DependencyAnalysisIf m_VerticalSliceAnalysis;

   private DependencyAnalysisIf m_LayerDependencyAnalysis;

   private DependencyAnalysisIf m_SubsystemDependencyAnalysis;

   private DependencyAnalysisIf m_PackageDependencyAnalysis;

   private DependencyAnalysisIf m_CompilationUnitDependencyAnalysis;

   private DependencyAnalysisIf m_TypeDependencyAnalysis;

   private ThresholdManager thresholdManager;

   private DependometerContext dependometerContext;
   
   private Date m_timeStop = null;

   public DependencyManager(DependometerContext dependometerContext, Date timeStop)
   {
      this.dependometerContext = dependometerContext;
      // runs not with java1.5 and maven dependometer-plugin!
      // Runtime.getRuntime().addShutdownHook(this);
      thresholdManager = new ThresholdManager();
      
      m_timeStop = timeStop;
   }

   private void finishAnalyis(EntityTypeEnum type, DependencyAnalysisIf cycleAnalysis, int maxCycles)
   {
      assert type != null;
      assert cycleAnalysis != null;

      if (!cycleAnalysis.hasCycles())
      {
         s_Logger.info("analyzing " + type.getDisplayName() + " levels ...");
         cycleAnalysis.analyzeLevels();
      }
      else if (maxCycles >= 0)
      {
         s_Logger.info("analyzing " + type.getDisplayName() + " cycle participation ...");
         cycleAnalysis.analyzeCyclePartizipation();
         s_Logger.info("analyzing " + type.getDisplayName() + " levels ...");
         cycleAnalysis.analyzeLevels();

         project.setCycleMetricForEntity(type, cycleAnalysis.getNumberOfDetectedCycles());
         project.setTangleMetricForEntity(type, cycleAnalysis.getNumberOfDetectedTangles());
      }
   }

   public void run()
   {
      synchronized (this)
      {
         if (m_IsFirstAnalysisStepFinished)
         {
            //wait for cycles analysis threads to finsih
            for (CycleAnalysisThread cycleThread:m_CycleAnalysisThreads)
            {
               cycleThread.finish();
               try
               {
                  cycleThread.join();
               }
               catch (InterruptedException e)
               {
                  // assert false;
               }
            }

            if (m_LayerDependencyAnalysis != null)
            {
               finishAnalyis(EntityTypeEnum.LAYER, m_LayerDependencyAnalysis, m_ConfigurationProvider
                  .getMaxLayerCycles());
            }
            if (m_VerticalSliceAnalysis != null)
            {
               finishAnalyis(EntityTypeEnum.VERTICAL_SLICE, m_VerticalSliceAnalysis, m_ConfigurationProvider
                  .getMaxVerticalSliceCycles());
            }
            if (m_SubsystemDependencyAnalysis != null)
            {
               finishAnalyis(EntityTypeEnum.SUBSYSTEM, m_SubsystemDependencyAnalysis, m_ConfigurationProvider
                  .getMaxSubsystemCycles());
            }
            if (m_PackageDependencyAnalysis != null)
            {
               finishAnalyis(EntityTypeEnum.PACKAGE, m_PackageDependencyAnalysis, m_ConfigurationProvider
                  .getMaxPackageCycles());
            }
            if (m_CompilationUnitDependencyAnalysis != null)
            {
               finishAnalyis(EntityTypeEnum.COMPILATION_UNIT, m_CompilationUnitDependencyAnalysis,
                  m_ConfigurationProvider.getMaxCompilationUnitCycles());
            }
            if (m_TypeDependencyAnalysis != null)
            {
               finishAnalyis(EntityTypeEnum.TYPE, m_TypeDependencyAnalysis, m_ConfigurationProvider.getMaxTypeCycles());
            }

            dependometerContext.getProjectManager().dispatch(new AnalysisFinishedEvent(project));
            s_Logger.info("[finished]");
         }
      }
   }

   private void calculateMetrics()
   {
      s_Logger.info("calculating metrics ...");

      MetricsProvider[] provider = MetricsProvider.getMetricsProvider();
      for (int i = 0; i < provider.length; i++)
      {
         provider[i].prepareCollectionOfMetrics();
      }

      Type[] types = Type.getTypes();
      for (int i = 0; i < types.length; ++i)
      {
         types[i].collectMetrics();
      }

      CompilationUnit[] compilationUnits = CompilationUnit.getCompilationUnits();
      for (int i = 0; i < compilationUnits.length; ++i)
      {
         compilationUnits[i].collectMetrics();
      }

      Package[] packages = Package.getPackages();
      for (int i = 0; i < packages.length; ++i)
      {
         packages[i].collectMetrics();
      }

      Subsystem[] subsystems = Subsystem.getSubsystems();
      for (int i = 0; i < subsystems.length; ++i)
      {
         subsystems[i].collectMetrics();
      }

      Layer[] layers = Layer.getLayers();
      for (int i = 0; i < layers.length; ++i)
      {
         layers[i].collectMetrics();
      }

      if (m_ConfigurationProvider.analyzeVerticalSlices())
      {
         VerticalSlice[] verticalSlices = VerticalSlice.getVerticalSlices();
         for (int i = 0; i < verticalSlices.length; i++)
         {
            verticalSlices[i].collectMetrics();
         }
      }
      project.collectMetrics();
   }

   private void checkThresholds()
   {
      s_Logger.info("checking thresholds ...");
      ThresholdIf[] thresholds = thresholdManager.getThresholds();
      for (int i = 0; i < thresholds.length; i++)
      {
         ThresholdIf next = thresholds[i];
         if (next.isSupported())
         {
            if (next.wasAnalyzed())
            {
               if (next.wasViolated())
               {
                  String type = next.isLowerThreshold() ? "lower" : "upper";
                  s_Logger.warn(type + " threshold violation for '" + next.getQueryId() + "' - threshold/value = "
                     + next.getThreshold() + "/" + next.getValue());
               }
            }
            else
            {
               s_Logger.warn("threshold check was not possible - QueryInfo with id '" + next.getQueryId()
                  + "' not analyzed");
            }
         }
         else
         {
            s_Logger.warn("threshold check was not possible - QueryInfo with id '" + next.getQueryId()
               + "' not supported");
         }
      }
      project.setThresholds(thresholds);

   }

   private void processConfiguration(ConfigurationProviderIf provider)
   {
      assert provider != null;
      project = new Project(provider.getProjectName(), provider.getSource());
      processLogicalArchitectureElements(provider);
      DependencyAnalysisListener.setCycleFeedback(provider.getCycleFeedback());
      DependencyAnalysisListener.setProgressFeedback(provider.getCycleAnalysisProgressFeedback());
      processThresholdDefinitions(provider);
   }

   private void processThresholdDefinitions(ConfigurationProviderIf provider)
   {
      assert provider != null;
      thresholdManager.createLowerThresholds(provider.getLowerThresholds());
      thresholdManager.createUpperThresholds(provider.getUpperThresholds());
   }

   public synchronized ProjectIf analyze() throws Exception
   {
      IProviderFactory providerFactory = ProviderFactory.getInstance();
      return analyze(providerFactory.getConfigurationProvider(), providerFactory.getTypeDefinitionProvider());
   }

   private synchronized ProjectIf analyze(ConfigurationProviderIf configurationProvider,
      TypeDefinitionProviderIf typeDefinitionProvider) throws Exception
   {
      m_ConfigurationProvider = configurationProvider;
      m_TypeDefinitionProvider = typeDefinitionProvider;

      processConfiguration(m_ConfigurationProvider);

      // Check sanity
      int ccdOptionsCount = 0;
      if (m_ConfigurationProvider.cumulateTypeDependencies())
         ccdOptionsCount++;
      if (m_ConfigurationProvider.cumulateCompilationUnitDependencies())
         ccdOptionsCount++;
      if (m_ConfigurationProvider.cumulatePackageDependencies())
         ccdOptionsCount++;
      if (m_ConfigurationProvider.cumulateSubsystemDependencies())
         ccdOptionsCount++;
      if (m_ConfigurationProvider.cumulateLayerDependencies())
         ccdOptionsCount++;

      if (ccdOptionsCount > 1)
      {
         s_Logger.warn("You have configured to calculate ACD/CCD on more than on level."
            + "The currently implemented output writers can only show one result and can not"
            + " tell you what result they show. What result is actually shown will depend on"
            + " subtle timing diffences." + " In case you are open to good advice:"
            + " Please set cumulateCompilationUnitDependencies to true and the rest to false.");
      }

      // Types
      TypeDefinitionIf[] typeDefinitions = m_TypeDefinitionProvider.getTypeDefinitions();
      ParsedType[] parsedTypes = createParsedTypes(typeDefinitions);
      analyzeTypes(parsedTypes);

      int numberOfSkippedProjectInternalTypeDependencies = checkSkippedTypes(typeDefinitions);
      if (numberOfSkippedProjectInternalTypeDependencies > 0)
      {
         // Most dependometer config errors give InvalidArgument
         // exceptions.
         // But this type of error occurs too late.
         // You do not like an empty result after hours of parsing C++.
         s_Logger.fatal("There are " + numberOfSkippedProjectInternalTypeDependencies
            + " skipped dependencies to project internal types. Please fix your dependometer config");
      }

      // TODO fast prefilter types with no dependencies, just outgoing or just incoming dependendies
      // to save resources?

      m_TypeDependencyAnalysis = analyzeCycles(Type.ELEMENT_NAME,
         new TypeListener(dependometerContext.getTypeManager()), m_ConfigurationProvider.getMaxTypeCycles(),
         m_ConfigurationProvider.cumulateTypeDependencies(), parsedTypes);

      if (m_TypeDependencyAnalysis != null)
      {
         project.setTypeCyclesExist(m_TypeDependencyAnalysis.hasCycles());
      }

      // CompilationUnits
      analyzeCompilationUnitDependencies();
      m_CompilationUnitDependencyAnalysis = analyzeCycles(CompilationUnit.ELEMENT_NAME, new CompilationUnitListener(
         dependometerContext.getCompilationUnitManager()), m_ConfigurationProvider.getMaxCompilationUnitCycles(),
         m_ConfigurationProvider.cumulateCompilationUnitDependencies(), ParsedCompilationUnit
            .getParsedCompilationUnits());

      if (m_CompilationUnitDependencyAnalysis != null)
      {
         project.setCompilationUnitCyclesExist(m_CompilationUnitDependencyAnalysis.hasCycles());
      }

      // Packages
      if (m_ConfigurationProvider.checkPackageDependencies())
      {
         processAllowedPackageDependencies(m_ConfigurationProvider);
      }
      analyzePackageDependencies();

      m_PackageDependencyAnalysis = analyzeCycles(Package.ELEMENT_NAME, new PackageListener(dependometerContext
         .getPackageManager()), m_ConfigurationProvider.getMaxPackageCycles(), m_ConfigurationProvider
         .cumulatePackageDependencies(), ParsedPackage.getParsedPackages());
      if (m_PackageDependencyAnalysis != null)
      {
         project.setPackageCyclesExist(m_PackageDependencyAnalysis.hasCycles());
      }

      // Subsystems
      if (Subsystem.getNumberOfSubsystems() > 0)
      {
         analyzeSubsystemDependencies();
         Subsystem[] subsystems = Subsystem.getProjectInternalSubsystems();
         m_SubsystemDependencyAnalysis = analyzeCycles(Subsystem.ELEMENT_NAME, new SubsystemListener(
            dependometerContext.getSubsystemManager()), m_ConfigurationProvider.getMaxSubsystemCycles(),
            m_ConfigurationProvider.cumulateSubsystemDependencies(), subsystems);
         if (m_SubsystemDependencyAnalysis != null)
         {
            project.setSubsystemCyclesExist(m_SubsystemDependencyAnalysis.hasCycles());
         }
      }

      // Layers
      if (Layer.getNumberOfLayers() > 0)
      {
         analyzeLayerDependencies();
         Layer[] layers = Layer.getProjectInternalLayers();
         m_LayerDependencyAnalysis = analyzeCycles(Layer.ELEMENT_NAME, new LayerListener(dependometerContext
            .getLayerManager()), m_ConfigurationProvider.getMaxLayerCycles(), m_ConfigurationProvider
            .cumulateLayerDependencies(), layers);
         if (m_LayerDependencyAnalysis != null)
         {
            project.setLayerCyclesExist(m_LayerDependencyAnalysis.hasCycles());
         }
      }

      // Vertical slices
      if (m_ConfigurationProvider.analyzeVerticalSlices() && Subsystem.getNumberOfSubsystems() > 0)
      {
         analyzeVerticalSliceDependencies();
         VerticalSlice[] verticalSlices = VerticalSlice.getVerticalSlices();
         m_VerticalSliceAnalysis = analyzeCycles(VerticalSlice.ELEMENT_NAME, new VerticalSliceListener(
            dependometerContext.getVerticalSliceManager()), m_ConfigurationProvider.getMaxVerticalSliceCycles(),
            m_ConfigurationProvider.cumulateVerticalSliceDependencies(), verticalSlices);
         if (m_VerticalSliceAnalysis != null)
         {
            project.setVerticalSliceCyclesExist(m_VerticalSliceAnalysis.hasCycles());
         }
      }

      calculateMetrics();
      checkThresholds();

      dependometerContext.getProjectManager().dispatch(new ProjectInfoCollectedEvent(project));
      m_IsFirstAnalysisStepFinished = true;
      if (m_ConfigurationProvider.isCycleAnalysisEnabled())
      {
         startCycleAnalysis();
      }
      return project;
   }

   private synchronized void startCycleAnalyisThread(CycleAnalysisThread thread)
   {
      assert thread != null;
      m_CycleAnalysisThreads.add(thread);
      thread.start();
   }

   private void startCycleAnalysis()
   {
      if (m_LayerDependencyAnalysis != null && m_ConfigurationProvider.getMaxLayerCycles() >= 0
         && m_LayerDependencyAnalysis.hasCycles())
      {
         startCycleAnalyisThread(new CycleAnalysisThread(m_LayerDependencyAnalysis, m_ConfigurationProvider
            .getMaxLayerCycles(),EntityTypeEnum.LAYER, m_timeStop));
      }

      if (m_VerticalSliceAnalysis != null && m_ConfigurationProvider.getMaxVerticalSliceCycles() >= 0
         && m_VerticalSliceAnalysis.hasCycles())
      {
         startCycleAnalyisThread(new CycleAnalysisThread(m_VerticalSliceAnalysis, m_ConfigurationProvider

            .getMaxVerticalSliceCycles(), EntityTypeEnum.VERTICAL_SLICE, m_timeStop));
      }

      if (m_SubsystemDependencyAnalysis != null && m_ConfigurationProvider.getMaxSubsystemCycles() >= 0
         && m_SubsystemDependencyAnalysis.hasCycles())
      {
         startCycleAnalyisThread(new CycleAnalysisThread(m_SubsystemDependencyAnalysis, m_ConfigurationProvider
            .getMaxSubsystemCycles(), EntityTypeEnum.SUBSYSTEM, m_timeStop));
      }

      if (m_PackageDependencyAnalysis != null && m_ConfigurationProvider.getMaxPackageCycles() >= 0
         && m_PackageDependencyAnalysis.hasCycles())
      {
         startCycleAnalyisThread(new CycleAnalysisThread(m_PackageDependencyAnalysis, m_ConfigurationProvider
            .getMaxPackageCycles(), EntityTypeEnum.PACKAGE, m_timeStop));
      }

      if (m_CompilationUnitDependencyAnalysis != null && m_ConfigurationProvider.getMaxCompilationUnitCycles() >= 0
         && m_CompilationUnitDependencyAnalysis.hasCycles())
      {
         startCycleAnalyisThread(new CycleAnalysisThread(m_CompilationUnitDependencyAnalysis, m_ConfigurationProvider
            .getMaxCompilationUnitCycles(), EntityTypeEnum.COMPILATION_UNIT, m_timeStop));
      }

      if (m_TypeDependencyAnalysis != null && m_ConfigurationProvider.getMaxTypeCycles() >= 0
         && m_TypeDependencyAnalysis.hasCycles())
      {
         startCycleAnalyisThread(new CycleAnalysisThread(m_TypeDependencyAnalysis, m_ConfigurationProvider
            .getMaxTypeCycles(), EntityTypeEnum.TYPE, m_timeStop));
      }

      if (m_CycleAnalysisThreads.size() > 0)
      {
         s_Logger.info("terminate running analysis with 'ctrl-c'!");
      }
   }

   private DependencyAnalysisIf analyzeCycles(String elementType, DependencyAnalysisListener listener, int maxCycles,
      boolean cumulate, DependencyElement[] elements)
   {
      assert elementType != null;
      assert elementType.length() > 0;
      assert listener != null;
      assert AssertionUtility.checkArray(elements);

      DependencyAnalysisIf cycleAnalysis = null;

      if (maxCycles >= -1 || cumulate)
      {
         cycleAnalysis = new FTSReductor(elements, listener);
      }

      if (cumulate)
      {
         s_Logger.info("cumulating " + elementType + " dependencies ...");
         cycleAnalysis.cumulateNodeDependencies();
      }
      else
      {
         s_Logger.warn("dependency cumulation for " + elementType + "s disabled!");
      }

      if (maxCycles >= -1)
      {
         s_Logger.info("checking " + elementType + " cycle existence ...");
         if (!cycleAnalysis.hasCycles())
         {
            s_Logger.info("no " + elementType + " cycles detected!");
         }
         else
         {
            s_Logger.warn("detected " + elementType + " cycle!");
         }
      }
      else
      {
         cycleAnalysis = null;
         s_Logger.warn("cycle detection for " + elementType + "s disabled!");
      }

      return cycleAnalysis;
   }

   private int checkSkippedTypes(TypeDefinitionIf[] typeDefinitions)
   {
      assert AssertionUtility.checkArray(typeDefinitions);

      s_Logger.info("checking skipped " + Type.ELEMENT_NAME + " dependencies ...");
      int number = 0;

      for (int i = 0; i < typeDefinitions.length; i++)
      {
         TypeDefinitionIf nextTypeDefinition = typeDefinitions[i];
         String[] skipped = nextTypeDefinition.getSkippedFullyQualifiedImportedTypeNames();
         for (int j = 0; j < skipped.length; j++)
         {
            Type skippedType = Type.getType(skipped[j]);
            if (skippedType != null && skippedType.belongsToProject())
            {
               s_Logger.warn("skipped dependency to project internal type (from -> to) = "
                  + nextTypeDefinition.getFullyQualifiedTypeName() + " -> " + skipped[j]);
               number++;
            }
         }
      }

      return number;
   }

   private ParsedType[] createParsedTypes(TypeDefinitionIf[] typeDefinitions)
   {
      assert AssertionUtility.checkArray(typeDefinitions);

      ParsedType[] parsedTypes = new ParsedType[typeDefinitions.length];
      for (int i = 0; i < typeDefinitions.length; ++i)
      {
         TypeDefinitionIf nextTypeDefinition = typeDefinitions[i];
         ParsedType createdParsedType = null;
         if (nextTypeDefinition.isInterface())
         {
            createdParsedType = new ParsedInterface(nextTypeDefinition.getFullyQualifiedTypeName(), nextTypeDefinition
               .getRelativeCompilationUnitPath(), nextTypeDefinition.getFullyQualifiedSuperClassNames(),
               nextTypeDefinition.getFullyQualifiedSuperInterfaceNames(), nextTypeDefinition
                  .getFullyQualifiedImportedTypeNames(), nextTypeDefinition.isExtendable(), nextTypeDefinition
                  .getSource(), nextTypeDefinition.getAbsoluteSourcePath());
         }
         else
         {
            createdParsedType = new ParsedClass(nextTypeDefinition.getFullyQualifiedTypeName(), nextTypeDefinition
               .getRelativeCompilationUnitPath(), nextTypeDefinition.isAbstract(), nextTypeDefinition.isAccessible(),
               nextTypeDefinition.isExtendable(), nextTypeDefinition.getFullyQualifiedSuperClassNames(),
               nextTypeDefinition.getFullyQualifiedSuperInterfaceNames(), nextTypeDefinition
                  .getFullyQualifiedImportedTypeNames(), nextTypeDefinition.getNumberOfAssertions(), nextTypeDefinition
                  .getSource(), nextTypeDefinition.getAbsoluteSourcePath());
         }

         assert createdParsedType != null;
         createdParsedType.wasRefactored(nextTypeDefinition.wasRefactored());
         parsedTypes[i] = createdParsedType;
      }

      return parsedTypes;
   }

   private void analyzeTypes(ParsedType[] parsedTypes)
   {
      assert AssertionUtility.checkArray(parsedTypes);
      s_Logger.info("analyzing " + Type.ELEMENT_NAME + " dependencies ...");

      for (int i = 0; i < parsedTypes.length; ++i)
      {
         ParsedType nextParsedType = parsedTypes[i];

         String compilationUnitName = nextParsedType.getCompilationUnitName();
         String packageName = nextParsedType.getPackageName();
         CompilationUnit compilationUnit = CompilationUnit.getCompilationUnit(compilationUnitName);
         if (compilationUnit == null)
         {
            compilationUnit = new ParsedCompilationUnit(compilationUnitName, packageName);
         }

         compilationUnit.addType(nextParsedType);

         if (Package.getPackage(packageName) == null)
         {
            new ParsedPackage(packageName);
         }
      }

      for (int i = 0; i < parsedTypes.length; ++i)
      {
         parsedTypes[i].resolveTypes();
      }

      NotParsedType[] notParsedTypes = NotParsedType.getNotParsedTypes();
      for (int i = 0; i < notParsedTypes.length; ++i)
      {
         NotParsedType nextNotParsedType = notParsedTypes[i];
         String compilationUnitName = nextNotParsedType.getCompilationUnitName();
         String packageName = nextNotParsedType.getPackageName();
         CompilationUnit compilationUnit = CompilationUnit.getCompilationUnit(compilationUnitName);

         if (compilationUnit == null)
         {
            compilationUnit = new NotParsedCompilationUnit(compilationUnitName, packageName);
         }

         compilationUnit.addType(nextNotParsedType);

         if (Package.getPackage(packageName) == null)
         {
            new NotParsedPackage(packageName);
         }
      }
   }

   private void analyzeCompilationUnitDependencies()
   {
      s_Logger.info("analyzing " + CompilationUnit.ELEMENT_NAME + " dependencies ...");
      CompilationUnit[] compilationUnits = CompilationUnit.getCompilationUnits();
      for (int i = 0; i < compilationUnits.length; i++)
      {
         CompilationUnit compilationUnit = compilationUnits[i];
         compilationUnit.analyzeDependencies();
         String packageName = compilationUnit.getPackageName();
         Package javaPackage = Package.getPackage(packageName);

         if (javaPackage == null)
         {
            javaPackage = new ParsedPackage(packageName);
         }

         javaPackage.addCompilationUnit(compilationUnit);
      }
   }

   private void analyzePackageDependencies()
   {
      s_Logger.info("analyzing " + Package.ELEMENT_NAME + " dependencies ...");

      Package[] packages = Package.getPackages();
      for (int i = 0; i < packages.length; i++)
      {
         Package javaPackage = packages[i];
         javaPackage.analyzeDependencies();
         Subsystem[] subsystems = Subsystem.isAssignedToSubsystems(javaPackage);
         if ((subsystems != null) && (subsystems.length != 0))
         {
            if (subsystems.length == 1)
            {
               subsystems[0].addPackage(javaPackage);
            }
            else
            {
               StringBuffer str = new StringBuffer();
               for (int j = 0; j < subsystems.length; j++)
               {
                  str.append(subsystems[j].getFullyQualifiedName());
                  if (j < subsystems.length - 1)
                  {
                     str.append(", ");
                  }
               }
               throw new IllegalArgumentException("package " + javaPackage.getFullyQualifiedName() + " is assigned to "
                  + subsystems.length + " subsystems: " + str.toString());

            }
         }
      }

      if (m_ConfigurationProvider.checkPackageDependencies())
      {
         for (int i = 0; i < packages.length; i++)
         {
            packages[i].checkEfferents();
         }
      }
   }

   private void analyzeSubsystemDependencies()
   {
      s_Logger.info("analyzing " + Subsystem.ELEMENT_NAME + " dependencies ...");
      Subsystem[] subsystems = Subsystem.getSubsystems();

      for (int i = 0; i < subsystems.length; ++i)
      {
         subsystems[i].analyzeDependencies();
      }

      if (m_ConfigurationProvider.checkSubsystemDependencies())
      {
         for (int i = 0; i < subsystems.length; ++i)
         {
            subsystems[i].checkEfferents();
         }
      }
   }

   private void analyzeVerticalSliceDependencies()
   {
      s_Logger.info("analyzing " + VerticalSlice.ELEMENT_NAME + " dependencies ...");
      Subsystem[] subsystems = Subsystem.getProjectInternalSubsystems();
      VerticalSlice.createVerticalSlices(subsystems, m_ConfigurationProvider.getSubsystemFilter());
      VerticalSlice[] verticalSlices = VerticalSlice.getVerticalSlices();

      for (int i = 0; i < verticalSlices.length; ++i)
      {
         verticalSlices[i].analyzeDependencies();
      }

      if (m_ConfigurationProvider.checkSubsystemDependencies()
         && m_ConfigurationProvider.checkVerticalSliceDependencies())
      {
         for (int i = 0; i < verticalSlices.length; ++i)
         {
            verticalSlices[i].checkEfferents();
         }
      }
   }

   private void analyzeLayerDependencies()
   {
      s_Logger.info("analyzing " + Layer.ELEMENT_NAME + " dependencies ...");
      Layer[] layers = Layer.getLayers();

      for (int i = 0; i < layers.length; ++i)
      {
         layers[i].addTypesAndCompilationUnits();
      }

      for (int i = 0; i < layers.length; ++i)
      {
         layers[i].analyzeDependencies();
      }

      if (m_ConfigurationProvider.checkLayerDependencies())
      {
         for (int i = 0; i < layers.length; ++i)
         {
            layers[i].checkEfferents();
         }
      }
   }

   private void processLogicalArchitectureElements(ConfigurationProviderIf provider)
   {
      assert provider != null;

      PackageFilterIf projectFilter = provider.getPackageFilter();
      String[] layerNames = provider.getLayers();
      Layer[] createdLayers = new Layer[layerNames.length];
      Object source = provider.getSource();

      for (int i = 0; i < layerNames.length; ++i)
      {
         String layerName = layerNames[i];
         Layer layer = new Layer(layerName, source);
         String description = provider.getLayerDescription(layerName);
         if (description != null)
         {
            layer.setDescription(description);
         }
         createdLayers[i] = layer;
      }

      for (int i = 0; i < layerNames.length; ++i)
      {
         String nextLayerName = layerNames[i];
         Layer nextLayer = createdLayers[i];
         String[] layerDependsUpon = provider.layerDependsUpon(nextLayerName);
         for (int j = 0; j < layerDependsUpon.length; ++j)
         {
            String dependsUponLayerWithName = layerDependsUpon[j];
            if (!Layer.layerExists(dependsUponLayerWithName))
            {
               throw new IllegalArgumentException("depends upon " + Layer.ELEMENT_NAME + " entry '"
                  + dependsUponLayerWithName + "' for " + Layer.ELEMENT_NAME + " '" + nextLayerName
                  + "' is not a defined " + Layer.ELEMENT_NAME);
            }
            else
            {
               Layer dependsUponLayer = Layer.getLayer(dependsUponLayerWithName);
               if (!nextLayer.isAllowedEfferent(dependsUponLayer))
               {
                  nextLayer.addAllowedEfferent(dependsUponLayer);
               }
               else
               {
                  throw new IllegalArgumentException("definition of " + Layer.ELEMENT_NAME + " dependency from "
                     + Layer.ELEMENT_NAME + " '" + nextLayerName + "' to " + Layer.ELEMENT_NAME + " '"
                     + dependsUponLayerWithName + "' alread added");
               }
            }
         }
      }

      String[] subsystemNames = provider.getSubsystems();
      Subsystem[] createdSubsystems = new Subsystem[subsystemNames.length];

      for (int i = 0; i < subsystemNames.length; ++i)
      {
         String nextSubsystemName = subsystemNames[i];
         String belongsToLayerWithName = Subsystem.getLayerName(nextSubsystemName);
         Layer belongsToLayer = Layer.getLayer(belongsToLayerWithName);
         assert belongsToLayer != null;
         PackageFilterIf filter = provider.getSubsystemPackageFilter(nextSubsystemName);
         Subsystem nextSubsystem = belongsToLayer.createSubsystem(nextSubsystemName, filter, projectFilter, source);
         String description = provider.getSubsystemDescription(nextSubsystemName);
         if (description != null)
         {
            nextSubsystem.setDescription(description);
         }
         createdSubsystems[i] = nextSubsystem;
      }

      for (int i = 0; i < subsystemNames.length; ++i)
      {
         String nextSubsystemName = subsystemNames[i];
         Subsystem nextSubsystem = createdSubsystems[i];
         String[] subsystemDependsUpon = provider.subsystemDependsUpon(nextSubsystemName);
         for (int j = 0; j < subsystemDependsUpon.length; ++j)
         {
            String dependsUponSubsystemWithName = subsystemDependsUpon[j];
            if (!Subsystem.subsystemExists(dependsUponSubsystemWithName))
            {
               throw new IllegalArgumentException("depends upon " + Subsystem.ELEMENT_NAME + " entry '"
                  + dependsUponSubsystemWithName + "' for " + Subsystem.ELEMENT_NAME + " '" + nextSubsystemName
                  + "' is not a defined " + Subsystem.ELEMENT_NAME);
            }
            else
            {
               Subsystem dependsUponSubsystem = Subsystem.getSubsystem(dependsUponSubsystemWithName);
               if (!nextSubsystem.isAllowedEfferent(dependsUponSubsystem))
               {
                  nextSubsystem.addAllowedEfferent(dependsUponSubsystem);
               }
               else
               {
                  throw new IllegalArgumentException("definition of " + Subsystem.ELEMENT_NAME + " dependency from "
                     + Subsystem.ELEMENT_NAME + " '" + nextSubsystemName + "' to " + Subsystem.ELEMENT_NAME + " '"
                     + dependsUponSubsystemWithName + "' alread added");
               }
            }
         }
      }
   }

   private void processAllowedPackageDependencies(ConfigurationProviderIf provider)
   {
      assert provider != null;

      String[] packages = provider.getPackages();

      for (int i = 0; i < packages.length; ++i)
      {
         String nextPackageName = packages[i];
         Package nextPackage = Package.getPackage(nextPackageName);
         if (nextPackage != null)
         {
            String description = provider.getPackageDescription(nextPackageName);
            nextPackage.setCheckEfferents(true);
            if (description != null)
            {
               nextPackage.setDescription(description);
            }
            String[] dependsUpon = provider.packageDependsUpon(nextPackageName);
            for (int j = 0; j < dependsUpon.length; ++j)
            {
               String nextDependsUponPackageWithName = dependsUpon[j];
               Package nextDependsUponPackage = Package.getPackage(nextDependsUponPackageWithName);
               if (nextDependsUponPackage != null)
               {
                  nextPackage.addAllowedEfferent(nextDependsUponPackage);
               }
               else
               {
                  throw new IllegalArgumentException("depends upon " + Package.ELEMENT_NAME + " entry '"
                     + nextDependsUponPackageWithName + "' for " + Package.ELEMENT_NAME + " '" + nextPackageName
                     + "' is not a defined " + Package.ELEMENT_NAME);
               }
            }
         }
         else
         {
            s_Logger.warn(Package.ELEMENT_NAME + " '" + nextPackageName + "' not found - unable to set allowed "
               + Package.ELEMENT_NAME + " dependencies");
         }
      }
   }

   public Project getProject()
   {
      return project;
   }
}