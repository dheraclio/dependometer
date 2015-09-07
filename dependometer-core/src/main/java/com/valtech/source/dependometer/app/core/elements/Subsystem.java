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
import java.util.Set;
import java.util.TreeSet;

import com.valtech.source.ag.util.ListToPrimitive;
import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class Subsystem extends CompilationUnitGroupingElement
{
   /**
    * @deprecated use EntityTypeEnum instead
    */
   public static final String ELEMENT_NAME = "subsystem";


   private final String name;

   private final PackageFilterIf packageFilter;

   private final PackageFilterIf projectPackageFilter;

   private final Set<Package> packages = new TreeSet<Package>();

   private Boolean includedByProjectPackageFilter;

    Subsystem(String fullyQualifiedName, PackageFilterIf packageFilter, PackageFilterIf projectPackageFilter,
              Object source)
    {
        super(fullyQualifiedName, source);
        assert !subsystemExists(getFullyQualifiedName());
        assert packageFilter != null;
        assert projectPackageFilter != null;

        name = getSubsystemName(fullyQualifiedName);
        this.packageFilter = packageFilter;
        this.projectPackageFilter = projectPackageFilter;
        SubsystemShared.THE.addSubsystem( this );
        SubsystemShared.THE.addNotImplementedSubsystem( this );
    }



    static int getNumberOfUnusedEfferentSubsystemDependencies()
   {
      return SubsystemShared.THE.getUnusedDependencies().size();
   }

   static DirectedDependency[] getUnusedEfferentSubsystemDependencies()
   {
      return SubsystemShared.THE.getUnusedDependencies().toArray( new DirectedDependency[ 0 ] );
   }

   static int getNumberOfForbiddenEfferentSubsystemDependencies()
   {
      return SubsystemShared.THE.getForbiddenDependencies().size();
   }

   static DirectedDependency[] getForbiddenEfferentSubsystemDependencies()
   {
      return SubsystemShared.THE.getForbiddenDependencies().toArray( new DirectedDependency[ 0 ] );
   }

   static PackageFilterIf getPackageFilter(DependencyElementIf subsystem)
   {
      assert subsystem != null;
      String name = subsystem.getFullyQualifiedName();

      return SubsystemShared.THE.getSubsystem( name ).getPackageFilter();
   }

   static double[] getRelationalCohesionValues()
   {
      return ListToPrimitive.toDoubleArray( SubsystemShared.THE.getRelationCohesionValues() );
   }

   static int getTotalNumberOfEfferentDependencies()
   {
      return SubsystemShared.THE.getNumberOfEfferentDependencies();
   }

   protected void relationalCohesion(double rc)
   {
       SubsystemShared.THE.addRelationCohesionValue( rc );
   }

   protected void efferentAdded(DependencyElement efferent)
   {
      assert efferent != null;
       SubsystemShared.THE.increaseNumberOfEfferentDependencies();
   }

   protected void forbiddenEfferentAdded(DependencyElementIf forbidden)
   {
      assert forbidden != null;
      assert isForbiddenEfferent(forbidden);
       SubsystemShared.THE.addForbiddenDependency( new DirectedDependency( this, forbidden,
               getNumberOfTypeRelationsForEfferent( forbidden ), true ) );
   }

   public void allowedEfferentAdded(DependencyElement allowed)
   {
      assert allowed != null;
       SubsystemShared.THE.increaseNumberOfAllowedEfferents();
   }

   public static int getNumberOfAllowedEfferents()
   {
      return SubsystemShared.THE.getNumberOfAllowedEfferents();
   }

   private static String getSubsystemName(String fqName)
   {
      assert fqName != null;
      assert fqName.length() > 0;

      String subsystemName = fqName;
      int pos = fqName.indexOf(QUALIFICATION);
      if (pos != -1)
      {
         subsystemName = fqName.substring(pos + QUALIFICATION.length());
      }

      return subsystemName;
   }

   public static String getLayerName(String fqSubsystemName)
   {
      assert fqSubsystemName != null;
      assert fqSubsystemName.length() > 0;

      String layerName = "";
      int pos = fqSubsystemName.indexOf(QUALIFICATION);
      if (pos != -1)
      {
         layerName = fqSubsystemName.substring(0, pos);
      }

      return layerName;
   }

   public static Subsystem belongsToSubsystem(Package javaPackage)
   {
      assert javaPackage != null;

       for ( Subsystem subsystem : SubsystemShared.THE.getSubsystems() ) {
           if (subsystem.containsPackage(javaPackage)) {
               return subsystem;
           }
       }

      return null;
   }

   public static Subsystem[] isAssignedToSubsystems(Package javaPackage)
   {
      assert javaPackage != null;

       List<Subsystem> subsystems = new ArrayList<Subsystem>();

       for ( Subsystem subsystem : SubsystemShared.THE.getSubsystems() ) {
           if (subsystem.containsPackage(javaPackage))
           {
               subsystems.add(subsystem);
           }
       }

      return subsystems.toArray( new Subsystem[ 0 ] );
   }

   public static boolean subsystemExists(String name)
   {
      assert name != null;
      assert name.length() > 0;
      return SubsystemShared.THE.subsystemExists( name );
   }

   public static Subsystem getSubsystem(String name)
   {
      assert name != null;
      assert name.length() > 0;
      assert subsystemExists(name);
       return  SubsystemShared.THE.getSubsystem( name );
   }

   public static int getNumberOfSubsystems()
   {
      return SubsystemShared.THE.getSubsystems().size();
   }

   public static Subsystem[] getSubsystems()
   {
      return SubsystemShared.THE.getSubsystems().toArray( new Subsystem[ 0 ] );
   }

   public static Subsystem[] getProjectInternalSubsystems()
   {
      return SubsystemShared.THE.getProjectInternalSubsystems().toArray(new Subsystem[0]);
   }

   static void checkNumbersOfNotImplementedSubsystems()
   {
      if (SubsystemShared.THE.getNumberOfProjectInternalNotImplementedSubsystems() == -1
         && SubsystemShared.THE.getNumberOfProjectExternalNotImplementedSubsystems() == -1)
      {
          SubsystemShared.THE.resetNumberOfProjectNotImplementedSubsystems();

         Subsystem[] notImplemented = (Subsystem[])getNotImplementedSubsystems();
         for (int i = 0; i < notImplemented.length; i++)
         {
            Subsystem next = notImplemented[i];
            if (next.belongsToProject())
            {
                SubsystemShared.THE.increaseNumberProjectInternalNotImplementedSubsystems();
            }
            else
            {
                SubsystemShared.THE.increaseNumberProjectExternalNotImplementedSubsystems();
            }
         }
      }
   }

   public static DependencyElement[] getNotImplementedSubsystems()
   {
      return SubsystemShared.THE.getNotImplementedSubsystems().toArray( new Subsystem[ 0 ] );
   }

   static int getNumberOfProjectInternalNotImplementedSubsystems()
   {
      checkNumbersOfNotImplementedSubsystems();
      return SubsystemShared.THE.getNumberOfProjectInternalNotImplementedSubsystems();
   }

   static int getNumberOfProjectExternalNotImplementedSubsystems()
   {
      checkNumbersOfNotImplementedSubsystems();
      return SubsystemShared.THE.getNumberOfProjectExternalNotImplementedSubsystems();
   }

   public void collectMetrics()
   {
      super.collectMetrics();
      addMetric( MetricEnum.NUMBER_OF_CONTAINED_PACKAGES, packages.size() );
   }

   public String getName()
   {
      return name;
   }

   public PackageFilterIf getPackageFilter()
   {
      return packageFilter;
   }

   private boolean containsPackage(Package javaPackage)
   {
      assert javaPackage != null;
      return packageFilter.match(javaPackage.getFullyQualifiedName());
   }

   public boolean contains(DependencyElementIf element)
   {
      assert element != null;
      return packages.contains(element);
   }

   public void analyzeDependencies()
   {
      Package[] packages = this.packages.toArray( new Package[ 0 ] );
      for (int j = 0; j < packages.length; j++)
      {
         Package javaPackage = packages[j];

         DependencyElementIf[] efferents = javaPackage.getEfferents();
         for (int i = 0; i < efferents.length; ++i)
         {
            Package efferentJavaPackage = (Package)efferents[i];
            Subsystem efferentSubsystem = (Subsystem)efferentJavaPackage.belongsToDependencyElement();
            if (efferentSubsystem != null && this != efferentSubsystem)
            {
               addEfferent(efferentSubsystem, javaPackage, efferentJavaPackage);
            }
         }

         DependencyElementIf[] afferents = javaPackage.getAfferents();
         for (int i = 0; i < afferents.length; ++i)
         {
            Package afferentJavaPackage = (Package)afferents[i];
            Subsystem afferentSubsystem = (Subsystem)afferentJavaPackage.belongsToDependencyElement();
            if (afferentSubsystem != null && this != afferentSubsystem)
            {
               addAfferent(afferentSubsystem, javaPackage, afferentJavaPackage);
            }
         }
      }
   }

   public void addPackage(Package javaPackage)
   {
      assert javaPackage != null;
      assert !packages.contains(javaPackage);
      javaPackage.setContainer( this, true );

      if ( packages.size() == 0)
      {
          SubsystemShared.THE.removeNotImplementedSubsystem( this );
      }

      packages.add( javaPackage );

      Type[] types = javaPackage.getTypes();
      for (int i = 0; i < types.length; ++i)
      {
         addType(types[i]);
      }

      CompilationUnit[] compilationUnits = javaPackage.getCompilationUnits();
      for (int i = 0; i < compilationUnits.length; ++i)
      {
         addCompilationUnit(compilationUnits[i]);
      }

       SubsystemShared.THE.resetSome();
   }

   public void prepareCollectionOfMetrics()
   {
      super.prepareCollectionOfMetrics();
      DependencyElementIf[] unused = getUnusedAllowedEfferents();
      for (int i = 0; i < unused.length; i++)
      {
         assert getContainmentLevel() == ((DependencyElement)unused[i]).getContainmentLevel();
         SubsystemShared.THE.addUnusedDependency( new DirectedDependency( this, unused[ i ], 0, false ) );
      }
   }

   public int getContainmentLevel()
   {
      return 3;
   }

   public DependencyElementIf[] containsDependencyElements()
   {
      return packages.toArray(new Package[0]);
   }

   public String getElementName()
   {
      return ELEMENT_NAME;
   }

   public boolean belongsToProject()
   {
      if (getNumberOfTypes() > 0)
      {
         return hasProjectInternalTypes();
      }
      else
      {
         if ( includedByProjectPackageFilter == null)
         {
            includedByProjectPackageFilter = Boolean.valueOf( projectPackageFilter.includes( packageFilter ) );
         }

         return includedByProjectPackageFilter.booleanValue();
      }
   }

   public String getContainedElementName()
   {
      return Package.ELEMENT_NAME;
   }

   public static void reset()
   {
       SubsystemShared.THE.reset();
   }
   
   public EntityTypeEnum getEntityType()
   {
      return EntityTypeEnum.SUBSYSTEM;
   }
}