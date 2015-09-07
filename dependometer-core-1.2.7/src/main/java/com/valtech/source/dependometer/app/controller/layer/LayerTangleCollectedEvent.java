package com.valtech.source.dependometer.app.controller.layer;

import com.valtech.source.dependometer.app.controller.project.TangleCollectedEvent;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;

public class LayerTangleCollectedEvent extends TangleCollectedEvent
{
   @Override
   public EntityTypeEnum getEntityTypeEnum()
   {
      return EntityTypeEnum.LAYER;
   }
}
