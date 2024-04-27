package com.ccp.vis.async.commons.hash;

import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.hash.positions.GetDisponibilityValuesFromResume;
import com.ccp.vis.async.commons.hash.positions.GetMoneyValuesFromResume;
import com.ccp.vis.async.commons.hash.positions.GetPcdValuesFromResume;
import com.ccp.vis.async.commons.hash.resumes.GetDisponibilityValuesFromPosition;
import com.ccp.vis.async.commons.hash.resumes.GetMoneyValuesFromPosition;
import com.ccp.vis.async.commons.hash.resumes.GetPcdValuesFromPosition;
import com.ccp.vis.async.commons.hash.resumes.GetSeniorityValueFromPosition;

public class GetValuesFromJson {

	public final Function<CcpJsonRepresentation, String> getSeniorityValueFromJson;
	public final Function<CcpJsonRepresentation, List<Boolean>> getPcdValuesFromJson;
	public final Function<CcpJsonRepresentation, List<Integer>> getDisponibilityValuesFromJson;
	public final Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getPjValuesFromJson;
	public final Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getBtcValuesFromJson;
	public final Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getCltValuesFromJson;
	private GetValuesFromJson(
			Function<CcpJsonRepresentation, String> getSeniorityValueFromJson,
			Function<CcpJsonRepresentation, List<Boolean>> getPcdValuesFromJson,
			Function<CcpJsonRepresentation, List<Integer>> getDisponibilityValuesFromJson,
			Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getPjValuesFromJson,
			Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getBtcValuesFromJson,
			Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getCltValuesFromJson) {
	
		this.getDisponibilityValuesFromJson = getDisponibilityValuesFromJson;
		this.getSeniorityValueFromJson = getSeniorityValueFromJson;
		this.getPcdValuesFromJson = getPcdValuesFromJson;
		this.getBtcValuesFromJson = getBtcValuesFromJson;
		this.getCltValuesFromJson = getCltValuesFromJson;
		this.getPjValuesFromJson = getPjValuesFromJson;
	}

	public static GetValuesFromJson getHashFromJson(CcpJsonRepresentation json) {
		
		boolean isResumeJson = json.containsAllKeys("experience");
		
		if(isResumeJson) {
			GetValuesFromJson hashFromResume = getHashFromResume();
			return hashFromResume;
		}
		GetValuesFromJson hashFromPosition = getHashFromPosition();
		return hashFromPosition;
	}

	private static GetValuesFromJson getHashFromResume() {
		Function<CcpJsonRepresentation, String> getSeniorityValue = new GetSeniorityValueFromPosition();
		Function<CcpJsonRepresentation, List<Boolean>> getPcdValuesFromJson = new GetPcdValuesFromResume();
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getPjValuesFromJson = new GetMoneyValuesFromResume("pj");
		Function<CcpJsonRepresentation, List<Integer>> getDisponibilityValuesFromJson = new GetDisponibilityValuesFromResume();
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getBtcValuesFromJson = new GetMoneyValuesFromResume("btc");
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getCltValuesFromJson = new GetMoneyValuesFromResume("clt");
		GetValuesFromJson getHashFromJson = new GetValuesFromJson(getSeniorityValue, getPcdValuesFromJson, getDisponibilityValuesFromJson, getPjValuesFromJson, getBtcValuesFromJson, getCltValuesFromJson);
		return getHashFromJson;
	}

	private static GetValuesFromJson getHashFromPosition() {
		Function<CcpJsonRepresentation, String> getSeniorityValue = new GetSeniorityValueFromPosition();
		Function<CcpJsonRepresentation, List<Boolean>> getPcdValuesFromJson = new GetPcdValuesFromPosition();
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getPjValuesFromJson = new GetMoneyValuesFromPosition("pj");
		Function<CcpJsonRepresentation, List<Integer>> getDisponibilityValuesFromJson = new GetDisponibilityValuesFromPosition();
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getBtcValuesFromJson = new GetMoneyValuesFromPosition("btc");
		Function<CcpJsonRepresentation, List<CcpJsonRepresentation>> getCltValuesFromJson = new GetMoneyValuesFromPosition("clt");
		GetValuesFromJson getHashFromJson = new GetValuesFromJson(getSeniorityValue, getPcdValuesFromJson, getDisponibilityValuesFromJson, getPjValuesFromJson, getBtcValuesFromJson, getCltValuesFromJson);
		return getHashFromJson;
		
	}

}
