package morganj.meetingschedule;

import java.io.Serializable;

/**
 * Created on 13/11/2018.
 *
 * This class represents an attendee
 * It implements Serializable so it can be passed between fragments
 *
 * @author Morgan Eifion Jones 904410
 * @version 1.0
 */

public class Attendee implements Serializable
{
    private String name;

    /**
     * Constructor
     * @param name ..
     */
    public Attendee(String name)
    {
        this.name = name;
    }

    // Name

    /**
     * @return Name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @param name  ..
     */
    public void setName(String name)
    {
        this.name = name;
    }
}
