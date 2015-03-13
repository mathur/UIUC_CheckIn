package com.rmathur.uiuccheckin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.github.sendgrid.SendGrid;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private EditText edtUserInput;
    private TextView txtStatus;
    private HashMap students;
    private List<String> studentsIn;
    private ListView lstStudents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtUserInput = (EditText) findViewById(R.id.edtUserInput);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        lstStudents = (ListView) findViewById(R.id.lstStudents);

        // hack - change to async soon
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        InputStream inputStream = getResources().openRawResource(R.raw.students);
        CSVFile csvFile = new CSVFile(inputStream);
        students = csvFile.read();

        studentsIn = new ArrayList<String>();

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, studentsIn) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLACK);
                return view;
            }
        };
        lstStudents.setAdapter(arrayAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_getstudents) {
            createAndShowAlertDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    public void checkIn(View view) {
        txtStatus.setText("Processing card data...");
        String userText = edtUserInput.getText().toString();
        String uin = userText.substring(2, 15);
        Log.e("Debug - UIN", uin);

        txtStatus.setText("Finding student in database...");
        String netId = students.get(uin).toString();
        Log.e("Debug - Full Name", netId);

        studentsIn.add(netId);
        txtStatus.setText("Checked in!");
        edtUserInput.setText("");
        try {
            Thread.sleep(1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        txtStatus.setText("Waiting...");
    }

    private void createAndShowAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Email students?");
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                Date date = new Date();

                String emailBody = "List of students who attended class on " + dateFormat.format(date) + "\n";
                for (int i = 0; i < studentsIn.size(); i++) {
                    emailBody += studentsIn.get(i).toString() + "\n";
                }

                SendGrid sendgrid = new SendGrid("rohan32", "hackru");
                sendgrid.addTo("rohanmathur34@gmail.com");
                sendgrid.setFrom("attendance@illinois.edu");
                sendgrid.setSubject("Class Attendance for " + dateFormat.format(date));
                sendgrid.setText(emailBody);
                sendgrid.send();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}