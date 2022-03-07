package firemerald.custombgm.plugin.transformers;

import java.util.Iterator;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformMinecraft extends LSTransformer
{
	public static final String getAmbientMusicType = IS_DEOBFUSCATED ? "getAmbientMusicType" : "func_147109_W";
	public static final TransformMinecraft INSTANCE = new TransformMinecraft();

	public TransformMinecraft()
	{
		super(false);
	}

	@Override
	public void transform(ClassNode classNode, String className)
	{
		Iterator<MethodNode> methods = classNode.methods.iterator();
		while (methods.hasNext())
		{
			MethodNode m = methods.next();
			if (m.name.equals(getAmbientMusicType))
			{
				logger().debug("Patching " + getAmbientMusicType);
				int size = m.instructions.size();
				for (int i = 0; i < size; i++)
				{
					AbstractInsnNode node = m.instructions.get(i);
					if (node instanceof InsnNode && node.getOpcode() == ARETURN)
					{
						InsnList toInject = new InsnList();
						toInject.add(new LabelNode());
						toInject.add(new VarInsnNode(ALOAD, 0));
						toInject.add(new MethodInsnNode(INVOKESTATIC, "firemerald/custombgm/client/ClientState", "getCustomMusic", "(Lnet/minecraft/client/audio/MusicTicker$MusicType;Lnet/minecraft/client/Minecraft;)Lnet/minecraft/client/audio/MusicTicker$MusicType;", false));
						m.instructions.insertBefore(node, toInject);
						i += 3;
						size += 3;
					}
				}
			}
		}
	}
}