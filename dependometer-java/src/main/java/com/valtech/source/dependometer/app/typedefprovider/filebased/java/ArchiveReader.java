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

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;

import com.valtech.source.ag.util.DirectoryScanner;
import java.util.List;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class ArchiveReader
{
   private static Logger s_Logger = Logger.getLogger(ArchiveReader.class.getName());

   static interface FilterIf
   {
      public boolean acceptPath(String name, String relPath);
   }

   static final class Entry
   {
      private final InputStream m_InputStream;

      private final String m_Source;

      Entry(InputStream in, String source)
      {
         assert in != null;
         assert source != null;
         assert source.length() > 0;
         m_InputStream = in;
         m_Source = source;
      }

      public InputStream getInputStream()
      {
         return m_InputStream;
      }

      public String getSource()
      {
         return m_Source;
      }
   }

   private static final class EntryArray
   {
      private final static int INITIAL_SIZE = 300;

      private final static int GROW_FACTOR = 2;

      private final int m_InitialSize;

      private final int m_GrowFactor;

      private int m_Size;

      private Entry[] m_Elements;

      private int m_NumberOfEntries;

      public EntryArray()
      {
         m_InitialSize = INITIAL_SIZE;
         m_GrowFactor = GROW_FACTOR;
         clear();
      }

      public void clear()
      {
         m_Elements = null;
         m_Size = m_InitialSize;
         m_NumberOfEntries = 0;
         m_Elements = new Entry[m_InitialSize];
      }

      public void add(Entry entry)
      {
         assert entry != null;
         assert m_Elements != null;

         ++m_NumberOfEntries;

         if (m_NumberOfEntries > m_Size)
         {
            m_Size = m_Size * m_GrowFactor;
            Entry[] newEntries = new Entry[m_Size];
            System.arraycopy(m_Elements, 0, newEntries, 0, m_NumberOfEntries - 1);
            m_Elements = newEntries;
         }

         m_Elements[m_NumberOfEntries - 1] = entry;
      }

      public Entry[] getEntriesArray()
      {
         assert m_Elements != null;

         Entry[] newEntries = new Entry[m_NumberOfEntries];
         System.arraycopy(m_Elements, 0, newEntries, 0, m_NumberOfEntries);
         return newEntries;
      }
   }

   private static final String[] ARCHIVE_EXTENSIONS = {
      ".jar", ".zip" };

   private final FilterIf m_Filter;

   private final DirectoryScanner m_Scanner;

   private final EntryArray m_Entries = new EntryArray();

   ArchiveReader(FilterIf filter, boolean recursive)
   {
      assert filter != null;
      m_Filter = filter;
      m_Scanner = new DirectoryScanner(ARCHIVE_EXTENSIONS, recursive);
   }

   public void scan(File directory)
   {
      m_Scanner.scan(directory);
   }

   public Entry[] getEntries() throws IOException
   {
      List<File> archives = m_Scanner.getFiles();

      for (File archive: archives)
      {
         try
         {
            readArchiveContent(archive);
         }
         catch (IOException e)
         {
            s_Logger.warn("unable to read content from archive '" + archive + "' - Exception = " + e);
         }
      }

      return m_Entries.getEntriesArray();
   }

   private final static String[] s_NameRelPath = new String[2];

   private final static String NO_REL_PATH = "";

   private String[] split(String path)
   {
      assert path != null;
      assert path.length() > 0;

      int pos = path.lastIndexOf('/');
      if (pos != -1)
      {
         s_NameRelPath[0] = path.substring(pos + 1);
         s_NameRelPath[1] = path.substring(0, pos);
      }
      else
      {
         s_NameRelPath[0] = path;
         s_NameRelPath[1] = NO_REL_PATH;
      }

      return s_NameRelPath;
   }

   private void readArchiveContent(File archive) throws IOException
   {
      assert archive != null;
      assert archive.isFile();

      String archiveInfo = archive.getCanonicalPath() + ":";

      Map<String, Integer> sizes = new HashMap<String, Integer>();
      ZipFile zipFile = new ZipFile(archive);

      Enumeration entries = zipFile.entries();

      while (entries.hasMoreElements())
      {
         ZipEntry zipEntry = (ZipEntry)entries.nextElement();
         sizes.put(zipEntry.getName(), new Integer((int)zipEntry.getSize()));
      }

      zipFile.close();

      ZipInputStream inputStream = new ZipInputStream(new BufferedInputStream(new FileInputStream(archive)));
      ZipEntry nextZipEntry = null;

      while ((nextZipEntry = inputStream.getNextEntry()) != null)
      {
         if (nextZipEntry.isDirectory())
         {
            continue;
         }

         split(nextZipEntry.getName());

         if (m_Filter.acceptPath(s_NameRelPath[0], s_NameRelPath[1]))
         {
            String source = archiveInfo + nextZipEntry.getName();
            int size = (int)nextZipEntry.getSize();
            if (size == -1)
            {
               size = sizes.get(nextZipEntry.getName()).intValue();
            }

            assert size != -1;

            byte[] bytes = new byte[size];
            int bytesRead = 0;
            int chunk = 0;

            while ((size - bytesRead) > 0)
            {
               chunk = inputStream.read(bytes, bytesRead, size - bytesRead);
               if (chunk == -1)
               {
                  break;
               }
               bytesRead += chunk;
            }

            m_Entries.add(new Entry(new ByteArrayInputStream(bytes), source));
         }
      }

      inputStream.close();
   }
}