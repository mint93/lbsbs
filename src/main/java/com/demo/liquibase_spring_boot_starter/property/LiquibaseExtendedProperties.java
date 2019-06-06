package com.demo.liquibase_spring_boot_starter.property;

import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;

//@ConfigurationProperties(prefix = "spring.liquibase.ext", ignoreUnknownFields = false)
public class LiquibaseExtendedProperties extends LiquibaseProperties {
	
	//private LiquibaseProperties liquibaseProperties;
	/**
	 * Change log configuration path.
	 */
	private String changeLog = "classpath:/db/changelog/db.changelog-master.xml";
	
	/**
	 * Version number to which rollback should be performed.
	 */
	private String rollbackToVersion;
	
//	/**
//	 * Version number to which changelog should be synchronized.
//	 */
//	private String syncChangeLogToVersion;
	
	/**
	 * Update database to the newest version.
	 */
	private boolean updateDatabase = false;
	
	/**
	 * Write SQL update scripts to file.
	 */
	private boolean updateDatabaseToSql = false;
	
	/**
	 * Generate changelog file based on current database.
	 */
	private boolean generateChangeLog = false;
	
	/**
	 * Synchronize changelog with database
	 */
	private boolean synchronizeChangeLog = false;

	public String getRollbackToVersion() {
		return rollbackToVersion;
	}

	public void setRollbackToVersion(String rollbackToVersion) {
		this.rollbackToVersion = rollbackToVersion;
	}

	public boolean isUpdateDatabase() {
		return updateDatabase;
	}

	public void setUpdateDatabase(boolean updateDatabase) {
		this.updateDatabase = updateDatabase;
	}

	public String getChangeLog() {
		return changeLog;
	}

	public void setChangeLog(String changeLog) {
		this.changeLog = changeLog;
	}

	public boolean isGenerateChangeLog() {
		return generateChangeLog;
	}

	public void setGenerateChangeLog(boolean generateChangeLog) {
		this.generateChangeLog = generateChangeLog;
	}

	public boolean isSynchronizeChangeLog() {
		return synchronizeChangeLog;
	}

	public void setSynchronizeChangeLog(boolean synchronizeChangeLog) {
		this.synchronizeChangeLog = synchronizeChangeLog;
	}

	public boolean isUpdateDatabaseToSql() {
		return updateDatabaseToSql;
	}

	public void setUpdateDatabaseToSql(boolean updateDatabaseToSql) {
		this.updateDatabaseToSql = updateDatabaseToSql;
	}
	

//	public String getSyncChangeLogToVersion() {
//		return syncChangeLogToVersion;
//	}
//
//	public void setSyncChangeLogToVersion(String syncChangeLogToVersion) {
//		this.syncChangeLogToVersion = syncChangeLogToVersion;
//	}
	

//	public LiquibaseProperties getLiquibaseProperties() {
//		return liquibaseProperties;
//	}
//
//	public void setLiquibaseProperties(LiquibaseProperties liquibaseProperties) {
//		this.liquibaseProperties = liquibaseProperties;
//	}

//	public String getContexts() {
//		return liquibaseProperties.getContexts();
//	}
	
}
