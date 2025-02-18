package com.firemerald.custombgm.client.gui.screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;

import org.apache.commons.lang3.mutable.MutableInt;

import com.firemerald.custombgm.api.BgmDistribution;
import com.firemerald.custombgm.client.gui.EnumSearchMode;
import com.firemerald.custombgm.operators.BossSpawnerOperator;
import com.firemerald.custombgm.operators.IOperatorSource;
import com.firemerald.fecore.FECoreMod;
import com.firemerald.fecore.client.gui.EnumTextAlignment;
import com.firemerald.fecore.client.gui.components.Button;
import com.firemerald.fecore.client.gui.components.ToggleButton;
import com.firemerald.fecore.client.gui.components.decoration.FloatingText;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollBar;
import com.firemerald.fecore.client.gui.components.scrolling.VerticalScrollableComponentPane;
import com.firemerald.fecore.client.gui.components.text.CompoundTagField;
import com.firemerald.fecore.client.gui.components.text.DoubleField;
import com.firemerald.fecore.client.gui.components.text.IntegerField;
import com.firemerald.fecore.client.gui.components.text.LabeledBetterTextField;
import com.firemerald.fecore.network.server.BlockEntityGUIClosedPacket;
import com.firemerald.fecore.network.server.EntityGUIClosedPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;

public class BossSpawnerScreen<O extends BossSpawnerOperator<O, S>, S extends IOperatorSource<O, S>> extends OperatorScreen<O, S> {
	public class EntityButton extends Button {
		public final ResourceLocation id;
		public final String unformattedString;

	    public EntityButton(int x, int y, Component buttonText, ResourceLocation id) {
	    	super(x, y, buttonText, null);
	    	this.id = id;
	    	StringBuilder builder = new StringBuilder();
	    	buttonText.visit(text -> {
	    		builder.append(text);
	    		return Optional.empty();
	    	});
	    	unformattedString = builder.toString();
	    	setAction();
	    }

		public EntityButton(int x, int y, int widthIn, int heightIn, Component buttonText, ResourceLocation id) {
	    	super(x, y, widthIn, heightIn, buttonText, null);
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
			this.onClick = () -> BossSpawnerScreen.this.activeEntity = BossSpawnerScreen.this.activeEntity == this ? null : this;
		}

	    @Override
	    public boolean renderAsActive(boolean hovered) {
	    	return super.renderAsActive(hovered) && BossSpawnerScreen.this.activeEntity == this;
	    }

	    @Override
	    public boolean renderTextAsActive(boolean hovered) {
	    	return super.renderAsActive(hovered) && (BossSpawnerScreen.this.activeEntity == this || this.renderTextAsFocused(hovered));
	    }
	}

	public final List<EntityButton> allEntities = new ArrayList<>();
	public final List<EntityButton> entities = new ArrayList<>();
	public EntityButton activeEntity;
	public int sup = 0;

    public final FloatingText labelSelector, labelSpawn, labelSpawnNBT, labelPriority;
	public final Button musicButton;
    public final Button spawnRelative, okay, cancel;
    public final DoubleField fieldSpawnX, fieldSpawnY, fieldSpawnZ;
    public final CompoundTagField fieldSpawnNBT;
    public final ToggleButton musicEnabled;
    public final LabeledBetterTextField fieldSearch;
    public final VerticalScrollableComponentPane entityButtons;
    public final VerticalScrollBar entityButtonsScroll;
	public final IntegerField priorTxt;

	public BgmDistribution music = BgmDistribution.EMPTY;
	public int priority;
	public boolean disableMusic, relative;
	public int levelOnActive, levelOnKilled;
	public double spawnX, spawnY, spawnZ;
	public CompoundTag nbt = new CompoundTag();

	@SuppressWarnings({"resource", "rawtypes", "unchecked" })
	public BossSpawnerScreen(S source)
	{
		super(Component.translatable("custombgm.gui.bossspawner"), source);
		this.font = Minecraft.getInstance().font;
		MutableInt y = new MutableInt(0);
		ForgeRegistries.ENTITY_TYPES.getEntries().forEach(entry -> {
			ResourceLocation reg = entry.getKey().location();
			EntityType<?> type = entry.getValue();
			if (type.canSummon()) allEntities.add(new EntityButton(0, y.getValue(), 200, y.addAndGet(20), type.getDescription(), reg));
		});
		entities.addAll(allEntities);
		setupShapeField(0, 0, 200, 20);
		labelSelector = new FloatingText(0, 20, 200, 40, font, I18n.get("custombgm.gui.operator.selector"));
		this.setupSelectorTextField(0, 0, 40, 200, 20);
		labelSpawn = new FloatingText(0, 100, 100, 120, font, I18n.get("custombgm.gui.bossspawner.position"));
		spawnRelative = new Button(100, 100, 100, 120, Component.translatable(relative ? "fecore.shapesgui.operator.relative" : "fecore.shapesgui.operator.absolute"), null).setAction(button -> () -> {
			if (relative)
			{
				relative = false;
				button.displayString = Component.translatable("fecore.shapesgui.operator.absolute");
			}
			else
			{
				relative = true;
				button.displayString = Component.translatable("fecore.shapesgui.operator.relative");
			}
		});
		fieldSpawnX = new DoubleField(font, 0, 120, 67, 20, spawnX, Component.translatable("custombgm.gui.bossspawner.position.x"), (DoubleConsumer) (val -> spawnX = val));
		fieldSpawnY = new DoubleField(font, 67, 120, 66, 20, spawnY, Component.translatable("custombgm.gui.bossspawner.position.y"), (DoubleConsumer) (val -> spawnY = val));
		fieldSpawnZ = new DoubleField(font, 133, 120, 67, 20, spawnZ, Component.translatable("custombgm.gui.bossspawner.position.z"), (DoubleConsumer) (val -> spawnZ = val));
		labelSpawnNBT = new FloatingText(0, 140, 200, 160, font, I18n.get("custombgm.gui.bossspawner.nbt"));
		fieldSpawnNBT = new CompoundTagField(font, 0, 160, 200, 20, nbt, Component.translatable("custombgm.gui.bossspawner.nbt.narrate"), (Consumer<CompoundTag>) (val -> this.nbt = val));
		fieldSpawnNBT.setMaxLength(Short.MAX_VALUE);
		fieldSpawnNBT.setNBT(nbt);
		musicButton = new Button(0, 180, 100, 20, Component.translatable("custombgm.gui.music"), () -> new MusicScreen(music, val -> music = val).activate());
		musicEnabled = new ToggleButton(100, 180, 100, 20, Component.translatable(disableMusic ? "custombgm.gui.operator.disabled" : "custombgm.gui.operator.enabled"), !disableMusic, null).setToggleAction(button -> val -> {
			if (val)
			{
				disableMusic = false;
				button.displayString = Component.translatable("custombgm.gui.operator.enabled");
			}
			else
			{
				disableMusic = true;
				button.displayString = Component.translatable("custombgm.gui.operator.disabled");
			}
		});
		labelPriority = new FloatingText(0, 200, 100, 220, font, I18n.get("custombgm.gui.bgm.priority"), EnumTextAlignment.CENTER);
		priorTxt = new IntegerField(font, 100, 200, 100, 20, priority, Component.translatable("custombgm.gui.bgm.priority.narrate"), (IntConsumer) (v -> priority = v));

		fieldSearch = new LabeledBetterTextField(font, 200, 0, 220, 20, Component.translatable("custombgm.gui.entities.search"), Component.translatable("custombgm.gui.entities.search.narrate"), (Consumer<String>) this::updateSearch);
		entityButtons = new VerticalScrollableComponentPane(200, 40, 400, 220);
		entityButtonsScroll = new VerticalScrollBar(400, 40, 420, 220, entityButtons);

		okay = new Button(0, 220, 200, 20, Component.translatable("fecore.gui.confirm"), () -> {
			FECoreMod.NETWORK.sendToServer(source.isEntity() ? new EntityGUIClosedPacket(this) : new BlockEntityGUIClosedPacket(this));
			this.onClose();
		});
		cancel = new Button(200, 220, 400, 20, Component.translatable("fecore.gui.cancel"), () -> {
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
		 * <  music  ><mus enable>< list >
		 * <prior lbl>< priority >< list >
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
		musicButton.setSize(offX, y, midMidX, y + 20);
		musicEnabled.setSize(midMidX, y, midX, y + 20);
		y += 20;
		labelPriority.setSize(offX, y, midMidX, y + 20);
		priorTxt.setSize(midMidX, y, midX, y + 20);

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
		this.addRenderableWidget(musicButton);
		this.addRenderableWidget(musicEnabled);
		addRenderableWidget(labelPriority);
		addRenderableWidget(priorTxt);

		this.addRenderableWidget(fieldSearch);
		this.addRenderableWidget(entityButtons);
		this.addRenderableWidget(entityButtonsScroll);

		addRenderableWidget(okay);
		addRenderableWidget(cancel);
		if (activeEntity != null) entityButtons.ensureInView(activeEntity);
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
		music = BgmDistribution.STREAM_CODEC.decode(buf);
		priority = buf.readInt();
		priorTxt.setInteger(priority);
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
			ResourceLocation id = ResourceLocation.tryParse(toSpawn);
			allEntities.forEach(button -> {
				if (button.id.equals(id))
				{
					activeEntity = button;
					if (entityButtons != null) entityButtons.ensureInView(button);
					return;
				}
			});
		}
		nbt = buf.readNbt();
		byte levels = buf.readByte();
		levelOnActive = levels & 0xF;
		levelOnKilled = (levels >> 4) & 0xF;

		spawnRelative.displayString = Component.translatable(relative ? "fecore.shapesgui.operator.relative" : "fecore.shapesgui.operator.absolute");
		fieldSpawnX.setDouble(spawnX);
		fieldSpawnY.setDouble(spawnY);
		fieldSpawnZ.setDouble(spawnZ);
		fieldSpawnNBT.setNBT(nbt);
		musicEnabled.state = !disableMusic;
		musicEnabled.displayString = Component.translatable(disableMusic ? "custombgm.gui.operator.disabled" : "custombgm.gui.operator.enabled");
		this.updateSearch();
	}

	@Override
	public void write(FriendlyByteBuf buf)
	{
		super.write(buf);
		BgmDistribution.STREAM_CODEC.encode(buf, music);
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