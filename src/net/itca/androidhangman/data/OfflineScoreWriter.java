package net.itca.androidhangman.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import net.itca.androidhangman.core.Player;
import net.itca.androidhangman.interfaces.ScoreWriter;

public class OfflineScoreWriter implements ScoreWriter
{

	/**
	 * Write to file
	 */
	File savedFilesDir;
	Activity sendActivity;

	public OfflineScoreWriter(Activity sender)
	{
		sendActivity = sender;
		savedFilesDir = sendActivity.getFilesDir();
	}

	public ArrayList<String> read()
	{
		ArrayList<String> scores = new ArrayList<String>();
		try
		{
			FileInputStream inputStream = sendActivity
					.openFileInput("scores.txt");
			BufferedReader bufferedReader = new BufferedReader(new FileReader(
					new File(savedFilesDir, "scores.txt")));
			String line;
			while ((line = bufferedReader.readLine()) != null)
			{

				scores.add(line);
			}
		} catch (Exception ex)
		{
			ex.printStackTrace();
		}
		return scores;
	}

	public void write(String player, int score)
	{

		List<String> availableFiles = Arrays.asList(savedFilesDir.list());

		if (availableFiles == null || !availableFiles.contains("scores.txt"))
		{
			File file = new File(savedFilesDir, "scores.txt");
			try
			{

				file.createNewFile();
				FileOutputStream outputStream = sendActivity.openFileOutput(
						"scores.txt", Context.MODE_APPEND);
				outputStream.write((player + " - " + score + "\n").getBytes());
			} catch (IOException ex)
			{
				ex.printStackTrace();
			}
		} else
		{
			try
			{
				FileOutputStream outputStream = sendActivity.openFileOutput(
						"scores.txt", Context.MODE_APPEND);
				outputStream.write((player + " " + score).getBytes());

			} catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

}
