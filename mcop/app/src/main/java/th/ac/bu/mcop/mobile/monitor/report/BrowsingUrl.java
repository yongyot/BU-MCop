package th.ac.bu.mcop.mobile.monitor.report;

import java.util.Date;

import th.ac.bu.mcop.mobile.monitor.core.Report;

public class BrowsingUrl implements Report {
	public Date date;
	public String url;

	public BrowsingUrl(Date date, String url) {
		this.date = date;
		this.url = url;
	}
}
