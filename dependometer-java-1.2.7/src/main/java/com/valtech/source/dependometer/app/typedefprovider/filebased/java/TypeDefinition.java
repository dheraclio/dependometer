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

package com.valtech.source.dependometer.app.typedefprovider.filebased.java;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.valtech.source.ag.util.StringArray;
import com.valtech.source.dependometer.app.core.provider.IdentifierUtility;
import com.valtech.source.dependometer.app.core.provider.MetricsProviderIf;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class TypeDefinition implements TypeDefinitionIf
{
   private static final char DOT_CHAR = '.';

   private static final char SLASH_CHAR = '/';

   private static final String DOT = ".";

   static final int UNDEFINED_TYPE = -1;

   static final int CLASS_TYPE = 0;

   static final int INTERFACE_TYPE = 1;

   private static Map s_InlineableFieldToType = new HashMap();

   private static Map s_TypeNameToTypeDefinition = new HashMap();

   private int m_Type = UNDEFINED_TYPE;

   private int m_NumberOfAssertions = MetricsProviderIf.INITIAL_NUMBER_OF_ASSERTIONS;

   private boolean m_IsAbstract;

   private boolean m_IsExtendable;

   private boolean m_IsAccessible;

   private final String m_Source;

   private File m_AbsoluteSourcePath;

   private String m_RelativeCompilationUnitPath;

   private String m_PackageName;

   private String m_TypeName;

   private String m_UnrefactoredTypeName;

   private String m_SuperClassName;

   private StringArray m_SuperInterfaceNames = new StringArray(10, 2);

   private Set m_ImportedTypeNames = new HashSet();

   private Set m_IgnoredTypeNames = new HashSet();

   private Set m_SkippedTypeNames = new HashSet();

   private static void addInlineableField(String qualifiedName, TypeDefinition type)
   {
      assert qualifiedName != null;
      assert qualifiedName.length() > 0;
      assert qualifiedName.indexOf(DOT) != -1;
      assert type != null;

      List types = (List)s_InlineableFieldToType.get(qualifiedName);
      if (types == null)
      {
         types = new ArrayList();
         s_InlineableFieldToType.put(qualifiedName, types);
      }

      types.add(type);
   }

   static TypeDefinition getTypeDefintion(String typeName)
   {
      assert typeName != null;
      assert typeName.length() > 0;
      return (TypeDefinition)s_TypeNameToTypeDefinition.get(typeName);
   }

   static TypeDefinition[] getTypeDefinitionsForInlinableField(String qualifiedName, TypeDefinition type)
   {
      assert qualifiedName != null;
      assert qualifiedName.length() > 0;
      assert qualifiedName.indexOf(DOT) != -1;
      assert type != null;

      List types = (List)s_InlineableFieldToType.get(qualifiedName);

      if (types != null)
      {
         if (types.contains(type))
         {
            types = new ArrayList(types);
            types.remove(type);
         }

         return (TypeDefinition[])types.toArray(new TypeDefinition[0]);
      }
      else
      {
         return new TypeDefinition[0];
      }
   }

   TypeDefinition(String source)
   {
      assert source != null;
      assert source.length() > 0;
      m_Source = source;
   }

   public boolean isValid()
   {
      return m_Type != UNDEFINED_TYPE && m_TypeName != null && m_PackageName != null && m_Source != null
         && m_Source.length() > 0;
   }

   void setType(int type, boolean isAbstract, boolean isAccessible, boolean isExtendable)
   {
      assert m_Type == UNDEFINED_TYPE;
      assert type == CLASS_TYPE || type == INTERFACE_TYPE;
      assert type == INTERFACE_TYPE ? isAbstract == true : true;
      m_Type = type;
      m_IsAbstract = isAbstract;
      m_IsAccessible = isAccessible;
      m_IsExtendable = isExtendable;
   }

   void setAbsoluteSourcePath(File absoluteSourcePath)
   {
      assert absoluteSourcePath != null;
      assert absoluteSourcePath.isFile();
      m_AbsoluteSourcePath = absoluteSourcePath;
   }

   public File getAbsoluteSourcePath()
   {
      return m_AbsoluteSourcePath;
   }

   void setTypeName(String typeName)
   {
      assert m_TypeName == null;
      assert typeName != null;
      assert typeName.length() > 0;
      m_TypeName = IdentifierUtility.getFullyQualifiedTypeName(typeName);
      m_PackageName = IdentifierUtility.getPackageName(m_TypeName);
      s_TypeNameToTypeDefinition.put(m_TypeName, this);
   }

   void setCompilationUnitName(String name)
   {
      assert m_RelativeCompilationUnitPath == null;
      assert name != null;
      assert name.length() > 0;
      m_RelativeCompilationUnitPath = m_PackageName.replace(DOT_CHAR, SLASH_CHAR) + SLASH_CHAR + name;
   }

   void setSuperClassName(String superClassName)
   {
      assert m_SuperClassName == null;
      assert superClassName != null;
      assert superClassName.length() > 0;
      m_SuperClassName = IdentifierUtility.getFullyQualifiedTypeName(superClassName);
   }

   void addSuperInterfaceName(String superInterfaceName)
   {
      assert superInterfaceName != null;
      m_SuperInterfaceNames.add(IdentifierUtility.getFullyQualifiedTypeName(superInterfaceName));
   }

   void addImportedTypeName(String typeName)
   {
      assert typeName != null;
      assert m_TypeName != null;
      typeName = IdentifierUtility.getFullyQualifiedTypeName(typeName);
      if (!typeName.equals(m_TypeName))
      {
         m_ImportedTypeNames.add(typeName);
      }
   }

   void addInlineableField(String field)
   {
      assert field != null;
      assert field.length() > 0;
      assert m_TypeName != null;
      String qualifiedName = IdentifierUtility.getTypeName(m_TypeName) + DOT + field;
      addInlineableField(qualifiedName, this);
   }

   public boolean isClass()
   {
      return m_Type == CLASS_TYPE;
   }

   public boolean isInterface()
   {
      return m_Type == INTERFACE_TYPE;
   }

   public boolean isAbstract()
   {
      return m_IsAbstract;
   }

   public String getFullyQualifiedTypeName()
   {
      return m_TypeName;
   }

   public String[] getFullyQualifiedSuperInterfaceNames()
   {
      return m_SuperInterfaceNames.getStringArray();
   }

   public String[] getFullyQualifiedImportedTypeNames()
   {
      return (String[])m_ImportedTypeNames.toArray(new String[0]);
   }

   String getPackageName()
   {
      return m_PackageName;
   }

   public String getRelativeCompilationUnitPath()
   {
      if (m_RelativeCompilationUnitPath != null)
      {
         return m_RelativeCompilationUnitPath;
      }
      else
      {
         return IdentifierUtility.getRelativeCompilationUnitPath(getFullyQualifiedTypeName());
      }
   }

   void setNumberOfAssertions(int number)
   {
      assert number >= -1;
      m_NumberOfAssertions = number;
   }

   public int getNumberOfAssertions()
   {
      return m_NumberOfAssertions;
   }

   public Object getSource()
   {
      return m_Source;
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
      else if (obj instanceof TypeDefinition)
      {
         if (m_TypeName.equals(((TypeDefinition)obj).m_TypeName))
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
      return m_TypeName.hashCode();
   }

   public boolean isAccessible()
   {
      return m_IsAccessible;
   }

   void setUnrefactoredTypeName(String typeName)
   {
      assert typeName != null;
      assert typeName.length() > 0;
      typeName = IdentifierUtility.getFullyQualifiedTypeName(typeName);
      assert !m_TypeName.equals(typeName);
      m_UnrefactoredTypeName = typeName;
   }

   public boolean wasRefactored()
   {
      return m_UnrefactoredTypeName != null;
   }

   public String getUnrefactoredFullyQualifiedTypeName()
   {
      assert wasRefactored();
      return m_UnrefactoredTypeName;
   }

   public String[] getFullyQualifiedSuperClassNames()
   {
      if (m_SuperClassName != null)
      {
         return new String[] {
            m_SuperClassName };
      }
      else
      {
         return new String[0];
      }
   }

   void addIgnoredTypeName(String typeName)
   {
      assert typeName != null;
      assert m_TypeName != null;
      typeName = IdentifierUtility.getFullyQualifiedTypeName(typeName);
      if (!typeName.equals(m_TypeName))
      {
         m_IgnoredTypeNames.add(typeName);
      }
   }

   public String[] getIgnoredFullyQualifiedImportedTypeNames()
   {
      return (String[])m_IgnoredTypeNames.toArray(new String[0]);
   }

   void addSkippedTypeName(String typeName)
   {
      assert typeName != null;
      assert m_TypeName != null;
      typeName = IdentifierUtility.getFullyQualifiedTypeName(typeName);
      if (!typeName.equals(m_TypeName))
      {
         m_SkippedTypeNames.add(typeName);
      }
   }

   public String[] getSkippedFullyQualifiedImportedTypeNames()
   {
      return (String[])m_SkippedTypeNames.toArray(new String[0]);
   }

   public boolean isExtendable()
   {
      return m_IsExtendable;
   }
}