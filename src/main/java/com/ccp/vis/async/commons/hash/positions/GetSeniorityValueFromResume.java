package com.ccp.vis.async.commons.hash.positions;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpTimeDecorator;

public class GetSeniorityValueFromResume implements Function<CcpJsonRepresentation, String> {

	public String apply(CcpJsonRepresentation json) {

		Integer experience = json.getAsIntegerNumber("experience");
		
		CcpTimeDecorator ctd = new CcpTimeDecorator();
		int currentYear = ctd.getYear();
		int experienceInYears = currentYear - experience;
		
		if(experienceInYears > 2) {
			return "JR";
		}
		
		if(experienceInYears > 5) {
			return "PL";
		}

		if(experienceInYears > 10) {
			return "SR";
		}
		return "ES";
	}

}
