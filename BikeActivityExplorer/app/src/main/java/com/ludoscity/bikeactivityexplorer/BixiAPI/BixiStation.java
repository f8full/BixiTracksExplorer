package com.ludoscity.bikeactivityexplorer.BixiAPI;

public class BixiStation{
    public int empty_slots;
    public Extra extra;
    public int free_bikes;
    public String id;
    public Double latitude;
    public Double longitude;
    public String name;
    public String timestamp;

    public BixiStation() {
        //CECI EST TRÈS IMPORTANT !!
        extra = new Extra();
    }
}
