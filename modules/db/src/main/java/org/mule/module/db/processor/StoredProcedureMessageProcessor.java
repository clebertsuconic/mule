/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.processor;

import org.mule.module.db.domain.autogeneratedkey.NoAutoGeneratedKeyStrategy;
import org.mule.module.db.resolver.query.QueryResolver;
import org.mule.module.db.domain.executor.QueryExecutor;
import org.mule.module.db.domain.query.Query;
import org.mule.module.db.domain.query.QueryType;
import org.mule.module.db.resolver.database.DbConfigResolver;
import org.mule.module.db.domain.connection.DbConnection;
import org.mule.module.db.domain.autogeneratedkey.AutoGeneratedKeyStrategy;
import org.mule.module.db.domain.executor.QueryExecutorFactory;
import org.mule.module.db.domain.transaction.TransactionalAction;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Executes an stored procedure query on a database.
 * <p/>
 * Both database and queries are resolved, if required, using the {@link org.mule.api.MuleEvent} being processed.
 */

public class StoredProcedureMessageProcessor extends AbstractSingleQueryDbMessageProcessor
{

    private final QueryExecutorFactory queryExecutorFactory;
    private final boolean streaming;
    private final List<QueryType> queryTypes;
    private AutoGeneratedKeyStrategy autoGeneratedKeyStrategy;

    public StoredProcedureMessageProcessor(DbConfigResolver dbConfigResolver, QueryResolver queryResolver, QueryExecutorFactory queryExecutorFactory, TransactionalAction transactionalAction, boolean streaming)
    {
        super(dbConfigResolver, queryResolver, transactionalAction);
        this.queryExecutorFactory = queryExecutorFactory;
        this.streaming = streaming;
        this.queryTypes = Collections.singletonList(QueryType.STORE_PROCEDURE_CALL);
        this.autoGeneratedKeyStrategy = new NoAutoGeneratedKeyStrategy();
    }

    @Override
    protected boolean mustCloseConnection()
    {
        return !streaming;
    }

    @Override
    protected Object doExecuteQuery(DbConnection connection, Query query) throws SQLException
    {
        QueryExecutor queryExecutor = queryExecutorFactory.create();
        return queryExecutor.execute(connection, query, autoGeneratedKeyStrategy);
    }

    @Override
    protected List<QueryType> getValidQueryTypes()
    {
        return queryTypes;
    }

    public void setAutoGeneratedKeyStrategy(AutoGeneratedKeyStrategy autoGeneratedKeyStrategy)
    {
        this.autoGeneratedKeyStrategy = autoGeneratedKeyStrategy;
    }
}
