package net.itca.androidhangman.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import android.app.Activity;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import net.itca.androidhangman.R;
import net.itca.androidhangman.core.Player;
import net.itca.core.interfaces.DataSource;
import net.itca.core.interfaces.Observable;
import net.itca.core.interfaces.Observer;
import net.itca.data.Word;
import net.itca.hangman.core.Theme;

public class OnlineSource extends AsyncTask<String, Void, String> implements DataSource
{
	private Spinner spinner;
	ListView list;
	private Connection connection;
	private List<String> themes;
	private Activity context;
	private String theme;
	private ArrayList<Word> words;
	private ArrayList<Player> highscores;

	public OnlineSource(Activity _context)
	{
		context = _context;
		spinner = (Spinner) context.findViewById(R.id.themespinner);
		list = (ListView) context.findViewById(R.id.highscores);
		themes = new ArrayList<String>();
		words = new ArrayList<Word>();
		highscores = new ArrayList<Player>();
	}

	@Override
	public List<String> getThemes()
	{
		return themes;
	}

	private String getWordFromList(String theme)
	{
		
		ArrayList<String> tempList = new ArrayList<String>();
		for (Word word : words)
		{
			if (word.getCategory().toLowerCase().equals(theme.toLowerCase()))
			{
				tempList.add(word.GetWord());
			}
		}

		Random rand = new Random();
		int randomIndex = rand.nextInt(tempList.size());
		return tempList.get(randomIndex);
	}

	@Override
	public String getWord()
	{
		return getWordFromList(theme);
	}

	@Override
	public void setTheme(String _theme)
	{
		theme = _theme;
	}

	@Override
	protected String doInBackground(String... params)
	{
		
		/* ?sslfactory=org.postgresql.ssl.NonValidatingFactory&ssl=false */
		try
		{
			
			Class.forName("org.postgresql.Driver");
			
			String url = "jdbc:postgresql://gegevensbanken.khleuven.be:51415/probeer?sslfactory=org.postgresql.ssl.NonValidatingFactory&ssl=true";
			connection = DriverManager.getConnection(url, "***",
					"***");
			

			// GET THEMES
			
			String sqlThemes = "SELECT distinct(type) from hangman;";
			Statement ThemeStatement = connection.createStatement();
			ResultSet results = ThemeStatement.executeQuery(sqlThemes);
			while (results.next())
			{
				themes.add(results.getString("type"));
			}

			// GET WORDS
			String sqlWords = "SELECT * FROM HANGMAN;";
			Statement wordStatement = connection.createStatement();
			results = wordStatement.executeQuery(sqlWords);
			while (results.next())
			{
				words.add(new Word(results.getString("type"), results
						.getString("guessword")));
			}

			// GET HIGHSCORES
			String sqlHighscores = "select * from r0368004Hangman order by score desc limit 10;";
			Statement highScoreStatement = connection.createStatement();
			results = highScoreStatement.executeQuery(sqlHighscores);
			while (results.next())
			{
				highscores.add(new Player(results.getString("name"), Integer
						.parseInt(results.getString("score"))));
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			try
			{
				if (connection != null)
					connection.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(String result)
	{
		super.onPostExecute(result);
		updateSpinner();
		updateList();
	}
	
	// not mvc :( 
	private void updateList()
	{
		List<String> listArray = new ArrayList<String>();

		for (Player playerScore : highscores)
		{
			listArray.add(playerScore.getName() + " - " + playerScore.getScore());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_list_item_1, listArray);
		list.setAdapter(adapter);
	}

	private void updateSpinner()
	{
		List<String> spinnerArray = new ArrayList<String>();
		for (String theme : themes)
		{
			spinnerArray.add(theme);
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.simple_spinner_item, spinnerArray);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

}
