package net.antisea.meteorextra;

import net.antisea.meteorextra.modules.AutoRespond;
import net.antisea.meteorextra.modules.ChunkVisualizer;
import net.antisea.meteorextra.modules.CustomSplash;
import net.antisea.meteorextra.modules.BunnyHop;
import net.antisea.meteorextra.modules.MinecartBoost;
import net.antisea.meteorextra.modules.TNTCartAura;
import net.antisea.meteorextra.modules.LiquidElevator;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class MeteorExtra extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Meteor Extra");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Meteor Extra");
        
        Modules.get().add(new AutoRespond());
        Modules.get().add(new ChunkVisualizer());
        Modules.get().add(new CustomSplash());
        Modules.get().add(new BunnyHop());
        Modules.get().add(new MinecartBoost());
        Modules.get().add(new TNTCartAura());
        Modules.get().add(new LiquidElevator());
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