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
package com.valtech.source.dependometer.app.configprovider.filebased.xml;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.valtech.source.dependometer.app.core.provider.CompilationUnitFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class RegexprCompilationUnitFilter implements CompilationUnitFilterIf
{
   private List<String> m_FilterInformation = new ArrayList<String>();

   private Set<String> m_Patterns = new HashSet<String>();

   private Set<String> m_ExcludedCompilationUnits = new HashSet<String>();

   private List<Pattern> m_CompiledPatterns = new ArrayList<Pattern>();

   public boolean patternAlreadyAdded(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;
      return m_Patterns.contains(pattern);
   }

   public void addExcludePattern(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;
      assert !patternAlreadyAdded(pattern);

      m_Patterns.add(pattern);
      m_FilterInformation.add(pattern);
      m_CompiledPatterns.add(Pattern.compile(pattern));
   }

   public String[] getFilterInformation()
   {
      return m_FilterInformation.toArray(new String[0]);
   }

   public boolean exclude(String name, String relPackagePath)
   {
      assert name != null;
      assert name.length() > 0;
      assert relPackagePath != null;

      if (relPackagePath.length() > 0)
      {
         relPackagePath = relPackagePath + ".";
      }

      for (int i = 0; i < m_CompiledPatterns.size(); ++i)
      {
         Pattern nextPattern = m_CompiledPatterns.get(i);
         if (nextPattern.matcher(name).matches())
         {
            m_ExcludedCompilationUnits.add(relPackagePath + name);
            return true;
         }
      }

      return false;
   }

   public String[] getExcludedCompilationUnits()
   {
      return m_ExcludedCompilationUnits.toArray(new String[0]);
   }
}