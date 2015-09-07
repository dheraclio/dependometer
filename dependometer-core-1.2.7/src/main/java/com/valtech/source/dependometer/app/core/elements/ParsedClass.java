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

import java.io.File;

import com.valtech.source.dependometer.app.core.metrics.MetricsProvider;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class ParsedClass extends ParsedType
{
   private static int s_NumberOfParsedClasses;

   private static int s_TotalNumberOfAssertions = INITIAL_NUMBER_OF_ASSERTIONS;

   private final int m_NumberOfAssertions;

   private final boolean m_IsAbstract;

   private final boolean m_IsPublic;

   public ParsedClass(String fullyQualifiedTypeName, String compilationUnitName, boolean isAbstract, boolean isPublic,
      boolean isExtendable, String[] superClassNames, String[] interfaceNames, String[] importedTypes,
      int numberOfAssertions, Object source, File absoluteSourcePath)
   {
      super(fullyQualifiedTypeName, compilationUnitName, superClassNames, interfaceNames, importedTypes, isExtendable,
         source, absoluteSourcePath);

      m_IsAbstract = isAbstract;
      m_IsPublic = isPublic;
      m_NumberOfAssertions = numberOfAssertions;
      s_TotalNumberOfAssertions = MetricsProvider.cumulateNumberOfAssertions(s_TotalNumberOfAssertions,
         m_NumberOfAssertions);
      ++s_NumberOfParsedClasses;
   }

   static int getTotalNumberOfAssertions()
   {
      return s_TotalNumberOfAssertions;
   }

   static int getNumberOfParsedClasses()
   {
      return s_NumberOfParsedClasses;
   }

   public boolean isConcrete()
   {
      return !m_IsAbstract;
   }

   public boolean isAccessible()
   {
      return m_IsPublic;
   }

   public String getRelationQualifer(DependencyElement relationTo)
   {
      assert relationTo != null;

      String fqname = relationTo.getFullyQualifiedName();
      String[] superClassNames = getSuperClassNames();
      for (int i = 0; i < superClassNames.length; i++)
      {
         String nextSuperClassName = superClassNames[i];
         if (fqname.equals(nextSuperClassName))
         {
            return EXTENDS;
         }
      }

      String[] superInterfaceNames = getSuperInterfaceNames();
      for (int i = 0; i < superInterfaceNames.length; ++i)
      {
         if (fqname.equals(superInterfaceNames[i]))
         {
            return IMPLEMENTS;
         }
      }

      return USES;
   }

   public boolean isInterface()
   {
      return false;
   }

   boolean mayUseAssertions()
   {
      return true;
   }

   protected int getNumberOfAssertions()
   {
      assert mayUseAssertions();
      return m_NumberOfAssertions;
   }

   public static void reset()
   {
      s_NumberOfParsedClasses = 0;
      s_TotalNumberOfAssertions = INITIAL_NUMBER_OF_ASSERTIONS;
   }
}