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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author Dietmar Menges (Dietmar.Menges@valtech.de)
 */
final class PackageDescriptionReader implements ContentHandler, ErrorHandler, EntityResolver
{
   static private Logger log = Logger.getLogger(PackageDescriptionReader.class.getName());
   private static final String PACKAGE = "package";

   private static final String DEPENDS_UPON = "dependsUpon";

   private static final String NAME = "name";

   private static final String DTD = "PackageDescription.dtd";

   private static XMLReader s_Reader;

   private boolean m_IsXMLValid = true;

   private boolean m_WarningsWhileParsing = false;

   private boolean m_FatalErrorWhileParsing = false;

   private boolean m_IsDoctypeEntryMissing = true;

   private File m_PackageDescription;

   private String m_PackageName;

   private String description;

   private List<String> m_DependsUponPackages = new ArrayList<String>();

   private List<String> m_Warnings = new ArrayList<String>();

   static
   {
      try
      {
         s_Reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
         s_Reader.setFeature("http://xml.org/sax/features/validation", true);
         s_Reader.setFeature("http://xml.org/sax/features/namespaces", false);
      }
      catch (SAXException e)
      {
    	log.fatal(e.getMessage());
         e.printStackTrace();
         System.exit(1);
      }
      catch (ParserConfigurationException e)
      {
      	log.fatal(e.getMessage());
         e.printStackTrace();
         System.exit(1);
      }
      catch (FactoryConfigurationError e)
      {
      	 log.fatal(e.getMessage());
         e.printStackTrace();
         System.exit(1);
      }
   }

   PackageDescriptionReader(File dependencyFile)
   {
      assert dependencyFile != null;
      assert dependencyFile.isFile();

      try
      {
         m_PackageDescription = dependencyFile.getAbsoluteFile();
         s_Reader.setEntityResolver(this);
         s_Reader.setContentHandler(this);
         s_Reader.setErrorHandler(this);

         s_Reader.parse(dependencyFile.toString());
      }
      catch (IOException ex)
      {
         ex.printStackTrace();
         System.exit(1);
      }
      catch (SAXException ex)
      {
         ex.printStackTrace();
         System.exit(1);
      }
   }

   boolean wasSuccessfullyParsed()
   {
      return m_IsXMLValid && !m_WarningsWhileParsing && !m_FatalErrorWhileParsing;
   }

   String[] getWarnings()
   {
      return m_Warnings.toArray(new String[0]);
   }

   public void startElement(java.lang.String uri, java.lang.String localName, java.lang.String qName,
      Attributes attributes)
   {
      assert uri != null;
      assert localName != null;
      assert qName != null;
      assert attributes != null;

      if (PACKAGE.equals(qName))
      {
         m_PackageName = attributes.getValue(NAME);
      }
      else if (DEPENDS_UPON.equals(qName))
      {
         String packageName = attributes.getValue(PACKAGE);
         m_DependsUponPackages.add(packageName);
      }
      else
      {
         assert false;
      }
   }

   String getPackageName()
   {
      return m_PackageName;
   }

   String[] dependsUponPackages()
   {
      return m_DependsUponPackages.toArray(new String[0]);
   }

   String getDescription()
   {
      return description;
   }

   File getPackageDescription()
   {
      return m_PackageDescription;
   }

   public void endDocument()
   {
      if (m_IsDoctypeEntryMissing)
      {
         m_Warnings
            .add("Required '<!DOCTYPE package SYSTEM \"PackageDescription.dtd\">' entry in xml configuration file missing");
      }
   }

   public void error(SAXParseException saxParseException)
   {
      m_Warnings.add("XML configuration is not valid. Problem in line/column '" + saxParseException.getLineNumber()
         + "/" + saxParseException.getColumnNumber() + "' - " + saxParseException.getLocalizedMessage());
      m_IsXMLValid = false;
   }

   public void fatalError(SAXParseException saxParseException)
   {
      m_Warnings.add("Fatal Error - " + saxParseException.getLocalizedMessage());
      m_FatalErrorWhileParsing = true;
   }

   public void warning(SAXParseException saxParseException)
   {
      m_Warnings.add(saxParseException.getLocalizedMessage());
      m_WarningsWhileParsing = true;
   }

   public InputSource resolveEntity(String publicId, String systemId)
   {
      InputSource dtd = null;

      if (systemId != null && systemId.endsWith(DTD))
      {
         InputStream input = getClass().getResourceAsStream(DTD);
         assert input != null;
         dtd = new InputSource(input);
         m_IsDoctypeEntryMissing = false;
      }

      return dtd;
   }

    public void characters(char[] all, int start, int length)
    {
       // String() copies the char array => no problem here
      description = new String(all, start, length);  // NOSONAR
    }

   public void endElement(String arg0, String arg1, String arg2)
   {
      // Unused callback
   }

   public void endPrefixMapping(String arg0)
   {
      // Unused callback
   }

   public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
   {
      // Unused callback
   }

   public void processingInstruction(String arg0, String arg1)
   {
      // Unused callback
   }

   public void setDocumentLocator(Locator arg0)
   {
      // Unused callback
   }

   public void skippedEntity(String arg0)
   {
      // Unused callback
   }

   public void startDocument()
   {
      // Unused callback
   }

   public void startPrefixMapping(String arg0, String arg1)
   {
      // Unused callback
   }
}