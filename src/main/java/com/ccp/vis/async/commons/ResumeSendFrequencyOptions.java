package com.ccp.vis.async.commons;

public enum ResumeSendFrequencyOptions {
	minute(1d/60d),
	hourly(1),
	daily(24),
	weekly(168),
	montly(720)
	;
	public final double hours;

	private ResumeSendFrequencyOptions(double hours) {
		this.hours = hours;
	}
	
}
