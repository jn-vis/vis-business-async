package com.ccp.vis.async.commons.sort.resumes;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;

public class PositionResumesSort implements Comparator<CcpJsonRepresentation>{

	private final CcpJsonRepresentation position;

	public PositionResumesSort(CcpJsonRepresentation position) {
		this.position = position;
	}

	public int compare(CcpJsonRepresentation o1, CcpJsonRepresentation o2) {
		
		List<String> desiredSkill = this.position.getAsStringList("desiredSkill");
		
		CcpJsonRepresentation put1 = this.putDesiredSkills(o1, desiredSkill);
		CcpJsonRepresentation put2 = this.putDesiredSkills(o2, desiredSkill);
		
		List<String> sortFields = new ArrayList<>(this.position.getAsStringList("sortFields"));
		
		String desiredSkillEnumName = ResumeSortOptions.desiredSkill.name();
		boolean desiredSkillNotChoosed = sortFields.contains(desiredSkillEnumName) == false;
		
		if(desiredSkillNotChoosed ) {
			sortFields.add(desiredSkillEnumName);
		}
		
		for (String sortField : sortFields) {
			
			ResumeSortOptions valueOf = ResumeSortOptions.valueOf(sortField);
			int comparationResult = valueOf.compare(put1, put2);
			
			boolean areEsquals = comparationResult == 0;
			
			if(areEsquals) {
				continue;
			}
			
			return comparationResult;
		}
		return 0;
	}

	private CcpJsonRepresentation putDesiredSkills(CcpJsonRepresentation o1, List<String> desiredSkills) {
		CcpCollectionDecorator ccd1 = o1.getAsCollectionDecorator("skill");
		int size1 = ccd1.getIntersectList(desiredSkills).size();
		CcpJsonRepresentation put = o1.put("desiredSkill", -size1);
		return put;
	}

	
	
}
