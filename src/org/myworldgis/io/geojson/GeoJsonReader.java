package org.myworldgis.io.geojson;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

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
        tmpMap.put("MultiLineString", ShapeType.LINE);
        tmpMap.put("Polygon", ShapeType.POLYGON);
        tmpMap.put("MultiPolygon", ShapeType.POLYGON);
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
        } else if (topLevelType.equals("Point")           ||
                   topLevelType.equals("MultiPoint")      ||
                   topLevelType.equals("LineString")      ||
                   topLevelType.equals("MultiLineString") ||
                   topLevelType.equals("Polygon")         ||
                   topLevelType.equals("MultiPolygon")) {
            parseSingleGeometryDataset();
        } else {
            throw new ExtensionException(topLevelType + " is not a supported GeoJSON type");
        }
    }

    public void extractShapeInfo(JSONObject geometry) throws ExtensionException {
        String geometryTypeString = geometry.get("type").toString();
        this.shapeType = mapStringToShapeType(geometryTypeString);
        this.geojsonShapeType = geometryTypeString;
    }

    public void parseGeometryObject(JSONObject geometry, int featureIndex) throws ExtensionException {
        if (!geometry.get("type").toString().equals(this.geojsonShapeType)) { 
            throw new ExtensionException("Only homogenous FeatureCollections are supported");
        }

        JSONArray coordinates = (JSONArray) geometry.get("coordinates");
        this.geometries[featureIndex] = parseCoordinates(coordinates, this.geojsonShapeType);
    }

    public void extractPropertyInfoFromFeature(JSONObject firstFeature) throws ExtensionException {
        JSONObject firstFeatureProperties = ((JSONObject) firstFeature.get("properties"));
        this.numProperties = firstFeatureProperties.size();

        int propertyIndex = 0;
        this.propertiesToIndices = new HashMap<String, Integer>();
        this.propertyNames = new String[numProperties];
        this.propertyTypes = new PropertyType[numProperties];
        for (Object entryObj : firstFeatureProperties.entrySet()) {
            @SuppressWarnings("unchecked")
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) entryObj;

            this.propertiesToIndices.put(entry.getKey().toString(), propertyIndex);
            this.propertyNames[propertyIndex] = entry.getKey().toString(); 
            this.propertyTypes[propertyIndex] = getPropertyTypeForValue(entry.getValue());

            propertyIndex ++;
        }
    }

    public void parseFeatureObject(JSONObject feature, int featureIndex) throws ExtensionException {
            JSONObject geometry = (JSONObject) feature.get("geometry");
            parseGeometryObject(geometry, featureIndex);

            Object[] thesePropertyValues = new Object[this.numProperties];
            JSONObject propertiesObject = (JSONObject) feature.get("properties");
            for(int i = 0; i < this.numProperties; i++){
                if (!propertiesObject.containsKey(propertyNames[i])) {
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

        extractShapeInfo((JSONObject) firstFeature.get("geometry"));
        extractPropertyInfoFromFeature(firstFeature);

        int featureIndex = 0;
        for (Object featureObj : features) {
            JSONObject feature = (JSONObject) featureObj;
            parseFeatureObject(feature, featureIndex);
            featureIndex++;
        }
    }

    public void parseSingleFeatureDataset() throws ExtensionException {
        this.size = 1;
        this.geometries = new Geometry[size];
        this.propertyValues = new Object[size][];

        JSONObject firstFeature = geojson;

        extractShapeInfo((JSONObject) firstFeature.get("geometry"));
        extractPropertyInfoFromFeature(firstFeature);

        parseFeatureObject(firstFeature, 0);
    }

    public void parseSingleGeometryDataset() throws ExtensionException {
        this.size = 1;
        this.geometries = new Geometry[size];
        this.propertyValues = new Object[size][0];

        extractShapeInfo(geojson);
        this.propertyNames = new String[0];
        this.propertyTypes = new PropertyType[0];

        parseGeometryObject(geojson, 0);
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

    private Geometry parseCoordinates(JSONArray coordinates, String geojsonShapeType) throws ExtensionException{
        switch (geojsonShapeType) {
            case "Point":
                Geometry point = factory.createPoint(JSONPairToCoordinate(coordinates));
                return point;
            case "MultiPoint":
                Coordinate[] pointCoords = new Coordinate[coordinates.size()];
                for (int i = 0; i < coordinates.size(); i++) {
                    pointCoords[i] = JSONPairToCoordinate((JSONArray) coordinates.get(i));
                }
                Geometry points = factory.createMultiPoint(pointCoords);
                return points;
            case "LineString":
                return parseSingleLineString(coordinates);
            case "MultiLineString":
                LineString[] subLines = new LineString[coordinates.size()];
                for (int i = 0; i < coordinates.size(); i++) {
                    subLines[i] = parseSingleLineString((JSONArray) coordinates.get(i));
                }
                Geometry lines = factory.createMultiLineString(subLines);
                return lines;
            case "Polygon":
                return parseSingleComplexPolygon(coordinates);
            case "MultiPolygon":
                if(coordinates.size() < 1){throw new ExtensionException("One of the MultiPolygons has no polygons within it");}
                Polygon[] subPolygons = new Polygon[coordinates.size()];
                for (int i = 0; i < coordinates.size(); i++) {
                    subPolygons[i] = parseSingleComplexPolygon((JSONArray) coordinates.get(i));
                }
                Geometry polygons = factory.createMultiPolygon(subPolygons);
                return polygons;
            default:
                throw new ExtensionException(geojsonShapeType + " is not a supported geojson shape type");
        }
    }

    private LineString parseSingleLineString(JSONArray coordinates) throws ExtensionException {
        Coordinate[] linePoints = new Coordinate[coordinates.size()];
                for (int i = 0; i < coordinates.size(); i++) {
                    linePoints[i] = JSONPairToCoordinate((JSONArray) coordinates.get(i));
                }
        LineString line = factory.createLineString(linePoints);
        return line;
    }

    private Polygon parseSingleComplexPolygon(JSONArray coordinates) throws ExtensionException {
        if (coordinates.size() < 1) { throw new ExtensionException("Empty polygon in geojson file");}
        int numRings = coordinates.size();
        int numHoles = numRings - 1;

        JSONArray shellArr = (JSONArray) coordinates.get(0);
        Coordinate[] shellCoords = new Coordinate[shellArr.size()];
        for(int j = 0; j < shellArr.size(); j++){
            shellCoords[j] = JSONPairToCoordinate((JSONArray)shellArr.get(j));
        }
        LinearRing shell = factory.createLinearRing(shellCoords);

        Coordinate[][] holeCoords = new Coordinate[numHoles][];
        LinearRing[] holeRings = new LinearRing[numHoles];
        for (int i = 0; i < numHoles; i++) {
            JSONArray thisRing = (JSONArray) coordinates.get(i + 1);
            holeCoords[i] = new Coordinate[thisRing.size()];
            for (int j = 0; j < thisRing.size(); j++) {
                holeCoords[i][j] = JSONPairToCoordinate((JSONArray) thisRing.get(j));
            }
            holeRings[i] = factory.createLinearRing(holeCoords[i]);
        }

        Polygon polygon = factory.createPolygon(shell, holeRings);
        return polygon;
    }

    private static Coordinate JSONPairToCoordinate(JSONArray arr){
        return new Coordinate(((Number) arr.get(0)).doubleValue(), ((Number) arr.get(1)).doubleValue());
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
