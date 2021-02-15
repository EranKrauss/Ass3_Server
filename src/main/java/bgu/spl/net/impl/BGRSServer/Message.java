package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.impl.BGRSServer.Database;

public class Message {

    private int opcode;
    private String data;
    private short second;


    public Message(){
        opcode = 0;
        data = null;
        second = 0;
    }
    public Message(int opcode , String data){
        this.opcode = opcode;
        this.data = data;
    }
    public Message(int opcode){
        this.opcode = opcode;
        data = null;
    }

    //getters
    public int getOpcode(){return opcode;}
    public String getData(){return data;}
    public short getOpcodeShort(){
        return (short)opcode;
    }
    public short getSecond(){return second;}

    //stters
    public void setOpcode(int n){this.opcode = n;}
    public void setData(String d){
        this.data = d;
    }
    public void setSecond(short s){second = s;}

    //function
    public String toString(){
        String opcode;
        if (this.opcode < 10){
            opcode = "0" + this.opcode;
        }
        else{
            opcode = Integer.toString(this.opcode);
        }
        String messageAsString = opcode + data;
        return messageAsString;
    }

}
