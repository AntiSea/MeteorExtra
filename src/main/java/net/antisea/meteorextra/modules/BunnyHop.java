package net.antisea.meteorextra.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import net.antisea.meteorextra.MeteorExtra;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.orbit.EventHandler;

public class BunnyHop extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> speed = sgGeneral.add(new DoubleSetting.Builder()
        .name("speed")
        .description("The speed multiplier while bunny hopping.")
        .defaultValue(1.2)
        .min(0.1)
        .sliderMin(0.1)
        .sliderMax(3.0)
        .build()
    );

    public BunnyHop() {
        super(MeteorExtra.CATEGORY, "bunny-hop", "Automatically jumps to maintain speed while moving.");
    }

    @Override
    public void onActivate() {
        info("Bunny Hop activated!");
    }

    @Override
    public void onDeactivate() {
        info("Bunny Hop deactivated!");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        ClientPlayerEntity player = mc.player;

        if (player == null || !player.isOnGround()) return;

        if (PlayerUtils.isMoving()) {
            player.jump();
            Vec3d velocity = player.getVelocity();
            player.setVelocity(velocity.x * speed.get(), velocity.y, velocity.z * speed.get());
        }
    }
}
