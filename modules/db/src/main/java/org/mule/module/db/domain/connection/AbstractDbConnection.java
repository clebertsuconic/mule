/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.module.db.domain.connection;

import org.mule.module.db.domain.query.QueryTemplate;
import org.mule.module.db.domain.query.QueryType;
import org.mule.module.db.domain.transaction.TransactionalAction;
import org.mule.module.db.domain.type.DbType;
import org.mule.module.db.domain.type.DbTypeManager;
import org.mule.module.db.resolver.param.ParamTypeResolver;
import org.mule.module.db.resolver.param.ParamTypeResolverFactory;
import org.mule.module.db.resolver.param.QueryParamTypeResolver;
import org.mule.module.db.resolver.param.StoredProcedureParamTypeResolver;
import org.mule.module.db.result.resultset.ResultSetHandler;
import org.mule.module.db.result.statement.GenericStatementResultIteratorFactory;
import org.mule.module.db.result.statement.StatementResultIteratorFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * Implements connector side of {@link DbConnection}
 */
public abstract class AbstractDbConnection implements DbConnection
{

    private final TransactionalAction transactionalAction;
    private final DefaultDbConnectionReleaser connectionReleaseListener;
    private final ParamTypeResolverFactory paramTypeResolverFactory;
    protected final Connection delegate;

    public AbstractDbConnection(Connection delegate, TransactionalAction transactionalAction, DefaultDbConnectionReleaser connectionReleaseListener, ParamTypeResolverFactory paramTypeResolverFactory)
    {
        this.delegate = delegate;
        this.transactionalAction = transactionalAction;
        this.connectionReleaseListener = connectionReleaseListener;
        this.paramTypeResolverFactory = paramTypeResolverFactory;
    }

    @Override
    public StatementResultIteratorFactory getStatementResultIteratorFactory(ResultSetHandler resultSetHandler)
    {
        return new GenericStatementResultIteratorFactory(resultSetHandler);
    }

    @Override
    public Map<Integer, DbType> getParamTypes(QueryTemplate queryTemplate) throws SQLException
    {
        ParamTypeResolver paramTypeResolver = paramTypeResolverFactory.create(queryTemplate);

        return paramTypeResolver.getParameterTypes(this, queryTemplate);
    }

    @Override
    public TransactionalAction getTransactionalAction()
    {
        return transactionalAction;
    }

    @Override
    public void release()
    {
        connectionReleaseListener.release(this);
    }
}
