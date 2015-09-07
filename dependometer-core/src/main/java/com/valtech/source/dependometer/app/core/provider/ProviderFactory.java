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
package com.valtech.source.dependometer.app.core.provider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class ProviderFactory extends AbstractProviderFactory
{
   public final static String TYPE_DEFINITION_PROVIDER_KEY = "dependometer.provider.typedefinition";

   public final static String CONFIGURATION_PROVIDER_KEY = "dependometer.provider.configuration";

   private final static String PROPERTIES = "ProviderFactory.properties";

   private static Logger logger = Logger.getLogger(ProviderFactory.class.getName());

   private static IProviderFactory instance = null;

   public static void setInstance(IProviderFactory instance)
   {
      ProviderFactory.instance = instance;
   }

   private String defaultTypeDefintionProviderName;

   private String defaultConfigurationProviderName;

   public static IProviderFactory getInstance()
   {
      if (instance == null)
      {
         instance = new ProviderFactory();
      }

      return instance;
   }

   private void loadDefaultProviderNames()
   {
      logger.info("Try to load default provider names from " + PROPERTIES + " from "
         + ProviderFactory.class.getPackage());
      InputStream in = ProviderFactory.class.getResourceAsStream(PROPERTIES);
      if (in == null)
      {
         logger.error("Loading provider names failed");
         System.exit(-1);
      }
      assert in != null : "Properties could not be found";
      Properties properties = new Properties();
      try
      {
         properties.load(in);
      }
      catch (IOException e)
      {
    	 logger.fatal(e.getMessage());
         assert false;
      }

      Enumeration enumKeys = properties.keys();
      while (enumKeys.hasMoreElements())
      {
         String key = (String)enumKeys.nextElement();
         String value = properties.getProperty(key);
         if (key.equals(TYPE_DEFINITION_PROVIDER_KEY))
         {
            defaultTypeDefintionProviderName = value;
         }
         else if (key.equals(CONFIGURATION_PROVIDER_KEY))
         {
            defaultConfigurationProviderName = value;
         }
         else
         {
            logger.warn("Key '" + key + "' not supported for '" + getClass().getName());
         }
      }

      assert defaultTypeDefintionProviderName != null;
      assert defaultTypeDefintionProviderName.length() > 0;
      assert defaultConfigurationProviderName != null;
      assert defaultConfigurationProviderName.length() > 0;
   }

   private ProviderFactory()
   {
      loadDefaultProviderNames();

      String typeDefinitionProviderClassName = System.getProperty(TYPE_DEFINITION_PROVIDER_KEY);
      if (typeDefinitionProviderClassName == null || typeDefinitionProviderClassName.length() == 0)
      {
         typeDefinitionProviderClassName = defaultTypeDefintionProviderName;
      }
      logger.info("using '" + typeDefinitionProviderClassName + "' as type definition provider");

      String configurationProviderClassName = System.getProperty(CONFIGURATION_PROVIDER_KEY);
      if (configurationProviderClassName == null || configurationProviderClassName.length() == 0)
      {
         configurationProviderClassName = defaultConfigurationProviderName;
      }
      logger.info("using '" + configurationProviderClassName + "' as configuration provider");

      try
      {
         Class typeDefinitionProviderClass = Class.forName(typeDefinitionProviderClassName);
         setTypeDefinitionProvider((TypeDefinitionProviderIf)typeDefinitionProviderClass.newInstance());
         Class configurationProviderClass = Class.forName(configurationProviderClassName);
         setConfigurationProvider((ConfigurationProviderIf)configurationProviderClass.newInstance());
      }
      catch (ClassCastException e)
      {
         logger.error(e);
         System.exit(1);
      }
      catch (ClassNotFoundException e)
      {
         logger.error(e);
         System.exit(1);
      }
      catch (InstantiationException e)
      {
         logger.error(e);
         System.exit(1);
      }
      catch (IllegalAccessException e)
      {
         logger.error(e);
         System.exit(1);
      }
   }

   public static void reset()
   {
      instance = null;
   }
}