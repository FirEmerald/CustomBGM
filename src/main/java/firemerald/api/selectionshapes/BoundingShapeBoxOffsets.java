package firemerald.api.selectionshapes;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleConsumer;

import javax.annotation.Nullable;

import firemerald.api.betterscreens.IGuiElement;
import firemerald.api.betterscreens.components.Button;
import firemerald.api.betterscreens.components.decoration.FloatingText;
import firemerald.api.betterscreens.components.text.DoubleField;
import firemerald.api.core.client.Translator;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BoundingShapeBoxOffsets extends BoundingShapeConfigurable
{
	public boolean isRelative = true;
	public double x = 0, y = 0, z = 0, sizeX = 20, sizeY = 20, sizeZ = 20;

	@Override
	public String getUnlocalizedName()
	{
		return "shape.box.offsets";
	}

	@Override
	public boolean isWithin(@Nullable Entity entity, double posX, double posY, double posZ, double testerX, double testerY, double testerZ)
	{
		double x, y, z;
		if (isRelative)
		{
			x = this.x + testerX;
			y = this.y + testerY;
			z = this.z + testerZ;
		}
		else
		{
			x = this.x;
			y = this.y;
			z = this.z;
		}
		double dx = entity.posX - x;
		double dy = entity.posY - y;
		double dz = entity.posZ - z;
		return (sizeX >= 0 ? (dx >=0 && dx <= sizeX) : (dx <= 0 && dx >= sizeX)) && (sizeY >= 0 ? (dy >=0 && dy <= sizeY) : (dy <= 0 && dy >= sizeY)) && (sizeZ >= 0 ? (dz >=0 && dz <= sizeZ) : (dz <= 0 && dz >= sizeZ));
	}

	@Override
	public void saveToNBT(NBTTagCompound tag)
	{
		super.saveToNBT(tag);
		tag.setBoolean("isRelative", isRelative);
		tag.setDouble("x", x);
		tag.setDouble("y", y);
		tag.setDouble("z", z);
		tag.setDouble("sizeX", sizeX);
		tag.setDouble("sizeY", sizeY);
		tag.setDouble("sizeZ", sizeZ);
	}

	@Override
	public void loadFromNBT(NBTTagCompound tag)
	{
		super.loadFromNBT(tag);
		isRelative = tag.getBoolean("isRelative");
		x = tag.getDouble("x");
		y = tag.getDouble("y");
		z = tag.getDouble("z");
		if (tag.hasKey("sizeX", 99)) sizeX = tag.getDouble("sizeX");
		else sizeX = 20;
		if (tag.hasKey("sizeY", 99)) sizeY = tag.getDouble("sizeY");
		else sizeY = 20;
		if (tag.hasKey("sizeZ", 99)) sizeZ = tag.getDouble("sizeZ");
		else sizeZ = 20;
	}

	@Override
	public void saveToBuffer(ByteBuf buf)
	{
		super.saveToBuffer(buf);
		buf.writeBoolean(isRelative);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(sizeX);
		buf.writeDouble(sizeY);
		buf.writeDouble(sizeZ);
	}

	@Override
	public void loadFromBuffer(ByteBuf buf)
	{
		super.loadFromBuffer(buf);
		isRelative = buf.readBoolean();
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		sizeX = buf.readDouble();
		sizeY = buf.readDouble();
		sizeZ = buf.readDouble();
	}

	@Override
	public void addGuiElements(Vec3d pos, IShapeGui gui, FontRenderer font, Consumer<IGuiElement> addElement, int width)
	{
		int offX = (width - 200) >> 1;
		addElement.accept(new FloatingText(offX, 0, offX + 100, 20, font, Translator.format("gui.shape.pos")));
		final DoubleField
		posX = new DoubleField(0, font, offX, 20, 67, 20, x, (DoubleConsumer) (val -> x = val)),
		posY = new DoubleField(0, font, offX + 67, 20, 66, 20, y, (DoubleConsumer) (val -> y = val)),
		posZ = new DoubleField(0, font, offX + 133, 20, 67, 20, z, (DoubleConsumer) (val -> z = val));
		addElement.accept(new Button(offX + 100, 0, 100, 20, Translator.format(isRelative ? "gui.operator.relative" : "gui.operator.absolute"), null).setAction(button -> () -> {
			if (isRelative)
			{
				isRelative = false;
				x += pos.x;
				y += pos.y;
				z += pos.z;
				posX.setDouble(x);
				posY.setDouble(y);
				posZ.setDouble(z);
				button.displayString = Translator.format("gui.operator.absolute");
			}
			else
			{
				x -= pos.x;
				y -= pos.y;
				z -= pos.z;
				posX.setDouble(x);
				posY.setDouble(y);
				posZ.setDouble(z);
				isRelative = true;
				button.displayString = Translator.format("gui.operator.relative");
			}
		}));
		addElement.accept(posX);
		addElement.accept(posY);
		addElement.accept(posZ);
		addElement.accept(new FloatingText(offX, 40, offX + 200, 60, font, Translator.format("gui.shape.size")));
		addElement.accept(new DoubleField(0, font, offX, 60, 67, 20, sizeX, (DoubleConsumer) (val -> sizeX = val)));
		addElement.accept(new DoubleField(0, font, offX + 67, 60, 66, 20, sizeY, (DoubleConsumer) (val -> sizeY = val)));
		addElement.accept(new DoubleField(0, font, offX + 133, 60, 67, 20, sizeZ, (DoubleConsumer) (val -> sizeZ = val)));
	}

	@Override
	public int addPosition(EntityPlayer player, BlockPos blockPos, int num)
	{
		if (num == 0)
		{
			x = blockPos.getX() + .5;
			y = blockPos.getY() + .5;
			z = blockPos.getZ() + .5;
			player.sendMessage(new TextComponentTranslation("msg.shapetool.origin.set", new Vec3d(x, y, z).toString()));
			player.sendMessage(new TextComponentTranslation("msg.shapetool.sizex.selected"));
			return 1;
		}
		else if (num == 1)
		{
			sizeX = blockPos.getX() + .5 - x;
			player.sendMessage(new TextComponentTranslation("msg.shapetool.sizex.set", sizeX));
			player.sendMessage(new TextComponentTranslation("msg.shapetool.sizey.selected"));
			return 2;
		}
		else if (num == 2)
		{
			sizeY = blockPos.getY() + .5 - y;
			player.sendMessage(new TextComponentTranslation("msg.shapetool.sizey.set", sizeY));
			player.sendMessage(new TextComponentTranslation("msg.shapetool.sizez.selected"));
			return 3;
		}
		else
		{
			sizeZ = blockPos.getZ() + .5 - z;
			player.sendMessage(new TextComponentTranslation("msg.shapetool.sizez.set", sizeZ));
			player.sendMessage(new TextComponentTranslation("msg.shapetool.origin.selected"));
			return 0;
		}
	}

	@Override
	public int removePosition(EntityPlayer player, int num)
	{
		if (num == 0)
		{
			player.sendMessage(new TextComponentTranslation("msg.shapetool.sizex.selected"));
			return 1;
		}
		else if (num == 1)
		{
			player.sendMessage(new TextComponentTranslation("msg.shapetool.sizey.selected"));
			return 2;
		}
		else if (num == 2)
		{
			player.sendMessage(new TextComponentTranslation("msg.shapetool.sizez.selected"));
			return 3;
		}
		else
		{
			player.sendMessage(new TextComponentTranslation("msg.shapetool.origin.selected"));
			return 0;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(Translator.translate(isRelative ? "lore.shape.relative" : "lore.shape.absolute"));
		tooltip.add(Translator.format("lore.shape.origin", new Vec3d(x, y, z)));
		tooltip.add(Translator.format("lore.shape.size", new Vec3d(sizeX, sizeY, sizeZ)));
	}
}