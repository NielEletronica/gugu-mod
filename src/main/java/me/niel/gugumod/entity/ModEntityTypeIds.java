package me.niel.gugumod.entity;

import me.niel.gugumod.GuguMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;

public class ModEntityTypeIds {

    public static ResourceKey<EntityType<?>> GUGU = create();

    private static ResourceKey<EntityType<?>> create() {
        return ResourceKey.create(Registries.ENTITY_TYPE, GuguMod.id("gugu"));
    }

}
