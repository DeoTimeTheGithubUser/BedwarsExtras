package me.carbon.bedwarsextras.mixins;

import me.carbon.bedwarsextras.itemsteal.ItemSteal;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public class MixinItemStack {

    @Inject(method = "getDisplayName", at = @At("RETURN"), cancellable = true)
    private void getDisplayName(CallbackInfoReturnable<String> cir) {
        ItemStack stack = ((ItemStack) (Object) this);
        if (cir.getReturnValue().contains("Quick Buy")) return;
        String mappedName = ItemSteal.INSTANCE.getMappedName(cir.getReturnValue());
        if (mappedName == null) return;
        if (ItemSteal.INSTANCE.isBwGui(stack)) return;
        cir.setReturnValue(mappedName);
    }

}
