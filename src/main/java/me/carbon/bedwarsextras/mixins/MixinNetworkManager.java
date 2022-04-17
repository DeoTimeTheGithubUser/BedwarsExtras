package me.carbon.bedwarsextras.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.carbon.bedwarsextras.itemsteal.ItemSteal;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    // TODO Put item into inventory instead of dropping
    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void onChannelRead(ChannelHandlerContext context, Packet<?> packet, CallbackInfo info) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        if (ItemSteal.INSTANCE.getToggled()) {
            if (packet instanceof S2FPacketSetSlot) {
                S2FPacketSetSlot setSlotPacket = (S2FPacketSetSlot) packet;
                ItemStack item = setSlotPacket.func_149174_e();
                if (item == null) return;
                if (item.getDisplayName().equalsIgnoreCase("-")) {
                    info.cancel();
                    new Thread(() -> {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        C0DPacketCloseWindow closePacket = new C0DPacketCloseWindow(Minecraft.getMinecraft().thePlayer.inventoryContainer.windowId);
                        Minecraft.getMinecraft().getNetHandler().addToSendQueue(closePacket);
                        Minecraft.getMinecraft().thePlayer.inventory.setItemStack(null);
                        ItemSteal.INSTANCE.finish();
                    }).start();
                }
            }
        }
    }
}