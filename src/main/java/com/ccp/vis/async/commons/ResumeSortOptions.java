package com.ccp.vis.async.commons;

import com.ccp.decorators.CcpJsonRepresentation;

public enum ResumeSortOptions {

	disponibility("disponibility"),
	desiredSkill("desiredSkill"),
	money("clt", "pj", "btc"),
	experience("experience"),
	;
	final String[] fieldsToSort;
	
	
	private ResumeSortOptions(String... fieldsToSort) {
		this.fieldsToSort = fieldsToSort;
	}

	public int compare(CcpJsonRepresentation o1, CcpJsonRepresentation o2) {
		int compareTo = this.compareTo(o1, o2, this.fieldsToSort);
		return compareTo;
	}
	
	private int compareTo(CcpJsonRepresentation o1, CcpJsonRepresentation o2, String... keys) {
		
		for (String key : keys) {
			
			if(o1.containsAllFields(key) == false) {
				continue;
			}
			
			if(o2.containsAllFields(key) == false) {
				continue;
			}
			
			Double value1 = o1.getAsDoubleNumber(key);
			Double value2 = o2.getAsDoubleNumber(key);
			
			int compareTo = value1.compareTo(value2);
			
			boolean areEquals = compareTo == 0;
			
			if(areEquals) {
				continue;
			}
			
			return compareTo;
		}
		return 0;
	}
	
}
