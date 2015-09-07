package com.valtech.source.dependometer.app.util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class DependometerUtilTest extends TestCase
{

   public void testCopyResultTemplate() throws IOException
   {
      File testResultDir = File.createTempFile("test-results", "");

      if (testResultDir.exists())
      {
         testResultDir.delete();
      }
      testResultDir.mkdir();
      DependometerUtil.copyResultTemplateToDir(testResultDir);

      assertTrue(new File(testResultDir, "index.html").exists());
      assertTrue(new File(testResultDir, "dependometer/logo.gif").exists());
      assertTrue(new File(testResultDir, "dependometer/stylesheet.css").exists());
      System.out.println(testResultDir.getAbsolutePath());

      testResultDir.delete();
   }
}
