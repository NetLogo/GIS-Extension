package org.myworldgis.io.geojson;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;

import org.myworldgis.netlogo.VectorDataset;
import org.myworldgis.netlogo.VectorDataset.PropertyType;
import org.myworldgis.netlogo.VectorDataset.ShapeType;
import org.nlogo.api.ExtensionException;
import org.nlogo.core.File;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class GeoJsonReader {

    public final static String GEOJSON_EXTENSION = "geojson";
    public final static String JSON_EXTENSION = "json";
    private static final Map<String, ShapeType> geoJsonStringTypesToShapeTypes;
    static {
        Map<String, ShapeType> tmpMap = new HashMap<String, ShapeType>();
        tmpMap.put("Point", ShapeType.POINT);
        tmpMap.put("MultiPoint", ShapeType.POINT);
        tmpMap.put("LineString", ShapeType.LINE);
        // tmpMap.put("MultiLineString", ShapeType.LINE); // Not yet implemented
        tmpMap.put("Polygon", ShapeType.POLYGON);
        // tmpMap.put("MultiPolygon", ShapeType.POLYGON); // Not yet implemented
        geoJsonStringTypesToShapeTypes = Collections.unmodifiableMap(tmpMap);
    }

    JSONObject geojson;
    GeometryFactory factory;

    ShapeType             shapeType;
    String                geojsonShapeType;
    int                   size;
    int                   numProperties;
    Map<String, Integer>  propertiesToIndices;
    String[]              propertyNames;
    PropertyType[]        propertyTypes;
    Geometry[]            geometries;
    Object[][]            propertyValues;
    

    public GeoJsonReader(File file, GeometryFactory factory) throws IOException, ParseException, ExtensionException {
        this.factory = factory;

        //TODO: Figure out why file.reader() wouldn't go into parser.parse. 
        Scanner s = new Scanner(file.getInputStream()).useDelimiter("\\Z");
        String out = s.hasNext() ? s.next() : "";

        JSONParser parser = new JSONParser();
        this.geojson = (JSONObject) parser.parse(out);

        String topLevelType = geojson.get("type").toString();

        if (topLevelType.equals("FeatureCollection")) {
            parseFeatureCollection();
        } else if (topLevelType.equals("Feature")) {
            parseSingleFeatureDataset();
        } else if (topLevelType.equals("Geometry")) {
            parseSingleGeometryDataset();
        } else {
            throw new ExtensionException(topLevelType + " is not a valid GeoJSON type");
        }
    }

    public void parseFeatureCollection() throws ExtensionException {
        JSONArray features = (JSONArray) geojson.get("features");

        if (features.size() < 1) {
            throw new ExtensionException("Each FeatureCollection must have at least one feature.");
        }
        this.size = features.size();
        this.geometries = new Geometry[size];
        this.propertyValues = new Object[size][];

        JSONObject firstFeature = (JSONObject) features.get(0);

        String firstGeometryTypeString = ((JSONObject) firstFeature.get("geometry")).get("type").toString();
        this.shapeType = mapStringToShapeType(firstGeometryTypeString);
        this.geojsonShapeType = firstGeometryTypeString;

        JSONObject firstFeatureProperties = ((JSONObject) firstFeature.get("properties"));
        this.numProperties = firstFeatureProperties.size();

        int propertyIndex = 0;
        this.propertiesToIndices = new HashMap<String, Integer>();
        this.propertyNames = new String[numProperties];
        this.propertyTypes = new PropertyType[numProperties];
        for (Object entryObj : firstFeatureProperties.entrySet()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) entryObj;

            this.propertiesToIndices.put(entry.getKey().toString(), propertyIndex);
            this.propertyNames[propertyIndex] = entry.getKey().toString(); 
            this.propertyTypes[propertyIndex] = getPropertyTypeForValue(entry.getValue());

            // System.out.println(propertyIndex + " : " + propertyNames[propertyIndex] + " : " + propertyTypes[propertyIndex]);
            propertyIndex ++;
        }

        int featureIndex = 0;
        for (Object featureObj : features) {
            JSONObject feature = (JSONObject) featureObj;
            JSONObject geometry = (JSONObject) feature.get("geometry");
            if (!geometry.get("type").toString().equals(firstGeometryTypeString)) { 
                throw new ExtensionException("Only homogenous FeatureCollections are supported");
            }

            JSONArray coordinates = (JSONArray) geometry.get("coordinates");
            this.geometries[featureIndex] = factory.createPoint(new Coordinate((Double) coordinates.get(0),(Double) coordinates.get(1)));
            this.geometries[featureIndex] = parseGeometry(coordinates, this.geojsonShapeType);

            Object[] thesePropertyValues = new Object[this.numProperties];
            JSONObject propertiesObject = (JSONObject) feature.get("properties");
            for(int i = 0; i < this.numProperties; i++){
                if (!propertiesObject.containsKey(propertyNames[i])) {
                    // continue;
                    throw new ExtensionException(propertyNames[i] + " is missing from at least one feature");
                }
                Object thisPropertyValue = propertiesObject.get(propertyNames[i]);
                PropertyType thisPropertyType = getPropertyTypeForValue(thisPropertyValue);
                if (!thisPropertyType.equals(propertyTypes[i])) {
                    throw new ExtensionException("Not all " + propertyNames[i] + "'s are the same datatype");
                }
                // System.out.println(i + " : " + thisPropertyValue + " : " + propertyTypes[i]);
                thesePropertyValues[i] = thisPropertyValue;
            }
            this.propertyValues[featureIndex] = thesePropertyValues;
            featureIndex ++;
        }
    }

    public void parseSingleFeatureDataset() throws ExtensionException {
        return;
    }

    public void parseSingleGeometryDataset() throws ExtensionException {
        return;
    }

    public int size(){
        return size;
    }

    public ShapeType getShapeType(){
        return shapeType;
    }

    public String[] getPropertyNames(){
        return propertyNames;
    }

    public PropertyType[] getPropertyTypes(){
        return propertyTypes;
    }

    public Geometry[] getGeometries(){
        return geometries;
    }

    public Object[][] getPropertyValues(){
        return propertyValues;
    }

    private Geometry parseGeometry(JSONArray coordinates, String geojsonShapeType) throws ExtensionException{
        switch (geojsonShapeType) {
            case "Point":
                Geometry point = factory.createPoint(new Coordinate((Double) coordinates.get(0),(Double) coordinates.get(1)));
                return point;
            case "LineString":
                Coordinate[] linePoints = new Coordinate[coordinates.size()];
                for (int i = 0; i < coordinates.size(); i++) {
                    linePoints[i] = JSONPairToCoordinate((JSONArray) coordinates.get(i));
                }
                Geometry line = factory.createLineString(linePoints);
                return line;
            default:
                throw new ExtensionException(geojsonShapeType + " is not a supported geojson shape type");
        }
    }

    private static Coordinate JSONPairToCoordinate(JSONArray arr){
        return new Coordinate((Double) arr.get(0), (Double) arr.get(1));
    }

    private static ShapeType mapStringToShapeType(String str) throws ExtensionException {
        if (geoJsonStringTypesToShapeTypes.containsKey(str)) {
            return geoJsonStringTypesToShapeTypes.get(str);
        } else {
            throw new ExtensionException(str + "is not a supported geojson geometry type");
        }
    }

    private static PropertyType getPropertyTypeForValue(Object obj) throws ExtensionException {
        if(obj instanceof String){
            return PropertyType.STRING;
        } else if (obj instanceof Double){
            return PropertyType.NUMBER;
        } else {
            throw new ExtensionException(obj + " is not a valid property type");
        }
    }
}
