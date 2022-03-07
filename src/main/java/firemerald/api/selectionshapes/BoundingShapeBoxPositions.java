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

public class BoundingShapeBoxPositions extends BoundingShapeConfigurable
{
	public boolean isRelative = true;
	public double x1 = -10, y1 = -10, z1 = -10, x2 = 10, y2 = 10, z2 = 10;

	@Override
	public String getUnlocalizedName()
	{
		return "shape.box.positions";
	}

	@Override
	public boolean isWithin(@Nullable Entity entity, double posX, double posY, double posZ, double testerX, double testerY, double testerZ)
	{
		double x1, y1, z1, x2, y2, z2;
		if (isRelative)
		{
			x1 = this.x1 + testerX;
			y1 = this.y1 + testerY;
			z1 = this.z1 + testerZ;
			x2 = this.x2 + testerX;
			y2 = this.y2 + testerY;
			z2 = this.z2 + testerZ;
		}
		else
		{
			x1 = this.x1;
			y1 = this.y1;
			z1 = this.z1;
			x2 = this.x2;
			y2 = this.y2;
			z2 = this.z2;
		}
		return (x2 >= x1 ? (posX >= x1 && posX <= x2 + 1) : (posX <= x1 + 1 && posX >= x2)) && (y2 >= y1 ? (posY >= y1 && posY <= y2 + 1) : (posY <= y1 + 1 && posY >= y2)) && (z2 >= z1 ? (posZ >= z1 && posZ <= z2 + 1) : (posZ <= z1 + 1 && posZ >= z2));
	}

	@Override
	public void saveToNBT(NBTTagCompound tag)
	{
		super.saveToNBT(tag);
		tag.setBoolean("isRelative", isRelative);
		tag.setDouble("x1", x1);
		tag.setDouble("y1", y1);
		tag.setDouble("z1", z1);
		tag.setDouble("x2", x2);
		tag.setDouble("y2", y2);
		tag.setDouble("z2", z2);
	}

	@Override
	public void loadFromNBT(NBTTagCompound tag)
	{
		super.loadFromNBT(tag);
		isRelative = tag.getBoolean("isRelative");
		if (tag.hasKey("x1", 99)) x1 = tag.getDouble("x1");
		else x1 = -10;
		if (tag.hasKey("y1", 99)) y1 = tag.getDouble("y1");
		else y1 = -10;
		if (tag.hasKey("z1", 99)) z1 = tag.getDouble("z1");
		else z1 = -10;
		if (tag.hasKey("x2", 99)) x2 = tag.getDouble("x2");
		else x2 = 10;
		if (tag.hasKey("y2", 99)) y2 = tag.getDouble("y2");
		else y2 = 10;
		if (tag.hasKey("z2", 99)) z2 = tag.getDouble("z2");
		else z2 = 10;
	}

	@Override
	public void saveToBuffer(ByteBuf buf)
	{
		super.saveToBuffer(buf);
		buf.writeBoolean(isRelative);
		buf.writeDouble(x1);
		buf.writeDouble(y1);
		buf.writeDouble(z1);
		buf.writeDouble(x2);
		buf.writeDouble(y2);
		buf.writeDouble(z2);
	}

	@Override
	public void loadFromBuffer(ByteBuf buf)
	{
		super.loadFromBuffer(buf);
		isRelative = buf.readBoolean();
		x1 = buf.readDouble();
		y1 = buf.readDouble();
		z1 = buf.readDouble();
		x2 = buf.readDouble();
		y2 = buf.readDouble();
		z2 = buf.readDouble();
	}

	@Override
	public void addGuiElements(Vec3d pos, IShapeGui gui, FontRenderer font, Consumer<IGuiElement> addElement, int width)
	{
		int offX = (width - 200) >> 1;
		addElement.accept(new FloatingText(offX, 0, offX + 100, 20, font, Translator.format("gui.shape.pos1")));
		final DoubleField
		posX1 = new DoubleField(0, font, offX, 20, 67, 20, x1, (DoubleConsumer) (val -> x1 = val)),
		posY1 = new DoubleField(0, font, offX + 67, 20, 66, 20, y1, (DoubleConsumer) (val -> y1 = val)),
		posZ1 = new DoubleField(0, font, offX + 133, 20, 67, 20, z1, (DoubleConsumer) (val -> z1 = val)),
		posX2 = new DoubleField(0, font, offX, 60, 67, 20, x2, (DoubleConsumer) (val -> x2 = val)),
		posY2 = new DoubleField(0, font, offX + 67, 60, 66, 20, y2, (DoubleConsumer) (val -> y2 = val)),
		posZ2 = new DoubleField(0, font, offX + 133, 60, 67, 20, z2, (DoubleConsumer) (val -> z2 = val));
		addElement.accept(new Button(offX + 100, 0, 100, 20, Translator.format(isRelative ? "gui.operator.relative" : "gui.operator.absolute"), null).setAction(button -> () -> {
			if (isRelative)
			{
				isRelative = false;
				x1 += pos.x;
				y1 += pos.y;
				z1 += pos.z;
				x2 += pos.x;
				y2 += pos.y;
				z2 += pos.z;
				posX1.setDouble(x1);
				posY1.setDouble(y1);
				posZ1.setDouble(z1);
				posX2.setDouble(x2);
				posY2.setDouble(y2);
				posZ2.setDouble(z2);
				button.displayString = Translator.format("gui.operator.absolute");
			}
			else
			{
				isRelative = true;
				x1 -= pos.x;
				y1 -= pos.y;
				z1 -= pos.z;
				x2 -= pos.x;
				y2 -= pos.y;
				z2 -= pos.z;
				posX1.setDouble(x1);
				posY1.setDouble(y1);
				posZ1.setDouble(z1);
				posX2.setDouble(x2);
				posY2.setDouble(y2);
				posZ2.setDouble(z2);
				button.displayString = Translator.format("gui.operator.relative");
			}
		}));
		addElement.accept(posX1);
		addElement.accept(posY1);
		addElement.accept(posZ1);
		addElement.accept(new FloatingText(offX, 40, offX + 200, 60, font, Translator.format("gui.shape.pos2")));
		addElement.accept(posX2);
		addElement.accept(posY2);
		addElement.accept(posZ2);
	}

	@Override
	public int addPosition(EntityPlayer player, BlockPos blockPos, int num)
	{
		if (num == 0)
		{
			x1 = blockPos.getX() + .5;
			y1 = blockPos.getY() + .5;
			z1 = blockPos.getZ() + .5;
			player.sendMessage(new TextComponentTranslation("msg.shapetool.pos1.set", new Vec3d(x1, y1, z1).toString()));
			player.sendMessage(new TextComponentTranslation("msg.shapetool.pos2.selected"));
			return 1;
		}
		else
		{
			x2 = blockPos.getX() + .5;
			y2 = blockPos.getY() + .5;
			z2 = blockPos.getZ() + .5;
			player.sendMessage(new TextComponentTranslation("msg.shapetool.pos2.set", new Vec3d(x2, y2, z2).toString()));
			player.sendMessage(new TextComponentTranslation("msg.shapetool.pos1.selected"));
			return 0;
		}
	}

	@Override
	public int removePosition(EntityPlayer player, int num)
	{
		if (num == 0)
		{
			player.sendMessage(new TextComponentTranslation("msg.shapetool.pos2.selected"));
			return 1;
		}
		else
		{
			player.sendMessage(new TextComponentTranslation("msg.shapetool.pos1.selected"));
			return 0;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		tooltip.add(Translator.translate(isRelative ? "lore.shape.relative" : "lore.shape.absolute"));
		tooltip.add(Translator.format("lore.shape.pos1", new Vec3d(x1, y1, z1)));
		tooltip.add(Translator.format("lore.shape.pos2", new Vec3d(x2, y2, z2)));
	}
}