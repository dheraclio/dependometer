package com.valtech.source.dependometer.app.controller.subsystem;

import com.valtech.source.ag.evf.Dispatcher;
import com.valtech.source.ag.evf.EventIf;

final class DispatcherSubsystemTangleCollectedEvent extends Dispatcher
{
   protected void dispatch(Object handler, EventIf event)
   {
      assert handler != null;
      assert event != null;
      ((HandleSubsystemTangleCollectedEventIf)handler)
         .handleEvent((SubsystemTangleCollectedEvent)event);
   }
}
