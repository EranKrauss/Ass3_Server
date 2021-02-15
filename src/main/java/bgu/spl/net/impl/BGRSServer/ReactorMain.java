package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.srv.Reactor;

public class ReactorMain {

    public static void main (String [] args) {              //

            if (args.length < 1){
                System.out.println("Port required");
            }

            else {

                //initialize database
                Database database = Database.getInstance();
                database.initialize("Courses.txt");

                //initialize Reactor server
                Reactor reactor = new Reactor(10, Integer.parseInt(args[0])/*Integer.parseInt(args[1]), Integer.parseInt(args[0])*/,
                        new BGRSProtocolSupplier(), new BGRSEncoderDecoderSupplier());

                //server starts
                reactor.serve();
            }
    }
}
