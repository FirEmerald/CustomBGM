package firemerald.api.selectionshapes;

import java.util.function.Consumer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IShapeGui
{
	public void updateGuiButtonsList();

	public void openShape(BoundingShape shape, Consumer<BoundingShape> onAccepted);
}