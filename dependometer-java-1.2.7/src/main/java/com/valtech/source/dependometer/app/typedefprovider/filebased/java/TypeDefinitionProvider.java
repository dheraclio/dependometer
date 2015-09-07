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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.ag.util.DirectoryScanner;
import com.valtech.source.dependometer.app.core.provider.AbstractTypeDefinitionProvider;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.IIdentifierResolver;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;
import com.valtech.source.dependometer.app.typedefprovider.filebased.java.ArchiveReader.Entry;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class TypeDefinitionProvider extends AbstractTypeDefinitionProvider
{
   private static final char DOT_CHAR = '.';

   private static final String DOT = ".";

   private static final String ASTERISK = "*";

   private static final String JAVA_EXTENSION = ".java";

   private static Logger s_Logger = Logger.getLogger(TypeDefinitionProvider.class.getName());

   private DirectoryScanner m_ClassesDirectoryScanner;

   private ArchiveReader m_ArchiveReader;

   private ClassesFilter m_ClassesFilter;

   private int m_TypeDuplicates;

   private Map m_TypeDefinitions;

   private int m_SourceFileDuplicates;

   private Map m_SourceFiles;

   private SourceFileParserIf m_SourceFileParser;

   private final List m_AdditionalInfo = new ArrayList();

   private ClassFileParser classFileParser;

   private int countChar(char c, String s)
   {
      assert s != null;

      int fromIndex = -1;
      int count = 0;

      while ((fromIndex = s.indexOf(c, fromIndex + 1)) != -1)
      {
         ++count;
      }

      return count;
   }

   public TypeDefinitionIf[] execute() throws IOException
   {
      ConfigurationProviderIf configurationProvider = getConfigurationProvider();

      classFileParser = new ClassFileParser(this);
      classFileParser.refactorings(configurationProvider.getRefactorings());

      s_Logger.info("searching/parsing class files (plain/archived) and corresponding source files ...");

      scanDirectories();

      List<File>classFiles = m_ClassesDirectoryScanner.getFiles();
      int numberOfClassFiles = classFiles.size();
      Entry[] entries = m_ArchiveReader.getEntries();
      int numberOfArchivedClassFiles = entries.length;
      int totalNumberOfClassFiles = numberOfClassFiles + numberOfArchivedClassFiles;
      m_TypeDefinitions = new HashMap(totalNumberOfClassFiles);

      for (File archive: classFiles)
      {
         createTypeDefintion(new FileInputStream(archive), archive.getCanonicalPath());
      }

      for (int i = 0; i < numberOfArchivedClassFiles; ++i)
      {
         Entry nextEntry = entries[i];
         createTypeDefintion(nextEntry.getInputStream(), nextEntry.getSource());
      }

      TypeDefinition[] typeDefinitions = (TypeDefinition[])m_TypeDefinitions.values().toArray(new TypeDefinition[0]);
      m_TypeDefinitions = null;
      m_SourceFiles = new HashMap(totalNumberOfClassFiles);
      analyzeSources(typeDefinitions);

      int numberOfSkippedClasses = m_ClassesFilter.getNumberOfSkippedClasses();
      String numberOfClassFilesMessage = "class files (plain/archived/skipped/ignored duplicates) = "
         + classFiles.size() + "/" + entries.length + "/" + numberOfSkippedClasses + "/" + m_TypeDuplicates;
      s_Logger.info(numberOfClassFilesMessage);
      m_AdditionalInfo.add(numberOfClassFilesMessage);
      String[] skippedClasses = m_ClassesFilter.getFullyQualifiedSkippedClassNames();
      if (skippedClasses.length > 0)
      {
         m_AdditionalInfo.add("skipped class files (" + numberOfSkippedClasses + ")");
         for (int i = 0; i < skippedClasses.length; i++)
         {
            m_AdditionalInfo.add("-- " + skippedClasses[i]);
         }
      }
      String numberOfSourceFilesMessage = "source files (plain/ignored duplicates) = " + m_SourceFiles.size() + "/"
         + m_SourceFileDuplicates;
      s_Logger.info(numberOfSourceFilesMessage);
      m_AdditionalInfo.add(numberOfSourceFilesMessage);
      m_SourceFiles = null;

      return typeDefinitions;
   }

   private void createTypeDefintion(InputStream in, String source) throws IOException
   {
      assert in != null;
      assert source != null;
      assert source.length() > 0;

      TypeDefinition nextTypeDefinition = classFileParser.parse(in, source);
      if (m_TypeDefinitions.containsKey(nextTypeDefinition.getFullyQualifiedTypeName()))
      {
         TypeDefinition alreadyAdded = (TypeDefinition)m_TypeDefinitions.get(nextTypeDefinition
            .getFullyQualifiedTypeName());
         s_Logger.warn("ignoring type definition with fully qualified name '"
            + nextTypeDefinition.getFullyQualifiedTypeName() + "' from source '" + nextTypeDefinition.getSource()
            + "' - type was already added from source '" + alreadyAdded.getSource() + "'");

         ++m_TypeDuplicates;
      }
      else
      {
         m_TypeDefinitions.put(nextTypeDefinition.getFullyQualifiedTypeName(), nextTypeDefinition);
      }
   }

   private void scanDirectories() throws IOException
   {
      assert m_ClassesFilter == null;
      ConfigurationProviderIf configurationProvider = getConfigurationProvider();
      m_ClassesFilter = new ClassesFilter(configurationProvider.getPackageFilter(), configurationProvider
         .getCompilationUnitFilter());
      m_ClassesDirectoryScanner = new DirectoryScanner(m_ClassesFilter, true);
      m_ArchiveReader = new ArchiveReader(m_ClassesFilter, true);
      File[] inputDirectories = configurationProvider.getInputDirectories();

      if (inputDirectories.length == 0)
      {
         s_Logger.warn("No input directories have been configured!!");
      }
      for (int i = 0; i < inputDirectories.length; ++i)
      {
         File nextDir = inputDirectories[i];
         String msg = "searching input files in directory = " + nextDir.getCanonicalPath();
         s_Logger.debug(msg);
         m_AdditionalInfo.add(msg);
         m_ClassesDirectoryScanner.scan(nextDir);
         m_ArchiveReader.scan(nextDir);
      }
   }

   private void analyzeSources(TypeDefinition[] typeDefinitions) throws IOException
   {
      assert AssertionUtility.checkArray(typeDefinitions);
      assert m_SourceFiles != null;
      assert m_SourceFiles.size() == 0;

      ConfigurationProviderIf configurationProvider = getConfigurationProvider();
      if (m_SourceFileParser == null)
      {
         m_SourceFileParser = new JavaSourceParser();
         String assertionPattern = configurationProvider.getAssertionPattern();
         if (assertionPattern != null && assertionPattern.length() > 0)
         {
            m_SourceFileParser.setAssertionPattern(assertionPattern);
         }
      }
      File[] inputDirectories = configurationProvider.getInputDirectories();

      int numberOfTypesWithoutSource = 0;
      for (int i = 0; i < typeDefinitions.length; ++i)
      {
         TypeDefinition nextTypeDefinition = typeDefinitions[i];
         String typeName = null;
         if (!nextTypeDefinition.wasRefactored())
         {
            typeName = nextTypeDefinition.getFullyQualifiedTypeName();
         }
         else
         {
            typeName = nextTypeDefinition.getUnrefactoredFullyQualifiedTypeName();
         }

         boolean sourceFileFound = false;

         for (int j = 0; j < inputDirectories.length; ++j)
         {
            String relSourceFilePath = typeName.replace(DOT_CHAR, File.separatorChar) + JAVA_EXTENSION;
            File sourceFile = new File(inputDirectories[j], relSourceFilePath);
            if (sourceFile.exists())
            {
               sourceFileFound = true;
               if (m_SourceFiles.containsKey(relSourceFilePath))
               {
                  File alreadyAdded = (File)m_SourceFiles.get(relSourceFilePath);
                  s_Logger.warn("ignoring source file '" + sourceFile + "' - source file was already added from path '"
                     + alreadyAdded + "'");

                  ++m_SourceFileDuplicates;
               }
               else
               {
                  m_SourceFiles.put(relSourceFilePath, sourceFile);
                  nextTypeDefinition.setAbsoluteSourcePath(sourceFile);
                  m_SourceFileParser.parse(sourceFile);
                  int numberOfAssertions = m_SourceFileParser.getNumberOfAssertions();
                  nextTypeDefinition.setNumberOfAssertions(numberOfAssertions);
                  String[] access = m_SourceFileParser.getAccess();

                  for (int k = 0; k < access.length; ++k)
                  {
                     analyzePossibleUsageOfInlineableField(access[k], nextTypeDefinition);
                  }

                  String[] dotClass = m_SourceFileParser.getDotClass();
                  for (int k = 0; k < dotClass.length; ++k)
                  {
                     analyzeDotClassUsage(dotClass[k], nextTypeDefinition);
                  }
               }
            }
            else
            // check for multiple classes in one source file
            {
               if (!sourceFileFound)
               {
                  String compUnitPath = nextTypeDefinition.getRelativeCompilationUnitPath();
                  File srcFile = new File(inputDirectories[j], compUnitPath);
                  sourceFileFound = srcFile.exists();
               }
            }
         }
         if (!sourceFileFound)
         {
            numberOfTypesWithoutSource++;
            String compUnitPath = nextTypeDefinition.getRelativeCompilationUnitPath();
            m_AdditionalInfo.add("source file '" + compUnitPath + "' not found for type '" + typeName + "'");
         }
      }
      m_AdditionalInfo.add("total number of types without corresponding source file = " + numberOfTypesWithoutSource);
      if (numberOfTypesWithoutSource > 0)
      {
         s_Logger.warn("total number of types without corresponding source file = " + numberOfTypesWithoutSource);
      }
   }

   private String normalizePossibleUsage(String possibleUsage)
   {
      assert possibleUsage != null;
      assert possibleUsage.length() > 0;

      int numberOfDots = countChar(DOT_CHAR, possibleUsage);
      assert numberOfDots > 0;

      String normalizedUsage = possibleUsage;
      if (numberOfDots > 1)
      {
         int pos = normalizedUsage.lastIndexOf(DOT_CHAR);
         assert pos != -1;
         pos = normalizedUsage.lastIndexOf(DOT_CHAR, pos - 1);
         assert pos != -1;
         normalizedUsage = normalizedUsage.substring(pos + 1, normalizedUsage.length());
      }

      return normalizedUsage;
   }

   private String getDefiningType(String fqPossibleUsage)
   {
      assert fqPossibleUsage != null;
      assert fqPossibleUsage.length() > 0;

      int numberOfDots = countChar(DOT_CHAR, fqPossibleUsage);
      assert numberOfDots > 1;
      int pos = fqPossibleUsage.lastIndexOf(DOT_CHAR);
      assert pos != -1;
      return fqPossibleUsage.substring(0, pos);
   }

   private void analyzeDotClassUsage(String dotClass, TypeDefinition typeDefinition)
   {
      assert typeDefinition != null;
      assert dotClass != null;
      assert dotClass.length() > 0;

      String candidate = dotClass;

      if (countChar(DOT_CHAR, dotClass) == 0)
      // Not fully qualified
      {
         candidate = typeDefinition.getPackageName() + DOT + dotClass;
      }

      if (referenceAdded(typeDefinition, candidate))
      {
         return;
      }

      String[] imports = m_SourceFileParser.getImports();
      for (int i = 0; i < imports.length; ++i)
      {
         candidate = getImport(imports[i]) + dotClass;
         if (referenceAdded(typeDefinition, candidate))
         {
            return;
         }
      }
   }

   private boolean referenceAdded(TypeDefinition typeDefinition, String candidate)
   {
      assert typeDefinition != null;
      assert candidate != null;
      assert candidate.length() > 0;

      candidate = classFileParser.refactor(candidate);

      TypeDefinition referencedTypeDefinition = TypeDefinition.getTypeDefintion(candidate);
      if (referencedTypeDefinition != null)
      {
         String fqTypeName = referencedTypeDefinition.getFullyQualifiedTypeName();
         if (!typeDefinition.getFullyQualifiedTypeName().equals(fqTypeName)
            && !ignoreType(typeDefinition.getFullyQualifiedTypeName(), fqTypeName))
         {
            typeDefinition.addImportedTypeName(fqTypeName);
         }
         return true;
      }

      return false;
   }

   private String getImport(String fullImport)
   {
      assert fullImport != null;
      assert fullImport.length() > 0;
      int pos = fullImport.lastIndexOf(DOT_CHAR);
      assert pos != -1;
      return fullImport.substring(0, pos + 1);
   }

   private void analyzePossibleUsageOfInlineableField(String possibleUsage, TypeDefinition typeDefinition)
   {
      assert possibleUsage != null;
      assert countChar(DOT_CHAR, possibleUsage) > 0;
      assert typeDefinition != null;

      String normalizedPossibleUsage = normalizePossibleUsage(possibleUsage);
      TypeDefinition[] typeDefinitionsWithInlineableFields = TypeDefinition.getTypeDefinitionsForInlinableField(
         normalizedPossibleUsage, typeDefinition);

      if (typeDefinitionsWithInlineableFields.length == 1)
      {
         String fqTypeName = typeDefinitionsWithInlineableFields[0].getFullyQualifiedTypeName();
         if (!typeDefinition.getFullyQualifiedTypeName().equals(fqTypeName)
            && !ignoreType(typeDefinition.getFullyQualifiedTypeName(), fqTypeName))
         {
            typeDefinition.addImportedTypeName(fqTypeName);
         }
      }
      else
      {
         if (countChar(DOT_CHAR, possibleUsage) > 1)
         // Fully qualified
         {
            String definingType = getDefiningType(possibleUsage);
            for (int i = 0; i < typeDefinitionsWithInlineableFields.length; ++i)
            {
               TypeDefinition nextCandidate = typeDefinitionsWithInlineableFields[i];
               if (definingType.equals(nextCandidate.getFullyQualifiedTypeName()))
               {
                  String fqTypeName = nextCandidate.getFullyQualifiedTypeName();
                  if (!ignoreType(typeDefinition.getFullyQualifiedTypeName(), fqTypeName))
                  {
                     typeDefinition.addImportedTypeName(fqTypeName);
                  }
                  break;
               }
            }
         }
         else
         {
            String[] imports = m_SourceFileParser.getImports();

            boolean resolved = false;
            for (int i = 0; i < typeDefinitionsWithInlineableFields.length; ++i)
            {
               TypeDefinition nextCandidate = typeDefinitionsWithInlineableFields[i];
               if (isImportedType(imports, nextCandidate))
               {
                  String fqTypeName = nextCandidate.getFullyQualifiedTypeName();
                  if (!ignoreType(typeDefinition.getFullyQualifiedTypeName(), fqTypeName))
                  {
                     typeDefinition.addImportedTypeName(fqTypeName);
                  }
                  resolved = true;
                  break;
               }
            }

            if (!resolved)
            {
               for (int i = 0; i < typeDefinitionsWithInlineableFields.length; ++i)
               {
                  TypeDefinition nextCandidate = typeDefinitionsWithInlineableFields[i];
                  if (typeDefinition.getPackageName().equals(nextCandidate.getPackageName()))
                  {
                     String fqTypeName = nextCandidate.getFullyQualifiedTypeName();
                     if (!ignoreType(typeDefinition.getFullyQualifiedTypeName(), fqTypeName))
                     {
                        typeDefinition.addImportedTypeName(fqTypeName);
                     }
                     break;
                  }
               }
            }
         }
      }
   }

   private boolean isImportedType(String[] imports, TypeDefinition type)
   {
      assert AssertionUtility.checkArray(imports);
      assert type != null;

      for (int i = 0; i < imports.length; ++i)
      {
         String nextImport = imports[i];
         int pos = nextImport.indexOf(DOT + ASTERISK);
         if (pos != -1)
         {
            nextImport = nextImport.substring(0, pos);
            if (type.getPackageName().equals(nextImport))
            {
               return true;
            }
         }
         else
         {
            if (type.getFullyQualifiedTypeName().equals(nextImport))
            {
               return true;
            }
         }

      }

      return false;
   }

   public String[] getAdditionalInfo()
   {
      return (String[])m_AdditionalInfo.toArray(new String[0]);
   }

   public IIdentifierResolver getIdentifierResolver()
   {
      return new JavaIdentifierResolver();
   }
}