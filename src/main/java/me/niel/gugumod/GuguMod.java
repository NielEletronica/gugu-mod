package me.niel.gugumod;

import me.niel.gugumod.entity.GuguEntity;
import me.niel.gugumod.entity.ModEntityTypes;
import me.niel.gugumod.item.ModItems;
import me.niel.gugumod.sound.ModSounds;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.levelgen.Heightmap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuguMod implements ModInitializer {
	public static final String MOD_ID = "gugu-mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModItems.initialize();
		ModEntityTypes.initialize();
		ModSounds.initialize();

		BiomeModifications.addSpawn(
				BiomeSelectors.tag(BiomeTags.IS_OVERWORLD),
				MobCategory.CREATURE,           // Creature = passive mobs (cows, pigs)
				ModEntityTypes.GUGU,               // Your EntityType
				10,                             // Weight (relative spawn frequency)
				2,                              // Min group size
				20                               // Max group size
		);

		SpawnPlacements.register(
				ModEntityTypes.GUGU,
				SpawnPlacementTypes.ON_GROUND,
				Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
				Mob::checkMobSpawnRules
		);

		FabricDefaultAttributeRegistry.register(ModEntityTypes.GUGU, GuguEntity.createGuguAttributes());

		LOGGER.info("Gugu Mod inicializado");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
