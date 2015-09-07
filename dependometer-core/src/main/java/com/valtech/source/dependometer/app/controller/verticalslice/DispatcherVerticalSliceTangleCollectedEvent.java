package com.valtech.source.dependometer.app.controller.verticalslice;

import com.valtech.source.ag.evf.EventIf;



public class DispatcherVerticalSliceTangleCollectedEvent extends com.valtech.source.ag.evf.Dispatcher
{
   protected void dispatch(Object handler, EventIf event)
   {
      assert handler != null;
      assert event != null;
      ((HandleVerticalSliceTangleCollectedEventIf)handler)
         .handleEvent((VerticalSliceTangleCollectedEvent)event);
   }
}
