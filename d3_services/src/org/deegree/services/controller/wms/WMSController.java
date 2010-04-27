//$HeadURL$
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

package org.deegree.services.controller.wms;

import static java.util.Collections.singletonList;
import static javax.imageio.ImageIO.write;
import static org.deegree.commons.types.ows.Version.parseVersion;
import static org.deegree.commons.utils.ArrayUtils.join;
import static org.deegree.protocol.wms.WMSConstants.VERSION_111;
import static org.deegree.protocol.wms.WMSConstants.VERSION_130;
import static org.deegree.services.controller.OGCFrontController.getHttpGetURL;
import static org.deegree.services.controller.exception.ControllerException.NO_APPLICABLE_CODE;
import static org.deegree.services.controller.ows.OWSException.INVALID_FORMAT;
import static org.deegree.services.controller.ows.OWSException.INVALID_PARAMETER_VALUE;
import static org.deegree.services.controller.ows.OWSException.OPERATION_NOT_SUPPORTED;
import static org.deegree.services.i18n.Messages.get;
import static org.slf4j.LoggerFactory.getLogger;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import java_cup.runtime.Symbol;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axiom.om.OMElement;
import org.apache.commons.fileupload.FileItem;
import org.deegree.commons.types.ows.Version;
import org.deegree.commons.utils.Pair;
import org.deegree.commons.utils.templating.TemplatingLexer;
import org.deegree.commons.utils.templating.TemplatingParser;
import org.deegree.commons.utils.templating.lang.PropertyTemplateCall;
import org.deegree.commons.xml.NamespaceContext;
import org.deegree.commons.xml.XMLAdapter;
import org.deegree.commons.xml.XPath;
import org.deegree.crs.exceptions.TransformationException;
import org.deegree.crs.exceptions.UnknownCRSException;
import org.deegree.feature.Feature;
import org.deegree.feature.GenericFeatureCollection;
import org.deegree.feature.Property;
import org.deegree.feature.types.FeatureType;
import org.deegree.geometry.Geometry;
import org.deegree.geometry.GeometryTransformer;
import org.deegree.geometry.io.WKTWriter;
import org.deegree.gml.GMLVersion;
import org.deegree.gml.feature.GMLFeatureWriter;
import org.deegree.gml.feature.schema.ApplicationSchemaXSDEncoder;
import org.deegree.protocol.ows.capabilities.GetCapabilities;
import org.deegree.protocol.wms.WMSConstants.WMSRequestType;
import org.deegree.services.controller.AbstractOGCServiceController;
import org.deegree.services.controller.Credentials;
import org.deegree.services.controller.ImplementationMetadata;
import org.deegree.services.controller.OGCFrontController;
import org.deegree.services.controller.configuration.AllowedServices;
import org.deegree.services.controller.configuration.DeegreeServicesMetadata;
import org.deegree.services.controller.configuration.ServiceIdentificationType;
import org.deegree.services.controller.configuration.ServiceProviderType;
import org.deegree.services.controller.exception.ControllerInitException;
import org.deegree.services.controller.ows.OWSException;
import org.deegree.services.controller.utils.HttpResponseBuffer;
import org.deegree.services.controller.wms.configuration.PublishedInformation;
import org.deegree.services.controller.wms.configuration.PublishedInformation.GetFeatureInfoFormat;
import org.deegree.services.controller.wms.configuration.PublishedInformation.ImageFormat;
import org.deegree.services.controller.wms.configuration.PublishedInformation.SupportedVersions;
import org.deegree.services.controller.wms.ops.GetFeatureInfo;
import org.deegree.services.controller.wms.ops.GetFeatureInfoSchema;
import org.deegree.services.controller.wms.ops.GetLegendGraphic;
import org.deegree.services.controller.wms.ops.GetMap;
import org.deegree.services.controller.wms.plugins.FeatureInfoSerializer;
import org.deegree.services.controller.wms.plugins.ImageSerializer;
import org.deegree.services.wms.MapService;
import org.deegree.services.wms.WMSException.InvalidDimensionValue;
import org.deegree.services.wms.WMSException.MissingDimensionValue;
import org.deegree.services.wms.configuration.ServiceConfiguration;
import org.slf4j.Logger;

/**
 * <code>WMSController</code> handles the protocol and map service globally.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class WMSController extends AbstractOGCServiceController {

    private static final Logger LOG = getLogger( WMSController.class );

    private final static String CONFIG_SCHEMA_FILE = "/META-INF/schemas/wms/0.4.0/wms_configuration.xsd";

    private static final ImplementationMetadata<WMSRequestType> IMPLEMENTATION_METADATA = new ImplementationMetadata<WMSRequestType>() {
        {
            supportedVersions = new Version[] { VERSION_111, VERSION_130 };
            handledNamespaces = new String[] { "" }; // WMS uses null namespace for SLD GetMap Post requests
            handledRequests = WMSRequestType.class;
            supportedConfigVersions = new Version[] { Version.parseVersion( "0.4.0" ) };
        }
    };

    private final HashMap<String, FeatureInfoSerializer> featureInfoSerializers = new HashMap<String, FeatureInfoSerializer>();

    private final HashMap<String, ImageSerializer> imageSerializers = new HashMap<String, ImageSerializer>();

    /** The list of supported image formats. */
    public final LinkedList<String> supportedImageFormats = new LinkedList<String>();

    /** The list of supported info formats. */
    public final LinkedHashMap<String, String> supportedFeatureInfoFormats = new LinkedHashMap<String, String>();

    protected MapService service;

    protected ServiceIdentificationType identification;

    protected ServiceProviderType provider;

    protected TreeMap<Version, Controller> controllers = new TreeMap<Version, Controller>();

    private Version highestVersion;

    private static <T> void instantiateSerializer( HashMap<String, T> map, String format, String className,
                                                   Class<T> clazz ) {
        try {
            // generics and reflection don't go well together
            Class<T> c = (Class<T>) Class.forName( className );
            if ( !c.isAssignableFrom( clazz ) ) {
                LOG.warn( "The serializer class '{}' does not implement the '{}' interface.", className, clazz );
            } else {
                map.put( format, c.newInstance() );
            }
        } catch ( ClassNotFoundException e ) {
            LOG.warn( "The feature info serializer class '{}' could not be found " + "on the classpath.", className );
            LOG.debug( "Stack trace: ", e );
        } catch ( InstantiationException e ) {
            LOG.warn( "The feature info serializer class '{}' could not be instantiated.", className );
            LOG.debug( "Stack trace: ", e );
        } catch ( IllegalAccessException e ) {
            LOG.warn( "The feature info serializer class '{}' could not be instantiated.", className );
            LOG.debug( "Stack trace: ", e );
        }
    }

    @Override
    public void init( XMLAdapter controllerConf, DeegreeServicesMetadata serviceMetadata )
                            throws ControllerInitException {

        init( serviceMetadata, IMPLEMENTATION_METADATA, controllerConf );

        identification = mainControllerConf.getServiceIdentification();
        provider = mainControllerConf.getServiceProvider();

        NamespaceContext nsContext = new NamespaceContext();
        nsContext.addNamespace( "wms", "http://www.deegree.org/services/wms" );

        try {
            String additionalClasspath = "org.deegree.services.controller.wms.configuration:org.deegree.services.wms.configuration";
            Unmarshaller u = getUnmarshaller( additionalClasspath, CONFIG_SCHEMA_FILE );

            XPath xp = new XPath( "wms:PublishedInformation", nsContext );
            OMElement elem = controllerConf.getElement( controllerConf.getRootElement(), xp );

            // put in the default formats
            supportedFeatureInfoFormats.put( "application/vnd.ogc.gml", "" );
            supportedFeatureInfoFormats.put( "text/xml", "" );
            supportedFeatureInfoFormats.put( "text/plain", "" );
            supportedFeatureInfoFormats.put( "text/html", "" );

            supportedImageFormats.add( "image/png" );
            supportedImageFormats.add( "image/png; subtype=8bit" );
            supportedImageFormats.add( "image/png; mode=8bit" );
            supportedImageFormats.add( "image/gif" );
            supportedImageFormats.add( "image/jpeg" );
            supportedImageFormats.add( "image/tiff" );
            supportedImageFormats.add( "image/x-ms-bmp" );

            if ( elem != null ) {
                PublishedInformation pi = (PublishedInformation) u.unmarshal( elem.getXMLStreamReaderWithoutCaching() );

                if ( pi.getGetFeatureInfoFormat() != null ) {
                    for ( GetFeatureInfoFormat t : pi.getGetFeatureInfoFormat() ) {
                        String format = t.getFormat();
                        if ( t.getFile() != null ) {
                            supportedFeatureInfoFormats.put( format, controllerConf.resolve( t.getFile() ).getFile() );
                        } else {
                            instantiateSerializer( featureInfoSerializers, format, t.getClazz(),
                                                   FeatureInfoSerializer.class );
                        }
                    }
                }

                if ( pi.getImageFormat() != null ) {
                    for ( ImageFormat f : pi.getImageFormat() ) {
                        instantiateSerializer( imageSerializers, f.getFormat(), f.getClazz(), ImageSerializer.class );
                    }
                }

                identification = pi.getServiceIdentification() == null ? identification : pi.getServiceIdentification();
                provider = pi.getServiceProvider() == null ? provider : pi.getServiceProvider();
                final SupportedVersions versions = pi.getSupportedVersions();
                if ( versions == null ) {
                    ArrayList<String> vs = new ArrayList<String>();
                    vs.add( "1.1.1" );
                    vs.add( "1.3.0" );
                    validateAndSetOfferedVersions( vs );
                } else {
                    validateAndSetOfferedVersions( versions.getVersion() );
                }
            } else {
                ArrayList<String> vs = new ArrayList<String>();
                vs.add( "1.1.1" );
                vs.add( "1.3.0" );
                validateAndSetOfferedVersions( vs );
            }

            for ( Version v : offeredVersions ) {
                if ( v.equals( VERSION_111 ) ) {
                    controllers.put( VERSION_111, new WMSController111() );
                }
                if ( v.equals( VERSION_130 ) ) {
                    controllers.put( VERSION_130, new WMSController130() );
                }
            }

            Iterator<Version> iter = controllers.keySet().iterator();
            while ( iter.hasNext() ) {
                highestVersion = iter.next();
            }

            xp = new XPath( "wms:ServiceConfiguration", nsContext );
            elem = controllerConf.getRequiredElement( controllerConf.getRootElement(), xp );
            ServiceConfiguration sc = (ServiceConfiguration) u.unmarshal( elem.getXMLStreamReaderWithoutCaching() );
            service = new MapService( sc, controllerConf );
        } catch ( JAXBException e ) {
            throw new ControllerInitException( e.getMessage(), e );
        } catch ( MalformedURLException e ) {
            throw new ControllerInitException( e.getMessage(), e );
        }

    }

    @Override
    public void destroy() {
        service.close();
    }

    @Override
    public void doKVP( Map<String, String> map, HttpServletRequest request, HttpResponseBuffer response,
                       List<FileItem> multiParts )
                            throws ServletException, IOException {
        String v = map.get( "VERSION" );
        if ( v == null ) {
            v = map.get( "WMTVER" );
        }
        Version version = v == null ? highestVersion : parseVersion( v );

        WMSRequestType req;
        try {
            req = IMPLEMENTATION_METADATA.getRequestTypeByName( map.get( "REQUEST" ) );
        } catch ( IllegalArgumentException e ) {
            controllers.get( version ).sendException(
                                                      new OWSException( get( "WMS.OPERATION_NOT_KNOWN",
                                                                             map.get( "REQUEST" ) ),
                                                                        OPERATION_NOT_SUPPORTED ), response );
            return;
        } catch ( NullPointerException e ) {
            controllers.get( version ).sendException(
                                                      new OWSException( get( "WMS.PARAM_MISSING", "REQUEST" ),
                                                                        OPERATION_NOT_SUPPORTED ), response );
            return;
        }

        try {
            handleRequest( req, response, map, version );
        } catch ( OWSException e ) {
            if ( controllers.get( version ) == null ) {
                // happens if non capabilities request is made with unsupported version
                version = highestVersion;
            }

            LOG.debug( "The response is an exception with the message '{}'", e.getLocalizedMessage() );
            LOG.trace( "Stack trace of OWSException being sent", e );

            controllers.get( version ).handleException( map, req, e, response, this );
        }
    }

    private void handleRequest( WMSRequestType req, HttpResponseBuffer response, Map<String, String> map,
                                Version version )
                            throws IOException, OWSException {
        try {
            switch ( req ) {
            case GetCapabilities:
            case capabilities:
                break;
            default:
                if ( controllers.get( version ) == null ) {
                    throw new OWSException( get( "WMS.VERSION_UNSUPPORTED", version ), INVALID_PARAMETER_VALUE );
                }
            }

            switch ( req ) {
            case DescribeLayer:
                throw new OWSException( get( "WMS.OPERATION_NOT_SUPPORTED_IMPLEMENTATION", req.name() ),
                                        OPERATION_NOT_SUPPORTED );
            case capabilities:
            case GetCapabilities:
                getCapabilities( map, response );
                break;
            case GetFeatureInfo:
                getFeatureInfo( map, response, version );
                break;
            case GetMap:
            case map:
                getMap( map, response, version );
                break;
            case GetFeatureInfoSchema:
                getFeatureInfoSchema( map, response );
                break;
            case GetLegendGraphic:
                getLegendGraphic( map, response );
                break;
            }
        } catch ( MissingDimensionValue e ) {
            throw new OWSException( get( "WMS.DIMENSION_VALUE_MISSING", e.name ), "MissingDimensionValue" );
        } catch ( InvalidDimensionValue e ) {
            throw new OWSException( get( "WMS.DIMENSION_VALUE_INVALID", e.value, e.name ), "InvalidDimensionValue" );
        }
    }

    private void getLegendGraphic( Map<String, String> map, HttpResponseBuffer response )
                            throws OWSException, IOException {
        GetLegendGraphic glg = new GetLegendGraphic( map, service );
        if ( !supportedImageFormats.contains( glg.getFormat() ) ) {
            throw new OWSException( get( "WMS.UNSUPPORTED_IMAGE_FORMAT", glg.getFormat() ), INVALID_FORMAT );
        }
        BufferedImage img = service.getLegend( glg );
        sendImage( img, response, glg.getFormat() );
    }

    private void getFeatureInfo( Map<String, String> map, HttpResponseBuffer response, Version version )
                            throws OWSException, IOException, MissingDimensionValue, InvalidDimensionValue {
        GetFeatureInfo fi = new GetFeatureInfo( map, version, service );
        checkGetFeatureInfo( fi );
        Pair<GenericFeatureCollection, LinkedList<String>> pair = service.getFeatures( fi );
        GenericFeatureCollection col = pair.first;
        addHeaders( response, pair.second );
        String format = fi.getInfoFormat();
        format = format == null ? "application/vnd.ogc.gml" : format;
        response.setContentType( format );
        response.setCharacterEncoding( "UTF-8" );

        FeatureInfoSerializer serializer = featureInfoSerializers.get( format );
        if ( serializer != null ) {
            serializer.serialize( col, response.getOutputStream() );
            response.flushBuffer();
            return;
        }

        String fiFile = supportedFeatureInfoFormats.get( format );
        if ( !fiFile.isEmpty() ) {
            PrintWriter out = new PrintWriter( new OutputStreamWriter( response.getOutputStream(), "UTF-8" ) );

            try {
                Symbol s = new TemplatingParser( new TemplatingLexer( new FileInputStream( fiFile ) ) ).parse();
                HashMap<String, Object> tmpl = (HashMap) s.value;
                StringBuilder sb = new StringBuilder();
                new PropertyTemplateCall( "start", singletonList( "*" ), false ).eval( sb, tmpl, col,
                                                                                       fi.returnGeometries() );
                out.println( sb.toString() );
            } catch ( Exception e ) {
                LOG.error( "Could not load template '{}' for GFI response.", fiFile );
                LOG.debug( "Stack trace:", e );
            } finally {
                out.close();
            }
            response.flushBuffer();
            return;
        }

        if ( format.equalsIgnoreCase( "application/vnd.ogc.gml" ) || format.equalsIgnoreCase( "text/xml" ) ) {
            try {
                XMLStreamWriter xmlWriter = response.getXMLWriter();
                // quick hack to get better prefixes
                HashSet<String> set = new HashSet<String>();
                int cur = 0;
                for ( Feature f : col ) {
                    String ns = f.getType().getName().getNamespaceURI();
                    if ( ns != null && ns.length() > 0 && !set.contains( ns ) ) {
                        set.add( ns );
                        xmlWriter.setPrefix( "app" + cur++, ns );
                    }
                }
                xmlWriter.setPrefix( "xlink", "http://www.w3.org/1999/xlink" );
                String loc = getHttpGetURL() + "request=GetFeatureInfoSchema&layers=" + join( ",", fi.getQueryLayers() );
                new GMLFeatureWriter( GMLVersion.GML_2, xmlWriter, fi.getCoordinateSystem(), null, null, null, 0, -1,
                                      false ).export( col, loc );
            } catch ( XMLStreamException e ) {
                LOG.warn( "Error when writing GetFeatureInfo GML response '{}'.", e.getLocalizedMessage() );
                LOG.debug( "Stack trace:", e );
            } catch ( UnknownCRSException e ) {
                LOG.warn( "Could not instantiate the geometry transformer for output srs '{}'."
                          + " Aborting GetFeatureInfo response.", fi.getCoordinateSystem() );
                LOG.debug( "Stack trace:", e );
            } catch ( TransformationException e ) {
                LOG.warn( "Could transform to output srs '{}'. Aborting GetFeatureInfo response.",
                          fi.getCoordinateSystem() );
                LOG.debug( "Stack trace:", e );
            }
        }
        if ( format.equalsIgnoreCase( "text/plain" ) ) {
            PrintWriter out = new PrintWriter( new OutputStreamWriter( response.getOutputStream(), "UTF-8" ) );
            for ( Feature f : col ) {
                out.println( f.getName().getLocalPart() + ":" );
                for ( Property<?> p : f.getProperties() ) {
                    out.println( "  " + p.getName().getLocalPart() + ": " + p.getValue() );
                }
                out.println();
            }
            out.close();
        }

        if ( format.equalsIgnoreCase( "text/html" ) ) {
            GeometryTransformer trans = null;
            try {
                trans = new GeometryTransformer( fi.getCoordinateSystem().getWrappedCRS() );

                PrintWriter out = new PrintWriter( new OutputStreamWriter( response.getOutputStream(), "UTF-8" ) );

                out.println( "<html>" );
                out.println( "<head>" );
                out.println( "<meta http-equiv='Content-Type' content='text/html; charset=utf-8'>" );
                out.println( "</head>" );
                out.println( "<body>" );
                out.println( "<table style='border: solid black;'>" );
                boolean alternating = true;
                for ( Feature f : col ) {
                    out.println( "<tr><td style='border: solid black; border-width: 1px;' span='2'><b>" );
                    out.println( f.getName().getLocalPart() );
                    out.println( "</b></tr>" );
                    for ( Property<?> p : f.getProperties() ) {
                        if ( p.getValue() instanceof Geometry && fi.returnGeometries() ) {
                            alternating = !alternating;
                            if ( alternating ) {
                                out.print( "<tr bgcolor='#dddddd'>" );
                            } else {
                                out.print( "<tr>" );
                            }
                            out.print( "<td>" + p.getName().getLocalPart() + ":</td><td>" );
                            WKTWriter.write( trans.transform( (Geometry) p.getValue() ), out );
                            out.println( "</td></tr>" );
                        }
                        if ( p.getValue() instanceof Geometry || p.getValue() instanceof Feature ) {
                            continue;
                        }
                        alternating = !alternating;
                        if ( alternating ) {
                            out.print( "<tr bgcolor='#dddddd'>" );
                        } else {
                            out.print( "<tr>" );
                        }
                        out.println( "<td>" + p.getName().getLocalPart() + ":</td><td>" + p.getValue() + "</td></tr>" );
                    }
                }
                out.println( "</table>" );
                out.println( "</body>" );
                out.println( "</html>" );
                out.close();

            } catch ( IllegalArgumentException e ) {
                LOG.warn( "Could not instantiate the geometry transformer for output srs '{}'."
                          + " Aborting GetFeatureInfo response.", fi.getCoordinateSystem() );
                LOG.debug( "Stack trace:", e );
            } catch ( UnknownCRSException e ) {
                LOG.warn( "Could not instantiate the geometry transformer for output srs '{}'."
                          + " Aborting GetFeatureInfo response.", fi.getCoordinateSystem() );
                LOG.debug( "Stack trace:", e );
            } catch ( TransformationException e ) {
                LOG.warn( "Could transform to output srs '{}'. Aborting GetFeatureInfo response.",
                          fi.getCoordinateSystem() );
                LOG.debug( "Stack trace:", e );
            }
        }
    }

    private void getFeatureInfoSchema( Map<String, String> map, HttpResponseBuffer response )
                            throws IOException {
        GetFeatureInfoSchema fis = new GetFeatureInfoSchema( map );
        List<FeatureType> schema = service.getSchema( fis );
        try {
            response.setContentType( "text/xml" );
            XMLStreamWriter writer = response.getXMLWriter();
            writer.writeStartDocument();
            new ApplicationSchemaXSDEncoder( GMLVersion.GML_2, null ).export( writer, schema );
            writer.writeEndDocument();
            response.flushBuffer();
        } catch ( XMLStreamException e ) {
            LOG.error( "Unknown error", e );
        }
    }

    private static void addHeaders( HttpResponseBuffer response, LinkedList<String> headers ) {
        while ( !headers.isEmpty() ) {
            String s = headers.poll();
            response.addHeader( "Warning", s );
        }
    }

    protected void getMap( Map<String, String> map, HttpResponseBuffer response, Version version )
                            throws OWSException, IOException, MissingDimensionValue, InvalidDimensionValue {
        GetMap gm = new GetMap( map, version, service );
        checkGetMap( version, gm );
        final Pair<BufferedImage, LinkedList<String>> pair = service.getMapImage( gm );
        addHeaders( response, pair.second );
        sendImage( pair.first, response, gm.getFormat() );
    }

    private void checkGetFeatureInfo( GetFeatureInfo gfi )
                            throws OWSException {
        if ( gfi.getInfoFormat() != null && !gfi.getInfoFormat().equals( "" )
             && !supportedFeatureInfoFormats.containsKey( gfi.getInfoFormat() ) ) {
            throw new OWSException( get( "WMS.INVALID_INFO_FORMAT", gfi.getInfoFormat() ), INVALID_FORMAT );
        }
    }

    private void checkGetMap( Version version, GetMap gm )
                            throws OWSException {
        if ( !supportedImageFormats.contains( gm.getFormat() ) ) {
            throw new OWSException( get( "WMS.UNSUPPORTED_IMAGE_FORMAT", gm.getFormat() ), INVALID_FORMAT );
        }
        try {
            // check for existence/validity
            if ( gm.getCoordinateSystem() == null ) {
                // this can happen if some AUTO SRS id was invalid
                controllers.get( version ).throwSRSException( "automatic" );
            }
            gm.getCoordinateSystem().getWrappedCRS();
        } catch ( UnknownCRSException e ) {
            // only throw an exception if a truly invalid srs is found
            // this makes it possible to request srs that are not advertised, which may be useful
            controllers.get( version ).throwSRSException( gm.getCoordinateSystem().getName() );
        }

    }

    protected void getCapabilities( Map<String, String> map, HttpResponseBuffer response )
                            throws OWSException, IOException {

        String version = map.get( "VERSION" );
        // not putting it into the bean, why should I? It's used just a few lines below...
        String updateSequence = map.get( "UPDATESEQUENCE" );
        if ( version == null ) {
            version = map.get( "WMTVER" );
        }
        GetCapabilities req = new GetCapabilities( version );

        Version myVersion = negotiateVersion( req );

        String getUrl = OGCFrontController.getHttpGetURL();
        String postUrl = OGCFrontController.getHttpPostURL();

        controllers.get( myVersion ).getCapabilities( getUrl, postUrl, updateSequence, service, response,
                                                      identification, provider, map, this );
        response.flushBuffer();
    }

    @Override
    public void doXML( XMLStreamReader xmlStream, HttpServletRequest request, HttpResponseBuffer response,
                       List<FileItem> multiParts )
                            throws ServletException, IOException {
        throw new UnsupportedOperationException( "XML request handling is currently not supported for the wms" );
    }

    /**
     * @param img
     * @param response
     * @param format
     * @throws OWSException
     * @throws IOException
     */
    public void sendImage( BufferedImage img, HttpResponseBuffer response, String format )
                            throws OWSException, IOException {
        response.setContentType( format );

        ImageSerializer serializer = imageSerializers.get( format );
        if ( serializer != null ) {
            serializer.serialize( img, response.getOutputStream() );
            return;
        }

        format = format.substring( format.indexOf( "/" ) + 1 );
        if ( format.equals( "x-ms-bmp" ) ) {
            format = "bmp";
        }
        if ( format.equals( "png; subtype=8bit" ) || format.equals( "png; mode=8bit" ) ) {
            format = "png";
        }
        LOG.debug( "Sending in format " + format );
        if ( !write( img, format, response.getOutputStream() ) ) {
            throw new OWSException( get( "WMS.CANNOT_ENCODE_IMAGE", format ), NO_APPLICABLE_CODE );
        }
    }

    /**
     * <code>Controller</code>
     * 
     * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
     * @author last edited by: $Author$
     * 
     * @version $Revision$, $Date$
     */
    public interface Controller {
        /**
         * @param map
         * @param req
         * @param e
         * @param response
         * @param controller
         * @throws ServletException
         */
        void handleException( Map<String, String> map, WMSRequestType req, OWSException e,
                              HttpResponseBuffer response, WMSController controller )
                                throws ServletException;

        /**
         * @param ex
         * @param response
         * @throws ServletException
         */
        void sendException( OWSException ex, HttpResponseBuffer response )
                                throws ServletException;

        /**
         * @param getUrl
         * @param postUrl
         * @param updateSequence
         * @param service
         * @param response
         * @param identification
         * @param provider
         * @param customParameters
         * @param controller
         * @throws OWSException
         * @throws IOException
         */
        void getCapabilities( String getUrl, String postUrl, String updateSequence, MapService service,
                              HttpResponseBuffer response, ServiceIdentificationType identification,
                              ServiceProviderType provider, Map<String, String> customParameters,
                              WMSController controller )
                                throws OWSException, IOException;

        /**
         * @param name
         * @throws OWSException
         */
        void throwSRSException( String name )
                                throws OWSException;
    }

    
}
