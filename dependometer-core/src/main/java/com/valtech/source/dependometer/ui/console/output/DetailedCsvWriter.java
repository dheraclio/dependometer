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

import com.valtech.source.dependometer.app.controller.compilationunit.CompilationUnitManager;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleMultiplePackageCompilationUnitCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleSinglePackageCompilationUnitCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.compilationunit.MultiplePackageCompilationUnitCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.compilationunit.SinglePackageCompilationUnitCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.layer.HandleLayerCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.layer.LayerCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.main.DependometerContext;
import com.valtech.source.dependometer.app.controller.pack.HandlePackageCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.pack.PackageCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.project.AnalysisFinishedEvent;
import com.valtech.source.dependometer.app.controller.project.HandleAnalysisFinishedEventIf;
import com.valtech.source.dependometer.app.controller.project.ProjectInfoCollectedEvent;
import com.valtech.source.dependometer.app.controller.subsystem.HandleSubsystemCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.HandleMultiplePackageTypeCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleSingleCompilationUnitTypeCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.HandleSinglePackageTypeCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.MultiplePackageTypeCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.SingleCompilationUnitTypeCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.SinglePackageTypeCycleCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.TypeManager;
import com.valtech.source.dependometer.app.controller.verticalslice.HandleVerticalSliceCycleCollectedEventIf;
import com.valtech.source.dependometer.app.controller.verticalslice.VerticalSliceCycleCollectedEvent;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.ui.console.Dependometer;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class DetailedCsvWriter extends CsvWriter implements HandleVerticalSliceCycleCollectedEventIf,
   HandleLayerCycleCollectedEventIf, HandleSubsystemCycleCollectedEventIf, HandlePackageCycleCollectedEventIf,
   HandleMultiplePackageCompilationUnitCycleCollectedEventIf, HandleSinglePackageCompilationUnitCycleCollectedEventIf,
   HandleMultiplePackageTypeCycleCollectedEventIf, HandleSinglePackageTypeCycleCollectedEventIf,
   HandleSingleCompilationUnitTypeCycleCollectedEventIf, HandleAnalysisFinishedEventIf
{
   private int m_NumberOfPackageCycles;

   private int m_NumberOfSubsystemCycles;

   private int m_NumberOfLayerCycles;

   private int m_NumberOfVerticalSliceCycles;

   private int m_NumberOfMultiplePackageCompilationUnitCycles;

   private int m_NumberOfSinglePackageCompilationUnitCycles;

   private int m_NumberOfMultiplePackageTypeCycles;

   private int m_NumberOfSinglePackageTypeCycles;

   private int m_NumberOfSingleCompilationUnitTypeCycles;

   private ProjectIf m_Project;

   public DetailedCsvWriter(String[] arguments) throws IOException
   {
      super(arguments);

      DependometerContext context = Dependometer.getContext();

      context.getVerticalSliceManager().attach(this);
      context.getLayerManager().attach(this);
      context.getSubsystemManager().attach(this);
      context.getPackageManager().attach(this);

      CompilationUnitManager compilationUnitManager = context.getCompilationUnitManager();
      compilationUnitManager.attach((HandleMultiplePackageCompilationUnitCycleCollectedEventIf)this);
      compilationUnitManager.attach((HandleSinglePackageCompilationUnitCycleCollectedEventIf)this);

      TypeManager typeManager = context.getTypeManager();
      typeManager.attach((HandleMultiplePackageTypeCycleCollectedEventIf)this);
      typeManager.attach((HandleSinglePackageTypeCycleCollectedEventIf)this);
      typeManager.attach((HandleSingleCompilationUnitTypeCycleCollectedEventIf)this);

      context.getProjectManager().attach((HandleAnalysisFinishedEventIf)this);
   }

   public void handleEvent(ProjectInfoCollectedEvent event)
   {
      assert event != null;
      super.handleEvent(event);
      m_Project = event.getProject();
   }

   public void handleEvent(VerticalSliceCycleCollectedEvent event)
   {
      assert event != null;
      ++m_NumberOfVerticalSliceCycles;
   }

   public void handleEvent(LayerCycleCollectedEvent event)
   {
      assert event != null;
      ++m_NumberOfLayerCycles;
   }

   public void handleEvent(SubsystemCycleCollectedEvent event)
   {
      assert event != null;
      ++m_NumberOfSubsystemCycles;
   }

   public void handleEvent(PackageCycleCollectedEvent event)
   {
      assert event != null;
      ++m_NumberOfPackageCycles;
   }

   public void handleEvent(MultiplePackageCompilationUnitCycleCollectedEvent event)
   {
      assert event != null;
      ++m_NumberOfMultiplePackageCompilationUnitCycles;
   }

   public void handleEvent(SinglePackageCompilationUnitCycleCollectedEvent event)
   {
      assert event != null;
      ++m_NumberOfSinglePackageCompilationUnitCycles;
   }

   public void handleEvent(MultiplePackageTypeCycleCollectedEvent event)
   {
      assert event != null;
      ++m_NumberOfMultiplePackageTypeCycles;
   }

   public void handleEvent(SinglePackageTypeCycleCollectedEvent event)
   {
      assert event != null;
      ++m_NumberOfSinglePackageTypeCycles;
   }

   public void handleEvent(SingleCompilationUnitTypeCycleCollectedEvent event)
   {
      assert event != null;
      ++m_NumberOfSingleCompilationUnitTypeCycles;
   }

   public void handleEvent(AnalysisFinishedEvent event)
   {
      assert event != null;
      assert m_Project != null;

      PrintWriter writer = getWriter();
      ConfigurationProviderIf provider = ProviderFactory.getInstance().getConfigurationProvider();

      writer.println();
      writer.println();
      writer.println("# Number of analyzed cycles #");
      writer.println();

      if (provider.analyzeVerticalSlices())
      {
         writer.print("Number of " + m_Project.getVerticalSliceElementName() + " cycles,");
         writer.println(m_NumberOfVerticalSliceCycles);
      }

      writer.print("Number of " + m_Project.getLayerElementName() + " cycles,");
      writer.println(m_NumberOfLayerCycles);
      writer.print("Number of " + m_Project.getSubsystemElementName() + " cycles,");
      writer.println(m_NumberOfSubsystemCycles);
      writer.print("Number of " + m_Project.getPackageElementName() + " cycles,");
      writer.println(m_NumberOfPackageCycles);
      writer.print("Number of multiple " + m_Project.getPackageElementName() + " "
         + m_Project.getCompilationUnitElementName() + " cycles,");
      writer.println(m_NumberOfMultiplePackageCompilationUnitCycles);
      writer.print("Number of single " + m_Project.getPackageElementName() + " "
         + m_Project.getCompilationUnitElementName() + " cycles,");
      writer.println(m_NumberOfSinglePackageCompilationUnitCycles);
      writer.print("Number of multiple " + m_Project.getPackageElementName() + " " + m_Project.getTypeElementName()
         + " cycles,");
      writer.println(m_NumberOfMultiplePackageTypeCycles);
      writer.print("Number of single " + m_Project.getPackageElementName() + " " + m_Project.getTypeElementName()
         + " cycles,");
      writer.println(m_NumberOfSinglePackageTypeCycles);
      writer.print("Number of single " + m_Project.getCompilationUnitElementName() + " "
         + m_Project.getTypeElementName() + " cycles,");
      writer.println(m_NumberOfSingleCompilationUnitTypeCycles);

      getLogger().info("writing detailed csv file ...");
      close();
   }

   void finish()
   {
      // super class should not close the file
   }
}