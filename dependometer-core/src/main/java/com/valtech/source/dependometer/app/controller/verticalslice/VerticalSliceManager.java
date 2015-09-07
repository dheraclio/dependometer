package com.valtech.source.dependometer.app.controller.verticalslice;

public class VerticalSliceManager
{
   private final DispatcherVerticalSliceLevelCollectedEvent dispatcherVerticalSliceLevelCollectedEvent = new DispatcherVerticalSliceLevelCollectedEvent();


   private final DispatcherVerticalSliceCycleParticipationCollectedEvent dispatcherVerticalSliceCycleParticipationCollectedEvent = new DispatcherVerticalSliceCycleParticipationCollectedEvent();

   private final DispatcherVerticalSliceCycleParticipantsCollectedEvent dispatcherVerticalSliceCycleParticipantsCollectedEvent = new DispatcherVerticalSliceCycleParticipantsCollectedEvent();

   private final DispatcherVerticalSliceCycleCollectedEvent dispatcherVerticalSliceCycleCollectedEvent = new DispatcherVerticalSliceCycleCollectedEvent();

   private final DispatcherVerticalSliceTangleCollectedEvent dispatcherVerticalSliceTangleCollectedEvent = new DispatcherVerticalSliceTangleCollectedEvent();

   // VerticalSliceLevelCollectedEvent
   public void attach(HandleVerticalSliceLevelCollectedEventIf handler)
   {
      dispatcherVerticalSliceLevelCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleVerticalSliceLevelCollectedEventIf handler)
   {
      dispatcherVerticalSliceLevelCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(VerticalSliceLevelCollectedEvent event)
   {
      dispatcherVerticalSliceLevelCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleVerticalSliceLevelCollectedEventIf handler)
   {
      return dispatcherVerticalSliceLevelCollectedEvent.isAttached(handler);
   }

   public int numberOfVerticalSliceLevelCollectedEventHandler()
   {
      return dispatcherVerticalSliceLevelCollectedEvent.numberOfEventHandler();
   }

   // VerticalSliceCycleParticipationCollectedEvent
   public void attach(HandleVerticalSliceCycleParticipationCollectedEventIf handler)
   {
      dispatcherVerticalSliceCycleParticipationCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleVerticalSliceCycleParticipationCollectedEventIf handler)
   {
      dispatcherVerticalSliceCycleParticipationCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(VerticalSliceCycleParticipationCollectedEvent event)
   {
      dispatcherVerticalSliceCycleParticipationCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleVerticalSliceCycleParticipationCollectedEventIf handler)
   {
      return dispatcherVerticalSliceCycleParticipationCollectedEvent.isAttached(handler);
   }

   public int numberOfVerticalSliceCycleParticipationCollectedEventHandler()
   {
      return dispatcherVerticalSliceCycleParticipationCollectedEvent.numberOfEventHandler();
   }

   // VerticalSliceCycleParticipantsCollectedEvent
   public void attach(HandleVerticalSliceCycleParticipantsCollectedEventIf handler)
   {
      dispatcherVerticalSliceCycleParticipantsCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleVerticalSliceCycleParticipantsCollectedEventIf handler)
   {
      dispatcherVerticalSliceCycleParticipantsCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(VerticalSliceCycleParticipantsCollectedEvent event)
   {
      dispatcherVerticalSliceCycleParticipantsCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleVerticalSliceCycleParticipantsCollectedEventIf handler)
   {
      return dispatcherVerticalSliceCycleParticipantsCollectedEvent.isAttached(handler);
   }

   public int numberOfVerticalSliceCycleParticipantsCollectedEventHandler()
   {
      return dispatcherVerticalSliceCycleParticipantsCollectedEvent.numberOfEventHandler();
   }

   // VerticalSliceCycleCollectedEvent
   public void attach(HandleVerticalSliceCycleCollectedEventIf handler)
   {
      dispatcherVerticalSliceCycleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleVerticalSliceCycleCollectedEventIf handler)
   {
      dispatcherVerticalSliceCycleCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(VerticalSliceCycleCollectedEvent event)
   {
      dispatcherVerticalSliceCycleCollectedEvent.dispatch(event);
   }

   
   // VerticalSliceCycleCollectedEvent
   public void attach(HandleVerticalSliceTangleCollectedEventIf handler)
   {
      dispatcherVerticalSliceTangleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleVerticalSliceTangleCollectedEventIf handler)
   {
      dispatcherVerticalSliceTangleCollectedEvent.removeEventHandler(handler);
   }
      
   public void dispatch(VerticalSliceTangleCollectedEvent event)
   {
      dispatcherVerticalSliceTangleCollectedEvent.dispatch(event);
   }
   
   
   
   public boolean isAttached(HandleVerticalSliceCycleCollectedEventIf handler)
   {
      return dispatcherVerticalSliceCycleCollectedEvent.isAttached(handler);
   }

   public int numberOfVerticalSliceCycleCollectedEventHandler()
   {
      return dispatcherVerticalSliceCycleCollectedEvent.numberOfEventHandler();
   }
}
