package com.tradeapp.batch.model;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tradeapp.batch.constants.TradeConstants;
import com.tradeapp.batch.processor.TradeRecordProcessor;

import io.micrometer.core.instrument.util.StringUtils;

public class Trade implements Serializable {

	private static final Logger log = LoggerFactory.getLogger(Trade.class);

	private static final long serialVersionUID = -6402068923614583448L;
	private String tradeId;
	private Integer version;
	private String counterPartyId;
	private String bookId;
	private String maturityDate;
	private Date createdDate;
	private String expired;

	public Trade() {

	}

	/**
	 * @param tradeId
	 * @param version
	 * @param counterPartyId
	 * @param bookId
	 * @param maturityDate
	 * @param createdDate
	 * @param expired
	 */
	public Trade(String tradeId, Integer version, String counterPartyId, String bookId, String maturityDate,
			Date createdDate, String expired) {
		super();
		this.tradeId = tradeId;
		this.version = version;
		this.counterPartyId = counterPartyId;
		this.bookId = bookId;
		this.maturityDate = maturityDate;
		this.createdDate = createdDate;
		this.expired = expired;
	}

	/**
	 * @param tradeId
	 * @param version
	 * @param counterPartyId
	 * @param bookId
	 * @param maturityDate
	 */
	public Trade(String tradeId, Integer version, String counterPartyId, String bookId, String maturityDate) {
		super();
		this.tradeId = tradeId;
		this.version = version;
		this.counterPartyId = counterPartyId;
		this.bookId = bookId;
		this.maturityDate = maturityDate;
		this.createdDate = new Date();
	}

	/**
	 * @return the tradeId
	 */
	public String getTradeId() {
		return tradeId;
	}

	/**
	 * @param tradeId the tradeId to set
	 */
	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	/**
	 * @return the version
	 */
	public Integer getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(Integer version) {
		this.version = version;
	}

	/**
	 * @return the counterPartyId
	 */
	public String getCounterPartyId() {
		return counterPartyId;
	}

	/**
	 * @param counterPartyId the counterPartyId to set
	 */
	public void setCounterPartyId(String counterPartyId) {
		this.counterPartyId = counterPartyId;
	}

	/**
	 * @return the bookId
	 */
	public String getBookId() {
		return bookId;
	}

	/**
	 * @param bookId the bookId to set
	 */
	public void setBookId(String bookId) {
		this.bookId = bookId;
	}

	/**
	 * @return the maturityDate
	 */
	public Date getMaturityDate() {
		Date maturityDate = null;
		if (StringUtils.isNotBlank(this.maturityDate)) {
			try {
				maturityDate = new SimpleDateFormat(TradeConstants.DATE_FOMAT).parse(this.maturityDate);
			} catch (ParseException e) {
				log.error("Error while parsing Maturity Date for input value {} ", this.maturityDate);
			}
		}
		return maturityDate;
	}

	/**
	 * @param maturityDate the maturityDate to set
	 */
	public void setMaturityDate(String maturityDate) {
		this.maturityDate = maturityDate;
	}

	/**
	 * @return the createdDate
	 */
	public Date getCreatedDate() {
		return createdDate;
	}

	/**
	 * @param createdDate the createdDate to set
	 */
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	/**
	 * @return the expired
	 */
	public String getExpired() {
		return expired;
	}

	/**
	 * @param expired the expired to set
	 */
	public void setExpired(String expired) {
		this.expired = expired;
	}

	@Override
	public String toString() {
		return "Trade [tradeId=" + tradeId + ", version=" + version + ", counterPartyId=" + counterPartyId + ", bookId="
				+ bookId + ", maturityDate=" + maturityDate + ", createdDate=" + createdDate + ", expired=" + expired
				+ "]";
	}

}
