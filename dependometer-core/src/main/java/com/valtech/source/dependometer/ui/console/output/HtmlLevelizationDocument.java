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
package com.valtech.source.dependometer.ui.console.output;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import com.valtech.source.ag.util.AssertionUtility;
import com.valtech.source.dependometer.app.core.provider.DependencyElementIf;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class HtmlLevelizationDocument extends HtmlDocument {
    private final String type;

    private boolean titleWritten;

    private int excludedElements;

    protected HtmlLevelizationDocument( File directory, String typeName ) throws IOException {
        super( directory );
        assert typeName != null;
        assert typeName.length() > 0;
        type = typeName;
        open( typeName + "-levels.html" );
    }

    public void setNumberOfExcludedElements( int excluded ) {
        synchronized( this ) {
            assert !titleWritten;
            assert excluded >= 0;
            excludedElements = excluded;
        }
    }

    void addLevel( int level, DependencyElementIf[] elements ) {
        assert AssertionUtility.checkArray( elements );
        synchronized( this ) {
            if( !titleWritten ) {
                StringBuffer title = new StringBuffer( type );
                title.append( " levelization" );
                if( excludedElements > 0 ) {
                    title.append( " (" );
                    title.append( excludedElements );
                    title.append( " " );
                    title.append( type );
                    title.append( "(s) excluded - due to direct cycle participation or not completed analysis)" );
                }
                writeAnchoredTitle( "levels", title.toString() );
                titleWritten = true;
            }

            PrintWriter writer = getWriter();
            assert level > 0;
            writer.println( "<h3> " + type + "s on level " + level + " (" + elements.length + ")</h3>" );
            writer.println( "<table>" );
            for( int i = 0; i < elements.length; i++ ) {
                DependencyElementIf next = elements[ i ];
                writer.println( "<tr>" );
                writer.println( "<td>" );
                writeHRef( next.hashCode() + ".html#element", next.getFullyQualifiedName() );
                writer.println( "</td>" );
                writer.println( "</tr>" );
            }
            writer.println( "</table>" );
            writer.flush();
        }
    }
}