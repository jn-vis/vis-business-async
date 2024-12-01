package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.text.extractor.CcpTextExtractor;
import com.ccp.exceptions.process.CcpFlow;
import com.ccp.jn.async.commons.JnAsyncMensageriaSender;
import com.ccp.jn.async.messages.JnAsyncSendMessage;
import com.ccp.process.CcpProcessStatus;
import com.jn.commons.entities.JnEntityEmailMessageSent;
import com.jn.vis.commons.utils.VisAsyncBusiness;
import com.jn.vis.commons.utils.VisStringConstants;
import com.vis.commons.entities.VisEntityResume;

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

		CcpJsonRepresentation addTimeFields = VisEntityResume.ENTITY.addTimeFields(json);
		
		CcpTextExtractor textExtractor = CcpDependencyInjection.getDependency(CcpTextExtractor.class);

		String resumeBase64 = json.getAsString("resumeBase64");

		try {
			String resumeText = textExtractor.extractText(resumeBase64);
			
			boolean emptyText = resumeText.trim().isEmpty();
			
			if(emptyText) {
				this.sendMessage(json, VisStringConstants.ID_TO_LOAD_RESUME_ERROR_TEMPLATE_MESSAGE.name());
				throw new CcpFlow(json, CcpProcessStatus.NOT_FOUND);
			}
			
			this.sendMessage(json, VisStringConstants.ID_TO_LOAD_RESUME_SUCCESS_TEMPLATE_MESSAGE.name());
			CcpJsonRepresentation put = addTimeFields.put("resumeText", resumeText);
			
			return put;
			
		} catch (Exception e) {
			
			throw new RuntimeException(e);
		}
	}

	private void sendMessage(CcpJsonRepresentation json, String templateId) {
		CcpJsonRepresentation put = json
			.renameField(VisStringConstants.originalEmail.name(), JnEntityEmailMessageSent.Fields.email.name())
			.put(JnEntityEmailMessageSent.Fields.subjectType.name(), templateId);
			
		String language = VisStringConstants.PORTUGUESE.name();//TODO INTERNACIONALIZAR SALVAMENTO DE CURRICULO
		
		JnAsyncSendMessage sender = new JnAsyncSendMessage();
		sender
		.addDefaultProcessForEmailSending()
		.soWithAllAddedProcessAnd()
		.withTheTemplateEntity(templateId)
		.andWithTheEntityToBlockMessageResend(JnEntityEmailMessageSent.ENTITY)
		.andWithTheMessageValuesFromJson(put)
		.andWithTheSupportLanguage(language)
		.sendAllMessages()
		;
		
	}

}
