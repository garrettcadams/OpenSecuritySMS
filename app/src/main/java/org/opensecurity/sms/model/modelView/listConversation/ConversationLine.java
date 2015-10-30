package org.opensecurity.sms.model.modelView.listConversation;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import org.opensecurity.sms.R;
import org.opensecurity.sms.view.ConversationActivity;
import org.opensecurity.sms.view.OpenSecuritySMS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Object used to show a line of conversation
 * in the main activity
 * A ConversationLine is a class which just implements
 * the contents of a rowView.
 * latestMessage will be the last message so display in the rowView
 * contactName will be the name of a Contact in the rowView
 */
public class ConversationLine implements Serializable {

    public static int LIMIT_LOAD_MESSAGE = 20;

    private String contactName;
    private String latestMessage;
    private Calendar date;
    private int thread_ID;
    private String photoUrl;
    private String number;
    private int numberMessagesInTotal, numberLoaded;
    private boolean reloaded;

    public ConversationLine(String contactName, String latestMessage, Calendar date, int th_id, String photoUrl, String number, int numberMessagesInTotal){
        setContactName(contactName);
        setLatestMessage(latestMessage);
        setDate(date);
        setThread_id(th_id);
        setPhotoUrl(photoUrl);
        setNumber(number);
        setNumberMessagesInTotal(numberMessagesInTotal);
        setNumerLoaded(LIMIT_LOAD_MESSAGE);
        setReloaded(false);
    }

    public int getThread_ID() {
        return thread_ID;
    }

    public void setThread_id(int ID) {
        this.thread_ID = ID;
    }

    public void setLatestMessage(String latestMessage) {
        latestMessage = (latestMessage.length() > 100)?(latestMessage.substring(0, 97) + "..."):latestMessage;
        this.latestMessage = latestMessage;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setDate(Calendar date) {
        this.date = date;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public final String getContactName() {
        return this.contactName;
    }

    public final String getLatestMessage() {
        return this.latestMessage;
    }

    public  final Calendar getDate() {
        return this.date;
    }

    public String getManagedDate() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        Calendar dayWeek = (Calendar) today.clone();
        dayWeek.set(Calendar.DAY_OF_WEEK, dayWeek.getFirstDayOfWeek());

        Calendar dateMsg = (Calendar) getDate().clone();
        dateMsg.set(Calendar.HOUR_OF_DAY, 0);
        dateMsg.set(Calendar.MINUTE, 0);
        dateMsg.set(Calendar.SECOND, 0);
        dateMsg.set(Calendar.MILLISECOND, 0);

        if (dateMsg.compareTo(today) == 0) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(getDate().getTime());
        } else if (dateMsg.compareTo(dayWeek) >= 0) {
            SimpleDateFormat format = new SimpleDateFormat("E");
            return format.format(getDate().getTime());
        } else {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/y");
            return format.format(getDate().getTime());
        }
    }

    public final String getPhotoUrl() {
        return this.photoUrl;
    }

    public final Bitmap getPhoto(ContentResolver contentResolver) {
        Bitmap b = null;

        if (hasPhoto()) {
            try {
                b = MediaStore.Images.Media.getBitmap(contentResolver, Uri.parse(getPhotoUrl()));
            } catch (IOException e) {
                Log.i("Photo error", e.getMessage());
            }
        }

        return (b != null ? b : createLetterPhoto());
    }

    private final Bitmap createLetterPhoto() {
        Bitmap b = Bitmap.createBitmap(50, 50, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.drawColor(Color.DKGRAY);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
        p.setColor(Color.WHITE);

        if (getNumber().equals(getContactName())) {
            c.drawCircle(25, 15, 8, p);
            c.drawCircle(25, 48, 20, p);
            p.setColor(Color.DKGRAY);
            c.drawRect(0, 45, 50, 50, p);
        } else {
            String lettre = getContactName().substring(0, 1);

            p.setTextSize(35);
            p.setShadowLayer(1f, 0f, 1f, Color.BLACK);
            p.setTextAlign(Paint.Align.LEFT);

            // draw text to the Canvas center
            Rect bounds = new Rect();
            p.getTextBounds(lettre, 0, lettre.length(), bounds);
            int x = c.getClipBounds().width() / 2 - bounds.width() / 2 - bounds.left;
            int y = c.getClipBounds().height() / 2 + bounds.height() / 2 - bounds.bottom;

            c.drawText(lettre, x, y, p);
        }

        return b;
    }

    public String getNumber() {
        return number;
    }

    public boolean hasPhoto() {
        return this.photoUrl != null && !this.photoUrl.isEmpty();
    }

    public void setNumberMessagesInTotal(int numberMessagesInTotal) {
        this.numberMessagesInTotal = numberMessagesInTotal;
    }

    public int getMessageInTotal() {
        return this.numberMessagesInTotal;
    }

    public void setNumerLoaded(int numerLoaded) {
        this.numberLoaded = numerLoaded;
    }

    public int getNumberLoaded() {
        return this.numberLoaded;
    }

    public boolean isReloaded() {
        return reloaded;
    }

    public void setReloaded(boolean reloaded) {
        this.reloaded = reloaded;
    }
}