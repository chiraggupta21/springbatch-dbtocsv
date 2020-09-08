package com.example.demo.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort.Direction;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;

@Configuration
public class BatchConfiguration {
	
	@Autowired
	UserRepository userRepository;
	
	
	@Bean
	Job job(JobBuilderFactory jobBuilderFactory,StepBuilderFactory stepBuilderFactory
			,ItemReader<User> itemReader,ItemWriter<User> itemWriter
			,ItemProcessor<User, User> itemProcessor) {
		
		Step step =stepBuilderFactory.get("STEP")
					.<User,User>chunk(null)
					.reader(itemReader)
					.processor(itemProcessor)
					.writer(itemWriter)
					.build();
		return jobBuilderFactory.get("JOB Should Be Done")
				.incrementer(new RunIdIncrementer())
				.start(step)
				.build();
	}
	
	@Bean
	RepositoryItemReader<User> reader(){
		RepositoryItemReader<User> repositoryItemReader=new RepositoryItemReader<>();
		repositoryItemReader.setRepository(userRepository);
		repositoryItemReader.setMethodName("findAll");
		Map<String, Direction> sorts =new HashMap<>();
		sorts.put("id", Direction.ASC);
		repositoryItemReader.setSort(sorts );
		
		return repositoryItemReader;
	}
	
	@Bean
	FlatFileItemWriter<User> flatfilewriter(@Value("${fileLocation}") Resource resource){
		
		
		BeanWrapperFieldExtractor<User> beanWrapper=new BeanWrapperFieldExtractor<>();
		beanWrapper.setNames(new String[] {"id","name","Department"});
		
		DelimitedLineAggregator<User> delimitedLineAggregator=new DelimitedLineAggregator<>();
		delimitedLineAggregator.setDelimiter(",");
		delimitedLineAggregator.setFieldExtractor(beanWrapper);
		
		FlatFileItemWriter<User> flatFileItemWriter=new FlatFileItemWriter<>();
		flatFileItemWriter.setResource(resource);
		flatFileItemWriter.setLineAggregator(delimitedLineAggregator);
		return flatFileItemWriter;
	}

}
