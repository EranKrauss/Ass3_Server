package bgu.spl.net.impl.BGRSServer;
import bgu.spl.net.api.MessageEncoderDecoder;


import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class bgrsEncoderDecoder implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private byte[] extraBitesHolder = new byte[2];
    private int len = 0;
    private Short currentOpcode = (-1);
    private int countZero = 0;


    @Override
    public Message decodeNextByte(byte nextByte) {
        if (len > 2 && nextByte == '\0'){countZero++;}


        pushByte(nextByte);


        if (len < 2){return null;}

        else if (len == 2){
            byte[] curr = new byte[2];
            curr[0] = bytes[0];
            curr[1] = bytes[1];
            currentOpcode = bytesToShort(curr);
            if (currentOpcode == 4 ){
                clear();
                return new Message(4);
            }
            else if(currentOpcode == 11){
                clear();
                return new Message(11);
            }
            return null;
        }


        else{
            if (currentOpcode == 1 ||currentOpcode == 2 ||currentOpcode == 3 ){return CASE1(nextByte);}
            else if (currentOpcode == 5||currentOpcode == 6 ||currentOpcode == 7 ||currentOpcode == 9 ||currentOpcode == 10){return CASE2(nextByte);}
            else{return CASE3(nextByte);}
        }
    }
    @Override
    public byte[] encode(Message message) {
        short opcode = message.getOpcodeShort();
        byte[] arr1 = shortToByte(opcode);
        byte[] arr2 = shortToByte(message.getSecond());
        byte[] arr3 = new byte[0];

        String tmp = message.getData();
        if (tmp != null &&tmp.length() != 0){
            arr3 = tmp.getBytes(StandardCharsets.UTF_8);
        }

        byte[] last;
        int ind = 0;
        if (opcode == 12){last = new byte[5 + arr3.length]; last[last.length - 1] = '\0';}
        else{last = new byte[4];}
        for (int i = 0 ; i < arr1.length ; i++){
            last[ind] = arr1[i];
            ind ++;
        }
        for (int i = 0 ; i < arr2.length ; i++){
            last[ind] = arr2[i];
            ind ++;
        }
        for (int i = 0; i < arr3.length; i++) {
                last[ind] = arr3[i];
                ind++;
        }

        return last;
    }



    private byte[] shortToByte(short num){
        byte[] arr = new byte[2];
        arr[0] = (byte)((num >> 8) & 0xFF);
        arr[1] = (byte)(num & 0xFF);
        return arr;
    }
    private short bytesToShort(byte[] arr){
        short result = (short)((arr[0] & 0xff) << 8);
        result += (short)(arr[1] & 0xff);
        return result;
    }
    private void clear(){
        len = 0;
        currentOpcode = 0;
        countZero = 0;
    }
    private void pushByte(byte nextByte) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len] = nextByte;
        len++;
    }



    //ADMINREG , STUDENTREG , LOGIN
    private Message CASE1(byte nextByte){
        if (countZero == 2){
            String data = new String(bytes, 2, len - 2, StandardCharsets.UTF_8);
            Message m = new Message(currentOpcode , data);
            clear();
            return m;
        }
        return null;
    }
    //COURSEREG , KDAMCHECK , COURSESTAT , ISREGISTERED , UNREGISTER
    private Message CASE2(byte nextByte){
        if (len == 4){
            byte[] tmp = new byte[2];
            tmp[0] = bytes[2];
            tmp[1] = bytes[3];


            short a = bytesToShort(tmp);
            //test
            int b = currentOpcode;
            Message m = new Message(b , Short.toString(a));
            m.setSecond(a);

            clear();
            return m;
        }
        return null;
    }
    //STUDENTSTAT
    private Message CASE3(byte nextByte){
        if (countZero == 1){
            String data = new String(bytes, 2, len - 2, StandardCharsets.UTF_8);
            Message m = new Message(currentOpcode , data);
            clear();
            return m;
        }
        return null;
    }
}
