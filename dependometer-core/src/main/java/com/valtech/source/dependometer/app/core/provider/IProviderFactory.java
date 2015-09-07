package com.valtech.source.dependometer.app.core.provider;

public interface IProviderFactory
{
   TypeDefinitionProviderIf getTypeDefinitionProvider();

   ConfigurationProviderIf getConfigurationProvider();
}