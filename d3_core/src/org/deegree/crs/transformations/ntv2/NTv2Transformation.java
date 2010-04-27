//$HeadURL$
/*----------------    FILE HEADER  ------------------------------------------
 This file is part of deegree.
 Copyright (C) 2001-2009 by:
 Department of Geography, University of Bonn
 http://www.giub.uni-bonn.de/deegree/
 lat/lon GmbH
 http://www.lat-lon.de

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.
 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.
 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 Contact:

 Andreas Poth
 lat/lon GmbH
 Aennchenstr. 19
 53177 Bonn
 Germany
 E-Mail: poth@lat-lon.de

 Prof. Dr. Klaus Greve
 Department of Geography
 University of Bonn
 Meckenheimer Allee 166
 53115 Bonn
 Germany
 E-Mail: greve@giub.uni-bonn.de
 ---------------------------------------------------------------------------*/

package org.deegree.crs.transformations.ntv2;

import static org.slf4j.LoggerFactory.getLogger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import javax.vecmath.Point3d;

import org.deegree.crs.CRSCodeType;
import org.deegree.crs.CRSIdentifiable;
import org.deegree.crs.CRSRegistry;
import org.deegree.crs.components.Ellipsoid;
import org.deegree.crs.coordinatesystems.CoordinateSystem;
import org.deegree.crs.exceptions.TransformationException;
import org.deegree.crs.transformations.Transformation;
import org.slf4j.Logger;

import au.com.objectix.jgridshift.GridShift;
import au.com.objectix.jgridshift.GridShiftFile;

/**
 * The <code>NTv2Transformation</code> class TODO add class documentation here.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 * 
 */
public class NTv2Transformation extends Transformation {

    private static final Logger LOG = getLogger( NTv2Transformation.class );

    private final GridShiftFile gsf;

    private boolean isIdentity;

    private URL gridURL;

    /**
     * @param sourceCRS
     * @param targetCRS
     * @param id
     * @param gridURL
     */
    public NTv2Transformation( CoordinateSystem sourceCRS, CoordinateSystem targetCRS, CRSIdentifiable id, URL gridURL ) {
        super( sourceCRS, targetCRS, id );
        if ( gridURL == null ) {
            throw new NullPointerException( "The NTv2 transformation needs a grid file to work on." );
        }
        try {
            this.gridURL = gridURL;
            InputStream in = gridURL.openStream();
            gsf = new GridShiftFile();
            gsf.loadGridShiftFile( in, true );
        } catch ( FileNotFoundException e ) {
            LOG.debug( "Could not find the gridshift file stack trace.", e );
            LOG.error( "Could not find the gridshift file because: " + e.getLocalizedMessage() );
            throw new IllegalArgumentException( "Could not load the gridshift file.", e );
        } catch ( IOException e ) {
            LOG.debug( "Could not read the gridshift file stack trace.", e );
            LOG.error( "Could not read the gridshift file because: " + e.getLocalizedMessage() );
            throw new IllegalArgumentException( "Could not load the gridshift file.", e );
        }

        String fromEllips = gsf.getFromEllipsoid();
        String toEllips = gsf.getToEllipsoid();

        Ellipsoid sourceEl = sourceCRS.getGeodeticDatum().getEllipsoid();
        Ellipsoid targetEl = targetCRS.getGeodeticDatum().getEllipsoid();

        // rb: patched the gridshift file for access to the axis
        if ( Math.abs( sourceEl.getSemiMajorAxis() - gsf.getFromSemiMajor() ) > 0.001
             || Math.abs( sourceEl.getSemiMinorAxis() - gsf.getFromSemiMinor() ) > 0.001 ) {
            LOG.warn( "The given source CRS' ellipsoid (" + sourceEl.getCode().getOriginal()
                      + ") does not match the 'from' ellipsoid (" + fromEllips + ")defined in the gridfile: " + gridURL );
        }

        if ( Math.abs( targetEl.getSemiMajorAxis() - gsf.getToSemiMajor() ) > 0.001
             || Math.abs( targetEl.getSemiMinorAxis() - gsf.getToSemiMinor() ) > 0.001 ) {
            LOG.warn( "The given target CRS' ellipsoid (" + targetEl.getCode().getOriginal()
                      + ") does not match the 'to' ellipsoid (" + toEllips + ") defined in the gridfile: " + gridURL );
        }

        isIdentity = ( Math.abs( gsf.getFromSemiMajor() - gsf.getToSemiMajor() ) < 0.001 )
                     && ( Math.abs( gsf.getFromSemiMinor() - gsf.getToSemiMinor() ) < 0.001 );
    }

    @Override
    public List<Point3d> doTransform( List<Point3d> srcPts )
                            throws TransformationException {
        GridShift shifter = new GridShift();
        if ( !isInverseTransform() ) {

            for ( Point3d p : srcPts ) {
                // shifter.setLatSeconds( p.x )
                p.x = shifter.getShiftedLonPositiveWestSeconds();
                p.y = shifter.getShiftedLatSeconds();
            }
        } else {
            for ( Point3d p : srcPts ) {
                // shifter.setLatSeconds( p.x )
                p.x = shifter.getShiftedLonPositiveWestSeconds();
                p.y = shifter.getShiftedLatSeconds();
            }
        }
        return null;
    }

    @Override
    public String getImplementationName() {
        return "NTv2";
    }

    @Override
    public boolean isIdentity() {
        return isIdentity;
    }

    public static void main( String[] args )
                            throws Exception {
        CoordinateSystem source = CRSRegistry.lookup( "EPSG:4314" );
        CoordinateSystem target = CRSRegistry.lookup( "EPSG:4258" );
        NTv2Transformation ntv2 = new NTv2Transformation(
                                                          source,
                                                          target,
                                                          new CRSIdentifiable( new CRSCodeType( "a" ) ),
                                                          new URL(
                                                                   "file:/home/rutger/workspace/bkg_wps/resources/data/BETA2007.gsb" ) );
        System.out.println( "Is identity: " + ntv2.isIdentity() );

    }

    /**
     * @return the url to the gridfile.
     */
    public URL getGridfile() {
        return this.gridURL;
    }
}
