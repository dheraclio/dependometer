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
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;

import com.valtech.source.ag.util.BasicDirectoryScanner;
import com.valtech.source.dependometer.app.core.provider.CompilationUnitFilterIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;

/**
 * Filters the compilation units supposed to be considered according to the configured directories and exclusion
 * patterns
 * 
 * @author Carsten Kaiser (carsten.kaiser@valtech.de)
 * @version 1.0
 */
final class CompilationUnitsFilter implements BasicDirectoryScanner.FilterIf
{
   private static final char DOT_CHAR = '.';

   private final CompilationUnitFilterIf m_compilationUnitFilter;

   private final Set<String> m_excludedCompilationUnits = new TreeSet<String>();

   private final Pattern m_matchingPattern = Pattern
      .compile("(.*\\.cpp$)|(.*\\.C$)|(.*\\.cc$)|(.*\\.cxx$)|(.*\\.c++$)");

   private final PackageFilterIf m_packageFilter;

   /**
    * Creates a new compilation unit filter which uses the given package and compilation unit filters to accept only
    * those compilation units supposed to be considered
    * 
    * @param packageFilter Package filter
    * @param compilationUnitFilter Compilation unit filter
    */
   CompilationUnitsFilter(PackageFilterIf packageFilter, CompilationUnitFilterIf compilationUnitFilter)
   {
      assert packageFilter != null;
      assert compilationUnitFilter != null;

      m_packageFilter = packageFilter;
      m_compilationUnitFilter = compilationUnitFilter;
   }

   /**
    * Checks whether the given name represents a compilation unit supposed to be considered according to the configured
    * inclusion and exclusion patterns
    * 
    * @param name File name
    * @param relativePath Relative path with forward or backward slashes
    */
   private boolean accept(String name, String relativePath)
   {
      assert name != null;
      assert name.length() > 0;
      assert relativePath != null;

      if (m_matchingPattern.matcher(name).matches())
      {
         String convertedPath = null;

         if (relativePath.length() > 0)
         {
            convertedPath = relativePath.replace('/', DOT_CHAR);
            convertedPath = convertedPath.replace('\\', DOT_CHAR);
         }

         if (convertedPath == null || m_packageFilter.match(convertedPath))
         {
            // For the time being do not provide any path information
            // until the fully qualified name retrieval is implemented
            if (!m_compilationUnitFilter.exclude(name, ""))
            {
               return true;
            }
            else
            {
               m_excludedCompilationUnits.add(name + (convertedPath.length() > 0 ? "@" + convertedPath : ""));
            }
         }
         else
         {
            m_excludedCompilationUnits.add(name + (convertedPath.length() > 0 ? "@" + convertedPath : ""));
         }
      }

      return false;
   }

   /**
    * @see de.valtech.ag.util.BasicDirectoryScanner.FilterIf#acceptFile(File, String)
    */
   public boolean acceptFile(File file, String relativePath)
   {
      assert file != null;
      assert !file.isDirectory();
      assert relativePath != null;

      return accept(file.getName(), relativePath);
   }

   /**
    * Returns the number of skipped compilation units
    * 
    * @return Number of skipped compilation units
    */
   int getNumberOfSkippedCompilationUnits()
   {
      return m_excludedCompilationUnits.size();
   }

   /**
    * Returns the names of all skipped compilation units
    * 
    * @return Skipped compilation units
    */
   String[] getSkippedCompilationUnits()
   {
      return m_excludedCompilationUnits.toArray(new String[0]);
   }
}