package net.antisea.meteorextra;

import net.antisea.meteorextra.commands.CommandExample;
import net.antisea.meteorextra.hud.HudExample;
import net.antisea.meteorextra.modules.ModuleExample;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class AddonTemplate extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Meteor Extra");
    public static final HudGroup HUD_GROUP = new HudGroup("Meteor Extra");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Addon Template");

        Modules.get().add(new ModuleExample());
        Commands.add(new CommandExample());
        Hud.get().register(HudExample.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "net.antisea.meteorextra";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("AntiSea", "meteor-extra");
    }
}