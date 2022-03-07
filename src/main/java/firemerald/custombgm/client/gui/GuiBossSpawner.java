package firemerald.custombgm.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import org.apache.commons.lang3.mutable.MutableInt;
import org.lwjgl.input.Keyboard;

import firemerald.api.betterscreens.components.Button;
import firemerald.api.betterscreens.components.ButtonToggle;
import firemerald.api.betterscreens.components.decoration.FloatingText;
import firemerald.api.betterscreens.components.scrolling.ScrollBar;
import firemerald.api.betterscreens.components.scrolling.ScrollableComponentPane;
import firemerald.api.betterscreens.components.text.BetterTextField;
import firemerald.api.betterscreens.components.text.DoubleField;
import firemerald.api.betterscreens.components.text.LabeledBetterTextField;
import firemerald.api.betterscreens.components.text.NBTTagCompoundField;
import firemerald.api.core.client.Translator;
import firemerald.custombgm.Main;
import firemerald.custombgm.networking.server.TileGUIClosedPacket;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@SuppressWarnings("unchecked")
public class GuiBossSpawner extends GuiTileEntityOperator
{
	public static class EntityButton extends Button
	{
		public final GuiBossSpawner gui;
		public final ResourceLocation id;

	    public EntityButton(int x, int y, String buttonText, GuiBossSpawner gui, ResourceLocation id)
	    {
	    	super(x, y, buttonText, null);
	    	this.gui = gui;
	    	this.id = id;
	    	setAction();
	    }

		public EntityButton(int x, int y, int widthIn, int heightIn, String buttonText, GuiBossSpawner gui, ResourceLocation id)
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
	public final Class<?>[] classes = { Entity.class, EntityLivingBase.class, EntityCreature.class, IMob.class };
	public final String[] classNames = { "entity", "living", "creature", "mob" };
	public int sup = 0;
    private MusicTabCompleter tabCompleter;

    public final FloatingText labelSelector, labelNBT, labelSpawn, labelSpawnNBT, labelMusic;
    public final Button spawnRelative, superType, okay, cancel;
    public final DoubleField fieldSpawnX, fieldSpawnY, fieldSpawnZ;
    public final NBTTagCompoundField fieldSpawnNBT;
    public final ButtonToggle musicEnabled;
    public final BetterTextField fieldMusic;
    public final LabeledBetterTextField fieldSearch;
    public final ScrollableComponentPane entityButtons;
    public final ScrollBar entityButtonsScroll;

	public String music = "";
	public int priority;
	public boolean disableMusic, relative;
	public int levelOnActive, levelOnKilled;
	public double spawnX, spawnY, spawnZ;
	public NBTTagCompound nbt = new NBTTagCompound();

	public GuiBossSpawner(BlockPos pos)
	{
		super(pos);
		this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
		MutableInt y = new MutableInt(0);
		EntityList.getEntityNameList().forEach(reg -> {
			if (reg.equals(EntityList.LIGHTNING_BOLT)) return;
			Class<? extends Entity> clazz = EntityList.getClass(reg);
			if (clazz != null)
			{
				EntityEntry entry = EntityRegistry.getEntry(clazz);
				String name = null;
				if (entry != null) name = entry.getName();
				if (name == null) name = reg.toString();
				allEntities.add(new EntityButton(0, y.getValue(), 200, y.addAndGet(20), Translator.format("entity." + name + ".name"), this, reg));
			}
		});
		entities.addAll(allEntities);
		/*
		 * <configure shape> <         search         >
		 * Select            <        subclass        >
		 * <  select text  >
		 * NBT
		 * <  select nbt   >
		 * Spawn  <relative>
		 * < x ><  y  >< z >
		 * spawn NBT
		 * <   spawn nbt   >
		 * music < enabled >
		 * <  music name   >
		 * <     okay      > <         cancel         >
		 */
		this.addElement(setupShapeField(0, 0, 200, 20));
		this.addElement(labelSelector = new FloatingText(0, 20, 200, 40, fontRenderer, Translator.format("gui.custombgm.operator.selector")));
		this.addElement(this.setupSelectorTextField(0, 0, 40, 200, 20));
		this.addElement(labelNBT = new FloatingText(0, 60, 200, 80, fontRenderer, Translator.format("gui.custombgm.operator.nbt")));
		this.addElement(this.setupSelectorNBTField(1, 0, 80, 200, 20));
		this.addElement(labelSpawn = new FloatingText(0, 100, 100, 120, fontRenderer, Translator.format("gui.custombgm.bossspawner.position")));
		this.addElement(spawnRelative = new Button(100, 100, 100, 120, Translator.format(relative ? "gui.custombgm.operator.relative" : "gui.custombgm.operator.absolute"), null).setAction(button -> () -> {
			if (relative)
			{
				relative = false;
				button.displayString = Translator.format("gui.custombgm.operator.absolute");
			}
			else
			{
				relative = true;
				button.displayString = Translator.format("gui.custombgm.operator.relative");
			}
		}));
		this.addElement(fieldSpawnX = new DoubleField(0, fontRenderer, 0, 120, 67, 20, spawnX, (DoubleConsumer) (val -> spawnX = val)));
		this.addElement(fieldSpawnY = new DoubleField(1, fontRenderer, 67, 120, 66, 20, spawnY, (DoubleConsumer) (val -> spawnY = val)));
		this.addElement(fieldSpawnZ = new DoubleField(2, fontRenderer, 133, 120, 67, 20, spawnZ, (DoubleConsumer) (val -> spawnZ = val)));
		this.addElement(labelSpawnNBT = new FloatingText(0, 140, 200, 160, fontRenderer, Translator.format("gui.custombgm.bossspawner.nbt")));
		this.addElement(fieldSpawnNBT = new NBTTagCompoundField(3, fontRenderer, 0, 160, 200, 20, nbt, (Consumer<NBTTagCompound>) (val -> this.nbt = val)));
		fieldSpawnNBT.setMaxStringLength(Short.MAX_VALUE);
		fieldSpawnNBT.setNBT(nbt);
		this.addElement(labelMusic = new FloatingText(0, 180, 100, 200, fontRenderer, Translator.format("gui.custombgm.bgm.music")));
		this.addElement(musicEnabled = new ButtonToggle(100, 180, 100, 20, Translator.format(disableMusic ? "gui.custombgm.operator.disabled" : "gui.custombgm.operator.enabled"), !disableMusic, null).setToggleAction(button -> val -> {
			if (val)
			{
				disableMusic = false;
				button.displayString = Translator.format("gui.custombgm.operator.enabled");
			}
			else
			{
				disableMusic = true;
				button.displayString = Translator.format("gui.custombgm.operator.disabled");
			}
		}));
		this.addElement(fieldMusic = new BetterTextField(3, fontRenderer, 0, 200, 200, 20, music, (Consumer<String>) (val -> this.music = val)));

		this.addElement(fieldSearch = new LabeledBetterTextField(2, fontRenderer, 200, 0, 220, 20, Translator.translate("gui.custombgm.entities.search"), (Consumer<String>) this::updateSearch));
		addElement(superType = new Button(0, 20, 200, 20, Translator.format("gui.custombgm.entities.type." + classNames[sup]), null).setAction(button -> () -> {
			sup = (sup + 1) % classes.length;
			button.displayString = Translator.format("gui.custombgm.entities.type." + classNames[sup]);
			updateSearch();
		}));
		this.addElement(entityButtons = new ScrollableComponentPane(200, 40, 400, 220));
		this.addElement(entityButtonsScroll = new ScrollBar(400, 40, 420, 220, entityButtons));

		addElement(okay = new Button(0, 220, 200, 20, Translator.format("gui.done"), () -> {
			Main.network().sendToServer(new TileGUIClosedPacket(this));
			mc.setIngameFocus();
		}));
		addElement(cancel = new Button(200, 220, 400, 20, Translator.format("gui.cancel"), () -> {
			mc.setIngameFocus();
		}));
		entityButtons.setScrollBar(entityButtonsScroll);
        this.tabCompleter = new MusicTabCompleter(this.fieldMusic);
	}

	@Override
	public void initGui()
	{
		int buttonsWidth = Math.min(this.width - 200, 392) - 10;
		int offX = (this.width - buttonsWidth - 210) >> 1;
		int offX2 = offX + 100;
		int offX3 = offX2 + 100;
		int offX4 = offX3 + buttonsWidth;
		int offX5 = offX4 + 10;
		/*
		 * <configure shape> <         search         >
		 * Select            <        subclass        >
		 * <  select text  >
		 * NBT
		 * <  select nbt   >
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
		labelNBT.setSize(offX, 60, offX3, 80);
		selectorNBTTxt.setSize(offX, 80, offX3, 100);
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
		superType.setSize(offX3, 20, offX5, 40);
		entityButtons.setSize(offX3, 40, offX4, height - 20);
		entityButtonsScroll.setSize(offX4, 40, offX5, height - 20);
		okay.setSize(offX, height - 20, offX3, height);
		cancel.setSize(offX5 - 200, height - 20, offX5, height);
		final int w = entityButtons.x2 - entityButtons.x1; //max 392
		allEntities.forEach(button -> button.setSize(0, button.getY1(), w, button.getY2()));
	}

	public void updateSearch()
	{
		updateSearch(fieldSearch.getText());
	}

	public void updateSearch(String s)
	{
		MutableInt y = new MutableInt(0);
		final int w = entityButtons.x2 - entityButtons.x1; //max 392
		allEntities.forEach(entityButtons::removeElement);
		entities.clear();
		for (int i = 0; i < allEntities.size(); i++)
		{
			EntityButton button = allEntities.get(i);
			if (EnumSearchMode.matchString(button.id, button.displayString, s) && (classes[sup] == Entity.class || isSuperClass(button.id, (Class<? extends Entity>) classes[sup])))
			{
				button.setSize(0, y.getValue(), w, y.addAndGet(20));
				entityButtons.addElement(button);
				entities.add(button);
			}
		}
		entityButtons.updateComponentSize();
		entityButtons.updateScrollSize();
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
		relative = buf.readBoolean();
		disableMusic = buf.readBoolean();
		spawnX = buf.readDouble();
		spawnY = buf.readDouble();
		spawnZ = buf.readDouble();
		String toSpawn = ByteBufUtils.readUTF8String(buf);
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
		nbt = ByteBufUtils.readTag(buf);
		byte levels = buf.readByte();
		levelOnActive = levels & 0xF;
		levelOnKilled = (levels >> 4) & 0xF;

		spawnRelative.displayString = Translator.format(relative ? "gui.operator.relative" : "gui.operator.absolute");
		fieldSpawnX.setDouble(spawnX);
		fieldSpawnY.setDouble(spawnY);
		fieldSpawnZ.setDouble(spawnZ);
		fieldSpawnNBT.setNBT(nbt);
		musicEnabled.state = !disableMusic;
		musicEnabled.displayString = Translator.format(disableMusic ? "gui.custombgm.operator.disabled" : "gui.custombgm.operator.enabled");
		fieldMusic.setString(music);
		this.updateSearch();
	}

	@Override
	public void write(ByteBuf buf)
	{
		super.write(buf);
		ByteBufUtils.writeUTF8String(buf, music == null ? "" : music.toString());
		buf.writeInt(priority);
		buf.writeBoolean(relative);
		buf.writeBoolean(disableMusic);
		buf.writeDouble(spawnX);
		buf.writeDouble(spawnY);
		buf.writeDouble(spawnZ);
		ByteBufUtils.writeUTF8String(buf, activeEntity == null ? "" : activeEntity.id.toString());
		ByteBufUtils.writeTag(buf, nbt);
		buf.writeByte(levelOnActive | (levelOnKilled << 4));
	}
}