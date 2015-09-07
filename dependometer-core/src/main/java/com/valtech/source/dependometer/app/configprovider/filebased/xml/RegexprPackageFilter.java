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

import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class RegexprPackageFilter implements PackageFilterIf
{
   protected final static String LINE_SEPARATOR = System.getProperty("line.separator");

   private final Set<String> m_IncludePatterns = new HashSet<String>();

   private final Set<String> m_ExcludePatterns = new HashSet<String>();

   private final Set<String> m_SkippedPackages = new TreeSet<String>();

   private final List<Pattern> m_CompiledIncludePatterns = new ArrayList<Pattern>();

   private final List<Pattern> m_CompiledExcludePatterns = new ArrayList<Pattern>();

   public boolean patternAlreadyAdded(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;
      return m_IncludePatterns.contains(pattern) || m_ExcludePatterns.contains(pattern);
   }

   public void include(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;
      assert !patternAlreadyAdded(pattern);
      m_IncludePatterns.add(pattern);
      m_CompiledIncludePatterns.add(Pattern.compile(pattern));
   }

   public String[] getIncludePatterns()
   {
      return m_IncludePatterns.toArray(new String[0]);
   }

   public boolean match(String fqname)
   {
      assert fqname != null;
      assert fqname.length() > 0;
      boolean included = false;

      for (int i = 0; i < m_CompiledIncludePatterns.size(); ++i)
      {
         Pattern nextPattern = m_CompiledIncludePatterns.get(i);
         if (nextPattern.matcher(fqname).matches())
         {
            included = true;
            break;
         }
      }

      if (included)
      {
         for (int i = 0; i < m_CompiledExcludePatterns.size(); ++i)
         {
            Pattern nextPattern = m_CompiledExcludePatterns.get(i);
            if (nextPattern.matcher(fqname).matches())
            {
               included = false;
               break;
            }
         }
      }

      if (!included)
      {
         m_SkippedPackages.add(fqname);
      }

      return included;
   }

   public String[] getSkippedPackages()
   {
      return m_SkippedPackages.toArray(new String[0]);
   }

   public boolean includes(PackageFilterIf filter)
   {
      // TODO find a complete solution - maybe an external regex library is able to detect in/exclusion of regular
      // expressions
      // TODO check exclude patterns
      assert filter != null;
      assert filter != this;

      String[] filterInfo = filter.getIncludePatterns();
      String[] filterInfoThis = getIncludePatterns();

      for (int i = 0; i < filterInfo.length; i++)
      {
         String nextFilterInfo = filterInfo[i];
         for (int j = 0; j < filterInfoThis.length; j++)
         {
            String nextFilterInfoThis = filterInfoThis[j];
            if (nextFilterInfoThis.endsWith(".*"))
            {
               nextFilterInfoThis = nextFilterInfoThis.substring(0, nextFilterInfoThis.length() - 1);
            }
            if (nextFilterInfo.startsWith(nextFilterInfoThis))
            {
               return true;
            }
         }
      }

      return false;
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

   public String toString()
   {
      StringBuffer buffer = new StringBuffer();
      String[] includes = m_IncludePatterns.toArray(new String[0]);
      for (int i = 0; i < includes.length; i++)
      {
         buffer.append("include pattern = ");
         buffer.append(includes[i]);
         buffer.append(LINE_SEPARATOR);
      }
      return buffer.toString();
   }
}