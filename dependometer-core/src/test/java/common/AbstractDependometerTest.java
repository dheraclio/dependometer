package common;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import junit.framework.TestCase;

import com.valtech.source.dependometer.app.typedefprovider.filebased.xml.TypeDefinitionProviderXML;

public abstract class AbstractDependometerTest extends TestCase
{
   public void setUp()
   {
      System.out.println("***  " + getClass().getSimpleName() + "." + this.getName());
   }

   protected URL loadTestData(String resLoc)
   {
      URL url = AbstractDependometerTest.class.getResource(resLoc);
      if (url == null)
      {
         fail("Could not load test resource '" + resLoc + "'");
      }
      return url;
   }

   protected File loadTestDataAsFile(String resLoc)
   {
      URL url = loadTestData(resLoc);
      File file = null;
      try
      {
         file = new File(url.toURI());
      }
      catch (URISyntaxException e)
      {
         e.printStackTrace();
         fail("Could not load test resource '" + resLoc + "' as file!");
      }
      return file;
   }

   protected TypeDefinitionProviderXML loadXMLTypeDefinitionProvider(String typeDefConfig)
   {
      URL sampleURL = loadTestData("/" + typeDefConfig);

      final String rules = "xmlrules.xml";
      TypeDefinitionProviderXML typeDefinitionProvider = new TypeDefinitionProviderXML(sampleURL.getPath(), rules);
      System.out.println(" - loading TypeDefinitionProvider " + sampleURL.getPath() + ", " + rules);
      return typeDefinitionProvider;
   }

   public static InputStream getTestDataByResLocAsStream(String resLoc)
   {
      InputStream in = AbstractDependometerTest.class.getResourceAsStream(resLoc);
      if (in == null)
      {
         fail("Could not load test data '" + resLoc + "'!");
      }
      return in;
   }

   /**
    * Resets static dependomter content -> refactor it
    */
   protected void resetDependomter()
   {
      DependomterTestUtil.resetDependometer();
   }
   
   protected File generateTempFile(String suffix)
   {
      File file=null;
      try
      {
         file = File.createTempFile(getClass().getSimpleName(), suffix);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         fail("Could not create tempFile!");
      }
      return file;
   }
}
