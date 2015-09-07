package com.valtech.source.dependometer.app.typedefprovider.filebased.xml;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.valtech.source.dependometer.app.core.provider.AbstractTypeDefinitionProvider;
import com.valtech.source.dependometer.app.core.provider.IIdentifierResolver;
import com.valtech.source.dependometer.app.core.provider.TypeDefinitionIf;

public class TypeDefinitionProviderXML extends AbstractTypeDefinitionProvider
{
   private static Logger logger = Logger.getLogger(TypeDefinitionProviderXML.class.getName());

   private String testScenario;

   private Digester digester;

   /**
    * Called via reflection.
    */
   public TypeDefinitionProviderXML()
   {
   }

   public TypeDefinitionProviderXML(String testScenario, String rulesfileName)
   {
      this.testScenario = testScenario;

      loadDigesterRules(rulesfileName);
   }

   private void loadDigesterRules(String rulesfileName)
   {
      if (rulesfileName == null)
      {
         logger.error("No rules file configured");
         return;
      }
      logger.info("loading rulesfile " + rulesfileName);
      URL rulesURL = ClassLoader.getSystemResource(rulesfileName);
      if (rulesURL == null)
      {
         logger.error("Unable to find rules file " + rulesfileName);
         throw new IllegalStateException("Unable to find rules file " + rulesfileName);
      }

      digester = DigesterLoader.createDigester(rulesURL);
   }

   public TypeDefinitionIf[] execute()
   {

      ProjectDefinitionXML project = new ProjectDefinitionXML();
      digester.push(project);

      // Process the input file.
      File srcFile = null;
      try
      {
         srcFile = new File(testScenario);
         digester.parse(srcFile);
      }
      catch (IOException ioe)
      {
         logger.error("Error reading input file " + srcFile.getAbsolutePath(), ioe);
         throw new IllegalStateException(ioe);
      }
      catch (SAXException se)
      {
         logger.error("Error parsing input file" + srcFile.getAbsolutePath(), se);
         throw new IllegalStateException(se);
      }

      return project.getTypeDefinitions();
   }

   public String[] getAdditionalInfo()
   {
      return null;
   }

   public IIdentifierResolver getIdentifierResolver()
   {
      return new XmlIdentifierResolver();
   }

}
