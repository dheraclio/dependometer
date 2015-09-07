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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class LogicalElementNode extends Node
{
   private final String m_Name;

   private String m_Description;

   private final static Map<String, LogicalElementNode> s_NameToNode = new HashMap<String, LogicalElementNode>();

   private final List<String> m_DependsUpon = new ArrayList<String>();

   public static boolean existsLogicalElementNode(String name)
   {
      assert name != null;
      assert name.length() > 0;
      return s_NameToNode.containsKey(name);
   }

   public static LogicalElementNode getLogicalElementNode(String name)
   {
      assert name != null;
      assert name.length() > 0;
      assert s_NameToNode.containsKey(name);
      return s_NameToNode.get(name);
   }

   protected LogicalElementNode(String name)
   {
      assert name != null;
      assert name.length() > 0;

      if (s_NameToNode.containsKey(name))
      {
         throw new IllegalArgumentException("Duplicate logical element added - "
            + Node.getLocationInfoProvider().getInfo());
      }
      else
      {
         s_NameToNode.put(name, this);
      }

      m_Name = name;
   }

   public final String getName()
   {
      return m_Name;
   }

   public final void processDependsUponNode(String name)
   {
      assert name != null;
      assert name.length() > 0;

      if (m_Name.equals(name))
      {
         throw new IllegalArgumentException("Illegal depends upon argument (self reference) - "
            + Node.getLocationInfoProvider().getInfo());
      }

      if (!m_DependsUpon.add(name))
      {
         throw new IllegalArgumentException("Duplicate added - " + Node.getLocationInfoProvider().getInfo());
      }

      getLogger().debug("Depends upon - " + name);
   }

   public final String[] dependsUpon()
   {
      return m_DependsUpon.toArray(new String[0]);
   }

   public final void setDescription(String description)
   {
      assert description != null;
      description = description.trim();
      if (m_Description == null)
      {
         m_Description = description;
      }
      else
      {
         m_Description = m_Description + description;
      }
   }

   public final String getDescription()
   {
      return m_Description;
   }

   public String toString()
   {
      StringBuffer buffer = new StringBuffer(getClass().getName());

      buffer.append(" # ");
      buffer.append(m_Name);
      if (m_Description != null)
      {
         buffer.append(m_Description);
      }
      return buffer.toString();
   }
   
   public static void reset()
   {
      s_NameToNode.clear();
   }
}
