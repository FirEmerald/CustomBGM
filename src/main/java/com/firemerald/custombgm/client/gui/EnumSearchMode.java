package com.firemerald.custombgm.client.gui;

import java.util.Locale;

import net.minecraft.resources.ResourceLocation;

public enum EnumSearchMode
{
	MOD('@') {
		@Override
		public boolean matches(ResourceLocation id, String name, String toMatch)
		{
			return id.getNamespace().toLowerCase(Locale.ROOT).contains(toMatch);
		}
	},
	ID('#') {
		@Override
		public boolean matches(ResourceLocation id, String name, String toMatch)
		{
			return id.toString().toLowerCase(Locale.ROOT).contains(toMatch);
		}
	},
	NAME('$') {
		@Override
		public boolean matches(ResourceLocation id, String name, String toMatch)
		{
			return name.toLowerCase(Locale.ROOT).contains(toMatch);
		}
	};

	public final char id;

	EnumSearchMode(char id)
	{
		this.id = id;
	}

	public abstract boolean matches(ResourceLocation id, String name, String toMatch);

	public static boolean matchString(ResourceLocation id, String name, String toMatch)
	{
		if (toMatch.isEmpty()) return true;
		EnumSearchMode mode = null;
		if (toMatch.length() > 1)
		{
			char c = toMatch.charAt(0);
			for (EnumSearchMode searchMode : values()) if (searchMode.id == c)
			{
				toMatch = toMatch.substring(1);
				mode = searchMode;
				break;
			}
		}
		return mode == null ? id.toString().toLowerCase(Locale.ROOT).contains(toMatch) || name.toLowerCase(Locale.ROOT).contains(toMatch) : mode.matches(id, name, toMatch);
	}
}