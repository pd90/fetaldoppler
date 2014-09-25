package com.example.fetalheartrate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AudioBroadcast extends BroadcastReceiver {
    public boolean state;
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		// TODO Auto-generated method stub
		if(arg1.getAction().equals("android.intent.action.HEADSET_PLUG"))
		{
			Bundle localBundle = arg1.getExtras();
			int j = localBundle.getInt("state",-1);
			if(j==0)
			{
				Toast.makeText(arg0, "unplugged",10).show();
				state=false;
			}
			else if(j==1)
			{
				Toast.makeText(arg0, "plugged",10).show();
				state=true;
			}
		}
	}

}
