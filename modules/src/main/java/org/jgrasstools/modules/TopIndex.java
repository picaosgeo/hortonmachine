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
package org.jgrasstools.modules;

import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_AUTHORCONTACTS;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_AUTHORNAMES;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_DESCRIPTION;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_KEYWORDS;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_LABEL;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_LICENSE;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_NAME;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_STATUS;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_inSlope_DESCRIPTION;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_inTca_DESCRIPTION;
import static org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex.OMSTOPINDEX_outTopindex_DESCRIPTION;

import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.jgrasstools.hortonmachine.modules.basin.topindex.OmsTopIndex;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Name;
import oms3.annotations.Status;
import oms3.annotations.UI;

@Description(OMSTOPINDEX_DESCRIPTION)
@Author(name = OMSTOPINDEX_AUTHORNAMES, contact = OMSTOPINDEX_AUTHORCONTACTS)
@Keywords(OMSTOPINDEX_KEYWORDS)
@Label(OMSTOPINDEX_LABEL)
@Name("_" + OMSTOPINDEX_NAME)
@Status(OMSTOPINDEX_STATUS)
@License(OMSTOPINDEX_LICENSE)
public class TopIndex extends JGTModel {

    @Description(OMSTOPINDEX_inTca_DESCRIPTION)
    @UI(JGTConstants.FILEIN_UI_HINT)
    @In
    public String inTca = null;

    @Description(OMSTOPINDEX_inSlope_DESCRIPTION)
    @UI(JGTConstants.FILEIN_UI_HINT)
    @In
    public String inSlope = null;

    @Description(OMSTOPINDEX_outTopindex_DESCRIPTION)
    @UI(JGTConstants.FILEOUT_UI_HINT)
    @In
    public String outTopindex = null;

    @Execute
    public void process() throws Exception {
        OmsTopIndex topindex = new OmsTopIndex();
        topindex.inTca = getRaster(inTca);
        topindex.inSlope = getRaster(inSlope);
        topindex.pm = pm;
        topindex.doProcess = doProcess;
        topindex.doReset = doReset;
        topindex.process();
        dumpRaster(topindex.outTopindex, outTopindex);
    }
}
