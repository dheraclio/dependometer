package com.valtech.source.dependometer.app.controller.layer;

import com.valtech.source.ag.evf.Dispatcher;
import com.valtech.source.ag.evf.EventIf;

final class DispatcherLayerTangleCollectedEvent extends Dispatcher
{
   protected void dispatch(Object handler, EventIf event)
   {
      assert handler != null;
      assert event != null;
      ((HandleLayerTangleCollectedEventIf)handler)
         .handleEvent((LayerTangleCollectedEvent)event);
   }
}
