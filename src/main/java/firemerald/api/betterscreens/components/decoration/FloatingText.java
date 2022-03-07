package firemerald.api.betterscreens.components.decoration;

import org.lwjgl.input.Keyboard;

import firemerald.api.betterscreens.EnumTextAlignment;
import firemerald.api.betterscreens.ScissorUtil;
import firemerald.api.betterscreens.components.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class FloatingText extends Component
{
	protected String text = "";
	public int clickPos = 0;
	public int selStart = 0, selEnd = 0;
	public FontRenderer font;
	protected float selX1, selX2;
	public float clickTime = 0;
	public int clickNum = 0;
	private EnumTextAlignment alignment = EnumTextAlignment.LEFT;
	protected int offset = 0;
	public int color = 0xE0E0E0;

	public FloatingText(int x1, int y1, int x2, int y2, FontRenderer font, String text, EnumTextAlignment alignment)
	{
		this(x1, y1, x2, y2, font);
		this.alignment = alignment;
		setText(text);
	}

	public FloatingText(int x1, int y1, int x2, int y2, FontRenderer font, String text)
	{
		this(x1, y1, x2, y2, font, text, EnumTextAlignment.LEFT);
	}

	public FloatingText(int x1, int y1, int x2, int y2, FontRenderer font)
	{
		super(x1, y1, x2, y2);
		this.font = font;
	}

	@Override
	public void setSize(int x1, int y1, int x2, int y2)
	{
		super.setSize(x1, y1, x2, y2);
		updateOffset();
		updatePos();
	}

	protected void updateOffset()
	{
		switch (alignment)
		{
		case CENTER:
			offset = ((x2 - x1) - font.getStringWidth(text)) / 2;
			break;
		case RIGHT:
			offset = (x2 - x1) - font.getStringWidth(text) - 2;
			break;
		case LEFT:
		default:
			offset = 2;
			break;
		}
	}

	protected void updatePos()
	{
		selX1 = x1 + font.getStringWidth(text.substring(0, selStart));
		selX2 = x1 + font.getStringWidth(text.substring(0, selEnd));
		clickNum = 0;
		clickTime = 0;
	}

	protected void updatePos2()
	{
		selX1 = x1 + font.getStringWidth(text.substring(0, selStart));
		selX2 = x1 + font.getStringWidth(text.substring(0, selEnd));
	}

	public void setText(String text)
	{
		this.text = text;
		selStart = selEnd = 0;
		updateOffset();
	}

	public String getText()
	{
		return text;
	}

	@Override
	public void onMousePressed(int mx, int my, int button)
	{
		mx -= offset;
		if (button == 0)
		{
			int clicked = font.trimStringToWidth(text, mx - x1).length();
			if (clicked == clickPos && clickTime > 0)
			{
				if (clickNum == 0)
				{
					selStart = getBeforePreviousSymbol(clicked);
					selEnd = getNextSymbol(clicked);
					updatePos2();
					clickNum = 1;
				}
				else if (clickNum == 1)
				{
					selStart = 0;
					selEnd = text.length();
					updatePos2();
					clickNum = 2;
				}
				else
				{
					clickPos = selStart = selEnd = clicked;
					updatePos();
				}
			}
			else
			{
				clickPos = selStart = selEnd = clicked;
				updatePos();
			}
			clickTime = .5f;
		}
	}

	public int getBeforePreviousSymbol(int pos)
	{
		if (pos <= 0) return 0;
		else if (pos >= text.length()) return text.length();
		else
		{
			for (int i = pos; i >= 0; i--) if (!Character.isLetterOrDigit(text.charAt(i))) return i + 1;
			return 0;
		}
	}

	public int getNextSymbol(int pos)
	{
		if (pos >= text.length()) return text.length();
		else
		{
			for (int i = pos; i < text.length(); i++) if (!Character.isLetterOrDigit(text.charAt(i))) return i;
			return text.length();
		}
	}

	@Override
	public void onDrag(int mx, int my, int button)
	{
		mx -= offset;
		if (button == 0)
		{
			int pos = font.trimStringToWidth(text, mx - x1).length();
			if (pos == clickPos) selStart = selEnd = pos;
			else if (pos < clickPos)
			{
				selStart = pos;
				selEnd = clickPos;
				clickTime = 0;
			}
			else if (pos > clickPos)
			{
				selStart = clickPos;
				selEnd = pos;
				clickTime = 0;
			}
			updatePos();
		}
	}

	@Override
	public void tick(Minecraft mc, int mx, int my)
	{
		if (clickTime > 0 && (clickTime -= .05f) < 0) clickTime = 0;
	}

	@Override
	public void render(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
	{
		this.setScissor(0, 0, x2 - x1, y2 - y1);
		font.drawString(text, x1 + offset, y1 + (y2 - y1 - 8) / 2, color);
		if (selStart != selEnd)
		{
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder bufferbuilder = tessellator.getBuffer();
	        GlStateManager.color(0, 0, 1, 1);
	        GlStateManager.disableTexture2D();
	        GlStateManager.enableColorLogic();
	        GlStateManager.colorLogicOp(GlStateManager.LogicOp.OR_REVERSE);
	        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
	        bufferbuilder.pos(selX1 + offset, y2 - 2, 0).endVertex();
	        bufferbuilder.pos(selX2 + offset, y2 - 2, 0).endVertex();
	        bufferbuilder.pos(selX2 + offset, y1 + 2, 0).endVertex();
	        bufferbuilder.pos(selX1 + offset, y1 + 2, 0).endVertex();
	        tessellator.draw();
	        GlStateManager.disableColorLogic();
	        GlStateManager.enableTexture2D();
		}
		GlStateManager.color(1, 1, 1, 1);
		ScissorUtil.popScissor();
	}

	public boolean onKey(char chr, int scancode)
	{
		switch (scancode)
		{
		case Keyboard.KEY_C:
		case Keyboard.KEY_X:
			if (GuiScreen.isCtrlKeyDown() && !GuiScreen.isShiftKeyDown() && !GuiScreen.isAltKeyDown())
			{
				onCopy();
				return true;
			}
		default:
			return false;
		}
	}

	public void onCopy()
	{
		if (selStart != selEnd) GuiScreen.setClipboardString(text.substring(selStart, selEnd));
	}

	@Override
	public void onUnfocus()
	{
		super.onUnfocus();
		selStart = selEnd = 0;
	}
}