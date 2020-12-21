package com.ghc.cn.junit;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngineConfiguration;
import org.junit.Test;

public class TestDBCreated {
	@Test
	public void testJdbc() {
		ProcessEngineConfiguration config = ProcessEngineConfiguration.createStandaloneProcessEngineConfiguration();
		config.setJdbcDriver("com.mysql.jdbc.Driver");
		config.setJdbcUrl("jdbc:mysql://101.132.155.61:3306/activitileavedb?useUnicode=true&characterEncoding=UTF-8");
		config.setJdbcUsername("root");
		config.setJdbcPassword("czj1998");
		
		config.setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
		ProcessEngine engine = config.buildProcessEngine();
		System.out.println("engine :" + engine);
	}
	
	
}
