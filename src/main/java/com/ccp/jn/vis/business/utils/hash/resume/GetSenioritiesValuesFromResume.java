package com.ccp.jn.vis.business.utils.hash.resume;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public class GetSenioritiesValuesFromResume implements Function<CcpJsonRepresentation, List<String>> {

	public List<String> apply(CcpJsonRepresentation json) {
		
		Integer experience = json.getAsIntegerNumber("experience");
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		int exp = currentYear - experience;

		if(exp <= 2) {
			return Arrays.asList("JR");
		}

		if(exp <= 5) {
			return Arrays.asList("PL");
		}

		if(exp <= 10) {
			return Arrays.asList("SR");
		}

		return Arrays.asList("ES");
	}

}
