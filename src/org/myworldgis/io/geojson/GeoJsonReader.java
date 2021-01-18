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

    public GeoJsonReader(File file, GeometryFactory factory) throws IOException, ParseException {
        this.factory = factory;

        //TODO: Figure out why file.reader() wouldn't go into parser.parse. 
        Scanner s = new Scanner(file.getInputStream()).useDelimiter("\\Z");
        String out = s.hasNext() ? s.next() : "";

        JSONParser parser = new JSONParser();
        this.geojson = (JSONObject) parser.parse(out);
    }

    public VectorDataset getDataset() throws ExtensionException {

        String topLevelType = geojson.get("type").toString();

        if (topLevelType.equals("FeatureCollection")) {
            return parseFeatureCollection();
        } else if (topLevelType.equals("Feature")) {
            return parseSingleFeatureDataset();
        } else if (topLevelType.equals("Geometry")) {
            return parseSingleGeometryDataset();
        } else {
            throw new ExtensionException(topLevelType + " is not a valid GeoJSON type");
        }
   }

    public VectorDataset parseFeatureCollection() throws ExtensionException {
        JSONArray features = (JSONArray) geojson.get("features");

        JSONObject firstFeature = (JSONObject) features.get(0);
        String firstGeometryTypeString = ((JSONObject) firstFeature.get("geometry")).get("type").toString();
        ShapeType featureShapeType = mapStringToShapeType(firstGeometryTypeString);
        JSONObject firstFeatureProperties = ((JSONObject) firstFeature.get("properties"));
        int featureCount = firstFeatureProperties.size();

        int index = 0;
        Map<String, Integer> propertiesToIndices = new HashMap<String, Integer>();
        String[] propertyNames = new String[featureCount];
        PropertyType[] propertyTypes = new PropertyType[featureCount];
        for (Object entryObj : firstFeatureProperties.entrySet()) {
            Map.Entry<String, Object> entry = (Map.Entry<String, Object>) entryObj;

            propertiesToIndices.put(entry.getKey().toString(), index);
            propertyNames[index] = entry.getKey().toString(); 
            propertyTypes[index] = getPropertyTypeForValue(entry.getValue());

            System.out.println(index + " : " + propertyNames[index] + " : " + propertyTypes[index]);
            index ++;
        }
        
        VectorDataset out = new VectorDataset(featureShapeType, propertyNames, propertyTypes);

        for (Object featureObj : features) {
            JSONObject feature = (JSONObject) featureObj;
            JSONObject geometry = (JSONObject) feature.get("geometry");
            if (!geometry.get("type").toString().equals(firstGeometryTypeString)) { 
                throw new ExtensionException("Only homogenous FeatureCollections are supported");
            }

            //TODO: Generalize to other shape types
            JSONArray coordinates = (JSONArray) geometry.get("coordinates");
            Geometry geom = factory.createPoint(new Coordinate((Double) coordinates.get(0),(Double) coordinates.get(1)));


            Object[] thesePropertyValues = new Object[featureCount];
            JSONObject propertiesObject = (JSONObject) feature.get("properties");
            for(int i = 0; i < featureCount; i++){
                if (!propertiesObject.containsKey(propertyNames[i])) {
                    continue;
                    // throw new ExtensionException(propertyNames[i] + " is missing from at least one feature");
                }
                Object thisPropertyValue = propertiesObject.get(propertyNames[i]);
                PropertyType thisPropertyType = getPropertyTypeForValue(thisPropertyValue);
                if (!thisPropertyType.equals(propertyTypes[i])) {
                    throw new ExtensionException("Not all " + propertyNames[i] + "'s are the same datatype");
                }
                System.out.println(i + " : " + thisPropertyValue + " : " + propertyTypes[i]);
                thesePropertyValues[i] = thisPropertyValue;
            }

            out.add(geom, thesePropertyValues); // TODO: Consider refactoring to behave like the other importers that don't actually create the VectorDataset themselves and turn add() back to private.

        }

        return out;
    }

    public VectorDataset parseSingleFeatureDataset() throws ExtensionException {
        return null;
    }

    public VectorDataset parseSingleGeometryDataset() throws ExtensionException {
        return null;
    }

    private static ShapeType mapStringToShapeType(String str) throws ExtensionException {
        if (geoJsonStringTypesToShapeTypes.containsKey(str)) {
            return geoJsonStringTypesToShapeTypes.get(str);
        } else {
            throw new ExtensionException(str + "is not a valid geojson geometry type");
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
