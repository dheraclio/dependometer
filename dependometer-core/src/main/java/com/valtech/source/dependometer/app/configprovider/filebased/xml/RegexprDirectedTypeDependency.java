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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.valtech.source.dependometer.app.core.provider.FullyQualifiedDirectedTypeDependencyIf;
import com.valtech.source.dependometer.app.core.provider.RegexprDirectedTypeDependencyIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class RegexprDirectedTypeDependency implements RegexprDirectedTypeDependencyIf
{
   private int m_HashCode;

   private Pattern m_From;

   private Pattern m_To;

   private Set<FullyQualifiedDirectedTypeDependency> m_MatchedDependencies = new HashSet<FullyQualifiedDirectedTypeDependency>();

   public RegexprDirectedTypeDependency(String from, String to)
   {
      assert from != null;
      assert from.length() > 0;
      assert to != null;
      assert to.length() > 0;

      m_From = Pattern.compile(from);
      m_To = Pattern.compile(to);
      m_HashCode = (from + to).hashCode();
   }

   public String getFrom()
   {
      return m_From.pattern();
   }

   public String getTo()
   {
      return m_To.pattern();
   }

   public boolean match(String from, String to)
   {
      assert from != null;
      assert from.length() > 0;
      assert to != null;
      assert to.length() > 0;
      assert !from.equals(to);

      boolean matches = m_From.matcher(from).matches() && m_To.matcher(to).matches();

      if (matches)
      {
         m_MatchedDependencies.add(new FullyQualifiedDirectedTypeDependency(from, to));
      }

      return matches;
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
      else if (obj instanceof RegexprDirectedTypeDependency)
      {
         return (m_From.equals(((RegexprDirectedTypeDependency)obj).m_From) && m_To
            .equals(((RegexprDirectedTypeDependency)obj).m_To));
      }
      else
      {
         return false;
      }
   }

   public FullyQualifiedDirectedTypeDependencyIf[] getMatchedDependencies()
   {
      return m_MatchedDependencies.toArray(new FullyQualifiedDirectedTypeDependency[0]);
   }

   public int hashCode()
   {
      return m_HashCode;
   }
}