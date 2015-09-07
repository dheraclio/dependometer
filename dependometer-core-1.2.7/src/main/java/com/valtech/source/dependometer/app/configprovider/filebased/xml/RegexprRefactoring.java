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
package com.valtech.source.dependometer.app.configprovider.filebased.xml;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import com.valtech.source.dependometer.app.core.provider.IdentifierUtility;
import com.valtech.source.dependometer.app.core.provider.RefactoringIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class RegexprRefactoring implements RefactoringIf
{
   private static final String DOT = ".";

   private final Pattern m_Pattern;

   private final String m_TargetPackage;

   private final Set<String> m_RefactoredTypes = new HashSet<String>();

   public RegexprRefactoring(String pattern, String targetPackage)
   {
      assert pattern != null;
      assert pattern.length() > 0;
      assert targetPackage != null;
      assert !targetPackage.endsWith(DOT);
      m_Pattern = Pattern.compile(pattern);
      m_TargetPackage = targetPackage;
   }

   public boolean match(String typeName)
   {
      assert typeName != null;
      assert typeName.length() > 0;
      typeName = IdentifierUtility.getCompilationUnitName(typeName);
      return m_Pattern.matcher(typeName).matches();
   }

   public String getTargetPackage()
   {
      return m_TargetPackage;
   }

   public String refactor(String typeName)
   {
      assert match(typeName);
      String refactored = null;

      int pos = typeName.lastIndexOf(DOT);
      if (pos != -1)
      {
         refactored = m_TargetPackage + typeName.substring(pos);
      }
      else
      {
         refactored = m_TargetPackage + typeName;
      }

      m_RefactoredTypes.add(typeName);
      return refactored;
   }

   public String[] getRefactoredTypes()
   {
      return m_RefactoredTypes.toArray(new String[0]);
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
      else if (obj instanceof RegexprRefactoring)
      {
         return (m_Pattern.equals(((RegexprRefactoring)obj).m_Pattern));
      }
      else
      {
         return false;
      }
   }

   public int hashCode()
   {
      return m_Pattern.hashCode();
   }

   public String getRefactoringInformation()
   {
      StringBuffer buffer = new StringBuffer();
      buffer.append(m_Pattern.pattern());
      buffer.append(" -> ");
      buffer.append(getTargetPackage());
      return buffer.toString();
   }
}