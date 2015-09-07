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
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.valtech.source.dependometer.app.controller.compilationunit.CompilationUnitCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.compilationunit.HandleCompilationUnitCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.layer.HandleLayerCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.layer.LayerCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.main.DependometerContext;
import com.valtech.source.dependometer.app.controller.pack.HandlePackageCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.pack.PackageCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.project.AnalysisFinishedEvent;
import com.valtech.source.dependometer.app.controller.project.HandleAnalysisFinishedEventIf;
import com.valtech.source.dependometer.app.controller.project.HandleProjectInfoCollectedEventIf;
import com.valtech.source.dependometer.app.controller.project.ProjectInfoCollectedEvent;
import com.valtech.source.dependometer.app.controller.subsystem.HandleSubsystemCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.subsystem.SubsystemCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.controller.type.HandleTypeCycleParticipationCollectedEventIf;
import com.valtech.source.dependometer.app.controller.type.TypeCycleParticipationCollectedEvent;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;
import com.valtech.source.dependometer.app.core.provider.ProjectIf;
import com.valtech.source.dependometer.ui.console.Dependometer;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class CycleParticipationReportCsvWriter extends SingleFileWriter implements
   HandleProjectInfoCollectedEventIf, HandleLayerCycleParticipationCollectedEventIf,
   HandleSubsystemCycleParticipationCollectedEventIf, HandlePackageCycleParticipationCollectedEventIf,
   HandleCompilationUnitCycleParticipationCollectedEventIf, HandleTypeCycleParticipationCollectedEventIf,
   HandleAnalysisFinishedEventIf
{
   private static final String SEPARATOR = ",";

   private static final Logger LOGGER = Logger.getLogger(CycleParticipationReportCsvWriter.class.getName());

   private ProjectIf project;

   private String requestedDetailLevel;

   private int detailLevel = 3; // default = package

   private String detailLevelName;

   private Map<Integer, DirectedDependencyIf[]> layer = new TreeMap<Integer, DirectedDependencyIf[]>();

   private Map<Integer, DirectedDependencyIf[]> subsystem = new TreeMap<Integer, DirectedDependencyIf[]>();

   private Map<Integer, DirectedDependencyIf[]> packge = new TreeMap<Integer, DirectedDependencyIf[]>();

   private Map<Integer, DirectedDependencyIf[]> compilationUnit = new TreeMap<Integer, DirectedDependencyIf[]>();

   private Map<Integer, DirectedDependencyIf[]> type = new TreeMap<Integer, DirectedDependencyIf[]>();

   public CycleParticipationReportCsvWriter(String[] arguments) throws IOException
   {
      super(arguments);
      if (arguments.length > 2)
      {
         throw new IllegalArgumentException("may only process one mandatory and one additional optional argument");
      }

      if (arguments.length == 2)
      {
         requestedDetailLevel = arguments[1];
         assert requestedDetailLevel != null;
         assert requestedDetailLevel.length() > 0;
      }

      DependometerContext context = Dependometer.getContext();

      context.getProjectManager().attach((HandleProjectInfoCollectedEventIf)this);
      context.getProjectManager().attach((HandleAnalysisFinishedEventIf)this);
      context.getLayerManager().attach(this);
      context.getSubsystemManager().attach(this);
      context.getPackageManager().attach(this);
      context.getCompilationUnitManager().attach(this);
      context.getTypeManager().attach(this);
   }

   private void setDetailLevel()
   {
      assert project != null;

      if ( requestedDetailLevel != null)
      {
         if ( requestedDetailLevel.equalsIgnoreCase( project.getLayerElementName()))
         {
            detailLevel = 1;
            detailLevelName = project.getLayerElementName();
         }
         else if ( requestedDetailLevel.equalsIgnoreCase( project.getSubsystemElementName()))
         {
            detailLevel = 2;
            detailLevelName = project.getSubsystemElementName();
         }
         else if ( requestedDetailLevel.equalsIgnoreCase( project.getPackageElementName()))
         {
            detailLevel = 3;
            detailLevelName = project.getPackageElementName();
         }
         else if ( requestedDetailLevel.equalsIgnoreCase( project.getCompilationUnitElementName()))
         {
            detailLevel = 4;
            detailLevelName = project.getCompilationUnitElementName();
         }
         else if ( requestedDetailLevel.equalsIgnoreCase( project.getTypeElementName()))
         {
            detailLevel = 5;
            detailLevelName = project.getTypeElementName();
         }
      }

      if ( detailLevelName == null) // default
      {
         if ( requestedDetailLevel != null)
         {
            LOGGER.warn( "detail level setting '" + requestedDetailLevel + "' supported - using default!" );
         }
         assert detailLevel == 3;
         detailLevelName = project.getCompilationUnitElementName();
      }
   }

   private String getDetailLevelName()
   {
      assert detailLevelName != null;
      assert detailLevelName.length() > 0;
      return detailLevelName;
   }

   public void handleEvent(ProjectInfoCollectedEvent event)
   {
      assert event != null;
      project = event.getProject();
   }

   private void writeContent()
   {
      assert project != null;
      PrintWriter writer = getWriter();
      setDetailLevel();

      writer.println("### " + getClass().getName() + " - " + getTimestamp() + " ###");
      writer.println();
      writer.println("# settings #");
      writer.println();
      writer.println("detail level = " + getDetailLevelName());
      writer.println();

      switch ( detailLevel )
      {
         case 5:
            writeCycleParticipations( writer, type, project.getTypeElementName() );
         case 4:
            writeCycleParticipations(writer, compilationUnit, project.getCompilationUnitElementName());
         case 3:
            writeCycleParticipations(writer, packge, project.getPackageElementName());
         case 2:
            writeCycleParticipations( writer, subsystem, project.getSubsystemElementName() );
         case 1:
            writeCycleParticipations(writer, layer, project.getLayerElementName());
         default:
            break;
      }

      getLogger().info("writing cycle participation report csv file ...");
      close();
   }

   private void writeHeader(PrintWriter writer, String type)
   {
      writer.println();
      writer.println("### " + type + " ###");
      writer.println();
      writer.print("type relations");
      writer.print(SEPARATOR);
      writer.print("from");
      writer.print(SEPARATOR);
      writer.print("");
      writer.print(SEPARATOR);
      writer.print("to");
      writer.print(SEPARATOR);
      writer.println("occurences");
   }

   private void writeCycleParticipations(PrintWriter writer, Map<Integer, DirectedDependencyIf[]> map, String type)
   {
      assert writer != null;
      assert map != null;
      assert type != null;
      assert type.length() > 0;

      Integer[] participations = map.keySet().toArray(new Integer[0]);
      if (participations.length > 0)
      {
         writeHeader(writer, type);
         for (int i = 0; i < participations.length; i++)
         {
            Integer next = participations[i];
            DirectedDependencyIf[] deps = map.get(next);
            for (int j = 0; j < deps.length; j++)
            {
               DirectedDependencyIf nextDep = deps[j];
               writer.print(nextDep.getNumberOfTypeRelations());
               writer.print(SEPARATOR);
               writer.print(nextDep.getFrom().getFullyQualifiedName());
               writer.print(SEPARATOR);
               writer.print("=>");
               writer.print(SEPARATOR);
               writer.print(nextDep.getTo().getFullyQualifiedName());
               writer.print(SEPARATOR);
               writer.println(next);
            }
         }
      }
      writer.println();
   }

   public void handleEvent(AnalysisFinishedEvent event)
   {
      assert event != null;
      writeContent();
   }

   public void handleEvent(LayerCycleParticipationCollectedEvent event)
   {
      assert event != null;
      Integer count = Integer.valueOf(event.getCount());
      assert !layer.containsKey(count);
      layer.put( count, event.getDependencies() );
   }

   public void handleEvent(SubsystemCycleParticipationCollectedEvent event)
   {
      assert event != null;
      Integer count = Integer.valueOf(event.getCount());
      assert !subsystem.containsKey(count);
      subsystem.put( count, event.getDependencies() );
   }

   public void handleEvent(PackageCycleParticipationCollectedEvent event)
   {
      assert event != null;
      Integer count = Integer.valueOf(event.getCount());
      assert !packge.containsKey(count);
      packge.put( count, event.getDependencies() );
   }

   public void handleEvent(CompilationUnitCycleParticipationCollectedEvent event)
   {
      assert event != null;
      Integer count = Integer.valueOf(event.getCount());
      assert !compilationUnit.containsKey(count);
      compilationUnit.put( count, event.getDependencies() );
   }

   public void handleEvent(TypeCycleParticipationCollectedEvent event)
   {
      assert event != null;
      Integer count = Integer.valueOf(event.getCount());
      assert !type.containsKey(count);
      type.put( count, event.getDependencies() );
   }
}