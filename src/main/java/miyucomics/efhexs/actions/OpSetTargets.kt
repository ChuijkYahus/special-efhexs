package miyucomics.efhexs.actions

import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.EntityIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.NullIota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.utils.getOrCreateList
import net.minecraft.nbt.NbtElement
import net.minecraft.nbt.NbtString
import java.util.*

class OpSetTargets : SpellAction {
	override val argc = 1
	override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
		if (args[0] is NullIota)
			return SpellAction.Result(Clear(0), 0, listOf())

		val list = args.getList(0, argc)
		val uuids = mutableListOf<UUID>()
		list.forEach {
			if (it !is EntityIota)
				throw MishapInvalidIota.of(args[0], 0, "entity_list")
			uuids.add(it.entity.uuid)
		}
		return SpellAction.Result(Set(uuids), 0, listOf())
	}

	private data class Clear(val random: Int) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {}
		override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
			val newImage = image.copy()
			newImage.userData.remove("efhexs_targets")
			return newImage
		}
	}

	private data class Set(val targets: List<UUID>) : RenderedSpell {
		override fun cast(env: CastingEnvironment) {}
		override fun cast(env: CastingEnvironment, image: CastingImage): CastingImage? {
			val newImage = image.copy()
			val list = newImage.userData.getOrCreateList("efhexs_targets", NbtElement.STRING_TYPE.toInt())
			targets.forEach { list.add(NbtString.of(it.toString())) }
			return newImage
		}
	}
}