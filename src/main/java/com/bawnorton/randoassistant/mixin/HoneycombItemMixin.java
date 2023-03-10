package com.bawnorton.randoassistant.mixin;

import com.bawnorton.randoassistant.RandoAssistant;
import com.bawnorton.randoassistant.networking.Networking;
import com.bawnorton.randoassistant.networking.SerializeableInteraction;
import net.minecraft.block.BlockState;
import net.minecraft.item.HoneycombItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;

@Mixin(HoneycombItem.class)
public abstract class HoneycombItemMixin {
    @Shadow
    public static Optional<BlockState> getWaxedState(BlockState state) {
        throw new AssertionError();
    }

    @Inject(method = "useOnBlock", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onUseOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir, World world, BlockPos blockPos, BlockState blockState) {
        getWaxedState(blockState).ifPresent(waxedState -> {
            if(context.getPlayer() instanceof ServerPlayerEntity serverPlayer) {
                Networking.sendInteractionPacket(serverPlayer, SerializeableInteraction.ofBlockToBlock(blockState.getBlock(), waxedState.getBlock()));
            }
        });
    }
}
