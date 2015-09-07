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

import com.valtech.source.ag.util.FlexInteger;
import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.metrics.ComponentIf;
import com.valtech.source.dependometer.app.core.metrics.MetricsProvider;
import com.valtech.source.dependometer.app.core.metrics.TypeIf;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.MetricsProviderIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract class TypeGroupingElement extends DependencyElement implements ComponentIf
{
   private final Set<Type> m_Types = new TreeSet<Type>();

   private final List<Type> m_ProjectInternalTypes = new ArrayList<Type>(5);

   private final List<Type> m_ProjectExternalTypes = new ArrayList<Type>(5);

   private final Map<DependencyElement, FlexInteger> m_Afferents = new TreeMap<DependencyElement, FlexInteger>();

   private final Map<DependencyElement, FlexInteger> m_Efferents = new TreeMap<DependencyElement, FlexInteger>();

   private final List<DependencyElement> m_ProjectInternalEfferents = new ArrayList<DependencyElement>(5);

   private int m_NumberOfAssertions = MetricsProviderIf.INITIAL_NUMBER_OF_ASSERTIONS;

   private int m_NumberOfTypesThatMayUseAssertions;

   private boolean m_HasAccessibleElements = false;

   private boolean m_HasOnlyAbstractElements = true;

   private DependencyElement[] m_ProjectInternalEfferentsAsArray;

   TypeGroupingElement(String fullyQualifiedName, Object source)
   {
      super(fullyQualifiedName, source);
   }

   public abstract void analyzeDependencies();

   final int getNumberOfTypes()
   {
      return m_Types.size();
   }

   void addType(Type type)
   {
      assert type != null;
      assert !m_Types.contains(type);

      m_Types.add(type);

      if (type.belongsToProject())
      {
         assert !m_ProjectInternalTypes.contains(type);
         if (type.mayUseAssertions())
         {
            m_NumberOfAssertions = MetricsProvider.cumulateNumberOfAssertions(m_NumberOfAssertions, type
               .getNumberOfAssertions());
            m_NumberOfTypesThatMayUseAssertions++;
         }
         m_ProjectInternalTypes.add(type);
         if (m_HasOnlyAbstractElements && type.isConcrete())
         {
            m_HasOnlyAbstractElements = false;
         }
      }
      else
      {
         assert !m_ProjectExternalTypes.contains(type);
         m_ProjectExternalTypes.add(type);
      }

      if (!m_HasAccessibleElements && type.isAccessible())
      {
         m_HasAccessibleElements = true;
      }
   }

   final Type[] getTypes()
   {
      return m_Types.toArray(new Type[0]);
   }

   final boolean containsType(Type element)
   {
      assert element != null;
      return m_Types.contains(element);
   }

   public final boolean hasAccessibleTypes()
   {
      return m_HasAccessibleElements;
   }

   public final boolean hasConcreteTypes()
   {
      return !m_HasOnlyAbstractElements;
   }

   public void collectMetrics()
   {
      super.collectMetrics();

      if (belongsToProject())
      {
         if (getNumberOfTypesThatMayUseAssertions() > 0)
         {
            if (getNumberOfAssertions() > -1)
            {
               addMetric(MetricEnum.NUMBER_OF_ASSERTIONS, getNumberOfAssertions());
            }
            else
            {
               addMetricWithNotAnalyzedInfo(MetricEnum.NUMBER_OF_ASSERTIONS );
            }
         }
         else
         {
            addMetricWithInfo(MetricEnum.NUMBER_OF_ASSERTIONS, "contains no types that may use assertions");
         }
      }
   }

   final protected int getNumberOfAssertions()
   {
      return m_NumberOfAssertions;
   }

   public final boolean hasProjectInternalTypes()
   {
      return m_ProjectInternalTypes.size() > 0;
   }

   final TypeIf[] getProjectInternalTypes()
   {
      return m_ProjectInternalTypes.toArray(new TypeIf[0]);
   }

   final TypeIf[] getProjectExternalTypes()
   {
      return m_ProjectExternalTypes.toArray(new TypeIf[0]);
   }

   final int getNumberOfTypesThatMayUseAssertions()
   {
      return m_NumberOfTypesThatMayUseAssertions;
   }

   final void addAfferent(DependencyElement afferent, DependencyElement contained,
      DependencyElement notContainedAfferent)
   {
      assert afferent != null;
      assert this.getContainmentLevel() == afferent.getContainmentLevel();
      assert contained != null;
      assert notContainedAfferent != null;
      assert contained.isAfferent(notContainedAfferent);

      FlexInteger count = m_Afferents.get(afferent);
      if (count == null)
      {
         count = new FlexInteger();
         m_Afferents.put(afferent, count);
      }

      count.add(contained.getNumberOfTypeRelationsForAfferent(notContainedAfferent));

      Coupling coupling = getAfferentCoupling(afferent, contained);
      coupling.addNotContained(notContainedAfferent);
   }

   public final int getNumberOfAfferents()
   {
      return m_Afferents.size();
   }

   final public boolean isAfferent(DependencyElementIf dependencyElement)
   {
      return m_Afferents.containsKey(dependencyElement);
   }

   public final DependencyElementIf[] getAfferents()
   {
      return m_Afferents.keySet().toArray(new DependencyElement[0]);
   }

   public final int getNumberOfTypeRelationsForAfferent(DependencyElementIf afferent)
   {
      assert isAfferent(afferent);
      assert m_Afferents.containsKey(afferent);
      return m_Afferents.get(afferent).getValue();
   }

   protected void efferentAdded(DependencyElement efferent)
   {
      assert efferent != null;
   }

   final void addEfferent(DependencyElement efferent, DependencyElement contained,
      DependencyElement notContainedEfferent)
   {
      assert efferent != null;
      assert this.getContainmentLevel() == efferent.getContainmentLevel();
      assert contained != null;
      assert notContainedEfferent != null;
      assert contained.isEfferent(notContainedEfferent);

      FlexInteger count = m_Efferents.get(efferent);
      if (count == null)
      {
         count = new FlexInteger();
         m_Efferents.put(efferent, count);

         if (efferent.belongsToProject())
         {
            m_ProjectInternalEfferents.add(efferent);
            m_ProjectInternalEfferentsAsArray = null;
         }

         efferentAdded(efferent);
      }

      count.add(contained.getNumberOfTypeRelationsForEfferent(notContainedEfferent));
      Coupling coupling = getEfferentCoupling(efferent, contained);
      coupling.addNotContained(notContainedEfferent);
   }

   final public int getNumberOfEfferents()
   {
      return m_Efferents.size();
   }

   final public boolean isEfferent(DependencyElementIf dependencyElement)
   {
      return m_Efferents.containsKey(dependencyElement);
   }

   public final DependencyElementIf[] getEfferents()
   {
      return m_Efferents.keySet().toArray(new DependencyElement[0]);
   }

   public final DependencyElement[] getProjectInternalEfferentElements()
   {
      if (m_ProjectInternalEfferentsAsArray == null)
      {
         m_ProjectInternalEfferentsAsArray = m_ProjectInternalEfferents.toArray(new DependencyElement[0]);
      }

      return m_ProjectInternalEfferentsAsArray;
   }

   public final int getNumberOfTypeRelationsForEfferent(DependencyElementIf efferent)
   {
      assert isEfferent(efferent);
      assert m_Efferents.containsKey(efferent);
      FlexInteger flexInt = m_Efferents.get(efferent);
      return flexInt.getValue();
   }

   final public String getRelationQualifer(DependencyElement relationTo)
   {
      assert relationTo != null;
      assert isEfferent(relationTo);
      return USES;
   }

   final int getNumberOfProjectInternalEfferents()
   {
      return m_ProjectInternalEfferents.size();
   }
}