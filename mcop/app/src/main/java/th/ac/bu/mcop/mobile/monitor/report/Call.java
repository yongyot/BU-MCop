package th.ac.bu.mcop.mobile.monitor.report;

import java.util.Date;

import th.ac.bu.mcop.mobile.monitor.core.Report;

public class Call implements Report {
	public static final int CALL_TYPE_INCOMING = 0;
	public static final int CALL_TYPE_OUTGOING = 1;
	public static final int CALL_TYPE_MISSEDCALL = 2;
	public static final int CALL_TYPE_UNKNOWN_DIRECTION = 3;
	
	public String from;
	public String to;
	public long duration;
	public int type;
	public Date date;

	public Call(String from, String to, long duration, int type, Date date) {
		this.from = from;
		this.to = to;
		this.duration = duration;
		this.date = date;
		this.type = type;
	}
}
