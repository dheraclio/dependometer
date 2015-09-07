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
package com.valtech.source.dependometer.ui.console;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.valtech.source.ag.cla.CommandLineParser;
import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.configprovider.filebased.xml.LayerNode;
import com.valtech.source.dependometer.app.configprovider.filebased.xml.LogicalElementNode;
import com.valtech.source.dependometer.app.configprovider.filebased.xml.Node;
import com.valtech.source.dependometer.app.configprovider.filebased.xml.SubsystemNode;
import com.valtech.source.dependometer.app.controller.main.DependencyManager;
import com.valtech.source.dependometer.app.controller.main.DependometerContext;
import com.valtech.source.dependometer.app.core.elements.CompilationUnit;
import com.valtech.source.dependometer.app.core.elements.Layer;
import com.valtech.source.dependometer.app.core.elements.NotParsedCompilationUnit;
import com.valtech.source.dependometer.app.core.elements.NotParsedPackage;
import com.valtech.source.dependometer.app.core.elements.NotParsedType;
import com.valtech.source.dependometer.app.core.elements.Package;
import com.valtech.source.dependometer.app.core.elements.ParsedClass;
import com.valtech.source.dependometer.app.core.elements.ParsedCompilationUnit;
import com.valtech.source.dependometer.app.core.elements.ParsedPackage;
import com.valtech.source.dependometer.app.core.elements.ParsedType;
import com.valtech.source.dependometer.app.core.elements.Project;
import com.valtech.source.dependometer.app.core.elements.Subsystem;
import com.valtech.source.dependometer.app.core.elements.Type;
import com.valtech.source.dependometer.app.core.elements.VerticalSlice;
import com.valtech.source.dependometer.app.core.metrics.MetricsProvider;
import com.valtech.source.dependometer.app.core.metrics.ProjectMetricsEnum;
import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.ListenerIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.util.DependometerUtil;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class Dependometer
{
   private final static String OPTION_CONFIG_FILE = "-file";

   private final static String OPTION_CONFIG_FILE_DESCRIPTION = "xml file with project settings";

   private final static Logger logger = Logger.getLogger(Dependometer.class.getName());

   private static DependometerContext context;

   /**
    * Ctor.
    * 
    * @param args command line args
    */
   public Dependometer(String[] args)
   {
      logger.info(DependometerUtil.getVersionInfo(getClass()));

      CommandLineParser cmdLineParser = CommandLineParser.getInstance();
      cmdLineParser.addArgumentOption(OPTION_CONFIG_FILE, OPTION_CONFIG_FILE_DESCRIPTION, true);

      if (cmdLineParser.parse(args))
      {
         String configFilePath = cmdLineParser.getArgumentForOption(OPTION_CONFIG_FILE);
         assert configFilePath != null;
         assert configFilePath.length() > 0;

         File configFile = new File(configFilePath);
         try
         {
            startAnalysis(configFile);
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }
      else
      {
         logger.warn("wrong command line syntax " + System.getProperty("line.separator") + cmdLineParser.usage());
      }
   }

   /**
    * Ctor.
    */
   public Dependometer()
   {

   }

   public void startAnalysis(File configFile) throws Exception
   {
      // @author Daniel Heraclio
      // @email dheraclio@gmail
      // workaround that should enable multiple dependomter runs in the same VM
      // moved to beggining so previous failures wont cause error on new runs in the same VM
      reset();

      ConfigurationProviderIf configurationProvider = ProviderFactory.getInstance().getConfigurationProvider();
      if (configurationProvider.readConfiguration(configFile))
      {
         createOutputListener(configurationProvider.getListeners());
         Date timeNow = new Date();
         long minutes = configurationProvider.getTimeoutMinutes();
         Date timeStop = new Date(timeNow.getTime() + minutes * 60000L);
         
         DependencyManager depManager = new DependencyManager(Dependometer.getContext(), timeStop);

         depManager.analyze();
         depManager.start();
         depManager.join();
      }
      else
      {
         logger.warn("aborting due to configuration problems");
      }      
   }

   public void createOutputListener(ListenerIf[] listener) throws Exception
   {
      if (listener == null)
      {
         logger.warn("There are no listeners configured!");
         return;
      }

      assert AssertionUtility.checkArray(listener);

      for (int i = 0; i < listener.length; ++i)
      {
         ListenerIf nextListener = listener[i];
         logger.info("creating listener '" + nextListener.getClassName() + "' with arguments '"
            + nextListener.getArguments() + "'");

         String[] args = createArgumentList(nextListener.getArguments());
         Class[] argTypes = new Class[1];
         argTypes[0] = String[].class;
         Constructor ctor = Class.forName(nextListener.getClassName(), true, getClass().getClassLoader())
            .getConstructor(argTypes);
         Object[] ctorArgs = new Object[1];
         ctorArgs[0] = args;
         ctor.newInstance(ctorArgs);
      }
   }

   private static String[] createArgumentList(String args)
   {
      assert args != null;

      List<String> list = new ArrayList<String>();
      StringTokenizer st = new StringTokenizer(args, ", ", false);

      while (st.hasMoreTokens())
      {
         String nextToken = st.nextToken();
         assert nextToken != null;
         assert nextToken.length() > 0;
         list.add(nextToken);
      }

      return list.toArray(new String[0]);
   }

   /**
    * Workaround: resets whole dependometer state.
    * 
    */
   // TODO remove most static fields + methods from dependometer (legacy)
   public static void reset()
   {
      // reset elements
      CompilationUnit.reset();
      Layer.reset();
      NotParsedCompilationUnit.reset();
      NotParsedPackage.reset();
      NotParsedType.reset();
      Package.reset();
      ParsedClass.reset();
      ParsedCompilationUnit.reset();
      ParsedPackage.reset();
      ParsedType.reset();
      Project.reset();
      Subsystem.reset();
      Type.reset();
      VerticalSlice.reset();

      // reset metrics
      MetricsProvider.reset();
      ProjectMetricsEnum.resetValues();

      // reset configuration, introduces architecture violations in selftest, but this is a good reminder
      Node.reset();
      LayerNode.reset();
      LogicalElementNode.reset();
      SubsystemNode.reset();

      ProviderFactory.reset();

      context = null;
   }

   /** @deprecated please eliminate global variables */
   public static DependometerContext getContext()
   {
      if (context == null)
      {
         context = new DependometerContext();
      }
      return context;
   }
}