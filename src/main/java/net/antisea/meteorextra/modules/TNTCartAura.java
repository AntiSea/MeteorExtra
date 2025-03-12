package net.antisea.meteorextra.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import net.antisea.meteorextra.MeteorExtra;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Direction;
import net.minecraft.block.Blocks;
import net.minecraft.block.Block;

import java.util.List;

public class TNTCartAura extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Block>> block = sgGeneral.add(new BlockListSetting.Builder()
        .name("whitelist")
        .description("Which block to use during setup.")
        .defaultValue(Blocks.DIRT)
        .build()
    );

    private final Setting<Double> radius = sgGeneral.add(new DoubleSetting.Builder()
        .name("radius")
        .description("The radius in which to place the minecarts.")
        .defaultValue(4.5)
        .min(0.1)
        .sliderMax(7.0)
        .build()
    );

    private final Setting<Double> delay = sgGeneral.add(new DoubleSetting.Builder()
        .name("delay")
        .description("The delay in seconds between placing minecarts.")
        .defaultValue(1.75)
        .min(0.1)
        .sliderMax(10.0)
        .build()
    );

    private final Setting<Boolean> placeProtectionBlock = sgGeneral.add(new BoolSetting.Builder()
        .name("place-protection-block")
        .description("Toggle to place a block in front of you for protection.")
        .defaultValue(false)
        .build()
    );

    private long lastPlaceTime;

    public TNTCartAura() {
        super(MeteorExtra.CATEGORY, "tntcart-aura", "Automatically places TNT minecarts around not friendly players. (Similar to auto-crystal)");
    }

    @Override
    public void onActivate() {
        info("TNTCart Aura activated!");
        lastPlaceTime = System.currentTimeMillis();
    }

    @Override
    public void onDeactivate() {
        info("TNTCart Aura deactivated!");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        List<AbstractClientPlayerEntity> players = mc.world.getPlayers();
        for (PlayerEntity player : players) {
            if (player != mc.player && !isFriendly(player)) {
                double distance = mc.player.distanceTo(player);
                if (distance <= radius.get()) {
                    if (hasRequiredItems()) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - lastPlaceTime >= delay.get() * 1000) {
                            placeSetup(player);
                            lastPlaceTime = currentTime;
                        }
                    } else {
                        printMissingItems();
                    }
                }
            }
        }
    }

    private boolean isFriendly(PlayerEntity player) {
        return false;
    }

    private Block getRandomBlock() {
        List<Block> blocks = block.get();
        if (blocks.isEmpty()) {
            return Blocks.DIRT;
        }
        int randomIndex = mc.world.random.nextInt(blocks.size());
        return blocks.get(randomIndex);
    }

    private boolean hasRequiredItems() {
        return mc.player.getInventory().main.stream().anyMatch(stack -> stack.getItem().equals(Items.RAIL)) &&
               mc.player.getInventory().main.stream().anyMatch(stack -> stack.getItem().equals(Items.TNT_MINECART)) &&
               mc.player.getInventory().main.stream().anyMatch(stack -> stack.getItem().equals(Items.FLINT_AND_STEEL));
    }

    private void printMissingItems() {
        if (!mc.player.getInventory().main.stream().anyMatch(stack -> stack.getItem().equals(Items.RAIL))) {
            info("Missing item: Rail");
        }
        if (!mc.player.getInventory().main.stream().anyMatch(stack -> stack.getItem().equals(Items.TNT_MINECART))) {
            info("Missing item: TNT Minecart");
        }
        if (!mc.player.getInventory().main.stream().anyMatch(stack -> stack.getItem().equals(Items.FLINT_AND_STEEL))) {
            info("Missing item: Flint and Steel");
        }
    }

    private void placeSetup(PlayerEntity player) {
        Block randomBlock = getRandomBlock();
        BlockPos playerPos = player.getBlockPos();
        BlockPos placePos = playerPos.west();

        if (placePos == null) {
            info("No suitable position found to place setup.");
            return;
        }

        mc.player.getInventory().selectedSlot = findItemSlot(randomBlock.asItem());
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(placePos), Direction.UP, placePos, false));

        BlockPos railPos1 = placePos.up();
        BlockPos railPos2 = railPos1.north().down();
        
        BlockPos blockUnderRailPos2 = railPos2.down();
        if (mc.world.getBlockState(blockUnderRailPos2).isAir()) {
            mc.player.getInventory().selectedSlot = findItemSlot(randomBlock.asItem());
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(blockUnderRailPos2), Direction.UP, blockUnderRailPos2, false));
        }

        mc.player.getInventory().selectedSlot = findItemSlot(Items.RAIL);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(railPos1), Direction.UP, railPos1, false));
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(railPos2), Direction.UP, railPos2, false));

        if (placeProtectionBlock.get()) {
            BlockPos protectionPos = mc.player.getBlockPos().offset(mc.player.getHorizontalFacing(), 1);
            if (mc.world.getBlockState(protectionPos).isAir()) {
            mc.player.getInventory().selectedSlot = findItemSlot(randomBlock.asItem());
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(protectionPos), Direction.UP, protectionPos, false));
            }
        }

        mc.player.getInventory().selectedSlot = findItemSlot(Items.TNT_MINECART);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(railPos2), Direction.UP, railPos2, false));

        BlockPos firePos = railPos2.north().down();
        if (mc.world.getBlockState(firePos).isAir()) {
            mc.player.getInventory().selectedSlot = findItemSlot(randomBlock.asItem());
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(firePos), Direction.UP, firePos, false));
        }
        mc.player.getInventory().selectedSlot = findItemSlot(Items.FLINT_AND_STEEL);
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(firePos), Direction.UP, firePos, false));
    }

    private int findItemSlot(Item item) {
        for (int i = 0; i < 9; i++) {
            if (mc.player.getInventory().getStack(i).getItem().equals(item)) {
                return i;
            }
        }
        return -1;
    }
}
