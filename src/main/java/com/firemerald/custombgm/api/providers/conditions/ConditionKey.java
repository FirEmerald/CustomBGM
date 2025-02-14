package com.firemerald.custombgm.api.providers.conditions;

import net.minecraft.resources.ResourceLocation;

public class ConditionKey<T> {
	public final ResourceLocation id;

	public ConditionKey(ResourceLocation id) {
		this.id = id;
	}

    @Override
    public String toString() {
        return "ConditionKey<" + id.toString() + ">";
    }

	@Override
    public boolean equals(Object other) {
    	if (other == null) return false;
    	else if (other == this) return true;
    	else if (other.getClass() == this.getClass()) return id.equals(((ConditionKey<?>) other).id);
    	else return false;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}