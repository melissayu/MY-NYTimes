package com.melissayu.cp.mynytimes;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by melissa on 3/16/17.
 */

public class FilterFragment extends DialogFragment implements TextView.OnEditorActionListener, AdapterView.OnItemSelectedListener{
    private EditText mEditText;
    private DatePicker datePicker;

    CheckBox cbArts;
    CheckBox cbFashion;
    CheckBox cbSports;
    Boolean checkArts = false;
    Boolean checkFashion = false;
    Boolean checkSports = false;

    String sortBy;
    String beginDate;
    String newsDesk;

    // 1. Defines the listener interface with a method passing back data result.

    public interface FilterDialogListener {
        void onFinishFilterDialog(String sortBy, String beginDate, String newsDesk);
    }



    public FilterFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below


    }
    public void setupSpinner(View view){
        Spinner spinner = (Spinner) view.findViewById(R.id.sort_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sort_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        setSpinnerToValue(spinner, sortBy);
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

    public void setupDatePicker(View view) {
        datePicker = (DatePicker) view.findViewById(R.id.dialogDatePicker);
        if (beginDate != null) {
            Date parsedDate = parseDateString(beginDate);
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(parsedDate);
            datePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        }
    }

    //TODO: move to utility class
    public static Date parseDateString(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        sdf.setLenient(false);
        Date parsedDate = new Date();
        try {
            parsedDate = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parsedDate;
    }

    //TODO: move to utility class
    public static String datePickerToString(DatePicker datePicker){
        int   day  = datePicker.getDayOfMonth();
        int   month= datePicker.getMonth();
        int   year = datePicker.getYear();

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);
        cal.set(Calendar.DAY_OF_MONTH, day);
        Date dateRepresentation = cal.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String formattedDate = sdf.format(dateRepresentation);

        return formattedDate;
    }



    public static FilterFragment newInstance(String sortByValue, String beginDateValue, String newsDeskValue) {
        FilterFragment frag = new FilterFragment();
        Bundle args = new Bundle();
        args.putString("sortBy", sortByValue);
        args.putString("beginDate", beginDateValue);
        args.putString("newsDesk", newsDeskValue);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String sortByVal = getArguments().getString("sortBy");
        String beginDateVal = getArguments().getString("beginDate");
        String newsDeskVal = getArguments().getString("newsDesk");
        sortBy = sortByVal;
        beginDate = beginDateVal;
        newsDesk = newsDeskVal;

        Button filterBtn = (Button) view.findViewById(R.id.btn_filter);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Get date string from DatePicker
                beginDate = datePickerToString(datePicker);

                //Set news desk
                checkArts = cbArts.isChecked();
                checkFashion = cbFashion.isChecked();
                checkSports = cbSports.isChecked();
                formatNewsDeskValue();

                FilterDialogListener listener = (FilterDialogListener) getActivity();
                listener.onFinishFilterDialog(sortBy, beginDate, newsDesk);
                // Close the dialog and return back to the parent activity
                dismiss();
            }
        });

        setupSpinner(view);
        setupDatePicker(view);

        parseNewsDeskValue();
        cbArts = (CheckBox) view.findViewById(R.id.checkbox_arts);
        cbFashion = (CheckBox) view.findViewById(R.id.checkbox_fashion);
        cbSports = (CheckBox) view.findViewById(R.id.checkbox_sports);
        cbArts.setChecked(checkArts);
        cbFashion.setChecked(checkFashion);
        cbSports.setChecked(checkSports);

//        boolean isChecked = checkCheese.isChecked();
//        checkCheese.setChecked(true);

    }

    public void parseNewsDeskValue () {
        if (newsDesk != null) {
            if (newsDesk.contains("Arts")) {
                checkArts = true;
            }
            if (newsDesk.contains("Fashion & Style")) {
                checkFashion = true;
            }
            if (newsDesk.contains("Sports")) {
                checkSports = true;
            }
        }
    }

    public void formatNewsDeskValue () {
        String newsDeskString = "";
        Boolean hasValue = false;
        if (checkArts || checkFashion || checkSports) {
            newsDeskString = "news_desk:(";
            if (checkArts) {
                newsDeskString += "\"Arts\"";
                hasValue = true;
            }
            if (checkFashion) {
                if (hasValue) {
                    newsDeskString += "%20";
                }
                newsDeskString += "\"Fashion & Style\"";
                hasValue = true;
            }
            if (checkSports) {
                if (hasValue) {
                    newsDeskString += "%20";
                }
                newsDeskString += "\"Sports\"";
            }

            newsDeskString += ")";
            newsDesk = newsDeskString;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (EditorInfo.IME_ACTION_DONE == actionId) {

            // Return input text back to activity through the implemented listener
            FilterDialogListener listener = (FilterDialogListener) getActivity();
            listener.onFinishFilterDialog(sortBy, beginDate, newsDesk);

            // Close the dialog and return back to the parent activity
            dismiss();
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();
        sortBy = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
