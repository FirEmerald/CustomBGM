package com.firemerald.custombgm.api;

public abstract class BGMProvider implements Comparable<BGMProvider>, ICustomMusic
{
	public final int priority;
	
	public BGMProvider(int priority)
	{
		this.priority = priority;
	}
	
	public final int compareTo(BGMProvider other)
	{
		return priority - other.priority;
	}
}