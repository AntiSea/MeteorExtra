package net.antisea.meteorextra.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.antisea.meteorextra.MeteorExtra;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class MinecartBoost extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<Double> boostStrength = sgGeneral.add(new DoubleSetting.Builder()
        .name("boost-strength")
        .description("The strength of the boost applied to the minecart.")
        .defaultValue(2.0)
        .min(0.1)
        .sliderMax(10.0)
        .build()
    );

    public MinecartBoost() {
        super(MeteorExtra.CATEGORY, "minecart-boost", "Boosts minecarts by simulating a strong push.");
    }

    @Override
    public void onActivate() {
        info("Minecart Boost activated!");
    }

    @Override
    public void onDeactivate() {
        info("Minecart Boost deactivated!");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        ClientPlayerEntity player = mc.player;
        if (player == null || !PlayerUtils.isMoving()) return;

        double strength = boostStrength.get();
        double yaw = Math.toRadians(player.getYaw());
        Vec3d pushVector = new Vec3d(-Math.sin(yaw) * strength, 0, Math.cos(yaw) * strength);

        for (Entity entity : mc.world.getEntitiesByClass(MinecartEntity.class, player.getBoundingBox().expand(2.0), e -> true)) {
            if (entity instanceof MinecartEntity && player.squaredDistanceTo(entity) < 2.0) {
                Vec3d velocity = entity.getVelocity().add(pushVector);
                entity.setVelocity(velocity);
            }
        }
    }
}
