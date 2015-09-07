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

import com.valtech.source.dependometer.app.core.provider.FullyQualifiedDirectedTypeDependencyIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class FullyQualifiedDirectedTypeDependency implements FullyQualifiedDirectedTypeDependencyIf
{
   private final int m_HashCode;

   private final String m_From;

   private final String m_To;

   public FullyQualifiedDirectedTypeDependency(String from, String to)
   {
      assert from != null;
      assert from.length() > 0;
      assert to != null;
      assert to.length() > 0;
      assert !from.equals(to);

      m_From = from;
      m_To = to;
      m_HashCode = (from + to).hashCode();
   }

   public String getFrom()
   {
      return m_From;
   }

   public String getTo()
   {
      return m_To;
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
      else if (obj instanceof FullyQualifiedDirectedTypeDependency)
      {
         return (m_From.equals(((FullyQualifiedDirectedTypeDependency)obj).m_From) && m_To
            .equals(((FullyQualifiedDirectedTypeDependency)obj).m_To));
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

   public String toString()
   {
      StringBuffer buffer = new StringBuffer();

      buffer.append(getFrom());
      buffer.append(" -> ");
      buffer.append(getTo());

      return buffer.toString();
   }
}
