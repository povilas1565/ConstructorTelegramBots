package keldkemp.telegram.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public enum LocalhostUtils {
    INSTANCE;

    private String hostName;
    private String hostAddress;

    /**
     * Gets the host name for this IP address.
     *
     * @return the host name for this IP address.
     */
    public String getHostName() {
        if (hostName != null) {
            return hostName;
        }

        return getHostNameInternal();
    }

    private synchronized String getHostNameInternal() {
        if (hostName != null) {
            return hostName;
        }
        return hostName = getLocalHost().getHostName();
    }

    /**
     * Returns the IP address string in textual presentation.
     *
     * @return the raw IP address in a string format.
     */
    public String getHostAddress() {
        if (hostAddress != null) {
            return hostAddress;
        }

        return getHostAddressInternal();
    }

    private synchronized String getHostAddressInternal() {
        if (hostAddress != null) {
            return hostAddress;
        }
        return hostAddress = getLocalHost().getHostAddress();
    }

    /**
     * Returns the address of the local host. This is achieved by retrieving
     * the name of the host from the system, then resolving that name into
     * an {@code InetAddress}.
     *
     * @return the address of the local host or {@code null}.
     */
    public InetAddress getLocalHost() {
        try {
            return InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException("Could not get host name", e);
        }
    }
}
