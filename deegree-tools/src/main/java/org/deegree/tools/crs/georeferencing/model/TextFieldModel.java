//$HeadURL$
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 - Department of Geography, University of Bonn -
 and
 - lat/lon GmbH -

 This library is free software; you can redistribute it and/or modify it under
 the terms of the GNU Lesser General Public License as published by the Free
 Software Foundation; either version 2.1 of the License, or (at your option)
 any later version.
 This library is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 details.
 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation, Inc.,
 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

 Contact information:

 lat/lon GmbH
 Aennchenstr. 19, 53177 Bonn
 Germany
 http://lat-lon.de/

 Department of Geography, University of Bonn
 Prof. Dr. Klaus Greve
 Postfach 1147, 53001 Bonn
 Germany
 http://www.geographie.uni-bonn.de/deegree/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.tools.crs.georeferencing.model;

import java.util.regex.Pattern;

/**
 * Model that holds the relevant information for textfields.
 * 
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class TextFieldModel {

    private double xCoordinate;

    private double yCoordiante;

    private double spanX;

    private double spanY;

    /**
     * <Code>" "</Code> or <br>
     * ";" or <br>
     * "/" or
     * 
     */
    private final static String separator = "\\p{Space}*[ ;/]\\p{Space}*";

    private String textInput;

    public TextFieldModel() {

    }

    public void setTextInput( String textInput ) {
        this.textInput = textInput;
        String[] inputParameters = null;
        Pattern p = Pattern.compile( separator );
        inputParameters = p.split( textInput );
        int numberOfParameters = inputParameters.length;

        for ( int i = 0; i < inputParameters.length; i += numberOfParameters ) {
            if ( numberOfParameters < 1 ) {
                throw new ArrayIndexOutOfBoundsException(
                                                          "The minimum number of parameters is 2 - xCoordinate and yCoordinate! " );
            } else {
                try {
                    xCoordinate = Double.parseDouble( inputParameters[i] );
                    yCoordiante = Double.parseDouble( inputParameters[i + 1] );

                } catch ( NumberFormatException e ) {
                    xCoordinate = 0.0;
                    yCoordiante = 0.0;

                }

                if ( numberOfParameters > 2 ) {
                    try {
                        spanX = Double.parseDouble( inputParameters[i + 2] );
                        spanY = Double.parseDouble( inputParameters[i + 3] );
                    } catch ( NumberFormatException e ) {
                        spanX = -1;
                        spanY = -1;
                    }
                }
                if ( inputParameters.length > 4 ) {
                    throw new ArrayIndexOutOfBoundsException(
                                                              "The maximum number of parameters is 4 - xCoordinate, yCoordinate, spanX and spanY! " );
                }
            }

        }
    }

    /**
     * The first parameter of the string.
     * 
     * @return the xCoordinate
     */
    public double getxCoordinate() {
        return xCoordinate;
    }

    /**
     * The second parameter of the string.
     * 
     * @return the yCoordinate
     */
    public double getyCoordiante() {
        return yCoordiante;
    }

    /**
     * The optional third parameter of the string to get the width.
     * 
     * @return the spanX, if not set, this value is <i>-1</i>
     */
    public double getSpanX() {
        return spanX;
    }

    /**
     * The optional fourth parameter of the string to get the height.
     * 
     * @return the spanY, if not set, this value is <i>-1</i>
     */
    public double getSpanY() {
        return spanY;
    }

    public static String getSeparator() {
        return separator;
    }

    public String getTextInput() {
        return textInput;
    }

    public String getTooltipText() {
        StringBuilder sb = new StringBuilder();
        sb.append( "<html><center>" );
        sb.append( "Example: x-Coordinate y-Coordinate [width height]" ).append( "<br>" );
        sb.append( "The separators between the parameters are: SPACE, SEMICOLON, SLASH" );
        sb.append( "</center></html>" );
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String preSpace = "               ";
        sb.append( "\nTextfieldimput \n" );
        sb.append( preSpace ).append( "X-Coordinate: " ).append( "\t" ).append( xCoordinate ).append( "\n" );
        sb.append( preSpace ).append( "Y-Coodinate: " ).append( "\t" ).append( yCoordiante ).append( "\n" );
        sb.append( preSpace ).append( "Width: " ).append( "\t\t" ).append( spanX ).append( "\n" );
        sb.append( preSpace ).append( "Height: " ).append( "\t\t" ).append( spanY ).append( "\n" );

        return sb.toString();
    }

}
