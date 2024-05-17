package com.ccp.vis.async.business.resume;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.file.bucket.CcpFileBucketOperation;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;
import com.ccp.vis.async.commons.VisAsyncUtils;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.utils.VisCommonsUtils;

public class VisAsyncBusinessResumeDelete implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessResumeDelete INSTANCE = new VisAsyncBusinessResumeDelete();

	private VisAsyncBusinessResumeDelete() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {

		VisAsyncUtils.removeFromCache(resume, "text");

		VisAsyncUtils.removeFromCache(resume, "file");

		String tentant = VisCommonsUtils.getTenant();

		String folder = VisCommonsUtils.getBucketFolderResume(resume);
		
		CcpFileBucketOperation.delete.execute(tentant, folder, "text", "file");

		JnAsyncCommitAndAudit.INSTANCE.executeBulk(resume, VisEntityResume.INSTANCE, CcpEntityOperationType.delete);
		
		return CcpConstants.EMPTY_JSON;
	}


}
