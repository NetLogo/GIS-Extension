package org.myworldgis.netlogo;

import java.io.IOException;
import java.io.StringWriter;

import org.myworldgis.io.asciigrid.AsciiGridFileWriter;
import org.myworldgis.io.geojson.GeoJsonWriter;
import org.myworldgis.io.shapefile.ESRIShapefileWriter;
import org.nlogo.api.Argument;
import org.nlogo.api.Context;
import org.nlogo.api.ExtensionException;
import org.nlogo.api.LogoException;
import org.nlogo.core.Syntax;
import org.nlogo.core.SyntaxJ;


/**
 *
 */
public final class StoreDatasetToString extends GISExtension.Reporter {

    //--------------------------------------------------------------------------
    // GISExtension.Reporter implementation
    //--------------------------------------------------------------------------

    /** */
    public String getAgentClassString() {
        return "OTPL";
    }

    /** */
    public Syntax getSyntax() {
        return SyntaxJ.reporterSyntax(new int[] { Syntax.WildcardType(),
                                                  Syntax.StringType() },
                                      Syntax.StringType());
    }

    /** */
    public Object reportInternal (Argument[] args, Context context)
            throws ExtensionException, IOException, LogoException {
        Object arg0 = args[0].get();
        String format = LoadDatasetFromString.normalizeFormat(args[1].getString());
        if (format.equals(GeoJsonWriter.GEOJSON_EXTENSION) ||
            format.equals(GeoJsonWriter.JSON_EXTENSION)) {
            if (!(arg0 instanceof VectorDataset)) {
                throw new ExtensionException("expected a VectorDataset to store as " + format);
            }
            return new GeoJsonWriter((VectorDataset)arg0).getJsonString();
        } else if (format.equals(AsciiGridFileWriter.ASCII_GRID_FILE_EXTENSION_1) ||
                   format.equals(AsciiGridFileWriter.ASCII_GRID_FILE_EXTENSION_2)) {
            if (!(arg0 instanceof RasterDataset)) {
                throw new ExtensionException("expected a RasterDataset to store as " + format);
            }
            StringWriter out = new StringWriter();
            StoreDataset.storeAsciiGrid((RasterDataset)arg0, out);
            return out.toString();
        } else if (format.equals(ESRIShapefileWriter.SHAPEFILE_EXTENSION)) {
            throw new ExtensionException("storing a shapefile produces multiple strings; use gis:store-dataset-to-strings");
        } else {
            throw new ExtensionException("unsupported data format " + args[1].getString());
        }
    }
}
