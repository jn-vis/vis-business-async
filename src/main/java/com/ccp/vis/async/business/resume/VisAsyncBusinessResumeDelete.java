package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.file.bucket.CcpFileBucket;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.vis.commons.entities.VisEntityResume;
import com.vis.commons.utils.VisCommonsUtils;

public class VisAsyncBusinessResumeDelete implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessResumeDelete INSTANCE = new VisAsyncBusinessResumeDelete();

	private VisAsyncBusinessResumeDelete() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {

		String tentant = VisCommonsUtils.getTenant();

		String email = json.getAsString("email");
		String folder = "resumes/" + email;
		
		CcpFileBucket bucket = CcpDependencyInjection.getDependency(CcpFileBucket.class);

		bucket.delete(tentant, folder);
		
		JnAsyncCommitAndAudit.INSTANCE.executeBulk(json, VisEntityResume.ENTITY, CcpEntityOperationType.delete);
		
		return json;
	}


}
