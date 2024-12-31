package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.decorators.CcpAddTimeFields;
import com.ccp.especifications.text.extractor.CcpTextExtractor;
import com.ccp.exceptions.process.CcpFlow;
import com.ccp.jn.async.commons.JnAsyncMensageriaSender;
import com.ccp.process.CcpDefaultProcessStatus;
import com.ccp.vis.async.commons.VisAsyncBusinessSendEmailMessageAndRegisterEmailSent;
import com.jn.vis.commons.utils.VisAsyncBusiness;

public class VisAsyncBusinessResume implements  Function<CcpJsonRepresentation, CcpJsonRepresentation>{

	public static final VisAsyncBusinessResume INSTANCE = new VisAsyncBusinessResume();

	private VisAsyncBusinessResume() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation handledResume = this.getHandledResume(json);
		
		JnAsyncMensageriaSender.INSTANCE.send(VisAsyncBusiness.resumeSave, handledResume);
		
		JnAsyncMensageriaSender.INSTANCE.send(VisAsyncBusiness.resumeBucketSave, handledResume);
		
		return handledResume;
	}

	private CcpJsonRepresentation getHandledResume(CcpJsonRepresentation json) {

		CcpTextExtractor textExtractor = CcpDependencyInjection.getDependency(CcpTextExtractor.class);

		String resumeBase64 = json.getAsString("resumeBase64");

		try {
			String resumeText = textExtractor.extractText(resumeBase64);
			
			boolean emptyText = resumeText.trim().isEmpty();
			
			if(emptyText) {
				JnAsyncMensageriaSender.INSTANCE.send(VisAsyncBusinessSendEmailMessageAndRegisterEmailSent.resumeErrorSaving, json);
				throw new CcpFlow(json, CcpDefaultProcessStatus.NOT_FOUND);
			}
			
			JnAsyncMensageriaSender.INSTANCE.send(VisAsyncBusinessSendEmailMessageAndRegisterEmailSent.resumeSuccessSaving, json);
			
			CcpJsonRepresentation put = json.put("resumeText", resumeText);
			
			CcpJsonRepresentation transformedJson = put.getTransformedJson(CcpAddTimeFields.INSTANCE);
			
			return transformedJson;
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
	}


}
