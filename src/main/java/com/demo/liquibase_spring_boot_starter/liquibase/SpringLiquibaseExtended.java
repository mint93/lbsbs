package com.demo.liquibase_spring_boot_starter.liquibase;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import liquibase.CatalogAndSchema;
import liquibase.Liquibase;
import liquibase.change.core.TagDatabaseChange;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.diff.output.DiffOutputControl;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.integration.commandline.CommandLineUtils;
import liquibase.integration.spring.SpringLiquibase;

public class SpringLiquibaseExtended extends SpringLiquibase {
	
	Logger logger = LoggerFactory.getLogger(SpringLiquibaseExtended.class);
	
	public void rollbackToVersion(String version, String contexts) {
		perfromOnLiquibase(liquibase -> {
			try {
				DatabaseChangeLog databaseChangeLog = liquibase.getDatabaseChangeLog();
				Optional<ChangeSet> versionChangeset = databaseChangeLog.getChangeSets().stream()
					.filter(changeSet -> changeSetContainsTag(changeSet, version))
					.findFirst();
				if (versionChangeset.isPresent()) {
					liquibase.rollback(version, "");	// run ALL contexts during rollback
					liquibase.getDatabaseChangeLog().addChangeSet(versionChangeset.get());
					liquibase.update(version, contexts);
				} else {
					logger.error("No tag database found with id " + version);
				}
			} catch (LiquibaseException e) {
				logger.error("Error during rollback procedure");
				e.printStackTrace();
			}
		});
	}
	private boolean changeSetContainsTag(ChangeSet changeSet, String tag) {
		return changeSet.getChanges().stream()
				.filter(change -> change instanceof TagDatabaseChange)
				.map(change -> (TagDatabaseChange) change)
				.filter(tagDatabaseChange -> tagDatabaseChange.getTag().equals(tag))
				.findFirst()
				.isPresent();		
	}
	
	public void generateChangelog(String contexts) {
		perfromOnLiquibase(liquibase -> {
			try {
				Database database = liquibase.getDatabase();
				CatalogAndSchema[] catalogAndSchema = {new CatalogAndSchema(database.getDefaultCatalogName(), database.getDefaultSchemaName())};
				//DiffOutputControl diffOutputControl = new DiffOutputControl(false, false, false, null);
				CommandLineUtils.doGenerateChangeLog(generateFileNameWithTimestamp("changelog", "xml"), database, catalogAndSchema, null, getSystemUsername(), contexts, null, new DiffOutputControl());
			} catch (IOException | ParserConfigurationException | LiquibaseException e) {
				logger.error("Error during changelog generation");
				e.printStackTrace();
			}
		});
	}
	
	public void updateToSql(String contexts) {
		perfromOnLiquibase(liquibase -> {
			try (FileWriter fileWriter = new FileWriter("." + File.separator + generateFileNameWithTimestamp("updateSql", "sql"))) {	
				liquibase.update(contexts, fileWriter);
			} catch (IOException e) {
				logger.error("Error during writing to file");
				e.printStackTrace();
			} catch (LiquibaseException e) {
				logger.error("Error during updating database to SQL file");
				e.printStackTrace();
			}
		});
	}
	
	private String generateFileNameWithTimestamp(String fileName, String fileType) {
		String currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		return fileName + "_" + currentDateTime + "." + fileType;
	}
	private String getSystemUsername() {
		String systemUsername = System.getProperty("user.name");
		if (systemUsername == null || systemUsername.isEmpty()) {
			systemUsername = "authorName";
		}
		return systemUsername;
	}
	
	public void synchronizeChangelog(String contexts) {
		perfromOnLiquibase(liquibase -> {
//			try {
//				liquibase.changeLogSync(contexts);
//				rollbackToVersion(version, contexts);
//			} catch (LiquibaseException e) {
//				logger.error("Error during synchronization of changelog procedure");
//				e.printStackTrace();
//			}
			try {
				liquibase.changeLogSync(contexts);
				DatabaseChangeLog databaseChangeLog = liquibase.getDatabaseChangeLog();
				List<TagDatabaseChange> tagDatabaseChanges = databaseChangeLog.getChangeSets().stream()
					.flatMap(changeSet -> changeSet.getChanges().stream())
					.filter(change -> change instanceof TagDatabaseChange)
					.map(change -> (TagDatabaseChange)change)
					.collect(Collectors.toList());
				// Remove unnecessary changeSets
				if (tagDatabaseChanges.size() > 1) {
					liquibase.rollback(tagDatabaseChanges.get(1).getTag(), "");
				}
			} catch (LiquibaseException e) {
				e.printStackTrace();
			}
		});
	}
	
	private void perfromOnLiquibase(Consumer<Liquibase> action) {
		Liquibase liquibase = null;
		Connection connection = null;
		try {
			connection = getDataSource().getConnection();
            liquibase = createLiquibase(connection);
            action.accept(liquibase);
		} catch (SQLException e) {
			logger.error("Error while getting connection to database");
			e.printStackTrace();
		} catch (LiquibaseException e) {
			logger.error("Error while creating liquibase instance");
			e.printStackTrace();
		} finally {
			Database database = null;
			if (liquibase != null) {
				database = liquibase.getDatabase();
			}
			if (database != null) {
                try {
					database.close();
				} catch (DatabaseException e) {
					logger.error("Error while closing database");
					e.printStackTrace();
				}
            }
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					logger.error("Error while closing connection to database");
					e.printStackTrace();
				}
			}
        }
	}
	
}