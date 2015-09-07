package com.valtech.source.dependometer.app.typedefprovider.filebased.xml;

import com.valtech.source.dependometer.app.core.provider.AbstractIdentifierResolver;

public class XmlIdentifierResolver extends AbstractIdentifierResolver
{

   @Override
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
