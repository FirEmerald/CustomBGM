package firemerald.api.betterscreens;

import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.LogicOp;
import net.minecraft.util.math.MathHelper;

public enum ButtonState
{
	NONE(new Vector3f(1, 1, 1), false),
	INVERT(new Vector3f(1, 1, 1), true),
	HOVER(new Vector3f(.75f, .75f, 1), false),
	PUSH(new Vector3f(.75f, .75f, .75f), true),
	DISABLED(new Vector3f(.5f, .5f, .5f), false);

	public final Vector3f color;
	public final boolean invert;

	ButtonState(Vector3f color, boolean invert)
	{
		this.color = color;
		this.invert = invert;
	}

	public void applyButtonEffects(boolean useColor)
	{
		if (useColor) GlStateManager.color(color.x, color.y, color.z);
		if (invert)
		{
	        GlStateManager.enableColorLogic();
			GlStateManager.colorLogicOp(LogicOp.COPY_INVERTED);
		}
	}

	public void applyButtonEffects(float a)
	{
		GlStateManager.color(color.x, color.y, color.z, a);
		if (invert)
		{
	        GlStateManager.enableColorLogic();
			GlStateManager.colorLogicOp(LogicOp.COPY_INVERTED);
		}
	}

	public void removeButtonEffects(boolean useColor)
	{
		if (useColor) GlStateManager.color(1, 1, 1);
		if (invert)
		{
			GlStateManager.colorLogicOp(LogicOp.COPY);
	        GlStateManager.disableColorLogic();
		}
	}

	public void removeButtonEffects(float a)
	{
		GlStateManager.color(1, 1, 1, a);
		if (invert)
		{
			GlStateManager.colorLogicOp(LogicOp.COPY);
	        GlStateManager.disableColorLogic();
		}
	}

	public Vector3f getColor(Vector3f c)
	{
		return getColor(c.x, c.y, c.z);
	}

	public Vector3f getColor(float r, float g, float b)
	{
		Vector3f v = invert ? new Vector3f(1 - r, 1 - g, 1 - b) : new Vector3f(r, g, b);
		v.x *= color.x;
		v.y *= color.y;
		v.z *= color.z;
		return v;
	}

	public int getColorInt(Vector3f c)
	{
		return getColorInt(c.x, c.y, c.z);
	}

	public int getColorInt(float r, float g, float b)
	{
		return ((int) MathHelper.clamp((invert ? 1 - r : r) * color.x * 255, 0, 255) << 16) | ((int) MathHelper.clamp((invert ? 1 - g : g) * color.y * 255, 0, 255) << 8) | ((int) MathHelper.clamp((invert ? 1 - b : b) * color.z * 255, 0, 255));
	}

	public int getColorInt(Vector4f c)
	{
		return getColorInt(c.x, c.y, c.z, c.w);
	}

	public int getColorInt(Vector3f c, float a)
	{
		return getColorInt(c.x, c.y, c.z, a);
	}

	public int getColorInt(float r, float g, float b, float a)
	{
		return ((int) MathHelper.clamp(a * 255, 0, 255) <<   24) | ((int) MathHelper.clamp((invert ? 1 - r : r) * color.x * 255, 0, 255) << 16) | ((int) MathHelper.clamp((invert ? 1 - g : g) * color.y * 255, 0, 255) << 8) | ((int) MathHelper.clamp((invert ? 1 - b : b) * color.z * 255, 0, 255));
	}
}