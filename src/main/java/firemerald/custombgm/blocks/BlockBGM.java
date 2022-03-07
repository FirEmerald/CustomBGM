package firemerald.custombgm.blocks;

import java.util.List;

import firemerald.api.betterscreens.TileEntityGUI;
import firemerald.api.core.client.Translator;
import firemerald.custombgm.Main;
import firemerald.custombgm.init.LSTabs;
import firemerald.custombgm.networking.client.TileGUIPacket;
import firemerald.custombgm.tileentity.TileEntityBGM;
import firemerald.custombgm.tileentity.TileEntityEntityOperator;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockBGM extends BlockContainer
{
    public BlockBGM()
    {
		super(Material.IRON);
		this.setSoundType(SoundType.METAL);
		this.setBlockUnbreakable();
		this.setCreativeTab(LSTabs.TAB);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    @Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityBGM();
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.MODEL;
    }

    /**
     * Called when the block is right clicked by a player.
     */
    @Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
    	if (!playerIn.capabilities.isCreativeMode) return false;
        if (worldIn.isRemote) return true;
        else
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof TileEntityGUI)
            {
            	if (playerIn instanceof EntityPlayerMP) Main.network().sendTo(new TileGUIPacket((TileEntityGUI) tileentity), (EntityPlayerMP) playerIn);
                //TODO playerIn.addStat(StatList.BEACON_INTERACTION);
            }
            return true;
        }
    }

    @Override
	public boolean hasComparatorInputOverride(IBlockState state)
    {
        return true;
    }

    @Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityBGM ? ((TileEntityBGM) tileentity).getSuccessCount() : 0;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    @Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityEntityOperator && stack.hasDisplayName()) ((TileEntityEntityOperator<?>) tileentity).setName(stack.getDisplayName());
    }

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, World world, List<String> list, ITooltipFlag flag)
	{
		super.addInformation(stack, world, list, flag);
		list.add(Translator.translate("lore.custombgm.bgm"));
		list.add(Translator.translate("lore.custombgm.redstone_activated"));
	}
}