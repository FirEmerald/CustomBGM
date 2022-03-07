package firemerald.api.betterscreens;

import java.awt.Rectangle;
import java.util.Stack;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class ScissorUtil
{
	private static final Stack<Rectangle> scissorStack = new Stack<>();
	private static Rectangle scissor = new Rectangle(0, 0, 1, 1);

	public static void updateScissor()
	{
		GL11.glScissor(scissor.x, scissor.y, scissor.width, scissor.height);
	}

	public static void clearScissor()
	{
		scissorStack.clear();
		scissor = new Rectangle(0, 0, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
		GL11.glDisable(GL11.GL_SCISSOR_TEST);
		updateScissor();
	}

	public static void pushScissor(int x1, int y1, int x2, int y2)
	{
		Minecraft client = Minecraft.getMinecraft();
        ScaledResolution res = new ScaledResolution(client);
        double scaleW = client.displayWidth / res.getScaledWidth_double();
        double scaleH = client.displayHeight / res.getScaledHeight_double();
        x1 *= scaleW;
        y1 *= scaleH;
        x2 *= scaleW;
        y2 *= scaleH;
		int t = y1;
		y1 = client.displayHeight - y2;
		y2 = client.displayHeight - t;
		x1 = Math.max(x1, scissor.x);
		y1 = Math.max(y1, scissor.y);
		x2 = Math.min(x2, scissor.x + scissor.width);
		y2 = Math.min(y2, scissor.y + scissor.height);
		if (scissorStack.isEmpty()) GL11.glEnable(GL11.GL_SCISSOR_TEST);
		scissorStack.push(scissor);
		scissor = new Rectangle(x1, y1, x2 <= x1 ? 0 : x2 - x1, y2 <= y1 ? 0 : y2 - y1);
		updateScissor();
	}

	public static void popScissor()
	{
		scissor = scissorStack.pop();
		if (scissorStack.isEmpty()) GL11.glDisable(GL11.GL_SCISSOR_TEST);
		updateScissor();
	}

	public static void disableScissor()
	{
		if (!scissorStack.isEmpty()) GL11.glDisable(GL11.GL_SCISSOR_TEST);
	}

	public static void enableScissor()
	{
		if (!scissorStack.isEmpty()) GL11.glEnable(GL11.GL_SCISSOR_TEST);
	}
}