package firemerald.api.selectionshapes;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class BoundingShapeConfigurable extends BoundingShape
{
	public abstract int addPosition(EntityPlayer player, BlockPos blockPos, int num);

	public abstract int removePosition(EntityPlayer player, int num);

	@SideOnly(Side.CLIENT)
    public abstract void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn);
}