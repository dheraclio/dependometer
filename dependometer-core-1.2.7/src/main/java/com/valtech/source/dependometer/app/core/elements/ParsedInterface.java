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

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class ParsedInterface extends ParsedType
{
   public ParsedInterface(String fullyQualifiedTypeName, String compilationUnitName, String[] superClassNames,
      String[] superInterfaces, String[] importedTypes, boolean isExtendable, Object source, File absoluteSourcePath)
   {
      super(fullyQualifiedTypeName, compilationUnitName, superClassNames, superInterfaces, importedTypes, isExtendable,
         source, absoluteSourcePath);
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

      String[] superInterfaces = getSuperInterfaceNames();
      for (int i = 0; i < superInterfaces.length; ++i)
      {
         if (fqname.equals(superInterfaces[i]))
         {
            return EXTENDS;
         }
      }

      return USES;
   }

   public boolean isConcrete()
   {
      return false;
   }

   public boolean isAccessible()
   {
      return true;
   }

   public boolean isInterface()
   {
      return true;
   }

   boolean mayUseAssertions()
   {
      return false;
   }

   protected int getNumberOfAssertions()
   {
      return 0;
   }
}