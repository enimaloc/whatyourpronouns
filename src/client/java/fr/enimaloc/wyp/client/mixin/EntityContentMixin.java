package fr.enimaloc.wyp.client.mixin;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.entity.EntityType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.UUID;

@Mixin(HoverEvent.EntityTooltipInfo.class)
public class EntityContentMixin {
    @Shadow @Final public EntityType<?> type;
    @Shadow @Final public UUID id;
    @Shadow @Final @Mutable @Nullable public Component name;
    @Shadow private @Nullable List<Component> linesCache;

    @Inject(method = "getTooltipLines", at = @At("HEAD"))
    private void modifyTooltip(CallbackInfoReturnable<List<Component>> cir) {
        if (!type.equals(EntityType.PLAYER)) return;

    }
}
