package miyucomics.efhexs.mixin;

import miyucomics.efhexs.misc.ClientInterface;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.particle.ParticleEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ParticleManager.class)
public class ParticleManagerMixin {
	@Inject(method = "createParticle", at = @At("HEAD"))
	private <T extends ParticleEffect> void onCreateParticle(T particleEffect, double d, double e, double f, double g, double h, double i, CallbackInfoReturnable<Particle> cir) {
		ClientInterface.pushParticle(particleEffect);
	}
}