package com.tradeapp.batch.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.tradeapp.batch.dao.TradeDaoImpl;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

	@Autowired
	private TradeDaoImpl tradeDaoImpl;
	
	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	/*
	 * private final JdbcTemplate jdbcTemplate;
	 * 
	 * @Autowired public JobCompletionNotificationListener(JdbcTemplate
	 * jdbcTemplate) { this.jdbcTemplate = jdbcTemplate; }
	 */

	@Override
	public void afterJob(JobExecution jobExecution) {
		if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("TRADE RECORD ADDEDTO STORE!!!");

			// 3. Store should automatically update the expire flag if in a store the trade
			// crosses the maturity date.
			log.info(" Inside TradeExipryTask :: Updating the Expired Flag in Trade Store");
			
			int updateCount = tradeDaoImpl.updateExpiredFlag();
			
			log.info(" End Of TradeExipryTask :: Updated the Expired Flag in Trade Store for {} records", updateCount);
			

		}
	}
}