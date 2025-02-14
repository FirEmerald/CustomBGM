package com.firemerald.custombgm.client.gui;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import com.firemerald.fecore.client.gui.components.text.BetterTextField;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MusicSuggestions
{
	final Minecraft minecraft;
	final Screen screen;
	private BetterTextField input;
	final Font font;
	final int lineStartOffset;
	final int suggestionLineLimit;
	final int fillColor;
	@Nullable
	private CompletableFuture<Suggestions> pendingSuggestions;
	@Nullable
	MusicSuggestions.SuggestionsList suggestions;
	private boolean allowSuggestions;
	boolean keepSuggestions;

	public MusicSuggestions(Minecraft mc, Screen screen, BetterTextField input, Font font, int lineStartOffset, int suggestionLineLimit, int fillColor)
	{
		this.minecraft = mc;
		this.screen = screen;
		this.input = input;
		this.font = font;
		this.lineStartOffset = lineStartOffset;
		this.suggestionLineLimit = suggestionLineLimit;
		this.fillColor = fillColor;
	}

	public void setInput(BetterTextField input) {
		this.input = input;
	}

	public void setAllowSuggestions(boolean allowSuggestions)
	{
		this.allowSuggestions = allowSuggestions;
		if (!allowSuggestions) this.suggestions = null;
	}

	public boolean keyPressed(int key, int scancode, int mods)
	{
		if (this.suggestions != null && this.suggestions.keyPressed(key, scancode, mods)) return true;
		else if (input != null && this.screen.getFocused() == this.input && key == 258)
		{
			this.showSuggestions(true);
			return true;
		}
		else return false;
	}

	public boolean mouseScrolled(double scrollY)
	{
		if (this.suggestions != null) {
			this.suggestions.mouseScrolled(Mth.clamp(scrollY, -1.0D, 1.0D));
			return true;
		} else return false;
	}

	public boolean mouseClicked(double mX, double mY, int button)
	{
		if (input != null && this.suggestions != null)
		{
			if (this.suggestions.mouseClicked((int)mX, (int)mY, button))
			{
				return true;
			}
			else
			{
				this.input.setSuggestion(null);
				this.suggestions = null;
				return false;
			}
		}
		else return false;
		//return this.suggestions != null && this.suggestions.mouseClicked((int)mX, (int)mY, button);
	}

	public void showSuggestions(boolean p_93931_)
	{
		if (this.pendingSuggestions != null && this.pendingSuggestions.isDone())
		{
			Suggestions suggestions = this.pendingSuggestions.join();
			if (!suggestions.isEmpty())
			{
				int width = 0;
				for (Suggestion suggestion : suggestions.getList()) width = Math.max(width, this.font.width(suggestion.getText()));
				width = Math.min(width, this.input.getWidth() - 4);
				this.suggestions = new MusicSuggestions.SuggestionsList(this.input.getTrueX1() + 4, this.input.getTrueY2(), width, this.sortSuggestions(suggestions), p_93931_);
			}
		}
	}

	private List<Suggestion> sortSuggestions(Suggestions suggestions)
	{
		String s = this.input.getValue().substring(0, this.input.getCursorPosition());
		String s1 = s.toLowerCase(Locale.ROOT);
		List<Suggestion> list = Lists.newArrayList();
		List<Suggestion> list1 = Lists.newArrayList();
		for(Suggestion suggestion : suggestions.getList())
		{
			if (!suggestion.getText().startsWith(s1) && !suggestion.getText().startsWith("minecraft:" + s1)) list1.add(suggestion);
			else list.add(suggestion);
		}
		list.addAll(list1);
		return list;
	}

	private CompletableFuture<Suggestions> requestCompletions(String prefix, int start)
	{
		SuggestionsBuilder builder = new SuggestionsBuilder(prefix, start);
		Minecraft.getInstance().getSoundManager().getAvailableSounds().stream().map(ResourceLocation::toString).filter(str -> str.startsWith(prefix)).forEach(builder::suggest);
		return builder.buildFuture();
	}

	public void deactivate() {
		if (suggestions != null) {
			if (this.input != null) this.input.setSuggestion(null);
			this.suggestions = null;
		}
	}

	public void updateCommandInfo()
	{
		if (this.input != null) {
			String s = this.input.getValue();
			if (!this.keepSuggestions)
			{
				this.input.setSuggestion((String)null);
				this.suggestions = null;
			}
			StringReader stringreader = new StringReader(s);
			int i = this.input.getCursorPosition();
			int j = stringreader.getCursor();
			if (i >= j && (this.suggestions == null || !this.keepSuggestions))
			{
				this.pendingSuggestions = requestCompletions(this.input.getValue().substring(0, i), 0);
				this.pendingSuggestions.thenRun(() -> {
					if (this.pendingSuggestions.isDone()) this.updateUsageInfo();
				});
			}
		}
	}

	private void updateUsageInfo()
	{
		this.suggestions = null;
		if (this.allowSuggestions && this.minecraft.options.autoSuggestions().get()) this.showSuggestions(false);
	}

	@Nullable
	static String calculateSuggestionSuffix(String p_93928_, String p_93929_)
	{
		return p_93929_.startsWith(p_93928_) ? p_93929_.substring(p_93928_.length()) : null;
	}

	public void render(GuiGraphics guiGraphics, int mX, int mY)
	{
		if (this.suggestions != null) this.suggestions.render(guiGraphics, mX, mY);
	}

	public String getNarrationMessage()
	{
		return this.suggestions != null ? "\n" + this.suggestions.getNarrationMessage() : "";
	}

	@OnlyIn(Dist.CLIENT)
	public class SuggestionsList
	{
		private final Rect2i rect;
		private final String originalContents;
		private final List<Suggestion> suggestionList;
		private int offset;
		private int current;
		private Vec2 lastMouse = Vec2.ZERO;
		private boolean tabCycles;
		private int lastNarratedEntry;

		SuggestionsList(int x, int y, int width, List<Suggestion> suggestions, boolean p_93961_)
		{
			int x1 = x - 1;
			int y1 = y;
			this.rect = new Rect2i(x1, y1, width + 1, Math.min(suggestions.size(), MusicSuggestions.this.suggestionLineLimit) * 12);
			this.originalContents = MusicSuggestions.this.input.getValue();
			this.lastNarratedEntry = p_93961_ ? -1 : 0;
			this.suggestionList = suggestions;
			this.select(0);
		}

		public void render(GuiGraphics guiGraphics, int mX, int mY)
		{
			int i = Math.min(this.suggestionList.size(), MusicSuggestions.this.suggestionLineLimit);
			int j = -0xFFAAAAAA;
			boolean flag = this.offset > 0;
			boolean flag1 = this.suggestionList.size() > this.offset + i;
			boolean flag2 = flag || flag1;
			boolean flag3 = this.lastMouse.x != mX || this.lastMouse.y != mY;
			if (flag3) this.lastMouse = new Vec2(mX, mY);
			if (flag2)
			{
				guiGraphics.fill(this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), MusicSuggestions.this.fillColor);
				guiGraphics.fill(this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, MusicSuggestions.this.fillColor);
				if (flag)
				{
					for(int k = 0; k < this.rect.getWidth(); ++k)
					{
						if (k % 2 == 0)
						{
							guiGraphics.fill(this.rect.getX() + k, this.rect.getY() - 1, this.rect.getX() + k + 1, this.rect.getY(), -1);
						}
					}
				}

				if (flag1)
				{
					for(int i1 = 0; i1 < this.rect.getWidth(); ++i1)
					{
						if (i1 % 2 == 0)
						{
							guiGraphics.fill(this.rect.getX() + i1, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + i1 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
						}
					}
				}
			}

			boolean flag4 = false;

			for(int l = 0; l < i; ++l)
			{
				Suggestion suggestion = this.suggestionList.get(l + this.offset);
				guiGraphics.fill(this.rect.getX(), this.rect.getY() + 12 * l, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * l + 12, MusicSuggestions.this.fillColor);
				if (mX > this.rect.getX() && mX < this.rect.getX() + this.rect.getWidth() && mY > this.rect.getY() + 12 * l && mY < this.rect.getY() + 12 * l + 12)
				{
					if (flag3) this.select(l + this.offset);
					flag4 = true;
				}
				guiGraphics.drawString(MusicSuggestions.this.font, suggestion.getText(), this.rect.getX() + 1, this.rect.getY() + 2 + 12 * l, l + this.offset == this.current ? -256 : j);
			}

			if (flag4)
			{
				Message message = this.suggestionList.get(this.current).getTooltip();
				if (message != null) guiGraphics.renderTooltip(MusicSuggestions.this.font, ComponentUtils.fromMessage(message), mX, mY);
			}

		}

		public boolean mouseClicked(int mX, int mY, int button)
		{
			if (!this.rect.contains(mX, mY)) return false;
			else
			{
				int i = (mY - this.rect.getY()) / 12 + this.offset;
				if (i >= 0 && i < this.suggestionList.size())
				{
					this.select(i);
					this.useSuggestion();
				}
				return true;
			}
		}

		public boolean mouseScrolled(double scrollY)
		{
			int i = (int)(MusicSuggestions.this.minecraft.mouseHandler.xpos() * MusicSuggestions.this.minecraft.getWindow().getGuiScaledWidth() / MusicSuggestions.this.minecraft.getWindow().getScreenWidth());
			int j = (int)(MusicSuggestions.this.minecraft.mouseHandler.ypos() * MusicSuggestions.this.minecraft.getWindow().getGuiScaledHeight() / MusicSuggestions.this.minecraft.getWindow().getScreenHeight());
			if (this.rect.contains(i, j))
			{
				this.offset = Mth.clamp((int)(this.offset - scrollY), 0, Math.max(this.suggestionList.size() - MusicSuggestions.this.suggestionLineLimit, 0));
				return true;
			}
			else return false;
		}

		public boolean keyPressed(int key, int code, int mods)
		{
			if (key == 265)
			{
				this.cycle(-1);
				this.tabCycles = false;
				return true;
			}
			else if (key == 264)
			{
				this.cycle(1);
				this.tabCycles = false;
				return true;
			}
			else if (key == 258)
			{
				if (this.tabCycles) this.cycle(Screen.hasShiftDown() ? -1 : 1);
				this.useSuggestion();
				return true;
			}
			else if (key == 256)
			{
				this.hide();
				return true;
			}
			else return false;
		}

		public void cycle(int amount)
		{
			this.select(this.current + amount);
			int i = this.offset;
			int j = this.offset + MusicSuggestions.this.suggestionLineLimit - 1;
			if (this.current < i)
			{
				this.offset = Mth.clamp(this.current, 0, Math.max(this.suggestionList.size() - MusicSuggestions.this.suggestionLineLimit, 0));
			}
			else if (this.current > j)
			{
				this.offset = Mth.clamp(this.current + MusicSuggestions.this.lineStartOffset - MusicSuggestions.this.suggestionLineLimit, 0, Math.max(this.suggestionList.size() - MusicSuggestions.this.suggestionLineLimit, 0));
			}

		}

		public void select(int index)
		{
			this.current = index;
			if (this.current < 0) this.current += this.suggestionList.size();
			if (this.current >= this.suggestionList.size()) this.current -= this.suggestionList.size();
			Suggestion suggestion = this.suggestionList.get(this.current);
			MusicSuggestions.this.input.setSuggestion(MusicSuggestions.calculateSuggestionSuffix(MusicSuggestions.this.input.getValue(), suggestion.apply(this.originalContents)));
			if (this.lastNarratedEntry != this.current)
			{
				MusicSuggestions.this.minecraft.getNarrator().sayNow(this.getNarrationMessage());
			}
		}

		public void useSuggestion()
		{
			Suggestion suggestion = this.suggestionList.get(this.current);
			MusicSuggestions.this.keepSuggestions = true;
			MusicSuggestions.this.input.setValue(suggestion.apply(this.originalContents));
			int i = suggestion.getRange().getStart() + suggestion.getText().length();
			MusicSuggestions.this.input.setCursorPosition(i);
			MusicSuggestions.this.input.setHighlightPos(i);
			this.select(this.current);
			MusicSuggestions.this.keepSuggestions = false;
			this.tabCycles = true;
		}

		Component getNarrationMessage()
		{
			this.lastNarratedEntry = this.current;
			Suggestion suggestion = this.suggestionList.get(this.current);
			Message message = suggestion.getTooltip();
			return message != null ? Component.translatable("narration.suggestion.tooltip", this.current + 1, this.suggestionList.size(), suggestion.getText(), message) : Component.translatable("narration.suggestion", this.current + 1, this.suggestionList.size(), suggestion.getText());
		}

		public void hide()
		{
			MusicSuggestions.this.suggestions = null;
		}
	}
}