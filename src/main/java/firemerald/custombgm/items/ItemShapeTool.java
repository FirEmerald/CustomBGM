package firemerald.custombgm.items;

import java.util.List;

import javax.annotation.Nullable;

import firemerald.api.core.client.Translator;
import firemerald.api.selectionshapes.BoundingShape;
import firemerald.api.selectionshapes.BoundingShapeBoxPositions;
import firemerald.api.selectionshapes.BoundingShapeConfigurable;
import firemerald.api.selectionshapes.IShapeTool;
import firemerald.custombgm.init.LSTabs;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class ItemShapeTool extends Item implements IShapeTool //TODO item tooltip for shape info
{
	public ItemShapeTool()
	{
		this.setCreativeTab(LSTabs.TAB);
		this.setMaxStackSize(1);
	}

	@Override
    public boolean doesSneakBypassUse(ItemStack stack, net.minecraft.world.IBlockAccess world, BlockPos pos, EntityPlayer player)
    {
		return true;
    }

	//right-click on block = add pos
	//shift-right-click on block = default action
	@Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand)
	{
		if (player.isSneaking()) return EnumActionResult.PASS;
		else if (!world.isRemote)
		{
			ItemStack stack = player.getHeldItem(hand);
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null) stack.setTagCompound(tag = new NBTTagCompound());
			int posIndex = tag.getInteger("posIndex");
			NBTTagCompound shapeTag = tag.getCompoundTag("shape");
			if (shapeTag == null) tag.setTag("shape", shapeTag = new NBTTagCompound());
			BoundingShape s = BoundingShape.constructFromNBT(shapeTag);
			BoundingShapeConfigurable shape;
			if (s instanceof BoundingShapeConfigurable) shape = (BoundingShapeConfigurable) s;
			else
			{
				shape = new BoundingShapeBoxPositions();
				posIndex = 0;
				if (s != null) //was invalid shape
				{
					player.sendMessage(new TextComponentTranslation("msg.custombgm.shapetool.invalid", new TextComponentTranslation(s.getUnlocalizedName()), new TextComponentTranslation(shape.getUnlocalizedName())));
				}
				else //no shape selected
				{
					player.sendMessage(new TextComponentTranslation("msg.custombgm.shapetool.new", new TextComponentTranslation(shape.getUnlocalizedName())));
				}
			}
			posIndex = shape.addPosition(player, pos, posIndex);
			shapeTag = new NBTTagCompound();
			shape.saveToNBT(shapeTag);
			tag.setTag("shape", shapeTag);
			tag.setInteger("posIndex", posIndex);
		}
		return EnumActionResult.SUCCESS;
	}

	//right-click empty = remove pos
	//shift-right-click on empty = change mode
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		if (!world.isRemote)
		{
			NBTTagCompound tag = stack.getTagCompound();
			if (tag == null) stack.setTagCompound(tag = new NBTTagCompound());
			int posIndex = tag.getInteger("posIndex");
			NBTTagCompound shapeTag = tag.getCompoundTag("shape");
			if (shapeTag == null) tag.setTag("shape", shapeTag = new NBTTagCompound());
			BoundingShape s = BoundingShape.constructFromNBT(shapeTag);
			BoundingShapeConfigurable shape;
			boolean isNew = false;
			if (s instanceof BoundingShapeConfigurable) shape = (BoundingShapeConfigurable) s;
			else
			{
				isNew = true;
				shape = new BoundingShapeBoxPositions();
				posIndex = 0;
				if (s != null) //was invalid shape
				{
					player.sendMessage(new TextComponentTranslation("msg.custombgm.shapetool.invalid", new TextComponentTranslation(s.getUnlocalizedName()), new TextComponentTranslation(shape.getUnlocalizedName())));
				}
				else //no shape selected
				{
					player.sendMessage(new TextComponentTranslation("msg.custombgm.shapetool.new", new TextComponentTranslation(shape.getUnlocalizedName())));
				}
			}
			if (player.isSneaking())
			{
				if (!isNew)
				{
					List<BoundingShapeConfigurable> shapes = BoundingShape.getConfigurableShapeList(shape);
					int index = shapes.indexOf(shape);
					int newIndex = (index + 1) % shapes.size();
					if (index != newIndex)
					{
						shape = shapes.get(newIndex);
						posIndex = 0;
						player.sendMessage(new TextComponentTranslation("msg.custombgm.shapetool.mode", new TextComponentTranslation(shape.getUnlocalizedName())));
					}
				}
			}
			else posIndex = shape.removePosition(player, posIndex);
			shapeTag = new NBTTagCompound();
			shape.saveToNBT(shapeTag);
			tag.setTag("shape", shapeTag);
			tag.setInteger("posIndex", posIndex);
		}
		return new ActionResult<>(EnumActionResult.SUCCESS, stack);
	}

    @Override
    public String getItemStackDisplayName(ItemStack stack)
    {
    	BoundingShape shape = getShape(stack);
    	return Translator.format(this.getUnlocalizedName(stack) + ".name", shape == null ? Translator.translate("shape.none") : shape.getLocalizedName()).trim();
    }

	@Override
	public boolean canAcceptShape(ItemStack stack, BoundingShape shape)
	{
		return shape instanceof BoundingShapeConfigurable;
	}

	@Override
	public void setShape(ItemStack stack, BoundingShape shape)
	{
		IShapeTool.super.setShape(stack, shape);
		stack.getTagCompound().setInteger("posIndex", 0);
	}

	@Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
    {
		tooltip.add(Translator.translate("lore.custombgm.shape_tool.add_pos"));
		tooltip.add(Translator.translate("lore.custombgm.shape_tool.remove_pos"));
		tooltip.add(Translator.translate("lore.custombgm.shape_tool.change_mode"));
		tooltip.add(Translator.translate("lore.custombgm.shape_tool.use_block"));
		BoundingShape shape = getShape(stack);
		if (shape instanceof BoundingShapeConfigurable) ((BoundingShapeConfigurable) shape).addInformation(stack, worldIn, tooltip, flagIn);
    }
}