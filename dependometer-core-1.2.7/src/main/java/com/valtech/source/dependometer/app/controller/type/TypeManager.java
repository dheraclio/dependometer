
package com.valtech.source.dependometer.app.controller.type;

public class TypeManager
{
   private final DispatcherTypeLevelCollectedEvent dispatcherTypeLevelCollectedEvent = new DispatcherTypeLevelCollectedEvent();

   private final DispatcherTypeCycleParticipationCollectedEvent dispatcherTypeCycleParticipationCollectedEvent = new DispatcherTypeCycleParticipationCollectedEvent();

   private final DispatcherTypeCycleParticipantsCollectedEvent dispatcherTypeCycleParticipantsCollectedEvent = new DispatcherTypeCycleParticipantsCollectedEvent();

   private final DispatcherSinglePackageTypeCycleCollectedEvent dispatcherSinglePackageTypeCycleCollectedEvent = new DispatcherSinglePackageTypeCycleCollectedEvent();

   private final DispatcherSingleCompilationUnitTypeCycleCollectedEvent dispatcherSingleCompilationUnitTypeCycleCollectedEvent = new DispatcherSingleCompilationUnitTypeCycleCollectedEvent();

   private final DispatcherMultiplePackageTypeCycleCollectedEvent dispatcherMultiplePackageTypeCycleCollectedEvent = new DispatcherMultiplePackageTypeCycleCollectedEvent();

   private final DispatcherTypeTangleCollectedEvent dispatcherTypeTangleCollectedEvent = new DispatcherTypeTangleCollectedEvent();
   
   
   // TypeLevelCollectedEvent
   public void attach(HandleTypeLevelCollectedEventIf handler)
   {
      dispatcherTypeLevelCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleTypeLevelCollectedEventIf handler)
   {
      dispatcherTypeLevelCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(TypeLevelCollectedEvent event)
   {
      dispatcherTypeLevelCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleTypeLevelCollectedEventIf handler)
   {
      return dispatcherTypeLevelCollectedEvent.isAttached(handler);
   }

   public int numberOfTypeLevelCollectedEventHandler()
   {
      return dispatcherTypeLevelCollectedEvent.numberOfEventHandler();
   }

   // TypeCycleParticipationCollectedEvent
   public void attach(HandleTypeCycleParticipationCollectedEventIf handler)
   {
      dispatcherTypeCycleParticipationCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleTypeCycleParticipationCollectedEventIf handler)
   {
      dispatcherTypeCycleParticipationCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(TypeCycleParticipationCollectedEvent event)
   {
      dispatcherTypeCycleParticipationCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleTypeCycleParticipationCollectedEventIf handler)
   {
      return dispatcherTypeCycleParticipationCollectedEvent.isAttached(handler);
   }

   public int numberOfTypeCycleParticipationCollectedEventHandler()
   {
      return dispatcherTypeCycleParticipationCollectedEvent.numberOfEventHandler();
   }

   // TypeCycleParticipantsCollectedEvent
   public void attach(HandleTypeCycleParticipantsCollectedEventIf handler)
   {
      dispatcherTypeCycleParticipantsCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleTypeCycleParticipantsCollectedEventIf handler)
   {
      dispatcherTypeCycleParticipantsCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(TypeCycleParticipantsCollectedEvent event)
   {
      dispatcherTypeCycleParticipantsCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleTypeCycleParticipantsCollectedEventIf handler)
   {
      return dispatcherTypeCycleParticipantsCollectedEvent.isAttached(handler);
   }

   public int numberOfTypeCycleParticipantsCollectedEventHandler()
   {
      return dispatcherTypeCycleParticipantsCollectedEvent.numberOfEventHandler();
   }

   // SinglePackageTypeCycleCollectedEvent
   public void attach(HandleSinglePackageTypeCycleCollectedEventIf handler)
   {
      dispatcherSinglePackageTypeCycleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleSinglePackageTypeCycleCollectedEventIf handler)
   {
      dispatcherSinglePackageTypeCycleCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(SinglePackageTypeCycleCollectedEvent event)
   {
      dispatcherSinglePackageTypeCycleCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleSinglePackageTypeCycleCollectedEventIf handler)
   {
      return dispatcherSinglePackageTypeCycleCollectedEvent.isAttached(handler);
   }

   public int numberOfSinglePackageTypeCycleCollectedEventHandler()
   {
      return dispatcherSinglePackageTypeCycleCollectedEvent.numberOfEventHandler();
   }

   // SingleCompilationUnitTypeCycleCollectedEvent
   public void attach(HandleSingleCompilationUnitTypeCycleCollectedEventIf handler)
   {
      dispatcherSingleCompilationUnitTypeCycleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleSingleCompilationUnitTypeCycleCollectedEventIf handler)
   {
      dispatcherSingleCompilationUnitTypeCycleCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(SingleCompilationUnitTypeCycleCollectedEvent event)
   {
      dispatcherSingleCompilationUnitTypeCycleCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleSingleCompilationUnitTypeCycleCollectedEventIf handler)
   {
      return dispatcherSingleCompilationUnitTypeCycleCollectedEvent.isAttached(handler);
   }

   public int numberOfSingleCompilationUnitTypeCycleCollectedEventHandler()
   {
      return dispatcherSingleCompilationUnitTypeCycleCollectedEvent.numberOfEventHandler();
   }

   // MultiplePackageTypeCycleCollectedEvent
   public void attach(HandleMultiplePackageTypeCycleCollectedEventIf handler)
   {
      dispatcherMultiplePackageTypeCycleCollectedEvent.addEventHandler(handler);
   }

   public void detach(HandleMultiplePackageTypeCycleCollectedEventIf handler)
   {
      dispatcherMultiplePackageTypeCycleCollectedEvent.removeEventHandler(handler);
   }

   public void dispatch(MultiplePackageTypeCycleCollectedEvent event)
   {
      dispatcherMultiplePackageTypeCycleCollectedEvent.dispatch(event);
   }

   public boolean isAttached(HandleMultiplePackageTypeCycleCollectedEventIf handler)
   {
      return dispatcherMultiplePackageTypeCycleCollectedEvent.isAttached(handler);
   }

   public int numberOfMultiplePackageTypeCycleCollectedEventHandler()
   {
      return dispatcherMultiplePackageTypeCycleCollectedEvent.numberOfEventHandler();
   }


   // TypeTangleCollectedEvent
   public void attach(HandleTypeTangleCollectedEventIf handler)
   {
      dispatcherTypeTangleCollectedEvent.addEventHandler(handler);
   }

   public void dispatch(TypeTangleCollectedEvent event)
   {
      dispatcherTypeTangleCollectedEvent.dispatch(event);
   }
}
