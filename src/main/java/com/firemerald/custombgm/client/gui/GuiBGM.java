package com.firemerald.custombgm.client.gui;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

import com.firemerald.fecore.betterscreens.EnumTextAlignment;
import com.firemerald.fecore.betterscreens.components.Button;
import com.firemerald.fecore.betterscreens.components.decoration.FloatingText;
import com.firemerald.fecore.betterscreens.components.text.BetterTextField;
import com.firemerald.fecore.betterscreens.components.text.IntegerField;
import com.firemerald.fecore.networking.FECoreNetwork;
import com.firemerald.fecore.networking.server.BlockEntityGUIClosedPacket;
import com.firemerald.fecore.util.Translator;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TranslatableComponent;

public class GuiBGM extends GuiTileEntityOperator
{
	int offX, offY;
	public String music = "";
	public int priority;
    private MusicTabCompleter tabCompleter;

    public final Button okay, cancel;
    public final FloatingText musStr, piorStr, selStr;
    public final BetterTextField musicTxt;
	public final IntegerField priorTxt;

	@SuppressWarnings("resource")
	public GuiBGM(BlockPos pos)
	{
		super(new TranslatableComponent("custombgm.gui.bgm"), pos);
		this.font = Minecraft.getInstance().font;
		setupShapeField(0, 0, 200, 20);
		selStr = new FloatingText(0, 20, 200, 40, font, Translator.format("gui.custombgm.operator.selector"), EnumTextAlignment.CENTER);
		setupSelectorTextField(0, 0, 41, 198, 18);
		musStr = new FloatingText(200, 20, 400, 40, font, Translator.format("gui.custombgm.bgm.music"), EnumTextAlignment.CENTER);
		musicTxt = new BetterTextField(font, 201, 41, 198, 18, new TranslatableComponent("custombgm.gui.bgm.music.narrate"), (Consumer<String>) (str -> music = str));
		musicTxt.setMaxLength(Short.MAX_VALUE);
		piorStr = new FloatingText(200, 20, 400, 60, font, Translator.format("gui.custombgm.bgm.priority"), EnumTextAlignment.CENTER);
		priorTxt = new IntegerField(font, 201, 81, 198, 18, priority, new TranslatableComponent("custombgm.gui.bgm.priority.narrate"), (IntConsumer) (v -> priority = v));
		okay = new Button(0, 100, 200, 120, new TranslatableComponent("fecore.gui.confirm"), () -> {
			FECoreNetwork.INSTANCE.sendToServer(new BlockEntityGUIClosedPacket(this));
			this.onClose();
		});
		cancel = new Button(200, 100, 400, 120, new TranslatableComponent("fecore.gui.cancel"), () -> {
			this.onClose();
		});
        this.tabCompleter = new MusicTabCompleter(this.musicTxt);
	}

	@Override
	public void init()
	{
		offX = (width - 400) >> 1;
		offY = (height - 120) >> 1;
		configureShape.setSize(offX, offY, offX + 200, offY + 20);
		selStr.setSize(offX, offY + 20, offX + 200, offY + 40);
		selectorTxt.setSize(offX, offY + 40, offX + 200, offY + 60);
		musStr.setSize(offX + 200, offY + 20, offX + 400, offY + 40);
		musicTxt.setSize(offX + 200, offY + 40, offX + 400, offY + 60);
		piorStr.setSize(offX + 200, offY + 60, offX + 400, offY + 80);
		priorTxt.setSize(offX + 200, offY + 80, offX + 400, offY + 100);
		okay.setSize(offX, offY + 100, offX + 200, offY + 120);
		cancel.setSize(offX + 200, offY + 100, offX + 400, offY + 120);
		addRenderableWidget(configureShape);
		addRenderableWidget(selStr);
		addRenderableWidget(selectorTxt);
		addRenderableWidget(musStr);
		addRenderableWidget(musicTxt);
		addRenderableWidget(piorStr);
		addRenderableWidget(priorTxt);
		addRenderableWidget(okay);
		addRenderableWidget(cancel);
	}

	@Override
	public boolean keyPressed(int key, int scancode, int mods)
	{
        this.tabCompleter.resetRequested();
        if (key == InputConstants.KEY_TAB && !hasControlDown())
        {
        	this.tabCompleter.complete();
        	return true;
        }
        else this.tabCompleter.resetDidComplete();
        return super.keyPressed(key, scancode, mods);
	}
	   
	@Override
	public void render(PoseStack pose, int mx, int my, float partialTicks, boolean canHover)
	{
		this.renderBackground(pose);
		super.render(pose, mx, my, partialTicks, canHover);
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		super.read(buf);
		music = buf.readUtf();
		priority = buf.readInt();
		musicTxt.setString(music);
		priorTxt.setInteger(priority);
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		super.write(buf);
		buf.writeUtf(music.toString());
		buf.writeInt(priority);
	}
}