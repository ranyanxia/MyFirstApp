package com.ranyanxia.myfirstapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ShareActionProvider;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;


public class MyActivity extends ActionBarActivity {

    public static final String EXTRA_MESSAGE = "com.ranyanxia.myfirstapp.MESSAGE";
    static final String INPUT_MSG = "inputMsg";
    public static final String ISBN_URL = "isbnUrl";
    private static final int PICK_CONTACT_REQUEST = 1;

    private String inputMsg;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my, menu);

        // Locate & assign share provider
//        MenuItem shareItem = menu.findItem(R.id.action_share);
//        mShareActionProvider = (ShareActionProvider) shareItem.getActionProvider();

        return true;
    }

    // Call to update the share intent
    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //inputMsg = getInputTextMessage();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setInputTextMessage(inputMsg);
        setInputTextMessage(getSavedInputFromSharedPreferences());
    }

    private void setInputTextMessage(String inputMsg) {
        EditText editText = (EditText)findViewById(R.id.edit_message);
        editText.setText(inputMsg);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings:
                openSettings();
                return true;
            case R.id.action_search:
                openSearch();
                return true;
//            case R.id.action_share:
//                openShare();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void openShare() {
        //show share action
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(INPUT_MSG, getInputTextMessage());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        inputMsg = savedInstanceState.getString(INPUT_MSG);
    }

    private void openSearch() {
        //TODO tbc
    }

    private void openSettings() {
        //TODO tbc
    }

    public void sendSwipe(View view) {
        //TODO respond to swipe click
        //1. init a Intent
        Intent intent = new Intent(this, SwipeActivity.class);
        String message = getInputTextMessage();
        intent.putExtra(EXTRA_MESSAGE, message);
        //2. start the Intent , system will receive a intent message, and start a new activity(Display Message Activity)
        startActivity(intent);
    }

    public void saveToSharedPref(View view) {
        //todo tbc
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String input = getInputTextMessage();
        editor.putString(getString(R.string.saved_input), input);
        editor.commit();

        //pop up message
    }

    private String getSavedInputFromSharedPreferences() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        String savedInput = sharedPref.getString(getString(R.string.saved_input), getString(R.string.saved_input_default));

        return savedInput;
    }

    public void saveToFile(View view) {
        //TODO tbc
        String input = getInputTextMessage();
        FileOutputStream fos ;
        try {
            fos = openFileOutput("testfile01", Context.MODE_PRIVATE);
            fos.write(input.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startIntentCall(View view) {
        try {
            long inputNum = Long.parseLong(getInputTextMessage());

            Uri num = Uri.parse("tel:" + inputNum);
            Intent callIntent = new Intent(Intent.ACTION_DIAL, num);

            startIntent(callIntent);
        } catch ( NumberFormatException nfe ) {
            nfe.printStackTrace();
            return;
        }
    }

    public void startIntentEmail(View view) {
        Intent emailIntent = initIntentEmail(view);
        if (emailIntent != null) {
            startIntent(emailIntent);
        }
    }

    public void showAppChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_SEND);
// This says something like "Share this photo with"
        String title = getResources().getString(R.string.chooser_title);
// Create intent to show chooser
        Intent chooser = Intent.createChooser(intent, title);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(chooser);
        }
    }

    public void searchISBN(View view) {
        String isbn = extractIsbn(getInputTextMessage());

        String url = "http://book.douban.com/isbn/" + isbn;

        //init a intent and show the page
        Intent intent = new Intent(this, DisplayViewActivity.class);
        intent.putExtra(ISBN_URL, url);
        startActivity(intent);
    }

    private String extractIsbn(String inputTextMessage) {
        //TODO add logic
        return inputTextMessage;
    }

    public void pickContact(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( requestCode == PICK_CONTACT_REQUEST ) {
            if ( resultCode == RESULT_OK ) {
                //TODO do sth
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);

                //todo call the number
                startIntent(initCallIntent(number));
            }
        }
    }

    private Intent initCallIntent(String number) {
        Uri num = Uri.parse("tel:" + number);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, num);

        return callIntent;
    }

    public void startIntent(Intent intent) {
        //validate
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
        boolean isIntentSafe = activities.size() > 0;

        if (isIntentSafe) {
            startActivity(intent);
        }
    }

    protected Intent initIntentEmail(View view) {
        String emailAddress = getInputTextMessage();
        if (!emailAddress.contains("@")) {
            return null;
        }
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// The intent does not have a URI, so declare the "text/plain" MIME type

        emailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress}); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"));

        return emailIntent;
    }

//    protected Intent initCalendarEventIntent(View view) {
//        Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
//        Calendar beginTime = Calendar.getInstance();
//        beginTime.set(2012, 0, 19, 7, 30);
//        Calendar endTime = Calendar.getInstance();
//        endTime.set(2012, 0, 19, 10, 30);
//        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
//        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
//        calendarIntent.putExtra(CalendarContract.Events.TITLE, "Ninja class");
//        calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Secret dojo");
//
//        return calendarIntent;
//    }

    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }

    public void sendMessage(View view) {
        //TODO respond to send click
        //1. init a Intent
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        String message = getInputTextMessage();
        intent.putExtra(EXTRA_MESSAGE, message);
        //2. start the Intent , system will receive a intent message, and start a new activity(Display Message Activity)
        startActivity(intent);
    }

    private String getInputTextMessage() {
        EditText editText = (EditText)findViewById(R.id.edit_message);
        String message = editText.getText().toString();

        return message;
    }

    public void sendNotification(View view) {
        //use new message notification helper class
        NewMessageNotification.notify(this, getInputTextMessage(), 1);
    }
}
