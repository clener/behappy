package whitewire.behappy.Fragments;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import whitewire.behappy.DatabaseHandler;
import whitewire.behappy.Message;
import whitewire.behappy.R;
import whitewire.behappy.ResultsAdapter;

import static whitewire.behappy.R.id.day;

/**
 * Created by Claudio on 18-Mar-17.
 */

public class DatabaseResultsFragment extends Fragment {

    ArrayList<String> dayArray = new ArrayList<>();
    ArrayList<String> monthArray = new ArrayList<>();
    ArrayList<String> yearArray = new ArrayList<>();

    private List<Message> messageList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ResultsAdapter mAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.database_results, container, false);
        Spinner dayInput = (Spinner) rootView.findViewById(day);
        Spinner monthInput = (Spinner) rootView.findViewById(R.id.month);
        Spinner yearInput = (Spinner) rootView.findViewById(R.id.year);

        dayArray.add("All");
        monthArray.add("All");
        yearArray.add("All");

        for (int i = 1; i <= 31; i++) {
            dayArray.add(Integer.toString(i));
        }
        for (int i = 1; i <= 12; i++) {
            monthArray.add(Integer.toString(i));
        }
        for (int i = 2015; i < 2020; i++) {
            yearArray.add(Integer.toString(i));
        }

        // Creating adapters for spinners
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, dayArray);

        ArrayAdapter<String> monthAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, monthArray);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<String>
                (getActivity(), android.R.layout.simple_spinner_item, yearArray);

        // Drop down layout style - list view with radio button
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Attaching data adapters to spinners
        dayInput.setAdapter(dayAdapter);
        monthInput.setAdapter(monthAdapter);
        yearInput.setAdapter(yearAdapter);

        recyclerView = (RecyclerView) rootView.findViewById(R.id.list);

        mAdapter = new ResultsAdapter(messageList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        Button enterButton = (Button) rootView.findViewById(R.id.enter);
        enterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner dayInput = (Spinner) getView().findViewById(day);
                Spinner monthInput = (Spinner) getView().findViewById(R.id.month);
                Spinner yearInput = (Spinner) getView().findViewById(R.id.year);

                int day, month, year;

                if (dayInput.getSelectedItem().toString() == "All") {
                    day = -1;
                } else {
                    day = Integer.parseInt(dayInput.getSelectedItem().toString());
                }

                if (monthInput.getSelectedItem().toString() == "All") {
                    month = -1;
                } else {
                    month = Integer.parseInt(monthInput.getSelectedItem().toString());
                }

                if (yearInput.getSelectedItem().toString() == "All") {
                    year = -1;
                } else {
                    year = Integer.parseInt(yearInput.getSelectedItem().toString());
                }

                loadData(day, month, year);
            }
        });

        // To enable user to reset all entries

        /*Button resetButton = (Button) rootView.findViewById(R.id.reset);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseHandler db = new DatabaseHandler(getContext());
                ArrayList<Message> messages = db.clearDatabase();
                RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.list);
                ResultsAdapter adapter = new ResultsAdapter(messages);
                recyclerView.setAdapter(adapter);
            }
        });*/
        return rootView;
    }

    private void loadData(int day, int month, int year) {
        DatabaseHandler db = new DatabaseHandler(getContext());
        ArrayList<Message> messages = db.getMessage(day, month, year);
        RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.list);
        ResultsAdapter adapter = new ResultsAdapter(messages);
        recyclerView.setAdapter(adapter);
    }
}
