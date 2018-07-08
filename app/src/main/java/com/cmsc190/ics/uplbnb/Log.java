package com.cmsc190.ics.uplbnb;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Dell on 20 Apr 2018.
 */

public class Log implements Serializable {
    String logMessage;
    String time;
    String userId;
    public Log(){

    }

    public Log(String logMessage,String userId){
        this.logMessage = logMessage;
        this.time = Calendar.getInstance().getTime().toString();
        this.userId = userId;
    }

    public String getLogMessage(){
        return this.logMessage;
    }

    public String getTime(){
        return this.time;
    }

    public String getUserId(){
        return this.userId;
    }

}
