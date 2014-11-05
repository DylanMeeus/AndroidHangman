package net.itca.androidhangman.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import android.os.AsyncTask;
import net.itca.androidhangman.core.Player;
import net.itca.androidhangman.interfaces.ScoreWriter;
import net.itca.data.Word;

public class OnlineScoreWriter extends AsyncTask<String, Void, String>  implements ScoreWriter
{

	String playerName;
	int playerScore;
	Connection connection;
	HashMap<String, Integer> highscores;
	public OnlineScoreWriter()
	{
		highscores = new HashMap<String, Integer>();
	}
	public OnlineScoreWriter(String name, int score)
	{
		playerName = name;
		playerScore = score;
	}

	/**
	 * All database stuff
	 */
	@Override
	protected String doInBackground(String... params)
	{
		
		/*?sslfactory=org.postgresql.ssl.NonValidatingFactory&ssl=false*/
		try
		{
			Class.forName("org.postgresql.Driver");
			String url = "jdbc:postgresql://gegevensbanken.khleuven.be:51415/probeer?sslfactory=org.postgresql.ssl.NonValidatingFactory&ssl=true";
			connection = DriverManager.getConnection(url,"***","***");
			
			// INSERT SCORE
			String insertString = "insert into r0368004Hangman values('"+playerName+"', "+playerScore+");";
			Statement ThemeStatement = connection.createStatement();
			ThemeStatement.execute(insertString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(connection!=null)
					connection.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
		
		return null;
	}
	@Override
	public ArrayList<String> read()
	{
		return null;
	}

	
}
