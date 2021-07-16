package com.restaurant.eatenjoy.util.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.restaurant.eatenjoy.util.type.DataSourceType;

public class ReplicationRoutingDatasource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		String transactionType = TransactionSynchronizationManager.isCurrentTransactionReadOnly() ? DataSourceType.READ : DataSourceType.WRITE;

		return transactionType;
	}
}
