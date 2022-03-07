package firemerald.custombgm.client.gui;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import firemerald.custombgm.client.audio.LoopingSounds;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MusicTabCompleter
{
    /** The {@link GuiTextField} that is backing this {@link MusicTabCompleter} */
    protected final GuiTextField textField;
    protected boolean didComplete;
    protected boolean requestedCompletions;
    protected int completionIdx;
    protected List<String> completions = Lists.<String>newArrayList();

    public MusicTabCompleter(GuiTextField textFieldIn)
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
            this.textField.deleteFromCursor(0);
            this.textField.deleteFromCursor(this.textField.getNthWordFromPosWS(-1, this.textField.getCursorPosition(), false) - this.textField.getCursorPosition());
            if (this.completionIdx >= this.completions.size()) this.completionIdx = 0;
        }
        else
        {
            int i = this.textField.getNthWordFromPosWS(-1, this.textField.getCursorPosition(), false);
            this.completions.clear();
            this.completionIdx = 0;
            String s = this.textField.getText().substring(0, this.textField.getCursorPosition());
            this.requestCompletions(s);
            if (this.completions.isEmpty()) return;
            this.didComplete = true;
            this.textField.deleteFromCursor(i - this.textField.getCursorPosition());
        }

        this.textField.writeText(net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes(this.completions.get(this.completionIdx = (this.completionIdx + 1) % this.completions.size())));
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
            String s1 = this.textField.getText().substring(this.textField.getNthWordFromPosWS(-1, this.textField.getCursorPosition(), false));
            String s2 = org.apache.commons.lang3.StringUtils.getCommonPrefix(newCompl.toArray(new String[newCompl.size()]));
            s2 = net.minecraft.util.text.TextFormatting.getTextWithoutFormattingCodes(s2);
            if (!s2.isEmpty() && !s1.equalsIgnoreCase(s2))
            {
                this.textField.deleteFromCursor(0);
                this.textField.deleteFromCursor(this.textField.getNthWordFromPosWS(-1, this.textField.getCursorPosition(), false) - this.textField.getCursorPosition());
                this.textField.writeText(s2);
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