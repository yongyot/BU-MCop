package th.ac.bu.mcop.mobile.monitor.report;

import java.util.Date;

import th.ac.bu.mcop.mobile.monitor.core.Report;

public class SMS implements Report {
	public String from;
	public String to;
	public Date date;
	public String message;

	public SMS(String from, String to, String message, Date date) {
		this.from = from;
		this.to = to;
		this.message = message;
		this.date = date;
	}
}
