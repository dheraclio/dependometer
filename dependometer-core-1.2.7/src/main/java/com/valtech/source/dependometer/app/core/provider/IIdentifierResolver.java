package com.valtech.source.dependometer.app.core.provider;

/**
 * @author oliver.rohr
 * 
 *         Resolver for language specific identifiers, such as package names, etc.
 * 
 */
public interface IIdentifierResolver
{

   String getVerticalSliceName(String fullyQualifiedSubsystem);

   String getLayerName(String fullyQualifiedSubsystem);

   String getFullyQualifiedTypeName(String fullyQualifiedTypeName);

   String getTypeName(String fullyQualifiedTypeName);

   String getPackageName(String fullyQualifiedTypeName);

   String getRelativeCompilationUnitPath(String fullyQualifiedTypeName);

   String getCompilationUnitName(String fullyQualifiedTypeName);
}
