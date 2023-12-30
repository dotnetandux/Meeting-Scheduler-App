package morganj.meetingschedule;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created on 13/11/2018.
 *
 * This class stores all the global data about the meetings
 * and attendees
 *
 * @author Morgan Eifion Jones 904410
 * @version 1.0
 */

public class DataManager implements Serializable
{
    // Data
    private ArrayList<Meeting> meetings;
    private ArrayList<Attendee> previousAttend;

    // Font
    private int fontSize;
    private String fontColour;

    /**
     * Constructor sets default values
     */
    public DataManager()
    {
        meetings = new ArrayList<>();
        previousAttend = new ArrayList<>();
        fontSize = 16; // Medium
        fontColour = "#000000"; // Black
    }

    // Font colour

    /**
     * @return Font colour String represent
     */
    public String getFontColour()
    {
        return fontColour;
    }

    /**
     * @param fontColour ..
     */
    public void setFontColour(String fontColour)
    {
        this.fontColour = fontColour;
    }

    // Font size

    /**
     * @return Font size
     */
    public int getFontSize()
    {
        return fontSize;
    }

    /**
     * @param fontSize ..
     */
    public void setFontSize(int fontSize)
    {
        this.fontSize = fontSize;
    }

    // Meetings list

    /**
     * @return List of meetings
     */
    public ArrayList<Meeting> getMeetings() {
        return meetings;
    }

    /**
     * @param meetings List of meetings
     */
    public void setMeetings(ArrayList<Meeting> meetings) {
        this.meetings = meetings;
    }

    // Attendees list

    /**
     * @return List of attendees
     */
    public ArrayList<Attendee> getPreviousAttend() {
        return previousAttend;
    }

    /**
     * @param previousAttend List of attendees
     */
    public void setPreviousAttend(ArrayList<Attendee> previousAttend)
    {
        this.previousAttend = previousAttend;
    }
}
