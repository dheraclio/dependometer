package com.valtech.source.dependometer.app.controller.layer;

public class LayerManager
{
   private final DispatcherLayerLevelCollectedEvent dispatcherLayerLevelCollectedEvent = new DispatcherLayerLevelCollectedEvent();

   private final DispatcherLayerCycleParticipationCollectedEvent dispatcherLayerCycleParticipationCollectedEvent = new DispatcherLayerCycleParticipationCollectedEvent();

   private final DispatcherLayerCycleParticipantsCollectedEvent dispatcherLayerCycleParticipantsCollectedEvent = new DispatcherLayerCycleParticipantsCollectedEvent();

   private final DispatcherLayerCycleCollectedEvent dispatcherLayerCycleCollectedEvent = new DispatcherLayerCycleCollectedEvent();

   private final DispatcherLayerTangleCollectedEvent dispatcherLayerTangleCollectedEvent = new DispatcherLayerTangleCollectedEvent();
   
   // LayerLevelCollectedEvent
   public void attach(HandleLayerLevelCollectedEventIf handler)
   {
      dispatcherLayerLevelCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleLayerLevelCollectedEventIf handler)
   {
      dispatcherLayerLevelCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(LayerLevelCollectedEvent event)
   {
      dispatcherLayerLevelCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleLayerLevelCollectedEventIf handler)
   {
      return dispatcherLayerLevelCollectedEvent.isAttached(handler);
   }

   public int numberOfLayerLevelCollectedEventHandler()
   {
      return dispatcherLayerLevelCollectedEvent.numberOfEventHandler();
   }

   // LayerCycleParticipationCollectedEvent
   public void attach(HandleLayerCycleParticipationCollectedEventIf handler)
   {
      dispatcherLayerCycleParticipationCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleLayerCycleParticipationCollectedEventIf handler)
   {
      dispatcherLayerCycleParticipationCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(LayerCycleParticipationCollectedEvent event)
   {
      dispatcherLayerCycleParticipationCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleLayerCycleParticipationCollectedEventIf handler)
   {
      return dispatcherLayerCycleParticipationCollectedEvent.isAttached(handler);
   }

   public int numberOfLayerCycleParticipationCollectedEventHandler()
   {
      return dispatcherLayerCycleParticipationCollectedEvent.numberOfEventHandler();
   }

   // LayerCycleParticipantsCollectedEvent
   public void attach(HandleLayerCycleParticipantsCollectedEventIf handler)
   {
      dispatcherLayerCycleParticipantsCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleLayerCycleParticipantsCollectedEventIf handler)
   {
      dispatcherLayerCycleParticipantsCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(LayerCycleParticipantsCollectedEvent event)
   {
      dispatcherLayerCycleParticipantsCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleLayerCycleParticipantsCollectedEventIf handler)
   {
      return dispatcherLayerCycleParticipantsCollectedEvent.isAttached(handler);
   }

   public int numberOfLayerCycleParticipantsCollectedEventHandler()
   {
      return dispatcherLayerCycleParticipantsCollectedEvent.numberOfEventHandler();
   }

   // LayerCycleCollectedEvent
   public void attach(HandleLayerCycleCollectedEventIf handler)
   {
      dispatcherLayerCycleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleLayerCycleCollectedEventIf handler)
   {
      dispatcherLayerCycleCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(LayerCycleCollectedEvent event)
   {
      dispatcherLayerCycleCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleLayerCycleCollectedEventIf handler)
   {
      return dispatcherLayerCycleCollectedEvent.isAttached(handler);
   }

   public int numberOfLayerCycleCollectedEventHandler()
   {
      return dispatcherLayerCycleCollectedEvent.numberOfEventHandler();
   }
   
   // LayerCycleCollectedEvent
   public void attach(HandleLayerTangleCollectedEventIf handler)
   {
      dispatcherLayerTangleCollectedEvent.addEventHandler(handler);
   }

   public void dispatch(LayerTangleCollectedEvent event)
   {
      dispatcherLayerTangleCollectedEvent.dispatch(event);
   }

}
