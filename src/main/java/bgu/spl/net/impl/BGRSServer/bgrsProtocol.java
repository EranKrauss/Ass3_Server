package bgu.spl.net.impl.BGRSServer;
import bgu.spl.net.api.MessagingProtocol;

public class bgrsProtocol implements MessagingProtocol<Message> {

    private boolean shouldTerminate = false;
    private User user = null;
    private Database database = Database.getInstance();



    public Message process(Message msg) {
        if (msg.getOpcode() == 1){return ADMINREG(msg);}
        else if (msg.getOpcode() == 2){return STUDENTREG(msg); }
        else if (msg.getOpcode() == 3){return LOGIN(msg); }
        else if (msg.getOpcode() == 4){return LOGOUT(msg); }
        else if (msg.getOpcode() == 5){return COURSEREG(msg); }
        else if (msg.getOpcode() == 6){return KDAMCHECK(msg); }
        else if (msg.getOpcode() == 7){return COURSESTAT(msg); }
        else if (msg.getOpcode() == 8){return STUDENTSTAT(msg); }
        else if (msg.getOpcode() == 9){return ISREGISTERED(msg); }
        else if (msg.getOpcode() == 10){return UNREGISTER(msg); }
        else{return MYCOURSES(msg);}

    }

    public boolean shouldTerminate() {
        return shouldTerminate;
    }


    //message 1
    private Message ADMINREG (Message msg ){
        Message m = new Message();
        short a = 1;
        m.setSecond(a);

        if (user != null && user.isLoggin()){m.setOpcode(13); return m;}

        String[] arr = msg.getData().split("\0"); //[username , password]
        boolean register = database.register(arr[0] , arr[1] , true);
        if (register){m.setOpcode(12);}
        else{m.setOpcode(13);}

        //System.out.println(database); //test
        return m;
    }
    //message 2
    private Message STUDENTREG (Message msg ){
        Message m = new Message();
        short a = 2;
        m.setSecond(a);
        if (user != null && user.isLoggin()){m.setOpcode(13); return m;}

        String[] arr = msg.getData().split("\0");
        boolean register = database.register(arr[0] , arr[1] , false);
        if (register){m.setOpcode(12);}
        else{m.setOpcode(13);}

        //System.out.println(database); //test
        return m;
    }
    //message 3
    private Message LOGIN (Message msg ) {
        String[] arr = msg.getData().split("\0");       //[username , password]
        Message m = new Message();
        short a = 3;
        m.setSecond(a);
        if (user == null || !user.isLoggin()){
            boolean login = database.login(arr[0], arr[1]);

            if (login){
                boolean isAdmin = database.isAdmin(arr[0]);
                if (isAdmin){user = new User(arr[0], arr[1], true);
                }
                else{user = new User(arr[0], arr[1], false);
                }
                user.setLoggin(true);
                m.setOpcode(12);
            }
            else {
                m.setOpcode(13);
            }
        }
        else {
            m.setOpcode(13);
        }
        //System.out.println(database); //test
        return m;
    }
    //message 4
    private Message LOGOUT (Message msg ){
        Message m = new Message();
        short a = 4;
        m.setSecond(a);
        boolean logout = false;
        if (user != null && user.isLoggin()){
            logout = database.logout(user.getUserName());
        }
        if (logout){
            m.setOpcode(12);
            user.setLoggin(false);
        }
        else{ m.setOpcode(13);}
        //System.out.println(database); //test
        return m;
    }
    //message 5
    private Message COURSEREG (Message msg ){
        Message m = new Message();
        short a = 5;
        m.setSecond(a);
        if(user != null && !user.isAdmin() && user.isLoggin()) {
            if(database.registerToCourse(user.getUserName() , msg.getData())){

                //System.out.println(database); //test
                m.setOpcode(12);
                return m;
            }
        }
        //System.out.println(database); //test
        m.setOpcode(13);
        return m;
    }
    //message 6
    private Message KDAMCHECK (Message msg ){
        Message m = new Message(12);
        short a = 6;
        m.setSecond(a);
        String data = "\n" + "[";
        int [] kdamList = database.getCourseKdamlist(msg.getData());
        if (kdamList == null){m.setOpcode(13); return m;}
        for(int i : kdamList){
            data += i + ",";
        }
        if(kdamList.length > 0) {
            data = data.substring(0, data.length() - 1);
        }
        data += "]";
        //System.out.println(database); //test
        m.setData(data);
        return m;
    }
    //message 7
    private Message COURSESTAT (Message msg ){

        Message m = new Message();
        short a = 7;
        m.setSecond(a);
        if (user != null && user.isAdmin() && user.isLoggin()){

            if (database.isCourseExsist(msg.getData())){
                String data = "\n" + "Course: (" +  msg.getData() + ") " + database.getCourseName(msg.getData()) + "\n"
                        + "Seats Available: " + database.getNumberOfAvailableSeats(Integer.parseInt(msg.getData())) + "/" + database.getCourseMaxNumOfStudents(msg.getData()) + "\n"
                        + "Students Registered: " + database.getCourseParticpants(msg.getData());

                //System.out.println(database); //test

                m.setOpcode(12);
                m.setData(data);
                return m;
            }
        }
        //System.out.println(database); //test
        m.setOpcode(13);
        return m;
    }
    //message 8
    private Message STUDENTSTAT (Message msg ){
        Message m = new Message();
        short a = 8;
        m.setSecond(a);
        if (!database.isRegisered(msg.getData().substring(0 , msg.getData().length() - 1))){
            m.setOpcode(13);
            return m;}
        if (user != null && user.isAdmin() && user.isLoggin()){
            String userName = msg.getData().substring(0 , msg.getData().length() - 1);
            String data = "\n" + "Student: " + userName + "\n" + "Courses: " + database.getClientCourseKdamlist(userName) ;

            //System.out.println(database); //test
            m.setOpcode(12);
            m.setData(data);
            return m;
        }
        //System.out.println(database); //test
        m.setOpcode(13);
        return m;
    }
    //message 9
    private Message ISREGISTERED (Message msg ){
        Message m = new Message();
        short a = 9;
        m.setSecond(a);
        String data = "\n";
        if (user != null && !user.isAdmin()){
            if (user.isLoggin()){
                if (database.isRegisterToCourse(user.getUserName(), msg.getData())){
                    data += "REGISTRED";
                }
                else{
                    data += "NOT REGISTRED";
                }
                m.setOpcode(12);
                m.setData(data);
                return m;
            }

        }
        //System.out.println(database); //test
        m.setOpcode(13);
        return m;

    }
    //message 10
    private Message UNREGISTER (Message msg ){
        Message m = new Message();
        short a = 10;
        m.setSecond(a);
        if (user != null && user.isLoggin() && !user.isAdmin()){
                boolean b = database.unregister(user.getUserName() , msg.getData());
                if (b){
                    //System.out.println(database); //test
                    m.setOpcode(12);
                    return m;
                }
        }
        //System.out.println(database); //test
        m.setOpcode(13);
        return m;
    }
    //message 11
    private Message MYCOURSES (Message msg ){
        Message m = new Message();
        short a = 11;
        m.setSecond(a);
        if (user != null && !user.isAdmin() && user.isLoggin()){
            String data = "\n" + database.getClientCourseKdamlist(user.getUserName());
            //System.out.println(database.toString()); //TEST
            m.setOpcode(12);
            m.setData(data);
            return m;
        }
        //System.out.println(database); //test
        m.setOpcode(13);
        return m;
    }








}
