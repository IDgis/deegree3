package org.deegree.model.geometry.multi;

/**
 * 
 * 
 * 
 * @author <a href="mailto:poth@lat-lon.de">Andreas Poth</a>
 * @author last edited by: $Author: poth $
 * 
 * @version. $Revision: 6251 $, $Date: 2007-03-19 16:59:28 +0100 (Mo, 19 Mrz 2007) $
 */
public interface GeometryCollection<T> extends MultiGeometry<T> {

    public boolean containsPoints();
    
    public boolean containsCurves();
    
    public boolean containsSurfaces();
    
    public boolean containsSolids();
    
    public boolean containsComplexes();
    
    public boolean containsCollections();
	
}