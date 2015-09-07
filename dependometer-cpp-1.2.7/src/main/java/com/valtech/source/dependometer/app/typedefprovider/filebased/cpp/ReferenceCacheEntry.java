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
package com.valtech.source.dependometer.app.typedefprovider.filebased.cpp;

import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Hold a set of strings. Most C++ Types have 1 or few references. In order to save space, turn the set of refences into
 * a concatenated string.
 * 
 * @author Bernhard Rümenapp
 */
public class ReferenceCacheEntry
{
   final static private String s_separator = "\n";

   /** LRU cache helper */
   int m_lastPos = 0;

   /** waste full container */
   Set<String> m_references = new HashSet<String>(1);

   /** concatenated string */
   String m_collapsed = null;

   /** number of items when collapsed */
   int m_size = 1;

   public ReferenceCacheEntry(String value)
   {
      m_references.add(value);
   }

   Set<String> expand()
   {
      if (m_references == null)
      {
         StringTokenizer st = new StringTokenizer(m_collapsed, s_separator);
         m_references = new HashSet<String>(st.countTokens());

         while (st.hasMoreTokens())
         {
            m_references.add(st.nextToken().intern());
         }
         m_collapsed = null;
      }
      return m_references;
   }

   /** Save spave. */
   void collapse()
   {
      if (m_references != null)
      {
         m_size = m_references.size();
         StringBuilder sb = new StringBuilder();
         for (String ref : m_references)
         {
            sb.append(ref);
            sb.append(s_separator);
         }
         m_collapsed = sb.toString();
         m_references = null;
      }
   }

   /**
    * Add a reference.
    * 
    * @param ref full type name referenced
    * @return can be collapsed later
    */
   boolean addRefIsExpanded(String ref)
   {
      if (m_references != null)
      {
         m_references.add(ref.intern());
         return true;
      }
      else
      {
         assert m_collapsed != null;

         if (m_size > 7)
         {
            expand().add(ref.intern());
            return true;
         }

         StringTokenizer st = new StringTokenizer(m_collapsed, s_separator);

         while (st.hasMoreTokens())
         {
            if (ref.equals(st.nextToken()))
            {
               return false;
            }
         }

         /*
          * sub-optimal first idea: m_collapsed = new
          * StringBuilder(m_collapsed).append(ref).append(s_separator).toString(); m_size++;
          * 
          * return false;
          */
         expand().add(ref.intern());
         return true;
      }
   }
}
