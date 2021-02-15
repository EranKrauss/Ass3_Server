package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.BGRSServer.bgrsEncoderDecoder;

import java.util.function.Supplier;

public class BGRSEncoderDecoderSupplier implements Supplier<bgrsEncoderDecoder> {
    @Override
    public bgrsEncoderDecoder get() {
        return new bgrsEncoderDecoder();
    }
}
