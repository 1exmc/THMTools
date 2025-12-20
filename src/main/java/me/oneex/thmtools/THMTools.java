package me.oneex.thmtools;

import me.oneex.thmtools.commands.Center;
//import com.example.addon.hud.HudExample;
import me.oneex.thmtools.modules.hud.DailyStatsHud;
import me.oneex.thmtools.modules.main.AxisViewer;
import me.oneex.thmtools.modules.main.THMHighway;
import com.mojang.logging.LogUtils;
import me.oneex.thmtools.system.THMSystem;
import me.oneex.thmtools.system.THMTab;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.gui.tabs.Tabs;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.systems.modules.misc.BetterChat;
import meteordevelopment.meteorclient.utils.misc.MeteorStarscript;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.metadata.ModMetadata;
import org.meteordev.starscript.value.ValueMap;
import org.slf4j.Logger;

import static meteordevelopment.meteorclient.MeteorClient.identifier;

public class THMTools extends MeteorAddon {
    public static final String MOD_ID = "thmtools";
    public static final ModMetadata METADATA;
    public static final String VERSION;
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY;
    //public static final HudGroup HUD_GROUP = new HudGroup("Example");

    static {
        METADATA = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow().getMetadata();
        VERSION = METADATA.getVersion().getFriendlyString();
        CATEGORY = new Category("THM Tools");
    }

    @Override
    public void onInitialize() {
        LOG.info("Initializing THM Tools Addon");
        LogUtils.getLogger().info("Initializing THMTools {}", THMTools.VERSION);

        Systems.add(new THMSystem());

        // Systems
        BetterChat.registerCustomHead("[THM]", identifier("icon.png"));
        MeteorStarscript.ss.set("thmtools", new ValueMap().set("version", THMTools.VERSION));
        Tabs.add(new THMTab());

        // Commands
        Commands.add(new Center());
        //Commands.add(new Coordinates());

        // Hud
        Hud hud = Systems.get(Hud.class);
        hud.register(DailyStatsHud.INFO);

        // Modules
        Modules modules = Modules.get();

        // Modules
        Modules.get().add(new THMHighway());
        Modules.get().add(new AxisViewer());

        // Commands
        //Commands.add(new Command.Center());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "me.oneex.thmtools";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("MeteorDevelopment", "meteor-addon-template");
    }
}