package com.tradeapp.batch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.tradeapp.batch.listener.JobCompletionNotificationListener;
import com.tradeapp.batch.model.Trade;
import com.tradeapp.batch.processor.TradeRecordProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	public FlatFileItemReader<Trade> reader() {
		FlatFileItemReader<Trade> reader = new FlatFileItemReader<Trade>();
		reader.setResource(new ClassPathResource("trade.csv"));
		reader.setLineMapper(new DefaultLineMapper<Trade>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer() {
					{
						setNames(new String[] { "tradeId", "version", "counterPartyId", "bookId", "maturityDate" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<Trade>() {
					{
						setTargetType(Trade.class);
					}
				});
			}
		});
		return reader;
	}

	@Bean
	public TradeRecordProcessor processor() {
		return new TradeRecordProcessor();
	}


	@Bean
	public JdbcBatchItemWriter<Trade> writer() {
		JdbcBatchItemWriter<Trade> writer = new JdbcBatchItemWriter<Trade>();
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Trade>());
		writer.setSql(
				"INSERT INTO Trade (TRADE_ID, VERSION,COUNTER_PARTY_ID,BOOK_ID,MATURITY_DATE,CREATED_DATE, EXPIRED) VALUES (:tradeId, :version,:counterPartyId,:bookId,:maturityDate,:createdDate,:expired)");
		writer.setDataSource(dataSource);
		return writer;
	}

	@Bean
	public Job importTradeJob(JobCompletionNotificationListener listener) {
		//We can use Partitioner as well for parallel processing
		return jobBuilderFactory.get("importTradeJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1())
                .end()
                .build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1").<Trade, Trade>chunk(10).reader(reader()).processor(processor())
				.writer(writer()).build();
	}


}