package edu.iu.eego;

import java.io.Serializable;

/**
 * Created by Shalaka on 11/30/2017.
 */

public class Community implements Serializable{
    private long id;
    private String totalPoints;
    private String calmPoints;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(String totalPoints) {
        this.totalPoints = totalPoints;
    }

    public String getCalmPoints() {
        return calmPoints;
    }

    public void setCalmPoints(String calmPoints) {
        this.calmPoints = calmPoints;
    }

    public Community(long id, String totalPoints, String calmPoints) {
        this.id = id;
        this.totalPoints = totalPoints;
        this.calmPoints = calmPoints;
    }
}
