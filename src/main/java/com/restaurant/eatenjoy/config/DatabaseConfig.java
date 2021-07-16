package com.restaurant.eatenjoy.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.SpringBootVFS;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.restaurant.eatenjoy.util.datasource.ReplicationRoutingDatasource;
import com.restaurant.eatenjoy.util.type.DataSourceType;

@Configuration
@Profile("!default")
@EnableTransactionManagement
@MapperScan("com.restaurant.eatenjoy.dao")
public class DatabaseConfig {

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.master")
	public DataSource master() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	@ConfigurationProperties(prefix = "spring.datasource.slave")
	public DataSource slave() {
		return DataSourceBuilder.create().build();
	}

	@Bean
	public DataSource routingDatasource(@Qualifier("master") DataSource master, @Qualifier("slave") DataSource slave) {
		ReplicationRoutingDatasource replicationRoutingDatasource = new ReplicationRoutingDatasource();

		Map<Object, Object> datasource = new HashMap<>();
		datasource.put(DataSourceType.WRITE, master);
		datasource.put(DataSourceType.READ, slave);

		replicationRoutingDatasource.setTargetDataSources(datasource);
		replicationRoutingDatasource.setDefaultTargetDataSource(master);

		return replicationRoutingDatasource;
	}

	@Bean
	public DataSource dataSource(@Qualifier("routingDatasource") DataSource routingDataSource) {
		return new LazyConnectionDataSourceProxy(routingDataSource);
	}

	@Bean
	public PlatformTransactionManager transactionManager(@Qualifier("dataSource") DataSource dataSource) {
		DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
		transactionManager.setDataSource(dataSource);

		return transactionManager;
	}

	@Bean
	public SqlSessionFactory sqlSessionFactory(@Qualifier("master") DataSource master, @Qualifier("slave") DataSource slave) throws Exception {
		SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
		sqlSessionFactory.setDataSource(routingDatasource(master, slave));
		sqlSessionFactory.setTypeAliasesPackage("com.restaurant.eatenjoy.dto");
		sqlSessionFactory.setMapperLocations(
			new PathMatchingResourcePatternResolver().getResources("classpath:mapper/*.xml"));

		return sqlSessionFactory.getObject();
	}

	@Bean
	public SqlSessionTemplate sqlSession(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) throws Exception {
		return new SqlSessionTemplate(sqlSessionFactory);
	}
}
