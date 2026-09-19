package fr.enimaloc.wyp.platforms.neoforge.compat.enhancedplayerlist;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Gates every mixin in {@code whatyourpronouns-epl.mixins.json} on Enhanced Player List actually
 * being installed. Checked via classpath presence rather than a mod-loaded registry lookup,
 * because Mixin config plugins run very early — before NeoForge's mod list is guaranteed ready.
 */
public class EnhancedPlayerListMixinPlugin implements IMixinConfigPlugin {
    private static final String EPL_MARKER_CLASS = "com.enhancedplayerlist.client.event.ClientEventHandler";

    private boolean enhancedPlayerListPresent;

    @Override
    public void onLoad(String mixinPackage) {
        try {
            Class.forName(EPL_MARKER_CLASS, false, getClass().getClassLoader());
            enhancedPlayerListPresent = true;
        } catch (ClassNotFoundException e) {
            enhancedPlayerListPresent = false;
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return enhancedPlayerListPresent;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
