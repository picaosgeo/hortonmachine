/*
 * This file is part of JGrasstools (http://www.jgrasstools.org)
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * JGrasstools is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.jgrasstools.dbs.compat;

import java.util.Arrays;
import java.util.List;

/**
 * Spatialite table namse and groups.
 * 
 * @author Andrea Antonello (www.hydrologis.com)
 *
 */
public interface ISpatialTableNames {
    public static final String USERDATA = "User Data";
    public static final String SPATIALINDEX = "Spatial Index";
    public static final String STYLE = "Styling (SLD/SE)";
    public static final String METADATA = "Metadata";
    public static final String INTERNALDATA = "Internal Data";

    public static final List<String> ALL_TYPES_LIST = Arrays.asList(//
            USERDATA, SPATIALINDEX, STYLE, METADATA, INTERNALDATA);

}
