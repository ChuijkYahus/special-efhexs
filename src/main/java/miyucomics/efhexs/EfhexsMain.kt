package miyucomics.efhexs

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import miyucomics.efhexs.misc.ComplexParticleHandler
import miyucomics.efhexs.misc.PlayerEntityMinterface
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.fabricmc.fabric.api.event.registry.RegistryAttribute
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.nbt.NbtElement
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.SimpleRegistry
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import java.util.*

class EfhexsMain : ModInitializer {
	override fun onInitialize() {
		EfhexsActions.init()

		ServerPlayNetworking.registerGlobalReceiver(PARTICLE_CHANNEL) { _, player, _, buf, _ ->
			val new = buf.readIdentifier()
			val ring = (player as PlayerEntityMinterface).getParticles()
			if (!ring.buffer().contains(new))
				ring.add(new)
		}

		ServerPlayNetworking.registerGlobalReceiver(SOUND_CHANNEL) { _, player, _, buf, _ ->
			val new = buf.readIdentifier()
			val ring = (player as PlayerEntityMinterface).getSounds()
			if (!ring.buffer().contains(new))
				ring.add(new)
		}
	}

	companion object {
		fun id(string: String) = Identifier("efhexs", string)

		val PARTICLE_CHANNEL = id("particles")
		val SOUND_CHANNEL = id("sounds")
		val SPAWN_SIMPLE_PARTICLE_CHANNEL = id("spawn_simple_particle")
		val SPAWN_COMPLEX_PARTICLE_CHANNEL = id("spawn_complex_particle")

		private val COMPLEX_PARTICLE_REGISTRY_KEY: RegistryKey<Registry<ComplexParticleHandler>> = RegistryKey.ofRegistry(id("complex_particle_registry"))
		val COMPLEX_PARTICLE_REGISTRY: SimpleRegistry<ComplexParticleHandler> = FabricRegistryBuilder.createSimple(COMPLEX_PARTICLE_REGISTRY_KEY).attribute(RegistryAttribute.MODDED).buildAndRegister()

		fun getTargetsFromImage(world: ServerWorld, image: CastingImage, x: Double, y: Double, z: Double): List<ServerPlayerEntity> {
			if (!image.userData.contains("efhexs_targets"))
				return world.getPlayers()
			val targets = image.userData.getList("efhexs_targets", NbtElement.STRING_TYPE.toInt()).map { UUID.fromString(it.asString()) }
			return world.getPlayers { targets.contains(it.uuid) && it.squaredDistanceTo(x, y, z) < 4096 }
		}
	}
}