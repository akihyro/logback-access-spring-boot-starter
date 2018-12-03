package net.rakugakibox.spring.boot.logback.access;

import ch.qos.logback.access.spi.IAccessEvent;

public interface PortRewriteSupport extends IAccessEvent {

    void setUseServerPortInsteadOfLocalPort(boolean value);

}
