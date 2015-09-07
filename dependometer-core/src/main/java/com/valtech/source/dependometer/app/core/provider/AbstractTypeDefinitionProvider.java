package com.valtech.source.dependometer.app.core.provider;

import java.io.IOException;

import com.valtech.source.ag.util.AssertionUtility;

public abstract class AbstractTypeDefinitionProvider implements TypeDefinitionProviderIf
{
   /**
    * Dependencies to ignore.
    */
   private RegexprDirectedTypeDependencyIf[] dependenciesToIgnore;

   /**
    * Skipped dependencies.
    */
   private SkipExternalIf[] skippedDependencies;

   public ConfigurationProviderIf getConfigurationProvider()
   {
      return ProviderFactory.getInstance().getConfigurationProvider();
   }

   /**
    * Checks whether the given type name is configured to be skipped as outgoing reference
    * 
    * @param typeName Type name
    * @return TRUE if the type name has to be skipped, FALSE otherwise
    */
   public boolean skipType(String typeName)
   {
      assert typeName != null;
      assert typeName.length() > 0;

      for (int i = 0; i < skippedDependencies.length; ++i)
      {
         if (skippedDependencies[i].match(typeName))
         {
            return true;
         }
      }

      return false;
   }

   /**
    * Checks whether references from the given fromType to the provided toType are configured to be ignored for analysis
    * 
    * @param fromType Type name of the source
    * @param toType Type name of the target
    * @return TRUE, if the reference is configured to be ignored, FALSE otherwise
    */
   public boolean ignoreType(String fromType, String toType)
   {
      assert fromType != null;
      assert fromType.length() > 0;
      assert toType != null;
      assert toType.length() > 0;

      for (int i = 0; i < dependenciesToIgnore.length; ++i)
      {
         RegexprDirectedTypeDependencyIf next = dependenciesToIgnore[i];

         if (next.match(fromType, toType))
         {
            return true;
         }
      }

      return false;
   }

   public final TypeDefinitionIf[] getTypeDefinitions() throws IOException
   {
      dependenciesToIgnore = getConfigurationProvider().getIgnore();
      assert AssertionUtility.checkArray(dependenciesToIgnore);

      skippedDependencies = getConfigurationProvider().getSkipPatterns();
      assert AssertionUtility.checkArray(skippedDependencies);

      return execute();
   }

   protected abstract TypeDefinitionIf[] execute() throws IOException;
}
