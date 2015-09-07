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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract class HtmlDocument
{
   final static String HTML_FILE_EXTENSION = ".html";

   protected final static String COLOR_WHITE = "#FFFFFF";

   protected final static String COLOR_RED = "#FF3300";

   protected final static String COLOR_ORANGE = "#FF9900";

   protected final static String COLOR_PURPLE = "#006699";

   private final static String BEGIN = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"><link rel=\"stylesheet\" type=\"text/css\" href=\"stylesheet.css\"></head><body>";

   private final static String END = "</body></html><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br><br>";

   private final File m_Directory;

   private File m_File;

   private String m_FileName;

   private PrintWriter m_Writer;

   protected HtmlDocument(File directory)
   {
      assert directory != null;
      assert directory.isDirectory();
      m_Directory = directory;
   }

   protected File getDirectory()
   {
      return m_Directory;
   }

   protected synchronized void open(String fileName) throws IOException
   {
      assert fileName != null;
      assert fileName.length() > 0;

      m_FileName = fileName;
      m_File = new File(m_Directory, fileName);
      m_File.createNewFile();
      m_Writer = new PrintWriter(new BufferedWriter(new FileWriter(m_File)));
      getWriter().println(BEGIN);
   }

   protected synchronized void close() throws IOException
   {
      getWriter().println(END);
      m_Writer.flush();
      m_Writer.close();
      m_Writer = null;
   }

   protected synchronized final PrintWriter getWriter()
   {
      assert m_Writer != null;
      return m_Writer;
   }

   protected synchronized final String getFileName()
   {
      return m_FileName;
   }

   final void writeAnchoredTitle(String anchor, String title)
   {
      assert anchor != null;
      assert anchor.length() > 0;
      assert title != null;
      assert title.length() > 0;

      PrintWriter writer = getWriter();
      writer.print("<a name=\"");
      writer.print(anchor);
      writer.print("\"></a><h2>");
      writer.print(title);
      writer.println("</h2>");
   }

   final void writeAnchoredCenteredTitle(String anchor, String title)
   {
      assert anchor != null;
      assert anchor.length() > 0;
      assert title != null;
      assert title.length() > 0;

      PrintWriter writer = getWriter();
      writer.print("<a name=\"");
      writer.print(anchor);
      writer.print("\"></a><h2><center>");
      writer.print(title);
      writer.println("</center></h2>");
   }

   final void writeAnchor(String anchor)
   {
      assert anchor != null;
      assert anchor.length() > 0;

      PrintWriter writer = getWriter();
      writer.print("<a name=\"");
      writer.print(anchor);
      writer.println("\"/>");
   }

   final void writeHRef(String ref, String name)
   {
      try
      {
         getWriter().println(getHRef(ref, name));
      }
      catch (Throwable e)
      {
         // diagram can fail with NAN
         // happened on an artificial sample only
         getWriter().println(e.getMessage());
      }
   }

   final String getHRef(String ref, String name)
   {
      assert ref != null;
      assert ref.length() > 0;
      assert name != null;
      assert name.length() > 0;
      StringBuffer buffer = new StringBuffer("<a href=\"");
      buffer.append(ref);
      buffer.append("\">");
      buffer.append(name);
      buffer.append("</a>");
      return buffer.toString();
   }

   final void writeHRefWithTarget(String ref, String name, String target)
   {
      assert ref != null;
      assert ref.length() > 0;
      assert name != null;
      assert name.length() > 0;
      assert target != null;
      assert target.length() > 0;

      PrintWriter writer = getWriter();
      writer.print("<a href=\"");
      writer.print(ref);
      writer.print("\" target=\"");
      writer.print(target);
      writer.print("\">");
      writer.print(name);
      writer.println("</a>");
   }

   final void writeHRef(String ref, String name, String fontColor)
   {
      assert ref != null;
      assert ref.length() > 0;
      assert name != null;
      assert name.length() > 0;
      assert fontColor != null;
      assert fontColor.length() > 0;

      PrintWriter writer = getWriter();
      writer.print("<a href=\"");
      writer.print(ref);
      writer.print("\"><font color=\"");
      writer.print(fontColor);
      writer.print("\">");
      writer.print(name);
      writer.println("</font></a>");
   }
}