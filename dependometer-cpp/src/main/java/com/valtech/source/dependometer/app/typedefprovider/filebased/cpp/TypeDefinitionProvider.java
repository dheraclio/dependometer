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
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;

import com.valtech.source.ag.util.DirectoryScanner;
import com.valtech.source.dependometer.app.core.provider.AbstractTypeDefinitionProvider;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.IIdentifierResolver;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;

/**
 * This class retrieves the <code>TypeDefinitionIf</code> objects that represent all C++ types that reside under the
 * &lt;src dir="..."/&gt; in the dependometer config.xml file. The parsing is done with the CDT parser for C++. CDT is
 * an eclipse plugin or development tool for the C/C++ environment.
 * 
 * @author Marek Wozny (marek.wozny@valtech.de)
 * @version 2.0
 * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionProviderIf
 */
public class TypeDefinitionProvider extends AbstractTypeDefinitionProvider
{
   /**
    * Dot separator
    */
   public static final String PACKAGE_SEPARATOR = ".";

   /**
    * Nested separator
    */
   public static final String NESTEDCLASS_SEPARATOR = "$";

   /**
    * Logging facility
    */
   private static final Logger s_logger = Logger.getLogger(TypeDefinitionProvider.class.getName());

   /**
    * Prints out all collected type definitions
    * 
    * @param arguments Command line arguments
    * @throws IOException
    */
   public static void main(String arguments[]) throws IOException
   {
      System.out.println("******** Started TypeDefinitionProvider C++");

      TypeDefinitionProvider provider = new TypeDefinitionProvider();
      TypeDefinitionIf[] typeDefinitions = provider.getTypeDefinitions();

      for (TypeDefinitionIf typeDefinition : typeDefinitions)
      {
         System.out.println(typeDefinition.toString());
      }

      System.out.println("******** Finished TypeDefinitionProvider C++");
   }

   /**
    * Contains the adapted qualified name required for the dependometer core processing steps of a given fully qualified
    * name
    */
   private Map<String, String> m_adaptedFullyQualifiedNames = new HashMap<String, String>();

   /**
    * Additional information
    */
   private final List<String> m_additionalInfo = new ArrayList<String>();

   /**
    * Filter for compilation units to consider
    */
   private CompilationUnitsFilter m_compilationUnitsFilter = null;

   /**
    * Scanned source directories
    */
   private String[] m_directories = null;

   /**
    * C++ source file parser used to resolve types and references
    */
   private SourceFileParser m_parser;

   /**
    * Package prefix for source directory.
    */
   private Map<File, String> m_srcDirPrefix = new HashMap();

   public TypeDefinitionProvider()
   {
      m_parser = new SourceFileParser();
   }

   /**
    * Attempts to adapt the given fully qualified name so that it is processed correctly by the dependometer core
    * 
    * @param fullyQualifiedName Fully qualified name
    * @return Adapted qualified name
    */
   private String adaptFullyQualifiedName(String fullyQualifiedName)
   {
      assert fullyQualifiedName != null;

      if (!m_adaptedFullyQualifiedNames.containsKey(fullyQualifiedName))
      {
         StringBuilder adaptedFullyQualifiedName = new StringBuilder();
         int position = fullyQualifiedName.lastIndexOf(SourceFileParser.SCOPE_SEPARATOR);

         if (position < 0)
         {
            adaptedFullyQualifiedName.append(fullyQualifiedName);
         }
         else if (position == 0)
         {
            adaptedFullyQualifiedName.append(fullyQualifiedName.substring(SourceFileParser.SCOPE_SEPARATOR.length()));
         }
         else if (position > 0)
         {
            // If given qualified name represents a nested type
            // it might not be globally qualified. Thus adapt
            // the qualified name of the nesting type first before
            // constructing its globally qualified name
            if (m_parser.isNested(fullyQualifiedName))
            {
               String nestingTypeFullyQualifiedName = fullyQualifiedName.substring(0, position);

               if (m_adaptedFullyQualifiedNames.containsKey(nestingTypeFullyQualifiedName))
                  adaptedFullyQualifiedName.append(m_adaptedFullyQualifiedNames.get(nestingTypeFullyQualifiedName));
               else
                  adaptedFullyQualifiedName.append(adaptFullyQualifiedName(nestingTypeFullyQualifiedName));

               adaptedFullyQualifiedName.append(NESTEDCLASS_SEPARATOR);
               adaptedFullyQualifiedName.append(fullyQualifiedName.substring(position
                  + SourceFileParser.SCOPE_SEPARATOR.length()));
            }
            else
            {
               // Given qualified name is interpreted as globally qualified
               // thus replace all occurrences of the scope separator by
               // the
               // dot separator used by dependometer core
               adaptedFullyQualifiedName.append(fullyQualifiedName.replaceAll(SourceFileParser.SCOPE_SEPARATOR,
                  PACKAGE_SEPARATOR));
            }
         }

         ConfigurationProviderIf configurationProvider = ProviderFactory.getInstance().getConfigurationProvider();
         if (!configurationProvider.ignorePhysicalStructure())
         {
            // Given qualified name is not globally qualified
            // Use its source or containing filename to determine
            // its qualification in case it is not externally defined
            String containingFilename = m_parser.getSource(fullyQualifiedName);

            if (containingFilename != null)
            {
               File parent = new File(containingFilename).getParentFile();
               if (parent != null)
               {
                  String prefix = m_srcDirPrefix.get(parent);

                  if (prefix == null)
                  {
                     String currDir = null;
                     String relPath = null;
                     for (String directory : m_directories)
                     {
                        if (containingFilename.startsWith(directory)
                           && (currDir == null || currDir.length() > directory.length()))
                        {
                           String rest = containingFilename.substring(directory.length());
                           if (rest.charAt(0) == File.separatorChar && rest.lastIndexOf(File.separatorChar) > 1)
                           {
                              relPath = rest.substring(1, rest.lastIndexOf(File.separatorChar));
                              currDir = directory;
                           }
                        }
                     }
                     if (relPath != null && relPath.length() > 0)
                     {
                        StringBuilder sb = new StringBuilder();
                        /*
                         * for (String part : relPath.split( new Character(File.separatorChar).toString()))
                         */
                        for (String part : relPath.split("\\\\|/"))
                        {
                           sb.append(part);
                           sb.append(PACKAGE_SEPARATOR);
                        }
                        prefix = sb.toString();
                        m_srcDirPrefix.put(parent, prefix);
                     }
                  }

                  if (prefix != null)
                  {
                     adaptedFullyQualifiedName.insert(0, prefix);
                  }
               }
            }
         }

         m_adaptedFullyQualifiedNames.put(fullyQualifiedName, adaptedFullyQualifiedName.toString());
      }

      s_logger.log(Priority.DEBUG, fullyQualifiedName + " -> " + m_adaptedFullyQualifiedNames.get(fullyQualifiedName));

      return m_adaptedFullyQualifiedNames.get(fullyQualifiedName);
   }

   /**
    * Adds the given type name either as skipped, ignored or simple reference to the passed type definition
    * 
    * @param typeDefinition Type definition
    * @param typeName Fully qualified type name
    */
   private void addReference(TypeDefinition typeDefinition, String typeName)
   {
      assert typeName != null;
      assert typeName.length() > 0;
      assert typeDefinition != null;

      if (!typeDefinition.getFullyQualifiedTypeName().equals(typeName))
      {
         if (skipType(typeName))
            typeDefinition.addSkippedReferencedType(typeName);
         else
         {
            if (ignoreType(typeDefinition.getFullyQualifiedTypeName(), typeName))
               typeDefinition.addIgnoredReferencedType(typeName);
            else
               typeDefinition.addReferencedType(typeName);
         }
      }
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.ProviderIf#getAdditionalInfo()
    */
   public String[] getAdditionalInfo()
   {
      return m_additionalInfo.toArray(new String[m_additionalInfo.size()]);
   }

   /**
    * @see com.valtech.source.dependometer.app.core.provider.TypeDefinitionProviderIf#getTypeDefinitions()
    */
   public TypeDefinitionIf[] execute() throws IOException
   {
      ConfigurationProviderIf configurationProvider = ProviderFactory.getInstance().getConfigurationProvider();

      m_parser.setAssertionPattern(configurationProvider.getAssertionPattern());
      
      Charset fileEncoding = configurationProvider.getFileEncoding();
      if (fileEncoding != null)
      {
         m_parser.setFileEncoding(fileEncoding);
      }
      
      // Collect all compilation units to be considered
      List<File>collectedCompilationUnits = scanDirectories();

      int numberOfSkippedCompilationUnits = m_compilationUnitsFilter.getNumberOfSkippedCompilationUnits();
      StringBuffer numberOfCompilationUnitsMessage = new StringBuffer("compilation units (plain/skipped/ignored) = ");
      numberOfCompilationUnitsMessage.append(collectedCompilationUnits.size());
      numberOfCompilationUnitsMessage.append("/");
      numberOfCompilationUnitsMessage.append(numberOfSkippedCompilationUnits);
      numberOfCompilationUnitsMessage.append("/");
      numberOfCompilationUnitsMessage.append(0);
      m_additionalInfo.add(numberOfCompilationUnitsMessage.toString());

      if (numberOfSkippedCompilationUnits > 0)
      {
         m_additionalInfo.add("Skipped compilation units (" + numberOfSkippedCompilationUnits + ")");
         String[] skippedCompilationUnits = m_compilationUnitsFilter.getSkippedCompilationUnits();

         for (String skippedCompilationUnit : skippedCompilationUnits)
         {
            m_additionalInfo.add("-- " + skippedCompilationUnit);
         }
      }

      Date start = new Date();
      // Parse each compilation unit and resolve its outgoing references
      Map<String, Integer> assertionsPerCU = new HashMap<>();

      for (int i = 0; i < collectedCompilationUnits.size(); i++)
      {
         File compilationUnit = collectedCompilationUnits.get(i);
         System.out.println("*** CompilationUnit " + (i + 1) + '/' + collectedCompilationUnits.size() + ": "
            + compilationUnit.getPath());

         m_parser.parse(compilationUnit);
         String path = compilationUnit.getPath();
         int inx = path.lastIndexOf(".");
         if (inx > 0)
            path = path.substring(0, inx); // cut off suffix of filename
         assertionsPerCU.put(path, m_parser.getNumberOfAssertions());
         // no effect: Runtime.getRuntime().gc();

         long elapsed = new Date().getTime() - start.getTime();
         long estimate = elapsed * (collectedCompilationUnits.size() - i - 1) / (i + 1);

         System.out.println("*** Estimated rest parse time: " + estimate / 1000 / 60 / 60 + " hours " + estimate / 1000
            / 60 % 60 + " minutes " + estimate / 1000 % 60 + " seconds.");
      }

      /** free cached header files */
      FileCodeReaderFactory factory = FileCodeReaderFactory.getInstance();
      factory.getCodeReaderCache().flush();

      // no effect: Runtime.getRuntime().gc();

      // Interpret the result of the first pass
      List<String> types = m_parser.getTypes();
      Map<String, String> externallyDefinedTypes = new HashMap<String, String>();
      Map<String, TypeDefinition> typeDefinitions = new HashMap<String, TypeDefinition>();
      TypeDefinition typeDefinition = null;
      String sourcePath = null;
      boolean outOfScope = false;

      for (String fullyQualifiedName : types)
      {
         if (!typeDefinitions.containsKey(fullyQualifiedName))
         {
            sourcePath = null;
            sourcePath = m_parser.getFilename(fullyQualifiedName);
            if (sourcePath == null)
            {
               // If this is not a link-time error,
               // maybe some programmer avoided including system headers by
               // copying declarations.
               s_logger.warn(fullyQualifiedName + " is lacking definition.");
               continue;
            }

            typeDefinition = new TypeDefinition(adaptFullyQualifiedName(fullyQualifiedName), (m_parser
               .isClass(fullyQualifiedName) ? TypeDefinition.CLASS_KIND : TypeDefinition.STRUCT_KIND));
            // Union Kind?

            String path = sourcePath;
            int inx = path.lastIndexOf(".");
            if (inx > 0)
               path = path.substring(0, inx);
            Integer noas = assertionsPerCU.get(path);
            if (noas != null)
               typeDefinition.setNumberOfAssertions(noas.intValue());
            else
               typeDefinition.setNumberOfAssertions(0);
            typeDefinition.setAbsoluteSourcePath(new File(sourcePath));
            typeDefinition.setAbstract(m_parser.isAbstract(fullyQualifiedName));
            typeDefinition.setAccessible(true);
            typeDefinition.setExtendable(true);
            typeDefinition.setNested(m_parser.isNested(fullyQualifiedName));

            for (String directory : m_directories)
            {
               if (sourcePath.startsWith(directory))
               {
                  // Cut off the directory part to retrieve the relative
                  // path
                  // to the source directory
                  typeDefinition.setRelativeCompilationUnitPath(sourcePath.substring(directory.length()
                     + File.separator.length()));
                  outOfScope = false;
                  break;
               }
            }

            if (typeDefinition.getRelativeCompilationUnitPath() == null)
            {
               typeDefinition.setRelativeCompilationUnitPath(sourcePath);
               outOfScope = true;
            }

            if (m_parser.getSource(fullyQualifiedName) == null)
            {
               typeDefinition.setSource(sourcePath);
               externallyDefinedTypes.put(typeDefinition.getFullyQualifiedTypeName(), sourcePath);
            }
            else
               typeDefinition.setSource(m_parser.getSource(fullyQualifiedName));

            // Skip all type definitions which are not detected within one
            // of the
            // configured source folders
            if (!outOfScope)
               typeDefinitions.put(fullyQualifiedName, typeDefinition);
         }
      }

      if (externallyDefinedTypes.size() > 0)
      {
         m_additionalInfo.add("Type definitions defined externally (" + externallyDefinedTypes.size() + ")");

         for (Map.Entry<String, String> entry : externallyDefinedTypes.entrySet())
         {
            m_additionalInfo.add("-- " + entry.getKey() + " -> " + entry.getValue());
         }
      }

      for (String fullyQualifiedName : types)
      {
         typeDefinition = typeDefinitions.get(fullyQualifiedName);
         // We are only interested in the classes in our source path, skip
         // the rest
         if (typeDefinition == null)
            continue;
         List<String> superClasses = null;

         if (m_parser.isClass(fullyQualifiedName)
            && (superClasses = m_parser.getSuperclasses(fullyQualifiedName)) != null)
            for (String superclassFullyQualifiedName : superClasses)
            {
               typeDefinition.addFullyQualifiedSuperclassName(adaptFullyQualifiedName(superclassFullyQualifiedName));
            }

         Set<String> references = m_parser.getReferences(fullyQualifiedName);

         if (references != null)
         {
            for (String referenceFullyQualifiedName : references)
            {
               addReference(typeDefinition, adaptFullyQualifiedName(referenceFullyQualifiedName));
            }
         }
      }

      for (Object object : typeDefinitions.values().toArray())
      {
         System.out.println(object.toString());
      }

      return typeDefinitions.values().toArray(new TypeDefinition[typeDefinitions.values().size()]);
   }

   /**
    * Scans the configured directories and searches for *.cpp, *.C, *.cc, *.cxx and *.c++ files while ignoring all files
    * specified in the exclude-compilation-unit tag using java regular expressions
    * 
    * @return Collected compilation units
    */
   private List<File> scanDirectories() throws IOException
   {
      assert m_compilationUnitsFilter == null;

      ConfigurationProviderIf configurationProvider = ProviderFactory.getInstance().getConfigurationProvider();
      m_compilationUnitsFilter = new CompilationUnitsFilter(configurationProvider.getPackageFilter(),
         configurationProvider.getCompilationUnitFilter());
      DirectoryScanner directoryScanner = new DirectoryScanner(m_compilationUnitsFilter, true);

      File[] systemIncludesDirectories = configurationProvider.getSystemIncludeDirectories();

      if (systemIncludesDirectories != null && systemIncludesDirectories.length > 0)
      {
         String[] systemIncludes = new String[systemIncludesDirectories.length];
         int counter = 0;

         for (File systemInclude : systemIncludesDirectories)
         {
            systemIncludes[counter++] = systemInclude.getCanonicalPath();
            s_logger.log(Priority.DEBUG, "System include directory -- " + systemInclude.getCanonicalPath());
         }

         SourceFileParser.s_systemIncludes = systemIncludes;
      }

      Map<String, String> systemDefines = configurationProvider.getSystemDefines();

      if (systemDefines != null && !systemDefines.isEmpty())
      {
         for (Map.Entry<String, String> systemDefine : systemDefines.entrySet())
         {
            s_logger
               .log(Priority.DEBUG, "System define -- " + systemDefine.getKey() + " -> " + systemDefine.getValue());
         }

         SourceFileParser.s_systemDefines = systemDefines;
      }

      File[] inputDirectories = configurationProvider.getInputDirectories();

      assert inputDirectories != null;
      assert inputDirectories.length > 0;

      m_directories = new String[inputDirectories.length];
      int index = 0;

      for (File directory : inputDirectories)
      {
         m_additionalInfo.add("Searching input files in directory = " + directory.getCanonicalPath());
         m_directories[index++] = directory.getCanonicalPath();
         directoryScanner.scan(directory);
      }

      return directoryScanner.getFiles();
   }

   public IIdentifierResolver getIdentifierResolver()
   {
      return new CppIdentifierResolver();
   }

}
