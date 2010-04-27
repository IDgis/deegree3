//$HeadURL: svn+ssh://sthomas@svn.wald.intevation.org/deegree/base/trunk/resources/eclipse/files_template.xml $
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

package org.deegree.protocol.csw;

import org.deegree.commons.types.ows.Version;

/**
 * 
 * Container for, in the specification defined, static specified elements
 * 
 * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
 * @author last edited by: $Author: thomas $
 * 
 * @version $Revision: $, $Date: $
 */
public class CSWConstants {

    /**
     * Namespace for elements from the CSW 2.0.2 specification
     * <p>
     * Namespace="http://www.opengis.net/cat/csw/2.0.2"
     * */
    public static final String CSW_202_NS = "http://www.opengis.net/cat/csw/2.0.2";

    /**
     * Namespace for elements from the ISO AP 1.0 specification
     * <p>
     * Namespace="http://www.isotc211.org/2005/gmd"
     * */
    public static final String GMD_NS = "http://www.isotc211.org/2005/gmd";

    /** Location of the schema */
    public static final String CSW_202_DISCOVERY_SCHEMA = "http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd";

    /** Location of the schema */
    public static final String CSW_202_PUBLICATION_SCHEMA = "http://schemas.opengis.net/csw/2.0.2/CSW-publication.xsd";

    /** Location of the schema */
    public static final String CSW_202_RECORD = "http://schemas.opengis.net/csw/2.0.2/record.xsd";

    /** Common namespace prefix for elements from the CSW specification */
    public static final String CSW_PREFIX = "csw";

    /** Common namespace prefix for elements from the ISO AP specification */
    public static final String GMD_PREFIX = "gmd";

    /** CSW protocol version 2.0.2 */
    public static final Version VERSION_202 = Version.parseVersion( "2.0.2" );

    /**
     * 
     * Operations that is the webservice capable of <br>
     * <li>GetCapabilities</li> <li>DescribeRecord</li> <li>GetRecords</li> <li>GetRecordById</li><li>Transaction</li> <br>
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author: thomas $
     * 
     * @version $Revision: $, $Date: $
     */
    public enum CSWRequestType {

        /** Retrieve the capabilities of the service. */
        GetCapabilities,
        /** Discover elements of the service */
        DescribeRecord,
        /** Resource discovery combines the two operations - search and present */
        GetRecords,
        /** Retrieve the default representation of the service */
        GetRecordById,
        /** Creates, modifys and deletes catalogue records */
        Transaction
    }

    /**
     * 
     * Sections are informations about the service represented in the GetCapabilities operation <br>
     * <li>ServiceIdentification</li> <li>ServiceProvider</li> <li>OperationsMetadata</li> <li>Filter_Capabilities</li> <br>
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author: thomas $
     * 
     * @version $Revision: $, $Date: $
     */
    public enum Sections {

        /** Metadata about the CSW implementation */
        ServiceIdentification,
        /** Metadata about the organisation that provides the CSW implementation */
        ServiceProvider,
        /** Metadata about the operations provided by this CSW implementation */
        OperationsMetadata,
        /** Metadata about the filter capabilities that are implemented at this server */
        Filter_Capabilities
    }

    /**
     * Specifies the mode of the response that is requested. The modes are: <br>
     * <li>hits (default)</li> <li>results</li> <li>validate</li> <br>
     * 
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author: thomas $
     * 
     * @version $Revision: $, $Date: $
     */
    public enum ResultType {

        /** returns an empty SearchResults element that include the size of the result set */
        hits,
        /** returns one or more records from the result set up to the maximum number of records specified in the request */
        results,
        /** validates the request message */
        validate

    }

    /**
     * 
     * Specifies the elements that should be returned in the response <br>
     * <li>brief</li> <li>summary</li> <li>full</li> <br>
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author: thomas $
     * 
     * @version $Revision: $, $Date: $
     */
    public enum SetOfReturnableElements {

        /**
         * Brief representation of a record. This is the shortest view of a record by a specific profile.
         */
        brief,

        /**
         * Summary representation of a record. This view responses all the elements that should be queryable by a
         * record-profile.
         */
        summary,
        /**
         * Full representation of a record. In that response there are all the elements represented that a record holds.
         * Thus, there are elements presented that are not queryable regarding to the CSW specification.
         */
        full

    }

    /**
     * 
     * Specifies in which filter mode the query has to be processed. Either there is a OGC XML filter encoding after the
     * filterspecification document <a href="http://www.opengeospatial.org/standards/filter">OGC 04-095</a> or there is
     * a common query language string (CqlText) which can be seen as an explicit typed statement like an SQL statement.
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author: thomas $
     * 
     * @version $Revision: $, $Date: $
     */
    public enum ConstraintLanguage {

        /** Common Queryable Language Text filtering */
        CQLTEXT,

        /** Filterexpression specified in OGC Spec document 04-095 */
        FILTER

    }

    /**
     * 
     * Defined in the CSW-publication.xsd. Specifies the data manipulation operations <br>
     * <li>insert</li> <li>delete</li> <li>update</li> <br>
     * 
     * @author <a href="mailto:thomas@lat-lon.de">Steffen Thomas</a>
     * @author last edited by: $Author: thomas $
     * 
     * @version $Revision: $, $Date: $
     */
    public enum TransactionType {

        /**
         * With the INSERT action of the transaction operation there can be inserted one or more records into the
         * backend.
         */
        INSERT,

        /**
         * With the DELETE action of the transaction operation there can be deleted specific records defined by a filter
         * expression.
         */
        DELETE,

        /**
         * With the UPDATE action of the transaction operation there can be updated one complete record or just
         * properties of specific records defined by a filter expression.
         */
        UPDATE

    }

}
