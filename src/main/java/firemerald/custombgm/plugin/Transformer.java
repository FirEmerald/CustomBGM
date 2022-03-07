package firemerald.custombgm.plugin;

import firemerald.api.core.plugin.TransformerBase;
import firemerald.custombgm.plugin.transformers.TransformMinecraft;
import firemerald.custombgm.plugin.transformers.TransformSoundManager;

public class Transformer extends TransformerBase
{
	@Override
	public void addCommonTransformers()
	{
	}

	@Override
	public void addClientTransformers()
	{
		transformers.put("net.minecraft.client.Minecraft", TransformMinecraft.INSTANCE);
		transformers.put("net.minecraft.client.audio.SoundManager", TransformSoundManager.INSTANCE);
	}
}