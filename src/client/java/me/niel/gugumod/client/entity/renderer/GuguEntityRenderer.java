package me.niel.gugumod.client.entity.renderer;

import me.niel.gugumod.client.entity.model.ModEntityModelLayers;
import me.niel.gugumod.client.entity.state.GuguEntityRenderState;
import me.niel.gugumod.entity.GuguEntity;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

public class GuguEntityRenderer extends LivingEntityRenderer<GuguEntity, GuguEntityRenderState, HumanoidModel<GuguEntityRenderState>> {

    private static final Identifier SKIN = Identifier.fromNamespaceAndPath("gugu-mod", "textures/entity/gugu.png");

    public GuguEntityRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new HumanoidModel<>(context.getModelSet().bakeLayer(ModEntityModelLayers.GUGU)),
                0.5F
        );
    }

    @Override
    public @NonNull GuguEntityRenderState createRenderState() {
        return new GuguEntityRenderState();
    }

    @Override
    public void extractRenderState(@NonNull GuguEntity entity, @NonNull GuguEntityRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);

        // Ativa o capacete/camada externa se o modelo tiver
        this.model.hat.visible = true;
    }

    @Override
    public @NonNull Identifier getTextureLocation(@NonNull GuguEntityRenderState state) {
        return SKIN;
    }
}