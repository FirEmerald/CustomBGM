package firemerald.custombgm.client.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

import org.apache.commons.lang3.mutable.MutableInt;

import firemerald.api.betterscreens.components.Button;
import firemerald.api.betterscreens.components.decoration.FloatingText;
import firemerald.api.betterscreens.components.scrolling.ScrollBar;
import firemerald.api.betterscreens.components.scrolling.ScrollableComponentPane;
import firemerald.api.betterscreens.components.text.IntegerField;
import firemerald.api.betterscreens.components.text.LabeledBetterTextField;
import firemerald.api.core.client.Translator;
import firemerald.custombgm.Main;
import firemerald.custombgm.networking.server.TileGUIClosedPacket;
import firemerald.custombgm.tileentity.TileEntityEntityTester;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.IMob;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@SuppressWarnings("unchecked")
public class GuiEntityTester extends GuiTileEntityOperator
{
	public static class EntityButton extends Button
	{
		public final Set<ResourceLocation> set;
		public final ResourceLocation id;
		public boolean state;

	    public EntityButton(int x, int y, String buttonText, Set<ResourceLocation> set, ResourceLocation id)
	    {
	    	super(x, y, buttonText, null);
	    	this.set = set;
	    	this.id = id;
	    	setAction();
	    }

		public EntityButton(int x, int y, int widthIn, int heightIn, String buttonText, Set<ResourceLocation> set, ResourceLocation id)
		{
	    	super(x, y, widthIn, heightIn, buttonText, null);
	    	this.set = set;
	    	this.id = id;
	    	setAction();
		}

		private void setAction()
		{
			this.onClick = () -> {
				if (state)
				{
					set.remove(id);
					state = false;
				}
				else
				{
					set.add(id);
					state = true;
				}
			};
		}

		@Override
	    protected int getHoverState(boolean mouseOver)
	    {
	    	return this.state ? super.getHoverState(mouseOver) : 0;
	    }
	}

	public final List<EntityButton> allEntities = new ArrayList<>();
	public final List<EntityButton> entities = new ArrayList<>();
	public final Set<ResourceLocation> enabled = new HashSet<>();
	public final Class<?>[] classes = { Entity.class, EntityLivingBase.class, EntityCreature.class, IMob.class, IProjectile.class };
	public final String[] classNames = { "entity", "living", "creature", "mob", "projectile" };
	short min, max;
	public int sup = 0;

    public final FloatingText labelSelector, labelNBT, labelMin, labelMax;
    public final Button selectAll, selectNone, superType, okay, cancel;
    public final IntegerField fieldMin, fieldMax;
    public final LabeledBetterTextField fieldSearch;
    public final ScrollableComponentPane entityButtons;
    public final ScrollBar entityButtonsScroll;

	public GuiEntityTester(BlockPos pos)
	{
		super(pos);
		this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
		allEntities.add(new EntityButton(0, 0, 200, 20, "Players", enabled, TileEntityEntityTester.PLAYER));
		MutableInt y = new MutableInt(20);
		EntityList.getEntityNameList().forEach(reg -> {
			if (reg.equals(EntityList.LIGHTNING_BOLT)) return;
			Class<? extends Entity> clazz = EntityList.getClass(reg);
			if (clazz != null)
			{
				EntityEntry entry = EntityRegistry.getEntry(clazz);
				String name = null;
				if (entry != null) name = entry.getName();
				if (name == null) name = reg.toString();
				allEntities.add(new EntityButton(0, y.getValue(), 200, y.addAndGet(20), Translator.format("entity." + name + ".name"), enabled, reg));
			}
		});
		entities.addAll(allEntities);
		this.addElement(setupShapeField(0, 0, 200, 20));
		this.addElement(labelSelector = new FloatingText(0, 20, 200, 40, fontRenderer, Translator.format("gui.custombgm.operator.selector")));
		this.addElement(this.setupSelectorTextField(0, 0, 40, 200, 20));
		this.addElement(labelNBT = new FloatingText(0, 60, 200, 80, fontRenderer, Translator.format("gui.custombgm.operator.nbt")));
		this.addElement(this.setupSelectorNBTField(1, 0, 80, 200, 20));
		this.addElement(labelMin = new FloatingText(0, 100, 200, 120, fontRenderer, Translator.format("gui.custombgm.entitytester.min")));
		this.addElement(fieldMin = new IntegerField(0, fontRenderer, 0, 120, 200, 20, Short.MAX_VALUE, (IntConsumer) (val -> this.min = (short) val)));
		this.addElement(labelMax = new FloatingText(0, 140, 200, 160, fontRenderer, Translator.format("gui.custombgm.entitytester.max")));
		this.addElement(fieldMax = new IntegerField(0, fontRenderer, 0, 160, 200, 20, Short.MAX_VALUE, (IntConsumer) (val -> this.max = (short) val)));
		this.addElement(selectAll = new Button(200, 0, 110, 20, Translator.format("gui.custombgm.entitytester.selectall"), () -> {
			entities.forEach(button -> button.state = true); //set all enabled
			entities.stream().map(button -> button.id).forEach(enabled::add); //add all to enabled list
		}));
		this.addElement(selectNone = new Button(310, 0, 110, 20, Translator.format("gui.custombgm.entitytester.selectnone"), () -> {
			entities.forEach(button -> button.state = false); //set all to disabled
			enabled.clear(); //clear the enabled list
		}));
		this.addElement(fieldSearch = new LabeledBetterTextField(2, fontRenderer, 200, 20, 220, 20, Translator.translate("gui.custombgm.entities.search"), (Consumer<String>) this::updateSearch));
		this.addElement(entityButtons = new ScrollableComponentPane(200, 60, 400, 180));
		this.addElement(entityButtonsScroll = new ScrollBar(400, 60, 420, 180, entityButtons));
		addElement(superType = new Button(0, 180, 200, 200, Translator.format("gui.custombgm.entities.type." + classNames[sup]), null).setAction(button -> () -> {
			sup = (sup + 1) % classes.length;
			button.displayString = Translator.format("gui.custombgm.entities.type." + classNames[sup]);
			updateSearch();
		}));

		addElement(okay = new Button(0, 180, 200, 200, Translator.format("gui.done"), () -> {
			Main.network().sendToServer(new TileGUIClosedPacket(this));
			mc.setIngameFocus();
		}));
		addElement(cancel = new Button(200, 180, 400, 200, Translator.format("gui.cancel"), () -> {
			mc.setIngameFocus();
		}));
		entityButtons.setScrollBar(entityButtonsScroll);
	}

	public void initEnabled()
	{
		allEntities.forEach(button -> button.state = enabled.contains(button.id));
	}

	@Override
	public void initGui()
	{
		int buttonsWidth = Math.min(this.width - 200, 392) - 10;
		int offX = (this.width - buttonsWidth - 210) >> 1;
		int offX2 = offX + 200;
		int offX3 = offX2 + ((buttonsWidth + 10) >> 1);
		int offX4 = offX2 + buttonsWidth;
		int offX5 = offX4 + 10;
		configureShape.setSize(offX, 0, offX2, 20);
		labelSelector.setSize(offX, 20, offX2, 40);
		selectorTxt.setSize(offX, 40, offX2, 60);
		labelNBT.setSize(offX, 60, offX2, 80);
		selectorNBTTxt.setSize(offX, 80, offX2, 100);
		labelMin.setSize(offX, 100, offX2, 120);
		fieldMin.setSize(offX, 120, offX2, 140);
		labelMax.setSize(offX, 140, offX2, 160);
		fieldMax.setSize(offX, 160, offX2, 180);
		selectAll.setSize(offX2, 0, offX3, 20);
		selectNone.setSize(offX3, 0, offX5, 20);
		fieldSearch.setSize(offX2, 20, offX5, 40);
		superType.setSize(offX2, 40, offX5, 60);
		entityButtons.setSize(offX2, 60, offX4, height - 20);
		entityButtonsScroll.setSize(offX4, 60, offX5, height - 20);
		okay.setSize(offX, height - 20, offX2, height);
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
		enabled.clear();
		min = buf.readShort();
		max = buf.readShort();
		int count = ByteBufUtils.readVarInt(buf, 5);
		for (int i = 0; i < count; i++)
		{
			ResourceLocation name = new ResourceLocation(ByteBufUtils.readUTF8String(buf));
			if (allEntities.stream().anyMatch(button -> button.id.equals(name))) enabled.add(name);
			else Main.LOGGER.warn("[GuiEntityTester]: Invalid entity ID: " + name + ", ignoring");
		}
		this.fieldMin.setInteger(min);
		this.fieldMax.setInteger(max);
		allEntities.forEach(button -> button.state = enabled.contains(button.id));
		this.updateSearch();
	}

	@Override
	public void write(ByteBuf buf)
	{
		super.write(buf);
		buf.writeShort(min);
		buf.writeShort(max);
		ByteBufUtils.writeVarInt(buf, enabled.size(), 5);
		enabled.forEach(name -> ByteBufUtils.writeUTF8String(buf, name.toString()));
	}
}