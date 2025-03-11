package net.antisea.meteorextra.modules;

import net.antisea.meteorextra.MeteorExtra;
import meteordevelopment.meteorclient.settings.StringSetting;
import net.minecraft.client.MinecraftClient;
import meteordevelopment.meteorclient.events.game.ReceiveMessageEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

public class AutoRespond extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<String> respondTo = sgGeneral.add(new StringSetting.Builder()
        .name("respond-to")
        .description("The text to respond to.")
        .defaultValue("hello")
        .build()
    );

    private final Setting<String> response = sgGeneral.add(new StringSetting.Builder()
        .name("response")
        .description("The text to respond with.")
        .defaultValue("hi there!")
        .build()
    );

    @Override
    public void onActivate() {
        info("AutoRespond activated! Responding with: " + response.get());
    }

    @Override
    public void onDeactivate() {
        info("AutoRespond deactivated!");
    }

    public AutoRespond() {
        super(MeteorExtra.CATEGORY, "auto-respond", "Automatically respond to specified messages.");
    }

    @EventHandler
    private void onChatReceived(ReceiveMessageEvent event) {
        String message = event.getMessage().getString();
        if (message.contains(respondTo.get())) {
            if (response.get().startsWith("/")) {
                MinecraftClient.getInstance().player.networkHandler.sendCommand(response.get().substring(1));
            } else {
                MinecraftClient.getInstance().player.networkHandler.sendChatMessage(response.get());
            }
        }
    }

}