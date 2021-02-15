package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.BaseServer;
import bgu.spl.net.srv.BlockingConnectionHandler;

import java.util.function.Supplier;

public class TPCServer extends BaseServer {


    public TPCServer(int port, Supplier protocolFactory, Supplier encdecFactory) {
        super(port, protocolFactory, encdecFactory);

    }

    @Override
    protected void execute(BlockingConnectionHandler handler) {
//        System.out.println("EXECUTE    !!!!!!!!!!!!!!!!!!!!!!!!!!");
        new Thread(handler).start();
    }
}
