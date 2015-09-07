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
package com.valtech.source.dependometer.app.core.metrics;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public class MetricsReader implements ContentHandler, ErrorHandler
{  
   private static final String NODE_METRICS = "metrics";

   private static final String NODE_METRIC = "metric";

   private static final String NODE_DESCRIPTION = "description";

   private static final String NODE_RELATED_METRIC = "related-metric";

   private static final String ATTRIBUTE_NAME = "name";

   private static final String ATTRIBUTE_VALUE_TYPE = "value-type";

   private static final String ATTRIBUTE_INDEX_TYPE = "index-type";

   private static final Logger s_Logger = Logger.getLogger(MetricsReader.class.getName());

   private XMLReader m_Reader;

   private boolean m_IsXMLValid = true;

   private boolean m_WarningsWhileParsing = false;

   private boolean m_FatalErrorWhileParsing = false;

   private final Stack<String> m_ElementStack = new Stack<String>();

   private final List<MetricDefinitionData> m_MetricData = new ArrayList<MetricDefinitionData>();

   private MetricDefinitionData m_CurrentMetricData;

   MetricsReader()
   {
      try
      {
         m_Reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
         m_Reader.setContentHandler(this);
         m_Reader.setErrorHandler(this);
         m_Reader.setFeature("http://xml.org/sax/features/validation", false);
         m_Reader.setFeature("http://xml.org/sax/features/namespaces", false);
      }
      catch (SAXException e)
      {
    	 s_Logger.error(e.getMessage());
         e.printStackTrace();
         System.exit(1);
      }
      catch (ParserConfigurationException e)
      {
     	 s_Logger.error(e.getMessage());
         e.printStackTrace();
         System.exit(1);
      }
      catch (FactoryConfigurationError e)
      {
     	 s_Logger.error(e.getMessage());
         e.printStackTrace();
         System.exit(1);
      }
   }

   final MetricDefinitionData[] readMetrics(InputSource input) throws IOException
   {
      assert input != null;

      try
      {
         m_Reader.parse(input);
      }
      catch (SAXException e)
      {
    	 s_Logger.fatal(e.getMessage());
         IOException ex = new IOException();
         ex.initCause(e);
         throw ex;
      }

      assert m_IsXMLValid;
      assert !m_WarningsWhileParsing;
      assert !m_FatalErrorWhileParsing;

      MetricDefinitionData[] metricData = m_MetricData.toArray(new MetricDefinitionData[0]);
      m_MetricData.clear();
      return metricData;
   }

   private void processMetricNodeAttributes(Attributes attributes) throws IOException
   {
      assert attributes != null;
      assert m_CurrentMetricData == null;
      m_CurrentMetricData = new MetricDefinitionData();

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(ATTRIBUTE_NAME))
         {
            m_CurrentMetricData.setName(value);
         }
         else if (name.equals(ATTRIBUTE_VALUE_TYPE))
         {
            m_CurrentMetricData.setValueType(value);
         }
         else if (name.equals(ATTRIBUTE_INDEX_TYPE))
         {
            m_CurrentMetricData.setIndexType(value);
         }
         else
         {
            assert false;
         }
      }
   }

   private void processRelatedMetricNodeAttributes(Attributes attributes) throws IOException
   {
      assert attributes != null;
      assert m_CurrentMetricData != null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(ATTRIBUTE_NAME))
         {
            m_CurrentMetricData.addRelatedMetricName(value);
         }
         else
         {
            assert false;
         }
      }
   }

   protected final String getParentNode()
   {
      String parentNode = null;
      int size = m_ElementStack.size();

      assert size >= 1;

      if (size >= 2)
      {
         parentNode = m_ElementStack.get(size - 2);
      }

      return parentNode;
   }

   protected final String getCurrentNode()
   {
      String currentNode = null;
      int size = m_ElementStack.size();

      assert size >= 1;

      if (size >= 2)
      {
         currentNode = m_ElementStack.get(size - 1);
      }

      return currentNode;
   }

   public final void startElement(String namespaceUri, String localName, String rawName, Attributes attributes)
      throws SAXException
   {
      assert namespaceUri != null;
      assert localName != null;
      assert rawName != null;
      assert rawName.length() > 0;

      m_ElementStack.push(rawName);

      try
      {
         if (rawName.equals(NODE_METRICS))
         {
            // Nothing to do here
         }
         else if (rawName.equals(NODE_METRIC))
         {
            processMetricNodeAttributes(attributes);
         }
         else if (rawName.equals(NODE_RELATED_METRIC))
         {
            processRelatedMetricNodeAttributes(attributes);
         }
         else if (rawName.equals(NODE_DESCRIPTION))
         {
            // Nothing to do here
         }
         else
         {
            assert false : rawName;
         }
      }
      catch (Exception ex)
      {
    	 s_Logger.fatal(ex.getMessage());
         SAXException saxEx = new SAXException("Exception caught");
         saxEx.initCause(ex);
         throw saxEx;
      }
   }

   public final void characters(char[] all, int start, int length)
   {
      String parentNode = getCurrentNode();
      if (NODE_DESCRIPTION.equals(parentNode))
      {
         assert m_CurrentMetricData != null;
         String description = new String(all, start, length);
         m_CurrentMetricData.appendDescription(description);
      }
   }

   public final void endElement(String namespaceUri, String localName, String rawName)
   {
      assert namespaceUri != null;
      assert localName != null;
      assert rawName != null;
      assert rawName.length() > 0;

      if (rawName.equals(NODE_METRIC))
      {
         assert m_CurrentMetricData != null;
         m_MetricData.add(m_CurrentMetricData);
         m_CurrentMetricData = null;
      }

      m_ElementStack.pop();
   }

   public final void error(SAXParseException saxParseException)
   {
      s_Logger.warn("XML configuration is not valid. Problem in line/column '" + saxParseException.getLineNumber()
         + "/" + saxParseException.getColumnNumber() + "' - " + saxParseException.getLocalizedMessage());
      m_IsXMLValid = false;
   }

   public final void fatalError(SAXParseException saxParseException)
   {
      s_Logger.error("Fatal Error. Problem in line/column '" + saxParseException.getLineNumber() + "/"
         + saxParseException.getColumnNumber() + "' - " + saxParseException.getLocalizedMessage());
      m_FatalErrorWhileParsing = true;
   }

   public final void warning(SAXParseException saxParseException)
   {
      s_Logger.warn("Warning. Problem in line/column '" + saxParseException.getLineNumber() + "/"
         + saxParseException.getColumnNumber() + "' - " + saxParseException.getLocalizedMessage());
      m_WarningsWhileParsing = true;
   }

   public final void endDocument()
   {
      // Unused callback
   }

   public final void setDocumentLocator(Locator locator)
   {
      // Unused callback
   }

   public final void startDocument()
   {
      // Unused callback
   }

   public final void startPrefixMapping(String arg0, String arg1)
   {
      // Unused callback
   }

   public final void endPrefixMapping(String arg0)
   {
      // Unused callback
   }

   public final void ignorableWhitespace(char[] arg0, int arg1, int arg2)
   {
      // Unused callback
   }

   public final void processingInstruction(String arg0, String arg1)
   {
      // Unused callback
   }

   public final void skippedEntity(String arg0)
   {
      // Unused callback
   }
}