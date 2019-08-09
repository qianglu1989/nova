package com.redefine.nove.bus.event;

import org.springframework.cloud.bus.event.RemoteApplicationEvent;

/**
 * @author QIANGLU
 */
public class NoveRefreshRemoteApplicationEvent extends RemoteApplicationEvent {

    public NoveRefreshRemoteApplicationEvent() {
    }

    public NoveRefreshRemoteApplicationEvent(Object source, String originService,
                                             String destinationService) {
        super(source, originService, destinationService);
    }
}
