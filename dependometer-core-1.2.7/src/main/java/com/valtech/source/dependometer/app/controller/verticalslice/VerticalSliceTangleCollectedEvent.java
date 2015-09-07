package com.valtech.source.dependometer.app.controller.verticalslice;

import com.valtech.source.dependometer.app.controller.project.TangleCollectedEvent;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;

public class VerticalSliceTangleCollectedEvent extends TangleCollectedEvent
{
   @Override
   public EntityTypeEnum getEntityTypeEnum()
   {
      return EntityTypeEnum.VERTICAL_SLICE;
   }
}
