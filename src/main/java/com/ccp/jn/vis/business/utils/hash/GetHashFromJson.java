package com.ccp.jn.vis.business.utils.hash;

import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.hash.position.GetDisponibilityValuesFromPosition;
import com.ccp.jn.vis.business.utils.hash.position.GetMoneyValuesFromPosition;
import com.ccp.jn.vis.business.utils.hash.position.GetPcdValuesFromPosition;
import com.ccp.jn.vis.business.utils.hash.position.GetSenioritiesValuesFromPosition;
import com.ccp.jn.vis.business.utils.hash.resume.GetDisponibilityValuesFromResume;
import com.ccp.jn.vis.business.utils.hash.resume.GetMoneyValuesFromResume;
import com.ccp.jn.vis.business.utils.hash.resume.GetPcdValuesFromResume;
import com.ccp.jn.vis.business.utils.hash.resume.GetSenioritiesValuesFromResume;

public class GetHashFromJson {

	public final Function<CcpJsonRepresentation, List<Boolean>> getPcdValuesFromJson;
	public final Function<CcpJsonRepresentation, List<String>> getSenioritiesValuesFromJson;
	public final Function<CcpJsonRepresentation, List<Integer>> getDisponibilityValuesFromJson;
	public final Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getPjValuesFromJson;
	public final Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getBtcValuesFromJson;
	public final Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getCltValuesFromJson;
	
	private GetHashFromJson(Function<CcpJsonRepresentation, List<Boolean>> getPcdValuesFromJson,
			Function<CcpJsonRepresentation, List<String>> getSenioritiesValuesFromJson,
			Function<CcpJsonRepresentation, List<Integer>> getDisponibilityValuesFromJson,
			Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getPjValuesFromJson,
			Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getBtcValuesFromJson,
			Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getCltValuesFromJson) {
	
		this.getDisponibilityValuesFromJson = getDisponibilityValuesFromJson;
		this.getSenioritiesValuesFromJson = getSenioritiesValuesFromJson;
		this.getPcdValuesFromJson = getPcdValuesFromJson;
		this.getBtcValuesFromJson = getBtcValuesFromJson;
		this.getCltValuesFromJson = getCltValuesFromJson;
		this.getPjValuesFromJson = getPjValuesFromJson;
	}

	public static GetHashFromJson getHashFromJson(CcpJsonRepresentation json) {
		
		boolean isResumeJson = json.containsAllKeys("experience");
		
		if(isResumeJson) {
			GetHashFromJson hashFromResume = getHashFromResume();
			return hashFromResume;
		}
		GetHashFromJson hashFromPosition = getHashFromPosition();
		return hashFromPosition;
	}

	private static GetHashFromJson getHashFromResume() {
		Function<CcpJsonRepresentation, List<Boolean>> getPcdValuesFromJson = new GetPcdValuesFromResume();
		Function<CcpJsonRepresentation, List<String>> getSenioritiesValuesFromJson = new GetSenioritiesValuesFromResume();
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getPjValuesFromJson = new GetMoneyValuesFromResume("pj");
		Function<CcpJsonRepresentation, List<Integer>> getDisponibilityValuesFromJson = new GetDisponibilityValuesFromResume();
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getBtcValuesFromJson = new GetMoneyValuesFromResume("btc");
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getCltValuesFromJson = new GetMoneyValuesFromResume("clt");
		GetHashFromJson getHashFromJson = new GetHashFromJson(getPcdValuesFromJson, getSenioritiesValuesFromJson, getDisponibilityValuesFromJson, getPjValuesFromJson, getBtcValuesFromJson, getCltValuesFromJson);
		return getHashFromJson;
	}

	private static GetHashFromJson getHashFromPosition() {
		Function<CcpJsonRepresentation, List<Boolean>> getPcdValuesFromJson = new GetPcdValuesFromPosition();
		Function<CcpJsonRepresentation, List<String>> getSenioritiesValuesFromJson = new GetSenioritiesValuesFromPosition();
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getPjValuesFromJson = new GetMoneyValuesFromPosition("pj");
		Function<CcpJsonRepresentation, List<Integer>> getDisponibilityValuesFromJson = new GetDisponibilityValuesFromPosition();
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getBtcValuesFromJson = new GetMoneyValuesFromPosition("btc");
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getCltValuesFromJson = new GetMoneyValuesFromPosition("clt");
		GetHashFromJson getHashFromJson = new GetHashFromJson(getPcdValuesFromJson, getSenioritiesValuesFromJson, getDisponibilityValuesFromJson, getPjValuesFromJson, getBtcValuesFromJson, getCltValuesFromJson);
		return getHashFromJson;
		
	}

}
