/*
	Author: Elias (Gotzi) Gottsbacher
	Copyright (c) 2023 Elias Gottsbacher
*/

package net.gotzi.minestrum.api.logging;

import net.gotzi.minestrum.api.logging.LoggerInterface;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class LogDispatcher<T extends Logger & LoggerInterface> extends Thread {

    private final T logger;
    private final BlockingQueue<LogRecord> queue = new LinkedBlockingQueue<>();

    public LogDispatcher(T logger)
    {
        super( "logging-thread" );
        this.logger = logger;
    }

    @Override
    public void run()
    {
        while ( !isInterrupted() )
        {
            LogRecord record;
            try
            {
                record = queue.take();
            } catch ( InterruptedException ex )
            {
                continue;
            }

            logger.doLog( record );
        }
        for ( LogRecord record : queue )
        {
            logger.doLog( record );
        }
    }

    public void queue(LogRecord record)
    {
        if ( !isInterrupted() )
        {
            queue.add( record );
        }
    }
}
