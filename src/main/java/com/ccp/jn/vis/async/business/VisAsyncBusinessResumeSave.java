package com.ccp.jn.vis.async.business;

import java.util.List;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.jn.vis.commons.entities.VisEntityResume;

public class VisAsyncBusinessResumeSave  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation newResume) {
		
		VisEntityResume visEntityResume = new VisEntityResume();
		
		CcpJsonRepresentation oldResume = visEntityResume.getOneById(newResume, CcpConstants.DO_NOTHING);
		
		CcpJsonRepresentation oldHash = oldResume.getInnerJson("hash");
		CcpJsonRepresentation newHash = newResume.getInnerJson("hash");

		List<String> existentHashes = oldHash.getAsStringList("insert");
		List<String> incomingHashes = newHash.getAsStringList("insert");
		
		List<String> hashesToRemoveIn = new CcpCollectionDecorator(existentHashes).getExclusiveList(incomingHashes);
		List<String> hashesToInsertIn = new CcpCollectionDecorator(incomingHashes).getExclusiveList(existentHashes);

		CcpJsonRepresentation dataToSave = newResume
		.put("lastUpdate", System.currentTimeMillis())
		.putSubKey("hash", "insert", hashesToInsertIn)
		.putSubKey("hash", "remove", hashesToRemoveIn)
		;
		visEntityResume.createOrUpdate(dataToSave);
		
		return dataToSave;
	}

}
