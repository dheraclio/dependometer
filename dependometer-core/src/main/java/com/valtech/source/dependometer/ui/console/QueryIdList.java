/*
 * Copyright 2009 Valtech GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.valtech.source.dependometer.ui.console;

import org.apache.log4j.Logger;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.elements.Project;
import com.valtech.source.dependometer.app.core.provider.QueryInfoIf;
import com.valtech.source.dependometer.app.util.DependometerUtil;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class QueryIdList
{
   private static Logger logger = Logger.getLogger(QueryIdList.class.getName());

   public static void main(String[] args)
   {
      new QueryIdList();
   }

   QueryIdList()
   {
      logger.info(DependometerUtil.getVersionInfo(getClass()));

      QueryInfoIf[] ids = Project.getProvidedQueryInfo();
      assert AssertionUtility.checkArray(ids);

      if (ids.length > 0)
      {
         logger.info("Supported query info ids are ... ");

         for (int i = 0; i < ids.length; ++i)
         {
            QueryInfoIf next = ids[i];
            logger.info("ID = " + next.getId());
            logger.info(">>>> " + next.getDescription());
         }
      }
   }
}
