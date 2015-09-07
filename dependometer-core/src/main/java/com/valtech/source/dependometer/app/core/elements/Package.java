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

import com.valtech.source.ag.util.ListToPrimitive;
import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.MetricsProviderIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class Package extends CompilationUnitGroupingElement
{
   /**
    * @deprecated use EntityTypeEnum instead
    */
   public static final String ELEMENT_NAME = "package";

   private static final Map<String, Package> s_FullyQualifiedNameToPackage = new TreeMap<String, Package>();

   private static final Set<DirectedDependency> s_ForbiddenDependencies = new TreeSet<DirectedDependency>();

   private static final Set<DirectedDependency> s_UnusedDependencies = new TreeSet<DirectedDependency>();

   private static final List<Double> s_RelationCohesionValues = new ArrayList<Double>();

   private static List<Package> s_NotAssignedPackages;

   private static int s_NumberOfProjectInternalNotAssignedPackages = -1;

   private static int s_NumberOfProjectExternalNotAssignedPackages = -1;

   private static int s_NumberOfEfferentDependencies;

   private static int s_MaxDepthOfPackageHierarchy;

   private static int s_NumberOfAllowedEfferents;

   private final int m_DepthOfPackageHierarchy;

   private boolean m_CheckEfferents;

   public static int getNumberOfPackages()
   {
      return s_FullyQualifiedNameToPackage.size();
   }

   static int getNumberOfUnusedEfferentPackageDependencies()
   {
      return s_UnusedDependencies.size();
   }

   static DirectedDependency[] getUnusedEfferentPackageDependencies()
   {
      return s_UnusedDependencies.toArray(new DirectedDependency[0]);
   }

   static int getNumberOfForbiddenEfferentPackageDependencies()
   {
      return s_ForbiddenDependencies.size();
   }

   static DirectedDependency[] getForbiddenEfferentPackageDependencies()
   {
      return s_ForbiddenDependencies.toArray(new DirectedDependency[0]);
   }

   protected void forbiddenEfferentAdded(DependencyElementIf forbidden)
   {
      assert forbidden != null;
      assert isForbiddenEfferent(forbidden);
      s_ForbiddenDependencies.add(new DirectedDependency(this, forbidden,
         getNumberOfTypeRelationsForEfferent(forbidden), true));
   }

   static int getMaxDepthOfPackageHierarchy()
   {
      return s_MaxDepthOfPackageHierarchy;
   }

   static int getTotalNumberOfEfferentDependencies()
   {
      return s_NumberOfEfferentDependencies;
   }

   final protected void efferentAdded(DependencyElement efferent)
   {
      assert efferent != null;
      ++s_NumberOfEfferentDependencies;
   }

   static Package[] getNotAssignedPackages()
   {
      if (s_NotAssignedPackages == null)
      {
         s_NotAssignedPackages = new ArrayList<Package>();
         Package[] all = Package.getPackages();
         for (int i = 0; i < all.length; ++i)
         {
            Package next = all[i];
            if (next.belongsToDependencyElement() == null)
            {
               s_NotAssignedPackages.add(next);
            }
         }
      }

      return s_NotAssignedPackages.toArray(new Package[0]);
   }

   static void checkNumbersOfNotAssignedPackages()
   {
      if (s_NumberOfProjectInternalNotAssignedPackages == -1 && s_NumberOfProjectExternalNotAssignedPackages == -1)
      {
         s_NumberOfProjectInternalNotAssignedPackages = 0;
         s_NumberOfProjectExternalNotAssignedPackages = 0;

         Package[] notAssigned = getNotAssignedPackages();
         for (int i = 0; i < notAssigned.length; i++)
         {
            Package next = notAssigned[i];
            if (next.belongsToProject())
            {
               s_NumberOfProjectInternalNotAssignedPackages++;
            }
            else
            {
               s_NumberOfProjectExternalNotAssignedPackages++;
            }
         }
      }
   }

   static int getNumberOfProjectInternalNotAssignedPackages()
   {
      checkNumbersOfNotAssignedPackages();
      return s_NumberOfProjectInternalNotAssignedPackages;
   }

   static int getNumberOfProjectExternalNotAssignedPackages()
   {
      checkNumbersOfNotAssignedPackages();
      return s_NumberOfProjectExternalNotAssignedPackages;
   }

   public void allowedEfferentAdded(DependencyElement allowed)
   {
      assert allowed != null;
      ++s_NumberOfAllowedEfferents;
   }

   static int getNumberOfAllowedEfferents()
   {
      return s_NumberOfAllowedEfferents;
   }

   static double[] getRelationalCohesionValues()
   {
      return ListToPrimitive.toDoubleArray( s_RelationCohesionValues );
   }

   public static Package[] getPackages()
   {
      return s_FullyQualifiedNameToPackage.values().toArray(new Package[0]);
   }

   public static Package getPackage(String fqname)
   {
      assert fqname != null;
      return s_FullyQualifiedNameToPackage.get(fqname);
   }

   Package(String fullyQualifiedName)
   {
      super(fullyQualifiedName, MetricsProviderIf.NO_SOURCE);
      assert !s_FullyQualifiedNameToPackage.containsKey(fullyQualifiedName);
      s_FullyQualifiedNameToPackage.put(fullyQualifiedName, this);
      m_DepthOfPackageHierarchy = countChar('.', getFullyQualifiedName()) + 1;
      wasRefactored(true);
   }

   private static int countChar(char c, String s)
   {
      assert s != null;

      int fromIndex = -1;
      int count = 0;

      while ((fromIndex = s.indexOf(c, fromIndex + 1)) != -1)
      {
         ++count;
      }

      return count;
   }

   public final void setCheckEfferents(boolean check)
   {
      m_CheckEfferents = check;
   }

   protected boolean performEfferentsCheck()
   {
      return m_CheckEfferents;
   }

   public boolean contains(DependencyElementIf element)
   {
      assert element != null;
      return containsCompilationUnit((CompilationUnit)element);
   }

   protected void belongsToSet(DependencyElementIf container)
   {
      assert container != null;
      s_NotAssignedPackages = null;
   }

   public void prepareCollectionOfMetrics()
   {
      super.prepareCollectionOfMetrics();

      DependencyElementIf[] unused = getUnusedAllowedEfferents();
      for (int i = 0; i < unused.length; i++)
      {
         assert getContainmentLevel() == ((DependencyElement)unused[i]).getContainmentLevel();
         s_UnusedDependencies.add(new DirectedDependency(this, unused[i], 0, false));
      }

      if (belongsToProject() && s_MaxDepthOfPackageHierarchy < m_DepthOfPackageHierarchy)
      {
         s_MaxDepthOfPackageHierarchy = m_DepthOfPackageHierarchy;
      }
   }

   public final void collectMetrics()
   {
      super.collectMetrics();

      if (belongsToProject())
      {
         addMetric(MetricEnum.DEPTH_OF_PACKAGE_HIERARCHY, m_DepthOfPackageHierarchy);
      }
   }

   protected void relationalCohesion(double rc)
   {
      s_RelationCohesionValues.add(rc);
   }

   public final String getName()
   {
      return getFullyQualifiedName();
   }

   public void analyzeDependencies()
   {
      CompilationUnit[] compilationUnits = getCompilationUnits();
      for (int j = 0; j < compilationUnits.length; j++)
      {
         CompilationUnit compilationUnit = compilationUnits[j];
         DependencyElementIf[] afferentCompilationUnits = compilationUnit.getAfferents();

         for (int i = 0; i < afferentCompilationUnits.length; ++i)
         {
            CompilationUnit nextAfferentCompilationUnit = (CompilationUnit)afferentCompilationUnits[i];
            String packageName = nextAfferentCompilationUnit.getPackageName();

            if (!getFullyQualifiedName().equals(packageName))
            {
               Package afferentPackage = getPackage(packageName);
               assert afferentPackage != null;
               addAfferent(afferentPackage, compilationUnit, nextAfferentCompilationUnit);
            }
         }

         DependencyElementIf[] efferentCompilationUnits = compilationUnit.getEfferents();
         for (int i = 0; i < efferentCompilationUnits.length; ++i)
         {
            CompilationUnit nextEfferentCompilationUnit = (CompilationUnit)efferentCompilationUnits[i];
            String packageName = nextEfferentCompilationUnit.getPackageName();

            if (!getFullyQualifiedName().equals(packageName))
            {
               Package efferentPackage = getPackage(packageName);
               assert efferentPackage != null;
               addEfferent(efferentPackage, compilationUnit, nextEfferentCompilationUnit);
            }
         }
      }
   }

   public final void addCompilationUnit(CompilationUnit compilationUnit)
   {
      super.addCompilationUnit(compilationUnit);
      compilationUnit.setContainer(this, true);

      if (!compilationUnit.wasRefactored())
      {
         wasRefactored(false);
      }

      Type[] containedTypes = compilationUnit.getTypes();
      for (int i = 0; i < containedTypes.length; ++i)
      {
         addType(containedTypes[i]);
      }
   }

   public final int getContainmentLevel()
   {
      return 2;
   }

   final public String getElementName()
   {
      return ELEMENT_NAME;
   }

   public final DependencyElementIf[] containsDependencyElements()
   {
      return getCompilationUnits();
   }

   public final boolean belongsToProject()
   {
      return hasProjectInternalTypes();
   }

   public String getContainedElementName()
   {
      return CompilationUnit.ELEMENT_NAME;
   }

   public static void reset()
   {
      s_FullyQualifiedNameToPackage.clear();
      s_ForbiddenDependencies.clear();
      s_UnusedDependencies.clear();
      s_RelationCohesionValues.clear();
      s_NotAssignedPackages = null;

      s_NumberOfProjectInternalNotAssignedPackages = -1;
      s_NumberOfProjectExternalNotAssignedPackages = -1;
      s_NumberOfEfferentDependencies = 0;
      s_MaxDepthOfPackageHierarchy = 0;
      s_NumberOfAllowedEfferents = 0;
   }
   
   public EntityTypeEnum getEntityType()
   {
      return EntityTypeEnum.PACKAGE;
   }
}