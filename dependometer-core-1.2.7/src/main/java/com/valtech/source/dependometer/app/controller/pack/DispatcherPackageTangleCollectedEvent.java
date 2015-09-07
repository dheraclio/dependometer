package com.valtech.source.dependometer.app.controller.pack;
import com.valtech.source.ag.evf.Dispatcher;
import com.valtech.source.ag.evf.EventIf;

final class DispatcherPackageTangleCollectedEvent extends Dispatcher
{
   protected void dispatch(Object handler, EventIf event)
   {
      assert handler != null;
      assert event != null;
      ((HandlePackageTangleCollectedEventIf)handler)
         .handleEvent((PackageTangleCollectedEvent)event);
   }
}
