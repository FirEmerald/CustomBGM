package firemerald.custombgm.command;

import java.util.List;

import com.google.common.collect.ImmutableList;

import firemerald.custombgm.tileentity.TileEntityEntityOperator;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.CommandStats;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandLSStats extends CommandStats
{
    /**
     * Gets the name of the command
     */
    @Override
	public String getName()
    {
        return "lsstats"; //can't be stats or it won't override vanilla
    }

	@Override
    public List<String> getAliases()
    {
		return ImmutableList.of("stats");
    }

    /**
     * Callback for when the command is executed
     */
    @Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1) throw new WrongUsageException("commands.stats.usage", new Object[0]);
        else
        {
            boolean isBlock;
            if ("entity".equals(args[0])) isBlock = false;
            else
            {
                if (!"block".equals(args[0])) throw new WrongUsageException("commands.stats.usage", new Object[0]);
                isBlock = true;
            }
            int i;
            if (isBlock)
            {
                if (args.length < 5) throw new WrongUsageException("commands.stats.block.usage", new Object[0]);
                i = 4;
            }
            else
            {
                if (args.length < 3) throw new WrongUsageException("commands.stats.entity.usage", new Object[0]);
                i = 2;
            }
            String s = args[i++];
            if ("set".equals(s))
            {
                if (args.length < i + 3)
                {
                    if (i == 5) throw new WrongUsageException("commands.stats.block.set.usage", new Object[0]);
                    throw new WrongUsageException("commands.stats.entity.set.usage", new Object[0]);
                }
            }
            else
            {
                if (!"clear".equals(s)) throw new WrongUsageException("commands.stats.usage", new Object[0]);
                if (args.length < i + 1)
                {
                    if (i == 5) throw new WrongUsageException("commands.stats.block.clear.usage", new Object[0]);
                    throw new WrongUsageException("commands.stats.entity.clear.usage", new Object[0]);
                }
            }

            CommandResultStats.Type commandresultstats$type = CommandResultStats.Type.getTypeByName(args[i++]);
            if (commandresultstats$type == null) throw new CommandException("commands.stats.failed", new Object[0]);
            else
            {
                World world = sender.getEntityWorld();
                CommandResultStats commandresultstats;
                if (isBlock)
                {
                    BlockPos blockpos = parseBlockPos(sender, args, 1, false);
                    TileEntity tileentity = world.getTileEntity(blockpos);
                    if (tileentity == null) throw new CommandException("commands.stats.noCompatibleBlock", new Object[] {blockpos.getX(), blockpos.getY(), blockpos.getZ()});
                    if (tileentity instanceof TileEntityCommandBlock) commandresultstats = ((TileEntityCommandBlock)tileentity).getCommandResultStats();
                    else if (tileentity instanceof TileEntitySign) commandresultstats = ((TileEntitySign)tileentity).getStats();
                    else if (tileentity instanceof TileEntityEntityOperator) commandresultstats = ((TileEntityEntityOperator<?>)tileentity).getStats();
                    else throw new CommandException("commands.stats.noCompatibleBlock", new Object[] {blockpos.getX(), blockpos.getY(), blockpos.getZ()});
                }
                else
                {
                    Entity entity = getEntity(server, sender, args[1]);
                    commandresultstats = entity.getCommandStats();
                }
                if ("set".equals(s))
                {
                    String s1 = args[i++];
                    String s2 = args[i];
                    if (s1.isEmpty() || s2.isEmpty()) throw new CommandException("commands.stats.failed", new Object[0]);
                    CommandResultStats.setScoreBoardStat(commandresultstats, commandresultstats$type, s1, s2);
                    notifyCommandListener(sender, this, "commands.stats.success", new Object[] {commandresultstats$type.getTypeName(), s2, s1});
                }
                else if ("clear".equals(s))
                {
                    CommandResultStats.setScoreBoardStat(commandresultstats, commandresultstats$type, (String)null, (String)null);
                    notifyCommandListener(sender, this, "commands.stats.cleared", new Object[] {commandresultstats$type.getTypeName()});
                }
                if (isBlock)
                {
                    BlockPos blockpos1 = parseBlockPos(sender, args, 1, false);
                    TileEntity tileentity1 = world.getTileEntity(blockpos1);
                    tileentity1.markDirty();
                }
            }
        }
    }
}