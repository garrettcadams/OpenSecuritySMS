package org.opensecurity.sms.model;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import org.opensecurity.sms.controller.Controller;
import org.opensecurity.sms.view.ConversationActivity;

/**
 * Created by Valentin on 10/11/2015.
 * Is a big listener for android. Active this piece of code when Android detect a new entrance
 * of a sms
 */
public class SMSReceiver extends BroadcastReceiver {

    private static final String RECEIVED_ACTION =
            "android.provider.Telephony.SMS_RECEIVED";


    /**
     * an override of BroadcastReceiver function. To execute code when android detect an intent.
     * @param context interface to global information about an application environment.
     * @param intent abstract description of an operation to be performed.
     */
    @Override
    public void onReceive(Context c, Intent in) {
        if(in.getAction().equals(RECEIVED_ACTION)) {

            Bundle bundle = in.getExtras();
            if(bundle!=null) {
                Object[] pdus = (Object[])bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                String messageContent = new String();
                for(int i = 0; i<pdus.length; i++) {
                    messages[i] =
                            SmsMessage.createFromPdu((byte[])pdus[i]);
                    messageContent = messageContent+messages[i].getDisplayMessageBody();
                }

                Toast.makeText(c, "sms : " + messageContent, Toast.LENGTH_SHORT).show();
                Controller.getInstance().putSmsIntoDataBase(messages[0], messageContent);
                ConversationActivity.getInstance().update();
            }
        }
    }
}