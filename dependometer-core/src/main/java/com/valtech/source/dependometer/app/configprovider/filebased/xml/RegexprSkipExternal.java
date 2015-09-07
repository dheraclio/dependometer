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

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.valtech.source.dependometer.app.core.provider.SkipExternalIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class RegexprSkipExternal implements SkipExternalIf
{
   private final String m_SkipPattern;

   private final Pattern m_SkipPatternCompiled;

   private final Set<String> m_SkippedTypes = new TreeSet<String>();

   public RegexprSkipExternal(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;
      m_SkipPattern = pattern;
      m_SkipPatternCompiled = Pattern.compile(pattern);
   }

   public boolean match(String fullyQualifiedTypeName)
   {
      assert fullyQualifiedTypeName != null;
      assert fullyQualifiedTypeName.length() > 0;
      if (m_SkipPatternCompiled.matcher(fullyQualifiedTypeName).matches())
      {
         m_SkippedTypes.add(fullyQualifiedTypeName);
         return true;
      }
      else
      {
         return false;
      }
   }

   public String[] getSkippedTypes()
   {
      return m_SkippedTypes.toArray(new String[0]);
   }

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
      else if (obj instanceof RegexprSkipExternal)
      {
         return (m_SkipPattern.equals(((RegexprSkipExternal)obj).m_SkipPattern));
      }
      else
      {
         return false;
      }
   }

   public int hashCode()
   {
      return m_SkipPattern.hashCode();
   }

   public String getSkipPattern()
   {
      return m_SkipPattern;
   }
}