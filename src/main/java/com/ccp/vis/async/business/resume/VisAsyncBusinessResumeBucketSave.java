package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.vis.async.commons.VisAsyncUtils;

public class VisAsyncBusinessResumeBucketSave implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessResumeBucketSave INSTANCE = new VisAsyncBusinessResumeBucketSave();

	private VisAsyncBusinessResumeBucketSave() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation resume) {
		VisAsyncUtils.saveResume(resume, "resumeBase64", "file");
		VisAsyncUtils.saveResume(resume, "resumeText", "text");
		return CcpConstants.EMPTY_JSON;
	}

}
