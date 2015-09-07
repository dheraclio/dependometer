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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.controller.compilationunit.CompilationUnitCycleParticipantsCollectedEvent;
import com.valtech.source.dependometer.app.controller.compilationunit.CompilationUnitCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.compilationunit.CompilationUnitLevelCollectedEvent;
import com.valtech.source.dependometer.app.controller.compilationunit.CompilationUnitManager;
import com.valtech.source.dependometer.app.controller.compilationunit.CompilationUnitTangleCollectedEvent;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleCompilationUnitCycleParticipantsCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleCompilationUnitCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleCompilationUnitLevelCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleCompilationUnitTangleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleMultiplePackageCompilationUnitCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleSinglePackageCompilationUnitCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.MultiplePackageCompilationUnitCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.compilationunit.SinglePackageCompilationUnitCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.layer.HandleLayerCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.layer.HandleLayerCycleParticipantsCollectedEventIf;
import com.valtech.source.dependometer.app.controller.layer.HandleLayerCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.layer.HandleLayerLevelCollectedEventIf;
import com.valtech.source.dependometer.app.controller.layer.HandleLayerTangleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.layer.LayerCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.layer.LayerCycleParticipantsCollectedEvent;
import com.valtech.source.dependometer.app.controller.layer.LayerCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.layer.LayerLevelCollectedEvent;
import com.valtech.source.dependometer.app.controller.layer.LayerManager;
import com.valtech.source.dependometer.app.controller.layer.LayerTangleCollectedEvent;
import com.valtech.source.dependometer.app.controller.main.DependometerContext;
import com.valtech.source.dependometer.app.controller.pack.HandlePackageCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.pack.HandlePackageCycleParticipantsCollectedEventIf;
import com.valtech.source.dependometer.app.controller.pack.HandlePackageCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.pack.HandlePackageLevelCollectedEventIf;
import com.valtech.source.dependometer.app.controller.pack.HandlePackageTangleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.pack.PackageCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.pack.PackageCycleParticipantsCollectedEvent;
import com.valtech.source.dependometer.app.controller.pack.PackageCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.pack.PackageLevelCollectedEvent;
import com.valtech.source.dependometer.app.controller.pack.PackageManager;
import com.valtech.source.dependometer.app.controller.pack.PackageTangleCollectedEvent;
import com.valtech.source.dependometer.app.controller.project.AnalysisFinishedEvent;
import com.valtech.source.dependometer.app.controller.project.HandleAnalysisFinishedEventIf;
import com.valtech.source.dependometer.app.controller.project.HandleProjectInfoCollectedEventIf;
import com.valtech.source.dependometer.app.controller.project.LevelCollectedEvent;
import com.valtech.source.dependometer.app.controller.project.ProjectInfoCollectedEvent;
import com.valtech.source.dependometer.app.controller.project.ProjectManager;
import com.valtech.source.dependometer.app.controller.project.TangleCollectedEvent;
import com.valtech.source.dependometer.app.controller.subsystem.HandleSubsystemCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.subsystem.HandleSubsystemCycleParticipantsCollectedEventIf;
import com.valtech.source.dependometer.app.controller.subsystem.HandleSubsystemCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.subsystem.HandleSubsystemLevelCollectedEventIf;
import com.valtech.source.dependometer.app.controller.subsystem.HandleSubsystemTangleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemCycleParticipantsCollectedEvent;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemLevelCollectedEvent;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemManager;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemTangleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.HandleMultiplePackageTypeCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleSingleCompilationUnitTypeCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleSinglePackageTypeCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleTypeCycleParticipantsCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleTypeCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleTypeLevelCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleTypeTangleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.MultiplePackageTypeCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.SingleCompilationUnitTypeCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.SinglePackageTypeCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.TypeCycleParticipantsCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.TypeCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.TypeLevelCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.TypeManager;
import com.valtech.source.dependometer.app.controller.type.TypeTangleCollectedEvent;
import com.valtech.source.dependometer.app.controller.verticalslice.HandleVerticalSliceCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.verticalslice.HandleVerticalSliceCycleParticipantsCollectedEventIf;
import com.valtech.source.dependometer.app.controller.verticalslice.HandleVerticalSliceCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.verticalslice.HandleVerticalSliceLevelCollectedEventIf;
import com.valtech.source.dependometer.app.controller.verticalslice.HandleVerticalSliceTangleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceCycleParticipantsCollectedEvent;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceLevelCollectedEvent;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceManager;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceTangleCollectedEvent;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.util.DependometerUtil;
import com.valtech.source.dependometer.ui.console.Dependometer;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */

public final class HtmlWriter extends SingleDirectoryWriter implements HandleProjectInfoCollectedEventIf,
   HandlePackageCycleCollectedEventIf, HandleLayerCycleCollectedEventIf, HandleVerticalSliceCycleCollectedEventIf,
   HandleSubsystemCycleCollectedEventIf, HandleSinglePackageCompilationUnitCycleCollectedEventIf,
   HandleMultiplePackageCompilationUnitCycleCollectedEventIf, HandleSinglePackageTypeCycleCollectedEventIf,
   HandleMultiplePackageTypeCycleCollectedEventIf, HandleSingleCompilationUnitTypeCycleCollectedEventIf,
   HandleLayerLevelCollectedEventIf, HandleVerticalSliceLevelCollectedEventIf, HandleSubsystemLevelCollectedEventIf,
   HandlePackageLevelCollectedEventIf, HandleCompilationUnitLevelCollectedEventIf, HandleTypeLevelCollectedEventIf,
   HandleLayerCycleParticipationCollectedEventIf, HandleVerticalSliceCycleParticipationCollectedEventIf,
   HandleSubsystemCycleParticipationCollectedEventIf, HandlePackageCycleParticipationCollectedEventIf,
   HandleCompilationUnitCycleParticipationCollectedEventIf, HandleTypeCycleParticipationCollectedEventIf,
   HandleLayerCycleParticipantsCollectedEventIf, HandleVerticalSliceCycleParticipantsCollectedEventIf,
   HandleSubsystemCycleParticipantsCollectedEventIf, HandlePackageCycleParticipantsCollectedEventIf,
   HandleCompilationUnitCycleParticipantsCollectedEventIf, HandleTypeCycleParticipantsCollectedEventIf,
   HandleAnalysisFinishedEventIf, HandleVerticalSliceTangleCollectedEventIf, HandleLayerTangleCollectedEventIf,
   HandleSubsystemTangleCollectedEventIf, HandlePackageTangleCollectedEventIf,
   HandleCompilationUnitTangleCollectedEventIf, HandleTypeTangleCollectedEventIf
{
   private final static Logger s_Logger = Logger.getLogger(HtmlWriter.class);

   private ProjectIf m_Project;

   private HtmlNavigationBarDocument m_NavigationBarDocument;

   private HtmlMetricDescriptionsDocument m_MetricDescriptionsDocument;

   private Map<EntityTypeEnum, HtmlCycleDocument> entity2CycleDoc = new HashMap<EntityTypeEnum, HtmlCycleDocument>();

   private Map<EntityTypeEnum, HtmlCycleDocument> entity2TangleDoc = new HashMap<EntityTypeEnum, HtmlCycleDocument>();

   private Map<EntityTypeEnum, HtmlLevelizationDocument> m_TypeToLevelizationDocument = new HashMap<EntityTypeEnum, HtmlLevelizationDocument>();

   private Map<EntityTypeEnum, HtmlCycleParticipationDocument> m_TypeToCycleParticipationDocument = new HashMap<EntityTypeEnum, HtmlCycleParticipationDocument>();

   private class EntityData
   {
      private int cycles;

      private int tangles;

      private int levels;
   }

   private Map<EntityTypeEnum, EntityData> entity2Data = new HashMap<EntityTypeEnum, EntityData>();

   private File htmlOutDir;

   public HtmlWriter(String[] arguments) throws IOException
   {
      super(arguments);

      if (arguments.length > 1)
      {
         throw new IllegalArgumentException("may only process one argument");
      }

      // TODO refactor: we dont want this static references
      DependometerContext context = Dependometer.getContext();

      ProjectManager projectManager = context.getProjectManager();
      projectManager.attach((HandleProjectInfoCollectedEventIf)this);
      projectManager.attach((HandleAnalysisFinishedEventIf)this);

      VerticalSliceManager verticalSliceManager = context.getVerticalSliceManager();
      verticalSliceManager.attach((HandleVerticalSliceCycleCollectedEventIf)this);
      verticalSliceManager.attach((HandleVerticalSliceLevelCollectedEventIf)this);
      verticalSliceManager.attach((HandleVerticalSliceCycleParticipationCollectedEventIf)this);
      verticalSliceManager.attach((HandleVerticalSliceCycleParticipantsCollectedEventIf)this);
      verticalSliceManager.attach((HandleVerticalSliceTangleCollectedEventIf)this);

      LayerManager layerManager = context.getLayerManager();
      layerManager.attach((HandleLayerCycleCollectedEventIf)this);
      layerManager.attach((HandleLayerLevelCollectedEventIf)this);
      layerManager.attach((HandleLayerCycleParticipationCollectedEventIf)this);
      layerManager.attach((HandleLayerCycleParticipantsCollectedEventIf)this);
      layerManager.attach((HandleLayerTangleCollectedEventIf)this);

      SubsystemManager subsystemManager = context.getSubsystemManager();
      subsystemManager.attach((HandleSubsystemCycleCollectedEventIf)this);
      subsystemManager.attach((HandleSubsystemLevelCollectedEventIf)this);
      subsystemManager.attach((HandleSubsystemCycleParticipationCollectedEventIf)this);
      subsystemManager.attach((HandleSubsystemCycleParticipantsCollectedEventIf)this);
      subsystemManager.attach((HandleSubsystemTangleCollectedEventIf)this);

      PackageManager packageManager = context.getPackageManager();
      packageManager.attach((HandlePackageCycleCollectedEventIf)this);
      packageManager.attach((HandlePackageLevelCollectedEventIf)this);
      packageManager.attach((HandlePackageCycleParticipationCollectedEventIf)this);
      packageManager.attach((HandlePackageCycleParticipantsCollectedEventIf)this);
      packageManager.attach((HandlePackageTangleCollectedEventIf)this);

      CompilationUnitManager compilationUnitManager = context.getCompilationUnitManager();
      compilationUnitManager.attach((HandleSinglePackageCompilationUnitCycleCollectedEventIf)this);
      compilationUnitManager.attach((HandleMultiplePackageCompilationUnitCycleCollectedEventIf)this);
      compilationUnitManager.attach((HandleCompilationUnitLevelCollectedEventIf)this);
      compilationUnitManager.attach((HandleCompilationUnitCycleParticipationCollectedEventIf)this);
      compilationUnitManager.attach((HandleCompilationUnitCycleParticipantsCollectedEventIf)this);
      compilationUnitManager.attach((HandleCompilationUnitTangleCollectedEventIf)this);

      TypeManager typeManager = context.getTypeManager();
      typeManager.attach((HandleSinglePackageTypeCycleCollectedEventIf)this);
      typeManager.attach((HandleMultiplePackageTypeCycleCollectedEventIf)this);
      typeManager.attach((HandleSingleCompilationUnitTypeCycleCollectedEventIf)this);
      typeManager.attach((HandleTypeLevelCollectedEventIf)this);
      typeManager.attach((HandleTypeCycleParticipationCollectedEventIf)this);
      typeManager.attach((HandleTypeCycleParticipantsCollectedEventIf)this);
      typeManager.attach((HandleTypeTangleCollectedEventIf)this);

      copyResultTemplateIfNeeded();

      htmlOutDir = new File(getTargetDirectory(), "dependometer");

      entity2Data.put(EntityTypeEnum.TYPE, new EntityData());
      entity2Data.put(EntityTypeEnum.COMPILATION_UNIT, new EntityData());
      entity2Data.put(EntityTypeEnum.PACKAGE, new EntityData());
      entity2Data.put(EntityTypeEnum.SUBSYSTEM, new EntityData());
      entity2Data.put(EntityTypeEnum.LAYER, new EntityData());
      entity2Data.put(EntityTypeEnum.VERTICAL_SLICE, new EntityData());
   }

   private void copyResultTemplateIfNeeded()
   {
      File targetDir = getTargetDirectory();
      File indexHtml = new File(targetDir, "index.html");
      if (indexHtml.exists())
      {
         // to not overwrite existing template
         return;
      }
      DependometerUtil.copyResultTemplateToDir(targetDir);
   }

   private void writeTypeFiles(DependencyElementIf[] elements)
   {
      assert AssertionUtility.checkArray(elements);
      assert m_MetricDescriptionsDocument != null;
      try
      {
         for (int i = 0; i < elements.length; i++)
         {
            new HtmlTypeDocument(htmlOutDir, elements[i], m_MetricDescriptionsDocument.getFileName()).write();
         }
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writeCompilationUnitFiles(DependencyElementIf[] elements)
   {
      assert AssertionUtility.checkArray(elements);
      assert m_MetricDescriptionsDocument != null;

      try
      {
         for (int i = 0; i < elements.length; i++)
         {
            new HtmlCompilationUnitDocument(htmlOutDir, elements[i], m_MetricDescriptionsDocument.getFileName())
               .write();
         }
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writePackageFiles(DependencyElementIf[] elements)
   {
      assert AssertionUtility.checkArray(elements);
      assert m_MetricDescriptionsDocument != null;
      try
      {
         for (int i = 0; i < elements.length; i++)
         {
            new HtmlPackageDocument(htmlOutDir, elements[i], m_MetricDescriptionsDocument.getFileName()).write();
         }
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writeSubsystemFiles(DependencyElementIf[] elements)
   {
      assert AssertionUtility.checkArray(elements);
      assert m_MetricDescriptionsDocument != null;

      try
      {
         for (int i = 0; i < elements.length; i++)
         {
            new HtmlSubsystemDocument(htmlOutDir, elements[i], m_Project, m_MetricDescriptionsDocument.getFileName())
               .write();
         }
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writeLayerFiles(DependencyElementIf[] elements)
   {
      assert AssertionUtility.checkArray(elements);
      assert m_MetricDescriptionsDocument != null;

      try
      {
         for (int i = 0; i < elements.length; i++)
         {
            new HtmlLayerDocument(htmlOutDir, elements[i], m_MetricDescriptionsDocument.getFileName()).write();
         }
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writeVerticalSliceFiles(DependencyElementIf[] elements)
   {
      assert AssertionUtility.checkArray(elements);
      assert m_MetricDescriptionsDocument != null;

      try
      {
         for (int i = 0; i < elements.length; i++)
         {
            new HtmlVerticalSliceDocument(htmlOutDir, elements[i], m_MetricDescriptionsDocument.getFileName()).write();
         }
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writeIndexFiles()
   {
      try
      {
         new HtmlIndexDocument(htmlOutDir, m_Project.getLayerElementName(), m_Project.getLayers());
         new HtmlIndexDocument(htmlOutDir, m_Project.getSubsystemElementName(), m_Project.getSubsystems());
         new HtmlIndexDocument(htmlOutDir, m_Project.getPackageElementName(), m_Project.getPackages());
         new HtmlIndexDocument(htmlOutDir, m_Project.getCompilationUnitElementName(), m_Project.getCompilationUnits());
         new HtmlIndexDocument(htmlOutDir, m_Project.getTypeElementName(), m_Project.getTypes());
         if (ProviderFactory.getInstance().getConfigurationProvider().analyzeVerticalSlices())
         {
            writeVerticalSliceFiles(m_Project.getVerticalSlices());
            new HtmlIndexDocument(htmlOutDir, m_Project.getVerticalSliceElementName(), m_Project.getVerticalSlices());
            writeVerticalSlicesFile();
         }
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writeProjectFile()
   {
      assert m_Project != null;
      assert m_MetricDescriptionsDocument != null;
      try
      {
         new HtmlProjectDocument(htmlOutDir, m_Project, getTimestamp(), m_MetricDescriptionsDocument.getFileName());
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private HtmlNavigationBarDocument getNavigationBarDocument()
   {
      assert m_NavigationBarDocument != null;
      return m_NavigationBarDocument;
   }

   private HtmlLevelizationDocument getLevelizationDocument(EntityTypeEnum entity)
   {
      assert entity != null;

      HtmlLevelizationDocument doc = null;
      try
      {
         doc = m_TypeToLevelizationDocument.get(entity);
         if (doc == null)
         {
            doc = new HtmlLevelizationDocument(htmlOutDir, entity.getEntityName());
            m_TypeToLevelizationDocument.put(entity, doc);
         }
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }

      return doc;
   }

   private HtmlCycleParticipationDocument getCycleParticipationDocument(EntityTypeEnum entity)
   {
      assert entity != null;

      HtmlCycleParticipationDocument doc = null;
      try
      {
         doc = m_TypeToCycleParticipationDocument.get(entity);
         if (doc == null)
         {
            doc = new HtmlCycleParticipationDocument(htmlOutDir, entity.getEntityName());
            m_TypeToCycleParticipationDocument.put(entity, doc);
         }
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }

      return doc;
   }

   public void handleEvent(ProjectInfoCollectedEvent event)
   {
      assert event != null;

      try
      {
         getLogger().info("generating html ...");
         m_Project = event.getProject();

         File htmlOut = new File(getTargetDirectory(), "dependometer");
         htmlOut.mkdir();

         m_MetricDescriptionsDocument = new HtmlMetricDescriptionsDocument(htmlOut, m_Project.getMetricDefinitions());

         writeIndexFiles();
         writeProjectFile();
         writeTypeFiles(m_Project.getTypes());
         writeCompilationUnitFiles(m_Project.getCompilationUnits());
         writePackageFiles(m_Project.getPackages());
         writeSubsystemFiles(m_Project.getSubsystems());
         writeLayerFiles(m_Project.getLayers());
         writeMetricFiles();
         writeViolationFiles();
         writeUnusedDependenciesFiles();

         m_NavigationBarDocument = new HtmlNavigationBarDocument(htmlOut, m_Project);
         m_NavigationBarDocument.write();
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writeVerticalSlicesFile() throws IOException
   {
      assert m_Project != null;
      new HtmlVerticalSlicesInfoDocument(htmlOutDir, m_Project).close();
   }

   public void handleEvent(LayerCycleCollectedEvent event)
   {
      assert event != null;
      try
      {
         DependencyElementIf[] cycle = event.getCycle();
         getCycleDocument(EntityTypeEnum.LAYER, false).addCycle(cycle);
         reportCycle(EntityTypeEnum.LAYER );
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void reportCycle(EntityTypeEnum entity)
   {
      assert entity != null;

      EntityData eData = entity2Data.get(entity);
      eData.cycles++;
      try
      {
         writeCycleNumbers();
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   public void handleEvent(LayerTangleCollectedEvent event)
   {
      handleTangleCollectedEvent(event);
   }

   private void handleTangleCollectedEvent(TangleCollectedEvent event)
   {
      assert event != null;
      try
      {
         EntityTypeEnum entity = event.getEntityTypeEnum();
         getCycleDocument(entity, true).addTangle(event.getTangle());
         entity2Data.get(entity).tangles += square(event.getTangle().length);
         writeTangleNumbers();
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private int square(int length)
   {
      return length * length;
   }

   /**
    * @throws IOException
    */
   private void writeTangleNumbers() throws IOException
   {
      getNavigationBarDocument().setTangleNumbers(entity2Data.get(EntityTypeEnum.LAYER ).tangles,
         entity2Data.get(EntityTypeEnum.VERTICAL_SLICE ).tangles, entity2Data.get(EntityTypeEnum.SUBSYSTEM ).tangles,
         entity2Data.get(EntityTypeEnum.PACKAGE ).tangles, entity2Data.get(EntityTypeEnum.COMPILATION_UNIT ).tangles,
         entity2Data.get(EntityTypeEnum.TYPE ).tangles);
      getNavigationBarDocument().writeLikely();
   }

   public void handleEvent(SubsystemCycleCollectedEvent event)
   {
      assert event != null;
      try
      {
         DependencyElementIf[] cycle = event.getCycle();
         getCycleDocument(EntityTypeEnum.SUBSYSTEM, false).addCycle(cycle);
         reportCycle(EntityTypeEnum.SUBSYSTEM );
         writeCycleNumbers();
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   /**
    * @throws IOException
    */
   private void writeCycleNumbers() throws IOException
   {
      getNavigationBarDocument().setCycleNumbers(entity2Data.get(EntityTypeEnum.LAYER ).cycles,
         entity2Data.get(EntityTypeEnum.VERTICAL_SLICE ).cycles, entity2Data.get(EntityTypeEnum.SUBSYSTEM ).cycles,
         entity2Data.get(EntityTypeEnum.PACKAGE ).cycles, entity2Data.get(EntityTypeEnum.COMPILATION_UNIT ).cycles,
         entity2Data.get(EntityTypeEnum.TYPE ).cycles);
      getNavigationBarDocument().writeLikely();
   }

   public void handleEvent(PackageCycleCollectedEvent event)
   {
      assert event != null;
      try
      {
         DependencyElementIf[] cycle = event.getCycle();
         getCycleDocument(EntityTypeEnum.PACKAGE, false).addCycle(cycle);
         reportCycle(EntityTypeEnum.PACKAGE );
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   public void handleEvent(SubsystemTangleCollectedEvent event)
   {
      handleTangleCollectedEvent(event);
   }

   public void handleEvent(PackageTangleCollectedEvent event)
   {
      handleTangleCollectedEvent(event);
   }

   public void handleEvent(SinglePackageCompilationUnitCycleCollectedEvent event)
   {
      assert event != null;
      try
      {
         DependencyElementIf[] cycle = event.getCycle();
         getCompilationUnitCycleDocument(false).addSinglePackageCyle(cycle);
         reportCycle(EntityTypeEnum.COMPILATION_UNIT );
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private HtmlTypeCycleDocument getTypeCycleDocument(boolean tangleDoc) throws IOException
   {
      // design issue
      return (HtmlTypeCycleDocument)getCycleDocument(EntityTypeEnum.TYPE, tangleDoc);
   }

   private HtmlCompilationUnitCycleDocument getCompilationUnitCycleDocument(boolean tangleDoc) throws IOException
   {
      // design issue
      return (HtmlCompilationUnitCycleDocument)getCycleDocument(EntityTypeEnum.COMPILATION_UNIT, tangleDoc);
   }

   public void handleEvent(MultiplePackageCompilationUnitCycleCollectedEvent event)
   {
      assert event != null;
      try
      {
         DependencyElementIf[] cycle = event.getCycle();
         getCompilationUnitCycleDocument(false).addMultiplePackageCyle(cycle);
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
      reportCycle(EntityTypeEnum.COMPILATION_UNIT );
   }

   public void handleEvent(SinglePackageTypeCycleCollectedEvent event)
   {
      assert event != null;
      try
      {
         DependencyElementIf[] cycle = event.getCycle();
         getTypeCycleDocument(false).addSinglePackageCyle(cycle);
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
      reportCycle(EntityTypeEnum.TYPE );
   }

   public void handleEvent(MultiplePackageTypeCycleCollectedEvent event)
   {
      assert event != null;
      try
      {
         DependencyElementIf[] cycle = event.getCycle();
         getTypeCycleDocument(false).addMultiplePackageCyle(cycle);
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
      reportCycle(EntityTypeEnum.TYPE );
   }

   public void handleEvent(SingleCompilationUnitTypeCycleCollectedEvent event)
   {
      assert event != null;
      try
      {
         DependencyElementIf[] cycle = event.getCycle();
         getTypeCycleDocument(false).addSingleCompilationUnitCyle(cycle);
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
      reportCycle(EntityTypeEnum.TYPE );
   }

   public void handleEvent(LayerLevelCollectedEvent event)
   {
      handleLevelCollectedEvent(event);
   }

   public void handleEvent(SubsystemLevelCollectedEvent event)
   {
      handleLevelCollectedEvent(event);
   }

   public void handleEvent(PackageLevelCollectedEvent event)
   {
      handleLevelCollectedEvent(event);
   }

   public void handleEvent(CompilationUnitLevelCollectedEvent event)
   {
      handleLevelCollectedEvent(event);
   }

   public void handleEvent(TypeLevelCollectedEvent event)
   {
      handleLevelCollectedEvent(event);
   }

   private void handleLevelCollectedEvent(LevelCollectedEvent event)
   {
      assert event != null;
      EntityTypeEnum entity = event.getEntityType();
      HtmlLevelizationDocument doc = getLevelizationDocument(entity);
      doc.addLevel(event.getLevel(), event.getElements());
      entity2Data.get(entity).levels++;
   }

   public void handleEvent(LayerCycleParticipationCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.LAYER );
      doc.addCycleParticipation(event.getCount(), event.getDependencies());
   }

   public void handleEvent(SubsystemCycleParticipationCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.SUBSYSTEM );
      doc.addCycleParticipation(event.getCount(), event.getDependencies());
   }

   public void handleEvent(PackageCycleParticipationCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.PACKAGE );
      doc.addCycleParticipation(event.getCount(), event.getDependencies());
   }

   public void handleEvent(CompilationUnitCycleParticipationCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.COMPILATION_UNIT );
      doc.addCycleParticipation(event.getCount(), event.getDependencies());
   }

   public void handleEvent(TypeCycleParticipationCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.TYPE );
      doc.addCycleParticipation(event.getCount(), event.getDependencies());
   }

   public void handleEvent(LayerCycleParticipantsCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.LAYER );
      doc.setCycleParticipants(event.getCycleParticipants(), event.getNotCompletelyAnalyzed());
      HtmlLevelizationDocument levelDoc = getLevelizationDocument(EntityTypeEnum.LAYER );
      levelDoc.setNumberOfExcludedElements(event.getCycleParticipants().length
         + event.getNotCompletelyAnalyzed().length);
   }

   public void handleEvent(SubsystemCycleParticipantsCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.SUBSYSTEM );
      doc.setCycleParticipants(event.getCycleParticipants(), event.getNotCompletelyAnalyzed());
      HtmlLevelizationDocument levelDoc = getLevelizationDocument(EntityTypeEnum.SUBSYSTEM );
      levelDoc.setNumberOfExcludedElements(event.getCycleParticipants().length
         + event.getNotCompletelyAnalyzed().length);
   }

   public void handleEvent(PackageCycleParticipantsCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.PACKAGE );
      doc.setCycleParticipants(event.getCycleParticipants(), event.getNotCompletelyAnalyzed());
      HtmlLevelizationDocument levelDoc = getLevelizationDocument(EntityTypeEnum.PACKAGE );
      levelDoc.setNumberOfExcludedElements(event.getCycleParticipants().length
         + event.getNotCompletelyAnalyzed().length);
   }

   public void handleEvent(CompilationUnitCycleParticipantsCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.COMPILATION_UNIT );
      doc.setCycleParticipants(event.getCycleParticipants(), event.getNotCompletelyAnalyzed());
      HtmlLevelizationDocument levelDoc = getLevelizationDocument(EntityTypeEnum.COMPILATION_UNIT );
      levelDoc.setNumberOfExcludedElements(event.getCycleParticipants().length
         + event.getNotCompletelyAnalyzed().length);
   }

   public void handleEvent(TypeCycleParticipantsCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.TYPE );
      doc.setCycleParticipants(event.getCycleParticipants(), event.getNotCompletelyAnalyzed());
      HtmlLevelizationDocument levelDoc = getLevelizationDocument(EntityTypeEnum.TYPE );
      levelDoc.setNumberOfExcludedElements(event.getCycleParticipants().length
         + event.getNotCompletelyAnalyzed().length);
   }

   public void handleEvent(AnalysisFinishedEvent event)
   {

      assert event != null;
      try
      {
         logCycleSummary(EntityTypeEnum.VERTICAL_SLICE );
         logCycleSummary(EntityTypeEnum.LAYER );
         logCycleSummary(EntityTypeEnum.SUBSYSTEM );
         logCycleSummary(EntityTypeEnum.PACKAGE );
         logCycleSummary(EntityTypeEnum.COMPILATION_UNIT );
         logCycleSummary(EntityTypeEnum.TYPE );

         HtmlLevelizationDocument[] docs = m_TypeToLevelizationDocument.values().toArray(
            new HtmlLevelizationDocument[0]);
         for (int i = 0; i < docs.length; i++)
         {
            HtmlLevelizationDocument nextDoc = docs[i];
            nextDoc.close();
         }

         HtmlCycleParticipationDocument[] cycleParticipationDocs = m_TypeToCycleParticipationDocument.values().toArray(
            new HtmlCycleParticipationDocument[0]);
         for (int i = 0; i < cycleParticipationDocs.length; i++)
         {
            HtmlCycleParticipationDocument nextDoc = cycleParticipationDocs[i];
            nextDoc.close();
         }

         getNavigationBarDocument().setLevelizationNumbers(entity2Data.get(EntityTypeEnum.LAYER ).levels,
            entity2Data.get(EntityTypeEnum.VERTICAL_SLICE ).levels, entity2Data.get(EntityTypeEnum.SUBSYSTEM ).levels,
            entity2Data.get(EntityTypeEnum.PACKAGE ).levels, entity2Data.get(EntityTypeEnum.COMPILATION_UNIT ).levels,
            entity2Data.get(EntityTypeEnum.TYPE ).levels);
         getNavigationBarDocument().close();
      }
      catch (IOException e)
      {
         getLogger().error(e);
         throw new RuntimeException(e);
      }
   }

   private void logCycleSummary(EntityTypeEnum entity) throws IOException
   {
      HtmlCycleDocument cycleDoc = entity2CycleDoc.get(entity);
      if (cycleDoc != null)
      {
         EntityData entityData = entity2Data.get(entity);
         s_Logger.info("total number of detected " + entity.getEntityName() + " cycles = " + entityData.cycles);
         cycleDoc.close();
      }
   }

   private void writeViolationFiles()
   {
      try
      {
         new HtmlViolationsDocument(htmlOutDir, m_Project.getLayerElementName(), m_Project
            .getForbiddenEfferentLayerDependencies());
         if (ProviderFactory.getInstance().getConfigurationProvider().analyzeVerticalSlices())
         {
            new HtmlViolationsDocument(htmlOutDir, m_Project.getVerticalSliceElementName(), m_Project
               .getForbiddenEfferentVerticalSliceDependencies());
         }
         new HtmlViolationsDocument(htmlOutDir, m_Project.getSubsystemElementName(), m_Project
            .getForbiddenEfferentSubsystemDependencies());
         new HtmlViolationsDocument(htmlOutDir, m_Project.getPackageElementName(), m_Project
            .getForbiddenEfferentPackageDependencies());
         new HtmlViolationsDocument(htmlOutDir, m_Project.getCompilationUnitElementName(), m_Project
            .getForbiddenEfferentCompilationUnitDependencies());
         new HtmlViolationsDocument(htmlOutDir, m_Project.getTypeElementName(), m_Project
            .getForbiddenEfferentTypeDependencies());
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writeUnusedDependenciesFiles()
   {
      try
      {
         new HtmlUnusedDependenciesDocument(htmlOutDir, m_Project.getLayerElementName(), m_Project
            .getUnusedDefinedEfferentLayerDependencies());
         new HtmlUnusedDependenciesDocument(htmlOutDir, m_Project.getSubsystemElementName(), m_Project
            .getUnusedDefinedEfferentSubsystemDependencies());
         new HtmlUnusedDependenciesDocument(htmlOutDir, m_Project.getPackageElementName(), m_Project
            .getUnusedDefinedEfferentPackageDependencies());
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   private void writeMetricFiles()
   {
      assert m_MetricDescriptionsDocument != null;
      try
      {
         new HtmlMetricsDocument(htmlOutDir, m_Project.getLayerElementName(), m_Project.getLayers(),
            m_MetricDescriptionsDocument.getFileName());
         if (ProviderFactory.getInstance().getConfigurationProvider().analyzeVerticalSlices())
         {
            new HtmlMetricsDocument(htmlOutDir, m_Project.getVerticalSliceElementName(), m_Project.getVerticalSlices(),
               m_MetricDescriptionsDocument.getFileName());
         }
         new HtmlMetricsDocument(htmlOutDir, m_Project.getSubsystemElementName(), m_Project.getSubsystems(),
            m_MetricDescriptionsDocument.getFileName());
         new HtmlMetricsDocument(htmlOutDir, m_Project.getPackageElementName(), m_Project.getPackages(),
            m_MetricDescriptionsDocument.getFileName());
         new HtmlMetricsDocument(htmlOutDir, m_Project.getCompilationUnitElementName(),
            m_Project.getCompilationUnits(), m_MetricDescriptionsDocument.getFileName());
         new HtmlMetricsDocument(htmlOutDir, m_Project.getTypeElementName(), m_Project.getTypes(),
            m_MetricDescriptionsDocument.getFileName());
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   public void handleEvent(VerticalSliceCycleCollectedEvent event)
   {
      assert event != null;
      try
      {
         DependencyElementIf[] cycle = event.getCycle();
         getCycleDocument(EntityTypeEnum.VERTICAL_SLICE, false).addCycle(cycle);
         reportCycle(EntityTypeEnum.VERTICAL_SLICE );
      }
      catch (IOException e)
      {
         getLogger().error(e);
      }
   }

   public void handleEvent(VerticalSliceLevelCollectedEvent event)
   {
      handleLevelCollectedEvent(event);
   }

   public void handleEvent(VerticalSliceCycleParticipationCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.VERTICAL_SLICE );
      doc.addCycleParticipation(event.getCount(), event.getDependencies());
   }

   public void handleEvent(VerticalSliceCycleParticipantsCollectedEvent event)
   {
      assert event != null;
      HtmlCycleParticipationDocument doc = getCycleParticipationDocument(EntityTypeEnum.VERTICAL_SLICE );
      doc.setCycleParticipants(event.getCycleParticipants(), event.getNotCompletelyAnalyzed());
      HtmlLevelizationDocument levelDoc = getLevelizationDocument(EntityTypeEnum.VERTICAL_SLICE );
      levelDoc.setNumberOfExcludedElements(event.getCycleParticipants().length
         + event.getNotCompletelyAnalyzed().length);
   }

   public void handleEvent(VerticalSliceTangleCollectedEvent event)
   {
      handleTangleCollectedEvent(event);
   }

   public void handleEvent(CompilationUnitTangleCollectedEvent event)
   {
      handleTangleCollectedEvent(event);
   }

   public void handleEvent(TypeTangleCollectedEvent event)
   {
      handleTangleCollectedEvent(event);
   }

   private HtmlCycleDocument getCycleDocument(EntityTypeEnum entity, boolean tangleDoc) throws IOException
   {
      HtmlCycleDocument doc = null;
      if (tangleDoc)
      {
         doc = entity2TangleDoc.get(entity);
      }
      else
      {
         doc = entity2CycleDoc.get(entity);
      }
      if (doc == null)
      {
         doc = createCycleDocument(entity, tangleDoc);
      }
      return doc;

   }

   private HtmlCycleDocument createCycleDocument(EntityTypeEnum entity, boolean tangleDoc) throws IOException
   {
      HtmlCycleDocument doc = null;
      switch (entity)
      {
         case TYPE:
            doc = new HtmlTypeCycleDocument(htmlOutDir, entity.getEntityName(), tangleDoc);
            break;
         case COMPILATION_UNIT:
            doc = new HtmlCompilationUnitCycleDocument(htmlOutDir, entity.getEntityName(), tangleDoc);
            break;
         case PACKAGE:
         case SUBSYSTEM:
         case LAYER:
         case VERTICAL_SLICE:
            doc = new HtmlCycleDocument(htmlOutDir, entity.getEntityName(), tangleDoc);
            break;
      }
      if (tangleDoc)
      {
         entity2TangleDoc.put(entity, doc);
      }
      else
      {
         entity2CycleDoc.put(entity, doc);
      }

      return doc;
   }
}