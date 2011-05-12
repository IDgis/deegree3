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
package org.deegree.filter.sql.expression;

import java.util.Collections;
import java.util.List;

import org.deegree.commons.tom.primitive.PrimitiveType;
import org.deegree.cs.coordinatesystems.ICRS;

/**
 * {@link SQLExpression} that represents a table column.
 * 
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider</a>
 * @author last edited by: $Author$
 * 
 * @version $Revision$, $Date$
 */
public class SQLColumn implements SQLExpression {

    private int sqlType;

    private boolean isSpatial;

    private String column;

    private String table;

    private String srid;

    private ICRS crs;

    private boolean isConcatenated;

    private PrimitiveType pt;

    public SQLColumn( String table, String column, boolean spatial, PrimitiveType pt, int sqlType, ICRS crs,
                      String srid, boolean isConcatenated ) {
        this.table = table;
        this.column = column;
        this.pt = pt;
        this.sqlType = sqlType;
        this.isSpatial = spatial;
        this.crs = crs;
        this.srid = srid;
        this.isConcatenated = isConcatenated;
    }

    @Override
    public ICRS getCRS() {
        return crs;
    }

    @Override
    public String getSRID() {
        return srid;
    }

    @Override
    public int getSQLType() {
        return sqlType;
    }

    @Override
    public PrimitiveType getPrimitiveType() {
        return pt;
    }

    @Override
    public void cast( PrimitiveType pt ) {
        if ( pt.getBaseType() != this.pt.getBaseType() ) {
            throw new UnsupportedOperationException( "Column type casts are not implemented yet." );
        }
    }

    @Override
    public boolean isSpatial() {
        return isSpatial;
    }

    @Override
    public boolean isMultiValued() {
        return isConcatenated;
    }

    @Override
    public String toString() {
        return table == null ? column : ( table + "." + column );
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SQLLiteral> getLiterals() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public StringBuilder getSQL() {
        StringBuilder sb = new StringBuilder();
        if ( table != null ) {
            sb.append( table ).append( "." );
        }
        sb.append( column );
        return sb;
    }
}