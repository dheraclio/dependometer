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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.valtech.source.dependometer.app.core.common.MetricEnum;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract class CompilationUnitGroupingElement extends TypeGroupingElement
{
   private final Set<CompilationUnit> m_CompilationUnits = new TreeSet<CompilationUnit>();

   private final List<CompilationUnit> m_ProjectInternalCompilationUnits = new ArrayList<CompilationUnit>();

   CompilationUnitGroupingElement(String fullyQualifiedName, Object source)
   {
      super(fullyQualifiedName, source);
   }

   void addCompilationUnit(CompilationUnit compilationUnit)
   {
      assert compilationUnit != null;
      assert !m_CompilationUnits.contains(compilationUnit);

      m_CompilationUnits.add(compilationUnit);
      if (compilationUnit.belongsToProject())
      {
         assert !m_ProjectInternalCompilationUnits.contains(compilationUnit);
         m_ProjectInternalCompilationUnits.add(compilationUnit);
      }
   }

   public void collectMetrics()
   {
      super.collectMetrics();
      if (belongsToProject())
      {
         calculateTypeMetricsForProjectInternalTypes(getProjectInternalTypes());
         calculateComponentMetrics(getProjectInternalCompilationUnits(), false);
         if (getNumberOfTypesThatMayUseAssertions() > 0 && getNumberOfAssertions() != -1)
         {
            addMetric(MetricEnum.AVERAGE_USAGE_OF_ASSERTIONS_PER_CLASS, (double)getNumberOfAssertions()
               / (double)getNumberOfTypesThatMayUseAssertions());
         }
      }
      else
      {
         calculateTypeMetricsForProjectExternalTypes(getProjectExternalTypes());
      }
   }

   public final CompilationUnit[] getProjectInternalCompilationUnits()
   {
      return m_ProjectInternalCompilationUnits.toArray(new CompilationUnit[0]);
   }

   public final CompilationUnit[] getCompilationUnits()
   {
      return m_CompilationUnits.toArray(new CompilationUnit[0]);
   }

   final boolean containsCompilationUnit(CompilationUnit unit)
   {
      assert unit != null;
      return m_CompilationUnits.contains(unit);
   }

   public final boolean hasViewableSourceFile()
   {
      return false;
   }

   public final File getAbsoluteSourcePath()
   {
      assert hasViewableSourceFile();
      return null;
   }
}