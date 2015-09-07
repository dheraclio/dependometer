package com.valtech.source.dependometer.app.controller.subsystem;

import com.valtech.source.dependometer.app.controller.project.TangleCollectedEvent;
import com.valtech.source.dependometer.app.core.elements.EntityTypeEnum;

public final class SubsystemTangleCollectedEvent extends TangleCollectedEvent
{
   @Override
   public EntityTypeEnum getEntityTypeEnum()
   {
      return EntityTypeEnum.SUBSYSTEM;
   }
}
