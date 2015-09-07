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
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class JavaSourceParser implements SourceFileParserIf
{
   private static final String DOT_CLASS = ".class";

   private static final String DOT_CLASS_WITH_ACCESS = ".class.";

   private static final int DOT_CLASS_LENGTH = DOT_CLASS.length();

   private static final int DOT_CLASS_WITH_ACCESS_LENGTH = DOT_CLASS_WITH_ACCESS.length();

   private static final char DOT_CHAR = '.';

   private final SimplifiedJavaSourceFileReader m_Reader = new SimplifiedJavaSourceFileReader();

   private final Set<String> m_Imports = new HashSet<String>();

   private final Set<String> m_Access = new HashSet<String>();

   private final Set<String> m_DotClass = new HashSet<String>();

   private Pattern m_AssertionPattern;

   private int m_NumberOfAssertions;

   JavaSourceParser()
   {
      // Just to reduce the visibility
   }

   public void setAssertionPattern(String pattern)
   {
      assert m_AssertionPattern == null;
      assert pattern != null;
      assert pattern.length() > 0;
      m_AssertionPattern = Pattern.compile(pattern);
   }

   public int getNumberOfAssertions()
   {
      return m_NumberOfAssertions;
   }

   public String[] getImports()
   {
      return m_Imports.toArray(new String[0]);
   }

   public String[] getAccess()
   {
      return m_Access.toArray(new String[0]);
   }

   public String[] getDotClass()
   {
      return m_DotClass.toArray(new String[0]);
   }

   private void initialize()
   {
      m_Imports.clear();
      m_Access.clear();
      m_DotClass.clear();

      if (m_AssertionPattern != null)
      {
         m_NumberOfAssertions = 0;
      }
      else
      {
         m_NumberOfAssertions = -1;
      }
   }

   private void processPackageInfo()
   {
      boolean condition = true;

      do
      {
         String nextToken = m_Reader.nextToken();

         if (!m_Reader.isKeyword())
         {
            int pos = nextToken.indexOf(DOT_CLASS_WITH_ACCESS);
            if (pos != -1)
            {
               assert nextToken.length() > DOT_CLASS_WITH_ACCESS_LENGTH;
               String typeName = nextToken.substring(0, pos);
               if (typeName.length() > 0)// FIXME array (byte[].class) not recognized
               {
                  m_DotClass.add(typeName);
               }
            }
            else if (nextToken.endsWith(DOT_CLASS) && nextToken.length() > DOT_CLASS_LENGTH)
            {
               String typeName = nextToken.substring(0, nextToken.length() - DOT_CLASS_LENGTH);
               if (typeName.length() > 0)// FIXME array (byte[].class) not recognized
               {
                  m_DotClass.add(typeName);
               }
            }
         }
         else if (nextToken.equals(SimplifiedJavaSourceFileReader.KEYWORD_PACKAGE))
         {
            condition = false;
         }
      }
      while (condition);
   }

   private void processPackageStatement()
   {
      boolean condition = true;

      do
      {
         String nextToken = m_Reader.nextToken();
         assert m_Reader.isKeyword();
         if (nextToken.equals(SimplifiedJavaSourceFileReader.KEYWORD_PACKAGE))
         {
            m_Reader.nextToken();
            condition = false;
         }
         else if (nextToken.equals(SimplifiedJavaSourceFileReader.KEYWORD_IMPORT)
            || nextToken.equals(SimplifiedJavaSourceFileReader.KEYWORD_CLASS)
            || nextToken.equals(SimplifiedJavaSourceFileReader.KEYWORD_INTERFACE))
         {
            condition = false;
            m_Reader.pushBackToken(nextToken);
         }
      }
      while (condition);
   }

   private void processImportStatements()
   {
      boolean condition = true;

      do
      {
         String nextToken = m_Reader.nextToken();
         assert m_Reader.isKeyword() : "Keyword expected! Token = " + nextToken + " in source '"
            + m_Reader.getCurrentSourceFileName() + "' in line '" + m_Reader.getCurrentLineNumber() + "'";

         if (nextToken.equals(SimplifiedJavaSourceFileReader.KEYWORD_IMPORT))
         {
            String nextImport = m_Reader.nextToken();

            if (nextImport.equals(SimplifiedJavaSourceFileReader.KEYWORD_STATIC))
            {
               nextImport = m_Reader.nextToken();
            }

            m_Imports.add(nextImport);
         }
         else if (nextToken.equals(SimplifiedJavaSourceFileReader.KEYWORD_SEMICOLON))
         {
            // Ignore
         }
         else
         {
            condition = false;
            m_Reader.pushBackToken(nextToken);
         }
      }
      while (condition);
   }

   private void readTypeDefinitions()
   {
      boolean condition = true;

      do
      {
         String nextToken = m_Reader.nextToken();

         if (nextToken != null)
         {
            if (!m_Reader.isKeyword())
            {
               if (isAssertion(nextToken))
               {
                  ++m_NumberOfAssertions;
               }
               else
               {
                  int pos = nextToken.indexOf(DOT_CLASS_WITH_ACCESS);
                  if (pos != -1)
                  {
                     assert nextToken.length() > DOT_CLASS_WITH_ACCESS_LENGTH;
                     String typeName = nextToken.substring(0, pos);
                     if (typeName.length() > 0)// FIXME array (byte[].class) not recognized
                     {
                        m_DotClass.add(typeName);
                     }
                  }
                  else if (nextToken.endsWith(DOT_CLASS) && nextToken.length() > DOT_CLASS_LENGTH)
                  {
                     String typeName = nextToken.substring(0, nextToken.length() - DOT_CLASS_LENGTH);
                     if (typeName.length() > 0)// FIXME array (byte[].class) not recognized
                     {
                        m_DotClass.add(typeName);
                     }
                  }
                  else
                  {
                     pos = nextToken.indexOf(DOT_CHAR);
                     if (pos != -1 && pos != 0 && pos != nextToken.length() - 1)
                     {
                        m_Access.add(nextToken);
                     }
                  }
               }
            }
         }
         else
         {
            condition = false;
         }
      }
      while (condition);
   }

   public void parse(File sourceFile) throws IOException
   {
      assert sourceFile != null;
      initialize();
      m_Reader.read(sourceFile);
      if ("package-info.java".equals(sourceFile.getName()))
      {
         processPackageInfo();
      }
      else
      {
         processPackageStatement();
         processImportStatements();
         readTypeDefinitions();
      }
   }

   private boolean isAssertion(String word)
   {
      assert word != null;
      assert word.length() > 0;
      if (m_AssertionPattern != null)
      {
         boolean matches = m_AssertionPattern.matcher(word).matches();
         return matches;
      }

      return false;
   }
}