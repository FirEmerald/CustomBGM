package firemerald.custombgm.tileentity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import firemerald.api.betterscreens.GuiTileEntityGui;
import firemerald.api.core.IChunkLoader;
import firemerald.custombgm.Main;
import firemerald.custombgm.api.Capabilities;
import firemerald.custombgm.api.IPlayer;
import firemerald.custombgm.client.gui.GuiBossSpawner;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.common.ForgeChunkManager.Ticket;
import net.minecraftforge.common.ForgeChunkManager.Type;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBossSpawner extends TileEntityEntityOperator<EntityPlayer> implements IChunkLoader
{
	public ResourceLocation music;
	public int priority;
	public boolean isRelative = true, disableMusic = false;
	public double spawnX = 0.5, spawnY = 1, spawnZ = 0.5;
	public ResourceLocation toSpawn;
	public NBTTagCompound spawnTag = new NBTTagCompound();
	public Entity boss;
	private UUID bossID;
	private Ticket ticket = null;
	public boolean killed = false;
	public int levelOnActive = 7, levelOnKilled = 15;

	public TileEntityBossSpawner()
	{
		super(EntityPlayer.class);
	}

	public int getLevel()
	{
		if (killed) return levelOnKilled;
		else if (boss != null || bossID != null) return levelOnActive;
		else return 0;
	}

	public void setKilled(boolean killed)
	{
		if (this.killed != killed)
		{
			this.killed = killed;
			world.updateComparatorOutputLevel(pos, world.getBlockState(pos).getBlock());
			this.markDirty();
		}
	}

    public Ticket getTicket()
    {
    	return this.ticket;
    }

    public void updateTicket()
    {
    	if (ticket != null)
    	{
    		if (boss == null) //remove ticket
    		{
    			ForgeChunkManager.releaseTicket(ticket);
    			ticket = null;
    		}
    		else
    		{
            	Set<ChunkPos> chunks = ticket.getChunkList();
            	Set<ChunkPos> required = new HashSet<>();
            	required.add(new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
            	required.add(new ChunkPos(MathHelper.floor(boss.posX / 16), MathHelper.floor(boss.posZ / 16)));
            	chunks.stream().filter(chunk -> !required.contains(chunk)).forEach(chunk -> ForgeChunkManager.unforceChunk(ticket, chunk)); //release old chunks
            	required.stream().filter(chunk -> !chunks.contains(chunk)).forEach(chunk -> ForgeChunkManager.forceChunk(ticket, chunk)); //capture new chunks
    		}
    	}
    	else if (boss != null) //create ticket
    	{
    		ticket = ForgeChunkManager.requestTicket(Main.instance(), world, Type.NORMAL);
        	ForgeChunkManager.forceChunk(ticket, new ChunkPos(pos.getX() >> 4, pos.getZ() >> 4));
        	ForgeChunkManager.forceChunk(ticket, new ChunkPos(MathHelper.floor(boss.posX / 16), MathHelper.floor(boss.posZ / 16)));
    	}
    }

	@Override
	public void update()
	{
		if (!world.isRemote && world.isBlockIndirectlyGettingPowered(pos) <= 0) setKilled(false);
		super.update();
		if (!world.isRemote)
		{
			if (bossID != null && boss == null)
			{
				boss = world.loadedEntityList.stream().filter(entity -> entity.getPersistentID().equals(bossID)).findFirst().orElse(null);
			}
			if (boss != null && boss.isDead) //was killed
			{
				setKilled(true);
				this.setBoss(null);
			}
			if (boss != null && !boss.isAddedToWorld()) //release reference on despawned entity
			{
				boss = null;
			}
			int count = this.getSuccessCount();
			if (boss == null) //boss does not exist
			{
				if (isActive() && count > 0 && toSpawn != null) //spawn boss
				{
					Entity entity = EntityList.createEntityByIDFromName(toSpawn, world);
					double x, y, z;
					if (isRelative)
					{
						x = spawnX + pos.getX();
						y = spawnY + pos.getY();
						z = spawnZ + pos.getZ();
					}
					else
					{
						x = spawnX;
						y = spawnY;
						z = spawnZ;
					}
					if (spawnTag != null)
					{
		                NBTTagCompound nbttagcompound = entity.writeToNBT(new NBTTagCompound());
		                UUID uuid = entity.getUniqueID();
		                nbttagcompound.merge(spawnTag);
		                entity.readFromNBT(spawnTag);
		                entity.setUniqueId(uuid);
					}
					entity.setPosition(x, y, z);
					setBoss(entity);
					world.spawnEntity(entity);
				}
			}
			else if ((!isActive() || count <= 0) && boss != null) //no players in area, despawn boss
			{
				despawn();
			}
			updateTicket();
		}
	}

	public void setBoss(Entity boss)
	{
		if (boss != this.boss)
		{
			if ((this.boss = boss) == null)
			{
				this.bossID = null;
			}
			else
			{
				this.bossID = boss.getPersistentID();
			}
			updateTicket();
			this.markDirty();
			world.updateComparatorOutputLevel(pos, world.getBlockState(pos).getBlock());
		}
	}

	public void despawn()
	{
		if (boss != null)
		{
			boss.world.removeEntity(boss);
			setBoss(null);
		}
	}

	@Override
	public boolean isActive()
	{
		return !world.isRemote && !killed && toSpawn != null && world.isBlockIndirectlyGettingPowered(pos) > 0;
	}

	@Override
	public boolean operate(EntityPlayer player)
	{
		if (boss != null && !disableMusic)
		{
			IPlayer iPlayer = player.getCapability(Capabilities.player, null);
			if (iPlayer != null) iPlayer.addMusicOverride(music, priority);
		}
		return true;
	}

	@Override
	public Stream<? extends EntityPlayer> allEntities()
	{
		return world.playerEntities.stream();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag)
	{
		super.readFromNBT(tag);
		String music = tag.getString("music");
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
		priority = tag.getInteger("priority");
		if (tag.hasKey("isRelative", 99)) isRelative = tag.getBoolean("isRelative");
		else isRelative = true;
		disableMusic = tag.getBoolean("disableMusic");
		killed = tag.getBoolean("killed");
		spawnX = tag.getDouble("spawnX");
		spawnY = tag.getDouble("spawnY");
		spawnZ = tag.getDouble("spawnZ");
		String toSpawn = tag.getString("toSpawn");
		this.toSpawn = toSpawn.isEmpty() ? null : new ResourceLocation(toSpawn);
		if (tag.hasKey("spawnTag", 10))
		{
			spawnTag = tag.getCompoundTag("spawnTag");
			if (spawnTag.hasNoTags()) spawnTag = null;
		}
		else spawnTag = null;
		if (tag.hasKey("bossIDMost", 4) && tag.hasKey("bossIDLeast", 4)) bossID = new UUID(tag.getLong("bossIDMost"), tag.getLong("bossIDLeast"));
		else bossID = null;
		levelOnActive = tag.hasKey("levelOnActive", 99) ? tag.getByte("levelOnActive") : 7;
		levelOnKilled = tag.hasKey("levelOnKilled", 99) ? tag.getByte("levelOnKilled") : 15;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag)
	{
		tag = super.writeToNBT(tag);
		tag.setString("music", music == null ? "" : music.toString());
		tag.setInteger("priority", priority);
		tag.setBoolean("isRelative", isRelative);
		tag.setBoolean("disableMusic", disableMusic);
		tag.setBoolean("killed", killed);
		tag.setDouble("spawnX", spawnX);
		tag.setDouble("spawnY", spawnY);
		tag.setDouble("spawnZ", spawnZ);
		tag.setString("toSpawn", toSpawn == null ? "" : toSpawn.toString());
		if (spawnTag != null) tag.setTag("spawnTag", spawnTag);
		if (bossID != null)
		{
			tag.setLong("bossIDMost", bossID.getMostSignificantBits());
			tag.setLong("bossIDLeast", bossID.getLeastSignificantBits());
		}
		tag.setByte("levelOnActive", (byte) levelOnActive);
		tag.setByte("levelOnKilled", (byte) levelOnKilled);
		return tag;
	}

	@Override
	public void read(ByteBuf buf)
	{
		super.read(buf);
		String music = ByteBufUtils.readUTF8String(buf);
		this.music = music.isEmpty() ? null : new ResourceLocation(music);
		priority = buf.readInt();
		isRelative = buf.readBoolean();
		disableMusic = buf.readBoolean();
		spawnX = buf.readDouble();
		spawnY = buf.readDouble();
		spawnZ = buf.readDouble();
		String toSpawn = ByteBufUtils.readUTF8String(buf);
		this.toSpawn = toSpawn.isEmpty() ? null : new ResourceLocation(toSpawn);
		spawnTag = ByteBufUtils.readTag(buf);
		if (spawnTag.hasNoTags()) spawnTag = null;
		byte levels = buf.readByte();
		levelOnActive = levels & 0xF;
		levelOnKilled = (levels >> 4) & 0xF;
	}

	@Override
	public void write(ByteBuf buf)
	{
		super.write(buf);
		ByteBufUtils.writeUTF8String(buf, music == null ? "" : music.toString());
		buf.writeInt(priority);
		buf.writeBoolean(isRelative);
		buf.writeBoolean(disableMusic);
		buf.writeDouble(spawnX);
		buf.writeDouble(spawnY);
		buf.writeDouble(spawnZ);
		ByteBufUtils.writeUTF8String(buf, toSpawn == null ? "" : toSpawn.toString());
		ByteBufUtils.writeTag(buf, spawnTag == null ? new NBTTagCompound() : spawnTag);
		buf.writeByte(levelOnActive | (levelOnKilled << 4));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public GuiTileEntityGui getScreen()
	{
		return new GuiBossSpawner(pos);
	}

	@Override
	public boolean setTicket(Ticket ticket)
	{
		this.ticket = ticket;
		return true;
	}
}