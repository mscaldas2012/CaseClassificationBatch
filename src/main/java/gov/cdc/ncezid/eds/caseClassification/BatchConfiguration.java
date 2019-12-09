package gov.cdc.ncezid.eds.caseClassification;

import javax.sql.DataSource;

import gov.cdc.ncezid.eds.caseClassification.model.FDDCase;
import gov.cdc.ncezid.eds.caseClassification.model.TransmissionHeader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.H2PagingQueryProvider;
import org.springframework.batch.item.database.support.SqlServerPagingQueryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import java.util.HashMap;
import java.util.Map;

// tag::setup[]
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	private static final String SELECT_QUERY = "SELECT state, patId, dtSpec";
	private static final String FROM_QUERY ="FROM transmissionHeader";
	private static final String INSERT_QUERY = "INSERT INTO fddcases (state, patId, dtSpec) VALUES (:state, :patId, :dtSpec)";


	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;
	// end::setup[]

	// tag::readerwriterprocessor[]
	@Bean
	public ItemReader<TransmissionHeader> reader(DataSource dataSource) {
		JdbcPagingItemReader<TransmissionHeader> databaseReader = new JdbcPagingItemReader<>();

		databaseReader.setDataSource(dataSource);
		databaseReader.setPageSize(10);

		PagingQueryProvider queryProvider = createQueryProvider();
		databaseReader.setQueryProvider(queryProvider);

		databaseReader.setRowMapper(new BeanPropertyRowMapper<>(TransmissionHeader.class));

		return databaseReader;
	}

	private PagingQueryProvider createQueryProvider() {
		org.springframework.batch.item.database.support.SqlServerPagingQueryProvider qp = new SqlServerPagingQueryProvider();
		H2PagingQueryProvider queryProvider = new H2PagingQueryProvider();

		queryProvider.setSelectClause(SELECT_QUERY);
		queryProvider.setFromClause(FROM_QUERY);
		queryProvider.setSortKeys(sortByEmailAddressAsc());

		return queryProvider;
	}

	private Map<String, Order> sortByEmailAddressAsc() {
		Map<String, Order> sortConfiguration = new HashMap<>();
		sortConfiguration.put("state", Order.ASCENDING);
		sortConfiguration.put("patId", Order.ASCENDING);
		sortConfiguration.put("dtSpec", Order.ASCENDING);
		return sortConfiguration;
	}

	@Bean
	public TransmissionProcessor processor() {
		return new TransmissionProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<FDDCase> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<FDDCase>()
			.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
			.sql(INSERT_QUERY)
			.dataSource(dataSource)
			.build();
	}
	// end::readerwriterprocessor[]

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob")
			.incrementer(new RunIdIncrementer())
			.listener(listener)
			.flow(step1)
			.end()
			.build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<FDDCase> writer, DataSource datasource) {
		return stepBuilderFactory.get("step1")
			.<TransmissionHeader, FDDCase> chunk(10)
			.reader(reader(datasource))
			.processor(processor())
			.writer(writer)
			.build();
	}
	// end::jobstep[]
}
