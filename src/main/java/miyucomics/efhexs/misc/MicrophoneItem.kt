package miyucomics.efhexs.misc

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Hand
import net.minecraft.util.TypedActionResult
import net.minecraft.world.World

class MicrophoneItem : Item(Settings().maxCount(1)) {
	override fun use(world: World, user: PlayerEntity, hand: Hand): TypedActionResult<ItemStack> {
		if (world.isClient) {
			if (ClientStorage.recording) {
				ClientStorage.recording = false
				ClientStorage.pushParticlesToServer()
				ClientStorage.pushSoundsToServer()
				user.sendMessage(Text.translatable("efhexs.microphone.inactive"), true)
			} else {
				ClientStorage.recording = true
				user.sendMessage(Text.translatable("efhexs.microphone.active"), true)
			}
		}
		return TypedActionResult.success(user.getStackInHand(hand))
	}
}