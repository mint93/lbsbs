package com.demo.liquibase_spring_boot_starter.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;

import com.demo.liquibase_spring_boot_starter.liquibase.SpringLiquibaseExtended;
import com.demo.liquibase_spring_boot_starter.property.LiquibaseExtendedProperties;

@Configuration
//@AutoConfigureAfter(LiquibaseAutoConfiguration.class)
@EnableConfigurationProperties(LiquibaseExtendedProperties.class)
public class LiquibaseConfiguration {
	
	private LiquibaseExtendedProperties liquibaseProperties;
	
	public LiquibaseConfiguration(LiquibaseExtendedProperties liquibaseExtendedProperties) {
		//liquibaseExtendedProperties.setLiquibaseProperties(liquibaseProperties);
		this.liquibaseProperties = liquibaseExtendedProperties;
	}

	@Bean
	public SpringLiquibaseExtended liquibase() {
	    SpringLiquibaseExtended liquibase = new SpringLiquibaseExtended();
	    String changelog = liquibaseProperties.getChangeLog()==null ? "" : liquibaseProperties.getChangeLog();
	    liquibase.setChangeLog(changelog);
	    liquibase.setDataSource(DataSourceBuilder.create()
	    		.url(liquibaseProperties.getUrl())
				.username(liquibaseProperties.getUser())
				.password(liquibaseProperties.getPassword()).build());
	    liquibase.setContexts(liquibaseProperties.getContexts());
	    liquibase.setShouldRun(liquibaseProperties.isUpdateDatabase());
	    return liquibase;
	}
	
	@EventListener
	public void onContextRefresh(ContextRefreshedEvent event) {
		String contexts = liquibaseProperties.getContexts();
		if (isNullOrEmpty(contexts)) {
			throw new RuntimeException("spring.liquibase.contexts property must not be empty");
		}
//		String syncChangeLogToVersion = liquibaseProperties.getSyncChangeLogToVersion();
//		if (syncChangeLogToVersion != null && !syncChangeLogToVersion.isEmpty()) {
//			liquibase().syncChangelogToVersion(syncChangeLogToVersion, contexts);
//		}
		if (liquibaseProperties.isSynchronizeChangeLog()) {
			liquibase().synchronizeChangelog(contexts);
		}
		if (liquibaseProperties.isGenerateChangeLog()) {
			liquibase().generateChangelog(contexts);
		}
		String rollbackToVersion = liquibaseProperties.getRollbackToVersion();
		if (isNotNullAndNotEmpty(rollbackToVersion)) {
			liquibase().rollbackToVersion(rollbackToVersion, contexts);
		}
		if (liquibaseProperties.isUpdateDatabaseToSql()) {
			liquibase().updateToSql(contexts);
		}
		
	}

	private boolean isNullOrEmpty(String contexts) {
		return contexts == null || contexts.isEmpty();
	}

	private boolean isNotNullAndNotEmpty(String rollbackToVersion) {
		return rollbackToVersion != null && !rollbackToVersion.isEmpty();
	}
}
