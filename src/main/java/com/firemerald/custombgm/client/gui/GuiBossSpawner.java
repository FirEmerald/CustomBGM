package com.firemerald.custombgm.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import org.apache.commons.lang3.mutable.MutableInt;

import com.firemerald.fecore.betterscreens.components.Button;
import com.firemerald.fecore.betterscreens.components.ToggleButton;
import com.firemerald.fecore.betterscreens.components.decoration.FloatingText;
import com.firemerald.fecore.betterscreens.components.scrolling.VerticalScrollBar;
import com.firemerald.fecore.betterscreens.components.scrolling.VerticalScrollableComponentPane;
import com.firemerald.fecore.betterscreens.components.text.BetterTextField;
import com.firemerald.fecore.betterscreens.components.text.CompoundTagField;
import com.firemerald.fecore.betterscreens.components.text.DoubleField;
import com.firemerald.fecore.betterscreens.components.text.LabeledBetterTextField;
import com.firemerald.fecore.networking.FECoreNetwork;
import com.firemerald.fecore.networking.server.BlockEntityGUIClosedPacket;
import com.firemerald.fecore.util.Translator;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public class GuiBossSpawner extends GuiTileEntityOperator
{
	public static class EntityButton extends Button
	{
		public final GuiBossSpawner gui;
		public final ResourceLocation id;

	    public EntityButton(int x, int y, Component buttonText, GuiBossSpawner gui, ResourceLocation id)
	    {
	    	super(x, y, buttonText, null);
	    	this.gui = gui;
	    	this.id = id;
	    	setAction();
	    }

		public EntityButton(int x, int y, int widthIn, int heightIn, Component buttonText, GuiBossSpawner gui, ResourceLocation id)
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
    private MusicTabCompleter tabCompleter;

    public final FloatingText labelSelector, labelSpawn, labelSpawnNBT, labelMusic;
    public final Button spawnRelative, okay, cancel;
    public final DoubleField fieldSpawnX, fieldSpawnY, fieldSpawnZ;
    public final CompoundTagField fieldSpawnNBT;
    public final ToggleButton musicEnabled;
    public final BetterTextField fieldMusic;
    public final LabeledBetterTextField fieldSearch;
    public final VerticalScrollableComponentPane entityButtons;
    public final VerticalScrollBar entityButtonsScroll;

	public String music = "";
	public int priority;
	public boolean disableMusic, relative;
	public int levelOnActive, levelOnKilled;
	public double spawnX, spawnY, spawnZ;
	public CompoundTag nbt = new CompoundTag();

	@SuppressWarnings({ "deprecation", "resource" })
	public GuiBossSpawner(BlockPos pos)
	{
		super(new TranslatableComponent("custombgm.gui.bossspawner"), pos);
		this.font = Minecraft.getInstance().font;
		MutableInt y = new MutableInt(0);
		Registry.ENTITY_TYPE.keySet().forEach(reg -> {
			EntityType<?> type = Registry.ENTITY_TYPE.get(reg);
			if (type.canSummon()) allEntities.add(new EntityButton(0, y.getValue(), 200, y.addAndGet(20), type.getDescription(), this, reg));
		});
		entities.addAll(allEntities);
		/*
		 * <configure shape> <         search         >
		 * Select            !!!
		 * <  select text  >
		 * !!!
		 * !!!
		 * Spawn  <relative>
		 * < x ><  y  >< z >
		 * spawn NBT
		 * <   spawn nbt   >
		 * music < enabled >
		 * <  music name   >
		 * <     okay      > <         cancel         >
		 */
		setupShapeField(0, 0, 200, 20);
		labelSelector = new FloatingText(0, 20, 200, 40, font, Translator.format("custombgm.gui.operator.selector"));
		this.setupSelectorTextField(0, 0, 40, 200, 20);
		labelSpawn = new FloatingText(0, 100, 100, 120, font, Translator.format("custombgm.gui.bossspawner.position"));
		spawnRelative = new Button(100, 100, 100, 120, new TranslatableComponent(relative ? "custombgm.gui.operator.relative" : "custombgm.gui.operator.absolute"), null).setAction(button -> () -> {
			if (relative)
			{
				relative = false;
				button.displayString = new TranslatableComponent("custombgm.gui.operator.absolute");
			}
			else
			{
				relative = true;
				button.displayString = new TranslatableComponent("custombgm.gui.operator.relative");
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

		fieldSearch = new LabeledBetterTextField(font, 200, 0, 220, 20, Translator.translate("custombgm.gui.entities.search"), new TranslatableComponent("custombgm.gui.entities.search.narrate"), (Consumer<String>) this::updateSearch);
		entityButtons = new VerticalScrollableComponentPane(200, 40, 400, 220);
		entityButtonsScroll = new VerticalScrollBar(400, 40, 420, 220, entityButtons);

		okay = new Button(0, 220, 200, 20, new TranslatableComponent("fecore.gui.confirm"), () -> {
			FECoreNetwork.INSTANCE.sendToServer(new BlockEntityGUIClosedPacket(this));
			this.onClose();
		});
		cancel = new Button(200, 220, 400, 20, new TranslatableComponent("fecore.gui.cancel"), () -> {
			this.onClose();
		});
		entityButtons.setScrollBar(entityButtonsScroll);
        this.tabCompleter = new MusicTabCompleter(this.fieldMusic);
	}

	@Override
	public void init()
	{
		int buttonsWidth = Math.min(this.width - 200, 392) - 10;
		int offX = (this.width - buttonsWidth - 210) >> 1;
		int offX2 = offX + 100;
		int offX3 = offX2 + 100;
		int offX4 = offX3 + buttonsWidth;
		int offX5 = offX4 + 10;
		/*
		 * <configure shape> <         search         >
		 * Select            !!!
		 * <  select text  >
		 * !!!
		 * !!!
		 * Spawn  <relative>
		 * < x ><  y  >< z >
		 * spawn NBT
		 * <   spawn nbt   >
		 * music < enabled >
		 * <  music name   >
		 * <     okay      > <         cancel         >
		 */
		configureShape.setSize(offX, 0, offX3, 20);
		labelSelector.setSize(offX, 20, offX3, 40);
		selectorTxt.setSize(offX, 40, offX3, 60);
		labelSpawn.setSize(offX, 100, offX2, 120);
		spawnRelative.setSize(offX2, 100, offX3, 120);
		fieldSpawnX.setSize(offX, 120, offX + 67, 140);
		fieldSpawnY.setSize(offX + 67, 120, offX3 - 67, 140);
		fieldSpawnZ.setSize(offX3 - 67, 120, offX3, 140);
		labelSpawnNBT.setSize(offX, 140, offX3, 160);
		fieldSpawnNBT.setSize(offX, 160, offX3, 180);
		labelMusic.setSize(offX, 180, offX2, 200);
		musicEnabled.setSize(offX2, 180, offX3, 200);
		fieldMusic.setSize(offX, 200, offX3, 220);

		fieldSearch.setSize(offX3, 0, offX5, 20);
		entityButtons.setSize(offX3, 40, offX4, height - 20);
		entityButtonsScroll.setSize(offX4, 40, offX5, height - 20);
		okay.setSize(offX, height - 20, offX3, height);
		cancel.setSize(offX5 - 200, height - 20, offX5, height);
		final int w = entityButtons.x2 - entityButtons.x1; //max 392
		allEntities.forEach(button -> button.setSize(0, button.getY1(), w, button.getY2()));

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

		spawnRelative.displayString = new TranslatableComponent(relative ? "gui.operator.relative" : "gui.operator.absolute");
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