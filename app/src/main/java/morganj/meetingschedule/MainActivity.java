package morganj.meetingschedule;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import java.util.Date;

/**
 * Created on 13/11/2018.
 *
 * This is the MainActivity class which is what is first launched
 * It controls what happens on setup as well as app navigation
 * It is responsible for activity_main.xml
 *
 * @author Morgan Eifion Jones 904410
 * @version 1.0
 */

public class MainActivity extends AppCompatActivity
{
    private SharedPreferences prefs = null;
    private static String PACKAGE;
    private static Fragment f = null;

    // Data
    private DataManager dataManager;
    private int currentFragment;

    // XML objects
    private BottomNavigationView bottomNavigationView;
    private TextView title;

    /**
     * This method loads the activityMain.xml file and calls other methods
     * It also assigns xml objects
     * @param savedInstanceState ..
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences(PACKAGE, MODE_PRIVATE);
        dataManager = new DataManager();
        PACKAGE = "com.morganj.meetingschedule";

        firstSetup();

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        title = findViewById(R.id.textView);

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener()
                {
                    /**
                     * Calls navigation method for nav bar click
                     * @param item Selection on nav bar
                     * @return ..
                     * @see MainActivity#navigation(int)
                     */
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item)
                    {
                        return navigation(item.getItemId());
                    }
                }
        );
        bottomNavigationView.setSelectedItemId(R.id.ic_home);
        currentFragment = bottomNavigationView.getSelectedItemId();
        navigation(R.id.ic_home);
    }

    /**
     * This loads the viewer_fragment.xml file for
     * a meeting passed in
     * @param m Meeting in focus
     */
    public void viewerNav(Meeting m)
    {
        Fragment fragment = new ViewerFragment();
        Bundle bundle = new Bundle();

        // Meeting data
        bundle.putSerializable("MEETING", m);
        bundle.putSerializable("DATAM", dataManager);

        fragment.setArguments(bundle);
        loadFragment(fragment);

        // Updates current
        currentFragment = R.id.ViewerFragment;
    }

    /**
     * This method controls switching between fragments
     * @param item The nav bar item
     * @return ..
     * @see MainActivity#loadFragment(Fragment)
     */
    public boolean navigation(int item)
    {
        Fragment fragment = null;
        Bundle bundle = new Bundle();

        // If not already on selected fragment
        if (item != currentFragment)
        {
            currentFragment = item;

            switch (item)
            {
                case R.id.ic_home:
                    fragment = new HomeFragment();
                    break;
                case R.id.ic_add:
                    fragment = new AddMeetingFragment();
                    break;
                case R.id.ic_menu:
                    fragment = new SettingsFragment();
                    break;
            }

            bundle.putSerializable("DATAM", dataManager);
            fragment.setArguments(bundle);
            loadFragment(fragment);
        }

        return true;
    }

    /**
     * Makes sure the app knows its been run before
     */
    @Override
    public void onResume()
    {
        prefs.edit().putBoolean("firstrun", false).commit();
        super.onResume();
    }

    /**
     * Loads a passed in fragment
     * @param fragment New fragment
     * @return ..
     */
    private boolean loadFragment(Fragment fragment)
    {
        if (f != fragment)
        {
            // Not empty
            if (fragment != null)
            {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentLayout, fragment)
                        .commit();

                f = fragment;
                return true;
            }
        }
        else
        {
            fragment = f;
            return true;
        }

        return false;
    }

    /**
     * This method checks if its the apps first run
     * If so it creates template data for an example and then
     * writes it so file
     * It also asks for permissions and gives basic instruction
     */
    private void firstSetup()
    {
        if (prefs.getBoolean("firstrun", true))
        {
            // Set firstRun to false
            prefs.edit().putBoolean("firstrun", false).commit();

            // Example data

            Location l = new Location("Bay Campus");
            l.setLatitude(51.619129);
            l.setLongitude(-3.879861);

            Meeting meeting = new Meeting("Example",
                    new Date(2019,0,13,15,0),
                    l);
            meeting.getAttendees().add(new Attendee("Tom Owen"));
            meeting.setNotes("This is an example.");

            dataManager.getMeetings().add(meeting);
            // Add attendees to global list
            for (Attendee a : meeting.getAttendees())
            {
                dataManager.getPreviousAttend().add(a);
            }

            FileManager.writeData(this, dataManager);

            // Instruction
            AlertDialog.Builder instructDialog = new AlertDialog.Builder(this,
                    R.style.datePicker);
            instructDialog.setMessage("Swipe left on a meeting to delete");
            instructDialog.setCancelable(true);
            final Activity a = this;
            instructDialog.setNegativeButton("OK", new DialogInterface.OnClickListener()
            {
                /**
                 * @param dialogInterface ..
                 * @param i ..
                 */
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {}
            });

            instructDialog.show();

            // Permission request
            final AlertDialog.Builder builder = new AlertDialog.Builder(this,
                    R.style.datePicker);
            builder.setMessage("Please allow location permissions");
            builder.setCancelable(true);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
            {
                /**
                 * @param dialogInterface ..
                 * @param i ..
                 */
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    ActivityCompat.requestPermissions(a, new String[] {Manifest.
                            permission.ACCESS_FINE_LOCATION}, 10);
                }
            });

            builder.show();
        }
        // If not first run then read data
        else
        {
            FileManager.sortedData(this, dataManager);
        }
    }
}
