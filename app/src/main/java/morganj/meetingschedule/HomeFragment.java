package morganj.meetingschedule;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created on 18/11/2018.
 *
 * This fragment class is responsible for the home_fragment.xml file
 * It handles displaying the view data and linking them to
 * new fragments
 *
 * @author Morgan Eifion Jones 904410
 * @version 1.0
 */
public class HomeFragment extends Fragment
{
    private View view;
    private RecyclerView recyclerView;
    private RecyclerView recyclerViewPast;

    // Data
    private DataManager dataManager;
    private static View v;
    private int adapat;

    private TextView pastTextView;
    private SearchView searchView;

    /**
     * This method assigns xml objects and calls other methods
     * @param inflater ..
     * @param container ..
     * @param savedInstanceState ..
     * @return Current View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Check if view already loaded
        if (v ==  null)
        {
            view = inflater.inflate(R.layout.home_fragment, container, false);
            v = view;
        }
        else
        {
            view = v;
        }

        // Gets package data from MainActivity
        dataManager = (DataManager) getArguments().getSerializable("DATAM");

        // Assign xml objects
        recyclerView = view.findViewById(R.id.recyleHomeView);
        recyclerViewPast = view.findViewById(R.id.recyleHomeView2);
        pastTextView = view.findViewById(R.id.meetingSeperator);
        searchView = view.findViewById(R.id.searchBar);

        // Display all meetings in recycler views
        setRecycleView(dataManager.getMeetings());
        setRecycleViewPast(dataManager.getMeetings());

        setFont();
        setSearch();

        return view;
    }

    /**
     * This method sets the onClickListener and onQueryTextListener for the searchView
     */
    private void setSearch()
    {
        // Makes the whole toolbar clickable
        searchView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Sets only icon clickable to false
             * @param v Current view
             */
            @Override
            public void onClick(View v)
            {
                searchView.setIconified(false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener()
        {
            /**
             * Loads relevant meeting info from submitted query
             * @param s The search query
             * @return ..
             */
            @Override
            public boolean onQueryTextSubmit(String s)
            {
                s = s.toLowerCase();
                Boolean b = false;
                Meeting focus = null;

                // Searches all meetings for matching title
                for (Meeting m : dataManager.getMeetings())
                {
                    if ((m.getName().toLowerCase()).equals(s))
                    {
                        b = true;
                        focus = m;
                    }
                }

                // Loads viewer_fragment for inputted meeting
                if (b)
                {
                    MainActivity mainActivity = (MainActivity) view.getContext();
                    mainActivity.viewerNav(focus);
                }
                else
                {
                    makeToast("Not found", view).show();
                }

                return false;
            }

            /**
             * Displays matching meeting info as query is inputted
             * @param s Search query
             * @return ..
             */
            @Override
            public boolean onQueryTextChange(String s)
            {
                s = s.toLowerCase();

                ArrayList<Meeting> matches = new ArrayList<>();
                // Checks against all meetings in list
                for (Meeting m : dataManager.getMeetings())
                {
                    // If start of nameText matches query i.e. "EXA" == "EXAMPLE"
                    if ((m.getName().toLowerCase()).startsWith(s))
                    {
                        matches.add(m); // Add to new list of matches
                    }
                }

                // Loads views with new lists
                setRecycleView(matches);
                setRecycleViewPast(matches);

                return false;
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
     * Sets size of pastTextView
     */
    private void setFont()
    {
        pastTextView.setTextSize(dataManager.getFontSize()+8);
    }

    /**
     * Displays only meetings before current data in recyclerViewPast
     * @param meetings Meeting list
     */
    private void setRecycleViewPast(ArrayList<Meeting> meetings)
    {
        ArrayList<Meeting> oldMeetings = new ArrayList<>();

        // Check all meetings
        for (Meeting m : meetings)
        {
            Date currrent = Calendar.getInstance().getTime();

            // Compare to current dateText
            if (currrent.after(m.getDate()))
            {
                oldMeetings.add(m);
            }
        }

        // Setup RecycleView
        final RecycleAdapter ra = new RecycleAdapter(view.getContext(), dataManager, oldMeetings);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(view.getContext());
        recyclerViewPast.setLayoutManager(lm);
        recyclerViewPast.setItemAnimator(new DefaultItemAnimator());
        recyclerViewPast.setAdapter(ra);

        // Sets on swipe actions for each Meeting view
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (0, ItemTouchHelper.LEFT)
        {
            /**
             * @param recyclerView ..
             * @param viewHolder ..
             * @param target ..
             * @return ..
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target)
            {
                return false;
            }

            /**
             * Sets onSwiped listener for recyclerViewPast meetings
             * @param viewHolder ViewHolder object
             * @param direction Left or Right direction
             * @see HomeFragment#confirmDelete(RecyclerView.ViewHolder, RecycleAdapter)
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                confirmDelete(viewHolder, ra);
                adapat = 0;
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerViewPast);
    }

    /**
     * Displays only meetings after current dateText in recyclerView
     * @param meetings
     */
    private void setRecycleView(ArrayList<Meeting> meetings)
    {
        ArrayList<Meeting> newMeetings = new ArrayList<>();

        // Checks all meetings
        for (Meeting m : meetings)
        {
            Date currrent = Calendar.getInstance().getTime();

            // Compare to current dateText
            if (currrent.before(m.getDate()))
            {
                newMeetings.add(m);
            }
        }

        // Set recyclerView
        final RecycleAdapter ra = new RecycleAdapter(view.getContext(), dataManager, newMeetings);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(view.getContext());
        recyclerView.setLayoutManager(lm);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(ra);

        // Set onSwipe listener for each meeting
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback
                (0, ItemTouchHelper.LEFT)
        {
            /**
             * @param recyclerView ..
             * @param viewHolder ..
             * @param target ..
             * @return ..
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target)
            {
                return false;
            }

            /**
             * Sets onSwipe listener for recyclerView meetings
             * @param viewHolder ViewHolder object
             * @param direction Left or right
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
               confirmDelete(viewHolder, ra);
               adapat = 1;
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Asks user to confirm delete and then removes meeting and global attendee data
     * @param vh ViewHolder object
     * @param ra RecycleAdapter
     */
    private void confirmDelete(final RecyclerView.ViewHolder vh, final RecycleAdapter ra)
    {
        // Dialog to confirm deletion
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),
                R.style.datePicker);
        builder.setTitle("Delete meeting?");
        builder.setCancelable(true);

        // NO
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            /**
             * Refreshes both views
             * @param dialogInterface ..
             * @param i ..
             */
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                if (adapat == 0)
                {
                    recyclerViewPast.setAdapter(ra);
                }
                else
                {
                    recyclerView.setAdapter(ra);
                }
            }
        });

        // YES
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            /**
             * This removes meeting and attendee data
             * @param dialogInterface ..
             * @param i ..
             */
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
                makeToast("Deleted", view).show();

                final int p = vh.getAdapterPosition();

                // Check all global attendees...
                for(int a = 0; a < dataManager.getPreviousAttend().size(); a++)
                {
                    //...Against all attendees in deleted meeting
                    for (int b = 0; b < dataManager.getMeetings().get(p).getAttendees().size(); b++)
                    {
                        // Delete if same nameText
                        if (dataManager.getPreviousAttend().get(a).getName().equals(
                                dataManager.getMeetings().get(p).getAttendees().get(b).getName()))
                        {
                            dataManager.getPreviousAttend().remove(a);
                        }
                    }
                }

                dataManager.getMeetings().remove(p);
                FileManager.writeData(view.getContext(), dataManager);
            }
        });

        builder.show();
    }
}
