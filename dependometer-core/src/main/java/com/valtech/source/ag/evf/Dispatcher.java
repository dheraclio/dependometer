/*
 * Valtech Public L I C E N S E (VPL) 1.0.2
 * 
 * dependometer Copyright � 2007 Valtech GmbH
 * 
 * dependometer software is made available free of charge under the following conditions.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * 1.1.All copies and redistributions of source code must retain the above copyright notice, this list of conditions and
 * the following disclaimer.
 * 
 * 1.2.Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the
 * following disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * 1.3.The end-user documentation included with the redistribution, if any, must include the following acknowledgment:
 * This product includes software developed by Valtech http://www.valtech.de/. This acknowledgement must appear in the
 * software itself, if and wherever such third-party acknowledgments normally appear.
 * 
 * 1.4.The names "Valtech" and "dependometer" must not be used to endorse or promote products derived from this software
 * without prior written permission. For written permission, please contact kmc@valtech.de <mailto:kmc@valtech.de>
 * 
 * BECAUSE THIS SOFTWARE IS LICENSED FREE OF CHARGE IT IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL VALTECH GMBH OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. LEGAL LIABILITY PROVIDED UNDER GERMAN LAW FOR
 * INTENDED DAMAGES, BAD FAITH OR GROSS NEGLIGENCE REMAINS UNAFFECTED.
 */

package com.valtech.source.ag.evf;

import java.util.ArrayList;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public abstract class Dispatcher
{
   private final HandlerArray m_HandlerArray = new HandlerArray();

   private long m_DispatchingDepth;

   private boolean m_EventHandlerCacheChanged;

   private final ArrayList<HandlerUpdate> m_EventHandlerCache = new ArrayList<HandlerUpdate>();

   public final synchronized int numberOfEventHandler()
   {
      return m_HandlerArray.m_NumberOfHandler;
   }

   public final synchronized void addEventHandler(Object handler)
   {
      assert handler != null;
      assert !isAttached(handler);

      if (m_DispatchingDepth > 0)
      {
         m_EventHandlerCache.add(new HandlerUpdate(handler, HandlerUpdate.ADD));
         m_EventHandlerCacheChanged = true;
      }
      else
      {
         updateEventHandlers(new HandlerUpdate(handler, HandlerUpdate.ADD));
      }
   }

   public final synchronized void removeEventHandler(Object handler)
   {
      assert handler != null;

      if (m_DispatchingDepth > 0)
      {
         m_EventHandlerCache.add(new HandlerUpdate(handler, HandlerUpdate.REMOVE));
         m_EventHandlerCacheChanged = true;
      }
      else
      {
         updateEventHandlers(new HandlerUpdate(handler, HandlerUpdate.REMOVE));
      }
   }

   public final void dispatch(EventIf event)
   {
      assert event != null;

      startDispatch();

      int numberOfHandler = 0;
      Object[] handler = null;

      synchronized (this)
      {
         numberOfHandler = m_HandlerArray.m_NumberOfHandler;
         handler = m_HandlerArray.m_Handler;
      }

      for (int i = 0; i < numberOfHandler; ++i)
      {
         dispatch(handler[i], event);
      }

      endDispatch();
   }

   private final synchronized void startDispatch()
   {
      ++m_DispatchingDepth;
   }

   private final synchronized void endDispatch()
   {
      --m_DispatchingDepth;

      assert m_DispatchingDepth >= 0;

      if (m_EventHandlerCacheChanged)
      {
         for (int i = 0; i < m_EventHandlerCache.size(); ++i)
            updateEventHandlers((HandlerUpdate)m_EventHandlerCache.get(i));

         m_EventHandlerCache.clear();
         m_EventHandlerCacheChanged = false;
      }
   }

   private final void updateEventHandlers(HandlerUpdate update)
   {
      assert update != null;

      if (update.getAction() == HandlerUpdate.ADD)
         m_HandlerArray.addHandler(update.getHandler());
      else
         m_HandlerArray.removeHandler(update.getHandler());
   }

   public final synchronized boolean isAttached(Object handler)
   {
      return m_HandlerArray.indexHandler(handler) != -1;
   }

   protected abstract void dispatch(Object handler, EventIf event);
}