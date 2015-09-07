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
package com.valtech.source.dependometer.app.core.fts14;

import com.valtech.source.dependometer.app.core.dependencyanalysis.NodeIf;

/**
 * Edge key. Compare edges by node pair values.
 */
class FTSDependencyKey
{
   private final NodeIf m_From;

   private final NodeIf m_To;

   FTSDependencyKey(NodeIf from, NodeIf to)
   {
      m_From = from;
      m_To = to;
   }

   public boolean equals(Object compareTo)
   {
      if (compareTo == null)
      {
         return false;
      }
      else if (compareTo == this)
      {
         return true;
      }
      else if (!(compareTo instanceof FTSDependencyKey))
      {
         return false;
      }
      else if (((FTSDependencyKey)compareTo).m_From == m_From && ((FTSDependencyKey)compareTo).m_To == m_To)
      {
         return true;
      }
      else
      {
         return false;
      }
   }

   public int hashCode()
   {
      return m_From.hashCode() + m_To.hashCode();
   }
}
