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
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.valtech.source.dependometer.app.core.provider.SubsystemFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class RegexprSubsystemFilter implements SubsystemFilterIf
{
   private final Set<String> m_ExcludePatterns = new HashSet<String>();

   private final Set<String> m_SkippedSubsystems = new TreeSet<String>();

   private final List<Pattern> m_CompiledExcludePatterns = new ArrayList<Pattern>();

   public boolean patternAlreadyAdded(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;
      return m_ExcludePatterns.contains(pattern);
   }

   public boolean match(String fqname)
   {
      assert fqname != null;
      boolean included = true;

      for (int i = 0; i < m_CompiledExcludePatterns.size(); ++i)
      {
         Pattern nextPattern = m_CompiledExcludePatterns.get(i);
         if (nextPattern.matcher(fqname).matches())
         {
            included = false;
            break;
         }
      }

      if (!included)
      {
         m_SkippedSubsystems.add(fqname);
      }

      return included;
   }

   public String[] getSkippedSubsystems()
   {
      return m_SkippedSubsystems.toArray(new String[0]);
   }

   public String[] getExcludePatterns()
   {
      return m_ExcludePatterns.toArray(new String[0]);
   }

   public void exclude(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;
      assert !patternAlreadyAdded(pattern);
      m_ExcludePatterns.add(pattern);
      m_CompiledExcludePatterns.add(Pattern.compile(pattern));
   }
}