package com.valtech.source.dependometer.app.typedefprovider.filebased.xml;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.valtech.source.dependometer.app.core.provider.IdentifierUtility;
import com.valtech.source.dependometer.app.core.provider.MetricsProviderIf;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;

public class TypeDefinitionXML implements TypeDefinitionIf
{
   private static final char DOT_CHAR = '.';

   private static final char SLASH_CHAR = '/';

   private int m_NumberOfAssertions = MetricsProviderIf.INITIAL_NUMBER_OF_ASSERTIONS;

   private Set<String> fullyQualifiedImportedTypeNames = new HashSet<String>();

   private Set<String> fullyQualifiedSuperClassNames = new HashSet<String>();

   private Set<String> fullyQualifiedInterfaceNames = new HashSet<String>();

   private Set<TypePropertyXMLEnum> properties = new HashSet<TypePropertyXMLEnum>();

   private Set<String> m_IgnoredTypeNames = new HashSet<String>();

   private Set<String> m_SkippedTypeNames = new HashSet<String>();

   private static Map<String, TypeDefinitionXML> s_TypeNameToTypeDefinition = new HashMap<String, TypeDefinitionXML>();

   private String m_TypeName;

   private String m_PackageName;

   private String m_UnrefactoredTypeName;

   private File m_AbsoluteSourcePath;

   private String m_RelativeCompilationUnitPath;

   public void setTypeName(String typeName)
   {
      assert m_TypeName == null;
      assert typeName != null;
      assert typeName.length() > 0;
      m_TypeName = IdentifierUtility.getFullyQualifiedTypeName(typeName);
      m_PackageName = IdentifierUtility.getPackageName(m_TypeName);
      s_TypeNameToTypeDefinition.put(m_TypeName, this);
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

   public String[] getFullyQualifiedImportedTypeNames()
   {
      return fullyQualifiedImportedTypeNames.toArray(new String[0]);
   }

   public boolean addFullyQualifiedImportedTypeNames(String name)
   {
      return fullyQualifiedImportedTypeNames.add(name);
   }

   public String[] getFullyQualifiedSuperClassNames()
   {
      return fullyQualifiedSuperClassNames.toArray(new String[0]);
   }

   public boolean addFullyQualifiedSuperClassNames(String name)
   {
      return fullyQualifiedSuperClassNames.add(name);
   }

   public String[] getFullyQualifiedSuperInterfaceNames()
   {
      return fullyQualifiedInterfaceNames.toArray(new String[0]);
   }

   public boolean addFullyQualifiedSuperInterfaceNames(String name)
   {
      return fullyQualifiedInterfaceNames.add(name);
   }

   public String getFullyQualifiedTypeName()
   {
      return m_TypeName;
   }

   public boolean addProperty(String name)
   {
      return properties.add(TypePropertyXMLEnum.parse(name));
   }

   public String[] getIgnoredFullyQualifiedImportedTypeNames()
   {
      return m_IgnoredTypeNames.toArray(new String[0]);
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

   void setCompilationUnitName(String name)
   {
      assert m_RelativeCompilationUnitPath == null;
      assert name != null;
      assert name.length() > 0;
      m_RelativeCompilationUnitPath = m_PackageName.replace(DOT_CHAR, SLASH_CHAR) + SLASH_CHAR + name;
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
      return m_SkippedTypeNames.toArray(new String[0]);
   }

   public Object getSource()
   {
      return "test source";
   }

   public boolean isAbstract()
   {
      return properties.contains(TypePropertyXMLEnum.ABSTRACT);
   }

   public boolean isAccessible()
   {
      return properties.contains(TypePropertyXMLEnum.PUBLIC);
   }

   public boolean isClass()
   {
      return properties.contains(TypePropertyXMLEnum.CLASS);
   }

   public boolean isExtendable()
   {
      return !properties.contains(TypePropertyXMLEnum.FINAL);
   }

   public boolean isInterface()
   {
      return properties.contains(TypePropertyXMLEnum.INTERFACE);
   }

   public boolean isValid()
   {
      return m_TypeName != null && m_PackageName != null;
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

   @Override
   public String toString()
   {
      String s = "";
      s += this.getFullyQualifiedTypeName() + "\n";
      s += "Imported types: " + Arrays.toString(this.getFullyQualifiedImportedTypeNames()) + "\n";
      return s;
   }

}
