package miyucomics.efhexs.mixin;

import miyucomics.efhexs.misc.ClientInterface;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SoundManager.class)
public class SoundManagerMixin {
	@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"))
	private void catchSound(SoundInstance soundInstance, CallbackInfo ci) {
		ClientInterface.pushSound(soundInstance);
	}

	@Inject(method = "play(Lnet/minecraft/client/sound/SoundInstance;I)V", at = @At("HEAD"))
	private void catchSound(SoundInstance soundInstance, int i, CallbackInfo ci) {
		ClientInterface.pushSound(soundInstance);
	}
}