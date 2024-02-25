package com.ccp.jn.vis.async.business.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class CalculateHashes {

	public static List<CcpJsonRepresentation> getHashes(CcpJsonRepresentation json) {

		List<Integer> ddds = json.getAsStringList("ddd").stream().map(x -> Integer.valueOf(x)).collect(Collectors.toList());
		
		List<String> resumeWords = json.getAsStringList("resumeWord", "mandatorySkills");
		
		List<Integer> disponibilities = json.get(new GetDisponibilityValues());

		List<CcpJsonRepresentation> moneyValues = getMoneyValues(json);
		
		List<String> seniorities = json.get(new GetSenioritiesValues());

		List<Boolean> pcds = json.get(new GetPcdValues());
		
		List<CcpJsonRepresentation> hashes = new ArrayList<>();

		String email = json.getAsString("email");
		
		for (Boolean pcd : pcds) {
			for (Integer disponibility : disponibilities) {
				for (String seniority : seniorities) {
					for (CcpJsonRepresentation moneyValue : moneyValues) {
						for (String resumeWord : resumeWords) {
							for (Integer ddd : ddds) {
								CcpJsonRepresentation hash = CcpConstants.EMPTY_JSON
										.put("disponibility", disponibility)
										.put("resumeWord", resumeWord)
										.put("seniority", seniority)
										.put("email", email)
										.putAll(moneyValue)
										.put("pcd", pcd)
										.put("ddd", ddd)
										;
								hashes.add(hash);
								
							}
						}
					}	
				}	
			}
		}
		

		return hashes;
	}
	
	private static List<CcpJsonRepresentation> getMoneyValues(CcpJsonRepresentation json){
		ArrayList<CcpJsonRepresentation> result = new ArrayList<>();
		
		result.addAll(new GetMoneyValues("btc").apply(json));
		result.addAll(new GetMoneyValues("clt").apply(json));
		result.addAll(new GetMoneyValues("pj").apply(json));
		
		return result;
	}

}
