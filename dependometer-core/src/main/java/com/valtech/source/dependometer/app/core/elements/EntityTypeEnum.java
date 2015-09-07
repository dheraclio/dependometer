package com.valtech.source.dependometer.app.core.elements;

public enum EntityTypeEnum {

   // type
   TYPE("type", "type"),
   // compilation unit
   COMPILATION_UNIT("compilation-unit", "compilationUnit", "compilation unit"),
   // package
   PACKAGE("package", "package"),
   // subsystem
   SUBSYSTEM("subsystem", "subsystem"),
   // layer
   LAYER("layer", "layer"),
   // vertical slice
   VERTICAL_SLICE("vertical-slice", "verticalSlice", "vertical slice"),
   // project
   PROJECT("project", "project");

   public String getXmlName()
   {
      return xmlName;
   }

   public String getDisplayName()
   {
      return displayName;
   }

   private final String xmlName;

   private final String displayName;

   private final String entityName;

   private EntityTypeEnum(String entityName, String xmlName, String displayName)
   {
      this.entityName = entityName;
      this.xmlName = xmlName;
      this.displayName = displayName;
   }

   public String getEntityName()
   {
      return entityName;
   }

   private EntityTypeEnum(String entityId, String displayName)
   {
      this(entityId, entityId, displayName);
   }
}
