package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.BGRSServer.bgrsProtocol;

import java.util.function.Supplier;

public class BGRSProtocolSupplier implements Supplier<bgrsProtocol> {

    @Override
    public bgrsProtocol get() {
        return new bgrsProtocol();
    }
}
