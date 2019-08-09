package com.redefine.nove.event;

import com.redefine.nove.bus.event.NoveRefreshRemoteApplicationEvent;
import com.redefine.nove.bus.refresh.NoveContextRefresher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

import java.util.Set;
/**
 * @author QIANGLU
 */
public class NoveRefreshListener implements ApplicationListener<NoveRefreshRemoteApplicationEvent> {

    private static Logger log = LoggerFactory.getLogger(NoveRefreshListener.class);

    private NoveContextRefresher noveContextRefresher;

    public NoveRefreshListener(NoveContextRefresher noveContextRefresher) {
        this.noveContextRefresher = noveContextRefresher;
    }


    @Override
    public void onApplicationEvent(NoveRefreshRemoteApplicationEvent event) {
        Set<String> keys = noveContextRefresher.refresh();
        log.info("NoveReceived remote refresh request. Keys refreshed " + keys);
    }


}
