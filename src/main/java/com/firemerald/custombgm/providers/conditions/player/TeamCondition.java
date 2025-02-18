package com.firemerald.custombgm.providers.conditions.player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.firemerald.custombgm.api.providers.conditions.BGMProviderPlayerCondition;
import com.firemerald.custombgm.api.providers.conditions.PlayerConditionData;
import com.firemerald.fecore.codec.Codecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Team;

public class TeamCondition implements BGMProviderPlayerCondition {
	public static final MapCodec<TeamCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
		instance.group(
				Codecs.compactListCodec(Codec.STRING).fieldOf("team").forGetter(TeamCondition::teams)
				)
		.apply(instance, TeamCondition::new)
	);

	public final Set<String> teams;

	private TeamCondition(Set<String> teams) {
		this.teams = teams;
	}

	public TeamCondition(Collection<String> teams) {
		this(new HashSet<>(teams));
	}

	public TeamCondition(String... teams) {
		this(Arrays.asList(teams));
	}

	public List<String> teams() {
		return new ArrayList<>(teams);
	}

	@Override
	public MapCodec<TeamCondition> codec() {
		return CODEC;
	}

	@Override
	public boolean test(PlayerConditionData playerData, Player player) {
        Team team = player.getTeam();
        if (team == null) return teams.size() == 0;
        else return teams.contains(team.getName());
	}
}