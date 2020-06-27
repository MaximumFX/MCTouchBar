package nl.maximumfx.mctouchbar;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.command.arguments.BlockArgumentParser;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.Locale;

public class Helper {

	private static MainClass mc;
	private static MinecraftClient mcc;

	public static void init(MainClass mainClass) {
		mc = mainClass;
		mcc = MinecraftClient.getInstance();
	}

	public static void copyLookAt(final boolean boolean1, final boolean boolean2) {
		final HitResult hitResult3 = mcc.crosshairTarget;
		if (hitResult3 == null) {
			return;
		}
		switch (hitResult3.getType()) {
			case BLOCK: {
				final BlockPos blockPos4 = ((BlockHitResult)hitResult3).getBlockPos();
				final BlockState blockState5 = mcc.player.world.getBlockState(blockPos4);
				if (!boolean1) {
					copyBlock(blockState5, blockPos4, null);
					mc.debugWarn("debug.inspect.client.block");
					break;
				}
				if (boolean2) {
					mcc.player.networkHandler.getDataQueryHandler().queryBlockNbt(blockPos4, compoundTag -> {
						copyBlock(blockState5, blockPos4, compoundTag);
						mc.debugWarn("debug.inspect.server.block");
					});
					break;
				}
				final BlockEntity blockEntity6 = mcc.player.world.getBlockEntity(blockPos4);
				final CompoundTag compoundTag2 = (blockEntity6 != null) ? blockEntity6.toTag(new CompoundTag()) : null;
				copyBlock(blockState5, blockPos4, compoundTag2);
				mc.debugWarn("debug.inspect.client.block");
				break;
			}
			case ENTITY: {
				final Entity entity4 = ((EntityHitResult)hitResult3).getEntity();
				final Identifier identifier5 = Registry.ENTITY_TYPE.getId(entity4.getType());
				final Vec3d vec3d6 = new Vec3d(entity4.getX(), entity4.getY(), entity4.getZ());
				if (!boolean1) {
					copyEntity(identifier5, vec3d6, null);
					mc.debugWarn("debug.inspect.client.entity");
					break;
				}
				if (boolean2) {
					mcc.player.networkHandler.getDataQueryHandler().queryEntityNbt(entity4.getEntityId(), compoundTag -> {
						copyEntity(identifier5, vec3d6, compoundTag);
						mc.debugWarn("debug.inspect.server.entity");
					});
					break;
				}
				final CompoundTag compoundTag2 = entity4.toTag(new CompoundTag());
				copyEntity(identifier5, vec3d6, compoundTag2);
				mc.debugWarn("debug.inspect.client.entity");
				break;
			}
		}
	}

	public static void copyBlock(final BlockState blockState, final BlockPos blockPos, final CompoundTag compoundTag) {
		if (compoundTag != null) {
			compoundTag.remove("x");
			compoundTag.remove("y");
			compoundTag.remove("z");
			compoundTag.remove("id");
		}
		final StringBuilder stringBuilder4 = new StringBuilder(BlockArgumentParser.stringifyBlockState(blockState));
		if (compoundTag != null) {
			stringBuilder4.append(compoundTag);
		}
		final String string5 = String.format(Locale.ROOT, "/setblock %d %d %d %s", blockPos.getX(), blockPos.getY(), blockPos.getZ(), stringBuilder4);
		mcc.keyboard.setClipboard(string5);
	}

	public static void copyEntity(final Identifier identifier, final Vec3d vec3d, final CompoundTag compoundTag) {
		String string6;
		if (compoundTag != null) {
			compoundTag.remove("UUIDMost");
			compoundTag.remove("UUIDLeast");
			compoundTag.remove("Pos");
			compoundTag.remove("Dimension");
			final String string5 = compoundTag.toText().getString();
			string6 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f %s", identifier.toString(), vec3d.x, vec3d.y, vec3d.z, string5);
		}
		else {
			string6 = String.format(Locale.ROOT, "/summon %s %.2f %.2f %.2f", identifier.toString(), vec3d.x, vec3d.y, vec3d.z);
		}
		mcc.keyboard.setClipboard(string6);
	}
}
