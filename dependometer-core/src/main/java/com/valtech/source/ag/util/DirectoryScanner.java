/*
 * Valtech Public L I C E N S E (VPL) 1.0.2
 * 
 * dependometer Copyright � 2007 Valtech GmbH
 * 
 * dependometer software is made available free of charge under the following conditions.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * 1.1.All copies and redistributions of source code must retain the above copyright notice, this list of conditions and
 * the following disclaimer.
 * 
 * 1.2.Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * 1.3.The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * This product includes software developed by Valtech http://www.valtech.de/. This acknowledgement must appear in the
 * software itself, if and wherever such third-party acknowledgments normally appear.
 * 
 * 1.4.The names "Valtech" and "dependometer" must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact kmc@valtech.de <mailto:kmc@valtech.de>
 * 
 * BECAUSE THIS SOFTWARE IS LICENSED FREE OF CHARGE IT IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL VALTECH GMBH OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. LEGAL LIABILITY PROVIDED UNDER GERMAN LAW FOR
 * INTENDED DAMAGES, BAD FAITH OR GROSS NEGLIGENCE REMAINS UNAFFECTED.
 */

package com.valtech.source.ag.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class DirectoryScanner extends BasicDirectoryScanner
{
   private static class FileNameFilterWindows implements FilterIf
   {
      private String[] patterns;

      FileNameFilterWindows(String[] patterns)
      {
         assert AssertionUtility.checkArray(patterns);
         assert patterns.length > 0;

         this.patterns = new String[patterns.length];
         for (int i = 0; i < this.patterns.length; ++i)
         {
            this.patterns[i] = patterns[i].toLowerCase();
         }
      }

      public boolean acceptFile(File file, String relPath)
      {
         assert file != null;
         assert !file.isDirectory();
         assert relPath != null;

         String fileName = file.getName();

         for (int i = 0; i < patterns.length; ++i)
         {
            if (fileName.toLowerCase().endsWith( patterns[i]))
            {
               return true;
            }
         }

         return false;
      }
   }

   private static class FileNameFilterUnix implements FilterIf
   {
      private String[] patterns;

      FileNameFilterUnix(String[] patterns)
      {
         assert AssertionUtility.checkArray(patterns);
         assert patterns.length > 0;
         this.patterns = Arrays.copyOf( patterns, patterns.length );
      }

      public boolean acceptFile(File file, String relPath)
      {
         assert file != null;
         assert !file.isDirectory();
         assert relPath != null;

         String fileName = file.getName();

         for (int i = 0; i < patterns.length; ++i)
         {
            if (fileName.endsWith( patterns[i]))
            {
               return true;
            }
         }

         return false;
      }
   }

   private List<String> relPathsAsString = new ArrayList<String>( 1000 );

   private List<File> directories = new ArrayList<File>(1000);

   private List<File> files = new ArrayList<File>(2000);

   private static FilterIf createFilter(String[] endsWith)
   {
      assert AssertionUtility.checkArray(endsWith);

      if (File.separatorChar == '\\')
      {
         return new FileNameFilterWindows(endsWith);
      }
      else
      {
         return new FileNameFilterUnix(endsWith);
      }
   }

   public DirectoryScanner(String[] endsWith, boolean recursiveScan)
   {
      super(createFilter(endsWith), recursiveScan);
   }

   public DirectoryScanner(FilterIf filter, boolean recursiveScan)
   {
      super(filter, recursiveScan);
   }

   public void clear()
   {
      directories.clear();
      files.clear();
   }

   protected void scanningDirectory(File directory)
   {
      assert directory != null;
      directories.add( directory );
   }

   protected void fileAccepted(File accepted, String relPath)
   {
      assert accepted != null;
      assert relPath != null;

      files.add( accepted );
      relPathsAsString.add( relPath );
   }

   public List<File> getDirectories()
   {
      return directories;
   }

   public int getNumberOfDirectories()
   {
      return directories.size();
   }

   public List<File> getFiles()
   {
      return files;
   }

   public int getNumberOfFiles()
   {
      return files.size();
   }

   public List<String> getRelPathsAsString()
   {
       // TODO make unmodifiable ?
      return relPathsAsString;
   }
}