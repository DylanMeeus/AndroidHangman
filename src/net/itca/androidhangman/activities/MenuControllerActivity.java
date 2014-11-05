package net.itca.androidhangman.activities;

import java.util.ArrayList;
import java.util.List;

import net.itca.androidhangman.R;
import net.itca.androidhangman.R.id;
import net.itca.androidhangman.R.layout;
import net.itca.androidhangman.R.menu;
import net.itca.androidhangman.core.AndroidHangmanFacade;
import net.itca.androidhangman.core.Player;
import net.itca.androidhangman.data.OfflineScoreWriter;
import net.itca.androidhangman.data.OnlineScoreWriter;
import net.itca.androidhangman.data.OnlineSource;
import net.itca.androidhangman.interfaces.ScoreWriter;
import net.itca.core.interfaces.DataSource;
import net.itca.core.interfaces.Observer;
import net.itca.hangman.core.Hangman;
import net.itca.hangman.core.HangmanFacade;
import net.itca.hangman.core.OfflineSource;
import net.itca.hangman.core.Theme;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

public class MenuControllerActivity extends ActionBarActivity 
{
	private AndroidHangmanFacade game;
	private DataSource dataSource;
	private boolean isConnected;
	private Spinner themeSpinner;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu);
		isConnected = connectionAvailable();
		game = AndroidHangmanFacade.getHangmanFacade();
		setScoreWriter();
		populateList();
		populateSpinner();
	}
	
	
	private boolean connectionAvailable()
	{
		boolean connected = false;
		ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if(networkInfo != null)
		{
			connected = networkInfo.isConnected();
		}
		connectivityManager = null;
		networkInfo = null;
		return connected;
	} 
	
	private void populateList()
	{
		if(!isConnected)
		{
			ListView list = (ListView) findViewById(R.id.highscores);
			List<String> listArray = new ArrayList<String>();

			for (String playerScore : game.getScoreWriter().read())
			{
				listArray.add(playerScore);
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listArray);
			list.setAdapter(adapter);
		}
	}
	private void setScoreWriter()
	{
		if(isConnected)
		{
			game.setScoreWriter(new OnlineScoreWriter());
		}
		else
		{
			game.setScoreWriter(new OfflineScoreWriter(this));
		}
	}
	
	private void populateSpinner()
	{
		if(isConnected)
		{
			dataSource = (OnlineSource) new OnlineSource(this).execute("");
		}
		else // no connection - use offline
		{
			dataSource = new OfflineSource();
		}
		game.setDataSource(dataSource);
		// add items dynamically to the spinner element
		themeSpinner = (Spinner) findViewById(R.id.themespinner);
		List<String> spinnerArray = new ArrayList<String>();
		for(String theme : game.getThemes())
		{
			spinnerArray.add(theme);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		themeSpinner.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void onClick(View v)
	{
		TextView playerName = (TextView) findViewById(R.id.dynPlayerName);
		String name = "";
		if(playerName.getText().toString().equals(""))
		{
			name = "Default";
		}
		else
		{
			name = playerName.getText().toString();
		}
	
		game.setName(name);
		confirmSpinnerSelected();
		Intent gamescreen = new Intent(this,ControllerActivity.class);
		startActivity(gamescreen);
	}
	
	private void confirmSpinnerSelected()
	{
		game.setTheme((String)themeSpinner.getSelectedItem());
	}
	
	
	/* Handle screen rotation changes */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    // Read values from the "savedInstanceState"-object and put them in your textview
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	}
	

}
