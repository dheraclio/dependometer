package com.valtech.source.dependometer.app.controller.type;

import com.valtech.source.dependometer.app.controller.project.TangleCollectedEvent;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;

public final class TypeTangleCollectedEvent extends TangleCollectedEvent
{
   @Override
   public EntityTypeEnum getEntityTypeEnum()
   {
      return EntityTypeEnum.TYPE;
   }
}
