package com.ccp.vis.exceptions;

import java.util.List;

@SuppressWarnings("serial")
public class RequiredSkillsMissingInResume extends RuntimeException{

	public final List<String> requiredSkillsNotFoundInResume;

	public RequiredSkillsMissingInResume(List<String> requiredSkillsNotFoundInResume) {
		this.requiredSkillsNotFoundInResume = requiredSkillsNotFoundInResume;
	}
	
	
	
}
