package th.ac.bu.mcop.mobile.monitor.report;

import java.util.Collection;

import th.ac.bu.mcop.mobile.monitor.core.Report;

public class BrowsingHistory implements Report {
	public BrowsingUrl[] urls;

	public BrowsingHistory(Collection<BrowsingUrl> urls) {
		this.urls = urls.toArray(new BrowsingUrl[urls.size()]);
	}
}
