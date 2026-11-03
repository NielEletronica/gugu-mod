package me.niel.gugumod.item;

import me.niel.gugumod.GuguMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

public class ModItemIds {

    public static final ResourceKey<Item> GUGU_SPAWN_EGG = create("gugu_spawn_egg");

    public static ResourceKey<Item> create(String name) {
        // Create the item key.
        return ResourceKey.create(Registries.ITEM, GuguMod.id(name));
    }
}
