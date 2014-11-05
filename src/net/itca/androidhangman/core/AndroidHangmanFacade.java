package net.itca.androidhangman.core;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import net.itca.androidhangman.interfaces.ScoreWriter;
import net.itca.hangman.core.HangmanFacade;
import net.itca.hangman.core.Theme;

/**
 *
 * @author Dylan Hangman facade specifically for the android implementation,
 *         with some extra features unique to android.
 */
public class AndroidHangmanFacade extends HangmanFacade implements Parcelable
{


	private static AndroidHangmanFacade facade;
	private int mData;
	private ScoreWriter scoreWriter;
	private String name;
	private AndroidHangmanFacade()
	{
		super();
	}

	public void setName(String _name)
	{
		name = _name;
	}
	
	public String getName()
	{
		return name;
	}
	public void setScoreWriter(ScoreWriter _scoreWriter)
	{
		scoreWriter = _scoreWriter;
	}
	
	public ScoreWriter getScoreWriter()
	{
		return scoreWriter;
	}
	public static AndroidHangmanFacade getHangmanFacade()
	{
		if (facade == null)
		{
			facade = new AndroidHangmanFacade();
		}
		return facade;
	}

	public int describeContents()
	{
		return 0;
	}

	/** save object in parcel */
	public void writeToParcel(Parcel out, int flags)
	{
		out.writeInt(mData);
	}

	public static final Parcelable.Creator<AndroidHangmanFacade> CREATOR = new Parcelable.Creator<AndroidHangmanFacade>()
	{
		public AndroidHangmanFacade createFromParcel(Parcel in)
		{
			return new AndroidHangmanFacade(in);
		}

		public AndroidHangmanFacade[] newArray(int size)
		{
			return new AndroidHangmanFacade[size];
		}
	};

	/** recreate object from parcel */
	private AndroidHangmanFacade(Parcel in)
	{
		mData = in.readInt();
	}
}
