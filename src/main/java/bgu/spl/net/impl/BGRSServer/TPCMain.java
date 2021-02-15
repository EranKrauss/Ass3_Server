package bgu.spl.net.impl.BGRSServer;

import java.util.function.Supplier;

public class TPCMain {

    public static void main(String[] args) {

        if (args.length < 1){
            System.out.println("Port required");
        }
        else {
            //initialize database
            Database database = Database.getInstance();
            database.initialize("Courses.txt");

            //initialize TPC server
            TPCServer tpcServer = new TPCServer(Integer.parseInt(args[0]) , new BGRSProtocolSupplier()  , new BGRSEncoderDecoderSupplier());

            //TPC server start
            tpcServer.serve();
        }


    }
}

