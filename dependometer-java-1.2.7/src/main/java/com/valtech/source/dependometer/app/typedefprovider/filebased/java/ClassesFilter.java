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
import java.util.Set;
import java.util.TreeSet;

import com.valtech.source.dependometer.app.core.provider.CompilationUnitFilterIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class ClassesFilter implements com.valtech.source.ag.util.BasicDirectoryScanner.FilterIf,
   com.valtech.source.dependometer.app.typedefprovider.filebased.java.ArchiveReader.FilterIf
{
   private static final String DEFAULT_PACKAGE = "default";

   private static final String CLASS_EXTENSION = ".class";

   private static final char DOT_CHAR = '.';

   private static final char NESTED = '$';

   private final Set<String> m_ExcludedClassFiles = new TreeSet<String>();

   private final PackageFilterIf m_PackageFilter;

   private final CompilationUnitFilterIf m_CompilationUnitFilter;

   ClassesFilter(PackageFilterIf packageFilter, CompilationUnitFilterIf compilationUnitFilter)
   {
      assert packageFilter != null;
      assert compilationUnitFilter != null;
      m_PackageFilter = packageFilter;
      m_CompilationUnitFilter = compilationUnitFilter;
   }

   public boolean acceptFile(File file, String relPath)
   {
      assert file != null;
      assert !file.isDirectory();
      assert relPath != null;
      return accept(file.getName(), relPath);
   }

   public boolean acceptPath(String name, String relPath)
   {
      return accept(name, relPath);
   }

   /**
    * @param name - file name
    * @param relPath - relative path with forward or backward slashes
    */
   private boolean accept(String name, String relPath)
   {
      assert name != null;
      assert name.length() > 0;
      assert relPath != null;

      if (name.endsWith(CLASS_EXTENSION))
      {
         String fullyQualifiedName = null;
         if (relPath.length() > 0)
         {
            fullyQualifiedName = relPath.replace('/', DOT_CHAR);
            fullyQualifiedName = fullyQualifiedName.replace('\\', DOT_CHAR);
         }
         else
         {
            fullyQualifiedName = DEFAULT_PACKAGE;
         }
         if (m_PackageFilter.match(fullyQualifiedName))
         {
            if (!m_CompilationUnitFilter.exclude(getCompilationUnitName(name), fullyQualifiedName))
            {
               return true;
            }
            else
            {
               m_ExcludedClassFiles.add(fullyQualifiedName + DOT_CHAR + name);
            }
         }
         else
         {
            m_ExcludedClassFiles.add(fullyQualifiedName + DOT_CHAR + name);
         }
      }

      return false;
   }

   private String getCompilationUnitName(String className)
   {
      assert className != null;
      assert className.length() > 0;
      assert className.endsWith(CLASS_EXTENSION);

      String compilationUnitName = className.substring(0, className.length() - 6);
      int pos = compilationUnitName.indexOf(NESTED);
      if (pos != -1)
      {
         compilationUnitName = className.substring(0, pos);
      }

      return compilationUnitName;
   }

   int getNumberOfSkippedClasses()
   {
      return m_ExcludedClassFiles.size();
   }

   String[] getFullyQualifiedSkippedClassNames()
   {
      return m_ExcludedClassFiles.toArray(new String[0]);
   }
}