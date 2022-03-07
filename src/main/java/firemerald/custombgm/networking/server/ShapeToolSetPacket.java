package firemerald.custombgm.networking.server;

import firemerald.api.core.networking.ServerPacket;
import firemerald.api.selectionshapes.BoundingShape;
import firemerald.api.selectionshapes.IShapeTool;
import firemerald.custombgm.common.CommonState;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public class ShapeToolSetPacket extends ServerPacket
{
	private EnumHand hand;
	private BoundingShape shape;

	public ShapeToolSetPacket() {}

	public ShapeToolSetPacket(EnumHand hand, BoundingShape shape)
	{
		this.hand = hand;
		this.shape = shape;
	}

	@Override
	public void fromBytes(ByteBuf buf)
	{
		hand = buf.readBoolean() ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		shape = BoundingShape.constructFromBuffer(buf);
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeBoolean(hand == EnumHand.OFF_HAND);
		shape.saveToBuffer(buf);
	}

	@Override
	public void handleServerSide(EntityPlayerMP player)
	{
		if (shape != null) CommonState.QUEUED_ACTIONS.add(() -> {
			ItemStack stack = player.getHeldItem(hand);
			if (!stack.isEmpty() && stack.getItem() instanceof IShapeTool)
			{
				IShapeTool tool = (IShapeTool) stack.getItem();
				if (tool.canAcceptShape(stack, shape)) tool.setShape(stack, shape);
			}
		});
	}

	public static class Handler extends ServerPacket.Handler<ShapeToolSetPacket> {}
}
