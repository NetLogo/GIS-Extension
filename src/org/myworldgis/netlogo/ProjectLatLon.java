package org.myworldgis.netlogo;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.myworldgis.projection.Geographic;
import org.myworldgis.projection.Projection;
import org.ngs.ngunits.NonSI;
import org.ngs.ngunits.SI;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.util.GeometryTransformer;


/**
 * 
 */
public final strictfp class ProjectLatLon extends GISExtension.Reporter {
    
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

        Geometry point = GISExtension.getState().factory().createPoint(new Coordinate(lon, lat));
        if (point == null){
            return result.toLogoList();
        }

        boolean reproject = !isDefaultGeographicProjection(dstProj);
        if(reproject){
            GeometryTransformer forward = dstProj.getForwardTransformer();
            GeometryTransformer inverse = new Geographic(Projection.DEFAULT_ELLIPSOID, Projection.DEFAULT_CENTER, NonSI.DEGREE_ANGLE).getInverseTransformer();
            point = forward.transform(inverse.transform(point));
        }

        Coordinate projected = point.getCoordinate();
        if (projected == null){
            return result.toLogoList();
        }

        Coordinate transformed = GISExtension.getState().gisToNetLogo(projected, null);
        if(transformed != null){
            result.add(Double.valueOf(transformed.x));
            result.add(Double.valueOf(transformed.y));
        }
        return result.toLogoList();
    }

    private boolean isDefaultGeographicProjection(Projection projection){
        if (projection instanceof Geographic){
            Geographic geographic = (Geographic) projection;
            if(geographic.getCenter().equals2D(Projection.DEFAULT_CENTER) 
                && geographic.getEllipsoid().equals(Projection.DEFAULT_ELLIPSOID)
                && geographic.getUnits().getConverterTo(SI.RADIAN).convert(1.0) == Projection.DEGREES_TO_RADIANS.convert(1.0)){
                return true;
            }
        }
        return false;
    }
}