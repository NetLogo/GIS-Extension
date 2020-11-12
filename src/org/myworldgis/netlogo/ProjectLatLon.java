package org.myworldgis.netlogo;

import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;
import org.myworldgis.projection.Ellipsoid;
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
public abstract strictfp class ProjectLatLon {

    private static final Ellipsoid WGS84 = new Ellipsoid("WGS 84", 6378137.0, SI.METER, 298.257223563);

    /**
     * 
     */
    public static final strictfp class ProjectFromEllipsoid extends GISExtension.Reporter {

        /** */
        public String getAgentClassString() {
            return "OTPL";
        }
        
        /** */
        public Syntax getSyntax() {
            return SyntaxJ.reporterSyntax(new int[] { Syntax.NumberType(), 
                                                        Syntax.NumberType(), 
                                                        Syntax.NumberType(), 
                                                        Syntax.NumberType() },
                                        Syntax.ListType());
        }

        /** */
        public Object reportInternal (Argument args[], Context context) 
                throws ExtensionException , LogoException {
            double lat = args[0].getDoubleValue();
            double lon = args[1].getDoubleValue();
            double ellispoidRadius = args[2].getDoubleValue();
            double ellispoidInverseFlattening = args[3].getDoubleValue();
            Ellipsoid srcEllipsoid = new Ellipsoid("user", ellispoidRadius, SI.METER, ellispoidInverseFlattening);
            return projectPointGivenEllipsoid(lat, lon, srcEllipsoid);
        }
    }

    /**
     * 
     */
    public static final strictfp class ProjectWGS84 extends GISExtension.Reporter {

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
            return projectPointGivenEllipsoid(lat, lon, WGS84);
        }
    }

    /**
     * 
     */
    public static Object projectPointGivenEllipsoid(double lat, double lon, Ellipsoid srcEllipsoid)
            throws ExtensionException , LogoException {
        LogoListBuilder result = new LogoListBuilder();
        Projection dstProj = GISExtension.getState().getProjection();
        if (dstProj == null){
            throw new ExtensionException("You must use gis:load-coordinate-system or gis:set-coordinate-system before you can project lat/lon pairs.");
        }

        Geometry point = GISExtension.getState().factory().createPoint(new Coordinate(lon, lat));
        if (point == null){
            return result.toLogoList();
        }
        
        Ellipsoid dstEllipsoid = dstProj.getEllipsoid();
        // In cases where the destination projection and destination reference 
        // ellipsoid are the same as the given reference ellipsoid, don't 
        // reproject and introduce a loss precision. cf. similar behavior 
        // in LoadDataset.java 
        boolean shouldReproject = !(dstProj instanceof Geographic) || !srcEllipsoid.equals(dstEllipsoid);
        if(shouldReproject){
            GeometryTransformer forward = dstProj.getForwardTransformer();
            GeometryTransformer inverse = new Geographic(srcEllipsoid, Projection.DEFAULT_CENTER, NonSI.DEGREE_ANGLE).getInverseTransformer();
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
}