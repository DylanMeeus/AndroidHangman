package net.itca.androidhangman.activities;

import java.util.HashMap;

import net.itca.androidhangman.R;
import net.itca.androidhangman.core.AndroidHangmanFacade;
import net.itca.androidhangman.core.DrawingPanel;
import net.itca.androidhangman.data.OfflineScoreWriter;
import net.itca.androidhangman.data.OnlineScoreWriter;
import net.itca.androidhangman.interfaces.ScoreWriter;
import net.itca.core.interfaces.DataSource;
import net.itca.core.interfaces.Observer;
import net.itca.hangman.core.GameState;
import net.itca.hangman.core.Hangman;
import net.itca.hangman.core.OfflineSource;
import net.itca.hangman.core.Theme;
import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ControllerActivity extends ActionBarActivity implements Observer
{
	private AndroidHangmanFacade game;
	private Handler handler;
	private DrawingPanel dp;
	private int screenWidth;
	private int screenHeight;
	private int score; // Controller keeps the score, because it's logic outside of the core logic. The controller can decide how it counts points.
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setup();
	}

	private void setup()
	{
		game = AndroidHangmanFacade.getHangmanFacade();
		getScreenSize();
		dp = (DrawingPanel) findViewById(R.id.drawingpanel);
		dp.setWillNotDraw(false);
		dp.postInvalidate();
		handler = new Handler();
		addDynamicButtons();
		game.setMaxAllowedErrors(9);
	}

	private void getScreenSize()
	{
		DisplayMetrics display = this.getResources().getDisplayMetrics();
		screenWidth = display.widthPixels;
		screenHeight = display.heightPixels;
	}

	private void addDynamicButtons()
	{
		int id = (int) 'a' - 1; 
		int startid = (int) 'a';
		int index = 0;
		int buttonWidth = 80; // hardcoded, change this!
		for (int i = (int) 'a'; i <= (int) 'z'; i++)
		{
			Button button = new Button(this);
			char letter = (char) i;
			String letterOnButton = Character.toString(letter);
			button.setText(letterOnButton);
			button.setId(i);
			button.setOnClickListener(new ButtonClick(button));
			RelativeLayout rl = (RelativeLayout) findViewById(R.id.dynbuttons);
			RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

			if (i > (int) 'a') 
			{
				if (buttonWidth * (index + 1) < screenWidth) 
				{
					lp.addRule(RelativeLayout.RIGHT_OF, id);
				} else
				{
					lp.addRule(RelativeLayout.BELOW, startid);
					lp.addRule(RelativeLayout.RIGHT_OF, startid);
					startid++;
				}
				index++;
			}
			button.setEnabled(false);
			rl.addView(button, lp);
			findViewById(android.R.id.content).invalidate();
			id = i;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.controller, menu);
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

	private void updateDisplayWord()
	{
		String word = game.getUncoveredWord();
		// Put some extra space between the letters
		String displayWord = "";
		for (char character : word.toCharArray())
		{
			displayWord += character + " ";
		}
		TextView uncovered = (TextView) findViewById(R.id.uncoveredword);
		uncovered.setText(displayWord);
	}

	public void onStart(View v)
	{
		game.RegisterObserver(this);
		game.start();
		enableButtons();
	}

	public void onStop(View v)
	{
		stop();
	}

	private void stop()
	{
		score = 0;
		disableButtons();
		updateScoreView();
	}

	private void updateScoreView()
	{
		TextView scoreView = (TextView) findViewById(R.id.dynScoreTextView);
		scoreView.setText(Integer.toString(score));
	}

	@Override
	public void update()
	{
		handler.post(new Runnable()
		{
			@Override
			public void run() // run on MT
			{
				updateDisplayWord();
				updateCanvas();
				checkGameState();
			}
		});
	}

	private void checkGameState()
	{
		GameState state = game.getGameState();
		switch (state)
		{
		case GAMELOSS:
			onLoss();
			break;
		case GAMEWIN:
			onWin();
			break;
		}
	}
	
	private void saveScore()
	{
		// We check what scorewriter we are currently using, to create a new instance of it and execute the async task.
		if(game.getScoreWriter() instanceof OnlineScoreWriter)
		{
			OnlineScoreWriter onlineScoreWriter = (OnlineScoreWriter) new OnlineScoreWriter(game.getName(),score).execute("");
		}
		else
		{
			OfflineScoreWriter offlineScoreWriter = (OfflineScoreWriter) game.getScoreWriter();
			offlineScoreWriter.write(game.getName(), score);
		}
	}

	private void onLoss()
	{
		// Call stop method, because you can just exit the dialog without
		// actually making a decision.
		stop();
		// show dialog, save scores
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Game over! *sadface* ")
				.setMessage("Try again?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("Yes!",
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int which)
							{
								game.start();
								enableButtons();
							}
						});
		builder.setNegativeButton("No, I give up.",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// save score
						saveScore();
						stop();
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void onWin()
	{
		// update score
		score++;
		updateScoreView();

		// show dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("You found the word!")
				.setMessage("Continue?")
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setPositiveButton("Yes, I'm addicted.",
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog,
									int which)
							{
								game.start();
								enableButtons();
							}
						});
		builder.setNegativeButton("No, I'm sick of this.",
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int which)
					{
						// save score
						saveScore();
						stop();
						dialog.dismiss();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void updateCanvas()
	{
		int errors = game.getErrors();
		dp.setErrors(errors);
		dp.postInvalidate();
	}

	private void enableButtons()
	{
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.dynbuttons);
		int children = rl.getChildCount();

		for (int i = 0; i < children; i++)
		{
			Button button = (Button) rl.getChildAt(i);
			button.setEnabled(true);
		}
	}

	private void disableButtons()
	{
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.dynbuttons);
		int children = rl.getChildCount();

		for (int i = 0; i < children; i++)
		{
			Button button = (Button) rl.getChildAt(i);
			button.setEnabled(false);
		}
	}
	
	private HashMap<String, Boolean> getButtonState()
	{
		HashMap<String, Boolean> buttonStates = new HashMap<String,Boolean>();
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.dynbuttons);
		int children = rl.getChildCount();

		for (int i = 0; i < children; i++)
		{
			Button button = (Button) rl.getChildAt(i);
			buttonStates.put((String)button.getText(), button.isEnabled());
		}
		return buttonStates;
	}

	// Class for the button onClick event

	class ButtonClick implements OnClickListener
	{
		Button button;

		public ButtonClick(Button sender)
		{
			button = sender;
		}

		@Override
		public void onClick(View v)
		{
			game.checkInput(button.getText().toString().charAt(0));
			button.setEnabled(false);
		}
	}
	
	private void restoreButtons(HashMap<String,Boolean> buttonStates)
	{
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.dynbuttons);
		int children = rl.getChildCount();

		for (int i = 0; i < children; i++) // I should split this off!
		{
			Button button = (Button) rl.getChildAt(i);
			button.setEnabled(buttonStates.get((String)button.getText()));
		}
		
		
	}

	/* Handle screen rotation changes */
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		
		super.onRestoreInstanceState(savedInstanceState);
		game = savedInstanceState.getParcelable("game");
		game.RegisterObserver(this); // New instance of this activity
		score = savedInstanceState.getInt("score");
		restoreButtons((HashMap<String, Boolean>) savedInstanceState.getSerializable("buttonstates"));
		update();
		
		//updateCanvas();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("errors", game.getErrors());
		outState.putInt("score", score);
		// Remove this observer from game, because when this gets reinitialized, it will get a different mem location (thus this is identified differently)
		game.RemoveObserver(this);
		outState.putParcelable("game", game);
		outState.putSerializable("buttonstates", this.getButtonState()); // HashMaps are serializable :)
		super.onSaveInstanceState(outState);
	}
}
