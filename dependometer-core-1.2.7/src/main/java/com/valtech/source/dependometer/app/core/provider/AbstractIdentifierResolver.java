package com.valtech.source.dependometer.app.core.provider;

public abstract class AbstractIdentifierResolver implements IIdentifierResolver
{

   protected static final char DOT_CHAR = '.';

   protected static final String QUALIFICATION = "::";

   protected static final String DEFAULT_PACKAGE = "default";

   protected static final String DEFAULT_PACKAGE_WITH_DOT = "default.";

   protected static final String NESTED = "$";

   protected static final char SLASH_CHAR = '/';

   public String getVerticalSliceName(String fullyQualifiedSubsystem)
   {
      assert fullyQualifiedSubsystem != null;
      assert fullyQualifiedSubsystem.length() > 0;
      int pos = fullyQualifiedSubsystem.indexOf(QUALIFICATION);
      assert pos != -1;
      assert fullyQualifiedSubsystem.length() > pos + 2;
      return fullyQualifiedSubsystem.substring(pos + 2);
   }

   public String getLayerName(String fullyQualifiedSubsystem)
   {
      assert fullyQualifiedSubsystem != null;
      assert fullyQualifiedSubsystem.length() > 0;
      int pos = fullyQualifiedSubsystem.indexOf(QUALIFICATION);
      assert pos != -1;
      assert pos > 0;
      return fullyQualifiedSubsystem.substring(0, pos);
   }

   public String getFullyQualifiedTypeName(String fullyQualifiedTypeName)
   {
      assert fullyQualifiedTypeName != null;
      assert fullyQualifiedTypeName.length() > 0;
      int pos = fullyQualifiedTypeName.lastIndexOf('.');
      if (pos != -1)
      {
         return fullyQualifiedTypeName;
      }
      else
      {
         return DEFAULT_PACKAGE_WITH_DOT + fullyQualifiedTypeName;
      }
   }

   public String getTypeName(String fullyQualifiedTypeName)
   {
      assert fullyQualifiedTypeName != null;
      assert fullyQualifiedTypeName.length() > 0;
      int pos = fullyQualifiedTypeName.lastIndexOf('.');
      if (pos != -1)
      {
         return fullyQualifiedTypeName.substring(pos + 1);
      }
      else
      {
         return fullyQualifiedTypeName;
      }
   }

   public String getPackageName(String fullyQualifiedTypeName)
   {
      assert fullyQualifiedTypeName != null;
      assert fullyQualifiedTypeName.length() > 0;

      int pos = fullyQualifiedTypeName.lastIndexOf(DOT_CHAR);
      if (pos != -1)
      {
         return fullyQualifiedTypeName.substring(0, pos);
      }
      else
      {
         return DEFAULT_PACKAGE;
      }
   }

   public abstract String getRelativeCompilationUnitPath(String fullyQualifiedTypeName);

   public String getCompilationUnitName(String fullyQualifiedTypeName)
   {
      assert fullyQualifiedTypeName != null;
      assert fullyQualifiedTypeName.length() > 0;

      int pos = fullyQualifiedTypeName.lastIndexOf(NESTED);
      if (pos != -1)
      {
         return fullyQualifiedTypeName.substring(0, pos);
      }

      return fullyQualifiedTypeName;
   }
}
