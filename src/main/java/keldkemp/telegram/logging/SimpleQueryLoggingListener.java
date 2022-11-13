package keldkemp.telegram.logging;

import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.SLF4JQueryLoggingListener;
import org.slf4j.LoggerFactory;

import java.util.List;

public class SimpleQueryLoggingListener extends SLF4JQueryLoggingListener {

    protected final SimpleQueryLogEntryCreator queryLogEntryCreator;

    /**
     * Ctor.
     */
    public SimpleQueryLoggingListener() {
        logger = LoggerFactory.getLogger(SimpleQueryLoggingListener.class);
        queryLogEntryCreator = new SimpleQueryLogEntryCreator();
        setWriteDataSourceName(false);
    }

    @Override
    public void beforeQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        String entry = queryLogEntryCreator.getBeforeLogEntry(execInfo, queryInfoList, writeDataSourceName,
                writeConnectionId);
        writeLog(entry);
    }

    @Override
    public void afterQuery(ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        String entry = queryLogEntryCreator.getAfterLogEntry(execInfo, queryInfoList, writeDataSourceName,
                writeConnectionId);
        writeLog(entry);
    }
}
