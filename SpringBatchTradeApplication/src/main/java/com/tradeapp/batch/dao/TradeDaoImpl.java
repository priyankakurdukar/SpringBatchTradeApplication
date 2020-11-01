package com.tradeapp.batch.dao;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TradeDaoImpl {
	private JdbcTemplate jdbcTemplate;

	@Autowired
	public void setdataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Value("${trade.getTradeRecordwithHigherVersion}")
	private String getTradeRecordwithHigherVersionSql;
	
	@Value("${trade.deleteTradeRecordwithSameVersion}")
	private String deleteTradeRecordwithSameVersionSql;
	
	@Value("${trade.updateExpiredFlag}")
	private String updateExpiredFlagSql;
	
	
	private static final Logger log = LoggerFactory.getLogger(TradeDaoImpl.class);
	
	public TradeDaoImpl() {
		
	}
	public int deleteTradeRecord(String tradeId, Integer version) {
		int rowDeleted = 0;
		try {
			rowDeleted = jdbcTemplate.update(deleteTradeRecordwithSameVersionSql,  tradeId,	version);
		} catch (Exception e) {
			log.error("Error while deleting Record same version for given Trade id.");
		}
		return rowDeleted;
	}

	public int getTradeRecordwithHigherVersion(String tradeId, int version) {
		int versionFromDb = -1;
		try {
			versionFromDb = jdbcTemplate.queryForObject(getTradeRecordwithHigherVersionSql, Integer.class, tradeId,
					version);
		}catch(EmptyResultDataAccessException eda){
			//no record present in DB for given trade
		}
		catch (Exception e) {
			log.error("Error while fetching Record count with higher version for given Trade id.");
		}
		return versionFromDb;
	}
	
	public int updateExpiredFlag() {
		int rowUpdated = 0;
		try {
			rowUpdated = jdbcTemplate.update(updateExpiredFlagSql);
		} catch (Exception e) {
			log.error("Error while updating Record Expired Flag for Expired Trades");
		}
		return rowUpdated;
	}


}
