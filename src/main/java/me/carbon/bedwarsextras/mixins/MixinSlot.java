package me.carbon.bedwarsextras.mixins;

import me.carbon.bedwarsextras.itemsteal.ItemSteal;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class MixinSlot {

    @Shadow
    @Final
    public IInventory inventory;

    @Shadow
    public abstract int getSlotIndex();

    @Inject(method = "getStack", at = @At("RETURN"), cancellable = true)
    private void getStack(CallbackInfoReturnable<ItemStack> cir) {
        ItemStack item = cir.getReturnValue();
        if (item == null) return;
        NBTTagCompound nbt = item.getTagCompound();
        if (nbt == null) return;
        String displayName = nbt.getCompoundTag("display").getString("Name");
        if (ItemSteal.INSTANCE.getMappedName(displayName) == null) return;
        if (ItemSteal.INSTANCE.checkQuickBuy(inventory)
                && (getSlotIndex() > 0 && getSlotIndex() < 9)) return;
        if (inventory.getDisplayName().getFormattedText().contains("Hotbar Manager")
                && (getSlotIndex() > 17 && getSlotIndex() < 45)) return;
        item.setTagCompound(ItemSteal.INSTANCE.formatNbt(nbt));
        cir.setReturnValue(item);
    }

}
