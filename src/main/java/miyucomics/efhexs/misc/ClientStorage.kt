package miyucomics.efhexs.misc

import miyucomics.efhexs.EfhexsMain
import miyucomics.hexpose.utils.RingBuffer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.minecraft.client.sound.SoundInstance
import net.minecraft.particle.ParticleEffect
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier

object ClientStorage {
	var recording = false
	private var particles: RingBuffer<Identifier> = RingBuffer(32)
	private var sounds: RingBuffer<Identifier> = RingBuffer(32)

	@JvmStatic
	fun pushParticle(particle: ParticleEffect) {
		if (recording)
			particles.add(Registries.PARTICLE_TYPE.getId(particle.type)!!)
	}

	@JvmStatic
	fun pushSound(sound: SoundInstance) {
		if (recording)
			sounds.add(sound.id)
	}

	fun pushParticlesToServer() {
		val buf = PacketByteBufs.create()
		buf.writeInt(particles.buffer().size)
		particles.buffer().forEach { buf.writeIdentifier(it) }
		ClientPlayNetworking.send(EfhexsMain.PARTICLE_CHANNEL, buf)
	}

	fun pushSoundsToServer() {
		val buf = PacketByteBufs.create()
		buf.writeInt(sounds.buffer().size)
		sounds.buffer().forEach { buf.writeIdentifier(it) }
		ClientPlayNetworking.send(EfhexsMain.SOUND_CHANNEL, buf)
	}
}