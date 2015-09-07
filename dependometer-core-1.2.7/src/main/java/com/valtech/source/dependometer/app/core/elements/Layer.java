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
package com.valtech.source.dependometer.app.core.elements;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.valtech.source.dependometer.app.core.common.MetricEnum;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;
import com.valtech.source.dependometer.app.core.provider.PackageFilterIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
public final class Layer extends CompilationUnitGroupingElement {
    /**
     * @deprecated use EntityTypeEnum instead
     */
    public static final String ELEMENT_NAME = "layer";

    private final Set<Subsystem> subsystems = new TreeSet<Subsystem>();

    public Layer( String fullyQualifiedName, Object source ) {
        super( fullyQualifiedName, source );

        LayerShared.THE.addLayer( this );
    }


    static double[] getRelationalCohesionValues() {
        return LayerShared.THE.getRelationalCohesionValues();
    }

    protected void relationalCohesion( double rc ) {
        LayerShared.THE.addRelationalCohesion( rc );
    }

    static int getTotalNumberOfEfferentDependencies() {
        return LayerShared.THE.getNumberOfEfferntDependencies();
    }

    static int getNumberOfUnusedEfferentLayerDependencies() {
        return LayerShared.THE.getNumberOfUnusedDependencies();
    }

    static DirectedDependency[] getUnusedEfferentLayerDependencies() {
        return LayerShared.THE.getUnusedDependencies();
    }

    static int getNumberOfForbiddenEfferentLayerDependencies() {
        return LayerShared.THE.getNumberOfForbiddenDependencies();
    }

    static DirectedDependency[] getForbiddenEfferentLayerDependencies() {
        return LayerShared.THE.getForbiddenLayerDependeciesAsArray();
    }

    protected void efferentAdded( DependencyElement efferent ) {
        assert efferent != null;
        LayerShared.THE.addEfferent();
    }

    protected void forbiddenEfferentAdded( DependencyElementIf forbidden ) {
        assert forbidden != null;
        assert isForbiddenEfferent( forbidden );
        int typeRels = getNumberOfTypeRelationsForEfferent( forbidden );

        LayerShared.THE.addForbiddenDependency( new DirectedDependency( this, forbidden, typeRels, true ) );
    }

    public boolean contains( DependencyElementIf element ) {
        assert element != null;
        return subsystems.contains( element );
    }

    public void allowedEfferentAdded( DependencyElement allowed ) {
        assert allowed != null;
        LayerShared.THE.increaseNumberOfAlledEfferents();
    }

    public static int getNumberOfAllowedEfferents() {
        return LayerShared.THE.getNumberOfAllowedEfferents();
    }

    public static boolean layerExists( String fullyQualifiedName ) {
        assert fullyQualifiedName != null;
        assert fullyQualifiedName.length() > 0;
        return LayerShared.THE.layerExists( fullyQualifiedName );
    }

    public static Layer getLayer( String fullyQualifiedName ) {
        assert layerExists( fullyQualifiedName );
        return LayerShared.THE.getLayer( fullyQualifiedName );
    }

    public static int getNumberOfLayers() {
        return LayerShared.THE.getNumberOfLayers();
    }

    public static Layer[] getLayers() {
        return LayerShared.THE.getLayers();
    }

    public static Layer[] getProjectInternalLayers() {
        return LayerShared.THE.getProjectInternalLayers();
    }

    public boolean belongsToProject() {
        for ( Subsystem sub : subsystems ) {
            if ( sub.belongsToProject() ) {
                return true;
            }
        }

        return false;
    }

    public void addTypesAndCompilationUnits() {
        for( Subsystem nextSubsystem : subsystems ) {

            Type[] types = nextSubsystem.getTypes();
            for( int j = 0; j < types.length; ++j ) {
                addType( types[ j ] );
            }

            CompilationUnit[] compilationUnits = nextSubsystem.getCompilationUnits();
            for( int j = 0; j < compilationUnits.length; ++j ) {
                addCompilationUnit( compilationUnits[ j ] );
            }
        }
    }

    public void analyzeDependencies() {
        Iterator subsystemIter = subsystems.iterator();
        while( subsystemIter.hasNext() ) {
            Subsystem nextSubsystem = (Subsystem) subsystemIter.next();

            DependencyElementIf[] efferentSubsystems = nextSubsystem.getEfferents();
            for( int i = 0; i < efferentSubsystems.length; ++i ) {
                Subsystem nextEfferentSubsystem = (Subsystem) efferentSubsystems[ i ];
                Layer efferentLayer = (Layer) nextEfferentSubsystem.belongsToDependencyElement();

                assert efferentLayer != null;

                if( this != efferentLayer ) {
                    addEfferent( efferentLayer, nextSubsystem, nextEfferentSubsystem );
                }
            }

            DependencyElementIf[] afferentSubsystems = nextSubsystem.getAfferents();
            for( int i = 0; i < afferentSubsystems.length; ++i ) {
                Subsystem nextAfferentSubsystem = (Subsystem) afferentSubsystems[ i ];
                Layer afferentLayer = (Layer) nextAfferentSubsystem.belongsToDependencyElement();
                assert afferentLayer != null;
                if( this != afferentLayer ) {
                    addAfferent( afferentLayer, nextSubsystem, nextAfferentSubsystem );
                }
            }
        }
    }


    public String getName() {
        return getFullyQualifiedName();
    }

    public void prepareCollectionOfMetrics() {
        super.prepareCollectionOfMetrics();
        DependencyElementIf[] unused = getUnusedAllowedEfferents();
        for( int i = 0; i < unused.length; i++ ) {
            assert getContainmentLevel() == ( (DependencyElement) unused[ i ] ).getContainmentLevel();
            LayerShared.THE.addUnusedDependency( new DirectedDependency( this, unused[ i ], 0, false ) );
        }
    }

    public void collectMetrics() {
        super.collectMetrics();
        addMetric( MetricEnum.NUMBER_OF_CONTAINED_SUBSYSTEMS, subsystems.size() );
    }

    public Subsystem createSubsystem( String fullyQualifiedName, PackageFilterIf subsystemFilter,
                                      PackageFilterIf projectFilter, Object source ) {
        assert fullyQualifiedName != null;
        assert fullyQualifiedName.length() > 0;
        assert !Subsystem.subsystemExists( fullyQualifiedName );

        Subsystem subsystem = new Subsystem( fullyQualifiedName, subsystemFilter, projectFilter, source );
        subsystem.setContainer( this, true );
        assert !subsystems.contains( subsystem );
        subsystems.add( subsystem );
        LayerShared.THE.resetProjectInternalLayers();
        return subsystem;
    }

    public int getContainmentLevel() {
        return 4;
    }

    public DependencyElementIf[] containsDependencyElements() {
        return subsystems.toArray( new Subsystem[ 0 ] );
    }

    public String getElementName() {
        return ELEMENT_NAME;
    }

    public String getContainedElementName() {
        return Subsystem.ELEMENT_NAME;
    }

    public static void reset() {
        LayerShared.THE.reset();
    }

    public EntityTypeEnum getEntityType() {
        return EntityTypeEnum.LAYER;
    }
}