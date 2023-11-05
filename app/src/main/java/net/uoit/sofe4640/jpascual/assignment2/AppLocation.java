package net.uoit.sofe4640.jpascual.assignment2;

public class AppLocation {
    public int id;
    public String address;
    public double latitude;
    public double longitude;

    public AppLocation()  {
        // Set defaults.
        // An ID of -1 is a special value indicating this AppLocation has never been inserted into the database.
        id = -1;
        address = "";
        latitude = 0;
        longitude = 0;
    }
}
