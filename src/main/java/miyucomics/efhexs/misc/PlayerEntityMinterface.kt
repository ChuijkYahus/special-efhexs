package miyucomics.efhexs.misc

import miyucomics.hexpose.utils.RingBuffer
import net.minecraft.util.Identifier

interface PlayerEntityMinterface {
	fun getParticles(): RingBuffer<Identifier>
	fun getSounds(): RingBuffer<Identifier>
}