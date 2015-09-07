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

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.valtech.source.dependometer.app.core.provider.MetricsProviderIf;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;

/**
 * Provides architectural meta information about a declared C++ type representing either a class or an interface
 * 
 * @author Carsten.Kaiser
 * @version 2.0
 */
public class TypeDefinition implements TypeDefinitionIf
{
   /**
    * Class type definition
    */
   protected static final int CLASS_KIND = 3;

   /**
    * Empty string array
    */
   private static final String[] EMPTY_STRING_ARRAY = new String[] {};

   /**
    * Struct type definition
    */
   protected static final int STRUCT_KIND = 1;

   /**
    * Union type definition
    */
   protected static final int UNION_KIND = 2;

   /**
    * Absolute path to the source or header file containing the type declaration
    */
   private File m_absoluteSourcePath = null;

   /**
    * Indicator whether the represented type is abstract
    */
   private boolean m_abstract = false;

   /**
    * Indicator whether the referenced type is common accessible
    */
   private boolean m_accessible = false;

   /**
    * Indicator whether the represented type is extendable
    */
   private boolean m_extendable = false;

   /**
    * Fully qualified name of this type definition
    */
   private String m_fullyQualifiedName = null;

   /**
    * List of superclass names associated to this type definition
    */
   private List<String> m_fullyQualifiedSuperclassNames = new ArrayList<String>();

   /**
    * Set of type reference names supposed to be ignored during analysis
    */
   private Set<String> m_ignoredReferencedTypes = new HashSet<String>();

   /**
    * Kind of type definition
    */
   private int m_kind = -1;

   /**
    * Indicator whether the represented type is nested within another
    */
   private boolean m_nested = false;

   /**
    * Set of type reference names supposed to be considered during analysis
    */
   private Set<String> m_referencedTypes = new HashSet<String>();

   /**
    * Relative path to the compilation unit defining or implementing this type definition
    */
   private String m_relativeCompilationUnitPath = null;

   /**
    * Set of type reference names supposed to be skipped during analysis
    */
   private Set<String> m_skippedReferencedTypes = new HashSet<String>();

   /**
    * Name of the source file containing the type definition respectively implementation
    */
   private String m_source = null;

   private int m_NumberOfAssertions = MetricsProviderIf.INITIAL_NUMBER_OF_ASSERTIONS;

   /**
    * Creates a type definition for a type of the given kind with the passed fully qualified name
    * 
    * @param fullyQualifiedName Fully qualified name
    * @param kind Kind of type
    */
   public TypeDefinition(String fullyQualifiedName, int kind)
   {
      assert fullyQualifiedName != null;
      assert fullyQualifiedName.trim().length() > 0;
      assert kind > 0 && kind < 4;

      m_fullyQualifiedName = fullyQualifiedName;
      m_kind = kind;
   }

   /**
    * Adds the given superclass name to this type definition
    * 
    * @param fullyQualifiedName Superclass name
    */
   protected void addFullyQualifiedSuperclassName(String fullyQualifiedName)
   {
      assert fullyQualifiedName != null;

      if (!m_fullyQualifiedSuperclassNames.contains(fullyQualifiedName))
         m_fullyQualifiedSuperclassNames.add(fullyQualifiedName);
   }

   /**
    * Adds the given type name to the set of type references supposed to be ignored during analysis
    * 
    * @param fullyQualifiedName Name of the referenced type
    */
   protected void addIgnoredReferencedType(String fullyQualifiedName)
   {
      assert fullyQualifiedName != null;

      m_ignoredReferencedTypes.add(fullyQualifiedName);
   }

   /**
    * Adds the given type name to the set of type references supposed to be considered during analysis
    * 
    * @param fullyQualifiedName Name of the referenced type
    */
   protected void addReferencedType(String fullyQualifiedName)
   {
      assert fullyQualifiedName != null;

      m_referencedTypes.add(fullyQualifiedName);
   }

   /**
    * Adds the given type name to the set of type references supposed to be skipped during analysis
    * 
    * @param fullyQualifiedName Name of the referenced type
    */
   protected void addSkippedReferencedType(String fullyQualifiedName)
   {
      assert fullyQualifiedName != null;
      assert fullyQualifiedName.length() > 0;

      m_skippedReferencedTypes.add(fullyQualifiedName);
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getAbsoluteSourcePath()
    */
   public File getAbsoluteSourcePath()
   {
      return m_absoluteSourcePath;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getFullyQualifiedImportedTypeNames()
    */
   public String[] getFullyQualifiedImportedTypeNames()
   {
      if (m_referencedTypes.isEmpty())
         return EMPTY_STRING_ARRAY;
      else
         return m_referencedTypes.toArray(new String[m_referencedTypes.size()]);
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getFullyQualifiedSuperClassNames()
    */
   public String[] getFullyQualifiedSuperClassNames()
   {
      if (m_referencedTypes.isEmpty())
         return EMPTY_STRING_ARRAY;
      else
         return m_fullyQualifiedSuperclassNames.toArray(new String[m_fullyQualifiedSuperclassNames.size()]);
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getFullyQualifiedSuperInterfaceNames()
    */
   public String[] getFullyQualifiedSuperInterfaceNames()
   {
      // Interfaces are not supported in C++
      return EMPTY_STRING_ARRAY;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getFullyQualifiedTypeName()
    */
   public String getFullyQualifiedTypeName()
   {
      return m_fullyQualifiedName;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getIgnoredFullyQualifiedImportedTypeNames()
    */
   public String[] getIgnoredFullyQualifiedImportedTypeNames()
   {
      if (m_ignoredReferencedTypes.isEmpty())
         return EMPTY_STRING_ARRAY;
      else
         return m_ignoredReferencedTypes.toArray(new String[m_referencedTypes.size()]);
   }

   void setNumberOfAssertions(int number)
   {
      assert number >= -1;
      m_NumberOfAssertions = number;

   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getNumberOfAssertions()
    */
   public int getNumberOfAssertions()
   {
      // Not supported in C++
      // return 0;
      // m_NumberOfAssertions = -1;
      return m_NumberOfAssertions;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getRelativeCompilationUnitPath()
    */
   public String getRelativeCompilationUnitPath()
   {
      return m_relativeCompilationUnitPath;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getSkippedFullyQualifiedImportedTypeNames()
    */
   public String[] getSkippedFullyQualifiedImportedTypeNames()
   {
      if (m_skippedReferencedTypes.isEmpty())
         return EMPTY_STRING_ARRAY;
      else
         return m_skippedReferencedTypes.toArray(new String[m_skippedReferencedTypes.size()]);
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getSource()
    */
   public Object getSource()
   {
      return m_source;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#getUnrefactoredFullyQualifiedTypeName()
    */
   public String getUnrefactoredFullyQualifiedTypeName()
   {
      return m_fullyQualifiedName;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#isAbstract()
    */
   public boolean isAbstract()
   {
      return m_abstract;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#isAccessible()
    */
   public boolean isAccessible()
   {
      return m_accessible;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#isClass()
    */
   public boolean isClass()
   {
      return m_kind == CLASS_KIND;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#isExtendable()
    */
   public boolean isExtendable()
   {
      return m_extendable;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#isInterface()
    */
   public boolean isInterface()
   {
      // Interfaces not supported in C++
      return false;
   }

   /**
    * Checks whether the represented type is nested within another type
    * 
    * @return TRUE if nested, FALSE otherwise
    */
   protected boolean isNested()
   {
      return m_nested;
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#isValid()
    */
   public boolean isValid()
   {
      return m_fullyQualifiedName != null && m_source != null && m_source.length() > 0;
   }

   /**
    * Assigns the path of the source or header file containing the type declaration
    * 
    * @param path Absolute path
    */
   protected void setAbsoluteSourcePath(File path)
   {
      assert path != null;
      assert path.getPath().length() > 0;

      m_absoluteSourcePath = path;
   }

   /**
    * Marks the represented type as abstract
    * 
    * @param value Either TRUE or FALSE
    */
   protected void setAbstract(boolean value)
   {
      m_abstract = value;
   }

   /**
    * Marks the represented type as accessible
    * 
    * @param value Either TRUE or FALSE
    */
   protected void setAccessible(boolean value)
   {
      m_accessible = value;
   }

   /**
    * Marks the represented type as extendable
    * 
    * @param value Either TRUE or FALSE
    */
   protected void setExtendable(boolean value)
   {
      m_extendable = value;
   }

   /**
    * Marks the represented type as nested
    * 
    * @param value Either TRUE or FALSE
    */
   protected void setNested(boolean value)
   {
      m_nested = value;
   }

   /**
    * Assigns the relative path of the compilation unit defining or implementing the represented type
    * 
    * @param path Relative path
    */
   protected void setRelativeCompilationUnitPath(String path)
   {
      assert path != null;
      assert path.length() > 0;

      m_relativeCompilationUnitPath = path;
   }

   /**
    * Assigns the name of the source file containing the file definition respectively implementation
    * 
    * @param source Source file
    */
   protected void setSource(String source)
   {
      assert source != null;
      assert source.length() > 0;

      m_source = source;
   }

   /**
    * @see java.lang.Object#toString()
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer("TYPE DEFINITION: ");
      buffer.append(getFullyQualifiedTypeName());
      buffer.append("\n");
      buffer.append("BASE CLASSES: ");
      buffer.append("\n");

      for (String baseClassName : getFullyQualifiedSuperClassNames())
      {
         buffer.append(baseClassName);
         buffer.append("\n");
      }

      buffer.append("IMPORTED TYPES: ");
      buffer.append("\n");

      for (String importedTypeName : getFullyQualifiedImportedTypeNames())
      {
         buffer.append(importedTypeName);
         buffer.append("\n");
      }

      buffer.append("IGNORED TYPES: ");
      buffer.append("\n");

      for (String ignoredTypeName : getIgnoredFullyQualifiedImportedTypeNames())
      {
         buffer.append(ignoredTypeName);
         buffer.append("\n");
      }

      buffer.append("SKIPPED TYPES: ");
      buffer.append("\n");

      for (String skippedTypeName : getSkippedFullyQualifiedImportedTypeNames())
      {
         buffer.append(skippedTypeName);
         buffer.append("\n");
      }

      return buffer.toString();
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf#wasRefactored()
    */
   public boolean wasRefactored()
   {
      return false;
   }
}
