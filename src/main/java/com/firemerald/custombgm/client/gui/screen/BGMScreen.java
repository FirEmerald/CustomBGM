package com.firemerald.custombgm.client.gui.screen;

import java.util.function.IntConsumer;

import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.operators.BGMOperator;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.fecore.FECoreMod;
import com.firemerald.fecore.client.gui.EnumTextAlignment;
import com.firemerald.fecore.client.gui.components.Button;
import com.firemerald.fecore.client.gui.components.decoration.FloatingText;
import com.firemerald.fecore.client.gui.components.text.IntegerField;
import com.firemerald.fecore.network.server.BlockEntityGUIClosedPacket;
import com.firemerald.fecore.network.server.EntityGUIClosedPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;

public class BGMScreen<O extends BGMOperator<O, S>, S extends IOperatorSource<O, S>> extends OperatorScreen<O, S>
{
	public BgmDistribution music = BgmDistribution.EMPTY;
	public int priority;

    public final Button okay, cancel, musicButton;
    public final FloatingText piorStr, selStr;
	public final IntegerField priorTxt;

	@SuppressWarnings({ "resource", "rawtypes", "unchecked" })
	public BGMScreen(S source)
	{
		super(Component.translatable("custombgm.gui.bgm"), source);
		this.font = Minecraft.getInstance().font;
		setupShapeField(0, 0, 200, 20);
		musicButton = new Button(200, 0, 200, 20, Component.translatable("custombgm.gui.music"), () -> new MusicScreen(music, val -> music = val).activate());
		selStr = new FloatingText(0, 20, 200, 40, font, I18n.get("custombgm.gui.operator.selector"), EnumTextAlignment.CENTER);
		setupSelectorTextField(0, 0, 41, 198, 18);

		piorStr = new FloatingText(200, 20, 400, 60, font, I18n.get("custombgm.gui.bgm.priority"), EnumTextAlignment.CENTER);
		priorTxt = new IntegerField(font, 201, 81, 198, 18, priority, Component.translatable("custombgm.gui.bgm.priority.narrate"), (IntConsumer) (v -> priority = v));
		okay = new Button(0, 100, 200, 120, Component.translatable("fecore.gui.confirm"), () -> {
			FECoreMod.NETWORK.sendToServer(source.isEntity() ? new EntityGUIClosedPacket(this) : new BlockEntityGUIClosedPacket(this));
			this.onClose();
		});
		cancel = new Button(200, 100, 400, 120, Component.translatable("fecore.gui.cancel"), () -> {
			this.onClose();
		});
	}

	@Override
	public void init()
	{
		/*
		 * <    shape     ><    music     >
		 * <selector label><priority label>
		 * <   selector   ><   priority   >
		 * <   confirm    ><    cancel    >
		 */
		int offX = (width - 400) >> 1;
		int offY = (height - 100) >> 1;
		int y = offY;
		configureShape.setSize(offX, y, offX + 200, y + 20);
		musicButton.setSize(offX + 200, y, offX + 400, y + 20);
		y += 20;
		selStr.setSize(offX, y, offX + 200, y + 20);
		piorStr.setSize(offX + 200, y, offX + 400, y + 20);
		y += 20;
		selectorTxt.setSize(offX, y, offX + 200, y + 20);
		priorTxt.setSize(offX + 200, y, offX + 400, y + 20);
		y += 20;
		okay.setSize(offX, y, offX + 200, y + 20);
		cancel.setSize(offX + 200, y, offX + 400, y + 20);

		addRenderableWidget(configureShape);

		addRenderableWidget(selStr);
		addRenderableWidget(selectorTxt);

		addRenderableWidget(musicButton);

		addRenderableWidget(piorStr);
		addRenderableWidget(priorTxt);

		addRenderableWidget(okay);
		addRenderableWidget(cancel);
	}

	@Override
	public void render(GuiGraphics guiGraphics, int mx, int my, float partialTicks, boolean canHover)
	{
		this.renderBackground(guiGraphics);
		super.render(guiGraphics, mx, my, partialTicks, canHover);
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		super.read(buf);
		music = BgmDistribution.STREAM_CODEC.decode(buf);
		priority = buf.readInt();
		priorTxt.setInteger(priority);
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		super.write(buf);
		BgmDistribution.STREAM_CODEC.encode(buf, music);
		buf.writeInt(priority);
	}
}