package com.firemerald.custombgm.client.gui.screen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.apache.commons.lang3.mutable.MutableInt;

import com.firemerald.custombgm.CustomBGM;
import com.firemerald.custombgm.client.gui.EnumSearchMode;
import com.firemerald.custombgm.operators.EntityTesterOperator;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.fecore.FECoreMod;
import com.firemerald.fecore.client.gui.components.Button;
import com.firemerald.fecore.client.gui.components.ToggleButton;
import com.firemerald.fecore.client.gui.components.decoration.FloatingText;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollBar;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollableComponentPane;
import com.firemerald.fecore.client.gui.components.text.IntegerField;
import com.firemerald.fecore.client.gui.components.text.LabeledBetterTextField;
import com.firemerald.fecore.network.server.BlockEntityGUIClosedPacket;
import com.firemerald.fecore.network.server.EntityGUIClosedPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityTesterScreen<O extends EntityTesterOperator<O, S>, S extends IOperatorSource<O, S>> extends OperatorScreen<O, S> {
	public static class EntityButton extends ToggleButton {
		public final Set<ResourceLocation> set;
		public final ResourceLocation id;
		public final String unformattedString;

	    public EntityButton(int x, int y, Component buttonText, Set<ResourceLocation> set, ResourceLocation id) {
	    	super(x, y, buttonText, null);
	    	this.set = set;
	    	this.id = id;
	    	StringBuilder builder = new StringBuilder();
	    	buttonText.visit(text -> {
	    		builder.append(text);
	    		return Optional.empty();
	    	});
	    	unformattedString = builder.toString();
	    	setAction();
	    }

		public EntityButton(int x, int y, int widthIn, int heightIn, Component buttonText, Set<ResourceLocation> set, ResourceLocation id) {
	    	super(x, y, widthIn, heightIn, buttonText, null);
	    	this.set = set;
	    	this.id = id;
	    	StringBuilder builder = new StringBuilder();
	    	buttonText.visit(text -> {
	    		builder.append(text);
	    		return Optional.empty();
	    	});
	    	unformattedString = builder.toString();
	    	setAction();
		}

		private void setAction() {
			this.setToggleAction(state -> {
				if (state) set.add(id);
				else set.remove(id);
			});
		}
	}

	public final List<EntityButton> allEntities = new ArrayList<>();
	public final List<EntityButton> entities = new ArrayList<>();
	public final Set<ResourceLocation> enabled = new HashSet<>();
	short min, max;
	public int sup = 0;

    public final FloatingText labelSelector, labelMin, labelMax;
    public final Button selectAll, selectNone, okay, cancel;
    public final IntegerField fieldMin, fieldMax;
    public final LabeledBetterTextField fieldSearch;
    public final VerticalScrollableComponentPane entityButtons;
    public final VerticalScrollBar entityButtonsScroll;

	@SuppressWarnings({ "resource", "rawtypes", "unchecked" })
	public EntityTesterScreen(S source)
	{
		super(Component.translatable("custombgm.gui.entitytester"), source);
		this.font = Minecraft.getInstance().font;
		MutableInt y = new MutableInt(0);
		ForgeRegistries.ENTITY_TYPES.getEntries().forEach(entry -> {
			ResourceLocation reg = entry.getKey().location();
			EntityType<?> type = entry.getValue();
			allEntities.add(new EntityButton(0, y.getValue(), 200, y.addAndGet(20), type.getDescription(), enabled, reg));
		});
		entities.addAll(allEntities);
		setupShapeField(0, 0, 200, 20);
		labelSelector = new FloatingText(0, 20, 200, 40, font, I18n.get("custombgm.gui.operator.selector"));
		this.setupSelectorTextField(0, 0, 40, 200, 20);
		labelMin = new FloatingText(0, 100, 200, 120, font, I18n.get("custombgm.gui.entitytester.min"));
		fieldMin = new IntegerField(font, 0, 120, 200, 20, Short.MAX_VALUE, Component.translatable("custombgm.gui.entitytester.min.narrate"), (IntConsumer) (val -> this.min = (short) val));
		labelMax = new FloatingText(0, 140, 200, 160, font, I18n.get("custombgm.gui.entitytester.max"));
		fieldMax = new IntegerField(font, 0, 160, 200, 20, Short.MAX_VALUE, Component.translatable("custombgm.gui.entitytester.max.narrate"), (IntConsumer) (val -> this.max = (short) val));
		selectAll = new Button(200, 0, 110, 20, Component.translatable("custombgm.gui.entitytester.selectall"), () -> {
			entities.forEach(button -> button.state = true); //set all enabled
			entities.stream().map(button -> button.id).forEach(enabled::add); //add all to enabled list
		});
		selectNone = new Button(310, 0, 110, 20, Component.translatable("custombgm.gui.entitytester.selectnone"), () -> {
			entities.forEach(button -> button.state = false); //set all to disabled
			enabled.clear(); //clear the enabled list
		});
		fieldSearch = new LabeledBetterTextField(font, 200, 20, 220, 20, Component.translatable("custombgm.gui.entities.search"), Component.translatable("custombgm.gui.entities.search.narrate"), (Consumer<String>) this::updateSearch);
		entityButtons = new VerticalScrollableComponentPane(200, 60, 400, 180);
		entityButtonsScroll = new VerticalScrollBar(400, 60, 420, 180, entityButtons);

		okay = new Button(0, 180, 200, 20, Component.translatable("fecore.gui.confirm"), () -> {
			FECoreMod.NETWORK.sendToServer(source.isEntity() ? new EntityGUIClosedPacket(this) : new BlockEntityGUIClosedPacket(this));
			EntityTesterScreen.this.onClose();
		});
		cancel = new Button(200, 180, 200, 20, Component.translatable("fecore.gui.cancel"), () -> {
			EntityTesterScreen.this.onClose();
		});
		entityButtons.setScrollBar(entityButtonsScroll);
	}

	public void initEnabled()
	{
		allEntities.forEach(button -> button.state = enabled.contains(button.id));
	}

	@Override
	public void init()
	{
		int width = Math.min(this.width, 200 + 392 + 10);
		int offX = (this.width - width) >> 1;
		int midX = offX + 200;
		int selectMidX = midX + ((width - 200) >> 1);
		int farX = offX + width;
		int listX = farX - 10;
		int buttonWidth = listX - midX;

		/*
		 * <  shape  ><sel all><sel none>
		 * <sel label><     search      >
		 * <   sel   ><      list       >
		 * <min label><      list       >
		 * <   min   ><      list       >
		 * <max label><      list       >
		 * <   max   ><      list       >
		 * < confirm ><     cancel      >
		 */

		int y = 0;
		configureShape.setSize(offX, y, midX, y + 20);
		selectAll.setSize(midX, y, selectMidX, y + 20);
		selectNone.setSize(selectMidX, y, farX, y + 20);
		y += 20;
		labelSelector.setSize(offX, y, midX, y + 20);
		fieldSearch.setSize(midX, y, farX, y + 20);
		y += 20;
		selectorTxt.setSize(offX, y, midX, y + 20);
		entityButtons.setSize(midX, y, listX, this.height - 20);
		entityButtonsScroll.setSize(listX, y, farX, this.height - 20);
		y += 20;
		labelMin.setSize(offX, y, midX, y + 20);
		y += 20;
		fieldMin.setSize(offX, y, midX, y + 20);
		y += 20;
		labelMax.setSize(offX, y, midX, y + 20);
		y += 20;
		fieldMax.setSize(offX, y, midX, y + 20);



		okay.setSize(offX, height - 20, offX + 200, height);
		cancel.setSize(farX - 200, height - 20, farX, height);
		allEntities.forEach(button -> button.setSize(0, button.getY1(), buttonWidth, button.getY2()));

		this.addRenderableWidget(configureShape);
		this.addRenderableWidget(labelSelector);
		this.addRenderableWidget(selectorTxt);
		this.addRenderableWidget(labelMin);
		this.addRenderableWidget(fieldMin);
		this.addRenderableWidget(labelMax);
		this.addRenderableWidget(fieldMax);
		this.addRenderableWidget(selectAll);
		this.addRenderableWidget(selectNone);
		this.addRenderableWidget(fieldSearch);
		this.addRenderableWidget(entityButtons);
		this.addRenderableWidget(entityButtonsScroll);
		addRenderableWidget(okay);
		addRenderableWidget(cancel);
	}

	public void updateSearch()
	{
		updateSearch(fieldSearch.getValue());
	}

	public void updateSearch(String s)
	{
		MutableInt y = new MutableInt(0);
		final int w = entityButtons.getWidth(); //max 392
		allEntities.forEach(entityButtons::removeComponent);
		entities.clear();
		for (int i = 0; i < allEntities.size(); i++)
		{
			EntityButton button = allEntities.get(i);
			if (EnumSearchMode.matchString(button.id, button.unformattedString, s.toLowerCase(Locale.ROOT)))
			{
				button.setSize(0, y.getValue(), w, y.addAndGet(20));
				entityButtons.addComponent(button);
				entities.add(button);
			}
		}
		entityButtons.updateComponentSize();
		entityButtons.updateScrollSize();
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
		enabled.clear();
		min = (short) buf.readVarInt();
		max = (short) buf.readVarInt();
		int count = buf.readVarInt();
		for (int i = 0; i < count; i++)
		{
			ResourceLocation name = buf.readResourceLocation();
			if (allEntities.stream().anyMatch(button -> button.id.equals(name))) enabled.add(name);
			else CustomBGM.LOGGER.warn("[EntityTesterScreen]: Invalid entity ID: " + name + ", ignoring");
		}
		this.fieldMin.setInteger(min);
		this.fieldMax.setInteger(max);
		allEntities.forEach(button -> button.state = enabled.contains(button.id));
		this.updateSearch();
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		super.write(buf);
		buf.writeVarInt(min);
		buf.writeVarInt(max);
		buf.writeVarInt(enabled.size());
		enabled.forEach(buf::writeResourceLocation);
	}
}