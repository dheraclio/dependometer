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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.dependencyanalysis.NodeIf;
import com.valtech.source.dependometer.app.core.metrics.MetricsProvider;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;

/**
 * Baseclass for all elements that can have dependencies (i.e. layers, subsystems, packages, compilation units, types)
 * 
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class DependencyElement extends MetricsProvider implements DependencyElementIf, NodeIf
{
   /**
    * If a dependency element contains other elements (like a layer that contains subsystems) a dependency to another
    * layer is the result of coupled elements that are contained (a contained subsystem uses other subsystems from
    * another layer)
    */
   public static final class Coupling implements CouplingIf
   {
      public static final int AFFERENT = 0;

      public static final int EFFERENT = 1;

      private final int m_Direction;

      private final DependencyElement m_Contained;

      private final List<DependencyElement> m_NotContained = new ArrayList<DependencyElement>();

      Coupling(DependencyElement contained, int direction)
      {
         assert contained != null;
         assert direction == AFFERENT || direction == EFFERENT;

         m_Contained = contained;
         m_Direction = direction;
      }

      void addNotContained(DependencyElement notContained)
      {
         assert notContained != null;
         assert !m_NotContained.contains(notContained);
         assert (notContained.equals(m_Contained.belongsToDependencyElement()) ? m_Contained
            .belongsToDependencyElement() != notContained.belongsToDependencyElement() : true);

         m_NotContained.add(notContained);
      }

      int getDirection()
      {
         return m_Direction;
      }

      public DependencyElementIf getContained()
      {
         return m_Contained;
      }

      public DependencyElementIf[] getNotContainedRelatedElements()
      {
         return m_NotContained.toArray(new DependencyElement[0]);
      }

      public String getRelationQualifier(DependencyElementIf element)
      {
         assert element != null;
         assert m_NotContained.contains(element);

         if (getDirection() == AFFERENT)
         {
            return getContained().getAfferentRelationQualifier(element);
         }
         else
         {
            return getContained().getEfferentRelationQualifier(element);
         }
      }
   }

   protected static final String QUALIFICATION = "::";

   private int m_DependsUponNumberOfElements = -1;

   private boolean m_WasRefactored;

   private String m_FullyQualifiedContainmentName;

   private String m_Description;

   private DependencyElement m_BelongsTo;

   private final List<DependencyElement> m_Container = new ArrayList<DependencyElement>(1);

   private final Map<String, Map<String, Coupling>> m_AfferentToCouplings = new TreeMap<String, Map<String, Coupling>>();

   private final Map<String, Map<String, Coupling>> m_EfferentToCouplings = new TreeMap<String, Map<String, Coupling>>();

   private final Set<DependencyElement> m_AllowedEfferents = new HashSet<DependencyElement>();

   private final Set<DependencyElementIf> m_UsedAllowedEfferents = new TreeSet<DependencyElementIf>();

   private final Set<DependencyElement> m_NotUsedAllowedEfferents = new TreeSet<DependencyElement>();

   private final Set<DependencyElementIf> m_ForbiddenEfferents = new HashSet<DependencyElementIf>();

   protected DependencyElement(String fullyQualifiedName, Object source)
   {
      super(fullyQualifiedName, source);
   }

   public final String getFullyQualifiedContainmentName()
   {
      if (m_FullyQualifiedContainmentName == null)
      {
         StringBuffer fqname = new StringBuffer();
         ArrayList<String> names = new ArrayList<String>();

         DependencyElementIf current = this;
         while (current != null)
         {
            names.add(current.getName());
            current = current.belongsToDependencyElement();
         }

         int i = names.size() - 1;
         for (; i > 0; --i)
         {
            fqname.append(names.get(i));
            fqname.append(QUALIFICATION);
         }

         fqname.append(names.get(i));
         m_FullyQualifiedContainmentName = fqname.toString();
      }

      return m_FullyQualifiedContainmentName;
   }

   public final boolean hasDescription()
   {
      return m_Description != null;
   }

   public final String getDescription()
   {
      return m_Description;
   }

   public final boolean wasRefactored()
   {
      return m_WasRefactored;
   }

   public final void wasRefactored(boolean refactored)
   {
      m_WasRefactored = refactored;
   }

   public final void setDescription(String description)
   {
      assert m_Description == null;
      assert description != null;
      assert description.length() > 0;
      m_Description = description;
   }

   public void collectMetrics()
   {
      super.collectMetrics();

      if (wasRefactored())
      {
         addMetric(MetricEnum.REFACTORED, "true");
      }

      if (belongsToProject())
      {
         addMetric(MetricEnum.PROJECT_INTERNAL, "true");

         if (getDependsUpon() != -1) // was calculated
         {
            addMetric(MetricEnum.DEPENDS_UPON, getDependsUpon());
         }
         else
         {
            addMetricWithNotAnalyzedInfo(MetricEnum.DEPENDS_UPON );
         }

         addMetric(MetricEnum.NUMBER_OF_INCOMING_DEPENDENCIES, getNumberOfAfferents());
         addMetric(MetricEnum.NUMBER_OF_OUTGOING_DEPENDENCIES, getNumberOfProjectInternalEfferents());
         addMetric(MetricEnum.NUMBER_OF_OUTGOING_DEPENDENCIES_TO_PROJECT_EXTERNAL, getNumberOfEfferents()
            - getNumberOfProjectInternalEfferents());
         if (getNumberOfAfferents() == 0)
         {
            addMetric(MetricEnum.NO_INCOMING_DEPENCIES_DETECTED, "true");
            if (getNumberOfProjectInternalEfferents() == 0)
            {
               addMetric(MetricEnum.NO_DEPENCIES_DETECTED, "true");
            }
         }
         addMetric(MetricEnum.NUMBER_OF_FORBIDDEN_OUTGOING_DEPENDENCIES, getNumberOfForbiddenEfferentDependencies());
      }
      else
      {
         addMetric(MetricEnum.PROJECT_EXTERNAL, "true");
         addMetric(MetricEnum.NUMBER_OF_INCOMING_DEPENDENCIES_PROJECT_EXTERNAL, getNumberOfAfferents());
      }
   }

   abstract DependencyElement[] getProjectInternalEfferentElements();

   abstract int getNumberOfProjectInternalEfferents();

   final void setContainer(DependencyElement container, boolean belongsTo)
   {
      assert container != null;
      assert this != container;
      assert container.getContainmentLevel() == getContainmentLevel() + 1;
      m_Container.add(container);
      if (belongsTo)
      {
         assert m_BelongsTo == null;
         m_BelongsTo = container;
         belongsToSet(container);
      }
   }

   protected void belongsToSet(DependencyElementIf belongsTo)
   {
      assert belongsTo != null;
   }

   public final DependencyElementIf belongsToDependencyElement()
   {
      return m_BelongsTo;
   }

   public final DependencyElementIf[] containingDependencyElements()
   {
      return m_Container.toArray(new DependencyElementIf[0]);
   }

   protected final Coupling getAfferentCoupling(DependencyElement afferent, DependencyElement contained)
   {
      return getCoupling(afferent, contained, Coupling.AFFERENT);
   }

   protected final Coupling getEfferentCoupling(DependencyElement efferent, DependencyElement contained)
   {
      return getCoupling(efferent, contained, Coupling.EFFERENT);
   }

   protected Coupling getCoupling(DependencyElement related, DependencyElement contained, int direction)
   {
      assert related != null;
      assert getContainmentLevel() == related.getContainmentLevel();
      assert contained != null;
      assert getContainmentLevel() == contained.getContainmentLevel() + 1;
      assert direction == Coupling.AFFERENT || direction == Coupling.EFFERENT;

      Map<String, Map<String, Coupling>> relatedToCouplings = null;

      if (direction == Coupling.AFFERENT)
      {
         relatedToCouplings = m_AfferentToCouplings;
      }
      else
      {
         relatedToCouplings = m_EfferentToCouplings;
      }

      Map<String, Coupling> containedToCoupling = relatedToCouplings.get(related.getFullyQualifiedName());

      if (containedToCoupling == null)
      {
         containedToCoupling = new TreeMap<String, Coupling>();
         relatedToCouplings.put(related.getFullyQualifiedName(), containedToCoupling);
      }

      Coupling coupling = containedToCoupling.get(contained.getFullyQualifiedName());
      if (coupling == null)
      {
         coupling = new Coupling(contained, direction);
         containedToCoupling.put(contained.getFullyQualifiedName(), coupling);
      }

      return coupling;
   }

   public final CouplingIf[] getAfferentCouplings(DependencyElementIf afferent)
   {
      return getCouplings((DependencyElement)afferent, Coupling.AFFERENT);
   }

   public final CouplingIf[] getEfferentCouplings(DependencyElementIf efferent)
   {
      return getCouplings((DependencyElement)efferent, Coupling.EFFERENT);
   }

   private Coupling[] getCouplings(DependencyElement related, int direction)
   {
      assert related != null;
      assert this != related;
      assert getContainmentLevel() == related.getContainmentLevel();
      assert direction == Coupling.AFFERENT || direction == Coupling.EFFERENT;

      Map<String, Map<String, Coupling>> relatedToCouplings = null;

      if (direction == Coupling.AFFERENT)
      {
         relatedToCouplings = m_AfferentToCouplings;
      }
      else
      {
         relatedToCouplings = m_EfferentToCouplings;
      }

      Map<String, Coupling> containedToCoupling = relatedToCouplings.get(related.getFullyQualifiedName());
      if (containedToCoupling != null)
      {
         return containedToCoupling.values().toArray(new Coupling[0]);
      }
      else
      {
         return new Coupling[0];
      }
   }

   public final String getAfferentRelationQualifier(DependencyElementIf relationFrom)
   {
      assert relationFrom != null;
      return ((DependencyElement)relationFrom).getRelationQualifer(this);
   }

   public final String getEfferentRelationQualifier(DependencyElementIf relationTo)
   {
      assert relationTo != null;
      return getRelationQualifer((DependencyElement)relationTo);
   }

   protected abstract String getRelationQualifer(DependencyElement relationTo);

   public final DependencyElementIf[] getUsedAllowedEfferents()
   {
      return m_UsedAllowedEfferents.toArray(new DependencyElement[0]);
   }

   public final DependencyElementIf[] getUnusedAllowedEfferents()
   {
      return m_NotUsedAllowedEfferents.toArray(new DependencyElement[0]);
   }

   public final boolean isAllowedEfferent(DependencyElementIf allowed)
   {
      assert allowed != null;
      assert this != allowed;
      assert getContainmentLevel() == ((DependencyElement)allowed).getContainmentLevel();
      return m_AllowedEfferents.contains(allowed);
   }

   final DependencyElementIf[] getAllowedEfferents()
   {
      return m_AllowedEfferents.toArray(new DependencyElementIf[0]);
   }

   public final void addAllowedEfferent(DependencyElement allowed)
   {
      assert allowed != null;
      assert this != allowed;
      assert getContainmentLevel() == allowed.getContainmentLevel();
      assert !m_AllowedEfferents.contains(allowed);

      m_AllowedEfferents.add(allowed);
      m_NotUsedAllowedEfferents.add(allowed);
      allowedEfferentAdded(allowed);
   }

   protected void allowedEfferentAdded(DependencyElement allowed)
   {
      assert allowed != null;
   }

   protected final boolean hasAllowedEfferents()
   {
      return m_AllowedEfferents.size() > 0;
   }

   public final void checkEfferents()
   {
      if (performEfferentsCheck())
      {
         DependencyElementIf[] efferents = getEfferents();
         for (int i = 0; i < efferents.length; i++)
         {
            DependencyElementIf next = efferents[i];
            if (!m_AllowedEfferents.contains(next))
            {
               setForbiddenEfferent(next);
            }
            else
            {
               m_NotUsedAllowedEfferents.remove(next);
               m_UsedAllowedEfferents.add(next);
            }
         }
      }
   }

   protected boolean performEfferentsCheck()
   {
      return true;
   }

   private final void setForbiddenEfferent(DependencyElementIf efferent)
   {
      assert efferent != null;
      assert this != efferent;
      assert getContainmentLevel() == ((DependencyElement)efferent).getContainmentLevel();
      assert isEfferent(efferent);

      if (m_ForbiddenEfferents.add(efferent))
      {
         forbiddenEfferentAdded(efferent);

         CouplingIf[] couplings = getEfferentCouplings(efferent);
         for (int i = 0; i < couplings.length; ++i)
         {
            CouplingIf next = couplings[i];
            DependencyElementIf contained = next.getContained();
            DependencyElementIf[] notContained = next.getNotContainedRelatedElements();
            for (int j = 0; j < notContained.length; ++j)
            {
               ((DependencyElement)contained).setForbiddenEfferent(notContained[j]);
            }
         }
      }
   }

   protected void forbiddenEfferentAdded(DependencyElementIf forbidden)
   {
      assert forbidden != null;
   }

   public final boolean isForbiddenEfferent(DependencyElementIf efferent)
   {
      assert efferent != null;
      assert this != efferent;
      assert getContainmentLevel() == ((DependencyElement)efferent).getContainmentLevel();
      assert isEfferent(efferent);

      return m_ForbiddenEfferents.contains(efferent);
   }

   public final int getNumberOfForbiddenEfferentDependencies()
   {
      return m_ForbiddenEfferents.size();
   }

   public final DependencyElement[] getForbiddenEfferents()
   {
      return m_ForbiddenEfferents.toArray(new DependencyElement[0]);
   }

   public final NodeIf[] getAfferentNodes()
   {
      return (NodeIf[])getAfferents();
   }

   public final NodeIf[] getEfferentNodes()
   {
      return getProjectInternalEfferentElements();
   }

   public final int getNumberOfEfferentNodes()
   {
      return getNumberOfProjectInternalEfferents();
   }

   public final void setDependsUpon(int dependsUponNumberOfElements)
   {
      assert m_DependsUponNumberOfElements == -1;
      m_DependsUponNumberOfElements = dependsUponNumberOfElements;
   }

   public final int getDependsUpon()
   {
      return m_DependsUponNumberOfElements;
   }

   public final boolean wasDependsUponSet()
   {
      return m_DependsUponNumberOfElements != -1;
   }

   protected abstract int getNumberOfAssertions();
   
   public final String toString()
   {
      return getFullyQualifiedContainmentName();
   }
}