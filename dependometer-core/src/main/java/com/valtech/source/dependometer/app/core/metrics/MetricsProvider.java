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
package com.valtech.source.dependometer.app.core.metrics;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.provider.MetricIf;
import com.valtech.source.dependometer.app.core.provider.MetricsProviderIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class MetricsProvider implements MetricsProviderIf
{
   private static final String NOT_ANALYZED = "! not analyzed !";

   private static final List<MetricsProvider> s_Provider = new ArrayList<MetricsProvider>(100);

   private final Map<String, Metric> nameToMetric = new TreeMap<String, Metric>();

   private final Object m_Source;

   private final String m_FullyQualifiedName;

   private int m_HashCode;

   private String m_NameForHashCode;

   protected MetricsProvider(String fullyQualifiedName, Object source)
   {
      assert fullyQualifiedName != null;
      assert fullyQualifiedName.length() > 0;
      assert source != null;
      m_FullyQualifiedName = fullyQualifiedName;
      m_Source = source;
      s_Provider.add(this);
   }

   public static MetricsProvider[] getMetricsProvider()
   {
      return s_Provider.toArray(new MetricsProvider[0]);
   }

   public final Object getSource()
   {
      return m_Source;
   }

   protected void addMetric(MetricEnum m, int value)
   {
      assert m != null;
      assert !nameToMetric.containsKey(m.getDisplayName());
      nameToMetric.put(m.getDisplayName(), new IntMetric(m, value));
   }

   protected void addMetric(MetricEnum m, double value)
   {
      assert m != null;
      assert !nameToMetric.containsKey(m.getDisplayName());
      nameToMetric.put(m.getDisplayName(), new DoubleMetric(m, value));
   }

   protected void addMetricWithInfo(MetricEnum m, String info)
   {
      assert m != null;
      assert !nameToMetric.containsKey(m.getDisplayName());
      nameToMetric.put(m.getDisplayName(), new InfoMetric(m, info));
   }

   protected void addMetricWithNotAnalyzedInfo(MetricEnum m)
   {
      assert m != null;
      assert !nameToMetric.containsKey(m.getDisplayName());
      nameToMetric.put(m.getDisplayName(), new InfoMetric(m, NOT_ANALYZED));
   }

   protected void addMetric(MetricEnum m, boolean value)
   {
      assert m != null;
      assert !nameToMetric.containsKey(m.getDisplayName());
      nameToMetric.put(m.getDisplayName(), new BooleanMetric(m, value));
   }

   protected void addMetric(MetricEnum m, String value)
   {
      assert m != null;
      assert !nameToMetric.containsKey(m.getDisplayName());
      nameToMetric.put(m.getDisplayName(), new TextMetric(m, value));
   }

   public void prepareCollectionOfMetrics()
   {
      // Should be overwritten
   }

   public void collectMetrics()
   {
      // Should be overwritten
   }

   public final MetricIf[] getMetrics()
   {
      return nameToMetric.values().toArray(new MetricIf[0]);
   }

   protected void relationalCohesion(double rc)
   {
      // Should be overwritten
   }

   protected final void calculateTypeMetricsForProjectExternalTypes(TypeIf[] types)
   {
      assert AssertionUtility.checkArray(types);
      Set<TypeIf> allAfferentTypes = new HashSet<TypeIf>();

      for (int i = 0; i < types.length; i++)
      {
         TypeIf nextType = types[i];
         assert !nextType.belongsToProject();

         TypeIf[] afferentTypes = nextType.getAfferentTypes();
         for (int j = 0; j < afferentTypes.length; ++j)
         {
            TypeIf nextAfferentType = afferentTypes[j];
            assert nextAfferentType.belongsToProject();

            if (!belongsToTypeGroup(nextAfferentType, types))
            {
               allAfferentTypes.add(nextAfferentType);
            }
         }
      }

      addMetric(MetricEnum.AFFERENT_INCOMING_COUPLING_PROJECT_EXTERNAL, allAfferentTypes.size());
   }

   protected final void calculateTypeMetricsForProjectInternalTypes(TypeIf[] types)
   {
      assert AssertionUtility.checkArray(types);

      int numberOfTypes = types.length;
      int numberOfAccessibleTypes = 0;
      int numberOfConcreteTypes = 0;
      int numberOfAbstractTypes = 0;
      int numberOfInternalRelations = 0;
      int numberOfExternalRelations = 0;
      int numberOfAfferentTypes = 0;
      int numberOfEfferentTypes = 0;

      Set<TypeIf> allAfferentTypes = new HashSet<TypeIf>();
      Set<TypeIf> allEfferentTypes = new HashSet<TypeIf>();

      for (int i = 0; i < numberOfTypes; ++i)
      {
         TypeIf nextType = types[i];
         assert nextType.belongsToProject();

         if (nextType.isConcrete())
         {
            ++numberOfConcreteTypes;
         }
         else
         {
            ++numberOfAbstractTypes;
         }

         if (nextType.isAccessible())
         {
            ++numberOfAccessibleTypes;
         }

         TypeIf[] afferentTypes = nextType.getAfferentTypes();
         for (int j = 0; j < afferentTypes.length; ++j)
         {
            TypeIf nextAfferentType = afferentTypes[j];
            assert nextAfferentType.belongsToProject();

            if (!belongsToTypeGroup(nextAfferentType, types))
            {
               allAfferentTypes.add(nextAfferentType);
               ++numberOfExternalRelations;
            }
         }

         TypeIf[] efferentTypes = nextType.getEfferentTypes();
         for (int j = 0; j < efferentTypes.length; ++j)
         {
            TypeIf nextEfferentType = efferentTypes[j];
            assert nextEfferentType.belongsToProject();

            if (!belongsToTypeGroup(nextEfferentType, types))
            {
               allEfferentTypes.add(nextEfferentType);
               ++numberOfExternalRelations;
            }
            else
            {
               ++numberOfInternalRelations;
            }
         }
      }

      numberOfAfferentTypes = allAfferentTypes.size();
      numberOfEfferentTypes = allEfferentTypes.size();

      double relationalCohesion = 0.0;
      double instability = 1.0;
      double abstractness = 0.0;
      double distance = 0.0;

      if (numberOfTypes > 0)
      {
         relationalCohesion = (double)numberOfInternalRelations / (double)numberOfTypes;

         if (numberOfAfferentTypes > 0)
         {
            instability = (double)numberOfEfferentTypes / (double)(numberOfAfferentTypes + numberOfEfferentTypes);
         }

         abstractness = (double)numberOfAbstractTypes / (double)numberOfTypes;
         distance = instability + abstractness - 1.0;
      }
      addMetric(MetricEnum.NUMBER_OF_TYPES, numberOfTypes);
      addMetric(MetricEnum.NUMBER_OF_ACCESSIBLE_TYPES, numberOfAccessibleTypes);
      addMetric(MetricEnum.NUMBER_OF_ABSTRACT_TYPES, numberOfAbstractTypes);

      addMetric(MetricEnum.NUMBER_OF_INTERNAL_TYPE_RELATIONS, numberOfInternalRelations);
      addMetric(MetricEnum.NUMBER_OF_EXTERNAL_TYPE_RELATIONS, numberOfExternalRelations);
      addMetric(MetricEnum.RELATIONAL_COHESION, relationalCohesion);
      addMetric(MetricEnum.AFFERENT_INCOMING_COUPLING, numberOfAfferentTypes);
      addMetric(MetricEnum.EFFERENT_OUTGOING_COUPLING, numberOfEfferentTypes);

      addMetric(MetricEnum.DISTANCE, distance);
      addMetric(MetricEnum.ABSTRACTNESS, abstractness);
      addMetric(MetricEnum.INSTABILITY, instability);

      relationalCohesion(relationalCohesion);
   }

   private boolean belongsToTypeGroup(TypeIf type, TypeIf[] types)
   {
      assert type != null;
      assert AssertionUtility.checkArray(types);

      for (int i = 0; i < types.length; ++i)
      {
         if (types[i].equals(type))
         {
            return true;
         }
      }

      return false;
   }

   protected final void calculateComponentMetrics(ComponentIf[] components, boolean calculateCompareMetrics)
   {
      assert AssertionUtility.checkArray(components);

      int n = components.length;
      int ccd = 0;
      double acd = 0.0;
      boolean dependsUponSet = false;

      if (n > 0)
      {
         for (int i = 0; i < n; ++i)
         {
            assert components[i].belongsToProject();
            if (components[i].wasDependsUponSet())
            {
               ccd += components[i].getDependsUpon();
               dependsUponSet = true;
            }
         }

         if (dependsUponSet)
         {
            acd = (double)ccd / (double)n;
         }
      }

      addMetric(MetricEnum.NUMBER_OF_COMPONENTS, n);
      if (dependsUponSet)
      {
         addMetric(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY, ccd);
         addMetric(MetricEnum.AVERAGE_COMPONENT_DEPENDENCY, acd);

         setAverageComponentDependency(acd);

         if (calculateCompareMetrics)
         {
            int ccd_cdg = n * n;
            int ccd_bbt = (n + 1) * ((int)(Math.log(n + 1) / Math.log(2))) - n;
            double nccd = (double)ccd / (double)ccd_bbt;

            addMetric(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY_FOR_BALANCED_BINARY_TREE, ccd_bbt);
            addMetric(MetricEnum.CUMULATIVE_COMPONENT_DEPENDENCY_FOR_CYCLICALLY_DEPENDENT_GRAPH, ccd_cdg);
            addMetric(MetricEnum.NORMALIZED_CUMULATIVE_COMPONENT_DEPENDENCY, nccd);
         }
      }
   }

   protected void setAverageComponentDependency(double acd)
   {
      // Should be overwritten
   }

   protected final double calculateRcPercentageNotLessThanOne(double[] rcs)
   {
      assert rcs != null;

      int numberOfRcNotLessThanOne = 0;

      for (int i = 0; i < rcs.length; ++i)
      {
         if (rcs[i] >= 1.0)
         {
            ++numberOfRcNotLessThanOne;
         }
      }

      double result = (double)numberOfRcNotLessThanOne / (double)rcs.length;
      return result * 100.0;
   }

   protected static int cumulateNumberOfAssertions(int cumulated, int toAdd)
   {
      assert toAdd >= -1; // -1 means not analyzed

      if (toAdd != -1)
      {
         if (cumulated == -1)
         {
            cumulated = toAdd;
         }
         else
         {
            cumulated += toAdd;
         }
      }

      return cumulated;
   }

   public final String getFullyQualifiedName()
   {
      return m_FullyQualifiedName;
   }

   public abstract int getContainmentLevel();

   public abstract String getElementName();

   @Override
   public boolean equals(Object obj)
   {
      if (obj == null)
      {
         return false;
      }
      else if (obj == this)
      {
         return true;
      }
      else if (obj instanceof MetricsProvider)
      {
         if (getFullyQualifiedName().equals(((MetricsProvider)obj).getFullyQualifiedName())
            && getContainmentLevel() == ((MetricsProvider)obj).getContainmentLevel())
         {
            return true;
         }
         else
         {
            return false;
         }
      }
      else
      {
         return false;
      }
   }

   public final int hashCode()
   {
      if (m_NameForHashCode == null)
      {
         m_NameForHashCode = "[" + getElementName() + "] " + m_FullyQualifiedName;
         m_HashCode = m_NameForHashCode.hashCode();
      }

      return m_HashCode;
   }

   public final int compareTo(MetricsProviderIf mpIf)
   {
      assert mpIf != null;
      assert mpIf instanceof MetricsProvider;
      int compared = m_FullyQualifiedName.compareTo(mpIf.getFullyQualifiedName());
      if (compared == 0)
      {
         compared = mpIf.getContainmentLevel() - getContainmentLevel();
      }
      return compared;
   }

   public static void reset()
   {
      s_Provider.clear();
   }

   public MetricIf getMetricByName(String metricName)
   {
      return nameToMetric.get(metricName);
   }

   /**
    * Returns a <code>MetricIf</code> according to the given <code>MetricEnum</code>. If the metric is not available, a
    * <code>NullMetric</code> is returned.
    * 
    * @param metricEnum
    * @return the <code>MetricIf</code> or an <code>NullMetric</code> if the metric is not available.
    */
   public MetricIf getMetric(MetricEnum metricEnum)
   {
      MetricIf metric = nameToMetric.get(metricEnum.getDisplayName());

      return metric == null ? new NullMetric(metricEnum) : metric;
   }

   /**
    * Returns the value of the <code>MetricIf</code> according to the given <code>MetricEnum</code>. If the metric is
    * not available, the value of a <code>NullMetric</code> 'n/a' is returned.
    * 
    * @param metricEnum
    * @return the value of the <code>MetricIf</code> or the value of the <code>NullMetric</code> 'n/a' if the metric is
    *         not available.
    */
   public String getMetricValue(MetricEnum metricEnum)
   {
      return getMetric(metricEnum).getValueAsString();
   }

}