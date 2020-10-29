package org.myworldgis.netlogo;

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

/**
 * 
 */
public final strictfp class Project extends GISExtension.Reporter {
    
    //--------------------------------------------------------------------------
    // GISExtension.Reporter implementation
    //--------------------------------------------------------------------------
    
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
        GeometryFactory factory = new GeometryFactory();
        Coordinate latLonRadians = new Coordinate(Projection.DEGREES_TO_RADIANS.convert(lat), Projection.DEGREES_TO_RADIANS.convert(lon)); 
        System.out.println("latLonRadians " + latLonRadians.toString());
        Geometry point = factory.createPoint(latLonRadians);
        point = forward.transform(point);
        if (point == null){
            return result.toLogoList();
        }
        Coordinate projected = point.getCoordinate();
        if (projected == null){
            return result.toLogoList();
        }
        System.out.println("Projected " + projected.toString());
        Coordinate transformed = GISExtension.getState().gisToNetLogo(projected, null);
        System.out.println("Transformed " + transformed.toString());        
        if(transformed != null){
            result.add(Double.valueOf(transformed.x));
            result.add(Double.valueOf(transformed.y));
        }
        return result.toLogoList();
    }
}