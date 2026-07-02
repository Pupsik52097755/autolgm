package com.example.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();
        if (player != null && player.getVelocity().y < -0.6 && !player.isAbilitiesFly() && !player.isInWater()) {
            if (player.getPitch() > 75.0F) {
                BlockHitResult hitResult = player.getWorld().raycast(new RaycastContext(
                        player.getEyePos(),
                        player.getEyePos().add(0, -3.5, 0),
                        RaycastContext.ShapeType.COLLIDER,
                        RaycastContext.FluidHandling.NONE,
                        player
                ));
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    int waterBucketSlot = -1;
                    for (int i = 0; i < 9; i++) {
                        ItemStack stack = player.getInventory().getStack(i);
                        if (stack.isOf(Items.WATER_BUCKET)) {
                            waterBucketSlot = i;
                            break;
                        }
                    }
                    if (waterBucketSlot != -1) {
                        if (player.getInventory().selectedSlot != waterBucketSlot) {
                            player.getInventory().selectedSlot = waterBucketSlot;
                        }
                        if (client.interactionManager != null) {
                            client.interactionManager.interactItem(player, Hand.MAIN_HAND);
                        }
                    }
                }
            }
        }
    }
}
