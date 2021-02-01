package org.myworldgis.io.geojson;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Writer;

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

    @SuppressWarnings("unchecked")
    private void setupJsonObject() {
        root.put("type", "FeatureCollection");  
    }

    @SuppressWarnings("unchecked")
    private void processVectorFeatures() throws IOException {
        VectorDataset.Property[] propSchema = dataset.getProperties();
        for (Iterator<VectorFeature> i = dataset.getFeatures().iterator(); i.hasNext(); ) {
            VectorFeature f = i.next();

            JSONObject thisFeatureJson = new JSONObject();
            thisFeatureJson.put("type", "Feature");

            Geometry g = f.getGeometry();
            thisFeatureJson.put("geometry", "null");

            JSONObject thisPropertiesObj = new JSONObject();
            for (VectorDataset.Property prop : propSchema) {
                String propName = prop.getName();
                thisPropertiesObj.put(propName, f.getProperty(propName));
            }
            thisFeatureJson.put("properties", thisPropertiesObj);
            this.features.add(thisFeatureJson);
        }
    }

    @SuppressWarnings("unchecked")
    private void writeToFile() throws IOException {
        root.put("features", this.features);
        file.writeBytes(root.toJSONString());
    }
}
