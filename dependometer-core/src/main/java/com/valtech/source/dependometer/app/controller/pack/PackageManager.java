
package com.valtech.source.dependometer.app.controller.pack;

public class PackageManager
{
   private final DispatcherPackageLevelCollectedEvent dispatcherPackageLevelCollectedEvent = new DispatcherPackageLevelCollectedEvent();

   private final DispatcherPackageCycleParticipationCollectedEvent dispatcherPackageCycleParticipationCollectedEvent = new DispatcherPackageCycleParticipationCollectedEvent();

   private final DispatcherPackageCycleParticipantsCollectedEvent dispatcherPackageCycleParticipantsCollectedEvent = new DispatcherPackageCycleParticipantsCollectedEvent();

   private final DispatcherPackageCycleCollectedEvent dispatcherPackageCycleCollectedEvent = new DispatcherPackageCycleCollectedEvent();

   
   private final DispatcherPackageTangleCollectedEvent dispatcherPackageTangleCollectedEvent = new DispatcherPackageTangleCollectedEvent();
   
   
   // PackageLevelCollectedEvent
   public void attach(HandlePackageLevelCollectedEventIf handler)
   {
      dispatcherPackageLevelCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandlePackageLevelCollectedEventIf handler)
   {
      dispatcherPackageLevelCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(PackageLevelCollectedEvent event)
   {
      dispatcherPackageLevelCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandlePackageLevelCollectedEventIf handler)
   {
      return dispatcherPackageLevelCollectedEvent.isAttached(handler);
   }

   public int numberOfPackageLevelCollectedEventHandler()
   {
      return dispatcherPackageLevelCollectedEvent.numberOfEventHandler();
   }

   // PackageCycleParticipationCollectedEvent
   public void attach(HandlePackageCycleParticipationCollectedEventIf handler)
   {
      dispatcherPackageCycleParticipationCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandlePackageCycleParticipationCollectedEventIf handler)
   {
      dispatcherPackageCycleParticipationCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(PackageCycleParticipationCollectedEvent event)
   {
      dispatcherPackageCycleParticipationCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandlePackageCycleParticipationCollectedEventIf handler)
   {
      return dispatcherPackageCycleParticipationCollectedEvent.isAttached(handler);
   }

   public int numberOfPackageCycleParticipationCollectedEventHandler()
   {
      return dispatcherPackageCycleParticipationCollectedEvent.numberOfEventHandler();
   }

   // PackageCycleParticipantsCollectedEvent
   public void attach(HandlePackageCycleParticipantsCollectedEventIf handler)
   {
      dispatcherPackageCycleParticipantsCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandlePackageCycleParticipantsCollectedEventIf handler)
   {
      dispatcherPackageCycleParticipantsCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(PackageCycleParticipantsCollectedEvent event)
   {
      dispatcherPackageCycleParticipantsCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandlePackageCycleParticipantsCollectedEventIf handler)
   {
      return dispatcherPackageCycleParticipantsCollectedEvent.isAttached(handler);
   }

   public int numberOfPackageCycleParticipantsCollectedEventHandler()
   {
      return dispatcherPackageCycleParticipantsCollectedEvent.numberOfEventHandler();
   }

   // PackageCycleCollectedEvent
   public void attach(HandlePackageCycleCollectedEventIf handler)
   {
      dispatcherPackageCycleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandlePackageCycleCollectedEventIf handler)
   {
      dispatcherPackageCycleCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(PackageCycleCollectedEvent event)
   {
      dispatcherPackageCycleCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandlePackageCycleCollectedEventIf handler)
   {
      return dispatcherPackageCycleCollectedEvent.isAttached(handler);
   }

   public int numberOfPackageCycleCollectedEventHandler()
   {
      return dispatcherPackageCycleCollectedEvent.numberOfEventHandler();
   }

   
   // PackageTangleCollectedEvent
   public void attach(HandlePackageTangleCollectedEventIf handler)
   {
      dispatcherPackageTangleCollectedEvent.addEventHandler(handler);
   }

   public void dispatch(PackageTangleCollectedEvent event)
   {
      dispatcherPackageTangleCollectedEvent.dispatch(event);
   }

   
}
