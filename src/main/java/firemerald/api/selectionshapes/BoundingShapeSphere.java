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

public class BoundingShapeSphere extends BoundingShapeConfigurable
{
	public boolean isRelative = true;
	public double x = .5, y = .5, z = .5, r = 10;

	@Override
	public String getUnlocalizedName()
	{
		return "shape.sphere";
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
		double dx = posX - x;
		double dy = posY - y;
		double dz = posZ - z;
		return (dx * dx) + (dy * dy) + (dz * dz) <= r * r;
	}

	@Override
	public void saveToNBT(NBTTagCompound tag)
	{
		super.saveToNBT(tag);
		tag.setBoolean("isRelative", isRelative);
		tag.setDouble("x", x);
		tag.setDouble("y", y);
		tag.setDouble("z", z);
		tag.setDouble("r", r);
	}

	@Override
	public void loadFromNBT(NBTTagCompound tag)
	{
		super.loadFromNBT(tag);
		isRelative = tag.getBoolean("isRelative");
		if (tag.hasKey("x", 99)) x = tag.getDouble("x");
		else x = .5;
		if (tag.hasKey("y", 99)) y = tag.getDouble("y");
		else y = .5;
		if (tag.hasKey("z", 99)) z = tag.getDouble("z");
		else z = .5;
		if (tag.hasKey("r", 99)) r = tag.getDouble("r");
		else r = 10;
	}

	@Override
	public void saveToBuffer(ByteBuf buf)
	{
		super.saveToBuffer(buf);
		buf.writeBoolean(isRelative);
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
		buf.writeDouble(r);
	}

	@Override
	public void loadFromBuffer(ByteBuf buf)
	{
		super.loadFromBuffer(buf);
		isRelative = buf.readBoolean();
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
		r = buf.readDouble();
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
				isRelative = true;
				x -= pos.x;
				y -= pos.y;
				z -= pos.z;
				posX.setDouble(x);
				posY.setDouble(y);
				posZ.setDouble(z);
				button.displayString = Translator.format("gui.operator.relative");
			}
		}));
		addElement.accept(posX);
		addElement.accept(posY);
		addElement.accept(posZ);
		addElement.accept(new FloatingText(offX, 40, offX + 100, 60, font, Translator.format("gui.shape.radius")));
		addElement.accept(new DoubleField(0, font, offX + 100, 40, 100, 20, r, (DoubleConsumer) (val -> r = val)));
	}

	@Override
	public int addPosition(EntityPlayer player, BlockPos blockPos, int num)
	{
		if (num == 0)
		{
			x = blockPos.getX() + .5;
			y = blockPos.getY() + .5;
			z = blockPos.getZ() + .5;
			player.sendMessage(new TextComponentTranslation("msg.shapetool.origin.set", new Vec3d(blockPos).addVector(.5, .5, .5).toString()));
			player.sendMessage(new TextComponentTranslation("msg.shapetool.radius.selected"));
			return 1;
		}
		else
		{
			double dx = blockPos.getX() + .5 - x;
			double dy = blockPos.getY() + .5 - y;
			double dz = blockPos.getZ() + .5 - z;
			r = Math.sqrt(dx * dx + dy * dy + dz * dz);
			player.sendMessage(new TextComponentTranslation("msg.shapetool.radius.set", r));
			player.sendMessage(new TextComponentTranslation("msg.shapetool.origin.selected"));
			return 0;
		}
	}

	@Override
	public int removePosition(EntityPlayer player, int num)
	{
		if (num == 0)
		{
			player.sendMessage(new TextComponentTranslation("msg.shapetool.radius.selected"));
			return 1;
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
		tooltip.add(Translator.format("lore.shape.radius", r));
	}
}