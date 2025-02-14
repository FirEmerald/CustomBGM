package com.firemerald.custombgm.util;

import com.firemerald.custombgm.api.BGM;
import com.firemerald.custombgm.api.LoopType;

import net.minecraft.resources.ResourceLocation;

public class VolumedBGM extends BGM {
	public final float volume;
	
	public VolumedBGM(ResourceLocation sound, LoopType loop, float volume) {
		super(sound, loop);
		this.volume = volume;
	}
	
	public VolumedBGM(BGM bgm, float volume) {
		this(bgm.sound(), bgm.loop(), volume);
	}
	
	public VolumedBGM(VolumedBGM other) {
		this(other.sound, other.loop, other.volume);
	}

	@Override
	public int compareTo(BGM other) {
        int i = sound.compareTo(other.sound);
        if (i == 0) i = loop.compareTo(other.loop);
        if (i == 0 && other instanceof VolumedBGM volumed) i = Float.compare(volume, volumed.volume);
        return i;
	}
	
	public float volume() {
		return volume;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		else if (o == this) return true;
		else if (o.getClass() != this.getClass()) return false;
		else {
			VolumedBGM other = (VolumedBGM) o;
			return other.loop == loop && other.volume == volume && other.sound.equals(sound);
		}
	}
	
	@Override
	public String toString() {
		return "VolumedBGM<sound=" + sound.toString() + ",loop=" + loop.toString() + ",volume=" + volume + ">";
	}
	
	@Override
	public VolumedBGM clone() {
		return new VolumedBGM(this);
	}
}
