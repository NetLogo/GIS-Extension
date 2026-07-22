package org.myworldgis.netlogo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Base64;
import java.util.Locale;

import org.myworldgis.io.asciigrid.AsciiGridFileReader;
import org.myworldgis.io.geojson.GeoJsonReader;
import org.myworldgis.io.shapefile.ESRIShapefileReader;
import org.myworldgis.projection.Projection;
import org.myworldgis.projection.ProjectionFormat;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.core.LogoList;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;


/**
 *
 */
public final class LoadDatasetFromString extends GISExtension.Reporter {

    //--------------------------------------------------------------------------
    // Class methods
    //--------------------------------------------------------------------------

    /** */
    private static String normalizeFormat (String format) {
        String result = format.trim().toLowerCase(Locale.ENGLISH);
        if (result.startsWith(".")) {
            result = result.substring(1);
        }
        return result;
    }

    // Deliberately not a StringReader: StringReader.ready() returns true even
    // at EOF, which can hang parsers that poll ready() to detect the end of
    // input. A stream-backed reader has file-like ready() semantics.
    private static Reader readerForString (String data) {
        return new InputStreamReader(new ByteArrayInputStream(data.getBytes(StandardCharsets.UTF_8)),
                                     StandardCharsets.UTF_8);
    }

    /** */
    private static byte[] decodeBase64 (String content, String extension) throws ExtensionException {
        try {
            // the MIME decoder tolerates the line breaks often found in base64 text
            return Base64.getMimeDecoder().decode(content);
        } catch (IllegalArgumentException e) {
            throw new ExtensionException("invalid base64 content for \"" + extension + "\" entry: " + e.getMessage());
        }
    }

    /** */
    private static Dataset loadShapefileFromParts (LogoList parts, Projection dstProj)
            throws ExtensionException, IOException, ParseException {
        byte[] shpBytes = null;
        byte[] dbfBytes = null;
        String prjText = null;
        for (int i = 0; i < parts.size(); i += 1) {
            Object entryObj = parts.get(i);
            if (!(entryObj instanceof LogoList)) {
                throw new ExtensionException("expected a two-element [extension content] list of strings, but got " + entryObj);
            }
            LogoList entry = (LogoList) entryObj;
            if ((entry.size() != 2) ||
                !(entry.get(0) instanceof String) ||
                !(entry.get(1) instanceof String)) {
                throw new ExtensionException("expected a two-element [extension content] list of strings, but got " + entry);
            }
            String extension = normalizeFormat((String) entry.get(0));
            String content = (String) entry.get(1);
            if (extension.equals(ESRIShapefileReader.SHAPEFILE_EXTENSION)) {
                shpBytes = decodeBase64(content, extension);
            } else if (extension.equals("dbf")) {
                dbfBytes = decodeBase64(content, extension);
            } else if (extension.equals("prj")) {
                prjText = content;
            }
            // any other entries (like "shx") are not needed to read a shapefile
        }
        if (shpBytes == null) {
            throw new ExtensionException("missing \"shp\" entry in shapefile data list");
        }
        if (dbfBytes == null) {
            throw new ExtensionException("missing \"dbf\" entry in shapefile data list");
        }
        Projection datasetProjection = null;
        if (prjText != null) {
            datasetProjection = ProjectionFormat.getInstance().parseProjection(prjText);
        }
        return LoadDataset.loadShapefile(new ByteArrayInputStream(shpBytes),
                                         new ByteArrayInputStream(dbfBytes),
                                         "shapefile data string",
                                         datasetProjection,
                                         dstProj);
    }

    //--------------------------------------------------------------------------
    // GISExtension.Reporter implementation
    //--------------------------------------------------------------------------

    /** */
    public String getAgentClassString() {
        return "OTPL";
    }

    /** */
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(new int[] { Syntax.StringType(),
                                                  Syntax.StringType() | Syntax.ListType() },
                                      Syntax.WildcardType());
    }

    /** */
    public Object reportInternal (Argument[] args, Context context)
            throws ExtensionException, IOException, LogoException, ParseException {
        String format = normalizeFormat(args[0].getString());
        Projection netLogoProjection = GISExtension.getState().getProjection();
        Dataset result;
        if (format.equals(ESRIShapefileReader.SHAPEFILE_EXTENSION)) {
            result = loadShapefileFromParts(args[1].getList(), netLogoProjection);
        } else if (format.equals(GeoJsonReader.GEOJSON_EXTENSION) ||
                   format.equals(GeoJsonReader.JSON_EXTENSION)) {
            result = LoadDataset.loadGeoJson(readerForString(args[1].getString()),
                                             "GeoJSON data string",
                                             netLogoProjection);
        } else if (format.equals(AsciiGridFileReader.ASCII_GRID_FILE_EXTENSION_1) ||
                   format.equals(AsciiGridFileReader.ASCII_GRID_FILE_EXTENSION_2)) {
            // there is no companion .prj equivalent for a data string, so as with
            // a missing .prj file the data is assumed to be in the current projection
            result = LoadDataset.loadAsciiGrid(new BufferedReader(readerForString(args[1].getString())),
                                               null,
                                               netLogoProjection);
        } else {
            throw new ExtensionException("unsupported data format " + args[0].getString());
        }
        LoadDataset.setDefaultTransformationIfUnset(result, context);
        return result;
    }
}
