package com.ruoyi.service;

import com.shimizukenta.secs.SecsCommunicator;
import com.shimizukenta.secs.SecsException;
import com.shimizukenta.secs.SecsSendMessageException;
import com.shimizukenta.secs.SecsWaitReplyMessageException;
import com.shimizukenta.secs.gem.ClockType;
import com.shimizukenta.secs.hsms.HsmsConnectionMode;
import com.shimizukenta.secs.hsmsss.HsmsSsCommunicator;
import com.shimizukenta.secs.hsmsss.HsmsSsCommunicatorConfig;
import com.shimizukenta.secs.secs2.Secs2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class EqpThead extends Thread {

    private static final Logger log = LoggerFactory.getLogger(EqpThead.class);

    @Override
    public void run() {
        /* HSMS-SS-Passive open example */
        HsmsSsCommunicatorConfig config = new HsmsSsCommunicatorConfig();
        config.connectionMode(HsmsConnectionMode.PASSIVE);
        config.socketAddress(new InetSocketAddress("127.0.0.1", 5000));
        config.sessionId(0);
        config.isEquip(true);
        config.timeout().t3(10.0F);
        config.timeout().t6(5.0F);
        config.timeout().t7(10.0F);
        config.timeout().t8(5.0F);
        config.gem().mdln("MDLN-A");
        config.gem().softrev("000001");
        config.gem().clockType(ClockType.A16);

        SecsCommunicator passive = HsmsSsCommunicator.newInstance(config);
        try {
            passive.openAndWaitUntilCommunicatable();
            /* example */
            Secs2 secs2 = Secs2.list(               /* <L                       */
                    Secs2.binary((byte) 0x81),           /*   <B  0x81>              */
                    Secs2.uint2(1001),                  /*   <U2 1001>              */
                    Secs2.ascii("ON FIRE")              /*   <A  "ON FIRE">         */
            );                                      /* >.                       */
            passive.send(5, 1, true, secs2);

            passive.addSecsCommunicatableStateChangeListener((boolean communicatable)->{
                if ( communicatable ) {
                    System.out.println("communicatable");
                } else {
                    System.out.println("not communicatable");
                }
            });
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void main(String[] args) {
        EqpThead thread = new EqpThead();
        thread.start();
    }
}
