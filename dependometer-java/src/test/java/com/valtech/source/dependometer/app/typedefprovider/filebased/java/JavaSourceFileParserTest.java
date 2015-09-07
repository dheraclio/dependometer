package com.valtech.source.dependometer.app.typedefprovider.filebased.java;

import java.io.File;
import java.io.IOException;

import common.AbstractDependometerTest;

public class JavaSourceFileParserTest extends AbstractDependometerTest
{
   public void testReadChacters() throws IOException
   {
      File f=loadTestDataAsFile("/testdata/StringChecks.java");
      
      SourceFileParserIf parser=new JavaSourceParser();
      parser.parse(f);
   }
}
