package com.valtech.source.dependometer.app.core.elements;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
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
public class SubsystemShared {

    public final static SubsystemShared THE = new SubsystemShared();

    private SubsystemShared() {}

    // fully qualified names
    private final Map<String, Subsystem> subsystems = new TreeMap<String, Subsystem>();

    private final Set<Subsystem> notImplementedSubsystems = new TreeSet<Subsystem>();

    private final Set<DirectedDependency> forbiddenDependencies = new TreeSet<DirectedDependency>();

    private final Set<DirectedDependency> unusedDependencies = new TreeSet<DirectedDependency>();

    private List<Subsystem> projectInternalSubsystems;

    private int numberOfProjectInternalNotImplementedSubsystems = -1;

    private int numberOfProjectExternalNotImplementedSubsystems = -1;

    private int numberOfAllowedEfferents;

    public int getNumberOfEfferentDependencies() {
        return numberOfEfferentDependencies;
    }

    private int numberOfEfferentDependencies;

    private final List<Double> relationCohesionValues = new ArrayList<Double>();

    public List<Double> getRelationCohesionValues() {
        return relationCohesionValues;
    }

    public Set<DirectedDependency> getForbiddenDependencies() {
        return forbiddenDependencies;
    }

    public Set<DirectedDependency> getUnusedDependencies() {
        return unusedDependencies;
    }

    public Subsystem getSubsystem( String name ) {
        assert subsystems.containsKey(name);

        return subsystems.get( name );
    }

    public void addRelationCohesionValue( double rc ) {
        relationCohesionValues.add( rc );
    }

    public void increaseNumberOfEfferentDependencies() {
        numberOfEfferentDependencies++;
    }

    public void addForbiddenDependency( DirectedDependency directedDependency ) {
        forbiddenDependencies.add( directedDependency );
    }

    public void increaseNumberOfAllowedEfferents() {
        numberOfAllowedEfferents++;
    }

    public  Set<Subsystem> getNotImplementedSubsystems() {
        return notImplementedSubsystems;
    }


    public  int getNumberOfProjectInternalNotImplementedSubsystems() {
        return numberOfProjectInternalNotImplementedSubsystems;
    }

    public  int getNumberOfProjectExternalNotImplementedSubsystems() {
        return numberOfProjectExternalNotImplementedSubsystems;
    }

    public  int getNumberOfAllowedEfferents() {
        return numberOfAllowedEfferents;
    }

    public Collection<Subsystem> getSubsystems() {
        return subsystems.values();
    }

    public void reset()
    {
        subsystems.clear();
        notImplementedSubsystems.clear();
        forbiddenDependencies.clear();
        unusedDependencies.clear();

        projectInternalSubsystems = null;

        numberOfProjectInternalNotImplementedSubsystems = -1;
        numberOfProjectExternalNotImplementedSubsystems = -1;
        numberOfAllowedEfferents = 0;
        numberOfEfferentDependencies = 0;
        relationCohesionValues.clear();
    }

    public boolean subsystemExists( String name ) {
        return subsystems.containsKey( name );
    }

    // TODO douplicate of other shared + scare design
    public List<Subsystem> getProjectInternalSubsystems() {
        if (projectInternalSubsystems == null)
        {
            projectInternalSubsystems = new ArrayList<Subsystem>();

            for ( Subsystem subsystem : getSubsystems() ) {
                if ( subsystem.belongsToProject()) {
                    projectInternalSubsystems.add( subsystem );
                }
            }
        }

        return projectInternalSubsystems;
    }

    public void resetNumberOfProjectNotImplementedSubsystems() {
        numberOfProjectInternalNotImplementedSubsystems = 0;
        numberOfProjectExternalNotImplementedSubsystems = 0;

    }

    public void increaseNumberProjectInternalNotImplementedSubsystems() {
        numberOfProjectInternalNotImplementedSubsystems++;
    }

    public void increaseNumberProjectExternalNotImplementedSubsystems() {
        numberOfProjectExternalNotImplementedSubsystems++;
    }

    public void addSubsystem( Subsystem subsystem ) {
        subsystems.put( subsystem.getFullyQualifiedName(), subsystem );
    }

    public void addNotImplementedSubsystem( Subsystem subsystem ) {
        notImplementedSubsystems.add( subsystem );
    }

    public void removeNotImplementedSubsystem( Subsystem subsystem ) {
        notImplementedSubsystems.remove( subsystem );
    }

    public void resetSome() {
        projectInternalSubsystems = null;
        numberOfProjectInternalNotImplementedSubsystems = -1;
        numberOfProjectExternalNotImplementedSubsystems = -1;
    }

    public void addUnusedDependency( DirectedDependency directedDependency ) {
        unusedDependencies.add( directedDependency );
    }
}
