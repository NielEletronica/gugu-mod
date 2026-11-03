package me.niel.gugumod.client;

import me.niel.gugumod.client.entity.model.ModEntityModelLayers;
import me.niel.gugumod.client.entity.renderer.GuguEntityRenderer;
import me.niel.gugumod.entity.ModEntityTypes;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.entity.EntityRenderers;

public class GuguModClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ModEntityModelLayers.registerModelLayers();
		EntityRenderers.register(ModEntityTypes.GUGU, GuguEntityRenderer::new);
	}
}