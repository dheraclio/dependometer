package com.valtech.source.dependometer.app.typedefprovider.filebased.xml;

import java.util.ArrayList;
import java.util.List;

import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;
import com.valtech.source.dependometer.app.core.provider.ProviderFactory;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;

public class ProjectDefinitionXML
{
   private List<TypeDefinitionXML> typeDefinitions = new ArrayList<TypeDefinitionXML>();

   private String name;

   public boolean addTypeDefinition(TypeDefinitionXML typeDefinition)
   {
      if(typeDefinitions.contains(typeDefinition))
      {
         throw new IllegalStateException("type def '"+typeDefinition.getFullyQualifiedTypeName()+"' already exists.");
      }
      if (isInConfiguredFilter(typeDefinition))
      {
         return typeDefinitions.add(typeDefinition);
      }
      return false;

   }

   // Note that currently package filter is used. that is sufficient for most kind of testing.
   // Later we could add compunit filter, skip, ignore, etc.
   private boolean isInConfiguredFilter(TypeDefinitionXML typeDefinition)
   {
      PackageFilterIf packageFilter = ProviderFactory.getInstance().getConfigurationProvider().getPackageFilter();
      if (packageFilter == null)
      {
         return true;
      }
      return packageFilter.match(typeDefinition.getFullyQualifiedTypeName());
   }

   public void setTypeDefinitionNodeSet(List<TypeDefinitionXML> typeDefinitionSet)
   {
      this.typeDefinitions = typeDefinitionSet;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public TypeDefinitionIf[] getTypeDefinitions()
   {
      return typeDefinitions.toArray(new TypeDefinitionXML[0]);
   }
}
