package com.cmsc190.ics.uplbnb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;

/**
 * Created by Dell on 10 Apr 2018.
 */

public class FilterDialogFragment extends DialogFragment {
    Spinner establishmentTypeSpinner;
    EditText minPrice;
    EditText maxPrice;
    Spinner vacanciesSpinner;
    Spinner curfewSpinner;
    Spinner visitorsSpinner;
    Spinner securitySpinner;
    Spinner tenantsSpinner;
    RatingBar ratingBar;
    Button cancelButton;
    Button resetButton;
    Button submitButton;
    LinearLayout dormTenantLayout;
    FilterInfo filterInfo;
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    public interface OnInputListener{
        void sendFilter(FilterInfo filterInfo);
    }


    public OnInputListener mOnInputListener;
    // Use this instance of the interface to deliver action events
    FilterDialogFragment.NoticeDialogListener mListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mOnInputListener = (OnInputListener)activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
    public void submitFilter(){
        int establishmentType = -1;
        double minPriceDouble;
        double maxPriceDouble;
        int vacancies = 1;
        int curfew = 1;
        int visitors = 1;
        int security = 1;
        int tennants = -1;
        float rating;
        if(establishmentTypeSpinner.getSelectedItemPosition() == 0){
            establishmentType = -1;
        }
        else if(establishmentTypeSpinner.getSelectedItemPosition() == 1){
            establishmentType = 1;
        }
        else if(establishmentTypeSpinner.getSelectedItemPosition() == 2){
            establishmentType = 0;
        }
        if(minPrice.getText().toString().equals("")){
            minPriceDouble = 0f;
        }else{
            minPriceDouble = Double.parseDouble(minPrice.getText().toString().trim());
        }
        if(maxPrice.getText().toString().equals("")){
            maxPriceDouble = 99999f;
        }else{
            maxPriceDouble = Double.parseDouble(maxPrice.getText().toString().trim());
        }


        if(vacanciesSpinner.getSelectedItemPosition() == 0){
            vacancies = -1;
        }
        else if(vacanciesSpinner.getSelectedItemPosition() == 1){
            vacancies = 1;
        }
        else{
            vacancies = 0;
        }
        if(curfewSpinner.getSelectedItemPosition() == 0){
            curfew = -1;
        }
        else if(curfewSpinner.getSelectedItemPosition() == 1){
            curfew = 1;
        }
        else{
            curfew = 0;
        }

        if(visitorsSpinner.getSelectedItemPosition() == 0){
            visitors = -1;
        }
        else if(visitorsSpinner.getSelectedItemPosition() == 1){
            visitors = 1;
        }
        else{
            visitors = 0;
        }

        if(securitySpinner.getSelectedItemPosition() == 0){
            security = -1;
        }
        else if(securitySpinner.getSelectedItemPosition() == 1){
            security = 1;
        }
        else{
            security = 0;
        }
        if(tenantsSpinner.getSelectedItemPosition() == 0){
            tennants = -1;
        }
        else if(tenantsSpinner.getSelectedItemPosition() == 1){
            tennants = 2;
        }
        else if(tenantsSpinner.getSelectedItemPosition() == 2){
            tennants = 1;
        }
        else{
            tennants = 0;
        }

        rating = ratingBar.getRating();

        FilterInfo filterInfo = new FilterInfo(establishmentType,minPriceDouble,maxPriceDouble,vacancies,curfew,visitors,security,tennants,rating);
        mOnInputListener.sendFilter(filterInfo);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_dialog,container,false);
        establishmentTypeSpinner = (Spinner)view.findViewById(R.id.filterEstablishmentTypeSpinner);
        minPrice = (EditText)view.findViewById(R.id.filterMinPrice);
        maxPrice = (EditText)view.findViewById(R.id.filterMaxPrice);
        vacanciesSpinner = (Spinner)view.findViewById(R.id.filterVacancies);
        curfewSpinner = (Spinner)view.findViewById(R.id.filterCurfew);
        visitorsSpinner = (Spinner)view.findViewById(R.id.filterVisitors);
        securitySpinner = (Spinner)view.findViewById(R.id.filterSecurity);
        tenantsSpinner = (Spinner)view.findViewById(R.id.filterTenants);
        ratingBar = (RatingBar)view.findViewById(R.id.filterRatingBar);
        cancelButton = (Button)view.findViewById(R.id.filterCancel);
        submitButton = (Button)view.findViewById(R.id.filterSubmit);
        dormTenantLayout = (LinearLayout)view.findViewById(R.id.filterDormTenantsLayout);
        dormTenantLayout.setVisibility(View.GONE);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitFilter();
                getDialog().dismiss();
            }
        });

        establishmentTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == 2){
                    dormTenantLayout.setVisibility(View.VISIBLE);
                }
                else{
                    dormTenantLayout.setVisibility(View.GONE);
                }
            }



            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

/*    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.filter_dialog,null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(inflater.inflate(R.layout.filter_dialog, null))
                // Add action buttons
                .setPositiveButton("Filter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogPositiveClick(FilterDialogFragment.this);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mListener.onDialogNegativeClick(FilterDialogFragment.this);
                    }
                });

        return builder.create();

    }*/
}
