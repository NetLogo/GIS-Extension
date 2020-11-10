package org.myworldgis.netlogo;

import org.myworldgis.projection.Geographic;
import org.myworldgis.projection.Projection;
import org.myworldgis.projection.ProjectionUtils;
import org.myworldgis.util.GeometryUtils;
import org.ngs.ngunits.Unit;
import org.ngs.ngunits.quantity.Angle;
import org.ngs.ngunits.NonSI;
import org.ngs.ngunits.SI;

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
        boolean reproject = true;
        

        System.out.println(dstProj.getEllipsoid().toString());
        System.out.println(dstProj.getEllipsoid().radius);
        System.out.println(dstProj.getEllipsoid().eccsq);
        System.out.println(Projection.DEFAULT_ELLIPSOID.toString());
        System.out.println(Projection.DEFAULT_ELLIPSOID.radius);
        System.out.println(Projection.DEFAULT_ELLIPSOID.eccsq);
        System.out.println(dstProj.getEllipsoid() == Projection.DEFAULT_ELLIPSOID);
        System.out.println(dstProj.getCenter().toString());
        System.out.println(dstProj.getCenter() == Projection.DEFAULT_CENTER);
        
        if (dstProj instanceof Geographic){
            Geographic geographic = (Geographic) dstProj;
            Unit<Angle> units = geographic.getUnits();
            boolean unitsMatch = units.getConverterTo(SI.RADIAN).convert(1.0) == Projection.DEGREES_TO_RADIANS.convert(1.0);
            boolean centersMatch = geographic.getCenter().equals2D(Projection.DEFAULT_CENTER);
            if(unitsMatch && centersMatch){
                reproject = false;
            }
        }
        System.out.println("lonLat " + lon + " " + lat);
        Geometry point = GISExtension.getState().factory().createPoint(new Coordinate(lon, lat));
        if (point == null){
            return result.toLogoList();
        }
        if(reproject){
            GeometryTransformer forward = dstProj.getForwardTransformer();
            GeometryTransformer inverse = new Geographic(Projection.DEFAULT_ELLIPSOID, Projection.DEFAULT_CENTER, NonSI.DEGREE_ANGLE).getInverseTransformer();
            point = forward.transform(inverse.transform(point));
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