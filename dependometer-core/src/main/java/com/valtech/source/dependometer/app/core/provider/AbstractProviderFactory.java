package com.valtech.source.dependometer.app.core.provider;

import com.valtech.source.dependometer.app.core.provider.ConfigurationProviderIf;
import com.valtech.source.dependometer.app.core.provider.IProviderFactory;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionProviderIf;

public class AbstractProviderFactory implements IProviderFactory
{
   private ConfigurationProviderIf configurationProvider;

   private TypeDefinitionProviderIf typeDefinitionProvider;

   public ConfigurationProviderIf getConfigurationProvider()
   {
      return configurationProvider;
   }

   public TypeDefinitionProviderIf getTypeDefinitionProvider()
   {
      return typeDefinitionProvider;
   }

   public void setConfigurationProvider(ConfigurationProviderIf configurationProvider)
   {
      this.configurationProvider = configurationProvider;
   }

   public void setTypeDefinitionProvider(TypeDefinitionProviderIf typeDefinitionProvider)
   {
      this.typeDefinitionProvider = typeDefinitionProvider;
   }

}
