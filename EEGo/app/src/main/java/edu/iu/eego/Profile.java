package edu.iu.eego;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Shalaka on 11/30/2017.
 */

public class Profile  implements Serializable {
    private long id;
    private String planName;
    private String sessioNumber;
    private String moodBefore;
    private String sessionLength;
    private String moodAfter;
    private String moodDesc;
    private String calmSeconds;
    private String calmPoints;
    private String recoveries;
    private String heartRate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getSessioNumber() {
        return sessioNumber;
    }

    public void setSessioNumber(String sessioNumber) {
        this.sessioNumber = sessioNumber;
    }

    public String getMoodBefore() {
        return moodBefore;
    }

    public void setMoodBefore(String moodBefore) {
        this.moodBefore = moodBefore;
    }

    public String getSessionLength() {
        return sessionLength;
    }

    public void setSessionLength(String sessionLength) {
        this.sessionLength = sessionLength;
    }

    public String getMoodAfter() {
        return moodAfter;
    }

    public void setMoodAfter(String moodAfter) {
        this.moodAfter = moodAfter;
    }

    public String getMoodDesc() {
        return moodDesc;
    }

    public void setMoodDesc(String moodDesc) {
        this.moodDesc = moodDesc;
    }

    public String getCalmSeconds() {
        return calmSeconds;
    }

    public void setCalmSeconds(String calmSeconds) {
        this.calmSeconds = calmSeconds;
    }

    public String getCalmPoints() {
        return calmPoints;
    }

    public void setCalmPoints(String calmPoints) {
        this.calmPoints = calmPoints;
    }

    public String getRecoveries() {
        return recoveries;
    }

    public void setRecoveries(String recoveries) {
        this.recoveries = recoveries;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public Profile(String planName, String sessioNumber, String moodBefore, String sessionLength, String moodAfter, String moodDesc, String calmSeconds, String calmPoints, String recoveries, String heartRate) {
        this.planName = planName;
        this.sessioNumber = sessioNumber;
        this.moodBefore = moodBefore;
        this.sessionLength = sessionLength;
        this.moodAfter = moodAfter;
        this.moodDesc = moodDesc;
        this.calmSeconds = calmSeconds;
        this.calmPoints = calmPoints;
        this.recoveries = recoveries;
        this.heartRate = heartRate;
    }

    public Profile(long id, String planName, String sessioNumber, String moodBefore, String sessionLength, String moodAfter, String moodDesc, String calmSeconds, String calmPoints, String recoveries, String heartRate) {
        this.id = id;
        this.planName = planName;
        this.sessioNumber = sessioNumber;
        this.moodBefore = moodBefore;
        this.sessionLength = sessionLength;
        this.moodAfter = moodAfter;
        this.moodDesc = moodDesc;
        this.calmSeconds = calmSeconds;
        this.calmPoints = calmPoints;
        this.recoveries = recoveries;
        this.heartRate = heartRate;
    }

    public Profile() {
    }
}
