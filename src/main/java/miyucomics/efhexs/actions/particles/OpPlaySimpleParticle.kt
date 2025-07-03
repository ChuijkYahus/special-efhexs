package miyucomics.efhexs.actions.particles

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import miyucomics.efhexs.EfhexsMain
import miyucomics.efhexs.EfhexsMain.Companion.getTargetsFromImage
import miyucomics.hexpose.iotas.getIdentifier
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.particle.DefaultParticleType
import net.minecraft.registry.Registries
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d

class OpPlaySimpleParticle : SpellAction {
	override val argc = 3
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		val id = args.getIdentifier(0, argc)
		if (!Registries.PARTICLE_TYPE.containsId(id))
			throw MishapInvalidIota.of(args[0], 2, "particle_id")
		if (Registries.PARTICLE_TYPE.get(id) !is DefaultParticleType)
			throw MishapInvalidIota.of(args[0], 2, "simple_particle_id")
		val pos = args.getVec3(1, argc)
		env.assertVecInRange(pos)
		val velocity = args.getVec3(2, argc)
		return SpellAction.Result(Spell(id, pos, velocity), MediaConstants.DUST_UNIT / 32, listOf())
	}

	private data class Spell(val particle: Identifier, val pos: Vec3d, val velocity: Vec3d) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {}
		override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage {
			val packet = PacketByteBufs.create()
			packet.writeIdentifier(particle)
			packet.writeDouble(pos.x)
			packet.writeDouble(pos.y)
			packet.writeDouble(pos.z)
			packet.writeDouble(velocity.x)
			packet.writeDouble(velocity.y)
			packet.writeDouble(velocity.z)
			getTargetsFromImage(env.world, image, pos.x, pos.y, pos.z).forEach {
				ServerPlayNetworking.send(it, EfhexsMain.SPAWN_SIMPLE_PARTICLE_CHANNEL, packet)
			}
			return image
		}
	}
}