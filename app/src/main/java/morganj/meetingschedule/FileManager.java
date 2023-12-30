package morganj.meetingschedule;

import android.content.Context;
import android.location.Location;

import java.io.*;
import java.text.*;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created on 13/11/2018.
 *
 * This static class manages the reading and writing to and from the file
 * It also handles populating the dataManager
 *
 * @author Morgan Eifion Jones 904410
 * @version 1.0
 */

public class FileManager
{
    // FileName
    private static final String FILE_NAME = "data.txt";

    // Write methods

    /**
     * This handles creating/opening the file and then
     * printing the string representation of the data to said file
     * @param context Application Context
     * @param data DataManager
     * @see FileManager#outputFormatter(DataManager)
     */
    public static void writeData(Context context, DataManager data)
    {
        FileOutputStream output = null;

        try
        {
            output = context.openFileOutput(FILE_NAME, context.MODE_PRIVATE);
            output.write(outputFormatter(data).getBytes());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (output != null)
        {
            try
            {
                output.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static String outputFormatter(DataManager data)
    {
        String output = "";

        for (Meeting m : data.getMeetings())
        {
            DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
            String d = df.format(m.getDate());

            output += m.getName() + ";" + d + ";" + m.getNotes()
                    + ";" + m.getLocation().getProvider() + ";" +
                    m.getLocation().getLatitude() + ";" +
                    m.getLocation().getLongitude() + ";";

            for (Attendee a : m.getAttendees())
            {
                output += a.getName() + ";";
            }

            output += "\n";
        }

        output += data.getFontSize() + "\n";
        output += data.getFontColour();

        return output;
    }

    // Read methods

    /**
     * This method takes a list of all meetings and then sorts them
     * into a new list by chronological order
     * @param context Application Context
     * @param data DataManager
     */
    public static void sortedData(Context context, DataManager data)
    {
        ArrayList<Meeting> unsort = readData(context, data);
        ArrayList<Meeting> sorted = new ArrayList<>();
        Meeting m = null;

        Boolean earliest;
        int pos = unsort.size();

        // Checks there is more than 1 meeting
        if (unsort.size() == 0 || unsort.size() == 1)
        {
            sorted = unsort;
        }
        else
        {
            // While there is more than one meeting to be sorted
            while (unsort.size() > 1)
            {
                earliest = true;
                m = unsort.get(pos-1); // Meeting at last position
                Date current = m.getDate();

                // Check every meeting in list...
                for (int i = 0; i < unsort.size(); i ++)
                {
                    //...Against every meeting in list
                    if (!m.equals(unsort.get(i)))
                    {
                        // Check if meeting i is the earliest on record
                        if (current.after(unsort.get(i).getDate()))
                        {
                            earliest = false;
                        }
                    }
                }

                if (earliest)
                {
                    // Add to sorted list and remove from 'need sorting list'
                    sorted.add(m);
                    unsort.remove(m);
                    pos = unsort.size();
                }
                else
                {
                    // Move back in list
                    pos = pos - 1;
                }
            }

            // Add last meeting to end of list
            if (unsort.size() == 1)
            {
                sorted.add(unsort.get(0));
            }
        }

        data.setMeetings(sorted);
    }

    /**
     * This method takes in every line and adds the outputted meeting to the dataManager
     * @param context Application context
     * @param data DataManager
     * @return List of unsorted meetings
     */
    private static ArrayList<Meeting> readData(Context context, DataManager data)
    {
        FileInputStream input = null;
        ArrayList<Meeting> meetings = new ArrayList<>();

        try
        {
            input = context.openFileInput(FILE_NAME);
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader br = new BufferedReader(reader);
            String text;

            // If the line has content
            while ((text = br.readLine()) != null)
            {
                // If its data about a meeting entry
                if (text.length() > 9)
                {
                    meetings.add(populateData(text, data));
                }
                // If its font size data
                else if (text.length() == 2)
                {
                    data.setFontSize(Integer.parseInt(text));
                }
                // If its colour data
                else
                {
                    data.setFontColour(text);
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if (input != null)
        {
            try
            {
                input.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return meetings;
    }

    /**
     * This method takes in a string and parses it for data to add to a new
     * meeting object
     * @param data
     * @param dataM
     * @return Meeting object
     * @see FileManager#populateAttendees(DataManager, String)
     */
    private static Meeting populateData(String data, DataManager dataM)
    {
        // Splits string every ";"
        String[] separate = data.split(";");

        DateFormat df = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        Date d = null;

        // Parse data from first string
        try
        {
            d = df.parse(separate[1]);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        // Create location
        Location l = new Location(separate[3]);
        l.setLatitude(Double.parseDouble(separate[4]));
        l.setLongitude(Double.parseDouble(separate[5]));

        Meeting meeting = new Meeting(separate[0], d, l);
        meeting.setNotes(separate[2]);

        // Check for attendee data
        if (separate.length > 6)
        {
            for (int i = 6; i < separate.length; i++)
            {
                meeting.getAttendees().add(new Attendee(separate[i]));
                populateAttendees(dataM, separate[i]); // Adds new attendee to global list
            }
        }

        return meeting;
    }

    /**
     * This adds a new attendee to the global list if it is not already present
     * @param data DataManager
     * @param name Attendee nameText
     */
    public static void populateAttendees(DataManager data, String name)
    {
        boolean found = false;
        // If the list has data
        if (data.getPreviousAttend().size() != 0)
        {
            // Check against every attendees
            for (Attendee a : data.getPreviousAttend())
            {
                //For some reason this returns true if they are different, false if same
                if ((a.getName().equals(name)))
                {
                    found = true;
                }
            }
        }

        // Add to list if not found
        if (!found)
        {
            data.getPreviousAttend().add(new Attendee(name));
        }
    }
}
