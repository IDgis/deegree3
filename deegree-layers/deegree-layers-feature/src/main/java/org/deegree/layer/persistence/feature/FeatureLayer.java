/*----------------------------------------------------------------------------
 This file is part of deegree, http://deegree.org/
 Copyright (C) 2001-2010 by:
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

 Occam Labs Schmitz & Schneider GbR
 Godesberger Allee 139, 53175 Bonn
 Germany
 http://www.occamlabs.de/

 e-mail: info@deegree.org
 ----------------------------------------------------------------------------*/
package org.deegree.layer.persistence.feature;

import org.deegree.commons.ows.exception.OWSException;
import org.deegree.feature.persistence.FeatureStore;
import org.deegree.feature.persistence.query.Query;
import org.deegree.feature.types.AppSchemas;
import org.deegree.filter.Expression;
import org.deegree.filter.Filters;
import org.deegree.filter.OperatorFilter;
import org.deegree.filter.expression.ValueReference;
import org.deegree.filter.sort.SortProperty;
import org.deegree.geometry.Envelope;
import org.deegree.layer.AbstractLayer;
import org.deegree.layer.LayerQuery;
import org.deegree.layer.metadata.LayerMetadata;
import org.deegree.style.se.unevaluated.Style;
import org.deegree.style.utils.Styles;
import org.slf4j.Logger;

import javax.xml.namespace.QName;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.deegree.filter.Filters.addBBoxConstraint;
import static org.deegree.layer.persistence.feature.FilterBuilder.buildFilterForMap;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author <a href="mailto:schmitz@lat-lon.de">Andreas Schmitz</a>
 */
public class FeatureLayer extends AbstractLayer {

	private static final Logger LOG = getLogger(FeatureLayer.class);

	private final FeatureStore featureStore;

	private final OperatorFilter filter;

	private final QName featureType;

	SortProperty[] sortBy, sortByFeatureInfo;

	private final DimensionFilterBuilder dimFilterBuilder;

	public FeatureLayer(LayerMetadata md, FeatureStore featureStore, QName featureType, OperatorFilter filter,
			List<SortProperty> sortBy, List<SortProperty> sortByFeatureInfo) {
		super(md);
		this.featureStore = featureStore;
		this.featureType = featureType;
		this.filter = filter;
		if (sortBy != null) {
			this.sortBy = sortBy.toArray(new SortProperty[sortBy.size()]);
		}
		if (sortByFeatureInfo != null) {
			this.sortByFeatureInfo = sortByFeatureInfo.toArray(new SortProperty[sortByFeatureInfo.size()]);
		}
		dimFilterBuilder = new DimensionFilterBuilder(md.getDimensions());
	}

	@Override
	public FeatureLayerData mapQuery(final LayerQuery query, List<String> headers) throws OWSException {
		Style style = resolveStyleRef(query.getStyle());
		if (style == null) {
			throw new OWSException("The style " + query.getStyle().getName() + " is not defined for layer "
					+ getMetadata().getName() + ".", "StyleNotDefined", "styles");
		}
		QName ftName = featureType == null ? style.getFeatureType() : featureType;
		if (ftName != null && featureStore.getSchema().getFeatureType(ftName) == null) {
			LOG.warn("FeatureType '{}' is not known to the FeatureStore.", ftName);
			return null;
		}
		style = style.filter(query.getScale());

		Set<QName> propertyNames = AppSchemas.collectProperyNames(featureStore.getSchema(), ftName);
		OperatorFilter filter = buildFilterForMap(this.filter, style, query, dimFilterBuilder, headers, propertyNames);

		filter = Filters.repair(filter, propertyNames);

		final Envelope bbox = query.getQueryBox();
		final ValueReference geomProp = findGeometryProperty(style);
		QueryBuilder builder = new QueryBuilder(featureStore, filter, ftName, bbox, query, geomProp, sortBy,
				getMetadata().getName());
		List<Query> queries = builder.buildMapQueries();

		if (queries.isEmpty()) {
			LOG.warn("No queries were generated. Is the configuration correct?");
			return null;
		}

		Integer maxFeats = query.getRenderingOptions().getMaxFeatures(getMetadata().getName());
		final int maxFeatures = maxFeats == null ? -1 : maxFeats;

		return new FeatureLayerData(queries, featureStore, maxFeatures, style, ftName);
	}

	@Override
	public FeatureLayerData infoQuery(final LayerQuery query, List<String> headers) throws OWSException {
		Style style = resolveStyleRef(query.getStyle());
		if (style == null) {
			throw new OWSException("The style " + query.getStyle().getName() + " is not defined for layer "
					+ getMetadata().getName() + ".", "StyleNotDefined", "styles");
		}
		QName featureType = this.featureType == null ? style.getFeatureType() : this.featureType;
		Set<QName> propertyNames = AppSchemas.collectProperyNames(featureStore.getSchema(), featureType);

		OperatorFilter filter = buildFilterForMap(this.filter, style, query, dimFilterBuilder, headers, propertyNames);

		int layerRadius = -1;
		if (getMetadata().getMapOptions() != null) {
			layerRadius = getMetadata().getMapOptions().getFeatureInfoRadius();
		}
		final Envelope clickBox = query.calcClickBox(layerRadius > -1 ? layerRadius : query.getLayerRadius());

		filter = (OperatorFilter) addBBoxConstraint(clickBox, filter, null, false);
		filter = Filters.repair(filter, AppSchemas.collectProperyNames(featureStore.getSchema(), featureType));

		LOG.debug("Querying the feature store(s)...");

		QueryBuilder builder = new QueryBuilder(featureStore, filter, featureType, clickBox, query, null,
				sortByFeatureInfo, getMetadata().getName());
		List<Query> queries = builder.buildInfoQueries();

		LOG.debug("Finished querying the feature store(s).");

		return new FeatureLayerData(queries, featureStore, query.getFeatureCount(), style, featureType);
	}

	private static ValueReference findGeometryProperty(Style style) {
		Set<Expression> exprs = new HashSet<>(Styles.getGeometryExpressions(style));
		if (exprs.size() == 1 && exprs.iterator().next() instanceof ValueReference)
			return (ValueReference) exprs.iterator().next();
		return null;
	}

}
