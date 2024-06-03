package com.ccp.vis.schedulling;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityGroupResumesOpinionsByRecruiter;
import com.jn.vis.commons.entities.VisEntityResumeOpinion;

public class VisAsyncBusinessGroupResumesOpinionsByRecruiter implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	private VisAsyncBusinessGroupResumesOpinionsByRecruiter() {}
	
	public static final VisAsyncBusinessGroupResumesOpinionsByRecruiter INSTANCE = new VisAsyncBusinessGroupResumesOpinionsByRecruiter();
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		CcpJsonRepresentation groupDetailsByMasters = VisAsyncUtils.groupDetailsByMasters(
				json, 
				VisEntityResumeOpinion.INSTANCE, 
				VisEntityGroupResumesOpinionsByRecruiter.INSTANCE, 
				VisEntityResumeOpinion.Fields.email, 
				VisEntityResumeOpinion.Fields.timestamp
				);
		
		return groupDetailsByMasters;
	}

}
