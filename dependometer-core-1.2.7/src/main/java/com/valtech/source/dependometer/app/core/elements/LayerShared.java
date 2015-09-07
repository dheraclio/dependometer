package com.valtech.source.dependometer.app.core.elements;

import com.valtech.source.ag.util.ListToPrimitive;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * ** BEGIN LICENSE BLOCK *****
 * BSD License (2 clause)
 * Copyright (c) 2006 - 2013, Stephan Pfab
 * All rights reserved.
 * <p/>
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * <p/>
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Stephan Pfab BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * **** END LICENSE BLOCK ****
 */
public class LayerShared {

    public static final LayerShared THE = new LayerShared();

    private final Map<String,Layer>       layers = new HashMap<String, Layer>();
    private final List<Double>            relationCohesionValues = new ArrayList<Double>();
    private final Set<DirectedDependency> forbiddenDependencies  = new TreeSet<DirectedDependency>();
    private final Set<DirectedDependency> unusedDependencies     = new TreeSet<DirectedDependency>();
    private int numberOfAllowedEfferents = 0;
    private int numberOfEfferentDependencies = 0;
    private List<Layer> projectInternalLayers = null;


    private LayerShared() {}

    public void reset() {
        layers.clear();
        relationCohesionValues.clear();
        forbiddenDependencies.clear();
        unusedDependencies.clear();
        numberOfAllowedEfferents = 0;
        numberOfEfferentDependencies = 0;
        projectInternalLayers = null;
    }



    public void addLayer( Layer layer ) {
        if ( layers.containsKey( layer.getFullyQualifiedName() )) {
            throw new IllegalArgumentException( "a layer with that name exists already" );
        }

        layers.put( layer.getFullyQualifiedName(), layer );
    }

    public boolean layerExists( String fullyQualifiedName ) {
        return layers.containsKey( fullyQualifiedName );
    }

    public Layer getLayer( String fullyQualifiedName ) {
        return layers.get( fullyQualifiedName );
    }

    public int getNumberOfLayers() {
        return layers.size();
    }

    public Layer[] getLayers() {
        return layers.values().toArray(new Layer[0]);
    }


    public double[] getRelationalCohesionValues() {
        return ListToPrimitive.toDoubleArray( relationCohesionValues );
    }

    public void addRelationalCohesion( double rc ) {
        relationCohesionValues.add( rc );
    }


    public int getNumberOfForbiddenDependencies() {
        return forbiddenDependencies.size();
    }

    public DirectedDependency[] getForbiddenLayerDependeciesAsArray() {
        return forbiddenDependencies.toArray( new DirectedDependency[ 0 ] );
    }

    public void addForbiddenDependency( DirectedDependency directedDependency ) {
        forbiddenDependencies.add( directedDependency );
    }



    public int getNumberOfUnusedDependencies() {
        return unusedDependencies.size();
    }

    public DirectedDependency[] getUnusedDependencies() {
        return unusedDependencies.toArray( new DirectedDependency[0]);
    }

    public void addUnusedDependency( DirectedDependency directedDependency ) {
        unusedDependencies.add( directedDependency );
    }

    public void increaseNumberOfAlledEfferents() {
        ++numberOfAllowedEfferents;
    }

    public int getNumberOfAllowedEfferents() {
        return numberOfAllowedEfferents;
    }

    public void addEfferent() {
        ++numberOfEfferentDependencies;
    }

    public int getNumberOfEfferntDependencies() {
        return numberOfEfferentDependencies;
    }

    public Layer[] getProjectInternalLayers() {
        // TODO see belows comment
        // TODO the result depends on wether all layers are already created !!
        if( projectInternalLayers == null ) {
            projectInternalLayers = new ArrayList<Layer>( 5 );
            for( Layer next : layers.values()  ) {
                if( next.belongsToProject() ) {
                    projectInternalLayers.add( next );
                }
            }
        }

        return projectInternalLayers.toArray( new Layer[ 0 ] );
    }

    public void resetProjectInternalLayers() {
        // TODO the existence of the method together with the cashing of getProjectInternalLayers is scary
        projectInternalLayers = null;
    }
}
