package miyucomics.efhexs.mixin;

import miyucomics.efhexs.misc.PlayerEntityMinterface;
import miyucomics.hexpose.utils.RingBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin implements PlayerEntityMinterface {
	@Unique private RingBuffer<Identifier> particles = new RingBuffer<>(32);
	@Unique private RingBuffer<Identifier> sounds = new RingBuffer<>(32);

	@Override
	public RingBuffer<Identifier> getParticles() {
		return particles;
	}

	@Override
	public RingBuffer<Identifier> getSounds() {
		return sounds;
	}
}