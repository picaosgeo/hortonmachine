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
package org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige;

import static org.jgrasstools.gears.libs.modules.JGTConstants.isNovalue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import oms3.annotations.Author;
import oms3.annotations.Description;
import oms3.annotations.Execute;
import oms3.annotations.In;
import oms3.annotations.Keywords;
import oms3.annotations.Label;
import oms3.annotations.License;
import oms3.annotations.Out;
import oms3.annotations.Status;
import oms3.annotations.Unit;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.feature.FeatureIterator;
import org.jgrasstools.gears.libs.exceptions.ModelsIllegalargumentException;
import org.jgrasstools.gears.libs.modules.JGTConstants;
import org.jgrasstools.gears.libs.modules.JGTModel;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.Dams;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.HillSlopeDuffy;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.Hydrometers;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.IDischargeContributor;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.IHillSlope;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.Offtakes;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.PfafstetterNumber;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.core.Tributaries;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.duffy.DuffyAdigeEngine;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.duffy.DuffyInputs;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.hymod.HymodAdigeEngine;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.hymod.HymodInputs;
import org.jgrasstools.hortonmachine.modules.hydrogeomorphology.adige.utils.AdigeUtilities;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
@Description("The OmsAdige model.")
@Author(name = "Silvia Franceschi, Andrea Antonello", contact = "www.hydrologis.com")
@Keywords("Hydrology")
@Label(JGTConstants.HYDROGEOMORPHOLOGY)
@Status(Status.EXPERIMENTAL)
@License("http://www.gnu.org/licenses/gpl-3.0.html")
public class OmsAdige extends JGTModel {

    @Description("The hillslope data.")
    @In
    public SimpleFeatureCollection inHillslope;

    @Description("The field name of the netnum attribute in the hillslope data.")
    @In
    public String fNetnum = null;

    @Description("The a field name of the baricenter elevation attribute in the hillslope data.")
    @In
    public String fBaricenter = null;

    @Description("A constant value of rain intensity.")
    @Unit("mm/h")
    @In
    @Out
    public double pRainintensity = -1.0;

    @Description("The duration of the constant rain in minutes.")
    @Unit("min")
    @In
    @Out
    public int pRainduration = -1;

    @Description("The rainfall data.")
    @In
    public HashMap<Integer, double[]> inRain;

    @Description("The hydrometers monitoring points.")
    @In
    public SimpleFeatureCollection inHydrometers;

    @Description("The hydrometers data.")
    @In
    public HashMap<Integer, double[]> inHydrometerdata;

    @Description("The dams monitoring points.")
    @In
    public SimpleFeatureCollection inDams;

    @Description("The dams data.")
    @In
    public HashMap<Integer, double[]> inDamsdata;

    @Description("The tributary monitoring points.")
    @In
    public SimpleFeatureCollection inTributary;

    @Description("The tributary data.")
    @In
    public HashMap<Integer, double[]> inTributarydata;

    @Description("The offtakes monitoring points.")
    @In
    public SimpleFeatureCollection inOfftakes;

    @Description("The offtakes data.")
    @In
    public HashMap<Integer, double[]> inOfftakesdata;

    @Description("Comma separated list of pfafstetter ids, in which to generate the output")
    @In
    public String pPfafids = null;

    @Description("The field name of the monitoring point's id attribute in the monitoring points data.")
    @In
    public String fMonpointid = null;

    @Description("The network data.")
    @In
    public SimpleFeatureCollection inNetwork;

    @Description("The field name of the pfafstetter enumeration attribute in the network data.")
    @In
    public String fPfaff = null;

    @Description("The field name of the elevation of the starting point of a link in the network data.")
    @In
    public String fNetelevstart = null;

    @Description("The field name of the elevation of the end point of a link in the network data.")
    @In
    public String fNetelevend = null;

    @Description("The evapotranspiration data.")
    @In
    public HashMap<Integer, double[]> inEtp;

    @Description("Switch to activate additional logging to file.")
    @In
    public boolean doLog = false;

    @Description("The timestep in minutes.")
    @In
    public int tTimestep = 0;

    @Description("The start date.")
    @In
    public String tStart = null;

    @Description("The end date.")
    @In
    public String tEnd = null;

    @Description("The inputs in the case of Duffy elaboration.")
    @In
    public DuffyInputs inDuffyInput = null;

    @Description("The inputs in the case of HyMod elaboration.")
    @In
    public HymodInputs inHymodInput = null;

    @Description("The superficial discharge for every basin id.")
    @Out
    public HashMap<Integer, double[]> outDischarge;

    @Description("The sub-superficial discharge for every basin id.")
    @Out
    public HashMap<Integer, double[]> outSubdischarge;

    // public String startDateArg = null;
    // public String endDateArg = null;
    // public double deltaTArg = null;

    /*
     * ATTRIBUTES FIELDS
     */
    // private Date startDate;
    // private Date endDate;

    /** the running rain array */
    private double[] rainArray = null;
    private double[] etpArray;

    /** the running discharge array, which at the begin holds the initial conditions */
    private double[] initialConditions = null;

    private IAdigeEngine adigeEngine = null;
    private List<PfafstetterNumber> netPfaffsList;

    // hydrometers
    private IDischargeContributor hydrometersHandler;
    private HashMap<String, Integer> hydrometer_pfaff2idMap;

    // dams
    private IDischargeContributor damsHandler;
    private HashMap<String, Integer> dams_pfaff2idMap;

    // tributaries
    private IDischargeContributor tributaryHandler;
    private HashMap<String, Integer> tributary_pfaff2idMap;

    // offtakes
    private IDischargeContributor offtakesHandler;
    private HashMap<String, Integer> offtakes_pfaff2idMap;

    private HashMap<Integer, Integer> basinid2Index;
    private HashMap<Integer, Integer> index2Basinid;

    private int hillsSlopeNum;
    private int outletHillslopeId = -1;
    private HashMap<String, Integer> pfaff2Index;
    private List<IHillSlope> orderedHillslopes;

    public static DateTimeFormatter adigeFormatter = JGTConstants.utcDateFormatterYYYYMMDDHHMM;

    private DateTime startTimestamp;
    private DateTime endTimestamp;
    private DateTime currentTimstamp;
    private DateTime rainEndTimestamp;

    private List<String> pfaffsList;

    @SuppressWarnings("nls")
    @Execute
    public void process() throws Exception {

        if (startTimestamp == null) {
            outDischarge = new HashMap<Integer, double[]>();
            outSubdischarge = new HashMap<Integer, double[]>();

            startTimestamp = adigeFormatter.parseDateTime(tStart);
            endTimestamp = adigeFormatter.parseDateTime(tEnd);

            currentTimstamp = startTimestamp;

            if (pRainintensity != -1) {
                if (pRainduration != -1) {
                    rainEndTimestamp = startTimestamp.plusMinutes(pRainduration);
                } else {
                    throw new ModelsIllegalargumentException(
                            "In the case of usage of a constant rainintensity it is necessary to define also its duration.\nCheck your arguments, probably the --rainduration flag is missing.",
                            this);
                }
            }

            if (fNetnum == null || fNetnum.length() < 1) {
                throw new ModelsIllegalargumentException("Missing net num attribute name.", this.getClass().getSimpleName());
            }
            if (fPfaff == null || fPfaff.length() < 1) {
                throw new ModelsIllegalargumentException("Missing pfafstetter attribute name.", this);
            }
            if (fMonpointid == null || fMonpointid.length() < 1) {
                throw new ModelsIllegalargumentException("Missing monitoring point id attribute name.", this.getClass()
                        .getSimpleName());
            }
            if (fBaricenter == null || fBaricenter.length() < 1) {
                throw new ModelsIllegalargumentException("Missing basin centroid attribute name.", this);
            }
            if (fNetelevstart == null || fNetelevstart.length() < 1) {
                throw new ModelsIllegalargumentException("Missing start net elevation attribute name.", this.getClass()
                        .getSimpleName());
            }
            if (fNetelevend == null || fNetelevend.length() < 1) {
                throw new ModelsIllegalargumentException("Missing start net elevation attribute name.", this.getClass()
                        .getSimpleName());
            }

            // hydrometers
            if (inHydrometers != null && inHydrometerdata != null) {
                if (hydrometersHandler == null) {
                    pm.message("Reading hydrometers geometries and mapping them to the network...");
                    hydrometer_pfaff2idMap = new HashMap<String, Integer>();
                    hydrometersHandler = new Hydrometers(hydrometer_pfaff2idMap);

                    FeatureIterator<SimpleFeature> hydrometersIterator = inHydrometers.features();
                    int pfaffIndex = -1;
                    int monIdIndex = -1;
                    while( hydrometersIterator.hasNext() ) {
                        SimpleFeature hydrometer = hydrometersIterator.next();
                        if (pfaffIndex == -1) {
                            SimpleFeatureType featureType = hydrometer.getFeatureType();
                            pfaffIndex = featureType.indexOf(fPfaff);
                            if (pfaffIndex == -1) {
                                throw new ModelsIllegalargumentException(
                                        "The hydrometer features are missing the pafaffstetter attribute field: " + fPfaff, this);
                            }
                            monIdIndex = featureType.indexOf(fMonpointid);
                            if (monIdIndex == -1) {
                                throw new ModelsIllegalargumentException(
                                        "The hydrometer features are missing the id attribute field: " + fMonpointid, this);
                            }
                        }

                        String pNumberStr = (String) hydrometer.getAttribute(pfaffIndex);
                        int id = ((Number) hydrometer.getAttribute(monIdIndex)).intValue();
                        hydrometer_pfaff2idMap.put(pNumberStr, id);
                    }
                }
            }

            // dams
            if (inDams != null && inDamsdata != null) {
                if (damsHandler == null) {
                    pm.message("Reading dams geometries and mapping them to the network...");
                    dams_pfaff2idMap = new HashMap<String, Integer>();
                    damsHandler = new Dams(dams_pfaff2idMap);

                    FeatureIterator<SimpleFeature> damsIterator = inDams.features();
                    int pfaffIndex = -1;
                    int monIdIndex = -1;
                    while( damsIterator.hasNext() ) {
                        SimpleFeature dam = damsIterator.next();
                        if (pfaffIndex == -1) {
                            SimpleFeatureType featureType = dam.getFeatureType();
                            pfaffIndex = featureType.indexOf(fPfaff);
                            if (pfaffIndex == -1) {
                                throw new ModelsIllegalargumentException(
                                        "The dams features are missing the pfaffstetter attribute field: " + fPfaff, this);
                            }
                            monIdIndex = featureType.indexOf(fMonpointid);
                            if (monIdIndex == -1) {
                                throw new ModelsIllegalargumentException("The dams features are missing the id attribute field: "
                                        + fMonpointid, this);
                            }
                        }

                        String pNumberStr = (String) dam.getAttribute(pfaffIndex);
                        int id = ((Number) dam.getAttribute(monIdIndex)).intValue();
                        dams_pfaff2idMap.put(pNumberStr, id);
                    }
                }
            }

            // tributary
            if (inTributary != null && inTributarydata != null) {
                if (tributaryHandler == null) {
                    pm.message("Reading tributary geometries and mapping them to the network...");
                    tributary_pfaff2idMap = new HashMap<String, Integer>();
                    tributaryHandler = new Tributaries(tributary_pfaff2idMap);

                    FeatureIterator<SimpleFeature> tributaryIterator = inTributary.features();
                    int pfaffIndex = -1;
                    int monIdIndex = -1;
                    while( tributaryIterator.hasNext() ) {
                        SimpleFeature tributary = tributaryIterator.next();
                        if (pfaffIndex == -1) {
                            SimpleFeatureType featureType = tributary.getFeatureType();
                            pfaffIndex = featureType.indexOf(fPfaff);
                            if (pfaffIndex == -1) {
                                throw new ModelsIllegalargumentException(
                                        "The tributary features are missing the pfaffstetter attribute field: " + fPfaff, this);
                            }
                            monIdIndex = featureType.indexOf(fMonpointid);
                            if (monIdIndex == -1) {
                                throw new ModelsIllegalargumentException(
                                        "The tributary features are missing the id attribute field: " + fMonpointid, this);
                            }
                        }

                        String pNumberStr = (String) tributary.getAttribute(pfaffIndex);
                        int id = ((Number) tributary.getAttribute(monIdIndex)).intValue();
                        tributary_pfaff2idMap.put(pNumberStr, id);
                    }
                }
            }

            // offtakes
            if (inOfftakes != null && inOfftakesdata != null) {
                if (offtakesHandler == null) {
                    pm.message("Reading offtakes geometries and mapping them to the network...");
                    offtakes_pfaff2idMap = new HashMap<String, Integer>();
                    offtakesHandler = new Offtakes(offtakes_pfaff2idMap, pm);

                    FeatureIterator<SimpleFeature> offtakesIterator = inOfftakes.features();
                    int pfaffIndex = -1;
                    int monIdIndex = -1;
                    while( offtakesIterator.hasNext() ) {
                        SimpleFeature offtakes = offtakesIterator.next();
                        if (pfaffIndex == -1) {
                            SimpleFeatureType featureType = offtakes.getFeatureType();
                            pfaffIndex = featureType.indexOf(fPfaff);
                            if (pfaffIndex == -1) {
                                throw new ModelsIllegalargumentException(
                                        "The offtakes features are missing the pfaffstetter attribute field: " + fPfaff, this);
                            }
                            monIdIndex = featureType.indexOf(fMonpointid);
                            if (monIdIndex == -1) {
                                throw new ModelsIllegalargumentException(
                                        "The offtakes features are missing the id attribute field: " + fMonpointid, this);
                            }
                        }

                        String pNumberStr = (String) offtakes.getAttribute(pfaffIndex);
                        int id = ((Number) offtakes.getAttribute(monIdIndex)).intValue();
                        offtakes_pfaff2idMap.put(pNumberStr, id);
                    }
                }
            }

            hillsSlopeNum = inHillslope.size();

            // at the first round create the hillslopes and network hierarchy
            orderedHillslopes = AdigeUtilities.generateHillSlopes(inNetwork, inHillslope, fNetnum, fPfaff, fNetelevstart,
                    fNetelevend, fBaricenter, pm);
            if (inDuffyInput != null) {
                List<IHillSlope> duffyHillslopes = new ArrayList<IHillSlope>();
                for( IHillSlope hillSlope : orderedHillslopes ) {
                    IHillSlope newHS = new HillSlopeDuffy(hillSlope, inDuffyInput);
                    duffyHillslopes.add(newHS);
                }
                orderedHillslopes = duffyHillslopes;
            }

            IHillSlope outletHillSlope = orderedHillslopes.get(0);
            outletHillslopeId = outletHillSlope.getHillslopeId();
            netPfaffsList = new ArrayList<PfafstetterNumber>();
            pfaff2Index = new HashMap<String, Integer>();
            basinid2Index = new HashMap<Integer, Integer>();
            index2Basinid = new HashMap<Integer, Integer>();
            pm.beginTask("Analaysing hillslopes and calculating distribution curves...", orderedHillslopes.size());
            for( int i = 0; i < orderedHillslopes.size(); i++ ) {
                IHillSlope hillSlope = orderedHillslopes.get(i);
                PfafstetterNumber pfafstetterNumber = hillSlope.getPfafstetterNumber();
                netPfaffsList.add(pfafstetterNumber);
                int hillslopeId = hillSlope.getHillslopeId();
                basinid2Index.put(hillslopeId, i);
                index2Basinid.put(i, hillslopeId);
                pfaff2Index.put(pfafstetterNumber.toString(), i);
                // the distributor
                pm.worked(1);
            }
            pm.done();

            if (pPfafids == null) {
                pPfafids = outletHillSlope.getPfafstetterNumber().toString();
            }
            if (pfaffsList == null) {
                String[] split = pPfafids.split(",");
                for( int i = 0; i < split.length; i++ ) {
                    split[i] = split[i].trim();
                }
                pfaffsList = Arrays.asList(split);
            }
            if (inDuffyInput != null) {
                initialConditions = new double[hillsSlopeNum * 4];
                adigeEngine = new DuffyAdigeEngine(orderedHillslopes, inDuffyInput, pm, doLog, initialConditions, basinid2Index,
                        index2Basinid, pfaffsList, pfaff2Index, outDischarge, outSubdischarge, startTimestamp, endTimestamp,
                        tTimestep);
            } else if (inHymodInput != null) {
                initialConditions =null;
                adigeEngine = new HymodAdigeEngine(inHymodInput, orderedHillslopes, index2Basinid, outDischarge, outSubdischarge,
                        pfaffsList, doLog, doLog, pm);
            } else {
                throw new ModelsIllegalargumentException("No parameters for any model were defined. Check your syntax.", this);
            }

            if (hydrometersHandler != null) {
                adigeEngine.addDischargeContributor(hydrometersHandler);
            }
            if (damsHandler != null) {
                adigeEngine.addDischargeContributor(damsHandler);
            }
            if (tributaryHandler != null) {
                adigeEngine.addDischargeContributor(tributaryHandler);
            }
            if (offtakesHandler != null) {
                adigeEngine.addDischargeContributor(offtakesHandler);
            }

        } else {
            currentTimstamp = currentTimstamp.plusMinutes(tTimestep);
        }

        if (inHydrometerdata != null) {
            hydrometersHandler.setCurrentData(inHydrometerdata);
        }
        if (inDamsdata != null) {
            damsHandler.setCurrentData(inDamsdata);
        }
        if (inOfftakesdata != null) {
            offtakesHandler.setCurrentData(inOfftakesdata);
        }
        if (inTributarydata != null) {
            tributaryHandler.setCurrentData(inTributarydata);
        }
        // deal with rain
        if (pRainintensity != -1) {
            /*
             * in the case of constant rain the array is build once and then used every time.
             * The only thing that changes, is that after the rainEndDate, the rain intensity is
             * set to 0.
             */
            rainArray = new double[netPfaffsList.size()];
            if (currentTimstamp.isBefore(rainEndTimestamp)) {
                Arrays.fill(rainArray, pRainintensity);
            } else {
                Arrays.fill(rainArray, 0);
            }

        } else {
            // read rainfall from input link scalar set and transform into a rainfall intensity
            // [mm/h]
            rainArray = new double[hillsSlopeNum];
            etpArray = new double[hillsSlopeNum];
            setDataArray(inRain, rainArray);

            if (inEtp != null) {
                setDataArray(inEtp, etpArray);
            }
        }

        // long runningDateInMinutes = currentTimstamp.getMillis() / 1000L / 60L;
        // double intervalStartTimeInMinutes = runningDateInMinutes;
        // double intervalEndTimeInMinutes = runningDateInMinutes + tTimestep;

        initialConditions = adigeEngine.solve(currentTimstamp, tTimestep, 1, initialConditions, rainArray, etpArray);

    }

    private void setDataArray( HashMap<Integer, double[]> dataMap, double[] endArray ) {
        Set<Entry<Integer, double[]>> entries = dataMap.entrySet();
        for( Entry<Integer, double[]> entry : entries ) {
            Integer id = entry.getKey();
            double[] value = entry.getValue();
            Integer index = basinid2Index.get(id);
            if (index == null) {
                continue;
            }
            if (isNovalue(value[0])) {
                value[0] = 0.0;
            }
            //endArray[index] = value[0] / (tTimestep / 60.0);
            endArray[index] = value[0];
        }
    }

}