package com.example.ourstars;

import android.os.AsyncTask;
import android.util.Log;

public class SendOrderEmail extends AsyncTask<String, Void, Void>{
	@Override
	protected Void doInBackground(String... param) {
        if (isCancelled()) return null;
        try
        {
        	GMailSender sender = new GMailSender("ourstarsdb@gmail.com","dbstarsour");
        	sender.sendMail("OurStars","OutStars Where Tomorrow's Stars Get Discovered",param[0],"ourstarsdb@gmail.com",param[1]);
        	
        }
        catch(Exception e)
        {
        	Log.e("error",e.getMessage(),e);
        }
		return null;
	}

}
