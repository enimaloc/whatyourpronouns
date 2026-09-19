package fr.enimaloc.wyp.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(HoverEvent.EntityTooltipInfo.class)
public class EntityContentMixin {
    @Shadow @Final public EntityType<?> type;

    @Inject(method = "getTooltipLines", at = @At("HEAD"))
    private void modifyTooltip(CallbackInfoReturnable<List<Component>> cir) {
        if (!type.equals(EntityType.PLAYER)) return;

    }
}
