package com.valtech.source.dependometer.app.controller.type;

import com.valtech.source.ag.evf.Dispatcher;
import com.valtech.source.ag.evf.EventIf;

final class DispatcherTypeTangleCollectedEvent extends Dispatcher
{
   protected void dispatch(Object handler, EventIf event)
   {
      assert handler != null;
      assert event != null;
      ((HandleTypeTangleCollectedEventIf)handler)
         .handleEvent((TypeTangleCollectedEvent)event);
   }
}
