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
package com.valtech.source.dependometer.app.configprovider.filebased.xml;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public class LayerNode extends LogicalElementNode
{
   private static final String QUALIFICATION = "::";

   private final static List<String> layerNames = new ArrayList<String>();

   public static String[] getLayerNames()
   {
      return layerNames.toArray(new String[0]);
   }

   public LayerNode(String name)
   {
      super(name);
      layerNames.add(name);
   }

   public SubsystemNode processSubsystemNode(String name)
   {
      assert name != null;
      assert name.length() > 0;

      name = getName() + QUALIFICATION + name;

      SubsystemNode node = new SubsystemNode(name);
      getLogger().debug("Subsystem added - " + node);

      return node;
   }

   public static void reset()
   {
      layerNames.clear();
   }
}