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

package com.valtech.source.ag.evf.codegen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import com.sun.javadoc.ClassDoc;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
abstract class CodeGenerator implements CodeGeneratorIf
{
   private static final String EVENT_IF = "de.valtech.ag.evf.EventIf";

   private static final String EVENT_BASE_CLASS = "de.valtech.ag.evf.Event";

   private static final String EVENT_PER_EMITTER_BASE_CLASS = "de.valtech.ag.evf.EventPerEmitter";

   private static final String LINE_SEPARATOR = System.getProperty("line.separator");

   private static Logger s_Logger = Logger.getLogger(CodeGenerator.class.getName());

   private static DateFormat s_DateTimeFormatter = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM,
      Locale.GERMANY);

   private static File s_TargetRootDirectory;

   private static File s_TargetDirectory;

   private static String s_PackageName;

   private static Map<OutputStreamWriter, FileOutputStream> s_OpenedFiles = new HashMap<OutputStreamWriter, FileOutputStream>();

   private static int s_NumberOfEvents;

   private static List<String> s_Packages = new ArrayList<String>();

   private static Map<String, ArrayList<ClassDoc>> s_PackageToEvents = new HashMap<String, ArrayList<ClassDoc>>();

   private static Map<String, ArrayList<ClassDoc>> s_PackageToEventsPerEmitter = new HashMap<String, ArrayList<ClassDoc>>();

   final String createDateInfo()
   {
      return ("DATE/TIME-INFO: " + s_DateTimeFormatter.format(new Date()));
   }

   final String createVersionInfo(Object obj)
   {
      assert obj != null;

      StringBuffer versionString = new StringBuffer("VERSION-INFO: ");
      String className = obj.getClass().getName();
      Package packageName = obj.getClass().getPackage();

      if (packageName != null)
      {
         boolean infoAvailable = false;

         String info = packageName.getImplementationVendor();
         if (info != null)
         {
            infoAvailable = true;
            versionString.append(info);
         }

         info = packageName.getImplementationTitle();
         if (info != null)
         {
            infoAvailable = true;
            versionString.append(' ' + info);
         }

         info = packageName.getImplementationVersion();
         if (info != null)
         {
            infoAvailable = true;
            versionString.append(' ' + info);
         }

         if (infoAvailable)
            versionString.append(" - ");
      }

      versionString.append(className);

      return versionString.toString();
   }

   static void setTargetRootDirectory(File targetRootDirectory) throws IOException
   {
      assert targetRootDirectory != null;
      assert targetRootDirectory.isDirectory();

      s_TargetRootDirectory = targetRootDirectory;
      s_Logger.info("Using target directory '" + s_TargetRootDirectory.getCanonicalPath() + "'");
   }

   static void analyzeClassDoc(ClassDoc classDoc)
   {
      assert classDoc != null;

      if (!classDoc.isClass() || classDoc.isAbstract() || classDoc.qualifiedName().equals(EVENT_BASE_CLASS)
         || classDoc.qualifiedName().equals(EVENT_PER_EMITTER_BASE_CLASS))
      {
         return;
      }

      ClassDoc superClassDoc = classDoc.superclass();

      while (superClassDoc != null && !superClassDoc.qualifiedName().equals(EVENT_IF))
      {
         if (superClassDoc.qualifiedName().equals(CodeGenerator.EVENT_BASE_CLASS))
         {
            checkEventClass(classDoc, CodeGenerator.EVENT_BASE_CLASS, s_PackageToEvents);
         }
         else if (superClassDoc.qualifiedName().equals(CodeGenerator.EVENT_PER_EMITTER_BASE_CLASS))
         {
            checkEventClass(classDoc, CodeGenerator.EVENT_PER_EMITTER_BASE_CLASS, s_PackageToEventsPerEmitter);
         }
         superClassDoc = superClassDoc.superclass();
      }
   }

   private static void checkEventClass(ClassDoc classDoc, String baseClassName, Map<String, ArrayList<ClassDoc>> map)
   {
      assert classDoc != null;
      assert baseClassName.equals(CodeGenerator.EVENT_BASE_CLASS)
         || baseClassName.equals(CodeGenerator.EVENT_PER_EMITTER_BASE_CLASS);
      assert map != null;

      s_Logger.debug(classDoc.qualifiedName() + " extends '" + baseClassName + "'");

      ArrayList<ClassDoc> eventClassDocs = null;
      String packageName = classDoc.containingPackage().name();

      if (!map.containsKey(packageName))
      {
         eventClassDocs = new ArrayList<ClassDoc>();
         map.put(packageName, eventClassDocs);
      }
      else
      {
         eventClassDocs = (ArrayList<ClassDoc>)map.get(packageName);
      }

      eventClassDocs.add(classDoc);

      if (!s_Packages.contains(packageName))
      {
         s_Packages.add(packageName);
      }

      ++s_NumberOfEvents;
   }

   private static ClassDoc[] convert(List list)
   {
      assert list != null;

      ClassDoc[] rawClassDoc = new ClassDoc[list.size()];
      for (int i = 0; i < list.size(); ++i)
         rawClassDoc[i] = (ClassDoc)list.get(i);

      return rawClassDoc;
   }

   private static ClassDoc[] getClassDoc(Map map)
   {
      assert map != null;
      assert s_PackageName != null;
      assert s_PackageName.length() > 0;

      List classDoc = (ArrayList)map.get(s_PackageName);
      if (classDoc != null)
      {
         return convert(classDoc);
      }
      else
      {
         return null;
      }
   }

   public final void generate() throws IOException
   {
      assert s_TargetRootDirectory != null;
      assert s_NumberOfEvents > 0;

      for (int i = 0; i < s_Packages.size(); ++i)
      {
         s_PackageName = (String)s_Packages.get(i);
         s_Logger.debug("Working in package '" + s_PackageName + "'");
         checkTargetDirectory();
         generate(getClassDoc(s_PackageToEvents), getClassDoc(s_PackageToEventsPerEmitter));
      }
   }

   abstract protected void generate(ClassDoc[] eventClassDoc, ClassDoc[] eventPerEmitterClassDoc) throws IOException;

   private final static void checkTargetDirectory() throws IOException
   {
      assert s_TargetRootDirectory != null;
      assert s_PackageName != null;
      assert s_PackageName.length() > 0;

      s_TargetDirectory = new File(s_TargetRootDirectory, s_PackageName.replace('.', File.separatorChar));
      if (!s_TargetDirectory.exists())
      {
         s_Logger.debug("Creating target directory '" + s_TargetDirectory.getCanonicalPath() + "'");
         s_TargetDirectory.mkdirs();
      }
      else
      {
         s_Logger.debug("Using target directory '" + s_TargetDirectory.getCanonicalPath() + "'");
      }
   }

   protected final String getPackageName()
   {
      assert s_PackageName != null;
      assert s_PackageName.length() > 0;

      return s_PackageName;
   }

   protected final String getLineSeparator(int number)
   {
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < number; i++)
      {
         sb.append(LINE_SEPARATOR);
      }

      return sb.toString();
   }

   protected static String indent(int number)
   {
      StringBuffer sb = new StringBuffer();

      for (int i = 0; i < number; i++)
      {
         sb.append("  ");
      }

      return sb.toString();
   }

   protected final void close(OutputStreamWriter writer) throws IOException
   {
      assert writer != null;
      assert s_OpenedFiles.containsKey(writer);
      assert s_OpenedFiles.get(writer) != null;

      ((FileOutputStream)s_OpenedFiles.remove(writer)).close();
   }

   protected final OutputStreamWriter open(String name) throws IOException
   {
      assert name != null;
      assert name.length() > 0;
      assert s_TargetDirectory != null;

      File file = new File(s_TargetDirectory, name);
      if (file.exists())
      {
         s_Logger.debug("Overwriting '" + file.getCanonicalPath() + "'");
      }
      else
      {
         s_Logger.debug("Creating '" + file.getCanonicalPath() + "'");
      }

      file.createNewFile();
      FileOutputStream fs = new FileOutputStream(file);
      OutputStreamWriter writer = new OutputStreamWriter(fs);

      s_OpenedFiles.put(writer, fs);

      return writer;
   }

   static int getNumberOfEvents()
   {
      return s_NumberOfEvents;
   }
}