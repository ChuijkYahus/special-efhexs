package miyucomics.efhexs.misc

import miyucomics.efhexs.EfhexsMain
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.sound.SoundInstance
import net.minecraft.particle.ParticleEffect
import net.minecraft.registry.Registries

object ClientInterface {
	@JvmStatic
	fun pushParticle(particle: ParticleEffect) {
		ClientPlayNetworking.send(EfhexsMain.PARTICLE_CHANNEL, PacketByteBufs.create().apply { writeIdentifier(Registries.PARTICLE_TYPE.getId(particle.type)!!) })
	}

	@JvmStatic
	fun pushSound(sound: SoundInstance) {
		ClientPlayNetworking.send(EfhexsMain.SOUND_CHANNEL, PacketByteBufs.create().apply { writeIdentifier(sound.id) })
	}
}