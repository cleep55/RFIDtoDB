package cakejam.rfidcam;

import java.util.Date;
import java.util.StringTokenizer;

/**
 * Created by wleeper on 4/6/2017.
 */

public class Swipe {
    private String rfid;
    private String swipeDate;
    private String name;

    public Swipe(){

    }

    public Swipe(String rfid_in, String date_in){
        rfid = rfid_in;
        swipeDate = date_in;
    }

    public Swipe(String rfid_in, String date_in, String name_in){
        rfid = rfid_in;
        swipeDate = date_in;
        name = name_in;
    }

    public String getRfid(){
        return rfid;
    }

    public String getSwipeDate(){
        return swipeDate;
    }

    public String getName(){ return name; }
//    private String rfid;
//    private Date swipeDate;
//
//    public Swipe(){
//
//    }
//
//    public Swipe(String rfid_in, Date date_in){
//        rfid = rfid_in;
//        swipeDate = date_in;
//    }
//
//    public String getRfid(){
//        return rfid;
//    }
//
//    public Date getSwipeDate(){
//        return swipeDate;
//    }

}
