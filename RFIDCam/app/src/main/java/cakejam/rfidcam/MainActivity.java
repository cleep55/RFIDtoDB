package cakejam.rfidcam;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;


public class MainActivity extends AppCompatActivity {

    // Layout objects
    TextView responseView;
    ProgressBar progressBar;
    Spinner nameDD;
    Button queryButton, editButton;
    DatePicker startDP, endDP;
    CheckBox rangeCB, nameCB, dayCB;

    ArrayList<String> studentsArray;

    // standard url strings for API query
    public String baseURL = "http://www.ckachur.com:8080/";
    public String API_URL = "";
    static final String keyAPI = "thinkaboutit";

    /*
    10 types of query that switch cases are based on.
    query name
    get
    + "Student/Swipe" (type of array returned)
    + "With" indicates extra object in JSON array
    + "By" what it is filtering by (all means no filter).
        getStudentsAll
        getStudentByName
        getStudentByRFID
        getSwipesAll
        getSwipesByDay
        getSwipesByRange
        getSwipesWithNameAll
        getSwipesWithNameByName
        getSwipesWithNameByNameByDay
        getSwipesWithNameByNameByRange
    */
    public String query = "";

    // API variables
    public String startDateAPI ="";
    public String endDateAPI ="";
    public String nameAPI ="";
    public String newNameAPI = "";
    public String rfidAPI = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //queryBtn = (Button) findViewById(R.id.queryButton);
        responseView = (TextView) findViewById(R.id.responseView);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        nameDD = (Spinner)findViewById(R.id.nameSpinner);

        studentsArray = new ArrayList<>();
        Intent catch_edit2mainintent = getIntent();
        studentsArray = catch_edit2mainintent.getStringArrayListExtra("NameArray");

        //new RetrieveFeedTask().execute();

        API_URL = "http://www.ckachur.com:8080/students";
        query = "getStudentsAll";
        new RetrieveFeedTask().execute();

        //ArrayList<String> items = getStudentsArray();
        //String items[] = new String[100];
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, studentsArray);
//        nameDD.setAdapter(adapter);
        editButton = (Button) findViewById(R.id.EditViewButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent throw_main2editIntent = new Intent(MainActivity.this, EditViewActivity.class);
                throw_main2editIntent.putExtra("NameArray", studentsArray); //Optional parameters
                MainActivity.this.startActivity(throw_main2editIntent);
            }
        });



        rangeCB = (CheckBox) findViewById(R.id.rangeCheckBox);
        rangeCB.setEnabled(false);
//        rangeCB.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                setQueryAPI();
//            }
//        });
        rangeCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                               @Override
                                               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                   setQueryAPI();
                                                   if(isChecked){
                                                       endDP.setEnabled(true);
                                                       endDP.setVisibility(View.VISIBLE);
                                                       endDateAPI = endDP.getYear() + "-" + (endDP.getMonth()+1) + "-" + endDP.getDayOfMonth();
                                                   }
                                                   else{
                                                       endDP.setEnabled(false);
                                                       endDP.setVisibility(View.INVISIBLE);
                                                   }

                                               }
                                           }
        );

        nameCB = (CheckBox) findViewById(R.id.nameCheckBox);
        nameCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                               @Override
                                               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                   if(nameCB.isChecked()){
                                                       nameDD.setEnabled(true);
                                                   }
                                                   else {
                                                       nameDD.setEnabled(false);
                                                   }

                                               }
                                           }
        );

        dayCB = (CheckBox) findViewById(R.id.dayCheckBox);
        dayCB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                                              @Override
                                              public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                  setQueryAPI();
                                                  if(isChecked){
                                                      rangeCB.setEnabled(true);
                                                      startDP.setEnabled(true);

                                                  }
                                                  else{
                                                      rangeCB.setEnabled(false);
                                                      rangeCB.setChecked(false);
                                                      endDP.setEnabled(false);
                                                      startDP.setEnabled(false);
                                                      endDP.setVisibility(View.INVISIBLE);
                                                  }

                                              }
                                          }
        );

        //setQueryAPI();

        queryButton = (Button) findViewById(R.id.queryButton);
        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQueryAPI();
                new RetrieveFeedTask().execute();
            }
        });

        nameDD = (Spinner) findViewById(R.id.nameSpinner);
        nameDD.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                nameAPI = nameDD.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                nameAPI = nameDD.getSelectedItem().toString();
            }

        });

        startDP = (DatePicker) findViewById(R.id.startDatePicker);
//        startDP.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                setQueryAPI();
//                startDateAPI = startDP.getYear() + "-" + startDP.getMonth() + "-" + startDP.getDayOfMonth();
//
//            }
//        });
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.setTimeInMillis(System.currentTimeMillis());
        startDP.init(startCalendar.get(Calendar.YEAR), startCalendar.get(Calendar.MONTH), startCalendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                //Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                startDateAPI = year + "-" + (month+1) + "-" + dayOfMonth;

            }
        });

        endDP = (DatePicker) findViewById(R.id.endDatePicker);
        endDP.setEnabled(false);
        endDP.setVisibility(View.INVISIBLE);
//        endDP.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                setQueryAPI();
//                endDateAPI = endDP.getYear() + "-" + endDP.getMonth() + "-" + endDP.getDayOfMonth();
//
//            }
//        });
        Calendar endCalendar = Calendar.getInstance();
        endCalendar.setTimeInMillis(System.currentTimeMillis());
        endDP.init(endCalendar.get(Calendar.YEAR), endCalendar.get(Calendar.MONTH), endCalendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                //Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);
                endDateAPI = year + "-" + (month+1) + "-" + dayOfMonth;

            }
        });

        nameDD.setEnabled(false);


    }
    void setQueryAPI(){
        if(nameCB.isChecked())
        {
            if(dayCB.isChecked()) {

                if(rangeCB.isChecked()){
                    query = "getSwipesWithNameByNameByRange";
                }

                else{
                    query = "getSwipesWithNameByNameByDay";
                }

            }

            else
            {
                query = "getSwipesWithNameByName";
            }

        }
        else{
            if(dayCB.isChecked()) {

                if(rangeCB.isChecked()){
                    query = "getSwipesByRange";
                }

                else{
                    query = "getSwipesByDay";
                }

            }

            else
            {
                query= "getSwipesWithNameAll";
            }

        }
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            responseView.setText("");
        }

        protected String doInBackground(Void... urls) {
            //String email = emailText.getText().toString();
            // Do some validation here

//            switch (query){
//                case "getStudentsAll":
//                    API_URL = baseURL + "students";
//                    break;
//                case "getStudentByName":
//                    API_URL = baseURL + "students/name?name=" + nameAPI;
//                    break;
//
//                case "getStudentByRFID":
//                    API_URL = baseURL + "students/rfid?rfid=" + rfidAPI;
//                    break;
//
//                case "getSwipesAll":
//                    API_URL = baseURL + "swipes/";
//                    break;
//
//                case "getSwipesByDay":
//                    API_URL = baseURL + "swipes/day?day=" + startDateAPI;
//                    break;
//
//                case "getSwipesByRange":
//                    API_URL = baseURL + "swipes/range?start=" + startDateAPI + "&end=" + endDateAPI;
//                    break;
//
//                case "getSwipesWithNameAll":
//                    API_URL = baseURL + "swipes/students";
//                    break;
//
//                case "getSwipesWithNameByName":
//                    API_URL = baseURL + "swipes/student?student=" + nameAPI;
//                    break;
//
//                case "getSwipesWithNameByNameByDay":
//                    API_URL = baseURL + "swipes/student?student=" + nameAPI + "&day=" + startDateAPI;
//                    break;
//
//                case "getSwipesWithNameByNameByRange":
//                    API_URL = baseURL + "swipes/student?student=" + nameAPI + "&start=" + startDateAPI + "&end=" + endDateAPI;
//                    break;
//            }
            if(query == "getStudentsAll") {
                API_URL = baseURL + "students";
            }
            else if(query =="getStudentByName") {
                API_URL = baseURL + "students/name?name=" + nameAPI;
            }
            else if(query =="getStudentByRFID") {
                API_URL = baseURL + "students/rfid?rfid=" + rfidAPI;
            }
            else if(query =="getSwipesAll") {
                API_URL = baseURL + "swipes";
            }
            else if(query =="getSwipesByDay") {
                API_URL = baseURL + "swipes/day?day=" + startDateAPI;
            }
            else if(query =="getSwipesByRange") {
                API_URL = baseURL + "swipes/range?start=" + startDateAPI + "&end=" + endDateAPI;
            }
            else if(query =="getSwipesWithNameAll") {
                API_URL = baseURL + "swipes/students";
            }
            else if(query =="getSwipesWithNameByName") {
                API_URL = baseURL + "swipes/student?name=" + nameAPI;
            }
            else if(query =="getSwipesWithNameByNameByDay") {
                API_URL = baseURL + "swipes/student/day?name=" + nameAPI + "&day=" + startDateAPI;
            }
            else if(query =="getSwipesWithNameByNameByRange") {
                API_URL = baseURL + "swipes/student/range?name=" + nameAPI + "&start=" + startDateAPI + "&end=" + endDateAPI;
            }
            else{
                API_URL = baseURL + "swipes";
            }

            try {
//                URL url = new URL(API_URL + "email=" + email + "&apiKey=" + API_KEY);
                URL url = new URL(API_URL);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            // TODO: check this.exception
            // TODO: do something with the feed
            
            String queryResponse ="";
            ArrayList<Swipe> swipes = new ArrayList<Swipe>();
            JSONArray items = new JSONArray();

            try {
                JSONObject json = new JSONObject(response);
                if(query=="getStudentsAll" || query =="getStudentByName" || query =="getStudentByRFID"){
                    items = json.getJSONArray("students");

                }
                else {
                    items = json.getJSONArray("swipes");
//                }
                }

            } catch (JSONException e) {
                // manage exceptions
                queryResponse = "Error";
            }

//            ArrayList<Swipe> swipes = new ArrayList<Swipe>();
//
//            try {
//                JSONObject json = new JSONObject(response);
////                JSONObject dataObject = json.getJSONObject("swipes");
////                JSONArray items = dataObject.getJSONArray("swipes");
//                JSONArray items = json.getJSONArray("swipes");
//                queryResponse = parsedAllSwipes((items));
//
////                for (int i = 0; i < items.length(); i++) {
////                    JSONObject swipeObject = items.getJSONObject(i);
////                    Swipe swipe = new Swipe(swipeObject.getString("rfid"),
////                            swipeObject.getString("time"));
////                    swipes.add(swipe);
////                }
//
//            } catch (JSONException e) {
//                // manage exceptions
//                queryResponse = "Error";
//            }
            if(query=="getStudentsAll") {
                queryResponse = "Apply a Filter";
                studentsArray = getStudentsArray(items);
                Collections.sort(studentsArray);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, studentsArray);
                nameDD.setAdapter(adapter);
            }
            else if(query =="getStudentByName" || query =="getStudentByRFID")
            {
                queryResponse = parsedStudents(items);
            }
            else if(query =="getSwipesAll" || query =="getSwipesByDay" ||
                    query == "getSwipesByRange"){
                queryResponse = parsedSwipes(items);
            }
            else {
                queryResponse = parsedSwipesWithName(items);
            }

//            switch (query){
//                case "getStudentsAll":
//                    queryResponse = "Apply a Filter";
//                    studentsArray = getStudentsArray(items);
//                    break;
//                case "getStudentByName":
//                    queryResponse = parsedStudents(items);
//                    break;
//
//                case "getStudentByRFID":
//                    API_URL = baseURL + "students/rfid?rfid=" + rfidAPI;
//                    break;
//
//                case "getSwipesAll":
//                    API_URL = baseURL + "swipes/";
//                    break;
//
//                case "getSwipesByDay":
//                    API_URL = baseURL + "swipes/day?day=" + startDateAPI;
//                    break;
//
//                case "getSwipesByRange":
//                    API_URL = baseURL + "swipes/range?start=" + startDateAPI + "&end=" + endDateAPI;
//                    break;
//
//                case "getSwipesWithNamesAll":
//                    API_URL = baseURL + "swipes/students";
//                    break;
//
//                case "getSwipesWithNameByName":
//                    API_URL = baseURL + "swipes/student?student=" + nameAPI;
//                    break;
//
//                case "getSwipesWithNameByNameByDay":
//                    API_URL = baseURL + "swipes/student?student=" + nameAPI + "&day=" + startDateAPI;
//                    break;
//
//                case "getSwipesWithNameByNameByRange":
//                    API_URL = baseURL + "swipes/student?student=" + nameAPI + "&start=" + startDateAPI + "&end=" + endDateAPI;
//                    break;
//
//
//            }

//            String parsedSwipes="";
//            for(int j=0; j<swipes.size(); j++){
//                parsedSwipes = parsedSwipes + swipes.get(j).getRfid() + ": " + swipes.get(j).getSwipeDate() + "\n";
//            }
            //responseView.setText(response)
            //responseView.setText(parsedSwipes);
            responseView.setText(queryResponse);

//            try {
//                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
//                String requestID = object.getString("requestId");
//                int likelihood = object.getInt("likelihood");
//                JSONArray photos = object.getJSONArray("photos");
//                .
//                .
//                .
//                .
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    public String parsedSwipes(JSONArray swipeArray){
        String parsedSwipes="";
        ArrayList<Swipe> swipes = new ArrayList<Swipe>();

            for (int i = 0; i < swipeArray.length(); i++) {
                try {
                    JSONObject swipeObject = swipeArray.getJSONObject(i);
                    Swipe swipe = new Swipe(swipeObject.getString("rfid"),
                            swipeObject.getString("time"));
                    swipes.add(swipe);
                }
                catch (JSONException e) {
                    // manage exceptions
                    parsedSwipes = "Error";
                }
            }
        for(int j=0; j<swipes.size(); j++){
            parsedSwipes = parsedSwipes + swipes.get(j).getRfid() + ": " + swipes.get(j).getSwipeDate() + "\n";
        }

        return parsedSwipes;
    }
    public String parsedSwipesWithName(JSONArray swipeArray){
        String parsedSwipes="";
        ArrayList<Swipe> swipes = new ArrayList<Swipe>();

        for (int i = 0; i < swipeArray.length(); i++) {
            try {
                JSONObject swipeObject = swipeArray.getJSONObject(i);
                Swipe swipe = new Swipe(swipeObject.getString("rfid"),
                        swipeObject.getString("time"),
                        swipeObject.getString("name"));
                swipes.add(swipe);
            }
            catch (JSONException e) {
                // manage exceptions
                parsedSwipes = "Error";
            }
        }
        for(int j=0; j<swipes.size(); j++){
            parsedSwipes = parsedSwipes + swipes.get(j).getName() + ": \t" + swipes.get(j).getSwipeDate() + "\n";
        }

        return parsedSwipes;
    }

    public String parsedStudents(JSONArray studentArray) {
        String parsedSwipes = "";
        ArrayList<Student> students = new ArrayList<Student>();

        for (int i = 0; i < studentArray.length(); i++) {
            try {
                JSONObject swipeObject = studentArray.getJSONObject(i);
                Student student = new Student(swipeObject.getString("rfid"),
                        swipeObject.getString("name"));
                students.add(student);
            } catch (JSONException e) {
                // manage exceptions
                parsedSwipes = "Error";
            }
        }
        for (int j = 0; j < students.size(); j++) {
            parsedSwipes = parsedSwipes + students.get(j).getRfid() + ": " + students.get(j).getName() + "\t";
        }

        return parsedSwipes;
    }

    public ArrayList<String> getStudentsArray(JSONArray studentJSONArray) {
        String parsedSwipes = "";
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();
        //names.add("None");

        for (int i = 0; i < studentJSONArray.length(); i++) {
            try {
                JSONObject swipeObject = studentJSONArray.getJSONObject(i);
                names.add(swipeObject.getString("name"));
            } catch (JSONException e) {
                // manage exceptions
                parsedSwipes = "Error";
            }
        }

//        for (int j = 0; j < students.size(); j++) {
//            //parsedSwipes = parsedSwipes + students.get(j).getRfid() + ": " + students.get(j).getName() + "\t";
//            //names.add(j,students.get(j).getName());
//            names.add(students.get(j).getName());
//        }
        return names;
    }

}
