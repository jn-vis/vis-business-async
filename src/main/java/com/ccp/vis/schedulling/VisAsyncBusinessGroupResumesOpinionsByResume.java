package com.ccp.vis.schedulling;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityGroupResumesPerceptionsByResume;
import com.jn.vis.commons.entities.VisEntityResumePerception;

public class VisAsyncBusinessGroupResumesOpinionsByResume implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumesOpinionsByResume() {}
	
	public static final VisAsyncBusinessGroupResumesOpinionsByResume INSTANCE = new VisAsyncBusinessGroupResumesOpinionsByResume();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation groupDetailsByMasters = VisAsyncUtils.groupDetailsByMasters(
				json, 
				VisEntityResumePerception.INSTANCE, 
				VisEntityGroupResumesPerceptionsByResume.INSTANCE, 
				VisEntityResumePerception.Fields.email, 
				VisEntityResumePerception.Fields.timestamp
				);
		
		return groupDetailsByMasters;

	}

}
