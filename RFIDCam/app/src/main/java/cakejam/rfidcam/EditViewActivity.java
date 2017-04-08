package cakejam.rfidcam;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class EditViewActivity extends AppCompatActivity {
    EditText studentET, variableET;
    Button swipeButton, submitButton, refreshButton;
    TextView studentsTableTV;
    //RadioGroup editChoice;
    RadioButton addRB, removeRB, modifyNameRB, modifyRFIDRB;
    ProgressBar progressBar;

    public String baseURL = "http://www.ckachur.com:8080/";
    public String API_URL = "";
    static final String keyAPI = "&key=thinkaboutit";
    public String nameAPI ="";
    public String newNameAPI = "";
    public String rfidAPI = "";
    
    public String query;

    ArrayList<String> studentsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_view);
        Intent catch_main2editintent = getIntent();
        studentsArray = catch_main2editintent.getStringArrayListExtra("NameArray");

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        studentET = (EditText) findViewById(R.id.studentEditText);
        variableET = (EditText) findViewById(R.id.variableEditText);
        studentsTableTV = (TextView) findViewById(R.id.studentTableTextView);
        studentsTableTV.setText("");
        //editChoice = (RadioGroup) findViewById(R.id.radioGroup);
        addRB = (RadioButton) findViewById(R.id.addRadioButton);
        removeRB = (RadioButton) findViewById(R.id.removeRadioButton);
        modifyNameRB =(RadioButton) findViewById(R.id.modifyNameRadioButton);
        modifyRFIDRB=(RadioButton) findViewById(R.id.modifyRFIDRadioButton);
        swipeButton = (Button) findViewById(R.id.swipeViewButton);
        submitButton = (Button) findViewById(R.id.submitButton);
        refreshButton = (Button) findViewById(R.id.refreshButton);

        for (int i = 0; i < studentsArray.size(); i++) {
            studentsTableTV.setText(studentsTableTV.getText().toString() + studentsArray.get(i) + "\n");
        }

        studentET.setEnabled(false);
        variableET.setEnabled(false);

        swipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent throw_edit2mainIntent = new Intent(EditViewActivity.this, MainActivity.class);
                throw_edit2mainIntent.putExtra("NameArray", studentsArray); //Optional parameters
                EditViewActivity.this.startActivity(throw_edit2mainIntent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setQueryAPI();
                new RetrieveFeedTask().execute();

            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query = "getStudentsAll";
                new RetrieveFeedTask().execute();

            }
        });

        studentET.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                nameAPI = studentET.getText().toString();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        variableET.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

                // you can call or do what you want with your EditText here
                if(modifyNameRB.isChecked()){
                    newNameAPI = variableET.getText().toString();
                }
                else if(modifyRFIDRB.isChecked() || addRB.isChecked()){
                    rfidAPI = variableET.getText().toString();
                }
                else{}

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
    }

    class RetrieveFeedTask extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            studentsTableTV.setText("");
        }

        protected String doInBackground(Void... urls) {
            if (query == "modifyName") {
                API_URL = baseURL + "modify/student/name?name=" + nameAPI + "&newName=" + newNameAPI + keyAPI;
            } else if (query == "modifyRFID") {
                API_URL = baseURL + "modify/student/rfid?name=" + nameAPI + "&rfid=" + rfidAPI + keyAPI;
            } else if (query == "addStudent") {
                API_URL = baseURL + "add/student?name=" + nameAPI + "&rfid=" + rfidAPI + keyAPI;
            } else if (query == "removeStudent") {
                API_URL = baseURL + "remove/student?name=" + nameAPI + keyAPI;
            } else {
                API_URL = baseURL + "students";
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
                } finally {
                    urlConnection.disconnect();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return null;
            }
        }

        protected void onPostExecute(String response) {
            if (response == null) {
                response = "THERE WAS AN ERROR";
            }
            progressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            // TODO: check this.exception
            // TODO: do something with the feed

            String queryResponse = "";
            ArrayList<Swipe> swipes = new ArrayList<Swipe>();
            JSONArray items = new JSONArray();

            try {
                JSONObject json = new JSONObject(response);
                if (query == "getStudentsAll" || query == "sendStudentArray") {
                    items = json.getJSONArray("students");

                } else {
                    items = json.getJSONArray("success");
//                }
                }

            } catch (JSONException e) {
                // manage exceptions
                queryResponse = "Error";
            }

            if (query == "sendStudentArray") {
                queryResponse = "going to Swipe View";
                studentsArray = getStudentsArray(items);
                Collections.sort(studentsArray);
            } else if (query == "getStudentsAll") {
                queryResponse = parsedStudents(items);
            } else {
                queryResponse = "Success";
            }
            studentsTableTV.setText(queryResponse);

        }
    }

    void setQueryAPI(){

        if(modifyNameRB.isChecked()){
            query = "modifyName";
        }
        else if(modifyRFIDRB.isChecked()){
            query = "modifyRFID";
        }
        else if(removeRB.isChecked()){
            query = "removeStudent";
        }
        else if(addRB.isChecked()){
            query = "addStudent";
        }
        else{
            query = "getStudentsAll";
        }
        new RetrieveFeedTask().execute();
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
            parsedSwipes = parsedSwipes + students.get(j).getName() + ": \t" + students.get(j).getRfid() + "\n";
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

    public void onRadioButtonClicked(View view) {
                // Is the button now checked?
                boolean checked = ((RadioButton) view).isChecked();

                // Check which radio button was clicked
                switch(view.getId()) {
                    case R.id.addRadioButton:
                        if (checked)
                            studentET.setEnabled(true);
                            studentET.setHint("New Name");
                            variableET.setEnabled(true);
                            variableET.setHint("New RFID (8 Hex)");
                        break;
                    case R.id.removeRadioButton:
                        if (checked)
                            studentET.setEnabled(true);
                            studentET.setHint("Removed Name");
                            variableET.setEnabled(false);
                            variableET.setHint("Disabled");
                        break;
                    case R.id.modifyNameRadioButton:
                        if (checked)
                            studentET.setEnabled(true);
                            studentET.setHint("Current Name");
                            variableET.setEnabled(true);
                            variableET.setHint("New Name");

                        // Ninjas rule
                        break;
                    case R.id.modifyRFIDRadioButton:
                        if (checked)
                            studentET.setEnabled(true);
                            studentET.setHint("Name of Student");
                            variableET.setEnabled(true);
                            variableET.setHint("New RFID (8 Hex)");
                    // Ninjas rule
                    break;
        }


    }





}
