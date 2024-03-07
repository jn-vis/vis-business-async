package com.ccp.jn.vis.business.utils;

public enum PositionSendFrequency {
	minute(1d/60d),
	hourly(1),
	daily(24),
	weekly(168),
	montly(720)
	;
	public final double hours;

	private PositionSendFrequency(double hours) {
		this.hours = hours;
	}
	
}
