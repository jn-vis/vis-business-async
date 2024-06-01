package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityGroupResumesOpinionsByResume;
import com.jn.vis.commons.entities.VisEntityResumeOpinion;

public class VisAsyncBusinessGroupResumesOpinionsByResume implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumesOpinionsByResume() {}
	
	public static final VisAsyncBusinessGroupResumesOpinionsByResume INSTANCE = new VisAsyncBusinessGroupResumesOpinionsByResume();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation groupDetailsByMasters = VisAsyncUtils.groupDetailsByMasters(
				json, 
				VisEntityResumeOpinion.INSTANCE, 
				VisEntityGroupResumesOpinionsByResume.INSTANCE, 
				VisEntityResumeOpinion.Fields.email, 
				VisEntityGroupResumesOpinionsByResume.Fields.opinion, 
				VisEntityResumeOpinion.Fields.timestamp
				);
		
		return groupDetailsByMasters;

	}

}
