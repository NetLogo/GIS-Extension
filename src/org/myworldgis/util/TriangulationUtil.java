package org.myworldgis.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Polygon;
import org.tinfour.common.IConstraint;
import org.tinfour.common.IIncrementalTin;
import org.tinfour.common.PolygonConstraint;
import org.tinfour.common.Vertex;
import org.tinfour.standard.IncrementalTin;
import org.tinfour.utils.TriangleCollector;

public class TriangulationUtil {
    private static Coordinate tinfourVertexToJTSCoordinate(Vertex vert) {
        return new Coordinate(vert.x, vert.y);
    }

    private static Polygon tinfourVertsToJTSPolygon(Vertex[] verts, GeometryFactory factory) {
        Coordinate[] coords = new Coordinate[4];
        for (int i = 0; i < 3; i++) {
            coords[i] = tinfourVertexToJTSCoordinate(verts[i]);
        }
        // in JTS poygons, the first and last vertex are identical -James Hovet 3/8/21
        coords[3] = tinfourVertexToJTSCoordinate(verts[0]);         
        return factory.createPolygon(coords);
    }

    private static class GeometryCollectionBuilder implements Consumer<Vertex[]> {
        GeometryFactory factory;
        Geometry geom;
        List<Geometry> geometries;

        GeometryCollectionBuilder(Geometry _geom) {
            this.geom = _geom;
            this.factory = geom.getFactory();
            geometries = new ArrayList<Geometry>();
        }

        public GeometryCollection getCollection() {
            Geometry[] geometriesArr = new Geometry[geometries.size()];
            geometriesArr = geometries.toArray(geometriesArr);
            return new GeometryCollection(geometriesArr, factory);
        }

        @Override
        public void accept(Vertex[] verts) {
            Polygon poly = tinfourVertsToJTSPolygon(verts, factory);
            geometries.add(poly);
        }

    }

    public static Geometry triangulate(Geometry geom) {
        List<IConstraint> constraints = new ArrayList<>();

        int numGeometries = geom.getNumGeometries();
        for (int n = 0; n < numGeometries; n++) {
            Polygon thisPolygon = (Polygon) geom.getGeometryN(n);
            LineString outerRing = thisPolygon.getExteriorRing();
            CoordinateSequence seq = outerRing.getCoordinateSequence();
            Vertex[] exteriorVertices = new Vertex[seq.size() - 1];
            for (int i = 0; i < exteriorVertices.length; i++) {
                Coordinate c = seq.getCoordinate(i);
                exteriorVertices[exteriorVertices.length - i - 1] = new Vertex(c.x, c.y, 0);
            }
            PolygonConstraint polygonConstraint = new PolygonConstraint(Arrays.asList(exteriorVertices));
            polygonConstraint.complete();
            constraints.add(polygonConstraint);

            int numInteriorRings = thisPolygon.getNumInteriorRing();
            for(int interiorRing = 0; interiorRing < numInteriorRings; interiorRing++) {
                LineString thisRing = thisPolygon.getInteriorRingN(interiorRing);
                CoordinateSequence thisSeq = thisRing.getCoordinateSequence();
                Vertex[] verts = new Vertex[thisSeq.size() - 1];
                for (int i = 0; i < verts.length; i++) {
                    Coordinate c = thisSeq.getCoordinate(i);
                    verts[verts.length - i - 1] = new Vertex(c.x, c.y, 0);
                }
                PolygonConstraint thisConstraint = new PolygonConstraint(Arrays.asList(verts));
                thisConstraint.complete();
                constraints.add(thisConstraint);
            }
        }
        IIncrementalTin tin = new IncrementalTin(0.000000001);
        tin.addConstraints(constraints, true);

        GeometryCollectionBuilder geometryCollectionBuilder = new GeometryCollectionBuilder(geom);
        TriangleCollector.visitTrianglesConstrained(tin, geometryCollectionBuilder);

        return geometryCollectionBuilder.getCollection();
    }
    
}
