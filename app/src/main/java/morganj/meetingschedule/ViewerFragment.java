package morganj.meetingschedule;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
 * This class is responsible for the viewer_fragment.xml file
 * It handles displaying data for a meeting and updating it
 * It also handles external calls to device operations such as Gmail
 *
 * @author Morgan Eifion Jones 904410
 * @version 1.0
 */
public class ViewerFragment extends Fragment
{
    private View view;

    // Dialogs
    private Calendar calendar;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private TimePicker timep;

    // Text Views
    private TextView dateField;
    private TextView timeField;
    private TextView nameField;
    private TextView notesField;
    private TextView peopleField;
    private TextView locNameView;

    private AutoCompleteTextView autoCompleteTextView;
    private PlaceAutocompleteFragment placeAutocompleteFragment;

    private boolean editedTime;
    private boolean editDate;

    // Buttons
    private Button saveButton;
    private Button clearButton;
    private Button addAttButton;
    private Button addToGoogleButton;
    private Button inviteButton;

    // Data
    private Location location;
    private DataManager dm;
    private Meeting m;
    private ArrayList<Attendee> newAttendees;

    private static View v;

    /**
     * This method loads the xml file and assigns all xml objects
     * It also calls all other setup methods
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Check if already loaded
        if (v ==  null)
        {
            view = inflater.inflate(R.layout.viewer_fragment, container, false);
            v = view;
        }
        else
        {
            view = v;
        }

        // XML assignment
        dateField = view.findViewById(R.id.dateFieldView);
        timeField = view.findViewById(R.id.timeFieldView);
        nameField = view.findViewById(R.id.nameFieldView);
        notesField = view.findViewById(R.id.notesFieldView);
        autoCompleteTextView = view.findViewById(R.id.personAutoView);
        locNameView = view.findViewById(R.id.viewMapBtn);
        placeAutocompleteFragment = (PlaceAutocompleteFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_view);

        clearButton = view.findViewById(R.id.clearPerson);
        addToGoogleButton = view.findViewById(R.id.addToGoogle);
        addAttButton = view.findViewById(R.id.addPersonView);
        peopleField = view.findViewById(R.id.peopleFieldView);
        inviteButton = view.findViewById(R.id.sendInvite);
        saveButton = view.findViewById(R.id.saveButton);

        // Instantiate
        newAttendees = new ArrayList<>();
        location = null;
        calendar = Calendar.getInstance();

        // Extra data
        m = (Meeting) getArguments().getSerializable("MEETING");
        dm = (DataManager) getArguments().getSerializable("DATAM");

        populateData();
        setDate();
        setTime();
        setSave();
        setClear();
        setAutoComplete();
        setAttendees();
        setLocation();
        setMap();
        setFont();
        setGoogle();
        setInvite();
        editDate = false;
        editedTime = false;

        return view;
    }

    /**
     * Sets onClickListener for inviteButton
     */
    private void setInvite()
    {
        inviteButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Alerts of user of action before loading device mail app
             * Populates new email with meeting data
             * @param view Current view
             */
            @Override
            public void onClick(View view)
            {
                // Alert user of action
                AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext(),
                        R.style.datePicker);
                builder2.setMessage("Redirecting to email");
                builder2.setCancelable(true);
                builder2.setPositiveButton("OK", new DialogInterface.OnClickListener()
                {
                    /**
                     * Load new gmail activity
                     * @param dialogInterface ..
                     * @param i ..
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // Email type
                        Intent intent = new Intent(Intent.ACTION_SEND);
                        intent.setType("message/rfc822");

                        // Attendee names
                        String[] s = new String[m.getAttendees().size()];
                        for (int x = 0; x < m.getAttendees().size(); x++)
                        {
                            s[x] = m.getAttendees().get(x).getName() + "@gmail.com";
                        }

                        // Add data
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Meeting: " + m.getName());
                        intent.putExtra(Intent.EXTRA_TEXT, "Meeting on: " + m.getDate().toString() +
                                "\nLocation: " + m.getLocation().getProvider() + "\nNotes: " +
                                m.getNotes());
                        intent.putExtra(Intent.EXTRA_EMAIL, s);

                        startActivity(Intent.createChooser(intent, "Send email"));
                    }
                });

                builder2.show();
            }
        });
    }

    /**
     * Sets onClickListener to addToGoogleButton
     */
    private void setGoogle()
    {
        addToGoogleButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Alerts user of action the loads default calendar app
             * Populates calendar entry with meeting info
             * @param view Current view
             */
            @Override
            public void onClick(View view)
            {
                // Alert user of action
                AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext(),
                        R.style.datePicker);
                builder2.setMessage("Redirecting to calendar");
                builder2.setCancelable(true);
                builder2.setNegativeButton("OK", new DialogInterface.OnClickListener()
                {
                    /**
                     * Loads Google Calendar activity with meeting data
                     * @param dialogInterface ..
                     * @param i ..
                     */
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        // Calendar type
                        Intent intent = new Intent(Intent.ACTION_INSERT);
                        intent.setType("vnd.android.cursor.item/event");

                        // Add data
                        intent.putExtra(CalendarContract.Events.TITLE, m.getName());
                        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, m.getLocation()
                                .getProvider());
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, m.getDate());

                        startActivity(intent);
                    }
                });
                builder2.show();
            }
        });
    }

    /**
     * Sets the font size and colour of all buttons and text views
     * from the dataManager
     */
    private void setFont()
    {
        int x = dm.getFontSize();
        int y = Color.parseColor(dm.getFontColour());

        // Colour

        dateField.setTextColor(y);
        timeField.setTextColor(y);
        nameField.setTextColor(y);
        notesField.setTextColor(y);
        peopleField.setTextColor(y);
        locNameView.setTextColor(y);

        saveButton.setTextColor(y);
        clearButton.setTextColor(y);
        addAttButton.setTextColor(y);
        addToGoogleButton.setTextColor(y);
        inviteButton.setTextColor(y);

        // Size
        dateField.setTextSize(x);
        timeField.setTextSize(x);
        nameField.setTextSize(x);
        notesField.setTextSize(x);
        peopleField.setTextSize(x);
        locNameView.setTextSize(x);
        autoCompleteTextView.setTextSize(x);

        saveButton.setTextSize(x);
        clearButton.setTextSize(x);
        addAttButton.setTextSize(x);
        addToGoogleButton.setTextSize(x);
        inviteButton.setTextSize(x);
    }

    /**
     * Sets the onClickListener for the locNameView
     */
    private void setMap()
    {
        locNameView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Loads a new map activity with location of meeting passed through
             * @param view Current view
             */
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(view.getContext(), MapsActivity.class);

                intent.putExtra("LOC_NAME", location.getProvider());
                intent.putExtra("LOC_LAT", (location.getLatitude()));
                intent.putExtra("LOC_LAN", (location.getLongitude()));

                startActivity(intent);
            }
        });
    }

    /**
     * Sets the onPlaceSelectedListener for the placeAutoCompleteFragment
     */
    private void setLocation()
    {
        placeAutocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener()
        {
            /**
             * Sets the location to the one entered into the fragment
             * @param place Selected place
             */
            @Override
            public void onPlaceSelected(Place place)
            {
                location = new Location(place.getName().toString());

                location.setLatitude(place.getLatLng().latitude);
                location.setLongitude(place.getLatLng().longitude);

                locNameView.setText(place.getName());
            }

            @Override
            public void onError(Status status) {}
        });
    }

    /**
     * Sets the onClickListener for the clearButton
     */
    private void setClear()
    {
        clearButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Clears the attendees list and text view
             * @param view Current view
             * @see ViewerFragment#findMeeting()
             */
            @Override
            public void onClick(View view)
            {
                peopleField.setText("");
                newAttendees = new ArrayList<>();
            }
        });
    }

    /**
     * Sets the onClickListener for addAttButton
     */
    private void setAttendees()
    {
        addAttButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Adds the data in the text view as a new meeting attendee
             * @param v
             */
            @Override
            public void onClick(View v)
            {
                if (!autoCompleteTextView.getText().toString().equals(""))
                {
                    newAttendees.add(new Attendee(autoCompleteTextView.getText().toString()));
                    autoCompleteTextView.setText("");
                    makeToast("Added person", view).show();
                }
            }
        });
    }

    /**
     * This method returns the position of viewed meeting in dataManager array
     * @return
     */
    private int findMeeting()
    {
        for (int i = 0; i < dm.getMeetings().size(); i++)
        {
            if (m.equals(dm.getMeetings().get(i)))
            {
                return i;
            }
        }

        return  0;
    }

    /**
     * Sets the onClickListener for the autoCompleteTextView
     */
    private void setAutoComplete()
    {
        // Check there are attendees
        if (dm.getPreviousAttend().size() != 0)
        {
            ArrayList<String> autoFill = new ArrayList<>();
            // Convert to list of just names
            for (Attendee a : dm.getPreviousAttend())
            {
                autoFill.add(a.getName());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(view.getContext(),
                    R.layout.auto_complete, R.id.autoLayout , autoFill);
            autoCompleteTextView.setAdapter(adapter);
        }
    }

    /**
     * Due to java starting arrays on 0 this changes the abbreviation so
     * that it outputs the correct day
     * @param s Inputted abbreviation
     * @return New abbreviation
     */
    private String changeDay(String s)
    {
        switch(s)
        {
            case "Mon":
                return "Sat";
            case "Tue":
                return "Sun";
            case "Wed":
                return "Mon";
            case "Thu":
                return "Tue";
            case "Fri":
                return "Wed";
            case "Sat":
                return "Thu";
            case "Sun":
                return "Fri";
            default:
                return s;
        }
    }

    /**
     * This method populates all the text fields with data from the viewed meeting
     */
    private void populateData()
    {
        // Truncate date to readable
        String s = changeDay(m.getDate().toString().substring(0, 3));
        s += m.getDate().toString().substring(3, m.getDate().toString().length() - 18);

        String time = m.getDate().toString();

        nameField.setText(m.getName());
        dateField.setText(s);
        timeField.setText(time.substring(time.length()-17, time.length()-12));
        notesField.setText(m.getNotes());
        locNameView.setText(m.getLocation().getProvider());
        location = new Location(m.getLocation().getProvider());
        location.setLatitude(m.getLocation().getLatitude());
        location.setLongitude(m.getLocation().getLongitude());
        newAttendees = new ArrayList<>();

        // Print all attendees in text view
        if (m.getAttendees().size() != 0)
        {
            String a = "";

            for (int i = 0; i < m.getAttendees().size(); i++)
            {
                a += m.getAttendees().get(i).getName() +
                    "\n";
            }

            // Truncate useless extra line
            peopleField.setText(a.substring(0, a.length()-1));
            newAttendees = m.getAttendees();
        }
    }

    /**
     * Sets the onClickListener for saveButton
     */
    private void setSave()
    {
        saveButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Checks if data is valid then saves to a new meeting and writes to file
             * @param v Current view
             */
            @Override
            public void onClick(View v)
            {
                // Check data meets requirements
                if (isValid())
                {
                    Date date = m.getDate();
                    DatePicker dp;

                    // If date not changed then use old date data
                    if (editedTime && editDate)
                    {
                        dp = datePickerDialog.getDatePicker();
                        date =  new Date(dp.getYear()-1900, dp.getMonth(), (dp.getDayOfMonth()),
                                timep.getHour(), timep.getMinute());
                    }

                    Location l;

                    if (location == null)
                    {
                        // Extract Location info from place object
                        l = new Location(location.getProvider());
                        l.setLongitude(location.getLongitude());
                        l.setLatitude(location.getLatitude());
                    }
                    else
                    {
                        l = new Location("Bay Campus");
                        l.setLatitude(51.619129);
                        l.setLongitude(-3.879861);
                    }

                    // Update
                    dm.getMeetings().remove(findMeeting());
                    Meeting meet = new Meeting(nameField.getText().toString(), date,
                            l);

                    if (!notesField.getText().equals(""))
                    {
                        meet.setNotes(notesField.getText().toString());
                    }

                    // Add attendees
                    meet.setAttendees(newAttendees);
                    for (Attendee a : newAttendees)
                    {
                        dm.getPreviousAttend().add(a);
                    }

                    // Save
                    dm.getMeetings().add(meet);
                    FileManager.writeData(view.getContext(), dm);

                    makeToast("Saved", view).show();

                    // Go home
                    MainActivity mainActivity = (MainActivity) view.getContext();
                    mainActivity.navigation(R.id.ic_home);
                }
                else
                {
                    makeToast("Please input all data", view).show();
                }
            }
        });
    }

    /**
     * Checks data meets field requirements
     * @return True or False if valid
     */
    private boolean isValid()
    {
        boolean valid = true;

        if (nameField.getText().toString().equals(""))
        {
            valid = false;
        }

        if (locNameView.getText().toString().equals(""))
        {
            valid = false;
        }

        // Both time and date fields must be edited or neither
        if ((editedTime && !editDate) || (!editedTime && editDate))
        {
            // Clears fields if not both nor neither edited
            valid = false;
            dateField.setText("");
            timeField.setText("");
        }

        return valid;
    }

    /**
     * Sets the onClickListener for the timeField
     */
    private void setTime()
    {
        // Current time
        final int hour = calendar.get(Calendar.HOUR);
        final int min = calendar.get(Calendar.MINUTE);

        timeField.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Shows the TimePicker with current time
             * @param v Current view
             */
            @Override
            public void onClick(View v)
            {
                timePickerDialog = new TimePickerDialog(view.getContext(), R.style.datePicker,
                        new TimePickerDialog.OnTimeSetListener()
                        {
                            /**
                             * Sets timeField to inputted time
                             * @param tp ..
                             * @param hourOfDay ..
                             * @param minute ..
                             */
                            @Override
                            public void onTimeSet(TimePicker tp, int hourOfDay, int minute)
                            {
                                timep = tp;
                                timeField.setText(convertTime(hourOfDay, minute)); // Custom convert
                                editedTime = true;
                            }
                        }, hour, min, true);
                timePickerDialog.show();
            }
        });
    }

    /**
     * Custom converts time to readable string
     * @param h Hour
     * @param m Minute
     * @return Readable string
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
     * Sets the onClickListener for the dateField
     */
    private void setDate()
    {
        // Current date
        final int d = calendar.get(Calendar.DAY_OF_MONTH);
        final int m = calendar.get(Calendar.MONTH);
        final int y = calendar.get(Calendar.YEAR);

        dateField.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Shows the DatePicker with current date
             * @param v
             */
            @Override
            public void onClick(View v)
            {
                datePickerDialog = new DatePickerDialog(view.getContext(), R.style.datePicker,
                        new DatePickerDialog.OnDateSetListener()
                        {
                            /**
                             * Sets the dateField to inputted date
                             * @param view ..
                             * @param year ..
                             * @param month ..
                             * @param day ..
                             */
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int day)
                            {
                                dateField.setText(convertDate(day,
                                        month, year)); // Custom convert
                                editDate = true;
                            }
                        }, y, m, d);
                datePickerDialog.show();
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
     * Custom convert date to readable string
     * @param d Day
     * @param m Month
     * @param y Year
     * @return Readable String
     */
    private String convertDate(int d, int m, int y)
    {
        String months[] = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep",
                "Oct", "Nov", "Dec"};

        SimpleDateFormat date = new SimpleDateFormat("E");

        return date.format(new Date(y, m, d-2)) + ", " + d + " " + months[m] + " " + y;
    }
}
