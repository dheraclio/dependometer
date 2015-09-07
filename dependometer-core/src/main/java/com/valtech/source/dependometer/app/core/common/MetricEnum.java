package com.valtech.source.dependometer.app.core.common;

public enum MetricEnum {

    DEPENDS_UPON( "depends upon" ),
    NUMBER_OF_TYPES( "number of types (Nc)" ),
    NUMBER_OF_ACCESSIBLE_TYPES( "number of accessible types" ),
    NUMBER_OF_CONCRETE_TYPES( "number of concrete types" ),
    NUMBER_OF_ABSTRACT_TYPES( "number of abstract types (Na)" ),
    NUMBER_OF_INCOMING_DEPENDENCIES( "number of incoming dependencies" ),
    NUMBER_OF_OUTGOING_DEPENDENCIES( "number of outgoing dependencies" ),
    NUMBER_OF_PACKAGE_EXTERNAL_RELATIONS( "number of package external relations" ),
    NUMBER_OF_PACKAGE_INTERNAL_RELATIONS( "number of package internal relations" ),
    NUMBER_OF_FORBIDDEN_OUTGOING_DEPENDENCIES( "number of forbidden outgoing dependencies" ),
    NUMBER_OF_OUTGOING_DEPENDENCIES_TO_PROJECT_EXTERNAL( "number of outgoing dependencies to project external" ),
    NUMBER_OF_ASSERTIONS( "number of assertions" ),
    AFFERENT_INCOMING_COUPLING( "afferent (incoming) coupling (Ca)" ),
    EFFERENT_OUTGOING_COUPLING( "efferent (outgoing) coupling (Ce)" ),
    AVERAGE_COMPONENT_DEPENDENCY( "average component dependency (ACD)" ),
    CUMULATIVE_COMPONENT_DEPENDENCY( "cumulative component dependency (CCD)" ),
    DEPTH_OF_PACKAGE_HIERARCHY( "depth of package hierarchy" ),
    ABSTRACTNESS( "abstractness (A)" ),
    INSTABILITY( "instability (I)" ),
    DISTANCE( "distance (D)" ),
    NUMBER_OF_COMPONENTS( "number of components" ),
    NUMBER_OF_EXTERNAL_TYPE_RELATIONS( "number of external type relations" ),
    NUMBER_OF_INTERNAL_TYPE_RELATIONS( "number of internal type relations" ),
    PROJECT_INTERNAL( "project internal" ),
    RELATIONAL_COHESION( "relational cohesion (RC)" ),
    NUMBER_OF_SKIP_NODES( "number of skipped nodes" ),
    NUMBER_OF_IGNORE_NODES( "number of ignore nodes" ),
    NUMBER_OF_REFACTORING_NODES( "number of refactoring nodes" ),
    AVERAGE_USAGE_OF_ASSERTIONS_PER_CLASS( "average usage of assertions per class" ),
    CUMULATIVE_COMPONENT_DEPENDENCY_FOR_BALANCED_BINARY_TREE( "cumulative component dependency for balanced binary tree" ),
    CUMULATIVE_COMPONENT_DEPENDENCY_FOR_CYCLICALLY_DEPENDENT_GRAPH( "cumulative component dependency for cyclically dependent graph" ),
    CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_COMPILATION_UNITS( "cycles exist between project internal compilation units" ),
    CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_LAYERS( "cycles exist between project internal layers" ),
    CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_PACKAGES( "cycles exist between project internal packages" ),
    CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_SUBSYSTEM( "cycles exist between project internal subsystems" ),
    CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_TYPES( "cycles exist between project internal types" ),
    CYCLES_EXIST_BETWEEN_PROJECT_INTERNAL_VERTICAL_SLICES( "cycles exist between project internal vertical slices" ),
    MAX_DEPTH_OF_PACKAGE_HIERARCHY( "max depth of package hierarchy" ),
    MAX_DEPTH_OF_TYPE_INHERITANCE( "max depth of type inheritance" ),
    NORMALIZED_CUMULATIVE_COMPONENT_DEPENDENCY( "normalized cumulative component dependency (NCCD)" ),
    NUMBER_OF_ALLOWED_OUTGOING_LAYER_DEPENDENCIES( "number of allowed outgoing layer dependencies" ),
    NUMBER_OF_ALLOWED_OUTGOING_PACKAGE_DEPENDENCIES( "number of allowed outgoing package dependencies" ),
    NUMBER_OF_ALLOWED_OUTGOING_SUBSYSTEM_DEPENDENCIES( "number of allowed outgoing subsystem dependencies" ),
    NUMBER_OF_FORBIDDEN_OUTGOING_COMPILATION_UNIT_DEPENDENCIES( "number of forbidden outgoing compilation unit dependencies" ),
    NUMBER_OF_FORBIDDEN_OUTGOING_LAYER_DEPENDENCIES( "number of forbidden outgoing layer dependencies" ),
    NUMBER_OF_FORBIDDEN_OUTGOING_PACKAGE_DEPENDENCIES( "number of forbidden outgoing package dependencies" ),
    NUMBER_OF_FORBIDDEN_OUTGOING_SUBSYSTEM_DEPENDENCIES( "number of forbidden outgoing subsystem dependencies" ),
    NUMBER_OF_FORBIDDEN_OUTGOING_TYPE_DEPENDENCIES( "number of forbidden outgoing type dependencies" ),
    NUMBER_OF_FORBIDDEN_OUTGOING_VERTICAL_SLICE_DEPENDENCIES( "number of forbidden outgoing vertical slice dependencies" ),
    NUMBER_OF_NOT_ASSIGNED_PACKAGES( "number of not assigned packages" ),
    NUMBER_OF_NOT_IMPLEMENTED_SUBSYSTEMS( "number of not implemented subsystems" ),
    NUMBER_OF_OUTGOING_COMPILATION_UNIT_DEPENDENCIES( "number of outgoing compilation unit dependencies" ),
    NUMBER_OF_OUTGOING_LAYER_DEPENDENCIES( "number of outgoing layer dependencies" ),
    NUMBER_OF_OUTGOING_PACKAGE_DEPENDENCIES( "number of outgoing package dependencies" ),
    NUMBER_OF_OUTGOING_SUBSYSTEM_DEPENDENCIES( "number of outgoing subsystem dependencies" ),
    NUMBER_OF_OUTGOING_TYPE_DEPENDENCIES( "number of outgoing type dependencies" ),
    NUMBER_OF_OUTGOING_VERTICAL_SLICE_DEPENDENCIES( "number of outgoing vertical slice dependencies" ),
    NUMBER_OF_PROJECT_EXTERNAL_COMPILATION_UNITS( "number of project external compilation units" ),
    NUMBER_OF_PROJECT_EXTERNAL_LAYERS( "number of project external layers" ),
    NUMBER_OF_PROJECT_EXTERNAL_PACKAGES( "number of project external packages" ),
    NUMBER_OF_PROJECT_EXTERNAL_SUBSYSTEMS( "number of project external subsystems" ),
    NUMBER_OF_PROJECT_EXTERNAL_TYPES( "number of project external types" ),
    NUMBER_OF_PROJECT_INTERNAL_COMPILATION_UNITS( "number of project internal compilation units" ),
    NUMBER_OF_PROJECT_INTERNAL_LAYERS( "number of project internal layers" ),
    NUMBER_OF_PROJECT_INTERNAL_PACKAGES( "number of project internal packages" ),
    NUMBER_OF_PROJECT_INTERNAL_SUBSYSTEMS( "number of project internal subsystems" ),
    NUMBER_OF_PROJECT_INTERNAL_TYPES( "number of project internal types" ),
    NUMBER_OF_PROJECT_INTERNAL_VERTICAL_SLICES( "number of project internal vertical slices" ),
    NUMBER_OF_TYPE_CYCLES( "number of type cycles" ),
    NUMBER_OF_COMPILATION_UNIT_CYCLES( "number of compilation unit cycles" ),
    NUMBER_OF_PACKAGE_CYCLES( "number of package cycles" ),
    NUMBER_OF_SUBSYSTEM_CYCLES( "number of subsystem cycles" ),
    NUMBER_OF_VERTICAL_SLICES_CYCLES( "number of vertical cycles" ),
    NUMBER_OF_LAYER_CYCLES( "number of layer cycles" ),
    AFFERENT_INCOMING_COUPLING_PROJECT_EXTERNAL( "afferent (incoming) coupling (Ca) - project external" ),
    PERCENTAGE_OF_PROJECT_INTERNAL_VERTICAL_SLICES_WITH_A_RELATIONAL_COHESION_GREATER_THAN_ONE( "percentage of project internal vertical slices with a relational cohesion >= 1.0" ),
    PERCENTAGE_OF_PROJECT_INTERNAL_LAYERS_WITH_A_RELATIONAL_COHESION_GREATER_THAN_ONE( "percentage of project internal layers with a relational cohesion >= 1.0" ),
    PERCENTAGE_OF_PROJECT_INTERNAL_SUBSYSTEMS_WITH_A_RELATIONAL_COHESION_GREATER_THAN_ONE( "percentage of project internal subsystems with a relational cohesion >= 1.0" ),
    PERCENTAGE_OF_PROJECT_INTERNAL_PACKAGES_WITH_A_RELATIONAL_COHESION_GREATER_THAN_ONE( "percentage of project internal packages with a relational cohesion >= 1.0" ),
    NUMBER_OF_CONTAINED_SUBSYSTEMS( "number of contained subsystems" ),
    ABSTRACT( "abstract" ),
    DEPTH_OF_CLASS_INHERITANCE( "depth of class inheritance" ),
    DEPTH_OF_INTERFACE_INHERITANCE( "depth of interface inheritance" ),
    INTERFACE( "interface" ),
    NESTED( "nested" ),
    ACCESSIBLE( "accessible" ),
    NUMBER_OF_CHILDREN( "number of children (NOC)" ),
    NUMBER_OF_CONTAINED_PACKAGES( "number of contained packages" ),
    EXTENDABLE( "extendable" ),
    REFACTORED( "refactored" ),
    NO_INCOMING_DEPENCIES_DETECTED( "no incoming dependencies detected" ),
    PROJECT_EXTERNAL( "project external" ),
    NUMBER_OF_INCOMING_DEPENDENCIES_PROJECT_EXTERNAL( "number of incoming dependencies - project external" ),
    MORE_PACKAGE_EXTERNAL_THAN_INTERNAL_RELATIONS_EXIST( "more package external than internal relations exist" ),
    THE_MOST_EXTERNAL_RELATIONS_EXIST_WITH_PACKAGE( "the most external relations exist with package" ),
    CONTAINS_ACCESSIBLE_TYPES_BUT_NO_INCOMING_OUTER_PACKAGE_DEPENDENCIES_EXIST( "contains accessible types but no incoming outer package dependencies exist" ),
    NO_DEPENCIES_DETECTED( "no dependencies detected" ),
    NUMBER_OF_TYPE_TANGLES( "number of type tangles" ),
    NUMBER_OF_COMPILATION_UNIT_TANGLES( "number of compilation unit tangles" ),
    NUMBER_OF_PACKAGE_TANGLES( "number of package tangles" ),
    NUMBER_OF_SUBSYSTEM_TANGLES( "number of subsystem tangles" ),
    NUMBER_OF_LAYER_TANGLES( "number of layer tangles" ),
    NUMBER_OF_VERTICAL_SLICE_TANGLES("number of vertical slice tangles" );

    private final String displayName;

    public String getDisplayName() {
        return displayName;
    }

    private MetricEnum( String displayName ) {
        this.displayName = displayName;
    }

    public static MetricEnum parseByKey( String k ) {
        for( MetricEnum m : values() ) {
            if( k.equals( m.name() ) ) {
                return m;
            }
        }
        return null;
    }

    // TODO try to parse by key instead

    /**
     * @param displayName
     * @return
     * @deprecated
     */
    public static MetricEnum parseByDisplayName( String displayName ) {
        for( MetricEnum m : values() ) {
            if( displayName.equals( m.displayName ) ) {
                return m;
            }
        }
        assert false : "metric could not be parsed: '" + displayName + "'";

        return null;
    }
}