package com.tradeapp.batch.processor;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import com.tradeapp.batch.dao.TradeDaoImpl;
import com.tradeapp.batch.exception.TradeException;
import com.tradeapp.batch.model.Trade;

public class TradeRecordProcessor implements ItemProcessor<Trade, Trade> {

	@Autowired
	private TradeDaoImpl tradeDaoImpl;

	private static final Logger log = LoggerFactory.getLogger(TradeRecordProcessor.class);

	@Override
	public Trade process(final Trade inputTrade) throws Exception {
		log.info("Inside TradeRecordProcessor : Input Trade ID is {}, Trade Version is {} ", inputTrade.getTradeId(),
				inputTrade.getVersion());
		Date currentDate = new Date();
		try {

			// Validation 1 :: 1. During transmission if the lower version is being received
			// by the store it will reject the trade and throw an exception. If the version
			// is same it will override the existing record.
			int versionFromDb  = tradeDaoImpl.getTradeRecordwithHigherVersion(inputTrade.getTradeId(),
					inputTrade.getVersion());
			
			if(versionFromDb != -1 && (versionFromDb == inputTrade.getVersion())) {
				//Delete the existing Trade
				int deleteRowCount = tradeDaoImpl.deleteTradeRecord(inputTrade.getTradeId(),inputTrade.getVersion());
				log.info("deleteRowCount :: {}", deleteRowCount); 
			}else if(versionFromDb > inputTrade.getVersion()) {
				log.info(
						"Trade Record with higher version already present in store for Trade id {} and version {} ",inputTrade.getTradeId(), inputTrade.getVersion());
				throw new TradeException("Trade Record with higher version already present in store");
			}

			
			// Validation 2 :: 2. Store should not allow the trade which has less maturity
			// date then today date.
			Date maturityDate = inputTrade.getMaturityDate();			
			if (currentDate.after(maturityDate)) {
				log.info(
						"Maturity Date for Trade id {} and version {} is before curent Date. hence this trade record is not considered.",
						inputTrade.getTradeId(), inputTrade.getVersion());
				throw new TradeException("Maturity Date of the Trade record is before curent Date.");
			}

		} catch (TradeException te) {
			return null;
		}
		inputTrade.setCreatedDate(currentDate);
		inputTrade.setExpired("N");

		log.info("Exiting TradeRecordProcessor : Input Trade ID is {}, Trade Version is {} ", inputTrade.getTradeId(),
				inputTrade.getVersion());
		return inputTrade;
	}

	

}