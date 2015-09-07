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

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class BasicDirectoryScanner
{
   public static interface FilterIf
   {
      public boolean acceptFile(File file, String relPath);
   }

   private final FilterIf m_Filter;

   private final boolean m_RecursiveScan;

   private int m_CurrentRootDirectoryAsStringLength;

   public BasicDirectoryScanner(FilterIf filter, boolean recursiveScan)
   {
      assert filter != null;
      m_RecursiveScan = recursiveScan;
      m_Filter = filter;
   }

   public final void scan(File directory)
   {
      assert directory != null;
      assert directory.isDirectory();

      m_CurrentRootDirectoryAsStringLength = directory.toString().length();
      scanDirectory(directory);
   }

   public FilterIf getFilter()
   {
      return m_Filter;
   }

   private void scanDirectory(File directory)
   {
      assert directory != null;
      assert directory.isDirectory();

      scanningDirectory(directory);

      File files[] = directory.listFiles();

      if (files != null)
      {
         for (int i = 0; i < files.length; ++i)
         {
            File current = files[i];

            if (current.isDirectory())
            {
               if (m_RecursiveScan)
               {
                  scanDirectory(current);
               }
            }
            else
            {
               String parent = current.getParent();

               assert parent != null;

               String relPath = null;
               if (parent.length() == m_CurrentRootDirectoryAsStringLength)
               {
                  relPath = parent.substring(m_CurrentRootDirectoryAsStringLength);
               }
               else
               {
                  relPath = parent.substring(m_CurrentRootDirectoryAsStringLength + 1);
               }

               if (m_Filter.acceptFile(current, relPath))
               {
                  fileAccepted(current, relPath);
               }
            }
         }
      }
   }

   protected void scanningDirectory(File directory)
   {
      assert directory != null;
      assert directory.isDirectory();
   }

   protected void fileAccepted(File accepted, String relPath)
   {
      assert accepted != null;
      assert accepted.isFile();
      assert relPath != null;
   }
}
