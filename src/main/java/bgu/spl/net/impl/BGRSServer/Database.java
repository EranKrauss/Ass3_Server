package bgu.spl.net.impl.BGRSServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Passive object representing the Database where all courses and users are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add private fields and methods to this class as you see fit.
 */
public class Database {

    private ConcurrentHashMap<String , String> courseName;                    //course number , course name
    private ConcurrentHashMap<String , int[]> courseKdamList;                 //course number , Kdam courses list
    private ConcurrentHashMap<String , Integer[] > courseMaxNumOfStudent;     //course number , [0] -max num of student , [1] - num of stude  nt in course
    private ConcurrentHashMap<String, Queue<String>> courseParticipants;      // course number , participants
    private ConcurrentHashMap<String , String> clients;                       //username , password
    private ConcurrentHashMap<String , String> admins;                        //username , password
    private ConcurrentHashMap<String , Queue<String>> clientsCourses;
    private Set<String> connected;                                            //username , courses
    private Object lock1 , lock2 , lock3;
    private AtomicInteger count = new AtomicInteger(0); // test
    private int countlogin = 0; // test



    private static class singeltonHolder{
        private static Database instance = new Database();
    }
    private Database() {
        this.courseName = new ConcurrentHashMap<>();
        this.courseKdamList = new ConcurrentHashMap<>();
        this.courseMaxNumOfStudent = new ConcurrentHashMap<>();
        this.clients = new ConcurrentHashMap<>();
        this.admins = new ConcurrentHashMap<>();
        this.courseParticipants = new ConcurrentHashMap<>();
        this.clientsCourses = new ConcurrentHashMap<>();
        this.connected = new TreeSet<>();
        lock1 = new Object();
        lock2 = new Object();
        lock3 = new Object();
        //this.connecedClients = new HashMap<>();
    }
    public static Database getInstance() {
        return singeltonHolder.instance;
    }
    boolean initialize(String coursesFilePath) {
        try {

            File file = new File(coursesFilePath);
            BufferedReader br = new BufferedReader(new FileReader(file));

            String st;
            while ((st = br.readLine()) != null) {
                if (st == ""){continue;}
                String[] curr = st.split("\\|");
                this.courseName.put(curr[0], curr[1]);
                this.courseParticipants.put(curr[0] , new LinkedList<>());
                Integer[] currArr = new Integer[2];
                currArr[0] = new Integer(curr[3]);
                currArr[1] = 0;
                this.courseMaxNumOfStudent.put(curr[0], currArr);

                String kdam = curr[2].substring(1, curr[2].length() - 1);
                String[] kdamArr = kdam.split(",");
                int[] kdamIntArr;
                if (kdamArr.length > 0 && !kdamArr[0].equals("")) {
                    kdamIntArr = new int[kdamArr.length];
                    for (int i = 0; i < kdamArr.length; i++) {
                        kdamIntArr[i] = (new Integer(kdamArr[i]));
                    }
                }
                else{
                    kdamIntArr = new int[0];
                }

                this.courseKdamList.put(curr[0], kdamIntArr);
            }
            return true;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }

    }


    //getters
    public synchronized  String getCourseName (String courseNum){
        return this.courseName.get(courseNum);
    }
    public synchronized  int[] getCourseKdamlist (String courseNum){      //TODO
        return this.courseKdamList.get(courseNum);
    }
    public synchronized  int getCourseMaxNumOfStudents (String courseNum){
        return this.courseMaxNumOfStudent.get(courseNum)[0];
    }
    public synchronized  String getCourseName (int courseNum){
        return this.courseName.get(Integer.toString(courseNum));
    }
    public synchronized  String getNumberOfAvailableSeats (int courseNum) {
        Integer num = this.courseMaxNumOfStudent.get(Integer.toString(courseNum))[0] -// - this.courseParticipants.get(())
        this.courseMaxNumOfStudent.get(Integer.toString(courseNum))[1];
        return num.toString();
    }
    public synchronized  String getCourseParticpants(String courseNum){
        Queue<String> queue =  this.courseParticipants.get(courseNum);
        if(queue == null || queue.size() == 0){
            return "[]";
        }
        String[] particpants = new String[queue.size()];
        Iterator<String> itr = queue.iterator();
        int ind = 0;
        while(itr.hasNext()){
            particpants[ind] = itr.next();
            ind++;
        }
        //sorting by name
        Arrays.sort(particpants);
        String s = "[";
        for (String x : particpants){
            s += x + ",";
        }
        if (particpants.length > 0){
            s = s.substring(0 , s.length() - 1);
        }
        s += "]";
        return s;
    }
    public synchronized String getClientCourseKdamlist(String userName){
        //synchronized (lock2) {
            Queue<String> queue = this.clientsCourses.get(userName);
            if (queue == null || queue.size() == 0) {
                return "[]";
            }
            Iterator<String> itr = queue.iterator();
            String s = "[";
            while (itr.hasNext()) {
                s += itr.next() + ",";
            }
            if (queue.size() > 0) {
                s = s.substring(0, s.length() - 1);
            }
            s += "]";
            return s;
       // }
    }



    public synchronized boolean isAdmin(String userName){
        if(this.admins.get(userName) == null){
            return false;
        }
        return true;
    }
    public synchronized  boolean isRegisered(String userName){
        //synchronized (lock3) {
            if (this.clients.get(userName) == null && this.admins.get(userName) == null) {
                return false;
            }
            return true;
        //}
    }
    public synchronized  boolean isPasswordMatch(String userName , String password){
        if (isRegisered(userName)){
            if (clients.get(userName) != null) {
                return clients.get(userName).equals(password);
            }
            else if (admins.get(userName) != null){
                return admins.get(userName).equals(password);
            }
        }
        return false;
    }



    public synchronized  boolean canRegisterToCourse(String userName , String wantedCourse) {

        boolean registered = true;
        Queue<String> kdamCourses = this.clientsCourses.get(userName);
        if(kdamCourses.contains(wantedCourse)){return false;}       //allready register to this course
        if (this.courseName.get(wantedCourse) != null) {
            int[] wantedCourseKdamCoursesList = this.courseKdamList.get((wantedCourse));        //kdam courses of the wanted course

            //check if user complete all kdam courses
            for (int course : wantedCourseKdamCoursesList){
                //System.out.println(course);
                boolean found = false;
                for (String s : kdamCourses){
                   // System.out.println(s);
                    if (Integer.toString(course).equals(s) ){
                        found = true;
                        break;
                    }
                }
                if (!found){
                    //System.out.println("test 111111111111111111111111111111");
                    return false;}
            }

            //if he have all courses - check if there is spot left in the course
            if (registered) {
                registered = this.courseMaxNumOfStudent.get(wantedCourse)[0] - this.courseMaxNumOfStudent.get(wantedCourse)[1] > 0;
            }
        }
        else {
            registered = false;
        }
        return registered;
    }
    public synchronized boolean isCourseExsist(String courseNum){
        return this.courseName.get(courseNum) != null;
    }
    public synchronized boolean login(String userName , String password ){
        //synchronized (connected) {
            countlogin++;
            if (isRegisered(userName) && isPasswordMatch(userName , password) && !connected.contains(userName)) {
                this.connected.add(userName);
                return true;
            }
            return false;
        //}
    }
    public synchronized boolean logout(String userName){
        //synchronized (connected) {
            //System.out.println(count);
            if (connected.size() == 0){return false;}
            else{return this.connected.remove(userName);}
       // }
    }
    public synchronized  boolean isAnyoneConnect(){
        //synchronized (connected) {
            return this.connected.size() > 0;
        //}
    }
    public synchronized  boolean register(String userName , String password , boolean isAdmin){
        //synchronized (lock1) {
            if (isRegisered(userName)){return false;}

            if (isAdmin) {
                this.admins.put(userName, password);
            } else {
                this.clients.put(userName, password);
                this.clientsCourses.put(userName, new LinkedList<String>());
            }
            return true;
        //}
    }

    public synchronized boolean registerToCourse(String userName , String wantedCourse){
        //synchronized (lock2) {
            if (canRegisterToCourse(userName, wantedCourse)) {
                //add it to course (remove 1 from max num of student)
                String wantedCourseAsString = (wantedCourse);
                Integer[] currArr = new Integer[2];
                currArr[0] = this.courseMaxNumOfStudent.get(wantedCourseAsString)[0];
                currArr[1] = this.courseMaxNumOfStudent.get(wantedCourseAsString)[1] + 1;

                this.courseMaxNumOfStudent.put(wantedCourseAsString, currArr);

                Queue<String> courseParticipantsQueue = this.courseParticipants.get(wantedCourseAsString);
                if (courseParticipantsQueue == null) {
                    courseParticipantsQueue = new LinkedList<String>();
                    courseParticipantsQueue.add(userName);
                    this.courseParticipants.put(wantedCourseAsString, courseParticipantsQueue);
                } else {
                    courseParticipantsQueue.add(userName);
                }

                Queue<String> studentCourses = this.clientsCourses.get(userName);
                if (studentCourses == null) {
                    studentCourses = new LinkedList<>();
                    studentCourses.add(wantedCourseAsString);
                    this.clientsCourses.put(userName, studentCourses);
                } else {
                    studentCourses.add(wantedCourseAsString);
                }
            } else {
                return false;
            }
            return true;
       // }
    }
    public synchronized boolean unregister(String userName , String courseNum){
        //synchronized (lock2) {
            if (this.getCourseParticpants(courseNum).contains(userName)) {
                this.courseParticipants.get(courseNum).remove(userName);
                this.clientsCourses.get(userName).remove(courseNum);
                this.courseMaxNumOfStudent.get(courseNum)[1]--;
                return true;
            }
            return false;
        //}
    }
    public synchronized boolean isRegisterToCourse(String userName , String wantedCourse) {
       // synchronized (lock2) {
            if (isCourseExsist(wantedCourse) && courseParticipants.get(wantedCourse).contains(userName)) {
                return true;
            }
            return false;
       // }
    }

    public String toString(){
        String dataBaseAsString = "database: " + "\n";


        dataBaseAsString += "1. clients: " + clients.size() + "\n";
        for (String k : clients.keySet()){
            dataBaseAsString += k + ": " + clients.get(k) + " , ";
        }
        dataBaseAsString += "\n" + "2. admins: " + admins.size() + "\n";
        for (String k : admins.keySet()){
            dataBaseAsString += k + ": " + admins.get(k) + " , ";
        }
        dataBaseAsString += "\n" + "3. clientCourses" + "\n";
        for (String k : clientsCourses.keySet()){
            dataBaseAsString += k + ": ";
            for (String course : clientsCourses.get(k)){
                dataBaseAsString += course + " , ";
            }
            dataBaseAsString += "\n";
        }
        dataBaseAsString += "\n" + "4. courseParticipants" + "\n";
        for (String k : courseParticipants.keySet()){
            dataBaseAsString += k + ": ";
            for (String course : courseParticipants.get(k)){
                dataBaseAsString += course + " , ";
            }
            dataBaseAsString += "\n";
        }
        dataBaseAsString += "\n" + "5. courseName" + "\n";
        for (String k : courseName.keySet()){
            dataBaseAsString += k + ": " + courseName.get(k) + " , ";
        }

        dataBaseAsString += "\n" + "6. courseKdamList" + "\n";
        for (String k : courseKdamList.keySet()){
            dataBaseAsString += k + ": ";
            for (int course : courseKdamList.get(k)){
                dataBaseAsString += course + " , ";
            }
            dataBaseAsString += "\n";
        }
        dataBaseAsString += "\n" + "7. courseMaxNumOfStudent" + "\n";
        for (String k : courseMaxNumOfStudent.keySet()){
            dataBaseAsString += k + ": ";
            for (int course : courseMaxNumOfStudent.get(k)){
                dataBaseAsString += course + " , ";
            }
            dataBaseAsString += "\n";
        }
        dataBaseAsString += "\n" + "8. connected : " + connected.size() + "\n";
        for (String k : connected){
            dataBaseAsString += k + " , ";
        }

        return dataBaseAsString;
    }



}



