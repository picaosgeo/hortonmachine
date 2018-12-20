package org.hortonmachine.gps.utils;

import java.util.List;

import net.sf.marineapi.nmea.util.Date;
import net.sf.marineapi.nmea.util.GpsFixQuality;
import net.sf.marineapi.nmea.util.GpsFixStatus;
import net.sf.marineapi.nmea.util.Position;
import net.sf.marineapi.nmea.util.SatelliteInfo;
import net.sf.marineapi.nmea.util.Time;
import net.sf.marineapi.provider.event.PositionEvent;
import net.sf.marineapi.provider.event.SatelliteInfoEvent;

public class NmeaUtils {

    public static String toString( SatelliteInfoEvent satInfoEvent ) {
        GpsFixStatus gpsFixStatus = satInfoEvent.getGpsFixStatus();
        double horizontalPrecision = satInfoEvent.getHorizontalPrecision();
        double verticalPrecision = satInfoEvent.getVerticalPrecision();
        double positionPrecision = satInfoEvent.getPositionPrecision();
        StringBuilder sb = new StringBuilder();
        sb.append("GPS fix status: ").append(gpsFixStatus).append("\n");
        sb.append("Horizontal precision").append(horizontalPrecision).append("\n");
        sb.append("Vertical precision").append(verticalPrecision).append("\n");
        sb.append("Position precision").append(positionPrecision).append("\n");

        sb.append("Satellites:").append(positionPrecision).append("\n");
        List<SatelliteInfo> satelliteInfo = satInfoEvent.getSatelliteInfo();
        for( SatelliteInfo sInfo : satelliteInfo ) {
            String id = sInfo.getId();
            int azimuth = sInfo.getAzimuth();
            int noise = sInfo.getNoise();
            int elevation = sInfo.getElevation();

            sb.append("--> id:").append(id);
            sb.append("azimuth:").append(azimuth);
            sb.append("noise:").append(noise);
            sb.append("elevation:").append(elevation);
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String toString( PositionEvent pEvent ) {
        GpsFixQuality fixQuality = pEvent.getFixQuality();
        Date date = pEvent.getDate();
        Time time = pEvent.getTime();
        Double kmHSpeed = pEvent.getSpeed();
        Double course = pEvent.getCourse();
        Position position = pEvent.getPosition();

        StringBuilder sb = new StringBuilder();
        sb.append("GPS fix quality: ").append(fixQuality).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Time: ").append(time).append("\n");
        sb.append("Speed [Km/h]: ").append(kmHSpeed).append("\n");
        sb.append("Course: ").append(course).append("\n");
        sb.append("Position: ").append(position).append("\n");

        return sb.toString();
    }

}
