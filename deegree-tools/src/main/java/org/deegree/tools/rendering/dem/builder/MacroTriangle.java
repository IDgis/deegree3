//$HeadURL: svn+ssh://mschneider@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 and
 lat/lon GmbH

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

package org.deegree.tools.rendering.dem.builder;

import javax.vecmath.Point2f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.deegree.coverage.raster.data.RasterData;

/**
 * A macro triangle that consists of several "normal" triangles.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author: schneider $
 * 
 * @version $Revision: $, $Date: $
 */
public class MacroTriangle {

    enum Orientation {
        NORTH, SOUTH, WEST, EAST, NORTH_WEST, NORTH_EAST, SOUTH_WEST, SOUTH_EAST
    }

    // point opposite to the longest edge
    Point2f p0;

    Point2f p1;

    Point2f p2;

    private String locationCode;

    private DEMDatasetGenerator builder;

    /** The bbox of the macro triangle */
    public float[][] bbox;

    /** The geometric error of the macro triangle */
    public float geometryError;

    private float minZ = Float.MAX_VALUE;

    private float maxZ = Float.MIN_VALUE;

    private Orientation orientation;

    // private int level;

    private boolean disposeEachRow;

    /**
     * Constructs a new <code>MacroTriangle</code> with already initialized bounding box.
     * 
     * @param builder
     * @param p0
     * @param p1
     * @param p2
     * @param level
     * @param locationCode
     * @param bbox
     * @param geometryError
     */
    MacroTriangle( DEMDatasetGenerator builder, Point2f p0, Point2f p1, Point2f p2, int level, String locationCode,
                   float[][] bbox, float geometryError ) {
        this( builder, p0, p1, p2, level, locationCode, geometryError );
        this.bbox = bbox;
    }

    /**
     * Constructs a new <code>MacroTriangle</code> without bounding box.
     * <p>
     * The bounding box will be generated by the first call to {@link #getBBox()}.
     * 
     * @param builder
     * @param p0
     * @param p1
     * @param p2
     * @param level
     * @param locationCode
     * @param geometryError
     */
    MacroTriangle( DEMDatasetGenerator builder, Point2f p0, Point2f p1, Point2f p2, int level, String locationCode,
                   float geometryError ) {
        this.builder = builder;
        this.p0 = p0;
        this.p1 = p1;
        this.p2 = p2;
        // this.level = level;
        this.locationCode = locationCode;
        this.geometryError = geometryError;
        this.disposeEachRow = locationCode.length() == 1;
        this.orientation = getOrientation();
    }

    double getError() {
        return geometryError;
    }

    void generateTileData( RasterData dataBuffer, double tileHeight, int rowsPerTile, Point3f[] tileVertices,
                           Vector3f[] vertexNormals, int[][] tileTriangles ) {

        // determine helper vectors 'stepRight', 'halftStepDownRight' and 'halfStepDownLeft'
        Vector2f stepRight = new Vector2f();
        stepRight.sub( p2, p1 );
        stepRight.scale( 1.0f / ( rowsPerTile * 2.0f ) );

        Point2f middleBottom = new Point2f();
        middleBottom.sub( p2, p1 );
        middleBottom.scale( 0.5f );
        middleBottom.add( p1 );
        Vector2f stepUp = new Vector2f();
        stepUp.sub( p0, middleBottom );
        stepUp.scale( 1.0f / rowsPerTile );

        Vector2f halfStepDown = new Vector2f();
        Point2f midPoint = calcMidPoint( p2, p1 );
        halfStepDown.sub( midPoint, p0 );
        halfStepDown.scale( 1.0f / ( rowsPerTile * 2.0f ) );

        Vector2f halfStepDownLeft = new Vector2f();
        halfStepDownLeft.sub( p1, p2 );
        halfStepDownLeft.scale( 1.0f / ( rowsPerTile * 4.0f ) );
        halfStepDownLeft.add( halfStepDown );

        Vector2f halfStepDownRight = new Vector2f();
        halfStepDownRight.sub( p2, p1 );
        halfStepDownRight.scale( 1.0f / ( rowsPerTile * 4.0f ) );
        halfStepDownRight.add( halfStepDown );

        // build row 0 of points (just p0)
        int vertexId = 0;
        int normalId = 0;
        tileVertices[vertexId++] = build3DPoint( p0 );
        vertexNormals[normalId++] = getNormal( p0, stepRight, stepUp );

        Point2f lastRowLeftEnd = new Point2f( p0 );
        Point2f lastRowRightEnd = new Point2f( p0 );

        double firstHeight = lastRowLeftEnd.y;
        double newHeight = firstHeight - tileHeight;
        // build vertices of macro triangle starting at p0
        for ( int row = 1; row <= rowsPerTile; row++ ) {
            Point2f currentPos = new Point2f( lastRowLeftEnd );

            // build point left middle
            Point2f leftMiddle = new Point2f( currentPos );
            leftMiddle.add( halfStepDownLeft );
            tileVertices[vertexId++] = build3DPoint( leftMiddle );
            vertexNormals[normalId++] = getNormal( leftMiddle, stepRight, stepUp );

            // start: currentPos is the leftmost vertex position in this row
            currentPos = new Point2f( leftMiddle );
            currentPos.add( halfStepDownLeft );
            Point2f rowStart = new Point2f( currentPos );
            for ( int rowVertex = 0; rowVertex < row * 2 + 1; rowVertex++ ) {
                tileVertices[vertexId++] = build3DPoint( currentPos );
                vertexNormals[normalId++] = getNormal( currentPos, stepRight, stepUp );
                currentPos.add( stepRight );
            }

            // build point right middle
            Point2f rightMiddle = new Point2f( lastRowRightEnd );
            rightMiddle.add( halfStepDownRight );
            tileVertices[vertexId++] = build3DPoint( rightMiddle );
            vertexNormals[normalId++] = getNormal( rightMiddle, stepRight, stepUp );

            lastRowLeftEnd = rowStart;
            lastRowRightEnd = currentPos;
            currentPos.sub( stepRight );
            if ( !disposeEachRow ) {
                if ( currentPos.y < newHeight ) {
                    newHeight -= tileHeight;
                    dataBuffer.dispose();
                }
            } else {
                // System.out.println( "Dispose each row." );
                dataBuffer.dispose();
            }

        }
        dataBuffer.dispose();

        // build triangles
        int lastRowFirstVertexId = 0;
        int lastRowLastVertexId = 0;
        int triangleId = 0;
        int firstVertexId = 2;

        for ( int row = 1; row <= rowsPerTile; row++ ) {
            int rowLastVertexId = firstVertexId + row * 2;

            // build the two leftmost triangles
            tileTriangles[triangleId++] = new int[] { firstVertexId - 1, firstVertexId + 1, lastRowFirstVertexId };
            tileTriangles[triangleId++] = new int[] { firstVertexId - 1, firstVertexId, firstVertexId + 1 };

            for ( int i = 0; i < rowLastVertexId - firstVertexId - 2; i++ ) {
                int lastRowLeft = lastRowFirstVertexId + i;
                int lastRowRight = lastRowLeft + 1;
                int left = firstVertexId + i + 1;
                int right = left + 1;

                switch ( orientation ) {
                case NORTH:
                case SOUTH:
                case NORTH_WEST:
                case SOUTH_EAST: {
                    tileTriangles[triangleId++] = new int[] { lastRowLeft, left, lastRowRight };
                    tileTriangles[triangleId++] = new int[] { right, lastRowRight, left };
                    break;
                }
                case WEST:
                case EAST:
                case NORTH_EAST:
                case SOUTH_WEST: {
                    tileTriangles[triangleId++] = new int[] { left, right, lastRowLeft };
                    tileTriangles[triangleId++] = new int[] { lastRowRight, lastRowLeft, right };
                    break;
                }

                }
            }

            // build the two rightmost triangles
            tileTriangles[triangleId++] = new int[] { rowLastVertexId + 1, lastRowLastVertexId, rowLastVertexId - 1 };
            tileTriangles[triangleId++] = new int[] { rowLastVertexId + 1, rowLastVertexId - 1, rowLastVertexId };

            lastRowFirstVertexId = firstVertexId;
            lastRowLastVertexId = rowLastVertexId;
            firstVertexId = rowLastVertexId + 3;
            // dataBuffer.dispose();
        }
        // dataBuffer.dispose();
    }

    private Orientation getOrientation() {
        Orientation orientation = null;

        if ( p1.y == p2.y ) {
            if ( p0.y > p1.y ) {
                orientation = Orientation.NORTH;
            } else if ( p0.y < p1.y ) {
                orientation = Orientation.SOUTH;
            }
        } else if ( p1.x == p2.x ) {
            if ( p0.x > p1.x ) {
                orientation = Orientation.EAST;
            } else {
                orientation = Orientation.WEST;
            }
        } else {
            if ( p0.x == p1.x ) {
                if ( p0.y == p2.y ) {
                    orientation = Orientation.NORTH_WEST;
                } else {
                    orientation = Orientation.SOUTH_EAST;
                }
            } else {
                if ( p0.x == p2.x ) {
                    orientation = Orientation.SOUTH_WEST;
                } else {
                    orientation = Orientation.NORTH_EAST;
                }
            }
        }
        return orientation;
    }

    /**
     * @return the location code
     */
    public String getLocationCode() {
        return locationCode;
    }

    /**
     * @return the bbox of this Macrotriangle
     */
    public float[][] getBBox() {
        if ( bbox == null ) {
            addToBBox( p0 );
            addToBBox( p1 );
            addToBBox( p2 );
        }
        return bbox;
    }

    private void addToBBox( Point2f point ) {
        addToBBox( new Point3f( point.x, point.y, minZ ) );
        addToBBox( new Point3f( point.x, point.y, maxZ ) );
    }

    private void addToBBox( Point3f point ) {
        if ( bbox == null ) {
            bbox = new float[2][3];
            bbox[0][0] = point.x;
            bbox[0][1] = point.y;
            bbox[0][2] = point.z;
            bbox[1][0] = point.x;
            bbox[1][1] = point.y;
            bbox[1][2] = point.z;
        } else {
            if ( point.x < bbox[0][0] ) {
                bbox[0][0] = point.x;
            }
            if ( point.y < bbox[0][1] ) {
                bbox[0][1] = point.y;
            }
            if ( point.z < bbox[0][2] ) {
                bbox[0][2] = point.z;
            }

            if ( point.x > bbox[1][0] ) {
                bbox[1][0] = point.x;
            }
            if ( point.y > bbox[1][1] ) {
                bbox[1][1] = point.y;
            }
            if ( point.z > bbox[1][2] ) {
                bbox[1][2] = point.z;
            }
        }
    }

    private Point3f build3DPoint( Point2f p ) {
        float z = builder.getHeight( p.x, p.y );
        if ( z < minZ ) {
            minZ = z;
        }
        if ( z > maxZ ) {
            maxZ = z;
        }
        return new Point3f( p.x, p.y, z );
    }

    private Point2f calcMidPoint( Point2f pa, Point2f pb ) {
        float maxX = pa.x;
        float minX = pb.x;
        float maxY = pa.y;
        float minY = pb.y;
        if ( maxX < minX ) {
            maxX = pb.x;
            minX = pa.x;
        }
        if ( maxY < minY ) {
            maxY = pb.y;
            minY = pa.y;
        }
        float midX = ( maxX - minX ) / 2.0f + minX;
        float midY = ( maxY - minY ) / 2.0f + minY;
        return new Point2f( midX, midY );
    }

    private Vector3f getNormal( Point2f p, Vector2f stepRight, Vector2f stepUp ) {

        float x = p.x;
        float y = p.y;

        // if ( builder.heixelBuffer == null || x < 1.0f || x >= builder.inputX * builder.resX|| y < 1.0f || y >=
        // builder.inputY * builder.resY ) {
        // return new Vector3f( 0.0f, 0.0f, 1.0f );
        // }

        Point3f p0 = new Point3f( x, y, builder.getHeight( x, y ) );

        Point2f p12D = new Point2f( p );
        p12D.sub( stepRight );
        Point3f p1 = new Point3f( p12D.x, p12D.y, builder.getHeight( p12D ) );

        Point2f p22D = new Point2f( p );
        p22D.add( stepUp );
        Point3f p2 = new Point3f( p22D.x, p22D.y, builder.getHeight( p22D ) );

        Point2f p32D = new Point2f( p );
        p32D.add( stepUp );
        p32D.add( stepRight );
        Point3f p3 = new Point3f( p32D.x, p32D.y, builder.getHeight( p32D ) );

        Point2f p42D = new Point2f( p );
        p42D.add( stepRight );
        Point3f p4 = new Point3f( p42D.x, p42D.y, builder.getHeight( p42D ) );

        Point2f p52D = new Point2f( p );
        p52D.sub( stepUp );
        Point3f p5 = new Point3f( p52D.x, p52D.y, builder.getHeight( p52D ) );

        Point2f p62D = new Point2f( p );
        p62D.sub( stepUp );
        p62D.sub( stepRight );
        Point3f p6 = new Point3f( p62D.x, p62D.y, builder.getHeight( p62D ) );

        Vector3f n0 = calculateTriangleNormal( p0, p2, p1 );
        Vector3f n1 = calculateTriangleNormal( p0, p3, p2 );
        Vector3f n2 = calculateTriangleNormal( p0, p4, p3 );
        Vector3f n3 = calculateTriangleNormal( p0, p5, p4 );
        Vector3f n4 = calculateTriangleNormal( p0, p6, p5 );
        Vector3f n5 = calculateTriangleNormal( p0, p1, p6 );

        Vector3f normal = n0;
        normal.add( n1 );
        normal.add( n2 );
        normal.add( n3 );
        normal.add( n4 );
        normal.add( n5 );
        normal.normalize();
        return normal;
    }

    private Vector3f calculateTriangleNormal( Point3f p1, Point3f p2, Point3f p3 ) {
        Vector3f first = new Vector3f();
        first.sub( p3, p1 );
        Vector3f second = new Vector3f();
        second.sub( p3, p2 );
        Vector3f normal = new Vector3f();
        normal.cross( first, second );
        normal.normalize();
        return normal;
    }
}
