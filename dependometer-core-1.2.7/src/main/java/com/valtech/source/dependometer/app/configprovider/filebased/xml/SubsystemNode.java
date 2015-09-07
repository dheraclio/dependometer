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
import java.util.List;

import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public class SubsystemNode extends LogicalElementNode
{
   private final static List<String> subsystemNames = new ArrayList<String>();

   private final RegexprPackageFilter m_PackageFilter = new RegexprPackageFilter();

   public static String[] getSubsystemNames()
   {
      return subsystemNames.toArray(new String[0]);
   }

   public SubsystemNode(String name)
   {
      super(name);
      subsystemNames.add(name);
   }

   public void processIncludePackageNode(String pattern)
   {
      if (m_PackageFilter.patternAlreadyAdded(pattern))
      {
         throw new IllegalArgumentException("Duplicate include package entry '" + pattern + "' - "
            + Node.getLocationInfoProvider().getInfo());
      }

      m_PackageFilter.include(pattern);
      getLogger().debug("Include package pattern for subsystem added - " + pattern);
   }

   final void processExcludePackageNode(String pattern)
   {
      assert pattern != null;
      assert pattern.length() > 0;

      if (m_PackageFilter.patternAlreadyAdded(pattern))
      {
         throw new IllegalArgumentException("Duplicate exclude package entry '" + pattern + "' - "
            + Node.getLocationInfoProvider().getInfo());
      }

      m_PackageFilter.exclude(pattern);
      getLogger().debug("Exclude package pattern for project added - " + pattern);
   }

   public PackageFilterIf getPackageFilter()
   {
      return m_PackageFilter;
   }
   
   public static void reset()
   {
      subsystemNames.clear();
   }
}