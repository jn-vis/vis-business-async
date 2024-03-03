package com.ccp.jn.vis.cron.business.positions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.exceptions.process.CcpAsyncProcess;
import com.jn.commons.entities.JnEntityAsyncTask;
import com.jn.vis.commons.entities.VisEntityBalance;
import com.jn.vis.commons.entities.VisEntityPosition;
import com.jn.vis.commons.entities.VisEntityPositionFeesToSend;
import com.jn.vis.commons.entities.VisEntityPositionSchedulleSendResumes;
import com.jn.vis.commons.utils.VisTopics;

public class VisCronBusinessPositionSearchResumes  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation schedullingPlan) {
		
		List<CcpJsonRepresentation> schedullings = new VisEntityPositionSchedulleSendResumes().getManyByIds(schedullingPlan);

		List<CcpJsonRepresentation> schedullingsFilteredByRecruiterFunds = this.getSchedullingsFilteredByRecruiterFunds(schedullingPlan, schedullings);
		
		List<CcpJsonRepresentation> positions = new VisEntityPosition().getManyByIds(schedullingsFilteredByRecruiterFunds);
		for (CcpJsonRepresentation position : positions) {
			new CcpAsyncProcess().send(position, VisTopics.sendResumesToThisPosition, new JnEntityAsyncTask());
		}

		
		return CcpConstants.EMPTY_JSON;
	}
	
	
	private List<CcpJsonRepresentation> getSchedullingsFilteredByRecruiterFunds( CcpJsonRepresentation schedullingPlan, List<CcpJsonRepresentation> schedullings){
		
		List<CcpJsonRepresentation> allBalances = new VisEntityBalance().getManyByIds(schedullings);

		List<CcpJsonRepresentation> balancesNotFound = new ArrayList<CcpJsonRepresentation>(allBalances).stream().filter(balance -> balance.getAsBoolean("_found") == false).collect(Collectors.toList());

		CcpConstants.EMPTY_JSON.put("reason", "balanceNotFound").put("data", balancesNotFound);
		
		List<CcpJsonRepresentation> balancesFound = new ArrayList<CcpJsonRepresentation>(allBalances).stream().filter(balance -> balance.getAsBoolean("_found")).collect(Collectors.toList());
		
		CcpJsonRepresentation jsonFee = new VisEntityPositionFeesToSend().getOneById(schedullingPlan);
		
		Double fee = jsonFee.getAsDoubleNumber("fee");

		List<CcpJsonRepresentation> balancesAble = new ArrayList<CcpJsonRepresentation>(balancesFound)
				.stream().filter(balance -> balance.getAsDoubleNumber("balance") > fee)
				.collect(Collectors.toList());
		
		List<CcpJsonRepresentation> insufficientFunds = new ArrayList<CcpJsonRepresentation>(balancesFound)
				.stream().filter(balance -> balance.getAsDoubleNumber("balance") <= fee)
				.map(x -> x.putAll(x.getInnerJson("_originalQuery"))
						.removeKey("_originalQuery").put("fee", fee)
						.putAll(schedullingPlan))
				.collect(Collectors.toList());
		
		CcpConstants.EMPTY_JSON.put("reason", "insufficientFunds").put("data", insufficientFunds);
		
		return balancesAble;
	}

}
