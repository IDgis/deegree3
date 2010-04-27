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
package org.deegree.record.persistence.sqltransform;

import java.util.HashSet;
import java.util.Set;

import javax.xml.namespace.QName;

/**
 * Encapsulates the atomic properties that are needed to build an SQL expression.
 * 
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author: thomas $
 * 
 * @version $Revision: $, $Date: $
 */
public class ExpressionFilterHelper {

    private Set<String> tables = new HashSet<String>();

    private Set<String> columns = new HashSet<String>();

    private Set<QName> propertyName = new HashSet<QName>();

    private static final ExpressionFilterHelper INSTANCE = new ExpressionFilterHelper();

    /**
     * Private constructor disables the access from outside.
     */
    private ExpressionFilterHelper() {
        // nothing to do here
    }

    /**
     * Threadsafety for this instance.
     * 
     * @return {@link ExpressionFilterHelper}
     */
    public static ExpressionFilterHelper getInstance() {
        return INSTANCE;
    }

    /**
     * @return the tables
     */
    public Set<String> getTables() {
        return tables;
    }

    /**
     * @return the columns
     */
    public Set<String> getColumns() {
        return columns;
    }

    /**
     * @return the propertyName
     */
    public Set<QName> getPropertyName() {
        return propertyName;
    }

    /**
     * @param propertyName
     *            the propertyName to set
     */
    public void setPropertyName( QName propertyName ) {
        this.propertyName.add( propertyName );
    }

    /**
     * Adds tables and columns to an existing set.
     * 
     * @param tables
     *            to be added
     * @param columns
     *            to be added
     */
    public void addTablesANDColumns( Set<String> tables, Set<String> columns ) {
        this.tables.addAll( tables );
        this.columns.addAll( columns );
    }

}
