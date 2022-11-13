package keldkemp.telegram.logging;

import keldkemp.telegram.util.SecurityUtils;
import keldkemp.telegram.util.UuidUtils;
import net.ttddyy.dsproxy.ExecutionInfo;
import net.ttddyy.dsproxy.QueryInfo;
import net.ttddyy.dsproxy.listener.logging.DefaultQueryLogEntryCreator;
import net.ttddyy.dsproxy.proxy.ParameterSetOperation;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class SimpleQueryLogEntryCreator extends DefaultQueryLogEntryCreator {

    private static final String QUID = "QUID";
    private static final String USER = "User";
    private static final String QUOTE = "\"";

    private static final ThreadLocal<String> uuidHolder = new ThreadLocal<>();

    protected String getBeforeLogEntry(ExecutionInfo execInfo,
                                       List<QueryInfo> queryInfoList,
                                       boolean writeDataSourceName,
                                       boolean writeConnectionId) {
        // for performance purposes used notSecure
        uuidHolder.set(UuidUtils.notSecureRandomUuid().toString());
        return getLogEntry(execInfo, queryInfoList, writeDataSourceName, writeConnectionId, true);
    }

    protected String getAfterLogEntry(ExecutionInfo execInfo,
                                      List<QueryInfo> queryInfoList,
                                      boolean writeDataSourceName,
                                      boolean writeConnectionId) {
        String entry = getLogEntry(execInfo, queryInfoList, writeDataSourceName, writeConnectionId, false);
        uuidHolder.remove();
        return entry;
    }

    /**
     * getLogEntry.
     */
    public String getLogEntry(ExecutionInfo execInfo,
                              List<QueryInfo> queryInfoList,
                              boolean writeDataSourceName,
                              boolean writeConnectionId,
                              boolean isBefore) {
        final StringBuilder sb = new StringBuilder();

        // UUID
        writeUuidEntry(sb);

        if (isBefore) {
            if (writeDataSourceName) {
                writeDataSourceNameEntry(sb, execInfo, queryInfoList);
            }

            // User
            writeUserEntry(sb);

            if (writeConnectionId) {
                writeConnectionIdEntry(sb, execInfo, queryInfoList);
            }

            // Queries
            writeQueriesEntry(sb, execInfo, queryInfoList);

            if (execInfo.getBatchSize() > 0) {
                // BatchSize
                writeBatchSizeEntry(sb, execInfo, queryInfoList);
                //writeParamsEntry(sb, execInfo, queryInfoList);
            } else {
                // Params
                writeParamsEntry(sb, execInfo, queryInfoList);
            }
        } else {
            // Time
            writeTimeEntry(sb, execInfo, queryInfoList);

            // Success
            writeResultEntry(sb, execInfo, queryInfoList);
        }

        chompIfEndWith(sb, ' ');
        chompIfEndWith(sb, ',');

        return sb.toString();
    }

    /**
     * Write UUID query.
     *
     * @param sb StringBuilder to write
     */
    protected void writeUuidEntry(StringBuilder sb) {
        sb.append(QUID);
        sb.append(':');
        sb.append(uuidHolder.get());
        sb.append(", ");
    }

    /**
     * Write user query.
     *
     * @param sb StringBuilder to write
     */
    protected void writeUserEntry(StringBuilder sb) {
        sb.append(USER);
        sb.append(':');
        sb.append(SecurityUtils.getUser());
        sb.append(", ");
    }

    @Override
    protected void writeConnectionIdEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        sb.append("Conn:");
        sb.append(execInfo.getConnectionId());
        sb.append(", ");
    }

    @Override
    protected void writeResultEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        if (!execInfo.isSuccess()) {
            sb.append("Success:False");
        }
    }

    @Override
    protected void writeParamsEntry(StringBuilder sb, ExecutionInfo execInfo, List<QueryInfo> queryInfoList) {
        for (QueryInfo queryInfo : queryInfoList) {
            for (List<ParameterSetOperation> paramsList : queryInfo.getParametersList()) {
                if (!paramsList.isEmpty()) {
                    super.writeParamsEntry(sb, execInfo, queryInfoList);
                    return;
                }
            }
        }
    }

    /**
     * Get value to display.
     *
     * @param param parameter set operation
     * @return value to display
     * @since 1.4
     */
    @Override
    public String getDisplayValue(ParameterSetOperation param) {
        Object value = param.getArgs()[1];
        return value == null ? null : wrapString(value);
    }

    private String wrapString(Object value) {
        return value instanceof String ? StringUtils.wrap((String) value, QUOTE) : value.toString();
    }
}
