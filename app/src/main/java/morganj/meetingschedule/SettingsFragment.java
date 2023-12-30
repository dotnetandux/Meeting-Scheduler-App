package morganj.meetingschedule;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created on 18/11/2018.
 *
 * This class is responsible for the settings_fragment.xml file
 * It links the xml objects and handles setting features such as changing font and clearing data
 *
 * @author Morgan Eifion Jones 904410
 * @version 1.0
 */

public class SettingsFragment extends Fragment
{
    private View view;
    private DataManager dataManager;
    private static View v;

    // XML Text Views
    private TextView fontSizeView;
    private TextView fontColourView;

    // XML Buttons
    private Button clearAllButton;
    private Button clearAttButton;

    /**
     * This assigns all xml objects and calls other setup methods
     * @param inflater ..
     * @param container ..
     * @param savedInstanceState ..
     * @return ..
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Checks if view is already loaded
        if (v ==  null)
        {
            view = inflater.inflate(R.layout.settings_fragment, container, false);
            v = view;
        }
        else
        {
            view = v;
        }

        // Get extra data
        dataManager = (DataManager) getArguments().getSerializable("DATAM");

        // Assign xml
        fontSizeView = view.findViewById(R.id.fontSizePick);
        fontColourView = view.findViewById(R.id.fontColourPick);
        clearAllButton = view.findViewById(R.id.clearAllButton);
        clearAttButton = view.findViewById(R.id.clearAllAttButton);

        setSize();
        setColour();
        setClear();
        setClearAttendees();

        if(dataManager.getMeetings().size() != 0)
        {
            // If template meeting is still present (assume this means first few runs)
            if (dataManager.getMeetings().get(0).getName().equals("EXAMPLE"))
            {
                // Display instruction on changing font
                AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext(),
                        R.style.datePicker);
                builder2.setMessage("Click text to circle through font size/colour");
                builder2.setCancelable(true);
                builder2.setNegativeButton("OK", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                });

                builder2.show();
            }
        }

        return view;
    }

    /**
     * Sets onClickListener for clearButton
     */
    private void setClear()
    {
        clearAllButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Clears all meeting and attendee data and saves tio file
             * @param view
             */
            @Override
            public void onClick(View view)
            {
                // New empty data
                dataManager.setMeetings(new ArrayList<Meeting>());
                dataManager.setPreviousAttend(new ArrayList<Attendee>());
                FileManager.writeData(view.getContext(), dataManager);
                makeToast("Cleared all", view);
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
     * Sets onClickListener for clearAttButton button
     */
    private void setClearAttendees()
    {
        clearAttButton.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Clears global attendee data
             * @param view
             */
            @Override
            public void onClick(View view)
            {
                dataManager.setPreviousAttend(new ArrayList<Attendee>());
                makeToast("Cleared Attendees", view);
            }
        });
    }

    /**
     * Sets onClickListener for fontColourView and sets default colour of text
     */
    private void setColour()
    {
        // Buttons
        clearAllButton.setTextColor(Color.parseColor(dataManager.getFontColour()));
        clearAttButton.setTextColor(Color.parseColor(dataManager.getFontColour()));

        // Text
        fontColourView.setTextColor(Color.parseColor(dataManager.getFontColour()));
        fontSizeView.setTextColor(Color.parseColor(dataManager.getFontColour()));

        fontColourView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Changes hex colour and string representation depending on current
             * colour
             * @param view Current view
             */
            @Override
            public void onClick(View view)
            {
                String y = dataManager.getFontColour();

                switch (y)
                {
                    // IF
                    case "#000000": // Black
                        y = ("#009933");
                        break;
                    case "#009933": // Green
                        y = ("#003380");
                        break;
                    case "#003380": // Blue
                        y = ("#4d4d4d");
                        break;
                    case "#4d4d4d": // Grey
                        y = ("#b30000");
                        break;
                    case "#b30000": // Red
                        y = ("#000000");
                        break;
                }

                fontColourView.setTextColor(Color.parseColor(y));
                fontSizeView.setTextColor(Color.parseColor(y));
                clearAllButton.setTextColor(Color.parseColor(y));
                clearAttButton.setTextColor(Color.parseColor(y));

                dataManager.setFontColour(y);
                FileManager.writeData(view.getContext(), dataManager);
            }
        });
    }

    /**
     * Sets onClickListener for fontSizeView
     */
    private void setSize()
    {
        // Buttons
        clearAllButton.setTextSize(dataManager.getFontSize());
        clearAttButton.setTextSize(dataManager.getFontSize());

        // Text
        fontSizeView.setTextSize(dataManager.getFontSize());
        fontColourView.setTextSize(dataManager.getFontSize());

        fontSizeView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Switches the font size of everything depending on current size
             * @param view
             */
            @Override
            public void onClick(View view)
            {
                int x = dataManager.getFontSize();
                // IF
                switch (x)
                {
                    case 12: // 12sp
                        x = 16;
                         break;
                    case 16: // 16sp
                        x = 20;
                        break;
                    case 20: // 20sp
                        x = 12;
                        break;
                }

                clearAllButton.setTextSize(x);
                clearAttButton.setTextSize(x);
                fontColourView.setTextSize(x);
                fontSizeView.setTextSize(x);

                dataManager.setFontSize(x);
                FileManager.writeData(view.getContext(), dataManager);
            }
        });
    }
}
