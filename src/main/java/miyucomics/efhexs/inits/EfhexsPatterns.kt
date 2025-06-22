package miyucomics.efhexs.inits

import at.petrak.hexcasting.api.casting.ActionRegistryEntry
import at.petrak.hexcasting.api.casting.castables.Action
import at.petrak.hexcasting.api.casting.getDouble
import at.petrak.hexcasting.api.casting.getPositiveDoubleUnderInclusive
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.math.HexDir
import at.petrak.hexcasting.api.casting.math.HexPattern
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.common.lib.hex.HexActions
import at.petrak.hexcasting.common.particles.ConjureParticleOptions
import miyucomics.efhexs.EfhexsMain.Companion.id
import miyucomics.efhexs.actions.*
import miyucomics.efhexs.misc.ComplexParticleHandler
import miyucomics.hexposition.iotas.getIdentifier
import miyucomics.hexposition.iotas.getItemStack
import net.minecraft.network.PacketByteBuf
import net.minecraft.particle.*
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.util.Identifier
import net.minecraft.util.math.ColorHelper

object EfhexsPatterns {
	fun init() {
		register("get_sounds", "aawa", HexDir.WEST, OpGetSounds())
		register("play_sound", "dwdd", HexDir.WEST, OpPlaySound())

		register("get_particles", "eqqqqqaq", HexDir.NORTH_EAST, OpGetParticles())
		register("play_particle", "eqqqqqaaw", HexDir.NORTH_EAST, OpPlaySimpleParticle())

		register("play_dust_particle", "eqqqqqaaq", HexDir.NORTH_EAST, OpPlayComplexParticle(Identifier("dust"), 3,
			{ buf, args ->
				val color = args.getVec3(2, 3)
				buf.writeVector3f(color.toVector3f())
			},
			object : ComplexParticleHandler {
				override fun produceParticleEffect(buf: PacketByteBuf) =
					DustParticleEffect(buf.readVector3f(), buf.readFloat())
			}
		))

		register("play_dust_transition_particle", "eqqqqqaaqda", HexDir.NORTH_EAST, OpPlayComplexParticle(Identifier("dust_color_transition"), 5,
			{ buf, args ->
				val colorA = args.getVec3(2, 5)
				val colorB = args.getVec3(3, 5)
				val duration = args.getDouble(4, 5).toFloat()

				buf.writeVector3f(colorA.toVector3f())
				buf.writeVector3f(colorB.toVector3f())
				buf.writeFloat(duration)
			},
			object : ComplexParticleHandler {
				override fun produceParticleEffect(buf: PacketByteBuf) =
					DustColorTransitionParticleEffect(buf.readVector3f(), buf.readVector3f(), buf.readFloat())
			}
		))

		register("play_block_particle", "eqqqqqaawqqqae", HexDir.NORTH_EAST, OpPlayComplexParticle(Identifier("block"), 3,
			{ buf, args ->
				val id = args.getIdentifier(2, 3)
				if (!Registries.BLOCK.containsId(id))
					throw MishapInvalidIota.of(args[2], 0, "block_id")
				buf.writeIdentifier(id)
			},
			object : ComplexParticleHandler {
				override fun produceParticleEffect(buf: PacketByteBuf) = BlockStateParticleEffect(
					ParticleTypes.BLOCK,
					Registries.BLOCK.get(buf.readIdentifier()).defaultState
				)
			}
		))

		register("play_falling_dust_particle", "eqqqqqaaqw", HexDir.NORTH_EAST, OpPlayComplexParticle(Identifier("falling_dust"), 3,
			{ buf, args ->
				val id = args.getIdentifier(2, 3)
				if (!Registries.BLOCK.containsId(id))
					throw MishapInvalidIota.of(args[2], 0, "block_id")
				buf.writeIdentifier(id)
			},
			object : ComplexParticleHandler {
				override fun produceParticleEffect(buf: PacketByteBuf) = BlockStateParticleEffect(
					ParticleTypes.FALLING_DUST,
					Registries.BLOCK.get(buf.readIdentifier()).defaultState
				)
			}
		))

		register("play_item_particle", "eqqqqqaaeaq", HexDir.NORTH_EAST, OpPlayComplexParticle(Identifier("item"), 3,
			{ buf, args ->
				val item = args.getItemStack(2, 3)
				buf.writeItemStack(item)
			},
			object : ComplexParticleHandler {
				override fun produceParticleEffect(buf: PacketByteBuf) =
					ItemStackParticleEffect(ParticleTypes.ITEM, buf.readItemStack())
			}
		))

		register("play_hex_particle", "eqqqqqaewawqwaw", HexDir.NORTH_EAST, OpPlayComplexParticle(Identifier("hex"), 4,
			{ buf, args ->
				val color = args.getVec3(2, 4)
				val alpha = (args.getPositiveDoubleUnderInclusive(3, 1.0, 4) * 255).toInt()
				buf.writeVector3f(color.toVector3f())
				buf.writeInt(alpha)
			},
			object : ComplexParticleHandler {
				override fun produceParticleEffect(buf: PacketByteBuf): ParticleEffect {
					val raw = buf.readVector3f()
					val color = ColorHelper.Argb.getArgb(buf.readInt(), (raw.x * 255).toInt(), (raw.y * 255).toInt(), (raw.z * 255).toInt())
					return ConjureParticleOptions(color)
				}
			}
		))
	}

	private fun register(name: String, signature: String, startDir: HexDir, action: Action) =
		Registry.register(
			HexActions.REGISTRY, id(name),
			ActionRegistryEntry(HexPattern.Companion.fromAngles(signature, startDir), action)
		)
}