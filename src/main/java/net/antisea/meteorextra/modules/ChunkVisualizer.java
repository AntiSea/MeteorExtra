package net.antisea.meteorextra.modules;

import net.antisea.meteorextra.MeteorExtra;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.BoolSetting;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.client.MinecraftClient;
import net.minecraft.world.chunk.ChunkManager;
import net.minecraft.world.chunk.WorldChunk;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Box;
import meteordevelopment.meteorclient.renderer.ShapeMode;

public class ChunkVisualizer extends Module {
    private final SettingGroup sgGeneral = this.settings.getDefaultGroup();

    private final Setting<Boolean> highlightLoadedChunks = sgGeneral.add(new BoolSetting.Builder()
        .name("highlight-loaded-chunks")
        .description("Highlights loaded chunks.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> highlightUnloadedChunks = sgGeneral.add(new BoolSetting.Builder()
        .name("highlight-unloaded-chunks")
        .description("Highlights unloaded chunks.")
        .defaultValue(true)
        .build()
    );

    private final Setting<SettingColor> loadedChunkColor = sgGeneral.add(new ColorSetting.Builder()
        .name("loaded-chunk-color")
        .description("Color for loaded chunks.")
        .defaultValue(new SettingColor(0, 255, 0, 75))
        .build()
    );

    private final Setting<SettingColor> unloadedChunkColor = sgGeneral.add(new ColorSetting.Builder()
        .name("unloaded-chunk-color")
        .description("Color for unloaded chunks.")
        .defaultValue(new SettingColor(255, 0, 0, 75))
        .build()
    );

    public ChunkVisualizer() {
        super(MeteorExtra.CATEGORY, "chunk-visualizer", "Highlights loaded/unloaded chunks and borders.");
    }

    @Override
    public void onActivate() {
        MinecraftClient client = MinecraftClient.getInstance();
        int viewDistance = client.options.getViewDistance().getValue();
        info("Chunk Visualizer activated! Render distance: " + viewDistance);
    }

    @Override
    public void onDeactivate() {
        info("Chunk Visualizer deactivate!");
    }

    @EventHandler
    private void onRender3d(Render3DEvent event) {
        MinecraftClient client = MinecraftClient.getInstance();
        ChunkManager chunkManager = client.world.getChunkManager();

        int viewDistance = client.options.getViewDistance().getValue();
        for (int x = -viewDistance; x <= viewDistance; x++) {
            for (int z = -client.options.getViewDistance().getValue(); z <= client.options.getViewDistance().getValue(); z++) {
                ChunkPos chunkPos = new ChunkPos(client.player.getChunkPos().x + x, client.player.getChunkPos().z + z);
                WorldChunk chunk = chunkManager.getWorldChunk(chunkPos.x, chunkPos.z, false);

                if (chunk != null && highlightLoadedChunks.get()) {
                    renderChunk(event, chunkPos, loadedChunkColor.get());
                } else if (chunk == null && highlightUnloadedChunks.get()) {
                    renderChunk(event, chunkPos, unloadedChunkColor.get());
                }
            }
        }
    }

    private void renderChunk(Render3DEvent event, ChunkPos chunkPos, SettingColor color) {
        Box marker = new Box(new BlockPos(chunkPos.getStartX(), 0, chunkPos.getStartZ()));
        marker = marker.stretch(
            marker.getLengthX(),
            255,
            marker.getLengthZ()
        );

        event.renderer.box(marker, color, color, ShapeMode.Both, 0);
    }
}