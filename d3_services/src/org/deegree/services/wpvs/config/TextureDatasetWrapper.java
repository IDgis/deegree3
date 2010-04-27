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

package org.deegree.services.wpvs.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.stream.XMLInputFactory;

import org.deegree.commons.datasource.configuration.AbstractGeospatialDataSourceType;
import org.deegree.commons.datasource.configuration.FeatureStoreType;
import org.deegree.commons.datasource.configuration.MultiResolutionDataSource;
import org.deegree.commons.datasource.configuration.RasterDataSource;
import org.deegree.commons.datasource.configuration.RasterFileSetType;
import org.deegree.commons.datasource.configuration.WMSDataSourceType;
import org.deegree.commons.datasource.configuration.MultiResolutionDataSource.Resolution;
import org.deegree.commons.utils.nio.DirectByteBufferPool;
import org.deegree.commons.xml.XMLAdapter;
import org.deegree.coverage.raster.cache.RasterCache;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.persistence.FeatureStoreException;
import org.deegree.feature.persistence.FeatureStoreManager;
import org.deegree.geometry.Envelope;
import org.deegree.rendering.r2d.se.parser.SymbologyParser;
import org.deegree.rendering.r2d.se.unevaluated.Style;
import org.deegree.rendering.r3d.opengl.rendering.dem.manager.TextureManager;
import org.deegree.rendering.r3d.opengl.rendering.dem.manager.TextureTileManager;
import org.deegree.rendering.r3d.opengl.rendering.dem.texturing.RasterAPITextureTileProvider;
import org.deegree.rendering.r3d.opengl.rendering.dem.texturing.StyledGeometryTTProvider;
import org.deegree.rendering.r3d.opengl.rendering.dem.texturing.TextureTileProvider;
import org.deegree.rendering.r3d.opengl.rendering.dem.texturing.WMSTextureTileProvider;
import org.deegree.services.wpvs.configuration.DEMTextureDataset;
import org.deegree.services.wpvs.configuration.DatasetDefinitions;
import org.deegree.services.wpvs.configuration.StyledGeometryProvider;
import org.deegree.services.wpvs.configuration.StyledGeometryProvider.TextureCacheDir;

/**
 * The <code>TextureDatasetWrapper</code> extracts data from jaxb configuration elements and creates texture managers,
 * from them. Following hierarchy is used: a {@link TextureManager} holds a {@link TextureTileManager} which can hold
 * one or more {@link TextureTileProvider}.
 * 
 * @author <a href="mailto:bezema@lat-lon.de">Rutger Bezema</a>
 * @author last edited by: $Author$
 * @version $Revision$, $Date$
 * 
 */
public class TextureDatasetWrapper extends DatasetWrapper<TextureManager> {

    private final static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger( TextureDatasetWrapper.class );

    private final int maxCachedTextureTiles;

    private final DirectByteBufferPool textureByteBufferPool;

    private final int maxTexturesInGPU;

    /**
     * 
     * @param textureByteBufferPool
     * @param maxTexturesInGPU
     *            the number of textures in gpu cache
     * @param maxCachedTextureTiles
     *            the number of texture tiles in cache.
     */
    public TextureDatasetWrapper( DirectByteBufferPool textureByteBufferPool, int maxTexturesInGPU,
                                  int maxCachedTextureTiles ) {
        // super( sceneEnvelope, translationToLocalCRS, configAdapter );
        this.textureByteBufferPool = textureByteBufferPool;
        this.maxTexturesInGPU = maxTexturesInGPU;
        this.maxCachedTextureTiles = maxCachedTextureTiles;
    }

    @Override
    public Envelope fillFromDatasetDefinitions( Envelope sceneEnvelope, double[] toLocalCRS, XMLAdapter configAdapter,
                                                DatasetDefinitions dsd ) {
        List<DEMTextureDataset> demTextureDatsets = dsd.getDEMTextureDataset();
        if ( !demTextureDatsets.isEmpty() ) {
            sceneEnvelope = analyseAndExtractConstraints( demTextureDatsets, sceneEnvelope, toLocalCRS,
                                                          dsd.getMaxPixelError(), configAdapter );
        } else {
            LOG.info( "No texture dataset has been configured." );
        }
        return sceneEnvelope;
    }

    private Envelope analyseAndExtractConstraints( List<DEMTextureDataset> textureDatasets, Envelope sceneEnvelope,
                                                   double[] toLocalCRS, Double parentMaxPixelError, XMLAdapter adapter ) {
        if ( textureDatasets != null && !textureDatasets.isEmpty() ) {
            for ( DEMTextureDataset dts : textureDatasets ) {
                if ( dts != null ) {
                    if ( isUnAmbiguous( dts.getTitle() ) ) {
                        LOG.info( "The feature dataset with name: " + dts.getName() + " and title: " + dts.getTitle()
                                  + " had multiple definitions in your service configuration." );
                    } else {
                        clarifyInheritance( dts, parentMaxPixelError );
                        try {
                            sceneEnvelope = handleTextureDataset( dts, sceneEnvelope, toLocalCRS, adapter );
                        } catch ( IOException e ) {
                            LOG.error( "Failed to initialize configured demTexture dataset: " + dts.getName() + ": "
                                       + dts.getTitle() + " because: " + e.getLocalizedMessage(), e );
                        }
                    }
                }
            }
        }
        return sceneEnvelope;
    }

    /**
     * @param datatype
     * @param parentMaxPixelError
     */
    private void clarifyInheritance( DEMTextureDataset datatype, Double parentMaxPixelError ) {
        datatype.setMaxPixelError( clarifyMaxPixelError( parentMaxPixelError, datatype.getMaxPixelError() ) );
    }

    /**
     * @param adapter
     * @param toLocalCRS
     * @param sceneEnvelope
     * @param mds
     * @throws IOException
     */
    private Envelope handleTextureDataset( DEMTextureDataset textureDataset, Envelope sceneEnvelope,
                                           double[] toLocalCRS, XMLAdapter adapter )
                            throws IOException {

        List<TextureTileProvider> tileProviders = new ArrayList<TextureTileProvider>();
        JAXBElement<? extends AbstractGeospatialDataSourceType> abstractRasterDataSource = textureDataset.getAbstractRasterDataSource();
        Envelope datasetEnvelope = null;
        LOG.debug( "Adding texture dataset: " + textureDataset.getTitle() );
        if ( abstractRasterDataSource != null ) {
            AbstractGeospatialDataSourceType sourceType = abstractRasterDataSource.getValue();
            if ( sourceType instanceof MultiResolutionDataSource ) {
                MultiResolutionDataSource mrds = (MultiResolutionDataSource) sourceType;
                for ( Resolution res : mrds.getResolution() ) {
                    AbstractGeospatialDataSourceType ds = res.getAbstractGeospatialDataSource().getValue();
                    TextureTileProvider ttProv = null;
                    if ( ds instanceof RasterDataSource ) {
                        RasterDataSource levelSource = (RasterDataSource) ds;
                        RasterFileSetType rfst = levelSource.getRasterDirectory();
                        if ( rfst != null ) {
                            URL resolvedURL = resolve( adapter, rfst.getValue() );
                            ttProv = new RasterAPITextureTileProvider( new File( resolvedURL.getFile() ), res.getRes() );

                        }
                    } else if ( ds instanceof WMSDataSourceType ) {
                        WMSDataSourceType levelSource = (WMSDataSourceType) ds;
                        URL capabilitiesURL = new URL( levelSource.getCapabilitiesDocumentLocation().getLocation() );
                        int maxWidth = -1;
                        int maxHeight = -1;
                        if ( levelSource.getMaxMapDimensions() != null ) {
                            maxWidth = levelSource.getMaxMapDimensions().getWidth().intValue();
                            maxHeight = levelSource.getMaxMapDimensions().getWidth().intValue();
                        }
                        int requestTimeout = -1;
                        if ( levelSource.getRequestTimeout() != null ) {
                            requestTimeout = levelSource.getRequestTimeout().intValue();
                        }
                        String requestFormat = "image/png";
                        boolean transparent = false;
                        if ( levelSource.getRequestedFormat() != null ) {
                            requestFormat = levelSource.getRequestedFormat().getValue();
                            transparent = levelSource.getRequestedFormat().isTransparent();
                        }
                        String[] layers = new String[] { levelSource.getRequestedLayers() };
                        // TODO how to handle differing CRS
                        // TODO remove hard code crs reference (But note: 'EPSG' must be in uppercase, which differs
                        // from
                        // getDefaultCRS())

                        // rb: don't get the envelope, because wms's tend to have wrong or falsely specified
                        // boundingboxes.
                        try {
                            ttProv = new WMSTextureTileProvider( capabilitiesURL, layers,
                                                                 sceneEnvelope.getCoordinateSystem(), requestFormat,
                                                                 transparent, res.getRes(), maxWidth, maxHeight,
                                                                 requestTimeout );
                        } catch ( Exception e ) {
                            LOG.warn( "Could not create wms dataset from " + capabilitiesURL + " because: " + e );
                        }
                    } else {
                        LOG.warn( "Unhandled datasource type: " + ds.getClass().getName() );
                    }
                    if ( ttProv != null ) {
                        Envelope tEnv = ttProv.getEnvelope();
                        if ( tEnv != null ) {
                            datasetEnvelope = ( datasetEnvelope == null ) ? tEnv : datasetEnvelope.merge( tEnv );
                        }
                        tileProviders.add( ttProv );
                    }
                }
            } else {
                LOG.warn( "Only multiresolution datasources are supported as raster texture providers." );
            }
        } else {
            StyledGeometryProvider ctDS = textureDataset.getStyledGeometryProvider();
            JAXBElement<? extends FeatureStoreType> featureStore = ctDS.getFeatureStore();
            FeatureStore store;
            try {
                store = FeatureStoreManager.create( featureStore.getValue(), adapter.getSystemId() );
            } catch ( FeatureStoreException e ) {
                throw new IOException( "Could not create a feature store because: " + e.getLocalizedMessage(), e );
            }

            String unresolved = ctDS.getSEStyleFile();
            if ( unresolved == null ) {
                LOG.warn( "The se-style file was not defined, could not create a closeup layer." );
                return sceneEnvelope;
            }
            URL styleFile = resolve( adapter, unresolved );
            InputStream styleStream = styleFile.openStream();
            Style style;
            try {
                style = SymbologyParser.parse( XMLInputFactory.newInstance().createXMLStreamReader(
                                                                                                    styleFile.toExternalForm(),
                                                                                                    styleStream ) );
            } catch ( Exception e ) {
                throw new IOException( "Could not read symbology encoding file because: " + e.getLocalizedMessage(), e );
            }

            double unitsPerPixel = ctDS.getMinimumUnitsPerPixel();
            TextureCacheDir rasterCache = ctDS.getTextureCacheDir();
            File cacheDir = RasterCache.DEFAULT_CACHE_DIR;
            double cacheSize = -1;
            if ( rasterCache != null ) {
                String cd = rasterCache.getValue();
                if ( cd != null ) {
                    URL resolve = adapter.resolve( cd );
                    cd = resolve.getFile();
                    cacheDir = new File( resolve.getFile() );
                }
                cacheSize = rasterCache.getCacheSize();
            }
            StyledGeometryTTProvider tProv = new StyledGeometryTTProvider( toLocalCRS,
                                                                           sceneEnvelope.getCoordinateSystem(), store,
                                                                           style, unitsPerPixel, cacheDir,
                                                                           Math.round( cacheSize * 1024 * 1024 * 1024 ) );
            tileProviders.add( tProv );
            datasetEnvelope = tProv.getEnvelope();

        }
        if ( !tileProviders.isEmpty() ) {
            TextureTileManager tileManager = new TextureTileManager(
                                                                     tileProviders.toArray( new TextureTileProvider[tileProviders.size()] ),
                                                                     maxCachedTextureTiles );

            TextureManager result = new TextureManager( this.textureByteBufferPool, tileManager, toLocalCRS,
                                                        maxTexturesInGPU, textureDataset.getRequestTimeout() );

            // these dataset envelopes are in realworld 2d coordinates
            if ( datasetEnvelope != null ) {
                if ( datasetEnvelope.getCoordinateDimension() == 2 ) {
                    double[] min = datasetEnvelope.getMin().getAsArray();
                    double[] max = datasetEnvelope.getMax().getAsArray();
                    double[] tMin = Arrays.copyOf( min, 3 );
                    double[] tMax = Arrays.copyOf( max, 3 );
                    tMin[2] = sceneEnvelope.getMin().get2();
                    tMax[2] = sceneEnvelope.getMax().get2();
                    datasetEnvelope = geomFac.createEnvelope( tMin, tMax, datasetEnvelope.getCoordinateSystem() );
                }
                sceneEnvelope = sceneEnvelope.merge( datasetEnvelope );
            }
            // will always have 3d.
            Envelope constraintEnv = datasetEnvelope == null ? sceneEnvelope : datasetEnvelope;
            // adding this constraint will result in never finding it in the scene, because it needs to be converted to
            // local crs... so lets convert.
            double[] min = constraintEnv.getMin().getAsArray();
            double[] max = constraintEnv.getMax().getAsArray();
            double[] tMin = Arrays.copyOf( min, min.length );
            double[] tMax = Arrays.copyOf( max, max.length );
            tMin[0] += toLocalCRS[0];
            tMin[1] += toLocalCRS[1];
            tMax[0] += toLocalCRS[0];
            tMax[1] += toLocalCRS[1];
            Envelope constEnv = geomFac.createEnvelope( tMin, tMax, constraintEnv.getCoordinateSystem() );
            addConstraint( textureDataset.getTitle(), result, constEnv );
        } else {
            LOG.warn( "Ignoring texture dataset: " + textureDataset.getName() + ": " + textureDataset.getTitle()
                      + " because no texture providers could be initialized." );
        }
        return sceneEnvelope;
    }
}
