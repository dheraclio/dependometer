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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.MetricsProviderIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.RefactoringIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract public class CompilationUnit extends TypeGroupingElement
{
   private static final Logger LOGGER = Logger.getLogger(CompilationUnit.class.getName());

   /**
    * @deprecated use EntityTypeEnum instead
    */
   public static final String ELEMENT_NAME = "compilation-unit";

   private static final Map<String, CompilationUnit> s_FullyQualifiedNameToCompilationUnit = new TreeMap<String, CompilationUnit>();

   private static final Set<DirectedDependency> s_ForbiddenDependencies = new TreeSet<DirectedDependency>();

   private static int s_NumberOfEfferentDependencies;

   private final Map<String, ContainerRelationCounter> relatedPackageNameToRelation = new HashMap<String, ContainerRelationCounter>();

   private int numberOfPackageInternalRelations;

   private int numberOfPackageExternalRelations;

   private int numberOfAbstractTypes;

   private int numberOfAccessibleTypes;

   private final String name;

   private final String packageName;

   private File absoluteSourcePath;

   public static int getNumberOfCompilationUnits()
   {
      return s_FullyQualifiedNameToCompilationUnit.size();
   }

   static int getTotalNumberOfEfferentDependencies()
   {
      return s_NumberOfEfferentDependencies;
   }

   static int getNumberOfForbiddenEfferentCompilationUnitDependencies()
   {
      return s_ForbiddenDependencies.size();
   }

   static DirectedDependency[] getForbiddenEfferentCompilationUnitDependencies()
   {
      return s_ForbiddenDependencies.toArray(new DirectedDependency[0]);
   }

   protected final void forbiddenEfferentAdded(DependencyElementIf forbidden)
   {
      assert isForbiddenEfferent(forbidden);
      s_ForbiddenDependencies.add(new DirectedDependency(this, forbidden,
         getNumberOfTypeRelationsForEfferent(forbidden), true));
   }

   private static String getCompilationUnitName(String fullyQualifiedName)
   {
      assert fullyQualifiedName != null;
      assert fullyQualifiedName.length() > 0;

      int pos = fullyQualifiedName.lastIndexOf('/');
      if (pos != -1)
      {
         return fullyQualifiedName.substring(pos + 1, fullyQualifiedName.length());
      }

      return fullyQualifiedName;
   }

   public static CompilationUnit getCompilationUnit(String fullyQualifiedTypeName)
   {
      assert fullyQualifiedTypeName != null;
      assert fullyQualifiedTypeName.length() > 0;
      return s_FullyQualifiedNameToCompilationUnit.get(fullyQualifiedTypeName);
   }

   public static CompilationUnit[] getCompilationUnits()
   {
      return s_FullyQualifiedNameToCompilationUnit.values().toArray(new CompilationUnit[0]);
   }

   CompilationUnit(String fullyQualifiedName, String packageName)
   {
      super(fullyQualifiedName, MetricsProviderIf.NO_SOURCE);
      assert packageName != null;
      assert packageName.length() > 0;
      assert !s_FullyQualifiedNameToCompilationUnit.containsKey(fullyQualifiedName);
      name = getCompilationUnitName(fullyQualifiedName);

      ConfigurationProviderIf configurationProvider = ProviderFactory.getInstance().getConfigurationProvider();
      RefactoringIf[] refactorings = configurationProvider.getRefactorings();

      for (RefactoringIf refactoring : refactorings)
      {
         if (refactoring.match(fullyQualifiedName))
         {
            packageName = refactoring.refactor(fullyQualifiedName);
            if (packageName.indexOf('.') >= 0)
               packageName = packageName.substring(0, packageName.lastIndexOf('.'));
            else if (packageName.endsWith(fullyQualifiedName) && packageName.length() > fullyQualifiedName.length())
            {
               packageName = packageName.substring(0, packageName.length() - fullyQualifiedName.length());
            }
            break;
         }
      }

      assert (packageName != null);

      this.packageName = packageName;
      s_FullyQualifiedNameToCompilationUnit.put(fullyQualifiedName, this);
      wasRefactored(true);
   }

   final protected void efferentAdded(DependencyElement efferent)
   {
      assert efferent != null;
      ++s_NumberOfEfferentDependencies;
   }

   public final boolean contains(DependencyElementIf element)
   {
      return containsType((Type)element);
   }

   public void collectMetrics()
   {
      super.collectMetrics();

      if (belongsToProject())
      {
         addMetric(MetricEnum.NUMBER_OF_TYPES, getNumberOfTypes());
         addMetric(MetricEnum.NUMBER_OF_ACCESSIBLE_TYPES, numberOfAccessibleTypes );
         addMetric(MetricEnum.NUMBER_OF_CONCRETE_TYPES, getNumberOfTypes() - numberOfAbstractTypes );
         addMetric(MetricEnum.NUMBER_OF_ABSTRACT_TYPES, numberOfAbstractTypes );
         addMetric(MetricEnum.NUMBER_OF_PACKAGE_INTERNAL_RELATIONS, numberOfPackageInternalRelations );
         addMetric(MetricEnum.NUMBER_OF_PACKAGE_EXTERNAL_RELATIONS, numberOfPackageExternalRelations );

         if ( relatedPackageNameToRelation.size() > 0)
         {
            Set<ContainerRelationCounter> relationCounters = new TreeSet<ContainerRelationCounter>();

            ContainerRelationCounter[] allCounters = relatedPackageNameToRelation.values().toArray(
               new ContainerRelationCounter[0]);
            for (int i = 0; i < allCounters.length; i++)
            {
               boolean success = relationCounters.add(allCounters[i]);
               assert success;
            }

            ContainerRelationCounter[] sortedCounters = relationCounters.toArray(new ContainerRelationCounter[0]);
            assert sortedCounters.length > 0;

            String packageInfo = collectPackageInfo(sortedCounters);
            if (packageInfo != null)
            {
               addMetric(MetricEnum.MORE_PACKAGE_EXTERNAL_THAN_INTERNAL_RELATIONS_EXIST, "true");
               addMetric(MetricEnum.THE_MOST_EXTERNAL_RELATIONS_EXIST_WITH_PACKAGE, collectPackageInfo(sortedCounters) + " ("
                  + sortedCounters[0].getValue() + ")");
            }
         }

         if ( numberOfAccessibleTypes > 0)
         {
            DependencyElementIf[] afferents = getAfferents();
            boolean hasIncomingOuterPackageDependencies = false;
            for (int i = 0; i < afferents.length; i++)
            {
               if (!afferents[i].belongsToDependencyElement().equals(belongsToDependencyElement()))
               {
                  hasIncomingOuterPackageDependencies = true;
                  break;
               }
            }
            if (!hasIncomingOuterPackageDependencies)
            {
               addMetric(MetricEnum.CONTAINS_ACCESSIBLE_TYPES_BUT_NO_INCOMING_OUTER_PACKAGE_DEPENDENCIES_EXIST, "true");
            }
         }
      }
   }

   private String collectPackageInfo(ContainerRelationCounter[] sortedCounters)
   {
      assert AssertionUtility.checkArray(sortedCounters);
      assert sortedCounters.length > 0;

      List<String> names = new ArrayList<String>();
      boolean returnInfo = true;
      int minCount = sortedCounters[0].getValue();

      for (int i = 0; i < sortedCounters.length; i++)
      {
         String nextName = sortedCounters[i].getElementName();
         if (sortedCounters[i].getValue() == minCount)
         {
            if (nextName.equals(getPackageName()))
            {
               returnInfo = false;
               break;
            }
            else
            {
               names.add(nextName);
            }
         }
         else
         {
            break;
         }
      }

      if (returnInfo)
      {
         assert names.size() > 0;
         String[] namesAsArray = names.toArray(new String[0]);
         StringBuffer packageInfo = new StringBuffer(namesAsArray[0]);
         for (int i = 1; i < namesAsArray.length; i++)
         {
            packageInfo.append(',');
            packageInfo.append(namesAsArray[i]);
         }

         return packageInfo.toString();
      }

      return null;
   }

   final public String getName()
   {
      return name;
   }

   public final String getPackageName()
   {
      return packageName;
   }

   private void updatePackageRelationInfo(CompilationUnit relatedCompilationUnit)
   {
      assert relatedCompilationUnit != null;
      assert relatedCompilationUnit != this;

      if (relatedCompilationUnit.belongsToProject())
      {
         String relatedPackageName = relatedCompilationUnit.getPackageName();

         ContainerRelationCounter counter = relatedPackageNameToRelation.get(relatedPackageName);
         if (counter == null)
         {
            counter = new ContainerRelationCounter(relatedPackageName);
            relatedPackageNameToRelation.put( relatedPackageName, counter );
         }
         counter.increment();

         if (relatedPackageName.equals(getPackageName()))
         {
            ++numberOfPackageInternalRelations;
         }
         else
         {
            ++numberOfPackageExternalRelations;
         }
      }
   }

   public final void analyzeDependencies()
   {
      Type[] types = getTypes();
      for (int j = 0; j < types.length; j++)
      {
         Type type = types[j];

         DependencyElementIf[] afferentTypes = type.getAfferents();
         for (int i = 0; i < afferentTypes.length; ++i)
         {
            Type nextAfferentType = (Type)afferentTypes[i];
            String compilationUnitName = nextAfferentType.getCompilationUnitName();

            if (!getFullyQualifiedName().equals(compilationUnitName))
            {
               CompilationUnit afferentCompilationUnit = getCompilationUnit(compilationUnitName);
               assert afferentCompilationUnit != null;
               addAfferent(afferentCompilationUnit, type, nextAfferentType);
               updatePackageRelationInfo(afferentCompilationUnit);
            }
         }

         if (type.belongsToProject())
         {
            DependencyElementIf[] efferentTypes = type.getEfferents();
            for (int i = 0; i < efferentTypes.length; ++i)
            {
               Type nextEfferentType = (Type)efferentTypes[i];
               String compilationUnitName = nextEfferentType.getCompilationUnitName();

               if (!getFullyQualifiedName().equals(compilationUnitName))
               {
                  CompilationUnit efferentCompilationUnit = getCompilationUnit(compilationUnitName);
                  assert efferentCompilationUnit != null;
                  addEfferent(efferentCompilationUnit, type, nextEfferentType);
                  updatePackageRelationInfo(efferentCompilationUnit);
               }
            }
         }
      }
   }

   public boolean hasViewableSourceFile()
   {
      return absoluteSourcePath != null;
   }

   public File getAbsoluteSourcePath()
   {
      assert hasViewableSourceFile();
      return absoluteSourcePath;
   }

   public final void addType(Type type)
   {
      super.addType(type);

      assert type.getCompilationUnitName().equals(getFullyQualifiedName());

      if (!type.getPackageName().equals( getPackageName() ))
      {
         // this is possible in C++
         LOGGER.warn( "compilation unit '" + getName() + "' package name '" + getPackageName() + "' defines type '"
                 + type.getName() + "' in package '" + type.getPackageName() + "'" );
      }

      type.setContainer(this, true);

      if (!type.wasRefactored())
      {
         wasRefactored(false);
      }

      if (type.isAccessible())
      {
         ++numberOfAccessibleTypes;
      }

      if (type.belongsToProject())
      {
         if (!type.isConcrete())
         {
            ++numberOfAbstractTypes;
         }

         if (type.hasViewableSourceFile() && !this.hasViewableSourceFile())
         {
            absoluteSourcePath = type.getAbsoluteSourcePath();
         }
      }
   }

   public final int getContainmentLevel()
   {
      return 1;
   }

   public final DependencyElementIf[] containsDependencyElements()
   {
      return getTypes();
   }

   final public String getElementName()
   {
      return ELEMENT_NAME;
   }

   public String getContainedElementName()
   {
      return Type.ELEMENT_NAME;
   }

   public final boolean isEfferentsCheckEnabled(DependencyElementIf efferent)
   {
      assert efferent != null;
      return false;
   }

   public final boolean belongsToProject()
   {
      return hasProjectInternalTypes();
   }

   public static void reset()
   {
      s_FullyQualifiedNameToCompilationUnit.clear();
      s_ForbiddenDependencies.clear();
      s_NumberOfEfferentDependencies = 0;
   }
   
   public EntityTypeEnum getEntityType()
   {
      return EntityTypeEnum.COMPILATION_UNIT;
   }
}