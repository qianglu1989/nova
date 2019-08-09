package com.redefine.nove.bus.refresh;

import com.redefine.nove.bus.event.NoveRefreshRemoteApplicationEvent;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.boot.actuate.endpoint.web.annotation.WebEndpoint;
import org.springframework.cloud.bus.endpoint.AbstractBusEndpoint;
import org.springframework.context.ApplicationEventPublisher;
/**
 * @author QIANGLU
 */
@WebEndpoint(id = "nove-refresh")
public class NoveRefreshBusEndpoint extends AbstractBusEndpoint {

    public NoveRefreshBusEndpoint(ApplicationEventPublisher context,  String id) {
        super(context, id);
    }



    @WriteOperation
    public void busNoveRefresh() {
        publish(new NoveRefreshRemoteApplicationEvent(this, getInstanceId(),null));
    }

    @WriteOperation
    public void busRefreshWithDestination(@Selector String arg0) {
        publish(new NoveRefreshRemoteApplicationEvent(this, getInstanceId(), arg0));
    }
}
