package firemerald.custombgm.client.gui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;

import firemerald.api.betterscreens.GuiPopup;
import firemerald.api.betterscreens.IGuiElement;
import firemerald.api.betterscreens.components.Button;
import firemerald.api.betterscreens.components.scrolling.ScrollBar;
import firemerald.api.betterscreens.components.scrolling.ScrollableComponentPane;
import firemerald.api.core.client.Translator;
import firemerald.api.selectionshapes.BoundingShape;
import firemerald.api.selectionshapes.ButtonShape;
import firemerald.api.selectionshapes.IShapeGui;
import firemerald.api.selectionshapes.IShapeTool;
import firemerald.custombgm.Main;
import firemerald.custombgm.networking.server.ShapeToolSetPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;

public class GuiShapes extends GuiPopup implements IShapeGui
{
    public final Stack<Pair<BoundingShape, Consumer<BoundingShape>>> prevShapes = new Stack<>();
    public Consumer<BoundingShape> onAccept;
    public final ButtonShape currentShape;
    public final ShapeToolButton fromToolMain, toToolMain, fromToolOffhand, toToolOffhand;
    public final Button okay, cancel;
    public final ScrollableComponentPane entityButtons;
    public final ScrollBar entityButtonsScroll;
    private final List<IGuiElement> addedElements = new ArrayList<>();
    public final Vec3d pos;

    public static class ShapeToolButton extends Button
    {
    	public final GuiShapes gui;
    	public final EntityPlayer player;
    	public final EnumHand hand;

    	public ShapeToolButton(int x, int y, String buttonText, GuiShapes gui, EntityPlayer player, EnumHand hand, Runnable onClick)
    	{
    		super(x, y, buttonText, onClick);
    		this.gui = gui;
    		this.player = player;
    		this.hand = hand;
    	}

    	public ShapeToolButton(int x, int y, int widthIn, int heightIn, String buttonText, GuiShapes gui, EntityPlayer player, EnumHand hand, Runnable onClick)
    	{
    		super(x, y, widthIn, heightIn, buttonText, onClick);
    		this.gui = gui;
    		this.player = player;
    		this.hand = hand;
    	}

    	@Override
    	public void tick(Minecraft mc, int mx, int my)
    	{
    		ItemStack held = player.getHeldItem(hand);
    		this.enabled = !held.isEmpty() && held.getItem() instanceof IShapeTool && ((IShapeTool) held.getItem()).canAcceptShape(held, gui.currentShape.shape);
    	}
    }

	public GuiShapes(Vec3d pos, BoundingShape shape, Consumer<BoundingShape> onAccept)
	{
		this.pos = pos;
		this.fontRenderer = Minecraft.getMinecraft().fontRenderer;
		this.onAccept = onAccept;
		int y = 0;
		if (Minecraft.getMinecraft().player != null)
		{
			y += 20;
			y += 20;
			addElement(currentShape = new ButtonShape(100, y, 200, 20, shape, (s) -> updateGuiButtonsList()));
			y -= 20;
			EntityPlayer player = Minecraft.getMinecraft().player;
			addElement(fromToolMain = new ShapeToolButton(0, 0, 200, 20, Translator.translate("gui.custombgm.shape.frommainhand"), this, player, EnumHand.MAIN_HAND, () -> {
	    		ItemStack held = player.getHeldItem(EnumHand.MAIN_HAND);
	    		if (!held.isEmpty() && held.getItem() instanceof IShapeTool)
	    		{
	    			this.currentShape.setShape(((IShapeTool) held.getItem()).getShape(held));
	    			this.updateGuiButtonsList();
	    		}
			}));
			addElement(fromToolOffhand = new ShapeToolButton(200, 0, 200, 20, Translator.translate("gui.custombgm.shape.fromoffhand"), this, player, EnumHand.OFF_HAND, () -> {
	    		ItemStack held = player.getHeldItem(EnumHand.OFF_HAND);
	    		if (!held.isEmpty() && held.getItem() instanceof IShapeTool)
	    		{
	    			this.currentShape.setShape(((IShapeTool) held.getItem()).getShape(held));
	    			this.updateGuiButtonsList();
	    		}
			}));
			y += 20;
			addElement(toToolMain = new ShapeToolButton(0, 0, 200, 20, Translator.translate("gui.custombgm.shape.tomainhand"), this, player, EnumHand.MAIN_HAND, () -> Main.network().sendToServer(new ShapeToolSetPacket(EnumHand.MAIN_HAND, this.currentShape.shape))));
			addElement(toToolOffhand = new ShapeToolButton(200, 0, 200, 20, Translator.translate("gui.custombgm.shape.tooffhand"), this, player, EnumHand.OFF_HAND, () -> Main.network().sendToServer(new ShapeToolSetPacket(EnumHand.OFF_HAND, this.currentShape.shape))));
		}
		else
		{
			fromToolMain = fromToolOffhand = toToolMain = toToolOffhand = null;
			addElement(currentShape = new ButtonShape(100, y, 200, 20, shape, (s) -> updateGuiButtonsList()));
		}
		y += 20;
		addElement(entityButtons = new ScrollableComponentPane(0, y, 390, y + 100));
		addElement(entityButtonsScroll = new ScrollBar(390, y, 400, y + 100, entityButtons));
		entityButtons.setScrollBar(entityButtonsScroll);
		y += 100;
		addElement(okay = new Button(0, y, 200, 20, Translator.format("gui.done"), () -> {
			this.onAccept.accept(currentShape.shape);
			if (prevShapes.isEmpty()) this.deactivate();
			else
			{
				Pair<BoundingShape, Consumer<BoundingShape>> pair = prevShapes.pop();
				currentShape.setShape(pair.getLeft());
				this.onAccept = pair.getRight();
				updateGuiButtonsList();
			}
		}));
		addElement(cancel = new Button(200, y, 400, 20, Translator.format("gui.cancel"), () -> {
			if (prevShapes.isEmpty()) this.deactivate();
			else
			{
				Pair<BoundingShape, Consumer<BoundingShape>> pair = prevShapes.pop();
				currentShape.setShape(pair.getLeft());
				this.onAccept = pair.getRight();
				updateGuiButtonsList();
			}
		}));
		updateGuiButtonsList();
	}

	@Override
	public void initGui()
	{
		int offX = (width - 400) >> 1;
		int y = 0;
		if (fromToolMain != null)
		{
			fromToolMain.setSize(offX, y, offX + 200, y + 20);
			fromToolOffhand.setSize(offX + 200, y, offX + 400, y + 20);
			y += 20;
			toToolMain.setSize(offX, y, offX + 200, y + 20);
			toToolOffhand.setSize(offX + 200, y, offX + 400, y + 20);
			y += 20;
		}
		currentShape.setSize(offX + 95, y, offX + 295, y + 20);
		y += 20;
		entityButtons.setSize(offX, y, offX + 390, height - 20);
		entityButtonsScroll.setSize(offX + 390, y, offX + 400, height - 20);
		okay.setSize(offX, height - 20, offX + 200, height);
		cancel.setSize(offX + 200, height - 20, offX + 400, height);
		updateGuiButtonsList();
	}

	@Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (keyCode == 1) cancel.onClick.run();
        else super.keyTyped(typedChar, keyCode);
    }

	@Override
	public void updateGuiButtonsList()
	{
		addedElements.forEach(entityButtons::removeElement);
		this.currentShape.shape.addGuiElements(pos, this, fontRenderer, ((Consumer<IGuiElement>) entityButtons::addElement).andThen(addedElements::add), entityButtons.x2 - entityButtons.x1);
		entityButtons.updateComponentSize();
		entityButtons.updateScrollSize();
	}

	@Override
	public void openShape(BoundingShape shape, Consumer<BoundingShape> onAccepted)
	{
		this.prevShapes.push(Pair.of(this.currentShape.shape, this.onAccept));
		this.onAccept = onAccepted;
		this.currentShape.setShape(shape);
		updateGuiButtonsList();
	}

	@Override
	public void doRender(Minecraft mc, int mx, int my, float partialTicks, boolean canHover)
	{
		GlStateManager.disableTexture2D();
		GlStateManager.color(0, 0, 0, .5f);
		Tessellator t = Tessellator.getInstance();
		BufferBuilder b = t.getBuffer();
		b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION);
		b.pos(0    , height, 0).endVertex();
		b.pos(width, height, 0).endVertex();
		b.pos(width, 0     , 0).endVertex();
		b.pos(0    , 0     , 0).endVertex();
		t.draw();
		GlStateManager.color(1, 1, 1, 1);
		GlStateManager.enableTexture2D();
	}
}