package com.valtech.source.dependometer.app.typedefprovider.filebased.xml;

public enum TypePropertyXMLEnum {
   ABSTRACT, FINAL, PUBLIC, PROTECTED, PRIVATE, INTERFACE, CLASS, ENUM;

   public static TypePropertyXMLEnum parse(String s)
   {
      for (TypePropertyXMLEnum property : values())
      {
         if (property.name().equalsIgnoreCase(s))
         {
            return property;
         }
      }
      return null;
   }
}
