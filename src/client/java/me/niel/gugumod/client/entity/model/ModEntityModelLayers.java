package me.niel.gugumod.client.entity.model;

import me.niel.gugumod.GuguMod;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

public class ModEntityModelLayers {

    public static final ModelLayerLocation GUGU = createMain();

    private static ModelLayerLocation createMain() {
        return new ModelLayerLocation(GuguMod.id("gugu"), "main");
    }

    public static LayerDefinition getTexturedModelData() {
        return LayerDefinition.create(
                HumanoidModel.createMesh(CubeDeformation.NONE, 0.0f),
                64, 64
        );
    }

    public static void registerModelLayers() {
        ModelLayerRegistry.registerModelLayer(GUGU, ModEntityModelLayers::getTexturedModelData);
    }

}
