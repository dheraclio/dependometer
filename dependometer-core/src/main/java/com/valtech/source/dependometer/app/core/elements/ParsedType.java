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
import java.util.Arrays;
import java.util.List;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class ParsedType extends Type
{
   private static List<ParsedType> s_ParsedTypes = new ArrayList<ParsedType>();

   private final File absoluteSourcePath;

   private final String[] importedTypes;

   private final boolean isExtendable;

   private Type[] efferents;

   private List<Type> parsedEfferents = new ArrayList<Type>();

   private ParsedType[] parsedEfferentsAsArray;

   ParsedType(String fullyQualifiedTypeName, String compilationUnitName, String[] superClassNames,
      String[] superInterfaceNames, String[] importedTypes, boolean isExtendable, Object source, File absoluteSourcePath)
   {
      super(fullyQualifiedTypeName, superClassNames, superInterfaceNames, compilationUnitName, source);
      assert AssertionUtility.checkArray(importedTypes);
      assert !s_ParsedTypes.contains(this);

      this.absoluteSourcePath = absoluteSourcePath;
      this.importedTypes = Arrays.copyOf( importedTypes, importedTypes.length );
      this.isExtendable = isExtendable;
      s_ParsedTypes.add(this);
   }

   public static ParsedType[] getParsedTypes()
   {
      return s_ParsedTypes.toArray(new ParsedType[0]);
   }

   public static int getNumberOfParsedTypes()
   {
      return s_ParsedTypes.size();
   }

   public final DependencyElementIf[] getEfferents()
   {
      return efferents;
   }

   public final DependencyElement[] getProjectInternalEfferentElements()
   {
      if ( parsedEfferentsAsArray == null)
      {
         parsedEfferentsAsArray = parsedEfferents.toArray(new ParsedType[0]);
      }

      return parsedEfferentsAsArray;
   }

   final public int getNumberOfEfferents()
   {
      return efferents.length;
   }

   public void collectMetrics()
   {
      super.collectMetrics();

      if (belongsToProject())
      {
         addMetric(MetricEnum.EXTENDABLE, String.valueOf( isExtendable ));
      }
   }

   public final void resolveTypes()
   {
      int numberOfImportedTypes = importedTypes.length;
      efferents = new Type[numberOfImportedTypes];

      for (int i = 0; i < numberOfImportedTypes; ++i)
      {
         String importedType = importedTypes[i];
         Type type = Type.getType(importedType);
         if (type != null)
         {
            efferents[i] = type;
            if (type.belongsToProject())
            {
               parsedEfferents.add( type );
               parsedEfferentsAsArray = null;
            }
         }
         else
         {
            type = new NotParsedType(importedType);
            efferents[i] = type;
         }

         type.addAfferent(this);
      }
   }

   final public void prepareCollectionOfMetrics()
   {
      super.prepareCollectionOfMetrics();

      String[] superClassNames = getSuperClassNames();
      for (int i = 0; i < superClassNames.length; i++)
      {
         String superTypeName = superClassNames[i];
         Type superType = Type.getType(superTypeName);
         if (superType != null) // May be a skipped supertype
         {
            superType.incrementNumberOfDirectSubtypes();
         }
      }

      String[] superInterfaceNames = getSuperInterfaceNames();
      for (int i = 0; i < superInterfaceNames.length; i++)
      {
         String superTypeName = superInterfaceNames[i];
         Type superType = Type.getType(superTypeName);
         if (superType != null) // May be a skipped supertype
         {
            superType.incrementNumberOfDirectSubtypes();
         }
      }
   }

   public final boolean isEfferent(DependencyElementIf dependencyElement)
   {
      assert dependencyElement != null;
      assert efferents != null;

      for (int i = 0; i < efferents.length; ++i)
      {
         if ( efferents[i].equals(dependencyElement))
         {
            return true;
         }
      }

      return false;
   }

   public int getNumberOfTypeRelationsForEfferent(DependencyElementIf dependencyElement)
   {
      assert isEfferent(dependencyElement);
      return 1;
   }

   final int getNumberOfProjectInternalEfferents()
   {
      return parsedEfferents.size();
   }

   public final boolean belongsToProject()
   {
      return true;
   }

   public final boolean hasViewableSourceFile()
   {
      return absoluteSourcePath != null;
   }

   public final File getAbsoluteSourcePath()
   {
      assert hasViewableSourceFile();
      return absoluteSourcePath;
   }

   public static void reset()
   {
      s_ParsedTypes.clear();
   }

   public String getInfo()
   {
      String s = super.getInfo();
      s += "\n\tEfferent dependencies: ";
      for (Type t : efferents )
      {
         s += "\t" + t.getFullyQualifiedContainmentName();
      }

      return s;
   }
}