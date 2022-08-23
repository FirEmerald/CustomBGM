package com.firemerald.custombgm.client.gui;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.minecraft.client.gui.components.EditBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.firemerald.custombgm.client.audio.LoopingSounds;

@OnlyIn(Dist.CLIENT)
public class MusicTabCompleter
{
    /** The {@link EditBox} that is backing this {@link MusicTabCompleter} */
    protected final EditBox textField;
    protected boolean didComplete;
    protected boolean requestedCompletions;
    protected int completionIdx;
    protected List<String> completions = Lists.<String>newArrayList();

    public MusicTabCompleter(EditBox textFieldIn)
    {
        this.textField = textFieldIn;
    }

    /**
     * Called when tab key pressed. If it's the first time we tried to complete this string, we ask the server for
     * completions. When the server responds, this method gets called again (via setCompletions).
     */
    public void complete()
    {
        if (this.didComplete)
        {
            this.textField.deleteWords(0);
            this.textField.deleteWords(this.textField.getWordPosition(-1, this.textField.getCursorPosition(), false) - this.textField.getCursorPosition());
            if (this.completionIdx >= this.completions.size()) this.completionIdx = 0;
        }
        else
        {
            int i = this.textField.getWordPosition(-1, this.textField.getCursorPosition(), false);
            this.completions.clear();
            this.completionIdx = 0;
            String s = this.textField.getValue().substring(0, this.textField.getCursorPosition());
            this.requestCompletions(s);
            if (this.completions.isEmpty()) return;
            this.didComplete = true;
            this.textField.deleteWords(i - this.textField.getCursorPosition());
        }

        this.textField.insertText(this.completions.get(this.completionIdx = (this.completionIdx + 1) % this.completions.size()));
    }

    private void requestCompletions(String prefix)
    {
        this.requestedCompletions = true;
        this.setCompletions(LoopingSounds.allSoundNames.stream().filter(str -> str.startsWith(prefix)).collect(Collectors.toList()));
    }

    /**
     * Only actually sets completions if they were requested (via requestCompletions)
     */
    public void setCompletions(List<String> newCompl)
    {
        if (this.requestedCompletions)
        {
            this.didComplete = false;
            this.completions.clear();
            for (String s : newCompl) if (!s.isEmpty()) this.completions.add(s);
            String s1 = this.textField.getValue().substring(this.textField.getWordPosition(-1, this.textField.getCursorPosition(), false));
            String s2 = StringUtils.getCommonPrefix(newCompl.toArray(new String[newCompl.size()]));
            if (!s2.isEmpty() && !s1.equalsIgnoreCase(s2))
            {
                this.textField.deleteWords(0);
                this.textField.deleteWords(this.textField.getWordPosition(-1, this.textField.getCursorPosition(), false) - this.textField.getCursorPosition());
                this.textField.insertText(s2);
            }
            else if (!this.completions.isEmpty())
            {
                this.didComplete = true;
                this.complete();
            }
        }
    }

    /**
     * Called when new text is entered, or backspace pressed
     */
    public void resetDidComplete()
    {
        this.didComplete = false;
    }

    public void resetRequested()
    {
        this.requestedCompletions = false;
    }
}