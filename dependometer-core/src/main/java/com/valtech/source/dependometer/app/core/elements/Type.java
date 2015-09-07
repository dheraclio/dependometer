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
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.metrics.TypeIf;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.IdentifierUtility;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract public class Type extends DependencyElement implements TypeIf
{
   /**
    * @deprecated use EntityTypeEnum instead
    */
   public static final String ELEMENT_NAME = "type";

   private static final int INHERITANCE_CLASS = 0;

   private static final int INHERITANCE_INTERFACE = 1;

   private static final Map<String, Type> s_FullyQualifiedNameToType = new TreeMap<String, Type>();

   private static final Set<DirectedDependency> s_ForbiddenDependencies = new TreeSet<DirectedDependency>();

   private static int s_MaxDepthOfTypeInheritance;

   private static int s_NumberOfEfferentDependencies;

   private final String name;

   private final String[] superClassNames;

   private final String[] superInterfaceNames;

   private final String compilationUnitName;

   private final String packageName;

   private final List<Type> m_Afferents = new ArrayList<Type>(5);

   private int maxDepthOfClassInheritance = -1;

   private int maxDepthOfInterfaceInheritance = -1;

   private int numberOfDirectSubtypes = 0;

   static int getNumberOfForbiddenEfferentTypeDependencies()
   {
      return s_ForbiddenDependencies.size();
   }

   static DirectedDependency[] getForbiddenEfferentTypeDependencies()
   {
      return s_ForbiddenDependencies.toArray(new DirectedDependency[0]);
   }

   static int getTotalNumberOfEfferentDependencies()
   {
      return s_NumberOfEfferentDependencies;
   }

   protected final void forbiddenEfferentAdded(DependencyElementIf forbidden)
   {
      assert isForbiddenEfferent(forbidden);
      s_ForbiddenDependencies.add(new DirectedDependency(this, forbidden,
         getNumberOfTypeRelationsForEfferent(forbidden), true));
   }

   static int getMaxDepthOfInheritance()
   {
      return s_MaxDepthOfTypeInheritance;
   }

   public static Type[] getTypes()
   {
      return s_FullyQualifiedNameToType.values().toArray(new Type[0]);
   }

   public static Type getType(String fullyQualifiedName)
   {
      assert fullyQualifiedName != null;
      assert fullyQualifiedName.length() > 0;
      return s_FullyQualifiedNameToType.get(fullyQualifiedName);
   }

   Type(String fullyQualifiedName, String[] superClassNames, String[] superInterfaceNames, String compilationUnitName,
      Object source)
   {
      super(fullyQualifiedName, source);

      assert compilationUnitName != null;
      assert compilationUnitName.length() > 0;
      assert AssertionUtility.checkArray(superClassNames);
      assert AssertionUtility.checkArray(superInterfaceNames);

      name = IdentifierUtility.getTypeName(fullyQualifiedName);
      this.superClassNames = Arrays.copyOf( superClassNames, superClassNames.length );
      this.superInterfaceNames = Arrays.copyOf( superInterfaceNames, superInterfaceNames.length );
      this.compilationUnitName = compilationUnitName;
      this.packageName = IdentifierUtility.getPackageName(fullyQualifiedName);

      s_FullyQualifiedNameToType.put(fullyQualifiedName, this);
   }

   public final String getName()
   {
      return name;
   }

   public final String[] getSuperClassNames()
   {
      return superClassNames;
   }

   public final String[] getSuperInterfaceNames()
   {
      return superInterfaceNames;
   }

   public final String getPackageName()
   {
      return packageName;
   }

   public final String getCompilationUnitName()
   {
      return compilationUnitName;
   }

   final void incrementNumberOfDirectSubtypes()
   {
      ++numberOfDirectSubtypes;
   }

   final int getNumberOfDirectSubtypes()
   {
      return numberOfDirectSubtypes;
   }

   private boolean isNested()
   {
      return getName().indexOf('$') != -1;
   }

   public abstract boolean isInterface();

   public abstract boolean isAccessible();

   public abstract boolean isConcrete();

   abstract boolean mayUseAssertions();

   public final boolean hasAccessibleTypes()
   {
      return isAccessible();
   }

   public final boolean hasConcreteTypes()
   {
      return isConcrete();
   }

   public void prepareCollectionOfMetrics()
   {
      super.prepareCollectionOfMetrics();

      int depthOfInheritance = getMaxDepthOfClassInheritance();
      if (depthOfInheritance > s_MaxDepthOfTypeInheritance)
      {
         s_MaxDepthOfTypeInheritance = depthOfInheritance;
      }

      depthOfInheritance = getMaxDepthOfInterfaceInheritance();
      if (depthOfInheritance > s_MaxDepthOfTypeInheritance)
      {
         s_MaxDepthOfTypeInheritance = depthOfInheritance;
      }
   }

   public void collectMetrics()
   {
      super.collectMetrics();

      if (belongsToProject())
      {
//         addMetric("abstract", isConcrete() ? "false" : "true");
//         addMetric("depth of class inheritance", getMaxDepthOfClassInheritance());
//         addMetric("depth of interface inheritance", getMaxDepthOfInterfaceInheritance());
         
         addMetric(MetricEnum.ABSTRACT, isConcrete() ? "false" : "true");
         addMetric(MetricEnum.DEPTH_OF_CLASS_INHERITANCE, getMaxDepthOfClassInheritance());
         addMetric(MetricEnum.DEPTH_OF_INTERFACE_INHERITANCE, getMaxDepthOfInterfaceInheritance());

         if (isInterface())
         {
            addMetric(MetricEnum.INTERFACE, "true");
         }
         addMetric(MetricEnum.NESTED, String.valueOf(isNested()));
      }

      addMetric(MetricEnum.ACCESSIBLE, isAccessible() ? "true" : "false");
      addMetric(MetricEnum.NUMBER_OF_CHILDREN, getNumberOfDirectSubtypes());
   }

   private int getMaxDepthOfClassInheritance()
   {
      if ( maxDepthOfClassInheritance == -1)
      {
         maxDepthOfClassInheritance = getMaxDepthOfInheritance(INHERITANCE_CLASS, this);
      }

      return maxDepthOfClassInheritance;
   }

   private int getMaxDepthOfInterfaceInheritance()
   {
      if ( maxDepthOfInterfaceInheritance == -1)
      {
         maxDepthOfInterfaceInheritance = getMaxDepthOfInheritance(INHERITANCE_INTERFACE, this);
      }

      return maxDepthOfInterfaceInheritance;
   }

   private static int getMaxDepthOfInheritance(int inheritanceType, Type type)
   {
      assert type != null;

      String[] superTypeNames = null;

      switch (inheritanceType)
      {
         case INHERITANCE_CLASS:
            superTypeNames = type.getSuperClassNames();
            break;
         case INHERITANCE_INTERFACE:
            superTypeNames = type.getSuperInterfaceNames();
            break;
         default:
            assert false;
            break;
      }

      if (superTypeNames.length == 0)
      {
         return 0;
      }

      int maxDepth = 0;

      for (int i = 0; i < superTypeNames.length; i++)
      {
         String nextSuperTypeName = superTypeNames[i];
         int depth = 0;
         Type superType = Type.getType(nextSuperTypeName);
         if (superType != null)
         {
            switch (inheritanceType)
            {
               case INHERITANCE_CLASS:
                  depth = superType.getMaxDepthOfClassInheritance() + 1;
                  break;
               case INHERITANCE_INTERFACE:
                  depth = superType.getMaxDepthOfInterfaceInheritance() + 1;
                  break;
               default:
                  assert false;
                  break;
            }

            if (depth > maxDepth)
            {
               maxDepth = depth;
            }
         }
      }

      return maxDepth;
   }

   final void addAfferent(Type type)
   {
      assert type != null;
      assert !m_Afferents.contains(type);
      m_Afferents.add(type);
      ++s_NumberOfEfferentDependencies;
   }

   public final DependencyElementIf[] getAfferents()
   {
      return m_Afferents.toArray(new Type[0]);
   }

   public final TypeIf[] getAfferentTypes()
   {
      return m_Afferents.toArray(new TypeIf[0]);
   }

   public final TypeIf[] getEfferentTypes()
   {
      return (TypeIf[])getProjectInternalEfferentElements();
   }

   public final int getNumberOfTypeRelationsForAfferent(DependencyElementIf dependencyElement)
   {
      assert isAfferent(dependencyElement);
      return 1;
   }

   public final int getNumberOfAfferents()
   {
      return m_Afferents.size();
   }

   public final boolean isAfferent(DependencyElementIf dependencyElement)
   {
      assert dependencyElement != null;
      assert m_Afferents != null;
      return m_Afferents.contains(dependencyElement);
   }

   public final int getContainmentLevel()
   {
      return 0; // Does not contain elements
   }

   public final DependencyElementIf[] containsDependencyElements()
   {
      return new DependencyElement[0]; // Does not contain elements
   }

   public final String getElementName()
   {
      return ELEMENT_NAME;
   }

   public final String getContainedElementName()
   {
      return null; // Does not contain elements
   }

   public final boolean hasProjectInternalTypes()
   {
      return belongsToProject();
   }

   public final boolean contains(DependencyElementIf element)
   {
      assert element != null;
      return false; // Does not contain elements
   }

   public static void reset()
   {
      s_FullyQualifiedNameToType.clear();
      s_ForbiddenDependencies.clear();

      s_MaxDepthOfTypeInheritance = 0;
      s_NumberOfEfferentDependencies = 0;
   }

   public String getInfo()
   {
      String s = getFullyQualifiedContainmentName() + "\n";
      s += "\tAfferent dependencies: ";
      for (Type t : m_Afferents)
      {
         s += "\t" + t.getFullyQualifiedContainmentName();
      }
      return s;
   }
   
   public EntityTypeEnum getEntityType()
   {
      return EntityTypeEnum.TYPE;
   }

}