package morganj.meetingschedule;

import android.location.Location;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created on 13/11/2018.
 *
 * This class represents a meeting object and stores all its data
 *
 * @author Morgan Eifion Jones 904410
 * @version 1.0
 */

public class Meeting implements Serializable
{
    private String name;
    private Date date;
    private ArrayList<Attendee> attendees;
    private String notes;

    // Location
    private Double lat;
    private Double longt;
    private String locName;

    /**
     * Constructor for meeting setting default values
     * @param name ..
     * @param date ..
     * @param loc Location
     */
    public Meeting(String name, Date date, Location loc)
    {
        this.name = name;
        this.date = date;
        attendees = new ArrayList<>();
        notes = "EXAMPLE";

        // Splits location into variables
        lat = loc.getLatitude();
        longt = loc.getLongitude();
        locName = loc.getProvider();
    }

    /**
     * @return ..
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name ..
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /**
     * Returns new location object from Meeting data
     * @return Location
     */
    public Location getLocation()
    {
        Location l = new Location(locName);
        l.setLongitude(longt);
        l.setLatitude(lat);

        return l;
    }

    /**
     * @param loc ..
     */
    public void setLocation(Location loc)
    {
        this.locName = loc.getProvider();
        this.longt = loc.getLongitude();
        this.lat = loc.getLatitude();
    }

    /**
     * @return ..
     */
    public Date getDate()
    {
        return date;
    }

    /**
     * @param date ..
     */
    public void setDate(Date date)
    {
        this.date = date;
    }

    /**
     * @return ..
     */
    public ArrayList<Attendee> getAttendees()
    {
        return attendees;
    }

    /**
     * @param attendees ..
     */
    public void setAttendees(ArrayList<Attendee> attendees)
    {
        this.attendees = attendees;
    }

    /**
     * @return ..
     */
    public String getNotes()
    {
        return notes;
    }

    /**
     * @param notes ..
     */
    public void setNotes(String notes)
    {
        this.notes = notes;
    }
}
