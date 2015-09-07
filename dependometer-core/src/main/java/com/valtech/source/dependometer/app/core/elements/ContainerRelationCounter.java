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

import com.valtech.source.ag.util.FlexInteger;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class ContainerRelationCounter implements Comparable
{
   private final FlexInteger m_Rels = new FlexInteger();

   private final String m_ElementName;

   private final int m_HashCode;

   ContainerRelationCounter(String elementName)
   {
      assert elementName != null;
      assert elementName.length() > 0;
      m_ElementName = elementName;
      m_HashCode = m_ElementName.hashCode();
   }

   String getElementName()
   {
      return m_ElementName;
   }

   void increment()
   {
      m_Rels.increment();
   }

   int getValue()
   {
      return m_Rels.getValue();
   }

   public int compareTo(Object obj)
   {
      assert obj != null;
      assert obj instanceof ContainerRelationCounter;
      if (equals(obj))
      {
         return 0;
      }

      ContainerRelationCounter compare = (ContainerRelationCounter)obj;
      int result = compare.m_Rels.getValue() - m_Rels.getValue();
      if (result == 0)
      {
         result = 1;
      }
      return result;
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
      else if (obj instanceof ContainerRelationCounter)
      {
         if (m_ElementName.equals(((ContainerRelationCounter)obj).m_ElementName))
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

   public int hashCode()
   {
      return m_HashCode;
   }
}
