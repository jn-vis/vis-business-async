package com.ccp.vis.async.commons;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityOperationType;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.jn.async.commons.JnAsyncCommitAndAudit;

public class GroupDetailsByMasters implements Consumer<CcpJsonRepresentation>{
	
	private CcpJsonRepresentation groupedRecords = CcpConstants.EMPTY_JSON;

	private final String detailFieldName;
	
	private final String masterFieldName;

	public GroupDetailsByMasters(String detailFieldName, String masterFieldName, CcpEntity entity , CcpEntity entityGrouper) {
		this.detailFieldName = detailFieldName;
		this.masterFieldName = masterFieldName;
		
		CcpEntity mirrorEntityGrouper = entityGrouper.getMirrorEntity();
		CcpEntity mirrorEntity = entity.getMirrorEntity();

		String mirrorEntityName = mirrorEntity.getEntityName();
		String entityName = entity.getEntityName();
		
		this.mappers = CcpConstants.EMPTY_JSON
					.put(entityName, entityGrouper)
					.put(mirrorEntityName, mirrorEntityGrouper)
					;
	}

	public void accept(CcpJsonRepresentation record) {
		String master = record.getAsString(this.masterFieldName);
		String entity = record.getAsString("entity");
		CcpJsonRepresentation entityGroup = this.groupedRecords.getInnerJson(entity);
		entityGroup = entityGroup.addToList(master, record);
		this.groupedRecords = this.groupedRecords.put(entity, entityGroup);
	}
	
	private CcpJsonRepresentation mappers;
	
	public void saveAllDetailsGroupedByMasters(){
		
		Set<String> entities = this.groupedRecords.keySet();

		List<CcpBulkItem> result = new ArrayList<>();
		
		for (String entity : entities) {
			
			CcpEntity entityGroupToSaveRecords =  this.mappers.getAsObject(entity);
			
			CcpJsonRepresentation mastersInThisGrouping = this.groupedRecords.getInnerJson(entity);
			
			Set<String> masters = mastersInThisGrouping.keySet();
			
			List<CcpBulkItem> collect = masters.stream()
			.map(master -> this.getPositionsGroupedByEmail(mastersInThisGrouping, master, entityGroupToSaveRecords))
			.collect(Collectors.toList());
			
			result.addAll(collect);
		}

		JnAsyncCommitAndAudit.INSTANCE.executeBulk(result);
	}

	private CcpBulkItem getPositionsGroupedByEmail(CcpJsonRepresentation emailsInThisGrouping, String master, CcpEntity entityGroupToSaveRecords) {
		
		List<CcpJsonRepresentation> records = emailsInThisGrouping.getAsJsonList(master);
		
		CcpJsonRepresentation json = CcpConstants.EMPTY_JSON
			.put(this.detailFieldName, records)
			.put(this.masterFieldName, master);
		
		CcpBulkItem bulkItem = entityGroupToSaveRecords.toBulkItem(json, CcpEntityOperationType.create);
		
		return bulkItem;
	}
	
}
