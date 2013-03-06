/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.transport.nio.tcp;

import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.endpoint.OutboundEndpoint;
import org.mule.api.lifecycle.CreateException;
import org.mule.transport.AbstractMessageDispatcher;

/**
 * Send transformed Mule events over TCP.
 */
public class TcpMessageDispatcher extends AbstractMessageDispatcher
{
    protected final TcpConnector tcpConnector;

    public TcpMessageDispatcher(final OutboundEndpoint endpoint) throws CreateException
    {
        super(endpoint);
        tcpConnector = (TcpConnector) connector;
    }

    @Override
    public void doDispatch(final MuleEvent event) throws Exception
    {
        final TcpClient tcpClient = tcpConnector.borrowTcpClient(this, endpoint);
        tcpClient.dispatch(event);
    }

    @Override
    public MuleMessage doSend(final MuleEvent event) throws Exception
    {
        final TcpClient tcpClient = tcpConnector.borrowTcpClient(this, endpoint);
        return tcpClient.send(event);
    }
}