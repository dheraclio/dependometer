package com.valtech.source.dependometer.app.controller.pack;

import com.valtech.source.dependometer.app.controller.project.TangleCollectedEvent;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;

public final class PackageTangleCollectedEvent extends TangleCollectedEvent
{
   @Override
   public EntityTypeEnum getEntityTypeEnum()
   {
      return EntityTypeEnum.PACKAGE;
   }
}
