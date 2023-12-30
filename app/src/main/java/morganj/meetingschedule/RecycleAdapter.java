package morganj.meetingschedule;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created on 13/11/2018.
 *
 * This class handles the adapter that gets passed into a RecyclerView
 * to display meeting data
 *
 * @author MorganJones 904410
 * @version 1.0
 */
public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder>
{
    private ArrayList<Meeting> meetings;
    private DataManager dm;
    private Context c;

    /**
     * Created on 13/11/2018.
     *
     * This class handles the card_layout.xml file which represents
     * meeting data once given to the adapter
     *
     * @author MorganJones 904410
     * @version 1.0
     */
    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView nameText;
        public TextView dateText;
        private DataManager dm;

        /**
         * Assigns xml objects
         * @param v Current View
         * @param dm DataManager
         * @see ViewHolder#setFont()
         */
        public ViewHolder(View v, DataManager dm)
        {
            super(v);
            nameText = v.findViewById(R.id.meetingName);
            dateText = v.findViewById(R.id.meetingDate);
            this.dm = dm;

            setFont();
        }

        /**
         * Sets the font size and colour with dataManager data
         */
        private void setFont()
        {
            int x = dm.getFontSize();
            int y = Color.parseColor(dm.getFontColour());

            // Size
            nameText.setTextSize(x+8);
            dateText.setTextSize(x+3);

            // Colour
            nameText.setTextColor(y);
            dateText.setTextColor(y);
        }
    }

    /**
     * Assigns variables with constructor
     * @param c Application context
     * @param dm DataManager
     * @param meetings Meetings list
     */
    public RecycleAdapter(Context c, DataManager dm, ArrayList<Meeting> meetings)
    {
        this.c = c;
        this.meetings = meetings;
        this.dm = dm;
    }

    /**
     * Loads the card_layout.xml file into the RecycleAdapter
     * @param parent ..
     * @param type ..
     * @return ..
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int type)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent,
                    false);

        return new ViewHolder(view, dm);
    }

    /**
     * Due to Java starting arrays at 0 the day needs to be incremented
     * for it to display the correct day name abbreviation
     * @param s String day abbreviation
     * @return Correct abbreviation
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
     * This sets the values for the textViews and creates
     * an onClickListener
     * @param vh ..
     * @param p ..
     */
    @Override
    public void onBindViewHolder(final ViewHolder vh, int p)
    {
        final ViewHolder viewHolder = vh;
        final Meeting m = meetings.get(p);

        // Truncates unnecessary data
        String s = changeDay(m.getDate().toString().substring(0, 3));
        s += m.getDate().toString().substring(3, m.getDate().toString().length() - 12);

        viewHolder.nameText.setText(m.getName());
        viewHolder.dateText.setText(s);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * This loads the viewer_fragment with the corresponding meeting data
             * @param view Current view
             */
            @Override
            public void onClick(View view)
            {
                MainActivity mainActivity = (MainActivity) view.getContext();
                mainActivity.viewerNav(m);
            }
        });
    }

    /**
     * @return ..
     */
    @Override
    public int getItemCount()
    {
        return meetings.size();
    }
}
