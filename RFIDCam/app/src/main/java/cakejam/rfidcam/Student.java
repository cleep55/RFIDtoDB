package cakejam.rfidcam;

/**
 * Created by wleeper on 4/7/2017.
 */

public class Student {
    private String rfid;
    private String name;

    public Student(){

    }

    public Student(String rfid_in, String name_in){
        rfid = rfid_in;
        name = name_in;
    }

    public String getRfid(){
        return rfid;
    }

    public String getName(){
        return name;
    }
}
