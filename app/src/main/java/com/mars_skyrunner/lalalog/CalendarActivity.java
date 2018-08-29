package com.mars_skyrunner.lalalog;

import android.content.Intent;
import android.util.Log;
import android.widget.CalendarView;
import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import static com.mars_skyrunner.lalalog.RecordDetailFragment.mBirthdateTextView;

public class CalendarActivity extends Activity {

    private static final String TAG = CalendarActivity.class.getSimpleName();
    private int mYear;
    private int mMonth;
    private int mDay;
    private long initialDate;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calendar);

        //initializes the calendarview

        CalendarView calendar = findViewById(R.id.calendar);
        calendar.setOnDateChangeListener(mDateSetListener);

        // Save initial date of calendar
        initialDate = calendar.getDate();

        Log.v(TAG,"initialDate: " + initialDate);


    }

    private CalendarView.OnDateChangeListener mDateSetListener = new CalendarView.OnDateChangeListener() {

        @Override public void onSelectedDayChange(CalendarView view, int year, int monthOfYear, int dayOfMonth) {

            Log.v(TAG,"view.getDate(): " + view.getDate());

            if (!(view.getDate() == initialDate) ){
                // Listener called but user have not selected date

                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;

                String nextMonth ;

                if((mMonth + 1) < 10) {
                    nextMonth = "0" + (mMonth + 1);
                }else{
                    nextMonth = "" + (mMonth + 1);
                }

                String nextDay ;

                if(mDay < 10) {
                    nextDay = "0" + mDay;
                }else{
                    nextDay = "" + mDay;
                }

                String nextDate_aux = nextDay + " - " + getMonthString(nextMonth)  + " - " + year ;

                if(newDateGreater()){

                    mBirthdateTextView.setText(nextDate_aux);
                    finish();

                }

            }

        }
    };

    
    //Takes month number, and returns months name
    
    private String getMonthString(String nextMonth) {
        
        String answer = "";
        
        switch (nextMonth){
            case "01":
                
                answer = "Enero";
                
                break;

            case "02":

                answer = "Febrero";

                break;

            case "03":

                answer = "Marzo";

                break;

            case "04":

                answer = "Abril";

                break;

            case "05":

                answer = "Mayo";

                break;

            case "06":

                answer = "Junio";

                break;

            case "07":

                answer = "Julio";

                break;

            case "08":

                answer = "Agosto";

                break;

            case "09":

                answer = "Septiembre";

                break;

            case "10":

                answer = "Octubre";

                break;

            case "11":

                answer = "Noviembre";

                break;

            case "12":

                answer = "Diciembre";

                break;
        }
        
        return answer;
        
    }


    private boolean newDateGreater(){

        boolean answer = true;
/*

        String[] parts = MainActivity.currentDate.split("-");

        int month1 = Integer.parseInt(parts[0].trim());
        int month2 = (mMonth + 1);

        int day1 = Integer.parseInt(parts[1].trim());
        int day2 = mDay;

        int year1 = Integer.parseInt(parts[2].trim());
        int year2 = mYear;

        Log.v(TAG, "newDateGreater: " );

        Log.v(TAG, "year1: " + year1);
        Log.v(TAG, "year2: " + year2);

        Log.v(TAG, "month1: " + month1);
        Log.v(TAG, "month2: " + month2);

        Log.v(TAG, "day1: " + day1);
        Log.v(TAG, "day2: " + day2);

        if(year2 > year1){
            answer = true;
        }else{
            if(year2 == year1){
                if(month2 > month1){
                    answer = true;
                }else{
                    if(month2 == month1){
                        if(day2 >= day1){
                            answer = true;
                        }
                    }
                }
            }
        }
*/

        return answer;
    }

}