package net.antisea.meteorextra.modules;

import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.BlockListSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import net.antisea.meteorextra.MeteorExtra;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import meteordevelopment.meteorclient.utils.render.RenderUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;

import java.util.List;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AutoBlockChest extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final SettingGroup sgRender = settings.createGroup("Render");

    private final Setting<List<Block>> block = sgGeneral.add(new BlockListSetting.Builder()
        .name("whitelist")
        .description("Which block to use during placement.")
        .defaultValue(Blocks.DIRT)
        .build()
    );

    private final Setting<Double> radius = sgGeneral.add(new DoubleSetting.Builder()
        .name("radius")
        .description("The radius in which to start preventing chests.")
        .defaultValue(4.5)
        .min(0.1)
        .sliderMax(7.0)
        .build()
    );

    private final Setting<Boolean> enableRenderBreaking = sgRender.add(new BoolSetting.Builder()
            .name("broken-blocks")
            .description("Enable rendering bounding box for Cube and Uniform Cube.")
            .defaultValue(true)
            .build()
    );

    private final Setting<ShapeMode> shapeModeBreak = sgRender.add(new EnumSetting.Builder<ShapeMode>()
        .name("break-block-mode")
        .description("How the shapes for broken blocks are rendered.")
        .defaultValue(ShapeMode.Both)
        .visible(enableRenderBreaking::get)
        .build()
    );

    private final Setting<SettingColor> sideColor = sgRender.add(new ColorSetting.Builder()
            .name("side-color")
            .description("The side color of the target block rendering.")
            .defaultValue(new SettingColor(135, 0, 255, 80))
            .visible(enableRenderBreaking::get)
            .build()
    );

    private final Setting<SettingColor> lineColor = sgRender.add(new ColorSetting.Builder()
            .name("line-color")
            .description("The line color of the target block rendering.")
            .defaultValue(new SettingColor(135, 0, 255, 255))
            .visible(enableRenderBreaking::get)
            .build()
    );

    public AutoBlockChest() {
        super(MeteorExtra.CATEGORY, "auto-block-chest", "Automatically blocks tops of chests to prevent entry into them.");
    }

    @Override
    public void onActivate() {
        info("Auto Block Chest activated!");
    }

    @Override
    public void onDeactivate() {
        info("Auto Block Chest deactivated!");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        Block randomBlock = getRandomBlock();
        int radiusInt = radius.get().intValue();
        BlockPos playerPos = mc.player.getBlockPos();

        for (BlockPos pos : BlockPos.iterateOutwards(playerPos, radiusInt, radiusInt, radiusInt)) {
            if (mc.world.getBlockState(pos).getBlock() == Blocks.CHEST) {
                BlockPos abovePos = pos.up();
                if (mc.world.isAir(abovePos)) {
                    mc.player.getInventory().selectedSlot = findItemSlot(randomBlock.asItem());
                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(Vec3d.ofCenter(abovePos), Direction.UP, abovePos, false));
                    RenderUtils.renderTickingBlock(abovePos, sideColor.get(), lineColor.get(), shapeModeBreak.get(), 0, 8, true, false);
                }
            }
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

    private Block getRandomBlock() {
        List<Block> blocks = block.get();
        if (blocks.isEmpty()) {
            return Blocks.DIRT;
        }
        int randomIndex = mc.world.random.nextInt(blocks.size());
        return blocks.get(randomIndex);
    }
}
