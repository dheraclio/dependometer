package com.valtech.source.dependometer.app.typedefprovider.filebased.java;

import com.valtech.source.dependometer.app.core.provider.AbstractIdentifierResolver;

public class JavaIdentifierResolver extends AbstractIdentifierResolver
{

   private static final String JAVA_EXTENSION = ".java";

   public String getRelativeCompilationUnitPath(String fullyQualifiedTypeName)
   {
      assert fullyQualifiedTypeName != null;
      assert fullyQualifiedTypeName.length() > 0;

      String unitName = fullyQualifiedTypeName;
      int pos = unitName.indexOf(NESTED);
      if (pos != -1)
      {
         unitName = unitName.substring(0, pos);
      }

      pos = unitName.indexOf(DOT_CHAR);
      if (pos == -1)
      {
         unitName = DEFAULT_PACKAGE + SLASH_CHAR + unitName + JAVA_EXTENSION;
      }
      else
      {
         unitName = unitName.replace(DOT_CHAR, SLASH_CHAR) + JAVA_EXTENSION;
      }
      return unitName;
   }
}
