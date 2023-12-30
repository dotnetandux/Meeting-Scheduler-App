package morganj.meetingschedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created on 18/11/2018.
 *
 * This Fragment class is responsible for the add_meeting_fragment.xml file
 * It handles creating and saving new meeting data
 *
 * @author Morgan Eifion Jones 904410
 * @version 1.0
 */
public class AddMeetingFragment extends Fragment
{
    // TextViews
    private TextView dateField;
    private TextView timeField;
    private TextView nameField;
    private TextView notesField;

    // Special Text views
    private PlaceAutocompleteFragment placeFragment;
    private AutoCompleteTextView personAutocomplete;

    // Buttons
    private Button saveButton;
    private Button clearButton;
    private Button addPersonButton;

    // Data
    private ArrayList<Attendee> newAttendees;
    private DataManager dataManager;
    private Place location;

    private View view;
    private static View v;

    // Dialogs
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private TimePicker timePicker;


    /**
     * This method is assigns xml objects and calls all other methods
     * @param inflater ..
     * @param container ..
     * @param savedInstanceState ..
     * @return The current view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Checks if view is already loaded
        if (v ==  null)
        {
            view = inflater.inflate(R.layout.add_meeting_fragment, container, false);
            v = view;
        }
        else
        {
            view = v;
        }

        // Assign xml objects
        dateField = view.findViewById(R.id.dateField);
        timeField = view.findViewById(R.id.timeField);
        nameField = view.findViewById(R.id.nameField);
        notesField = view.findViewById(R.id.notesField);
        personAutocomplete = view.findViewById(R.id.personAuto);
        placeFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.place_autocomplete);
        saveButton = view.findViewById(R.id.saveButton);
        clearButton = view.findViewById(R.id.clearButton);
        addPersonButton = view.findViewById(R.id.addPerson);

        // Instantiate data
        calendar = Calendar.getInstance();
        newAttendees = new ArrayList<>();

        // Gets extra package data from MainActivity
        dataManager = (DataManager) getArguments().getSerializable("DATAM");

        setDate();
        setTime();
        setClear();
        setSave();
        setAttendees();
        setAutoComplete();
        setLocation();
        setFont();

        return view;
    }

    /**
     * This method retrieves the font size and colour from the data manager
     * and changes all text fonts respectively
     */
    private void setFont()
    {
        // Size
        int x = dataManager.getFontSize();

        dateField.setTextSize(x);
        timeField.setTextSize(x);
        nameField.setTextSize(x);
        notesField.setTextSize(x);
        personAutocomplete.setTextSize(x);

        saveButton.setTextSize(x);
        clearButton.setTextSize(x);
        addPersonButton.setTextSize(x-2);

        // Colour
        int y = Color.parseColor(dataManager.getFontColour());

        dateField.setTextColor(y);
        timeField.setTextColor(y);
        nameField.setTextColor(y);
        personAutocomplete.setTextColor(y);

        saveButton.setTextColor(y);
        clearButton.setTextColor(y);
        addPersonButton.setTextColor(y);
    }

    /**
     * This method sets the onClickListener for the PlaceAutoCompleteFragment
     */
    private void setLocation()
    {
        location = null;
        placeFragment.setText("");

        placeFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            /**
             * Sets the class variable to the selected location
             * @param place Selected location
             */
            @Override
            public void onPlaceSelected(Place place)
            {
                location = place;
            }

            @Override
            public void onError(Status status) {}
        });
    }

    /**
     * This method populates the array for the AutoCompleteTextView so it can
     * auto-predict attendees
     */
    private void setAutoComplete()
    {
        // If previous attendees exist
        if (dataManager.getPreviousAttend().size() != 0)
        {
            // Convert to just names
            ArrayList<String> autoFill = new ArrayList<>();
            for (Attendee a : dataManager.getPreviousAttend())
            {
                autoFill.add(a.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                    R.layout.auto_complete, R.id.autoLayout , autoFill);
            personAutocomplete.setAdapter(adapter);
        }
    }

    /**
     * This method sets the onClickListener for the AddPersonButton
     */
    private void setAttendees()
    {
        addPersonButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This method adds text from the personAutoCompleteField to a list
             * of attendees
             * @param v The current view
             */
            @Override
            public void onClick(View v)
            {
                // If text not empty
                if (!personAutocomplete.getText().toString().equals(""))
                {
                    // Add to attendee list to be saved later
                    newAttendees.add(new Attendee(personAutocomplete.getText().toString()));
                    personAutocomplete.setText("");
                    makeToast("Added Person", view).show();
                }
            }
        });
    }

    /**
     * This method creates a toast for other methods to call
     * @param message The message displayed in the toast
     * @return Toast object
     */
    private Toast makeToast(String message, View view)
    {
        return Toast.makeText(view.getContext(), message, Toast.LENGTH_SHORT);
    }

    /**
     * This method sets the onClickListener for the saveButton
     */
    private void setSave()
    {
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This saves all the meeting data to a new meeting and
             * adds it to the dataManager if its valid
             * @param v The current view
             * @see AddMeetingFragment#isValid()
             */
            @Override
            public void onClick(View v)
            {
                // Run custom data validation
                if (isValid())
                {
                    // Return set dateText
                    DatePicker dp = datePickerDialog.getDatePicker();
                    Date date =  new Date(dp.getYear()-1900, dp.getMonth(), (dp.getDayOfMonth()),
                            timePicker.getHour(), timePicker.getMinute());

                    Location l;

                    if (location == null)
                    {
                        // Extract Location info from place object
                        l = new Location(location.getName().toString());
                        l.setLongitude(location.getLatLng().longitude);
                        l.setLatitude(location.getLatLng().latitude);
                    }
                    else
                    {
                        l = new Location("Bay Campus");
                        l.setLatitude(51.619129);
                        l.setLongitude(-3.879861);
                    }

                    Meeting meet = new Meeting(nameField.getText().toString(), date,
                            l);

                    // If not empty
                    if (!notesField.getText().equals(""))
                    {
                        meet.setNotes(notesField.getText().toString());
                    }

                    // If not empty
                    if (newAttendees.size() != 0)
                    {
                        meet.setAttendees(newAttendees);
                    }

                    // Add each new attendee
                    for (Attendee a : newAttendees)
                    {
                        dataManager.getPreviousAttend().add(a);
                    }

                    // Save data
                    dataManager.getMeetings().add(meet);
                    FileManager.writeData(view.getContext(), dataManager);

                    makeToast("Saved Meeting", view).show();

                    // Empty text fields for new data
                    clearData();
                }
                else
                {
                    // If not valid
                    makeToast("Please input all data", view).show();
                }
            }
        });
    }

    /**
     * This method checks data is valid by ensuring the required fields
     * have data inputted in them
     * @return True or False if valid
     */
    private boolean isValid()
    {
        // Data entry requires a nameText, location, dateText and time
        // Default valid
        boolean valid = true;

        if (nameField.getText().toString().equals(""))
        {
            valid = false;
        }

        /*
        if (location == null)
        {
            valid = false;
        }
        */

        if (datePickerDialog == null)
        {
            valid = false;
        }

        if(timePickerDialog == null)
        {
            valid = false;
        }

        return valid;
    }

    /**
     * This method sets the onClickListener for the clearButton
     * @see AddMeetingFragment#clearData()
     */
    private void setClear()
    {
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clearData();
            }
        });
    }

    /**
     * Clears all the text fields and data
     */
    private void clearData()
    {
        nameField.setText("");
        notesField.setText("");
        personAutocomplete.setText("");
        newAttendees = new ArrayList<>(); // Clears attendees
        dateField.setText("");
        nameField.setText("");
        placeFragment.setText("");
    }

    /**
     * This method sets the onClickListener for the TimeTextView
     */
    private void setTime()
    {
        final int hour = calendar.get(Calendar.HOUR);
        final int min = calendar.get(Calendar.MINUTE);

        // Sets to current time
        timeField.setHint(convertTime(hour+12, min));

        timeField.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Shows the Time Picker dialog with current time
             * @param v The current view
             * @see AddMeetingFragment#convertTime(int, int)
             */
            @Override
            public void onClick(View v)
            {
                timePickerDialog = new TimePickerDialog(view.getContext(), R.style.datePicker,
                        new TimePickerDialog.OnTimeSetListener()
                        {
                            /**
                             * Sets the timeTextView to the inputted time
                             * @param tp ..
                             * @param hourOfDay ..
                             * @param minute ..
                             */
                            @Override
                            public void onTimeSet(TimePicker tp, int hourOfDay, int minute)
                            {
                                timePicker = tp;
                                // String custom method
                                timeField.setText(convertTime(hourOfDay, minute));
                            }
                        }, hour, min, true);
                timePickerDialog.show();
            }
        });
    }

    /**
     * Converts the time to a readable string
     * @param h Hour
     * @param m Minute
     * @return String format of time
     */
    private String convertTime(int h, int m)
    {
        String output = "";

        if (h < 10)
        {
            output += "0" + h;
        }
        else
        {
            output += h;
        }

        output += ":";

        if (m < 10)
        {
            output += "0" + m;
        }
        else
        {
            output += m;
        }

        return output;
    }

    /**
     * This method sets the onClickListener for the dateTextView
     * @see AddMeetingFragment#convertDate(int, int, int)
     */
    private void setDate()
    {
        final int d = calendar.get(Calendar.DAY_OF_MONTH);
        final int m = calendar.get(Calendar.MONTH);
        final int y = calendar.get(Calendar.YEAR);

        // Sets to current dateText
        dateField.setHint(convertDate(d, m, y));

        dateField.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Shows the dateText picker dialog with current dateText
             * @param v Current view
             */
            @Override
            public void onClick(View v)
            {
                datePickerDialog = new DatePickerDialog(view.getContext(), R.style.datePicker,
                        new DatePickerDialog.OnDateSetListener()
                {
                    /**
                     * Sets the dateTextView to inputted dateText
                     * @param view ..
                     * @param year ..
                     * @param month ..
                     * @param day ..
                     */
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day)
                    {
                        // String custom method
                        dateField.setText(convertDate(day,
                                month, year));
                    }
                }, y, m, d);
                datePickerDialog.show();
            }
        });
    }

    /**
     * Converts the dateText to a readable string format
     * @param d Day
     * @param m Month
     * @param y Year
     * @return String format of dateText
     */
    private String convertDate(int d, int m, int y)
    {
        // Fixes issue with month and day starting at 0
        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};

        SimpleDateFormat date = new SimpleDateFormat("E");

        return date.format(new Date(y, m, d-2)) + ", " + d + " " + months[m] + " " + y;
    }
}
