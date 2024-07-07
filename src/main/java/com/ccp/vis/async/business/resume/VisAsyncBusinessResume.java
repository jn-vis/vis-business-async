package com.ccp.vis.async.business.resume;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.text.extractor.CcpTextExtractor;
import com.ccp.jn.async.commons.JnAsyncMensageriaSender;
import com.jn.vis.commons.utils.VisAsyncBusiness;
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

		CcpJsonRepresentation addTimeFields = VisEntityResume.INSTANCE.addTimeFields(json);
		
		CcpTextExtractor textExtractor = CcpDependencyInjection.getDependency(CcpTextExtractor.class);

		String resumeBase64 = json.getAsString("resumeBase64");
		
		String resumeText = textExtractor.extractText(resumeBase64);
		
		CcpJsonRepresentation put = addTimeFields.put("resumeText", resumeText);
		//TODO quando o base64 nao tiver conteudo
		//TODO quando o curriculo for salvo com sucesso
		
		return put;
	}

}
