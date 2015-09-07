package com.valtech.source.dependometer.app.typedefprovider.filebased.xml;

import java.io.IOException;

import com.valtech.source.dependometer.app.configprovider.filebased.xml.RegexprPackageFilter;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;
import common.AbstractDependometerTest;
import common.SimpleTestConfiguration;
import common.TestProviderFactory;

public class TypeDefinitionProviderXMLTest extends AbstractDependometerTest
{
   private TestProviderFactory testProviderFactory;
   private TypeDefinitionProviderXML typeDefinitionProvider;

   @Override
   public void setUp()
   {
      super.setUp();
      
      testProviderFactory = new TestProviderFactory();

      typeDefinitionProvider = loadXMLTypeDefinitionProvider("sample.xml");
      testProviderFactory.setTypeDefinitionProvider(typeDefinitionProvider);
      ProviderFactory.setInstance(testProviderFactory);
   }

   public void testGetTypeDefinitions() throws IOException
   {
      testProviderFactory.setConfigurationProvider(new SimpleTestConfiguration());
      
      TypeDefinitionIf[] typeDefinitions = typeDefinitionProvider.getTypeDefinitions();
      
      assertEquals(6, typeDefinitions.length);

      for (int i = 0; i < typeDefinitions.length; i++)
      {
         TypeDefinitionIf typeDefinition = typeDefinitions[i];
         if (typeDefinition.getFullyQualifiedTypeName().equals("com.valtech.source.project.domain.Employee"))
         {
            assertEquals(1, typeDefinition.getFullyQualifiedSuperClassNames().length);
            assertEquals("com.valtech.source.project.domain.AbstractEntity", typeDefinition
               .getFullyQualifiedSuperClassNames()[0]);
            assertEquals(1, typeDefinition.getFullyQualifiedImportedTypeNames().length);
            assertEquals("com.valtech.source.project.domain.Adresse", typeDefinition
               .getFullyQualifiedImportedTypeNames()[0]);
            assertTrue(typeDefinition.isClass());
            assertTrue(typeDefinition.isAccessible());
            assertFalse(typeDefinition.isAbstract());
            assertFalse(typeDefinition.isInterface());
            assertTrue(typeDefinition.isValid());
         }
      }
   }
    
   public void testPackageFilter() throws IOException
   {
      testProviderFactory.setConfigurationProvider(new SimpleTestConfiguration()
      {
         @Override
         public PackageFilterIf getPackageFilter()
         {
            PackageFilterIf packageFilter=new RegexprPackageFilter();
            
            packageFilter.include("com.valtech.source.project.domain.*");
            
            return packageFilter;
         }
         
      });
      
      TypeDefinitionIf[] typeDefinitions = typeDefinitionProvider.getTypeDefinitions();
      
      //only 4 types in package filter
      assertEquals(4, typeDefinitions.length);
   }
}
