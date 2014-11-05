package net.itca.androidhangman.core;

/**
 * 
 * @author Dylan
 * Android-specific player class.
 */
public class Player
{
	private String name;
	private int score;
	public Player(String _name, int _score)
	{
		score = _score;
		name = _name;
	}
	
	public String getName()
	{
		return name;
	}
	
	public int getScore()
	{
		return score;
	}
}
