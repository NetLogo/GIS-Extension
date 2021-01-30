package org.myworldgis.io.geojson;

import java.io.IOException;
import java.io.RandomAccessFile;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.myworldgis.netlogo.VectorDataset;
import org.myworldgis.netlogo.VectorFeature;
import java.util.Iterator;

public class GeoJsonWriter implements GeoJsonConstants {

    private RandomAccessFile file;
    private VectorDataset dataset;
    private JSONObject root;
    private JSONArray features;

    public GeoJsonWriter(RandomAccessFile file, VectorDataset dataset) throws IOException {
        this.file = file;
        this.dataset = dataset;
        this.root = new JSONObject();
        this.features = new JSONArray();

        setupJsonObject();

        processVectorFeatures();

        writeToFile();
    }

    private void setupJsonObject() {

    }

    private void processVectorFeatures() throws IOException {
        VectorDataset.Property[] props = dataset.getProperties();
        for (Iterator<VectorFeature> i = dataset.getFeatures().iterator(); i.hasNext(); ) {
            System.out.println("---");
            VectorFeature f = i.next();
            Geometry g = f.getGeometry();
            System.out.println("geom: ");
            System.out.println(g.getGeometryType());
            for (VectorDataset.Property prop : props) {
                System.out.println(prop.getName());
                System.out.println(f.getProperty(prop.getName()));
            }
        }
    }

    private void writeToFile() {

    }
}
