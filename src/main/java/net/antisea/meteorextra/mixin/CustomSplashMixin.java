package net.antisea.meteorextra.mixin;

import meteordevelopment.meteorclient.systems.config.Config;
import net.antisea.meteorextra.modules.CustomSplash;
import net.minecraft.client.gui.screen.SplashTextRenderer;
import net.minecraft.client.resource.SplashTextResourceSupplier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(SplashTextResourceSupplier.class)
public abstract class CustomSplashMixin {
    @Unique
    private boolean override = true;
    @Unique
    private static final Random RANDOM = new Random();

    @Inject(method = "get", at = @At("HEAD"), cancellable = true)
    private void onApply(CallbackInfoReturnable<SplashTextRenderer> cir) {
        if (Config.get() == null || !Config.get().titleScreenSplashes.get()) return;

        CustomSplash customSplash = CustomSplash.getInstance();
        if (customSplash != null && customSplash.isActive() && override) {
            String splashText = customSplash.getSplashText();
            cir.setReturnValue(new SplashTextRenderer(splashText));
            override = false;
        } else {
            override = true;
        }
    }
}