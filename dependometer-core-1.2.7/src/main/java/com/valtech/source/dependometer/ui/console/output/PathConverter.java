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
package com.valtech.source.dependometer.ui.console.output;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class PathConverter
{
   private static final class Entry
   {
      private final String[] m_Directories;

      private final String m_Drive;

      Entry(File directory)
      {
         assert directory != null;
         assert directory.isDirectory();

         if (File.separatorChar == '\\')
         {
            String dirAsString = directory.getAbsolutePath();
            int pos = dirAsString.indexOf(File.separatorChar);
            assert pos != -1;
            m_Drive = dirAsString.substring(0, pos);
         }
         else
         {
            m_Drive = null;
         }

         Stack<String> directories = new Stack<String>();
         File tempDir = directory;

         do
         {
            String name = tempDir.getName();
            directories.push(name);
            tempDir = tempDir.getParentFile();
         }
         while (tempDir != null);

         m_Directories = new String[directories.size()];
         for (int i = 0; i < m_Directories.length; i++)
         {
            assert !directories.isEmpty();
            m_Directories[i] = directories.pop();
         }
      }

      String getDrive()
      {
         return m_Drive;
      }

      String[] getDirectories()
      {
         return m_Directories;
      }
   }

   private static PathConverter m_Instance;

   private Map<File, Entry> m_DirectoryToEntry = new HashMap<File, Entry>();

   private final Stack<String> m_Stack = new Stack<String>();

   private PathConverter()
   {
      // Just to make the ctor unaccessible
   }

   public static PathConverter getInstance()
   {
      if (m_Instance == null)
      {
         m_Instance = new PathConverter();
      }

      return m_Instance;
   }

   public String getRelativePath(File directory, File file)
   {
      assert directory != null;
      assert directory.isDirectory();
      assert file != null;
      assert file.isFile();

      Entry entry = m_DirectoryToEntry.get(directory);
      if (entry == null)
      {
         entry = new Entry(directory);
         m_DirectoryToEntry.put(directory, entry);
      }

      String drive = entry.getDrive();
      if (drive != null)
      {
         String fileAsString = file.getAbsolutePath();
         int pos = fileAsString.indexOf(File.separatorChar);
         assert pos != -1;

         if (!drive.equals(fileAsString.substring(0, pos)))
         {
            return file.getAbsolutePath();
         }
      }

      String[] directories = entry.getDirectories();

      File tempDir = file.getParentFile();
      do
      {
         String name = tempDir.getName();
         m_Stack.push(name);
         tempDir = tempDir.getParentFile();
      }
      while (tempDir != null);

      boolean equals = true;
      int numberOfEqualDirectories = 0;

      do
      {
         if (!m_Stack.empty() && numberOfEqualDirectories < directories.length - 1)
         {
            String nextDirectory = directories[numberOfEqualDirectories];
            if (nextDirectory.equals(m_Stack.peek()))
            {
               ++numberOfEqualDirectories;
               m_Stack.pop();
            }
            else
            {
               equals = false;
            }
         }
         else
         {
            equals = false;
         }
      }
      while (equals);

      StringBuffer relPath = new StringBuffer();
      for (int i = 0; i < directories.length - numberOfEqualDirectories; i++)
      {
         relPath.append("..");
         relPath.append(File.separatorChar);
      }

      while (!m_Stack.empty())
      {
         relPath.append(m_Stack.pop());
         relPath.append(File.separatorChar);
      }

      String fileName = file.getName();
      relPath.append(fileName);
      String rel = relPath.toString();
      m_Stack.clear();
      return rel;
   }
}