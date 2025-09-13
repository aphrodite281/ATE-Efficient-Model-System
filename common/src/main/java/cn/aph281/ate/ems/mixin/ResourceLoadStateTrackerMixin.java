package cn.aph281.ate.ems.mixin;

import cn.aph281.ate.ems.Main;
import cn.aph281.ate.ems.shader.ContextCapability;
import net.minecraft.client.ResourceLoadStateTracker;
import net.minecraft.client.ResourceLoadStateTracker.*;
import java.util.List;
import net.minecraft.server.packs.PackResources;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.ResourceLoadStateTracker.class)
public class ResourceLoadStateTrackerMixin {
    @Inject(method = "startReload", at = @At("TAIL"))
    private void onStartReload(ReloadReason reloadReason, List<PackResources> packs, CallbackInfo ci) {
        ContextCapability.checkContextVersion();
        String glVersionStr = "OpenGL " + ContextCapability.contextVersion / 10 + "."
                + ContextCapability.contextVersion % 10;
        Main.LOGGER.info("ATE-EMS detected " + glVersionStr + (ContextCapability.isGL4ES ? " (GL4ES)." : "."));
    }
}
