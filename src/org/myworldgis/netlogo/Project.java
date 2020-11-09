package org.myworldgis.netlogo;

import org.myworldgis.projection.Geographic;
import org.myworldgis.projection.Projection;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.util.GeometryTransformer;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.ngs.ngunits.SI;
import org.ngs.ngunits.NonSI;
import org.ngs.ngunits.Unit;
import org.ngs.ngunits.UnitConverter;
import org.ngs.ngunits.quantity.Angle;


/**
 * 
 */
public final strictfp class Project extends GISExtension.Reporter {
    
    //--------------------------------------------------------------------------
    // GISExtension.Reporter implementation
    //--------------------------------------------------------------------------

    private final Coordinate _temp = new Coordinate();
    
    /** */
    public String getAgentClassString() {
        return "OTPL";
    }
    
    /** */
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(new int[] { Syntax.NumberType(), Syntax.NumberType() },
                                     Syntax.ListType());
    }
    
    /** */
    public Object reportInternal (Argument args[], Context context) 
            throws ExtensionException , LogoException {
        double lat = args[0].getDoubleValue();
        double lon = args[1].getDoubleValue();
        LogoListBuilder result = new LogoListBuilder();
        Projection dstProj = GISExtension.getState().getProjection();
        if (dstProj == null){
            throw new ExtensionException("You must use gis:load-coordinate-system or gis:set-coordinate-system before you can project lat/lon pairs.");
        }
        System.out.println("latLon " + lat + " " + lon);
        GeometryTransformer forward = dstProj.getForwardTransformer();
        GeometryFactory factory = GISExtension.getState().factory();
        Coordinate latLonRadians = new Coordinate(Projection.DEGREES_TO_RADIANS.convert(lat), Projection.DEGREES_TO_RADIANS.convert(lon)); 
        Geometry point = factory.createPoint(latLonRadians);
        if (dstProj instanceof Geographic){
            System.out.println("geographic:");
            // System.out.println("center:" + dstProj.getCenter().toString());
            // System.out.println("northing:" + dstProj.getCenterNorthing());
            // System.out.println("easting:" + dstProj.getCenterEasting());
            Coordinate latLon = new Coordinate(lat, lon);
            point = factory.createPoint(latLon);
        } else {
            point = forward.transform(point);
        }
        if (point == null){
            return result.toLogoList();
        }
        Coordinate projected = point.getCoordinate();
        if (projected == null){
            return result.toLogoList();
        }
        System.out.println("Projected " + projected.toString());
        Coordinate transformed = GISExtension.getState().gisToNetLogo(projected, _temp);
        if (transformed == null){
            return result.toLogoList();
        }
        System.out.println("Transformed " + transformed.toString());        
        if(transformed != null){
            result.add(Double.valueOf(transformed.x));
            result.add(Double.valueOf(transformed.y));
        }
        return result.toLogoList();
    }
}