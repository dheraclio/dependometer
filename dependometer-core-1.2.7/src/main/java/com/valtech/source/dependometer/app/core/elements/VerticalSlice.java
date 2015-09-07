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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.ag.util.ListToPrimitive;
import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.DirectedDependencyIf;
import com.valtech.source.dependometer.app.core.provider.SubsystemFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class VerticalSlice extends CompilationUnitGroupingElement
{
   /**
    * @deprecated use EntityTypeEnum instead
    */
   public final static String ELEMENT_NAME = "vertical-slice";

   private final static Map<String, VerticalSlice> s_NameToVerticalSlice = new TreeMap<String, VerticalSlice>();

   private final static Set<DependencyElementIf> s_RelevantLayers = new TreeSet<DependencyElementIf>();

   private final static Set<DirectedDependency> s_ForbiddenDependencies = new TreeSet<DirectedDependency>();

   private static final List<Double> s_RelationCohesionValues = new ArrayList<Double>();

   private static int s_NumberOfEfferentDependencies;

   private static SubsystemFilterIf s_SubsystemFilter;

   private final Set<Subsystem> subsystems = new TreeSet<Subsystem>();

   private final Set<String> efferentVerticalSliceNames = new TreeSet<String>();

   static int getTotalNumberOfEfferentDependencies()
   {
      return s_NumberOfEfferentDependencies;
   }

   static double[] getRelationalCohesionValues()
   {
      return ListToPrimitive.toDoubleArray( s_RelationCohesionValues );
   }

   protected void efferentAdded(DependencyElement efferent)
   {
      assert efferent != null;
      ++s_NumberOfEfferentDependencies;
   }

   protected void relationalCohesion(double rc)
   {
      s_RelationCohesionValues.add(rc);
   }

   public static void createVerticalSlices(Subsystem[] subsystems, SubsystemFilterIf subsystemFilter)
   {
      assert s_SubsystemFilter == null;
      assert AssertionUtility.checkArray(subsystems);
      assert subsystemFilter != null;

      s_SubsystemFilter = subsystemFilter;

      for (int i = 0; i < subsystems.length; i++)
      {
         Subsystem nextSubsystem = subsystems[i];
         assert nextSubsystem.belongsToProject();
         String fqName = nextSubsystem.getFullyQualifiedName();

         if (s_SubsystemFilter.match(fqName) && nextSubsystem.belongsToProject())
         {
            String name = nextSubsystem.getName();
            VerticalSlice verticalSlice = s_NameToVerticalSlice.get(name);
            if (verticalSlice == null)
            {
               verticalSlice = new VerticalSlice(name, nextSubsystem.getSource());
               s_NameToVerticalSlice.put(name, verticalSlice);
            }

            verticalSlice.addSubsystem(nextSubsystem);
         }
      }
   }

   public static VerticalSlice[] getVerticalSlices()
   {
      return s_NameToVerticalSlice.values().toArray(new VerticalSlice[0]);
   }

   private VerticalSlice(String fullyQualifiedName, Object source)
   {
      super(fullyQualifiedName, source);
   }

   public void collectMetrics()
   {
      super.collectMetrics();
      addMetric( MetricEnum.NUMBER_OF_CONTAINED_SUBSYSTEMS, subsystems.size() );
   }

   protected void forbiddenEfferentAdded(DependencyElementIf forbidden)
   {
      assert forbidden != null;
      assert isForbiddenEfferent(forbidden);
      int typeRels = getNumberOfTypeRelationsForEfferent(forbidden);
      s_ForbiddenDependencies.add(new DirectedDependency(this, forbidden, typeRels, true));
   }

   static int getNumberOfForbiddenEfferentVerticalSliceDependencies()
   {
      return s_ForbiddenDependencies.size();
   }

   public boolean contains(DependencyElementIf element)
   {
      assert element != null;
      return subsystems.contains(element);
   }

   public void analyzeDependencies()
   {
      Subsystem[] subsystems = this.subsystems.toArray( new Subsystem[ 0 ] );
      for (int i = 0; i < subsystems.length; i++)
      {
         Subsystem nextSubsystem = subsystems[i];

         Type[] types = nextSubsystem.getTypes();
         for (int j = 0; j < types.length; ++j)
         {
            addType(types[j]);
         }

         CompilationUnit[] compilationUnits = nextSubsystem.getCompilationUnits();
         for (int j = 0; j < compilationUnits.length; ++j)
         {
            addCompilationUnit(compilationUnits[j]);
         }

         DependencyElementIf[] allowed = nextSubsystem.getAllowedEfferents();
         for (int j = 0; j < allowed.length; j++)
         {
            String nextName = allowed[j].getName();
            String nextFqName = allowed[j].getFullyQualifiedName();
            if (s_SubsystemFilter.match(nextFqName) && allowed[j].belongsToProject() && !getName().equals(nextName))
            {
               VerticalSlice efferentSlice = s_NameToVerticalSlice.get(nextName);
               assert efferentSlice != null;
               if (!isAllowedEfferent(efferentSlice))
               {
                  addAllowedEfferent(efferentSlice);
               }
            }
         }

         DependencyElementIf[] efferentSubsystems = nextSubsystem.getEfferents();
         for (int j = 0; j < efferentSubsystems.length; ++j)
         {
            Subsystem nextEfferentSubsystem = (Subsystem)efferentSubsystems[j];
            String nextName = nextEfferentSubsystem.getName();
            String nextFqName = nextEfferentSubsystem.getFullyQualifiedName();

            if (s_SubsystemFilter.match(nextFqName) && nextEfferentSubsystem.belongsToProject()
               && !getName().equals(nextName))
            {
               VerticalSlice efferentSlice = s_NameToVerticalSlice.get(nextName);
               assert efferentSlice != null;
               addEfferent(efferentSlice, nextSubsystem, nextEfferentSubsystem);
            }
         }

         DependencyElementIf[] afferentSubsystems = nextSubsystem.getAfferents();
         for (int j = 0; j < afferentSubsystems.length; ++j)
         {
            Subsystem nextAfferentSubsystem = (Subsystem)afferentSubsystems[j];
            String nextName = nextAfferentSubsystem.getName();
            String nextFqName = nextAfferentSubsystem.getFullyQualifiedName();

            if (s_SubsystemFilter.match(nextFqName) && nextAfferentSubsystem.belongsToProject()
               && !getName().equals(nextName))
            {
               VerticalSlice afferentSlice = s_NameToVerticalSlice.get(nextName);
               assert afferentSlice != null;
               addAfferent(afferentSlice, nextSubsystem, nextAfferentSubsystem);
            }
         }
      }
   }

   private void addSubsystem(Subsystem subsystem)
   {
      assert subsystem != null;
      assert !subsystems.contains(subsystem);
      subsystems.add( subsystem );
      s_RelevantLayers.add(subsystem.belongsToDependencyElement());
      DependencyElementIf[] efferentSubsystems = subsystem.getEfferents();
      for (int i = 0; i < efferentSubsystems.length; i++)
      {
         String next = efferentSubsystems[i].getName();
         if (!next.equals(getName()))
         {
            efferentVerticalSliceNames.add( next );
         }
      }

      subsystem.setContainer(this, false);
   }

   public String getName()
   {
      return getFullyQualifiedName();
   }

   public int getContainmentLevel()
   {
      return 4;
   }

   public boolean belongsToProject()
   {
      return true;
   }

   public DependencyElementIf[] containsDependencyElements()
   {
      return subsystems.toArray(new DependencyElementIf[0]);
   }

   public boolean isEfferentsCheckEnabled(DependencyElementIf efferent)
   {
      assert efferent != null;
      assert false;
      return false;
   }

   public static int getNumberOfVerticalSlices()
   {
      return s_NameToVerticalSlice.size();
   }

   public static DependencyElementIf[] getRelevantLayers()
   {
      return s_RelevantLayers.toArray(new DependencyElementIf[0]);
   }

   public static DirectedDependencyIf[] getForbiddenEfferentVerticalSliceDependencies()
   {
      return s_ForbiddenDependencies.toArray(new DirectedDependency[0]);
   }

   public String getElementName()
   {
      return ELEMENT_NAME;
   }

   public String getContainedElementName()
   {
      return Subsystem.ELEMENT_NAME;
   }

   public static void reset()
   {
      s_NameToVerticalSlice.clear();
      s_RelevantLayers.clear();
      s_ForbiddenDependencies.clear();
      s_RelationCohesionValues.clear();

      s_NumberOfEfferentDependencies = 0;
      s_SubsystemFilter = null;
   }
   
   public EntityTypeEnum getEntityType()
   {
      return EntityTypeEnum.VERTICAL_SLICE;
   }
}