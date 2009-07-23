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
package org.deegree.commons.xml.schema;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSObjectList;
import org.deegree.commons.xml.XMLProcessingException;
import org.deegree.feature.gml.schema.XSModelGMLAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMErrorHandler;

/**
 * Provides convenient methods to retrieve "relevant" element and type declarations of an XML schema infoset (which is
 * represented as a Xerces {@link XSModel}).
 * <p>
 * This functionality is used to extract higher-level structures defined using XML schema, such as GML feature types.
 * </p>
 * 
 * @see XSModelGMLAnalyzer
 * @author <a href="mailto:schneider@lat-lon.de">Markus Schneider </a>
 * @author last edited by: $Author:$
 * 
 * @version $Revision:$, $Date:$
 */
public class XSModelAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger( XSModelAnalyzer.class );

    /** Encapsulates the full information of the XML schema infoset. */
    protected final XSModel xmlSchema;

    /**
     * Creates a new <code>XSModelAnalyzer</code> for a given XML schema infoset.
     * 
     * @param xmlSchema
     *            schema infoset
     */
    public XSModelAnalyzer( XSModel xmlSchema ) {
        this.xmlSchema = xmlSchema;
    }

    /**
     * Creates a new <code>XSModelAnalyzer</code> that reads a schema document from the given URL.
     * 
     * @param url
     *            location of the schema document
     * @throws ClassCastException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public XSModelAnalyzer( String url ) throws ClassCastException, ClassNotFoundException, InstantiationException,
                            IllegalAccessException {
        xmlSchema = loadModel( url );
    }

    /**
     * Returns the XML schema infoset (represented as a Xerces {@link XSModel}).
     * 
     * @return the XML schema infoset
     */
    public XSModel getXSModel() {
        return xmlSchema;
    }

    /**
     * Returns the declarations of all elements that are substitutable for a given element declaration.
     * 
     * @param elementDecl
     *            element declaration
     * @param namespace
     *            only element declarations in this namespace are returned, set to null for all namespaces
     * @param transitive
     *            if true, also substitutions for substitutions (and so one) are included
     * @param onlyConcrete
     *            if true, only concrete (non-abstract) declarations are returned
     * @return the declarations of all substitution elements in the requested namespace
     */
    public List<XSElementDeclaration> getSubstitutions( XSElementDeclaration elementDecl, String namespace,
                                                        boolean transitive, boolean onlyConcrete ) {
        XSObjectList xsObjectList = xmlSchema.getSubstitutionGroup( elementDecl );
        List<XSElementDeclaration> substitutions = new ArrayList<XSElementDeclaration>( xsObjectList.getLength() );
        for ( int i = 0; i < xsObjectList.getLength(); i++ ) {
            XSElementDeclaration substitution = (XSElementDeclaration) xsObjectList.item( i );
            if ( transitive || substitution.getSubstitutionGroupAffiliation().equals( elementDecl ) ) {
                if ( !substitution.getAbstract() || !onlyConcrete ) {
                    if ( namespace == null || namespace.equals( substitution.getNamespace() ) ) {
                        substitutions.add( (XSElementDeclaration) xsObjectList.item( i ) );
                    }
                }
            }
        }
        if ( transitive && ( !onlyConcrete || !elementDecl.getAbstract() ) ) {
            substitutions.add( elementDecl );
        }
        return substitutions;
    }

    /**
     * Returns the declarations of all elements that are substitutable for a given element name.
     * 
     * @param elementName
     *            qualified name of the element
     * @param namespace
     *            only element declarations in this namespace are returned, set to null for all namespaces
     * @param transitive
     *            if true, also substitutions for substitutions (and so one) are included
     * @param onlyConcrete
     *            if true, only concrete (non-abstract) declarations are returned
     * @return the declarations of all substitution elements in the requested namespace
     */
    public List<XSElementDeclaration> getSubstitutions( QName elementName, String namespace, boolean transitive,
                                                        boolean onlyConcrete ) {
        XSElementDeclaration elementDecl = xmlSchema.getElementDeclaration( elementName.getLocalPart(),
                                                                            elementName.getNamespaceURI() );
        if ( elementDecl == null ) {
            String msg = "The schema does not declare a top-level element with name '" + elementName + "'.";
            throw new IllegalArgumentException( msg );
        }
        return getSubstitutions( elementDecl, namespace, transitive, onlyConcrete );
    }

    public static XSModel loadModel( String url )
                            throws ClassCastException, ClassNotFoundException, InstantiationException,
                            IllegalAccessException {

        XMLSchemaLoader schemaLoader = new XMLSchemaLoader();
        DOMConfiguration config = schemaLoader.getConfig();

        // create and register DOMErrorHandler
        DOMErrorHandler errorHandler = new DOMErrorHandler() {
            @SuppressWarnings("synthetic-access")
            public boolean handleError( DOMError domError ) {
                switch ( domError.getSeverity() ) {
                case DOMError.SEVERITY_WARNING: {
                    LOG.debug( "DOM warning: " + domError.getMessage() );
                    break;
                }
                case DOMError.SEVERITY_ERROR:
                case DOMError.SEVERITY_FATAL_ERROR: {
                    String msg = "Severe error in schema document (line: " + domError.getLocation().getLineNumber()
                                 + ", column: " + domError.getLocation().getColumnNumber() + ") "
                                 + domError.getMessage();
                    throw new XMLProcessingException( msg );
                }
                }
                return false;
            }
        };

        config.setParameter( "error-handler", errorHandler );
        config.setParameter( "validate", Boolean.TRUE );

        schemaLoader.setEntityResolver( new RedirectingEntityResolver() );
        
        return schemaLoader.loadURI( url );
    }
}
