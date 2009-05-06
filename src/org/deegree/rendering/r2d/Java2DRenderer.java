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

package org.deegree.rendering.r2d;

import static java.awt.BasicStroke.CAP_BUTT;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.CAP_SQUARE;
import static java.awt.BasicStroke.JOIN_BEVEL;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.BasicStroke.JOIN_ROUND;
import static java.awt.Font.BOLD;
import static java.awt.Font.ITALIC;
import static java.awt.Font.PLAIN;
import static java.awt.geom.AffineTransform.getTranslateInstance;
import static org.deegree.commons.utils.math.MathUtils.isZero;
import static org.deegree.commons.utils.math.MathUtils.round;
import static org.deegree.rendering.r2d.RenderHelper.renderMark;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.TexturePaint;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D.Double;
import java.awt.image.BufferedImage;
import java.util.List;

import org.deegree.geometry.Envelope;
import org.deegree.geometry.Geometry;
import org.deegree.geometry.primitive.Curve;
import org.deegree.geometry.primitive.Point;
import org.deegree.geometry.primitive.Surface;
import org.deegree.geometry.primitive.curvesegments.LineStringSegment;
import org.deegree.geometry.primitive.surfacepatches.PolygonPatch;
import org.deegree.geometry.primitive.surfacepatches.SurfacePatch;
import org.deegree.rendering.r2d.strokes.OffsetStroke;
import org.deegree.rendering.r2d.strokes.TextStroke;
import org.deegree.rendering.r2d.styling.LineStyling;
import org.deegree.rendering.r2d.styling.PointStyling;
import org.deegree.rendering.r2d.styling.PolygonStyling;
import org.deegree.rendering.r2d.styling.TextStyling;
import org.deegree.rendering.r2d.styling.components.Fill;
import org.deegree.rendering.r2d.styling.components.Graphic;
import org.deegree.rendering.r2d.styling.components.Stroke;
import org.slf4j.Logger;

/**
 * <code>Java2DRenderer</code>
 * 
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class Java2DRenderer implements Renderer {

    private static final Logger LOG = getLogger( Java2DRenderer.class );

    private Graphics2D graphics;

    private AffineTransform worldToScreen = new AffineTransform();

    /**
     * @param graphics
     * @param width
     * @param height
     * @param bbox
     */
    public Java2DRenderer( Graphics2D graphics, int width, int height, Envelope bbox ) {
        this.graphics = graphics;

        if ( bbox != null ) {
            double scalex = width / bbox.getWidth();
            double scaley = height / bbox.getHeight();
            // always use the bigger scale to determine line width etc.
            double scale = ( 1 / ( scalex > scaley ? scalex : scaley ) );

            // we have to flip horizontally, so invert y scale and add the screen height
            worldToScreen.translate( -bbox.getMin().getX() * scalex, bbox.getMin().getY() * scaley + height );
            worldToScreen.scale( scalex, -scaley );

            LOG.debug( "Rendering with scale {}", scale );
            LOG.debug( "For coordinate transformations, scaling by x = {} and y = {}", scalex, -scaley );
            LOG.trace( "Final transformation was {}", worldToScreen );
        } else {
            LOG.warn( "No envelope given, proceeding with a scale of 1." );
        }
    }

    /**
     * @param graphics
     */
    public Java2DRenderer( Graphics2D graphics ) {
        this.graphics = graphics;
    }

    void applyGraphicFill( Graphic graphic ) {
        double width = graphic.size;
        double height = graphic.size;

        if ( width < 0 ) {
            if ( graphic.image == null ) {
                width = 6;
                height = 6;
            } else {
                width = graphic.image.getWidth();
                height = graphic.image.getHeight();
            }
        }

        double x0 = width * graphic.anchorPointX + graphic.displacementX;
        double y0 = height * graphic.anchorPointY + graphic.displacementY;

        BufferedImage img;

        if ( graphic.image == null ) {
            img = renderMark( graphic.mark, graphic.size < 0 ? 6 : round( graphic.size ) );
        } else {
            img = graphic.image;
        }

        graphics.setPaint( new TexturePaint( img, new Rectangle2D.Double( x0, y0, width, height ) ) );
    }

    void applyFill( Fill fill ) {
        if ( fill == null ) {
            graphics.setPaint( new Color( 0, 0, 0, 0 ) );
            return;
        }

        if ( fill.graphic == null ) {
            graphics.setPaint( fill.color );
        } else {
            applyGraphicFill( fill.graphic );
        }
    }

    void applyStroke( Stroke stroke ) {
        if ( stroke == null || isZero( stroke.width ) ) {
            graphics.setPaint( new Color( 0, 0, 0, 0 ) );
            return;
        }
        if ( stroke.stroke == null && stroke.fill == null ) {
            graphics.setPaint( stroke.color );
        }
        if ( stroke.fill != null ) {
            applyGraphicFill( stroke.fill );
        }
        if ( stroke.stroke != null ) {
        }

        int linecap = CAP_SQUARE;
        if ( stroke.linecap != null ) {
            switch ( stroke.linecap ) {
            case BUTT:
                linecap = CAP_BUTT;
                break;
            case ROUND:
                linecap = CAP_ROUND;
                break;
            case SQUARE:
                linecap = CAP_SQUARE;
                break;
            }
        }
        int linejoin = JOIN_MITER;
        float miterLimit = 10;
        if ( stroke.linejoin != null ) {
            switch ( stroke.linejoin ) {
            case BEVEL:
                linejoin = JOIN_BEVEL;
                break;
            case MITRE:
                linejoin = JOIN_MITER;
                break;
            case ROUND:
                linejoin = JOIN_ROUND;
                break;
            }
        }
        float dashoffset = (float) ( stroke.dashoffset );
        float[] dasharray = stroke.dasharray == null ? null : new float[stroke.dasharray.length];
        if ( stroke.dasharray != null ) {
            for ( int i = 0; i < stroke.dasharray.length; ++i ) {
                dasharray[i] = (float) ( stroke.dasharray[i] );
            }
        }

        BasicStroke bs = new BasicStroke( (float) ( stroke.width ), linecap, linejoin, miterLimit, dasharray,
                                          dashoffset );

        graphics.setStroke( bs );
    }

    private void render( TextStyling styling, Font font, String text, Point p ) {
        double x = p.getX() + styling.displacementX;
        double y = p.getY() + styling.displacementY;
        graphics.setFont( font );
        AffineTransform transform = graphics.getTransform();
        graphics.rotate( styling.rotation, x, y );
        TextLayout layout = new TextLayout( text, font, graphics.getFontRenderContext() );
        double width = layout.getBounds().getWidth();
        double height = layout.getBounds().getHeight();
        double px = x - styling.anchorPointX * width; // width/height already include the scale through the font
        // render
        // context
        double py = y + styling.anchorPointY * height;

        if ( styling.halo != null ) {
            applyFill( styling.halo.fill );

            BasicStroke stroke = new BasicStroke( round( 2 * styling.halo.radius ), CAP_BUTT, JOIN_ROUND );
            graphics.setStroke( stroke );
            graphics.draw( layout.getOutline( getTranslateInstance( px, py ) ) );
        }

        graphics.setStroke( new BasicStroke() );

        applyFill( styling.fill );
        layout.draw( graphics, (float) px, (float) py );

        graphics.setTransform( transform );
    }

    private void render( TextStyling styling, Font font, String text, Curve c ) {
        applyFill( styling.fill );
        java.awt.Stroke stroke = new TextStroke( text, font, styling.linePlacement );
        if ( !isZero( styling.linePlacement.perpendicularOffset ) ) {
            stroke = new OffsetStroke( styling.linePlacement.perpendicularOffset, stroke );
        }

        graphics.setStroke( stroke );
        Double line = fromCurve( c );

        graphics.draw( line );
    }

    public void render( TextStyling styling, String text, Geometry geom ) {
        if ( geom == null ) {
            LOG.debug( "Trying to render null geometry." );
            return;
        }

        int style = styling.font.bold ? BOLD : PLAIN;
        switch ( styling.font.fontStyle ) {
        case ITALIC:
            style += ITALIC;
            break;
        case NORMAL:
            style += PLAIN; // yes, it's zero, but the code looks nicer this way
            break;
        case OBLIQUE:
            LOG.warn( "The oblique font style is not supported, using italic instead." );
            style += ITALIC; // TODO something better here?
            break;
        }

        // use the first matching name, or Dialog, if none was found
        int size = round( styling.font.fontSize );
        Font font = new Font( "", style, size );
        for ( String name : styling.font.fontFamily ) {
            font = new Font( name, style, size );
            if ( !font.getFamily().equalsIgnoreCase( "dialog" ) ) {
                break;
            }
        }

        if ( geom instanceof Point ) {
            render( styling, font, text, (Point) geom );
        }
        if ( geom instanceof Surface ) {
            Surface surface = (Surface) geom;
            if ( styling.linePlacement != null ) {
                for ( SurfacePatch patch : surface.getPatches() ) {
                    if ( patch instanceof PolygonPatch ) {
                        PolygonPatch polygonPatch = (PolygonPatch) patch;
                        for ( Curve curve : polygonPatch.getBoundaryRings() ) {
                            render( styling, font, text, curve );
                        }
                    } else {
                        throw new IllegalArgumentException( "Cannot render non-planar surfaces." );
                    }
                }
            } else {
                render( styling, font, text, surface.getCentroid() );
            }
        }
        if ( geom instanceof Curve ) {
            if ( styling.linePlacement != null ) {
                render( styling, font, text, (Curve) geom );
            }
        }
    }

    private void render( PointStyling styling, double x, double y ) {
        Point2D.Double p = (Point2D.Double) worldToScreen.transform( new Point2D.Double( x, y ), null );
        x = p.x;
        y = p.y;

        Graphic g = styling.graphic;

        BufferedImage img;

        if ( g.image == null ) {
            img = renderMark( g.mark, g.size < 0 ? 6 : round( g.size ) );
        } else {
            img = g.image;
        }

        x += g.displacementX;
        y += g.displacementY;
        x -= g.anchorPointX * img.getWidth();
        y -= g.anchorPointY * img.getHeight();

        graphics.drawImage( img, round( x ), round( y ), img.getWidth(), img.getHeight(), null );
    }

    public void render( PointStyling styling, Geometry geom ) {
        if ( geom == null ) {
            LOG.debug( "Trying to render null geometry." );
            return;
        }

        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "Drawing " + geom + " with " + styling );
        }

        if ( geom instanceof Point ) {
            render( styling, ( (Point) geom ).getX(), ( (Point) geom ).getY() );
        }
        // TODO properly convert'em
        if ( geom instanceof Surface ) {
            Surface surface = (Surface) geom;
            for ( SurfacePatch patch : surface.getPatches() ) {
                if ( patch instanceof PolygonPatch ) {
                    PolygonPatch polygonPatch = (PolygonPatch) patch;
                    for ( Curve curve : polygonPatch.getBoundaryRings() ) {
                        render( styling, curve );
                    }
                } else {
                    throw new IllegalArgumentException( "Cannot render non-planar surfaces." );
                }
            }
        }
        if ( geom instanceof Curve ) {
            Curve curve = (Curve) geom;
            if ( curve.getCurveSegments().size() != 1
                 || !( curve.getCurveSegments().get( 0 ) instanceof LineStringSegment ) ) {
                // TODO handle non-linear and multiple curve segments
                throw new IllegalArgumentException();
            }
            LineStringSegment segment = ( (LineStringSegment) curve.getCurveSegments().get( 0 ) );
            // coordinate representation is still subject to change...
            for ( Point point : segment.getControlPoints() ) {
                render( styling, point );
            }
        }
    }

    private Double fromCurve( Curve curve ) {
        curve = curve.getAsLineString();
        if ( curve.getCurveSegments().size() != 1 || !( curve.getCurveSegments().get( 0 ) instanceof LineStringSegment ) ) {
            // TODO handle non-linear and multiple curve segments
            throw new IllegalArgumentException();
        }
        LineStringSegment segment = ( (LineStringSegment) curve.getCurveSegments().get( 0 ) );

        Double line = new Double();
        // coordinate representation is still subject to change...
        List<Point> points = segment.getControlPoints();
        Point p = points.get( 0 );
        line.moveTo( p.getX(), p.getY() );
        for ( Point point : segment.getControlPoints() ) {
            if ( point == p ) {
                continue;
            }
            line.lineTo( point.getX(), point.getY() );
        }
        line.transform( worldToScreen );

        return line;
    }

    public void render( LineStyling styling, Geometry geom ) {
        if ( geom == null ) {
            LOG.debug( "Trying to render null geometry." );
            return;
        }

        if ( LOG.isTraceEnabled() ) {
            LOG.trace( "Drawing " + geom + " with " + styling );
        }

        if ( geom instanceof Point ) {
            LOG.warn( "Trying to render point with line styling." );
        }
        if ( geom instanceof Curve ) {
            Double line = fromCurve( (Curve) geom );
            applyStroke( styling.stroke );
            if ( !isZero( styling.perpendicularOffset ) ) {
                graphics.setStroke( new OffsetStroke( styling.perpendicularOffset, graphics.getStroke() ) );
            }
            graphics.draw( line );
        }
        if ( geom instanceof Surface ) {
            Surface surface = (Surface) geom;
            for ( SurfacePatch patch : surface.getPatches() ) {
                if ( patch instanceof PolygonPatch ) {
                    PolygonPatch polygonPatch = (PolygonPatch) patch;
                    for ( Curve curve : polygonPatch.getBoundaryRings() ) {
                        render( styling, curve );
                    }
                } else {
                    throw new IllegalArgumentException( "Cannot render non-planar surfaces." );
                }
            }
        }
    }

    private void render( PolygonStyling styling, Surface surface ) {
        for ( SurfacePatch patch : surface.getPatches() ) {
            if ( patch instanceof PolygonPatch ) {
                PolygonPatch polygonPatch = (PolygonPatch) patch;
                Area polygon = null;
                for ( Curve curve : polygonPatch.getBoundaryRings() ) {
                    if ( polygon == null ) {
                        polygon = new Area( fromCurve( curve ) );
                    } else {
                        polygon.subtract( new Area( fromCurve( curve ) ) );
                    }
                }

                if ( polygon == null ) {
                    LOG.warn( "Trying to render polygon without rings." );
                    return;
                }

                polygon.transform( worldToScreen );

                applyFill( styling.fill );
                graphics.fill( polygon );
                applyStroke( styling.stroke );
                graphics.draw( polygon );
            } else {
                throw new IllegalArgumentException( "Cannot render non-planar surfaces." );
            }
        }
    }

    public void render( PolygonStyling styling, Geometry geom ) {
        if ( geom == null ) {
            LOG.debug( "Trying to render null geometry." );
            return;
        }

        if ( geom instanceof Point ) {
            LOG.warn( "Trying to render point with polygon styling." );
        }
        if ( geom instanceof Curve ) {
            LOG.warn( "Trying to render line with polygon styling." );
        }
        if ( geom instanceof Surface ) {
            render( styling, (Surface) geom );
        }
    }

}
