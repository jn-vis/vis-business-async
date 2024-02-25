package com.ccp.jn.vis.async.business;

import java.util.List;

import com.ccp.constantes.CcpConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntityOperationType;
import com.ccp.jn.async.business.JnAsyncBusinessCommitAndAudit;
import com.ccp.jn.vis.async.business.utils.CalculateHashes;
import com.jn.vis.commons.entities.VisEntityResumeHash;

public class VisAsyncBusinessResumeCalculateHashes  implements  java.util.function.Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	private JnAsyncBusinessCommitAndAudit commitAndAudit = new JnAsyncBusinessCommitAndAudit();

	@Override
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {

		/*Agendamento do envio de curriculos
		 * A) Salvar vaga
		 * B) Verifica-se o saldo do recrutador
		 * C) São calculados os hashes da vaga
		 * D) Os hashes da vaga buscam os hashes dos curriculos
		 * E) A associação entre vaga e currículo é salva em uma tabela de marcação
		 * F) A vaga, juntamente com os currículos encontrados, é salva em uma tabela de periodicidade 
		 * (vis_position_monthly_sending, vis_position_weekly_sending, vis_position_daily_sending, 
		 * vis_position_hourly_sending)
		 */
		/*Execução do envio de currículos
		 * A) Busca-se vagas pertencentes àquela periodicidade
		 * B) Os ids dos curriculos sao buscados
		 * C) Os donos dos curriculos sao notificados do envio
		 * D) O e-mail é formado e enviado ao dono da vaga
		 * E) Debitado o saldo do recrutador e registrada a transação
		 */
		
		/* Execução de envio de currículos sem agendamento
		 * A) Salvar vaga
		 * B) Verifica-se o saldo do recrutador
		 * C) São calculados os hashes da vaga
		 * D) Os hashes da vaga buscam os hashes dos curriculos
		 * E) A associação entre vaga e currículo é salva em uma tabela de marcação
		 * F) Os ids dos curriculos sao buscados
		 * G) Os donos dos curriculos sao notificados do envio
		 * H) O e-mail é formado e enviado ao dono da vaga
		 * I) Debitado o saldo do recrutador e registrada a transação
		 */
		
		/*Visualização da vaga
		 * A) Verificação permissão do candidato àquele recrutador
		 * B) Verificação de cumprimento de requisitos do currículo em realação à vaga
		 * C) Verificação de disponibilidade de currículo
		 * D) Verificação de suficiência de saldo do recrutador
		 * E) Verificação de disponibilidade da vaga
		 * F) Verificação de prévio pagamento à visualização
		 * G) Debitação de saldo do recrutador
		 * H) Creditação de saldo ao candidato
		 * I) Registro de transações financeiras
		 * J) Notificação ao candidato
		 * K) Registro da ocorrência
		 * L) Montagem da visualização 
		 */
		
		/*Cálculo de hashes de curriculos
		 * A) Salvar currículo 
		 * B) Calcular hashes		
		 * C) Salvar arquivo
		 * 
		 */


		/*Recepção de saldo LN dos recrutadores
		 * 
		 */
		
		/*Pagamento de saldo LN a candidatos
		 * 
		 */
		
		List<CcpJsonRepresentation> hashes = CalculateHashes.getHashes(json);
		
		this.commitAndAudit.execute(hashes, CcpEntityOperationType.create, new VisEntityResumeHash());
	
		return CcpConstants.EMPTY_JSON;
	}

}
