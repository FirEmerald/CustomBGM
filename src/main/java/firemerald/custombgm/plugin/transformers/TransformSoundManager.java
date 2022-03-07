package firemerald.custombgm.plugin.transformers;

import java.util.Iterator;

import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class TransformSoundManager extends LSTransformer
{
	public static final String stopAllSounds = IS_DEOBFUSCATED ? "stopAllSounds" : "func_148614_c";
	public static final String pauseAllSounds = IS_DEOBFUSCATED ? "pauseAllSounds" : "func_148610_e";
	public static final String resumeAllSounds = IS_DEOBFUSCATED ? "resumeAllSounds" : "func_148604_f";
	public static final TransformSoundManager INSTANCE = new TransformSoundManager();

	public TransformSoundManager()
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
			if (m.name.equals(stopAllSounds))
			{
				logger().debug("Patching " + stopAllSounds);
				InsnList toInject = new InsnList();
				toInject.add(new LabelNode(new Label()));
				toInject.add(new MethodInsnNode(INVOKESTATIC, "firemerald/custombgm/client/audio/LoopingSounds", "stopAll", "()V", false));
				m.instructions.insert(toInject);
			}
			else if (m.name.equals(pauseAllSounds))
			{
				logger().debug("Patching " + pauseAllSounds);
				InsnList toInject = new InsnList();
				toInject.add(new LabelNode(new Label()));
				toInject.add(new MethodInsnNode(INVOKESTATIC, "firemerald/custombgm/client/audio/LoopingSounds", "pauseAll", "()V", false));
				m.instructions.insert(toInject);
			}
			else if (m.name.equals(resumeAllSounds))
			{
				logger().debug("Patching " + resumeAllSounds);
				InsnList toInject = new InsnList();
				toInject.add(new LabelNode(new Label()));
				toInject.add(new MethodInsnNode(INVOKESTATIC, "firemerald/custombgm/client/audio/LoopingSounds", "resumeAll", "()V", false));
				m.instructions.insert(toInject);
			}
		}
	}
}