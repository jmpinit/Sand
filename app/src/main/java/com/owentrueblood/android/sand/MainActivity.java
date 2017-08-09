package com.owentrueblood.android.sand;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    private static final String TIMERS = "TIMERS";
    private Vector<Timer> timers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Are we restoring the state?
        if (savedInstanceState != null) {
            Timer[] timerArray = (Timer[])savedInstanceState.getParcelableArray(TIMERS);
            timers = new Vector<Timer>(Arrays.asList(timerArray));
        } else {
            timers = new Vector<Timer>();
        }

        // Get intent, action and MIME type
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (TimerManager.START_NEW_TIMER.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                // Create a named timer

                String name = intent.getStringExtra("name");
                Calendar cal = Calendar.getInstance();

                timers.add(new Timer(name, cal.getTime()));
            } else if ("".equals(type)) {
                // Default action is to start a new unnamed timer }
                Calendar cal = Calendar.getInstance();
                timers.add(new Timer(cal.getTime()));
            }
        }

        setContentView(R.layout.activity_main);

        ArrayAdapter<Timer> timerListAdapter = new TimerArrayAdapter(this, R.layout.timer_list_item, timers);

        ListView timerList = (ListView)findViewById(R.id.timer_list);
        timerList.setAdapter(timerListAdapter);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putParcelableArray(TIMERS, timers.toArray(new Parcelable[0]));

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    private class TimerArrayAdapter extends ArrayAdapter<Timer> {
        public TimerArrayAdapter(Context context, int textViewResourceId, List<Timer> timers) {
            super(context, textViewResourceId, timers);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout timerListRowView = (LinearLayout)getLayoutInflater().inflate(R.layout.timer_list_item, null);

            TextView nameView = timerListRowView.findViewById(R.id.timer_list_item_name);
            nameView.setText(getItem(position).getName());

            TextView dateView = timerListRowView.findViewById(R.id.timer_list_item_date);
            dateView.setText(getItem(position).getStart().toString());

            return timerListRowView;
        }
    }
}

class Timer implements Parcelable {
    private String name;
    private Date start;

    public Timer(String name, Date start) {
        this.name = name;
        this.start = start;
    }

    public Timer(Date start) {
        this.name = null;
        this.start = start;
    }

    public String getName() {
        return new String(this.name);
    }

    public Date getStart() {
        return new Date(start.getTime());
    }

    public String toString() {
        if (name == null) {
            return "timer " + name + " started on " + start.toString();
        } else {
            return "timer started on " + start.toString();
        }
    }

    public static final Creator<Timer> CREATOR = new Creator<Timer>() {
        @Override
        public Timer createFromParcel(Parcel in) {
            boolean hasName = in.readInt() > 0;
            String name = in.readString();
            Date start = new Date(in.readLong());

            if (hasName) {
                return new Timer(name, start);
            } else {
                return new Timer(start);
            }
        }

        @Override
        public Timer[] newArray(int size) {
            return new Timer[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (name == null) {
            parcel.writeInt(0);
            parcel.writeString("");
        } else {
            parcel.writeInt(1);
            parcel.writeString(name);
        }

        parcel.writeLong(start.getTime());
    }
}
