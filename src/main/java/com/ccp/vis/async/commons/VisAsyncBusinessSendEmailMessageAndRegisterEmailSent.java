package com.ccp.vis.async.commons;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.async.messages.JnAsyncSendMessage;
import com.jn.commons.entities.JnEntityEmailMessageSent;
import com.jn.commons.entities.JnEntityEmailTemplateMessage;
import com.jn.commons.utils.JnTopic;

public enum VisAsyncBusinessSendEmailMessageAndRegisterEmailSent implements Function<CcpJsonRepresentation, CcpJsonRepresentation>,JnTopic {

	
	resumeSuccessSaving,
	resumeErrorSaving
;

	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {

		CcpJsonRepresentation put = json
				.renameField("originalEmail", JnEntityEmailMessageSent.Fields.email.name())
				.put(JnEntityEmailMessageSent.Fields.subjectType.name(), this.name());
				
			String language = json.getAsObject(JnEntityEmailTemplateMessage.Fields.language.name());
			
			JnAsyncSendMessage sender = new JnAsyncSendMessage();
			sender
			.addDefaultProcessForEmailSending()
			.soWithAllAddedProcessAnd()
			.withTheTemplateEntity(this.name())
			.andWithTheEntityToBlockMessageResend(JnEntityEmailMessageSent.ENTITY)
			.andWithTheMessageValuesFromJson(put)
			.andWithTheSupportLanguage(language)
			.sendAllMessages()
			;

		
		return json;
	}

	public Class<?> validationClass() {
		return this.getClass();
	}

}
