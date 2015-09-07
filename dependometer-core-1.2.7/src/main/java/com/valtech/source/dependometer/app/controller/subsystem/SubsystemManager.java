
package com.valtech.source.dependometer.app.controller.subsystem;

public class SubsystemManager
{
   private final DispatcherSubsystemLevelCollectedEvent dispatcherSubsystemLevelCollectedEvent = new DispatcherSubsystemLevelCollectedEvent();

   private final DispatcherSubsystemCycleParticipationCollectedEvent dispatcherSubsystemCycleParticipationCollectedEvent = new DispatcherSubsystemCycleParticipationCollectedEvent();

   private final DispatcherSubsystemCycleParticipantsCollectedEvent dispatcherSubsystemCycleParticipantsCollectedEvent = new DispatcherSubsystemCycleParticipantsCollectedEvent();

   private final DispatcherSubsystemCycleCollectedEvent dispatcherSubsystemCycleCollectedEvent = new DispatcherSubsystemCycleCollectedEvent();

   private final DispatcherSubsystemTangleCollectedEvent dispatcherSubsystemTangleCollectedEvent = new DispatcherSubsystemTangleCollectedEvent();

   // SubsystemLevelCollectedEvent
   public void attach(HandleSubsystemLevelCollectedEventIf handler)
   {
      dispatcherSubsystemLevelCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleSubsystemLevelCollectedEventIf handler)
   {
      dispatcherSubsystemLevelCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(SubsystemLevelCollectedEvent event)
   {
      dispatcherSubsystemLevelCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleSubsystemLevelCollectedEventIf handler)
   {
      return dispatcherSubsystemLevelCollectedEvent.isAttached(handler);
   }

   public int numberOfSubsystemLevelCollectedEventHandler()
   {
      return dispatcherSubsystemLevelCollectedEvent.numberOfEventHandler();
   }

   // SubsystemCycleParticipationCollectedEvent
   public void attach(HandleSubsystemCycleParticipationCollectedEventIf handler)
   {
      dispatcherSubsystemCycleParticipationCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleSubsystemCycleParticipationCollectedEventIf handler)
   {
      dispatcherSubsystemCycleParticipationCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(SubsystemCycleParticipationCollectedEvent event)
   {
      dispatcherSubsystemCycleParticipationCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleSubsystemCycleParticipationCollectedEventIf handler)
   {
      return dispatcherSubsystemCycleParticipationCollectedEvent.isAttached(handler);
   }

   public int numberOfSubsystemCycleParticipationCollectedEventHandler()
   {
      return dispatcherSubsystemCycleParticipationCollectedEvent.numberOfEventHandler();
   }

   // SubsystemCycleParticipantsCollectedEvent
   public void attach(HandleSubsystemCycleParticipantsCollectedEventIf handler)
   {
      dispatcherSubsystemCycleParticipantsCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleSubsystemCycleParticipantsCollectedEventIf handler)
   {
      dispatcherSubsystemCycleParticipantsCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(SubsystemCycleParticipantsCollectedEvent event)
   {
      dispatcherSubsystemCycleParticipantsCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleSubsystemCycleParticipantsCollectedEventIf handler)
   {
      return dispatcherSubsystemCycleParticipantsCollectedEvent.isAttached(handler);
   }

   public int numberOfSubsystemCycleParticipantsCollectedEventHandler()
   {
      return dispatcherSubsystemCycleParticipantsCollectedEvent.numberOfEventHandler();
   }

   // SubsystemCycleCollectedEvent
   public void attach(HandleSubsystemCycleCollectedEventIf handler)
   {
      dispatcherSubsystemCycleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleSubsystemCycleCollectedEventIf handler)
   {
      dispatcherSubsystemCycleCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(SubsystemCycleCollectedEvent event)
   {
      dispatcherSubsystemCycleCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleSubsystemCycleCollectedEventIf handler)
   {
      return dispatcherSubsystemCycleCollectedEvent.isAttached(handler);
   }

   public int numberOfSubsystemCycleCollectedEventHandler()
   {
      return dispatcherSubsystemCycleCollectedEvent.numberOfEventHandler();
   }

   
   // SubsystemTangleCollectedEvent
   public void attach(HandleSubsystemTangleCollectedEventIf handler)
   {
      dispatcherSubsystemTangleCollectedEvent.addEventHandler(handler);
   }

   public void dispatch(SubsystemTangleCollectedEvent event)
   {
      dispatcherSubsystemTangleCollectedEvent.dispatch(event);
   }

}
