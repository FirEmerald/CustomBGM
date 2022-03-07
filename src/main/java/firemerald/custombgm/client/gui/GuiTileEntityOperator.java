package firemerald.custombgm.client.gui;

import java.util.function.Consumer;

import firemerald.api.betterscreens.GuiTileEntityGui;
import firemerald.api.betterscreens.components.text.BetterTextField;
import firemerald.api.betterscreens.components.text.NBTTagCompoundField;
import firemerald.api.selectionshapes.BoundingShape;
import firemerald.api.selectionshapes.BoundingShapeSphere;
import firemerald.api.selectionshapes.ButtonConfigureShape;
import firemerald.custombgm.tileentity.TileEntityEntityTester;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public abstract class GuiTileEntityOperator extends GuiTileEntityGui
{
	public String selector = "";
	public NBTTagCompound selectorNBT = new NBTTagCompound();
	public BoundingShape shape = new BoundingShapeSphere();
	public String customName = "";
	public final BlockPos pos;
	public BetterTextField selectorTxt;
	public NBTTagCompoundField selectorNBTTxt;
    public ButtonConfigureShape configureShape;

	public GuiTileEntityOperator(BlockPos pos)
	{
		this.pos = pos;
	}

	public BetterTextField setupSelectorTextField(int id, int x, int y, int w, int h)
	{
		selectorTxt = new BetterTextField(0, fontRenderer, x, y, w, h, (Consumer<String>) (str -> selector = str));
		selectorTxt.setMaxStringLength(Short.MAX_VALUE);
		selectorTxt.setString(selector);
		return selectorTxt;
	}

	public NBTTagCompoundField setupSelectorNBTField(int id, int x, int y, int w, int h)
	{
		selectorNBTTxt = new NBTTagCompoundField(0, fontRenderer, x, y, w, h, (Consumer<NBTTagCompound>) (nbt -> selectorNBT = nbt));
		selectorNBTTxt.setMaxStringLength(Short.MAX_VALUE);
		selectorNBTTxt.setNBT(selectorNBT);
		return selectorNBTTxt;
	}

	public ButtonConfigureShape setupShapeField(int x, int y, int w, int h)
	{
		return configureShape = new ButtonConfigureShape(x, y, w, h, (shape, onAccept) -> new GuiShapes(new Vec3d(pos), shape, onAccept).activate(), () -> this.shape, shape -> this.shape = shape);
	}

	@Override
	public BlockPos getTilePos()
	{
		return this.pos;
	}

	@Override
	public void read(ByteBuf buf)
	{
		shape = BoundingShape.constructFromBuffer(buf);
		selector = ByteBufUtils.readUTF8String(buf);
		selectorNBT = ByteBufUtils.readTag(buf);
		customName = ByteBufUtils.readUTF8String(buf);
		selectorTxt.setString(selector);
		selectorNBTTxt.setNBT(selectorNBT);
		configureShape.onShapeChanged(shape);
	}

	@Override
	public void write(ByteBuf buf)
	{
		shape.saveToBuffer(buf);
		ByteBufUtils.writeUTF8String(buf, selector);
		ByteBufUtils.writeTag(buf, selectorNBT);
		ByteBufUtils.writeUTF8String(buf, customName);
	}

	public static Class<? extends Entity> getClass(ResourceLocation id)
	{
		return id.equals(TileEntityEntityTester.PLAYER) ? EntityPlayer.class : EntityList.getClass(id);
	}

	public static boolean isSuperClass(ResourceLocation id, Class<? extends Entity> b)
	{
		Class<? extends Entity> a = getClass(id);
		return a != null && b.isAssignableFrom(a);
	}
}