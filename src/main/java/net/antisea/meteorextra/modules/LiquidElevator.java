package net.antisea.meteorextra.modules;

import java.util.List;

import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.antisea.meteorextra.MeteorExtra;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class LiquidElevator extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    private final Setting<List<Block>> block = sgGeneral.add(new BlockListSetting.Builder()
        .name("whitelist")
        .description("Which block to use when replaceing.")
        .defaultValue(Blocks.DIRT)
        .build()
    );

    public LiquidElevator() {
        super(MeteorExtra.CATEGORY, "liquid-elevator", "Helps you to move down through large amounts of liquids. (Lava/Water)");
    }

    @Override
    public void onActivate() {
        info("Liquid Elevator activated!");
    }

    @Override
    public void onDeactivate() {
        info("Liquid Elevator deactivated!");
    }

    private Block getRandomBlock() {
        List<Block> blocks = block.get();
        if (blocks.isEmpty()) {
            return Blocks.DIRT;
        }
        int randomIndex = mc.world.random.nextInt(blocks.size());
        return blocks.get(randomIndex);
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.player == null || mc.world == null) return;

        BlockPos pos = mc.player.getBlockPos().down().down();
        World world = mc.world;

        if (!world.getBlockState(pos).getFluidState().isEmpty()) {
            info("Found liquid at %s", pos);
            boolean isWater = world.getBlockState(pos).getFluidState().isStill() && world.getBlockState(pos).getFluidState().getFluid() == net.minecraft.fluid.Fluids.WATER;
            boolean isLava = world.getBlockState(pos).getFluidState().isStill() && world.getBlockState(pos).getFluidState().getFluid() == net.minecraft.fluid.Fluids.LAVA;

            Block randomBlock = getRandomBlock();
            mc.player.getInventory().selectedSlot = findItemSlot(randomBlock.asItem());
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
            if (mc.player.isCreative() || mc.player.canHarvest(world.getBlockState(pos))) {
                mc.interactionManager.breakBlock(pos);
            } else {
                info("Cannot break block at %s", pos);
            }

            if (isLava) {
                mc.player.getInventory().selectedSlot = findItemSlot(Items.WATER_BUCKET);
            } else if (isWater) {
                mc.player.getInventory().selectedSlot = findItemSlot(Items.LAVA_BUCKET);
            }
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(pos), Direction.UP, pos, false));
        }
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