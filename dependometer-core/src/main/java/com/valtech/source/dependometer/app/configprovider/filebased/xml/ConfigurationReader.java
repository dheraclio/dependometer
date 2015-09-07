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
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Stack;

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
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public class ConfigurationReader implements ContentHandler, ErrorHandler, EntityResolver, LocationInfoIf
{
   protected static final String DTD = "Configuration.dtd";

   protected static final String NAME_ATTRIBUTE = "name";

   private static final String PROJECT_NODE = "project";

   private static final String INPUT_NODE = "input";

   private static final String SYSTEM_INCLUDE_PATH_NODE = "system-include-path";

   private static final String SYSTEM_DEFINE_NODE = "system-define";

   private static final String INCLUDE_PACKAGE_NODE = "include-package";

   private static final String EXCLUDE_PACKAGE_NODE = "exclude-package";

   private static final String EXCLUDE_COMPILATION_UNIT_NODE = "exclude-compilation-unit";

   private static final String VERTICAL_SLICES_NODE = "vertical-slices";

   private static final String VERTICAL_SLICES_EXCLUDE_NODE = "exclude-subsystem";

   private static final String LISTENER_NODE = "listener";

   private static final String IGNORE_NODE = "ignore";

   private static final String SKIP_NODE = "skip";

   private static final String REFACTOR_NODE = "refactor";

   private static final String UPPER_THRESHOLD_NODE = "upper-threshold";

   private static final String LOWER_THRESHOLD_NODE = "lower-threshold";

   private static final String LAYER_NODE = "layer";

   private static final String SUBSYSTEM_NODE = "subsystem";

   private static final String DEPENDS_UPON_NODE = "depends-upon";

   private static final String VALUE_ATTRIBUTE = "value";

   private static final String CHECK_LAYER_DEPENDENCIES_ATTRIBUTE = "checkLayerDependencies";

   private static final String CHECK_VERTICAL_SLICE_DEPENDENCIES_ATTRIBUTE = "checkDependencies";

   private static final String CHECK_SUBSYSTEM_DEPENDENCIES_ATTRIBUTE = "checkSubsystemDependencies";

   private static final String CHECK_PACKAGE_DEPENDENCIES_ATTRIBUTE = "checkPackageDependencies";

   private static final String CUMULATE_LAYER_DEPENDENCIES_ATTRIBUTE = "cumulateLayerDependencies";

   private static final String CUMULATE_VERTICAL_SLICE_DEPENDENCIES_ATTRIBUTE = "cumulateDependencies";

   private static final String CUMULATE_SUBSYSTEM_DEPENDENCIES_ATTRIBUTE = "cumulateSubsystemDependencies";

   private static final String CUMULATE_PACKAGE_DEPENDENCIES_ATTRIBUTE = "cumulatePackageDependencies";

   private static final String CUMULATE_COMPILATION_UNIT_DEPENDENCIES_ATTRIBUTE = "cumulateCompilationUnitDependencies";

   private static final String CUMULATE_TYPE_DEPENDENCIES_ATTRIBUTE = "cumulateTypeDependencies";

   private static final String NUMBER_OF_CYCLE_ANALYSIS_FEEDBACK_ON_CONSOLE = "numberOfCycleAnalysisProgressFeedbackOnConsole";

   private static final String NUMBER_OF_CYCLES_FEEDBACK_ON_CONSOLE = "numberOfCyclesFeedbackOnConsole";

   private static final String MAX_LAYER_CYCLES_ATTRIBUTE = "maxLayerCycles";

   private static final String MAX_VERTICAL_SLICE_CYCLES_ATTRIBUTE = "maxCycles";

   private static final String MAX_SUBSYSTEM_CYCLES_ATTRIBUTE = "maxSubsystemCycles";

   private static final String MAX_PACKAGE_CYCLES_ATTRIBUTE = "maxPackageCycles";

   private static final String MAX_COMPILATION_UNIT_CYCLES_ATTRIBUTE = "maxCompilationUnitCycles";

   private static final String MAX_TYPE_CYCLES_ATTRIBUTE = "maxTypeCycles";

   private static final String DIR_ATTRIBUTE = "dir";

   private static final String ARGS_ATTRIBUTE = "args";

   private static final String CLASS_ATTRIBUTE = "class";

   private static final String PREFIX_ATTRIBUTE = "prefix";

   private static final String FROM_TYPE_ATTRIBUTE = "fromType";

   private static final String TO_TYPE_ATTRIBUTE = "toType";

   private static final String COMPILATION_UNIT_ATTRIBUTE = "compilationUnit";

   private static final String TO_PACKAGE_ATTRIBUTE = "toPackage";

   private static final String ASSERTION_NODE = "assertion";

   private static final String PATTERN_ATTRIBUTE = "pattern";

   private static final String SYMBOL_ATTRIBUTE = "symbol";

   private static final String MACRO_ATTRIBUTE = "macro";

   private static final String IGNORE_PHYSICAL_STRUCTURE_ATTRIBUTE = "ignorePhysicalStructure";

   private static final String FILE_ENCODING_ATTRIBUTE = "fileEncoding";
   
   private static final String TIMEOUT_MINUTES_ATTRIBUTE = "timeoutMinutes";

   private Logger log = Logger.getLogger(ConfigurationReader.class.getName());

   private XMLReader reader;

   private Locator m_Locator;

   private boolean m_IsDoctypeEntryMissing = true;

   private boolean m_IsXMLValid = true;

   private boolean m_WarningsWhileParsing = false;

   private boolean m_FatalErrorWhileParsing = false;

   private Stack<String> m_ElementStack = new Stack<String>();

   private ProjectNode m_ProjectNode;

   private LayerNode m_CurrentLayer;

   private SubsystemNode m_CurrentSubsystem;

   protected ProjectNode createProjectNode()
   {
      assert m_ProjectNode == null;
      return new ProjectNode();
   }

   protected final ProjectNode getProjectNode()
   {
      assert m_ProjectNode != null;
      return m_ProjectNode;
   }

   public ConfigurationReader()
   {
      try
      {
         reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
         reader.setEntityResolver(this);
         reader.setContentHandler(this);
         reader.setErrorHandler(this);
         reader.setFeature("http://xml.org/sax/features/validation", true);
         reader.setFeature("http://xml.org/sax/features/namespaces", false);
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

   public final ProjectNode readConfiguration(File config) throws Exception
   {
      assert config != null;
      assert log != null;
      ProjectNode result = null;

      if (!config.isFile())
      {
         throw new IOException(config.toString() + " is not a file");
      }

      config = config.getCanonicalFile();
      log.info("using config file '" + config + "'");
      Node.setBaseDirectory(config.getParentFile());
      Node.setLocationInfoProvider(this);
      m_ProjectNode = createProjectNode();
      getProjectNode().setSourceFile(config);

      reader.parse(config.toURI().toString());

      if (m_IsXMLValid && !m_WarningsWhileParsing && !m_FatalErrorWhileParsing)
      {
         result = getProjectNode();
      }

      return result;
   }

   private void processProjectNodeAttributes(Attributes attributes)
   {
      assert attributes != null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            getProjectNode().setProjectName(value);
         }
         else if (name.equals(CHECK_LAYER_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCheckLayerDependencies(value);
         }
         else if (name.equals(CHECK_SUBSYSTEM_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCheckSubsystemDependencies(value);
         }
         else if (name.equals(CHECK_PACKAGE_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCheckPackageDependencies(value);
         }
         else if (name.equals(NUMBER_OF_CYCLES_FEEDBACK_ON_CONSOLE))
         {
            getProjectNode().setCycleFeedback(value);
         }
         else if (name.equals(NUMBER_OF_CYCLE_ANALYSIS_FEEDBACK_ON_CONSOLE))
         {
            getProjectNode().setCycleAnalysisProgressFeedback(value);
         }
         else if (name.equals(MAX_LAYER_CYCLES_ATTRIBUTE))
         {
            getProjectNode().setMaxLayerCycles(value);
         }
         else if (name.equals(MAX_SUBSYSTEM_CYCLES_ATTRIBUTE))
         {
            getProjectNode().setMaxSubsystemCycles(value);
         }
         else if (name.equals(MAX_PACKAGE_CYCLES_ATTRIBUTE))
         {
            getProjectNode().setMaxPackageCycles(value);
         }
         else if (name.equals(MAX_COMPILATION_UNIT_CYCLES_ATTRIBUTE))
         {
            getProjectNode().setMaxCompilationUnitCycles(value);
         }
         else if (name.equals(MAX_TYPE_CYCLES_ATTRIBUTE))
         {
            getProjectNode().setMaxTypeCycles(value);
         }
         else if (name.equals(CUMULATE_LAYER_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCumulateLayerDependencies(value);
         }
         else if (name.equals(CUMULATE_VERTICAL_SLICE_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCumulateVerticalSliceDependencies(value);
         }
         else if (name.equals(CUMULATE_SUBSYSTEM_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCumulateSubsystemDependencies(value);
         }
         else if (name.equals(CUMULATE_PACKAGE_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCumulatePackageDependencies(value);
         }
         else if (name.equals(CUMULATE_COMPILATION_UNIT_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCumulateCompilationUnitDependencies(value);
         }
         else if (name.equals(CUMULATE_TYPE_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCumulateTypeDependencies(value);
         }
         else if (name.equals(IGNORE_PHYSICAL_STRUCTURE_ATTRIBUTE))
         {
            getProjectNode().setIgnorePhysicalStructure(value);
         }
         else if (name.equals(FILE_ENCODING_ATTRIBUTE))
         {
            getProjectNode().setFileEncoding(Charset.forName(value));
         }
         else if (name.equals(TIMEOUT_MINUTES_ATTRIBUTE))
         {
            getProjectNode().setTimeoutMinutes(value);
         }         
         else
         {
            assert false;
         }
      }
   }

   private void processInputNodeAttributes(Attributes attributes) throws IOException
   {
      assert attributes != null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(DIR_ATTRIBUTE))
         {
            getProjectNode().processInputNode(value);
         }
         else
         {
            assert false;
         }
      }
   }

   private void processSystemIncludePathNodeAttributes(Attributes attributes) throws IOException
   {
      assert attributes != null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(DIR_ATTRIBUTE))
         {
            getProjectNode().processSystemIncludePathNode(value);
         }
         else
         {
            assert false;
         }
      }
   }

   private void processSystemDefineNodeAttributes(Attributes attributes) throws IOException
   {
      assert attributes != null;

      String symbol = null;
      String macro = null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(SYMBOL_ATTRIBUTE))
            symbol = value;
         else if (name.equals(MACRO_ATTRIBUTE))
            macro = value;
         else
            assert false;
      }

      getProjectNode().processSystemDefineNode(symbol, macro);
   }

   protected void processIncludePackageNodeAttributes(Attributes attributes)
   {
      assert attributes != null;
      String parentNode = getParentNode();
      assert parentNode != null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            if (parentNode.equals(PROJECT_NODE))
            {
               getProjectNode().processIncludePackageNode(value);
            }
            else if (parentNode.equals(SUBSYSTEM_NODE))
            {
               assert m_CurrentSubsystem != null;
               m_CurrentSubsystem.processIncludePackageNode(value);
            }
         }
         else
         {
            assert false;
         }
      }
   }

   private void processExcludePackageNodeAttributes(Attributes attributes)
   {
      assert attributes != null;
      String parentNode = getParentNode();
      assert parentNode != null;
      assert parentNode.equals(PROJECT_NODE) || parentNode.equals(SUBSYSTEM_NODE);

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            if (parentNode.equals(PROJECT_NODE))
            {
               getProjectNode().processExcludePackageNode(value);
            }
            else if (parentNode.equals(SUBSYSTEM_NODE))
            {
               assert m_CurrentSubsystem != null;
               m_CurrentSubsystem.processExcludePackageNode(value);
            }
            else
            {
               assert false;
            }
         }
         else
         {
            assert false;
         }
      }
   }

   private void processExcludeCompilationUnitNodeAttributes(Attributes attributes)
   {
      assert attributes != null;
      String parentNode = getParentNode();
      assert parentNode != null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            if (parentNode.equals(PROJECT_NODE))
            {
               getProjectNode().processExcludeCompilationUnitNode(value);
            }
            else
            {
               assert false;
            }
         }
         else
         {
            assert false;
         }
      }
   }

   private void processVerticalSlicesNodeAttributes(Attributes attributes)
   {
      assert attributes != null;
      getProjectNode().setVerticalSlicesNodePresent();

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(CHECK_VERTICAL_SLICE_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCheckVerticalSliceDependencies(value);
         }
         else if (name.equals(MAX_VERTICAL_SLICE_CYCLES_ATTRIBUTE))
         {
            getProjectNode().setMaxVerticalSliceCycles(value);
         }
         else if (name.equals(CUMULATE_VERTICAL_SLICE_DEPENDENCIES_ATTRIBUTE))
         {
            getProjectNode().setCumulateVerticalSliceDependencies(value);
         }
         else
         {
            assert false;
         }
      }
   }

   private void processVerticalSlicesExcludeNodeAttributes(Attributes attributes)
   {
      assert attributes != null;
      String parentNode = getParentNode();
      assert parentNode != null;
      assert parentNode.equals(VERTICAL_SLICES_NODE);

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            getProjectNode().processVerticalSlicesExcludeNode(value);
         }
         else
         {
            assert false;
         }
      }
   }

   private void processListenerNodeAttributes(Attributes attributes)
   {
      assert attributes != null;

      String listenerClass = null;
      String file = null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(CLASS_ATTRIBUTE))
         {
            listenerClass = value;
         }
         else if (name.equals(ARGS_ATTRIBUTE))
         {
            file = value;
         }
         else
         {
            assert false;
         }
      }

      assert listenerClass != null;
      assert file != null;

      getProjectNode().processListenerNode(listenerClass, file);
   }

   private void processAssertionNodeAttributes(Attributes attributes)
   {
      assert attributes != null;

      String pattern = null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(PATTERN_ATTRIBUTE))
         {
            pattern = value;
         }
         else
         {
            assert false;
         }
      }

      assert pattern != null;
      assert pattern.length() > 0;

      getProjectNode().processAssertionNode(pattern);
   }

   private void processIgnoreNodeAttributes(Attributes attributes)
   {
      assert attributes != null;

      String fromType = null;
      String toType = null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(FROM_TYPE_ATTRIBUTE))
         {
            fromType = value;
         }
         else if (name.equals(TO_TYPE_ATTRIBUTE))
         {
            toType = value;
         }
         else
         {
            assert false;
         }
      }

      assert fromType != null;
      assert fromType.length() > 0;
      assert toType != null;
      assert toType.length() > 0;

      getProjectNode().processIgnoreNode(fromType, toType);
   }

   private void processSkipNodeAttributes(Attributes attributes)
   {
      assert attributes != null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(PREFIX_ATTRIBUTE))
         {
            getProjectNode().processSkipNode(value);
         }
         else
         {
            assert false;
         }
      }
   }

   private void processRefactorNodeAttributes(Attributes attributes)
   {
      assert attributes != null;

      String compilationUnit = null;
      String toPackage = null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(COMPILATION_UNIT_ATTRIBUTE))
         {
            compilationUnit = value;
         }
         else if (name.equals(TO_PACKAGE_ATTRIBUTE))
         {
            toPackage = value;
         }
         else
         {
            assert false;
         }
      }

      assert compilationUnit != null;
      assert compilationUnit.length() > 0;
      assert toPackage != null;
      assert toPackage.length() > 0;

      getProjectNode().processRefactoringNode(compilationUnit, toPackage);
   }

   private void processLowerThresholdNodeAttributes(Attributes attributes)
   {
      assert attributes != null;

      String nameValue = null;
      String thresholdValue = null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            nameValue = value;
         }
         else if (name.equals(VALUE_ATTRIBUTE))
         {
            thresholdValue = value;
         }
         else
         {
            assert false;
         }
      }

      assert nameValue != null;
      assert thresholdValue != null;

      getProjectNode().processLowerThresholdNode(nameValue, thresholdValue);
   }

   private void processUpperThresholdNodeAttributes(Attributes attributes)
   {
      assert attributes != null;

      String nameValue = null;
      String thresholdValue = null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            nameValue = value;
         }
         else if (name.equals(VALUE_ATTRIBUTE))
         {
            thresholdValue = value;
         }
         else
         {
            assert false;
         }
      }

      assert nameValue != null;
      assert thresholdValue != null;

      getProjectNode().processUpperThresholdNode(nameValue, thresholdValue);
   }

   private void processLayerNodeAttributes(Attributes attributes)
   {
      assert attributes != null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            m_CurrentLayer = getProjectNode().processLayerNode(value);
         }
         else
         {
            assert false;
         }
      }
   }

   private void processSubsystemNodeAttributes(Attributes attributes)
   {
      assert attributes != null;
      assert getParentNode().equals(LAYER_NODE);
      assert m_CurrentLayer != null;

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            assert m_CurrentLayer != null;
            m_CurrentSubsystem = m_CurrentLayer.processSubsystemNode(value);
         }
         else
         {
            assert false;
         }
      }
   }

   protected void processDependsUponNodeAttributes(Attributes attributes)
   {
      assert attributes != null;
      String parentNode = getParentNode();
      assert parentNode.equals(LAYER_NODE) || parentNode.equals(SUBSYSTEM_NODE);

      for (int i = 0; i < attributes.getLength(); ++i)
      {
         String name = attributes.getQName(i);
         String value = attributes.getValue(i);

         if (name.equals(NAME_ATTRIBUTE))
         {
            if (parentNode.equals(LAYER_NODE))
            {
               assert m_CurrentLayer != null;
               m_CurrentLayer.processDependsUponNode(value);
            }
            else if (parentNode.equals(SUBSYSTEM_NODE))
            {
               assert m_CurrentSubsystem != null;
               m_CurrentSubsystem.processDependsUponNode(value);
            }
            else
            {
               assert false;
            }
         }
         else
         {
            assert false;
         }
      }
   }

   public final String getInfo()
   {
      return "Node/Line/Column = " + getCurrentNode() + "/" + m_Locator.getLineNumber() + "/"
         + m_Locator.getColumnNumber();
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

      if (!m_IsDoctypeEntryMissing)
      {
         assert rawName != null;
         assert rawName.length() > 0;

         m_ElementStack.push(rawName);

         try
         {
            if (rawName.equals(PROJECT_NODE))
            {
               processProjectNodeAttributes(attributes);
            }
            else if (rawName.equals(INPUT_NODE))
            {
               processInputNodeAttributes(attributes);
            }
            else if (rawName.equals(SYSTEM_INCLUDE_PATH_NODE))
            {
               processSystemIncludePathNodeAttributes(attributes);
            }
            else if (rawName.equals(SYSTEM_DEFINE_NODE))
            {
               processSystemDefineNodeAttributes(attributes);
            }
            else if (rawName.equals(INCLUDE_PACKAGE_NODE))
            {
               processIncludePackageNodeAttributes(attributes);
            }
            else if (rawName.equals(EXCLUDE_PACKAGE_NODE))
            {
               processExcludePackageNodeAttributes(attributes);
            }
            else if (rawName.equals(EXCLUDE_COMPILATION_UNIT_NODE))
            {
               processExcludeCompilationUnitNodeAttributes(attributes);
            }
            else if (rawName.equals(LISTENER_NODE))
            {
               processListenerNodeAttributes(attributes);
            }
            else if (rawName.equals(ASSERTION_NODE))
            {
               processAssertionNodeAttributes(attributes);
            }
            else if (rawName.equals(IGNORE_NODE))
            {
               processIgnoreNodeAttributes(attributes);
            }
            else if (rawName.equals(SKIP_NODE))
            {
               processSkipNodeAttributes(attributes);
            }
            else if (rawName.equals(REFACTOR_NODE))
            {
               processRefactorNodeAttributes(attributes);
            }
            else if (rawName.equals(UPPER_THRESHOLD_NODE))
            {
               processUpperThresholdNodeAttributes(attributes);
            }
            else if (rawName.equals(LOWER_THRESHOLD_NODE))
            {
               processLowerThresholdNodeAttributes(attributes);
            }
            else if (rawName.equals(LAYER_NODE))
            {
               processLayerNodeAttributes(attributes);
            }
            else if (rawName.equals(SUBSYSTEM_NODE))
            {
               processSubsystemNodeAttributes(attributes);
            }
            else if (rawName.equals(DEPENDS_UPON_NODE))
            {
               processDependsUponNodeAttributes(attributes);
            }
            else if (rawName.equals(VERTICAL_SLICES_NODE))
            {
               processVerticalSlicesNodeAttributes(attributes);
            }
            else if (rawName.equals(VERTICAL_SLICES_EXCLUDE_NODE))
            {
               processVerticalSlicesExcludeNodeAttributes(attributes);
            }
            else if (!additionalStartElement(namespaceUri, localName, rawName, attributes))
            {
               assert false;
            }
         }
         catch (IOException e)
         {
        	log.fatal(e.getMessage());
            throw new SAXException(e);
         }
         catch (IllegalArgumentException e)
         {
         	log.fatal(e.getMessage());
            throw e;        	 
         }
      }
   }

   protected boolean additionalStartElement(String namespaceUri, String localName, String rawName, Attributes attributes)
   {
      return true; // successfully processed
   }

   public final void characters(char[] all, int start, int length)
   {
      String parentNode = getParentNode();
      assert parentNode != null : new String(all);
      assert parentNode.equals(LAYER_NODE) || parentNode.equals(SUBSYSTEM_NODE) : new String(all);
      String description = new String(all, start, length);

      if (parentNode.equals(LAYER_NODE))
      {
         assert m_CurrentLayer != null;
         m_CurrentLayer.setDescription(description);
      }
      else if (parentNode.equals(SUBSYSTEM_NODE))
      {
         assert m_CurrentSubsystem != null;
         m_CurrentSubsystem.setDescription(description);
      }
   }

   public final void endElement(String namespaceUri, String localName, String rawName)
   {
      assert namespaceUri != null;
      assert localName != null;

      if (!m_IsDoctypeEntryMissing)
      {
         assert rawName != null;
         assert rawName.length() > 0;

         m_ElementStack.pop();

         if (rawName.equals(LAYER_NODE))
         {
            m_CurrentLayer = null;
         }
         else if (rawName.equals(SUBSYSTEM_NODE))
         {
            m_CurrentSubsystem = null;
         }
         else
         {
            additionalEndElement(namespaceUri, localName, rawName);
         }
      }
   }

   protected void additionalEndElement(String namespaceUri, String localName, String rawName)
   {
      // For subclasses
   }

   /** Error Handler interface implementation.
     * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
     */
   public final void error(SAXParseException saxParseException)
   {
      log.error(
         "XML configuration is not valid. Problem in line/column '" + saxParseException.getLineNumber() + "/"
            + saxParseException.getColumnNumber() + "' - " + saxParseException.getLocalizedMessage());
      m_IsXMLValid = false;
   }

   /** Error Handler interface implementation.
    * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
    */
   public final void fatalError(SAXParseException saxParseException)
   {
      log.error(
         "Fatal Error. Problem in line/column '" + saxParseException.getLineNumber() + "/"
            + saxParseException.getColumnNumber() + "' - " + saxParseException.getLocalizedMessage());
      m_FatalErrorWhileParsing = true;
   }
   
   /** Error Handler interface implementation.
    * @see org.xml.sax.ErrorHandler#error(org.xml.sax.SAXParseException)
    */
   public final void warning(SAXParseException saxParseException)
   {
      log.warn(
         "Warning. Problem in line/column '" + saxParseException.getLineNumber() + "/"
            + saxParseException.getColumnNumber() + "' - " + saxParseException.getLocalizedMessage());
      m_WarningsWhileParsing = true;
   }

   public final InputSource resolveEntity(String publicId, String systemId)
   {
      InputSource dtd = null;

      if (systemId != null && systemId.endsWith(DTD))
      {
         URL dtdURL = getClass().getResource(DTD);
         assert dtdURL != null;
         log.info("Found DTD: '" + dtdURL + "'");

         InputStream input = getClass().getResourceAsStream(DTD);
         assert input != null;
         dtd = new InputSource(input);
         m_IsDoctypeEntryMissing = false;
      }

      return dtd;
   }

   public final void endDocument()
   {
      if (m_IsDoctypeEntryMissing)
      {
         log.warn(
            "Required '<!DOCTYPE project SYSTEM \"Configuration.dtd\">' entry in xml configuration file missing");
      }
   }

   public final void setDocumentLocator(Locator locator)
   {
      assert locator != null;
      m_Locator = locator;
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