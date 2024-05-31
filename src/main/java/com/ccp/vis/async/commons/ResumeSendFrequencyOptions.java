package com.ccp.vis.async.commons;

public enum ResumeSendFrequencyOptions {
	minute(1d/60d),
	hourly(1),
	daily(24),
	weekly(168),
	yearly(8766),
	montly(730.5),
	;
	public final double hours;

	private ResumeSendFrequencyOptions(double hours) {
		this.hours = hours;
	}
	
}
