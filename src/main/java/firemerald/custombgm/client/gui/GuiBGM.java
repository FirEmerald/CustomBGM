package firemerald.custombgm.client.gui;

import java.io.IOException;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.lwjgl.input.Keyboard;

import firemerald.api.betterscreens.EnumTextAlignment;
import firemerald.api.betterscreens.components.Button;
import firemerald.api.betterscreens.components.decoration.FloatingText;
import firemerald.api.betterscreens.components.text.BetterTextField;
import firemerald.api.betterscreens.components.text.IntegerField;
import firemerald.api.core.client.Translator;
import firemerald.custombgm.Main;
import firemerald.custombgm.networking.server.TileGUIClosedPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class GuiBGM extends GuiTileEntityOperator
{
	int offX, offY;
	public String music = "";
	public int priority;
    private MusicTabCompleter tabCompleter;

    public final Button okay, cancel;
    public final FloatingText musStr, piorStr, selStr, selNBTStr;
    public final BetterTextField musicTxt;
	public final IntegerField priorTxt;

	public GuiBGM(BlockPos pos)
	{
		super(pos);
		this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
		addElement(setupShapeField(0, 0, 200, 20));
		addElement(selStr = new FloatingText(0, 20, 200, 40, fontRenderer, Translator.format("gui.custombgm.operator.selector"), EnumTextAlignment.CENTER));
		addElement(setupSelectorTextField(0, 0, 41, 198, 18));
		addElement(selNBTStr = new FloatingText(0, 60, 200, 80, fontRenderer, Translator.format("gui.custombgm.operator.nbt"), EnumTextAlignment.CENTER));
		addElement(setupSelectorNBTField(0, 0, 81, 198, 18));
		addElement(musStr = new FloatingText(200, 20, 400, 40, fontRenderer, Translator.format("gui.custombgm.bgm.music"), EnumTextAlignment.CENTER));
		addElement(musicTxt = new BetterTextField(1, fontRenderer, 201, 41, 198, 18, (Consumer<String>) (str -> music = str)));
		musicTxt.setMaxStringLength(Short.MAX_VALUE);
		addElement(piorStr = new FloatingText(200, 20, 400, 60, fontRenderer, Translator.format("gui.custombgm.bgm.priority"), EnumTextAlignment.CENTER));
		addElement(priorTxt = new IntegerField(2, fontRenderer, 201, 81, 198, 18, priority, (IntConsumer) (v -> priority = v)));
		addElement(okay = new Button(0, 100, 200, 120, Translator.format("gui.done"), () -> {
			Main.network().sendToServer(new TileGUIClosedPacket(this));
			mc.setIngameFocus();
		}));
		addElement(cancel = new Button(200, 100, 400, 120, Translator.format("gui.cancel"), () -> {
			mc.setIngameFocus();
		}));
        this.tabCompleter = new MusicTabCompleter(this.musicTxt);
	}

	@Override
	public void initGui()
	{
		offX = (width - 400) >> 1;
		offY = (height - 120) >> 1;
		configureShape.setSize(offX, offY, offX + 200, offY + 20);
		selStr.setSize(offX, offY + 20, offX + 200, offY + 40);
		selectorTxt.setSize(offX, offY + 40, offX + 200, offY + 60);
		selNBTStr.setSize(offX, offY + 60, offX + 200, offY + 80);
		selectorNBTTxt.setSize(offX, offY + 80, offX + 200, offY + 100);
		musStr.setSize(offX + 200, offY + 20, offX + 400, offY + 40);
		musicTxt.setSize(offX + 200, offY + 40, offX + 400, offY + 60);
		piorStr.setSize(offX + 200, offY + 60, offX + 400, offY + 80);
		priorTxt.setSize(offX + 200, offY + 80, offX + 400, offY + 100);
		okay.setSize(offX, offY + 100, offX + 200, offY + 120);
		cancel.setSize(offX + 200, offY + 100, offX + 400, offY + 120);
	}

	@Override
	protected void keyTyped(char chr, int code) throws IOException
	{
        this.tabCompleter.resetRequested();
        if (code == Keyboard.KEY_TAB) this.tabCompleter.complete();
        else this.tabCompleter.resetDidComplete();
		super.keyTyped(chr, code);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks, boolean canHover)
	{
		GlStateManager.color(1f, 1f, 1f, 1f);
		this.drawBackground(0);
		GlStateManager.pushMatrix();
		GlStateManager.translate(0, 0, 1);
		super.drawScreen(mouseX, mouseY, partialTicks, canHover);
		GlStateManager.popMatrix();
	}

	@Override
	public void read(ByteBuf buf)
	{
		super.read(buf);
		music = ByteBufUtils.readUTF8String(buf);
		priority = buf.readInt();
		musicTxt.setString(music);
		priorTxt.setInteger(priority);
	}

	@Override
	public void write(ByteBuf buf)
	{
		super.write(buf);
		ByteBufUtils.writeUTF8String(buf, music.toString());
		buf.writeInt(priority);
	}
}