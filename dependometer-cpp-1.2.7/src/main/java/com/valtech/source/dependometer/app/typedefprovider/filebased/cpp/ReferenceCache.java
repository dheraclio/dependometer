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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Efficient store for the relation (referrer, referenced type)
 * 
 * @author Bernhard Rümenapp
 */
public class ReferenceCache
{
   private Map<String, ReferenceCacheEntry> map = new HashMap<String, ReferenceCacheEntry>();

   /** A limited number of reference sets is allowed to stay expanded. */
   private ReferenceCacheEntry[] m_cache = new ReferenceCacheEntry[128]; // [64];

   /** next position in the cache. */
   private int m_pos = 0;

   /**
    * Add a reference.
    * 
    * @param key referrer
    * @param value referenced
    */
   void addRef(String key, String value)
   {
      ReferenceCacheEntry e = map.get(key);
      if (e == null)
      {
         e = new ReferenceCacheEntry(value);
         map.put(key, e);
         place(e);
      }
      else
      {
         /*
          * if (e.m_collapsed != null) { s_Logger.fatal("Cache miss: " + key + " size " + e.size); }
          */

         if (e.addRefIsExpanded(value))
         {
            place(e);
         }
      }
   }

   /**
    * Uncache.
    * 
    * @param key full type name of referrer.
    * @return references
    */
   Set<String> getRefs(String key)
   {
      ReferenceCacheEntry e = map.get(key);
      if (e != null)
      {
         place(e);
         return e.expand();
      }
      else
      {
         return null;
      }

   }

   /**
    * Put the entry in the LRU cache.
    * 
    * @param e non-collapsed cache entry
    */
   void place(ReferenceCacheEntry e)
   {
      // This is only a lacy approximation to what could be an LRU algorithm.
      if (m_cache[e.m_lastPos] == e)
      {
         // entry is already in,
         // just swap positions
         m_cache[e.m_lastPos] = m_cache[m_pos];
         if (m_cache[m_pos] != null)
         {
            m_cache[m_pos].m_lastPos = e.m_lastPos;
         }
      }
      else if (m_cache[m_pos] != null)
      {
         // poor victim falls out the cache.
         m_cache[m_pos].collapse();
      }

      // entry placed here
      e.m_lastPos = m_pos;
      m_cache[m_pos] = e;

      // increment the logical clock
      m_pos = (m_pos + 1) % m_cache.length;
   }
}
