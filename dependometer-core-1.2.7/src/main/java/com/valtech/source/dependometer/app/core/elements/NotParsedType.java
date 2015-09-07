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
import java.util.ArrayList;
import java.util.List;

import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.IdentifierUtility;
import com.valtech.source.dependometer.app.core.provider.MetricsProviderIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class NotParsedType extends Type
{
   private static final String NOT_ANALYZED_UNIT = "not analyzed";

   private static final String[] EMPTY_STRING_ARRAY = new String[0];

   private static final List<NotParsedType> s_NotParsedTypes = new ArrayList<NotParsedType>();

   private static final Type[] s_Efferents = new Type[0];

   public static NotParsedType[] getNotParsedTypes()
   {
      return s_NotParsedTypes.toArray(new NotParsedType[0]);
   }

   NotParsedType(String fullyQualifiedName)
   {

      super(fullyQualifiedName, EMPTY_STRING_ARRAY, EMPTY_STRING_ARRAY, createCompilationUnitName(fullyQualifiedName),
         MetricsProviderIf.NO_SOURCE);
      assert !s_NotParsedTypes.contains(this);
      s_NotParsedTypes.add(this);
   }

   private static String createCompilationUnitName(String fullyQualifiedName)
   {
      String compName = NOT_ANALYZED_UNIT + " (";
      compName += IdentifierUtility.getCompilationUnitName(fullyQualifiedName);
      return compName+")";
   }

   static int getNumberOfNotParsedTypes()
   {
      return s_NotParsedTypes.size();
   }

   public boolean belongsToProject()
   {
      return false;
   }

   public boolean isAccessible()
   {
      return true;
   }

   public int getNumberOfEfferents()
   {
      assert belongsToProject();
      return 0;
   }

   public DependencyElementIf[] getEfferents()
   {
      assert belongsToProject();
      return s_Efferents;
   }

   public DependencyElement[] getProjectInternalEfferentElements()
   {
      assert belongsToProject();
      return null;
   }

   public boolean isConcrete()
   {
      assert belongsToProject();
      return false;
   }

   public String getRelationQualifer(DependencyElement relationTo)
   {
      assert belongsToProject();
      return null;
   }

   public boolean isEfferent(DependencyElementIf dependencyElement)
   {
      assert belongsToProject();
      return false;
   }

   public int getNumberOfTypeRelationsForEfferent(DependencyElementIf dependencyElement)
   {
      assert belongsToProject();
      return 0;
   }

   int getNumberOfProjectInternalEfferents()
   {
      assert belongsToProject();
      return 0;
   }

   public boolean hasViewableSourceFile()
   {
      assert belongsToProject();
      return false;
   }

   public File getAbsoluteSourcePath()
   {
      assert belongsToProject();
      return null;
   }

   boolean mayUseAssertions()
   {
      assert belongsToProject();
      return false;
   }

   public boolean isInterface()
   {
      assert belongsToProject();
      return false;
   }

   protected int getNumberOfAssertions()
   {
      assert belongsToProject();
      return 0;
   }

   public static void reset()
   {
      s_NotParsedTypes.clear();
   }
}