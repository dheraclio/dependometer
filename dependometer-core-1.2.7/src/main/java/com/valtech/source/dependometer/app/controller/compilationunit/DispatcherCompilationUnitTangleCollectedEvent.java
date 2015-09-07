package com.valtech.source.dependometer.app.controller.compilationunit;

import com.valtech.source.ag.evf.Dispatcher;
import com.valtech.source.ag.evf.EventIf;

final class DispatcherCompilationUnitTangleCollectedEvent extends Dispatcher
{
   protected void dispatch(Object handler, EventIf event)
   {
      assert handler != null;
      assert event != null;
      ((HandleCompilationUnitTangleCollectedEventIf)handler)
         .handleEvent((CompilationUnitTangleCollectedEvent)event);
   }
}
