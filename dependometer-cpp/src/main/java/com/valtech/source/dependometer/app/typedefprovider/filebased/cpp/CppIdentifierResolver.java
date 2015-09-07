package com.valtech.source.dependometer.app.typedefprovider.filebased.cpp;

import com.valtech.source.dependometer.app.core.provider.AbstractIdentifierResolver;

public class CppIdentifierResolver extends AbstractIdentifierResolver
{

   protected static final char DOT_CHAR = '.';

   protected static final String QUALIFICATION = "::";

   protected static final String DEFAULT_PACKAGE = "default";

   protected static final String DEFAULT_PACKAGE_WITH_DOT = "default.";

   protected static final String NESTED = "$";

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
         unitName = DEFAULT_PACKAGE + SLASH_CHAR + unitName;
      }
      else
      {
         unitName = unitName.replace(DOT_CHAR, SLASH_CHAR);
      }
      return unitName;
   }

}
