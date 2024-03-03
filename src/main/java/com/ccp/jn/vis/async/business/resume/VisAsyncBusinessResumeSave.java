package com.ccp.jn.vis.async.business.resume;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.jn.vis.business.utils.CalculateHashes;
import com.jn.vis.commons.entities.VisEntityResume;
import com.jn.vis.commons.entities.VisEntityResumeHash;

public class VisAsyncBusinessResumeSave  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public CcpJsonRepresentation apply(CcpJsonRepresentation newResume) {
		
		VisEntityResume visEntityResume = new VisEntityResume();
		VisEntityResumeHash visEntityResumeHash = new VisEntityResumeHash();
		CcpJsonRepresentation oldResume = visEntityResume.getOneById(newResume, CcpConstants.DO_NOTHING);
		
		CcpJsonRepresentation oldHash = oldResume.getInnerJson("hash");

		List<String> incomingHashes = CalculateHashes.getHashes(newResume).stream().map(x -> visEntityResumeHash.getId(x)).collect(Collectors.toList());
		List<String> existentHashes = oldHash.getAsStringList("insert");

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
