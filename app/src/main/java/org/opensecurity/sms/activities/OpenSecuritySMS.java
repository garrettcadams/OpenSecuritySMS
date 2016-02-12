package org.opensecurity.sms.activities;

import android.app.Activity;
import android.content.Intent;
import android.provider.Telephony;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.opensecurity.sms.R;
import org.opensecurity.sms.model.database.ContactDAO;
import org.opensecurity.sms.model.Engine;
import org.opensecurity.sms.model.lastMessageList.ArrayConversAdapter;
import org.opensecurity.sms.model.lastMessageList.ConversationLine;

import java.util.ArrayList;

/**
 * main activity when we start the application.
 */
public class OpenSecuritySMS extends Activity {

    public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";
    private MenuItem itemDefaultApp;


    /**
     * singleton pattern. We keep one instance (this) for the current activity
     */
    private static OpenSecuritySMS instance;

    /**
     * the listView of conversations
     */
    private ListView conversationList;

    private ContactDAO contactDAO;
    /**
     * The arrayList to keep objects of conversationLine
     */
    private ArrayList<ConversationLine> conversationLines;

    /**
     * The adapter for design the current activity
     */
    private ArrayConversAdapter adapter;

    /**
     * to get the current instance (=this)
     *
     * @return instance of our activity
     */
    public static OpenSecuritySMS getInstance() {
        return instance;
    }

    /**
     * To create the activity
     *
     * @param savedInstanceState save the instance state to keep informations.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_security_sms);

        this.conversationLines = new ArrayList<ConversationLine>();
        this.adapter = new ArrayConversAdapter(getBaseContext(), this.conversationLines);

        this.conversationList = (ListView) findViewById(R.id.listeConvers);
        this.conversationList.setAdapter(this.adapter);

        instance = this;

        update();
        listeners();

        contactDAO = new ContactDAO(getApplicationContext());
        contactDAO.openDb();
        instance = this;


        final String myPackageName = getPackageName();
        if (!Telephony.Sms.getDefaultSmsPackage(this).equals(myPackageName)) {
            // App is not default.
            Intent intent =
                    new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME,
                    myPackageName);
            startActivity(intent);
        }
    }

    /**
     * Use to create an optionMenu.
     *
     * @param menu keep informations relative to the menu
     * @return boolean if the menu is create or no
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_open_security_sms, menu);
        return true;
    }

    /**
     * To react when we select on item on the menu.
     *
     * @param item the current selected item.
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.deleteAllElementsOfTable) {
            contactDAO.deleteAllContactOSMS();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Allow the activity to keep the list of conversationsLine after the death of this activiy
     *
     * @param b the bundel used to serialize informations.
     */
    @Override
    public void onSaveInstanceState(Bundle b) {
        super.onSaveInstanceState(b);
        b.putSerializable("ConversSerialization", getConversationLines());
    }

    /**
     * To resore data which was keeped by the bundle on onSaveInstanceState function
     *
     * @param savedInstanceState the bundle
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        try {
            setConversationLines((ArrayList<ConversationLine>) savedInstanceState.getSerializable("ConversSerialization"));
        } catch (Exception e) {
            System.err.println("Error : the arrayList of conversationLine can't be saved !");
        }
    }


    /**
     * to return the current listView.
     *
     * @return the current listView
     */
    public ListView getConversationList() {
        return conversationList;
    }

    /**
     * to return the data of conversations (arrayList)
     *
     * @return the arrayList of conversationLines
     */
    public ArrayList<ConversationLine> getConversationLines() {
        return conversationLines;
    }

    /**
     * to set the data of conversationLines
     *
     * @param conversationLines arrayList of conversationLines
     */
    public void setConversationLines(ArrayList<ConversationLine> conversationLines) {
        getConversationLines().clear();
        getConversationLines().addAll(conversationLines);
        getAdapter().notifyDataSetChanged();
    }

    /**
     * This function is used to update this activity when it's necessary.
     */
    public void update() {
        setConversationLines(Engine.getInstance().loadLastMessages(this.getContentResolver()));

        /**
         * The listView conversationList will be showed in the activity thanks to the
         * Override of child class ArrayConversAdapter and getView method. and conversationLines is
         * the support(data of conversationLine information).
         */


    }


    /**
     * We write all listeners for current activity in this function.
     * <p/>
     * first listener : clickListeners on listView
     */
    public void listeners() {
        //starting the new activity when clicking on one of rowview.
        getConversationList().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ConversationActivity.class);
                intent.putExtra("Contact", getConversationLines().get(position).getContact());
                startActivity(intent);
            }
        });


    }

    /**
     * To get the adapter (design)
     *
     * @return the adapter of current activity
     */
    public ArrayConversAdapter getAdapter() {
        return adapter;
    }


    @Override
    protected void onResume() {
        contactDAO.openDb();
        super.onResume();
    }


    @Override
    protected void onPause() {
        contactDAO.closeDb();
        super.onPause();
    }
}