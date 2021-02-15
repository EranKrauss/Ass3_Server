package bgu.spl.net.impl.BGRSServer;

public class User {

    private String userName;
    private String password;
    private boolean loggin;
    private boolean admin;

    //constructors
    public User(){
        userName = null;
        password = null;
        loggin = false;
        admin = false;
    }
    public User(String userName , String password , boolean isAdmin){
        this.userName = userName;
        this.password = password;
        this.loggin = false;
        this.admin = isAdmin;

    }
    //setters
    public void setUserName(String name){
        userName = name;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setLoggin(boolean b){
        loggin = b;
    }
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
    //getters
    public String getUserName(){return userName;}
    public String getPassword(){return password;}
    //other functions
    public boolean isLoggin(){return loggin;}
    public boolean isAdmin() {return admin;}
}
