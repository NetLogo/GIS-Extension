package org.myworldgis.io;

import com.vividsolutions.jts.geom.Point;

public class PointZWrapper extends Point {
    public Point _point;
    public double _z;

    public PointZWrapper(Point point, double z) {
        super(point.getCoordinateSequence(), point.getFactory());
        _point = point;
        _z = z;
    }

    public Point getPoint() { return _point; }

    public double getZ() { return _z; }
}
