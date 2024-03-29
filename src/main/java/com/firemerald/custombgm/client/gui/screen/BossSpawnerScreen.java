package com.firemerald.custombgm.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import org.apache.commons.lang3.mutable.MutableInt;

import com.firemerald.custombgm.client.gui.EnumSearchMode;
import com.firemerald.custombgm.client.gui.MusicSuggestions;
import com.firemerald.custombgm.operators.BossSpawnerOperator;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.fecore.FECoreMod;
import com.firemerald.fecore.client.Translator;
import com.firemerald.fecore.client.gui.components.Button;
import com.firemerald.fecore.client.gui.components.ToggleButton;
import com.firemerald.fecore.client.gui.components.decoration.FloatingText;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollBar;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollableComponentPane;
import com.firemerald.fecore.client.gui.components.text.BetterTextField;
import com.firemerald.fecore.client.gui.components.text.CompoundTagField;
import com.firemerald.fecore.client.gui.components.text.DoubleField;
import com.firemerald.fecore.client.gui.components.text.LabeledBetterTextField;
import com.firemerald.fecore.networking.server.BlockEntityGUIClosedPacket;
import com.firemerald.fecore.networking.server.EntityGUIClosedPacket;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class BossSpawnerScreen<O extends BossSpawnerOperator<O, S>, S extends IOperatorSource<O, S>> extends OperatorScreen<O, S>
{
	public class EntityButton extends Button
	{
		public final BossSpawnerScreen<O, S> gui;
		public final ResourceLocation id;

	    public EntityButton(int x, int y, Component buttonText, BossSpawnerScreen<O, S> gui, ResourceLocation id)
	    {
	    	super(x, y, buttonText, null);
	    	this.gui = gui;
	    	this.id = id;
	    	setAction();
	    }

		public EntityButton(int x, int y, int widthIn, int heightIn, Component buttonText, BossSpawnerScreen<O, S> gui, ResourceLocation id)
		{
	    	super(x, y, widthIn, heightIn, buttonText, null);
	    	this.gui = gui;
	    	this.id = id;
	    	setAction();
		}

		private void setAction()
		{
			this.onClick = () -> gui.activeEntity = gui.activeEntity == this ? null : this;
		}

		@Override
	    protected int getHoverState(boolean mouseOver)
	    {
	    	return gui.activeEntity == this ? super.getHoverState(mouseOver) : 0;
	    }
	}

	public final List<EntityButton> allEntities = new ArrayList<>();
	public final List<EntityButton> entities = new ArrayList<>();
	public EntityButton activeEntity;
	public int sup = 0;

    public final FloatingText labelSelector, labelSpawn, labelSpawnNBT, labelMusic;
    public final Button spawnRelative, okay, cancel;
    public final DoubleField fieldSpawnX, fieldSpawnY, fieldSpawnZ;
    public final CompoundTagField fieldSpawnNBT;
    public final ToggleButton musicEnabled;
    public final BetterTextField fieldMusic;
    public final LabeledBetterTextField fieldSearch;
    public final VerticalScrollableComponentPane entityButtons;
    public final VerticalScrollBar entityButtonsScroll;

	public MusicSuggestions suggestions;

	public String music = "";
	public int priority;
	public boolean disableMusic, relative;
	public int levelOnActive, levelOnKilled;
	public double spawnX, spawnY, spawnZ;
	public CompoundTag nbt = new CompoundTag();

	@SuppressWarnings({ "deprecation", "resource", "rawtypes", "unchecked" })
	public BossSpawnerScreen(S source)
	{
		super(new TranslatableComponent("custombgm.gui.bossspawner"), source);
		this.font = Minecraft.getInstance().font;
		MutableInt y = new MutableInt(0);
		Registry.ENTITY_TYPE.keySet().forEach(reg -> {
			EntityType<?> type = Registry.ENTITY_TYPE.get(reg);
			if (type.canSummon()) allEntities.add(new EntityButton(0, y.getValue(), 200, y.addAndGet(20), type.getDescription(), this, reg));
		});
		entities.addAll(allEntities);
		setupShapeField(0, 0, 200, 20);
		labelSelector = new FloatingText(0, 20, 200, 40, font, Translator.format("custombgm.gui.operator.selector"));
		this.setupSelectorTextField(0, 0, 40, 200, 20);
		labelSpawn = new FloatingText(0, 100, 100, 120, font, Translator.format("custombgm.gui.bossspawner.position"));
		spawnRelative = new Button(100, 100, 100, 120, new TranslatableComponent(relative ? "fecore.shapesgui.operator.relative" : "fecore.shapesgui.operator.absolute"), null).setAction(button -> () -> {
			if (relative)
			{
				relative = false;
				button.displayString = new TranslatableComponent("fecore.shapesgui.operator.absolute");
			}
			else
			{
				relative = true;
				button.displayString = new TranslatableComponent("fecore.shapesgui.operator.relative");
			}
		});
		fieldSpawnX = new DoubleField(font, 0, 120, 67, 20, spawnX, new TranslatableComponent("custombgm.gui.bossspawner.position.x"), (DoubleConsumer) (val -> spawnX = val));
		fieldSpawnY = new DoubleField(font, 67, 120, 66, 20, spawnY, new TranslatableComponent("custombgm.gui.bossspawner.position.y"), (DoubleConsumer) (val -> spawnY = val));
		fieldSpawnZ = new DoubleField(font, 133, 120, 67, 20, spawnZ, new TranslatableComponent("custombgm.gui.bossspawner.position.z"), (DoubleConsumer) (val -> spawnZ = val));
		labelSpawnNBT = new FloatingText(0, 140, 200, 160, font, Translator.format("custombgm.gui.bossspawner.nbt"));
		fieldSpawnNBT = new CompoundTagField(font, 0, 160, 200, 20, nbt, new TranslatableComponent("custombgm.gui.bossspawner.nbt.narrate"), (Consumer<CompoundTag>) (val -> this.nbt = val));
		fieldSpawnNBT.setMaxLength(Short.MAX_VALUE);
		fieldSpawnNBT.setNBT(nbt);
		labelMusic = new FloatingText(0, 180, 100, 200, font, Translator.format("custombgm.gui.bgm.music"));
		musicEnabled = new ToggleButton(100, 180, 100, 20, new TranslatableComponent(disableMusic ? "custombgm.gui.operator.disabled" : "custombgm.gui.operator.enabled"), !disableMusic, null).setToggleAction(button -> val -> {
			if (val)
			{
				disableMusic = false;
				button.displayString = new TranslatableComponent("custombgm.gui.operator.enabled");
			}
			else
			{
				disableMusic = true;
				button.displayString = new TranslatableComponent("custombgm.gui.operator.disabled");
			}
		});
		fieldMusic = new BetterTextField(font, 0, 200, 200, 20, music, new TranslatableComponent("custombgm.gui.bgm.music.nmarrate"), (Consumer<String>) (val -> this.music = val));
		fieldMusic.setMaxLength(Short.MAX_VALUE);
		fieldMusic.setResponder(this::onEdited);

		fieldSearch = new LabeledBetterTextField(font, 200, 0, 220, 20, Translator.translate("custombgm.gui.entities.search"), new TranslatableComponent("custombgm.gui.entities.search.narrate"), (Consumer<String>) this::updateSearch);
		entityButtons = new VerticalScrollableComponentPane(200, 40, 400, 220);
		entityButtonsScroll = new VerticalScrollBar(400, 40, 420, 220, entityButtons);

		okay = new Button(0, 220, 200, 20, new TranslatableComponent("fecore.gui.confirm"), () -> {
			FECoreMod.NETWORK.sendToServer(source.isEntity() ? new EntityGUIClosedPacket(this) : new BlockEntityGUIClosedPacket(this));
			this.onClose();
		});
		cancel = new Button(200, 220, 400, 20, new TranslatableComponent("fecore.gui.cancel"), () -> {
			this.onClose();
		});
		entityButtons.setScrollBar(entityButtonsScroll);
	}

	@Override
	public void init()
	{
		int width = Math.min(this.width, 200 + 392 + 10);
		int offX = (this.width - width) >> 1;
		int midX = offX + 200;
		int midMidX = offX + 100;
		int leftMidX = offX + 67;
		int rightMidX = midX - 67;
		int farX = offX + width;
		int listX = farX - 10;
		int buttonWidth = listX - midX;

		/*
		 * <        shape        ><search>
		 * <      sel label      >< list >
		 * <         sel         >< list >
		 * <spawn label><rel/abs >< list >
		 * <  x   ><  y  ><  z   >< list >
		 * <      nbt label      >< list >
		 * <         nbt         >< list >
		 * <mus label><mus enable>< list >
		 * <        music        >< list >
		 * <       confirm       ><cancel>
		 */
		int y = 0;
		configureShape.setSize(offX, y, midX, y + 20);
		fieldSearch.setSize(midX, y, farX, y + 20);
		y += 20;
		labelSelector.setSize(offX, y, midX, y + 20);
		entityButtons.setSize(midX, y, listX, height - 20);
		entityButtonsScroll.setSize(listX, y, farX, height - 20);
		y += 20;
		selectorTxt.setSize(offX, y, midX, y + 20);
		y += 20;
		labelSpawn.setSize(offX, y, midMidX, y + 20);
		spawnRelative.setSize(midMidX, y, midX, y + 20);
		y += 20;
		fieldSpawnX.setSize(offX, y, leftMidX, y + 20);
		fieldSpawnY.setSize(leftMidX, y, rightMidX, y + 20);
		fieldSpawnZ.setSize(rightMidX, y, midX, y + 20);
		y += 20;
		labelSpawnNBT.setSize(offX, y, midX, y + 20);
		y += 20;
		fieldSpawnNBT.setSize(offX, y, midX, y + 20);
		y += 20;
		labelMusic.setSize(offX, y, midMidX, y + 20);
		musicEnabled.setSize(midMidX, y, midX, y + 20);
		y += 20;
		fieldMusic.setSize(offX, y, midX, y + 20);

		okay.setSize(offX, height - 20, offX + 200, height);
		cancel.setSize(farX - 200, height - 20, farX, height);
		allEntities.forEach(button -> button.setSize(0, button.getY1(), buttonWidth, button.getY2()));

		this.addRenderableWidget(configureShape);
		this.addRenderableWidget(labelSelector);
		this.addRenderableWidget(selectorTxt);
		this.addRenderableWidget(labelSpawn);
		this.addRenderableWidget(spawnRelative);
		this.addRenderableWidget(fieldSpawnX);
		this.addRenderableWidget(fieldSpawnY);
		this.addRenderableWidget(fieldSpawnZ);
		this.addRenderableWidget(labelSpawnNBT);
		this.addRenderableWidget(fieldSpawnNBT);
		this.addRenderableWidget(labelMusic);
		this.addRenderableWidget(musicEnabled);
		this.addRenderableWidget(fieldMusic);

		this.addRenderableWidget(fieldSearch);
		this.addRenderableWidget(entityButtons);
		this.addRenderableWidget(entityButtonsScroll);

		addRenderableWidget(okay);
		addRenderableWidget(cancel);
		suggestions = new MusicSuggestions(this.minecraft, this, this.fieldMusic, this.font, 0, 7, Integer.MIN_VALUE);
		suggestions.setAllowSuggestions(true);
		suggestions.updateCommandInfo();
	}

	public void updateSearch()
	{
		updateSearch(fieldSearch.getValue());
	}

	public void updateSearch(String s)
	{
		MutableInt y = new MutableInt(0);
		final int w = entityButtons.x2 - entityButtons.x1; //max 392
		allEntities.forEach(entityButtons::removeComponent);
		entities.clear();
		for (int i = 0; i < allEntities.size(); i++)
		{
			EntityButton button = allEntities.get(i);
			if (EnumSearchMode.matchString(button.id, button.displayString.getContents(), s))
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
	public boolean keyPressed(int key, int scancode, int mods)
	{
		if (this.suggestions.keyPressed(key, scancode, mods)) return true;
		else return super.keyPressed(key, scancode, mods);
	}

	@Override
	public boolean mouseScrolled(double mX, double mY, double scrollY)
	{
		return this.suggestions.mouseScrolled(scrollY) ? true : super.mouseScrolled(mX, mY, scrollY);
	}

	@Override
	public boolean mouseClicked(double mX, double mY, int button)
	{
		return this.suggestions.mouseClicked(mX, mY, button) ? true : super.mouseClicked(mX, mY, button);
	}

	private void onEdited(String p_97689_)
	{
		if (suggestions != null) this.suggestions.updateCommandInfo();
	}

	@Override
	public void render(PoseStack pose, int mx, int my, float partialTicks, boolean canHover)
	{
		this.renderBackground(pose);
		super.render(pose, mx, my, partialTicks, canHover);
		this.suggestions.render(pose, mx, my);
	}

	@Override
	public void read(FriendlyByteBuf buf)
	{
		super.read(buf);
		music = buf.readUtf();
		priority = buf.readInt();
		relative = buf.readBoolean();
		disableMusic = buf.readBoolean();
		spawnX = buf.readDouble();
		spawnY = buf.readDouble();
		spawnZ = buf.readDouble();
		String toSpawn = buf.readUtf();
		if (toSpawn.isEmpty()) activeEntity = null;
		else
		{
			activeEntity = null;
			ResourceLocation id = new ResourceLocation(toSpawn);
			allEntities.forEach(button -> {
				if (button.id.equals(id))
				{
					activeEntity = button;
					return;
				}
			});
		}
		nbt = buf.readAnySizeNbt();
		byte levels = buf.readByte();
		levelOnActive = levels & 0xF;
		levelOnKilled = (levels >> 4) & 0xF;

		spawnRelative.displayString = new TranslatableComponent(relative ? "fecore.shapesgui.operator.relative" : "fecore.shapesgui.operator.absolute");
		fieldSpawnX.setDouble(spawnX);
		fieldSpawnY.setDouble(spawnY);
		fieldSpawnZ.setDouble(spawnZ);
		fieldSpawnNBT.setNBT(nbt);
		musicEnabled.state = !disableMusic;
		musicEnabled.displayString = new TranslatableComponent(disableMusic ? "custombgm.gui.operator.disabled" : "custombgm.gui.operator.enabled");
		fieldMusic.setString(music);
		this.updateSearch();
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		super.write(buf);
		buf.writeUtf(music == null ? "" : music.toString());
		buf.writeInt(priority);
		buf.writeBoolean(relative);
		buf.writeBoolean(disableMusic);
		buf.writeDouble(spawnX);
		buf.writeDouble(spawnY);
		buf.writeDouble(spawnZ);
		buf.writeUtf(activeEntity == null ? "" : activeEntity.id.toString());
		buf.writeNbt(nbt);
		buf.writeByte(levelOnActive | (levelOnKilled << 4));
	}
}