//
// Copyright (c) 2007 Eric Russell. All rights reserved.
//

package org.myworldgis.netlogo;

import com.vividsolutions.jts.algorithm.CentroidArea;
import com.vividsolutions.jts.algorithm.CentroidLine;
import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryComponentFilter;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.triangulate.DelaunayTriangulationBuilder;

import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.myworldgis.netlogo.Painting;

import org.myworldgis.netlogo.VectorDataset.ShapeType;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.ExtensionObject;
import org.nlogo.api.LogoException;
import org.nlogo.api.LogoListBuilder;
import org.nlogo.core.Nobody$;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;


/**
 * 
 */
public final strictfp class VectorFeature implements ExtensionObject {

    //--------------------------------------------------------------------------
    // Inner classes
    //--------------------------------------------------------------------------
    
    /** */
    public static final strictfp class GetProperty extends GISExtension.Reporter {
        
        public String getAgentClassString() {
            return "OTPL";
        }
        
        public Syntax getSyntax() {
            return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType(),
                                                     Syntax.StringType() },
                                         Syntax.ReadableType());
        }
        
        public Object reportInternal (Argument args[], Context context)
                throws ExtensionException, LogoException {
            VectorFeature feature = getFeature(args[0]);
            String key = args[1].getString().toUpperCase();
            if (feature.hasProperty(key)) {
                Object result = feature.getProperty(key);
                if (result == null) {
                    return Nobody$.MODULE$;
                } else {
                    return result;
                }
            } else {
                throw new ExtensionException("feature does not have property '" + key + "'");
            }
        }
    }
    
    public static final strictfp class SetProperty extends GISExtension.Command {
        
        public String getAgentClassString() {
            return "OTPL";
        }
        
        public Syntax getSyntax() {
            return SyntaxJ.commandSyntax(new int[] { Syntax.WildcardType(),
                                                     Syntax.StringType(),
                                                     Syntax.StringType() | Syntax.NumberType() });
        }
        
        public void performInternal (Argument args[], Context context)
                throws ExtensionException, LogoException {
            VectorFeature feature = getFeature(args[0]);
            String key = args[1].getString().toUpperCase();
            if (!feature.hasProperty(key)) {
                throw new ExtensionException("feature does not have property '" + key + "'");
            }

            Object value = args[2].get();
            if (value instanceof String) {
                if (feature.getProperty(key) instanceof String) {
                    feature._properties.put(key, value);
                } else {
                    throw new ExtensionException("Tried to set a string property to a number value");
                }
            } else {
                if (feature.getProperty(key) instanceof Number) {
                    feature._properties.put(key, value);
                } else {
                    throw new ExtensionException("Tried to set a numeric property to a string value");
                }
            }
        }
    }

    /** */
    public static final strictfp class GetVertexLists extends GISExtension.Reporter {

        public String getAgentClassString() {
            return "OTPL";
        }
        
        public Syntax getSyntax() {
            return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() },
                                         Syntax.ListType());
        }
        
        @SuppressWarnings("unchecked")
        public Object reportInternal (Argument args[], Context context)
                throws ExtensionException, LogoException {
            VectorFeature feature = getFeature(args[0]);
            final LogoListBuilder result = new LogoListBuilder();
            feature.getGeometry().apply(new GeometryComponentFilter() {
                    public void filter (Geometry geom) {
                        if (geom instanceof Point) {
                            LogoListBuilder list = new LogoListBuilder() ;
                            list.add(new Vertex(((Point)geom).getCoordinate()));
                            result.add(list.toLogoList());
                        } else if (geom instanceof LineString) {
                            LineString ls = (LineString)geom;
                            LogoListBuilder list = new LogoListBuilder();
                            for (int i = 0; i < ls.getNumPoints(); i += 1) {
                                list.add(new Vertex(ls.getCoordinateN(i)));
                            }
                            result.add(list.toLogoList());
                        }   
                    }
                });
            return result.toLogoList();
        }
    }
    
    /** */
    public static final strictfp class GetCentroid extends GISExtension.Reporter {

        public String getAgentClassString() {
            return "OTPL";
        }
        
        public Syntax getSyntax() {
            return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() },
                                         Syntax.WildcardType());
        }
        
        @SuppressWarnings("unchecked")
        public Object reportInternal (Argument args[], Context context)
                throws ExtensionException, LogoException {
            VectorFeature feature = getFeature(args[0]);
            switch (feature.getShapeType()) {
                case POINT:
                    CentroidPoint cp = new CentroidPoint();
                    cp.add(feature.getGeometry());
                    return new Vertex(cp.getCentroid());
                case LINE:
                    CentroidLine cl = new CentroidLine();
                    cl.add(feature.getGeometry());
                    return new Vertex(cl.getCentroid());
                case POLYGON:
                    CentroidArea ca = new CentroidArea();
                    ca.add(feature.getGeometry());
                    return new Vertex(ca.getCentroid());
                default:
                    throw new ExtensionException("invalid shape type");
            }
        }
    }

    public static final strictfp class GetSamplePointInside extends GISExtension.Reporter {

        public String getAgentClassString() {
            return "OTPL";
        }
        
        public Syntax getSyntax() {
            return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType() },
                                         Syntax.WildcardType());
        }

        private static Coordinate randomPointInsideTriangle (Geometry tri) {
            // For more, see:
            //  Weisstein, Eric W. "Triangle Point Picking." From MathWorld--A Wolfram Web Resource. https://mathworld.wolfram.com/TrianglePointPicking.html 
            Random rand = new Random();
            double weight_b = rand.nextDouble();
            double weight_c = rand.nextDouble();

            Coordinate[] coords = tri.getCoordinates();
            Coordinate p_a = coords[0];
            Coordinate p_b = coords[1];
            Coordinate p_c = coords[2];

            if (weight_b + weight_c > 1.0) {
                weight_b = 1.0 - weight_b;
                weight_c = 1.0 - weight_c;
            }

            double b_x = weight_b * (p_b.x - p_a.x); 
            double b_y = weight_b * (p_b.y - p_a.y); 
            double c_x = weight_c * (p_c.x - p_a.x); 
            double c_y = weight_c * (p_c.y - p_a.y); 

            double x = b_x + c_x + p_a.x;
            double y = b_y + c_y + p_a.y;

            return new Coordinate(x, y);
        }

 
        public Object reportInternal (Argument args[], Context context) 
                throws ExtensionException, LogoException {

            VectorFeature feature = getFeature(args[0]);
            if (feature.getShapeType() != ShapeType.POLYGON) {
                throw new ExtensionException("Tried to get a point inside of a non-polygon vector feature");
            }

            if (feature._triangulation == null) {
                feature.setupTriangulation(context);
            }

            Random r = new Random();
            double randBetween = r.nextDouble() * feature._total_area;

            // Arrays.binarySearch will return `(-(insertion point) - 1)`  if the value is not found within the array,
            // where the insertion point is the point where the key would be placed if it were inserted
            // https://docs.oracle.com/javase/7/docs/api/java/util/Arrays.html#binarySearch(double[],%20double)
            // - James Hovet 2/24/21
            int index = (- Arrays.binarySearch(feature._triangulation_areas_cumulative, randBetween)) - 1;

            // System.out.println(randBetween + " : " + index + " of " + feature._triangulation_areas_cumulative.length);

            Coordinate out = randomPointInsideTriangle(feature._triangulation.getGeometryN(index));

            return new Vertex(out);

        }
    }

    //--------------------------------------------------------------------------
    // Class methods
    //--------------------------------------------------------------------------
    
    /** */
    static VectorFeature getFeature (Argument arg) 
            throws ExtensionException, LogoException {
        Object obj = arg.get();
        if (obj instanceof VectorFeature) {
            return (VectorFeature)obj;
        } else {
            throw new ExtensionException("not a VectorFeature: " + obj);
        }
    }
    
    //--------------------------------------------------------------------------
    // Instance variables
    //--------------------------------------------------------------------------
    
    /** */
    private VectorDataset.ShapeType _shapeType;
    
    /** */
    private Geometry _geometry;
    
    /** */
    private Map<String,Object> _properties;

    /** These three only used for sample-point-inside and are only intialized when the first sample point is 
     * requested - James Hovet 2/24/21
     */
    private Geometry _triangulation;
    private double[] _triangulation_areas_cumulative;  
    private double _total_area;
    
    //--------------------------------------------------------------------------
    // Constructors
    //--------------------------------------------------------------------------
    
    /** */
    public VectorFeature (VectorDataset.ShapeType shapeType,
                          Geometry geometry,
                          VectorDataset.Property[] properties,
                          Object[] propertyValues) {
        _shapeType= shapeType;
        _geometry = geometry;
        _properties = new HashMap<String,Object>(properties.length);
        for (int i = 0; i < properties.length; i += 1) {
            _properties.put(properties[i].getName(), propertyValues[i]);   
        }
    }
    
    //--------------------------------------------------------------------------
    // Instance methods
    //--------------------------------------------------------------------------
    
    /** */
    public VectorDataset.ShapeType getShapeType () {
        return _shapeType;
    }
    
    /** */
    public Envelope getEnvelope () {
        return _geometry.getEnvelopeInternal();
    }
    
    /** */
    public Geometry getGeometry () {
        return _geometry;
    }
    
    /** */
    public boolean hasProperty (String name) {
        return _properties.containsKey(name);
    }
    
    /** */
    public Object getProperty (String name) {
        return _properties.get(name.toUpperCase());
    }

    private void setupTriangulation (Context context) throws ExtensionException {
        DelaunayTriangulationBuilder builder = new DelaunayTriangulationBuilder();
        builder.setSites(_geometry);
        builder.setTolerance(0.0);
        _triangulation = builder.getTriangles(_geometry.getFactory());
        _triangulation = _triangulation.intersection(_geometry);

        int numTriangles = _triangulation.getNumGeometries();
        _triangulation_areas_cumulative = new double[numTriangles];
        for (int i = 0; i < numTriangles; i++) {
            double triangleArea = _triangulation.getGeometryN(i).getArea();
            _total_area += triangleArea;
            _triangulation_areas_cumulative[i] = _total_area;
        }
    }
    
    //--------------------------------------------------------------------------
    // ExtensionObject implementation
    //--------------------------------------------------------------------------
    
    /**
     * Returns a string representation of the object.  If readable is
     * true, it should be possible read it as NL code.
     *
     **/
    public String dump (boolean readable, boolean exporting, boolean reference ) {
        StringBuffer buffer = new StringBuffer();
        for (Iterator<String> i = _properties.keySet().iterator(); i.hasNext();) {
            String propertyName = i.next();
            buffer.append("[\"");
            buffer.append(propertyName);
            buffer.append("\":\"");
            buffer.append(_properties.get(propertyName));
            buffer.append("\"]");
        }
        return buffer.toString();
    }

    /** */
    public String getExtensionName () {
        return "gis";
    }

    /** */
    public String getNLTypeName() {
        return "VectorFeature";
    }
    
    /** */
    public boolean recursivelyEqual (Object obj) {
        if (obj instanceof VectorFeature) {
            VectorFeature vf = (VectorFeature)obj;
            return vf == this;
        } else {
            return false;
        }
    }
}
