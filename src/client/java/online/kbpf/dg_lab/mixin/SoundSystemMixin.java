package online.kbpf.dg_lab.mixin;

import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.SoundInstance;
import online.kbpf.dg_lab.client.entity.NoiseManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundSystem.class)
public class SoundSystemMixin {
    @Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"))
    public void onPlaySound(SoundInstance sound, CallbackInfo ci) {
        if (sound != null && sound.getId() != null) {
            // 将声音的ID传入管理器
            NoiseManager.onSoundPlayed(sound.getId().toString());
        }
    }
}
