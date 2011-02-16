/*
 * JGrass - Free Open Source Java GIS http://www.jgrass.org 
 * (C) HydroloGIS - www.hydrologis.com 
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Library General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option) any
 * later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Library General Public License
 * along with this library; if not, write to the Free Foundation, Inc., 59
 * Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package org.jgrasstools.gears.modules.utils.coveragelist;

import static org.jgrasstools.gears.libs.modules.JGTConstants.doubleNovalue;

import java.util.ArrayList;
import java.util.List;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.UI;

import org.geotools.coverage.grid.GridCoverage2D;
import org.jgrasstools.gears.io.rasterreader.RasterReader;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;

@Description("A module that reads coverages")
@Author(name = "Andrea Antonello", contact = "www.hydrologis.com")
@Keywords("Iterator, Raster")
@Label(JGTConstants.LIST_READER)
@Status(Status.DRAFT)
@License("http://www.gnu.org/licenses/gpl-3.0.html")
public class CoverageLister extends JGTModel {

    @Description("The list of file from which to read coverages.")
    @UI(JGTConstants.FILESPATHLIST_UI_HINT)
    @In
    public List<String> inFiles;

    @Description("The file novalue.")
    @In
    public Double fileNovalue = -9999.0;

    @Description("The novalue wanted in the coverage.")
    @In
    public Double geodataNovalue = doubleNovalue;

    @Description("The optional requested boundary north coordinate.")
    @UI(JGTConstants.PROCESS_NORTH_UI_HINT)
    @In
    public Double pNorth = null;

    @Description("The optional requested boundary south coordinate.")
    @UI(JGTConstants.PROCESS_SOUTH_UI_HINT)
    @In
    public Double pSouth = null;

    @Description("The optional requested boundary west coordinate.")
    @UI(JGTConstants.PROCESS_WEST_UI_HINT)
    @In
    public Double pWest = null;

    @Description("The optional requested boundary east coordinate.")
    @UI(JGTConstants.PROCESS_EAST_UI_HINT)
    @In
    public Double pEast = null;

    @Description("The optional requested resolution in x.")
    @UI(JGTConstants.PROCESS_XRES_UI_HINT)
    @In
    public Double pXres = null;

    @Description("The optional requested resolution in y.")
    @UI(JGTConstants.PROCESS_YRES_UI_HINT)
    @In
    public Double pYres = null;

    @Description("The optional requested numer of rows.")
    @UI(JGTConstants.PROCESS_ROWS_UI_HINT)
    @In
    public Integer pRows = null;

    @Description("The optional requested numer of cols.")
    @UI(JGTConstants.PROCESS_COLS_UI_HINT)
    @In
    public Integer pCols = null;

    @Description("All coverages matching read from the input files.")
    @Out
    public List<GridCoverage2D> outGC = null;

    @Execute
    public void process() throws Exception {

        outGC = new ArrayList<GridCoverage2D>();

        for( String file : inFiles ) {
            RasterReader reader = new RasterReader();
            reader.file = file;
            reader.fileNovalue = fileNovalue;
            reader.geodataNovalue = geodataNovalue;
            reader.pNorth = pNorth;
            reader.pSouth = pSouth;
            reader.pWest = pWest;
            reader.pEast = pEast;
            reader.pXres = pXres;
            reader.pYres = pYres;
            reader.pRows = pRows;
            reader.pCols = pCols;
            reader.process();

            outGC.add(reader.geodata);
        }

    }

}
