package net.antisea.meteorextra.modules;

import meteordevelopment.meteorclient.settings.StringSetting;
import net.minecraft.util.Identifier;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.network.PacketByteBuf;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;
import net.antisea.meteorextra.MeteorExtra;
import java.lang.reflect.Field;
import io.netty.buffer.Unpooled;

public class ServerAccess extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<String> command  = sgGeneral.add(new StringSetting.Builder()
        .name("command")
        .description("The server command to execute.")
        .defaultValue("say Meteor Extra on top!")
        .build()
    );

    public ServerAccess() {
        super(MeteorExtra.CATEGORY, "server-access", "Sends a command to the server to be executed.");
    }

    @Override
    public void onActivate() {
        sendCommand(command.get());
    }

    private void sendCommand(String cmd) {
        PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeString(cmd);
        ClientPlayNetworking.send(new Identifier("meteor-extra", "command_channel"), buf);
        info("Sent command to server: " + cmd);
    }
}