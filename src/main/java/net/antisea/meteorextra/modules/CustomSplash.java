package net.antisea.meteorextra.modules;

import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.antisea.meteorextra.MeteorExtra;

import java.lang.reflect.Field;

public class CustomSplash extends Module {
    private static CustomSplash instance;

    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<String> splash = sgGeneral.add(new StringSetting.Builder()
        .name("splash")
        .description("Custom splash message for the menu screen. (METEOR MAY OVERLAP SOME SPLASHES WITH THERE OWN)")
        .defaultValue("Meteor Extra on top!")
        .build()
    );

    public CustomSplash() {
        super(MeteorExtra.CATEGORY, "custom-splash", "Provides a custom splash message for the menu screen.");
        instance = this;
    }

    public static CustomSplash getInstance() {
        return instance;
    }

    public String getSplashText() {
        return splash.get();
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (MinecraftClient.getInstance().currentScreen instanceof TitleScreen) {
            TitleScreen screen = (TitleScreen) MinecraftClient.getInstance().currentScreen;
            try {
                Field splashTextField = TitleScreen.class.getDeclaredField("splashText");
                splashTextField.setAccessible(true);
                splashTextField.set(screen, splash.get());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}