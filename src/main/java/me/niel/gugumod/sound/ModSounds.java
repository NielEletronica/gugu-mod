package me.niel.gugumod.sound;


import me.niel.gugumod.GuguMod;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {
    private ModSounds() {
        // private empty constructor to avoid accidental instantiation
    }

    // ITEM_METAL_WHISTLE is the name of the custom sound event
    // and is called in the mod to use the custom sound
    public static final SoundEvent SAI_DE_PERTO = registerSound("sai_de_perto");
    public static final SoundEvent QUEBREI_MEU_DEDO = registerSound("quebrei_meu_dedo");
    public static final SoundEvent AI_MEU_CORACAO = registerSound("ai_meu_coracao");


    // actual registration of all the custom SoundEvents
    private static SoundEvent registerSound(String id) {
        Identifier identifier = GuguMod.id(id);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, identifier, SoundEvent.createVariableRangeEvent(identifier));
    }

    // This static method starts class initialization, which then initializes
    // the static class variables (e.g. ITEM_METAL_WHISTLE).
    public static void initialize() {
        GuguMod.LOGGER.info("Registering " + GuguMod.MOD_ID + " Sounds");
    }
}

