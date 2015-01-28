package com.ranyanxia.myfirstapp;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.ranyanxia.myfirstapp.fragments.ArticleFragment;
import com.ranyanxia.myfirstapp.fragments.HeadlinesFragment;
import com.ranyanxia.myfirstapp.fragments.ItemFragment;

import java.util.logging.Logger;


public class DisplayMessageActivity extends ActionBarActivity implements HeadlinesFragment.OnHeadlineSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_display_message);

        //here we will get the intent, and extract data from it
        Intent intent = getIntent();
        String message = intent.getStringExtra(MyActivity.EXTRA_MESSAGE);

        //display the message got
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(message);

        //set current root view to be display text view
//        setContentView(textView);

//        displayInItemFragment(message);

        displayArticleFragment(message);

        //check if using large layout
        if (findViewById(R.id.fragment_container) != null) {
            //if resume, do nothing
            if (savedInstanceState != null) {
                return;
            }

            //create a example fragment
            HeadlinesFragment firstFragment = new HeadlinesFragment();
            //incase this is created from a Intent
            firstFragment.setArguments(getIntent().getExtras());

            //add this created fragement to the container
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, firstFragment).commit();
        }
    }

    private void displayArticleFragment(final String message) {
        setContentView(R.layout.news_articles);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_display_message, menu);
//        return true;
//    }

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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onArticleSelected(int position) {
        //implement the callback function to be handled by fragement
        //user selected a item
        ArticleFragment articleFragment = (ArticleFragment)getSupportFragmentManager().findFragmentById(R.id.article_fragment);

        if (articleFragment != null) {
            //in 2 panel layout
            articleFragment.updateArticleView(position);
        } else {
            // in 1 panel layout, then swape
            ArticleFragment newFragment = new ArticleFragment();
            Bundle args = new Bundle();
            args.putInt(ArticleFragment.ARG_POSITION, position);
            newFragment.setArguments(args);

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            //replace container and add back stack
            transaction.replace(R.id.fragment_container, newFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }

    }
}
