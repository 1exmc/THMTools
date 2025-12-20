package me.oneex.thmtools.system;

import me.oneex.thmtools.THMTools;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.player.ChatUtils;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class THMSystem extends System<THMSystem> {
    public final Settings settings = new Settings();

    private final SettingGroup sgPrefix = settings.createGroup("Prefix");

    // Prefix

    public final Setting<String> prefix = sgPrefix.add(new StringSetting.Builder()
            .name("prefix")
            .description("What prefix to use for THM modules.")
            .defaultValue("THM Tools")
            .onChanged(p -> ChatUtils.registerCustomPrefix("me.oneex.thmtools", this::getPrefix))
            .build()
    );

    public final Setting<SettingColor> prefixColor = sgPrefix.add(new ColorSetting.Builder()
            .name("prefix-color")
            .description("Color display for the prefix.")
            .defaultValue(new SettingColor(145, 61, 226, 255))
            .onChanged(p -> ChatUtils.registerCustomPrefix("me.oneex.thmtools", this::getPrefix))
            .build()
    );

    public final Setting<Format> prefixFormat = sgPrefix.add(new EnumSetting.Builder<Format>()
            .name("prefix-format")
            .description("What type of minecraft formatting should be applied to the prefix.")
            .defaultValue(Format.Normal)
            .onChanged(p -> ChatUtils.registerCustomPrefix("me.oneex.thmtools", this::getPrefix))
            .build()
    );

    public final Setting<Boolean> formatBrackets = sgPrefix.add(new BoolSetting.Builder()
            .name("format-brackets")
            .description("Whether the formatting should apply to the brackets as well.")
            .visible(() -> prefixFormat.get() != Format.Normal)
            .onChanged(p -> ChatUtils.registerCustomPrefix("me.oneex.thmtools", this::getPrefix))
            .defaultValue(true)
            .build()
    );

    public final Setting<String> leftBracket = sgPrefix.add(new StringSetting.Builder()
            .name("left-bracket")
            .description("What to be displayed as left bracket for the prefix.")
            .defaultValue("[")
            .onChanged(p -> ChatUtils.registerCustomPrefix("me.oneex.thmtools", this::getPrefix))
            .build()
    );

    public final Setting<String> rightBracket = sgPrefix.add(new StringSetting.Builder()
            .name("right-bracket")
            .description("What to be displayed as right bracket for the prefix.")
            .defaultValue("]")
            .onChanged(p -> ChatUtils.registerCustomPrefix("me.oneex.thmtools", this::getPrefix))
            .build()
    );

    public final Setting<SettingColor> leftColor = sgPrefix.add(new ColorSetting.Builder()
            .name("left-color")
            .description("Color display for the left bracket.")
            .defaultValue(new SettingColor(150, 150, 150, 255))
            .onChanged(p -> ChatUtils.registerCustomPrefix("me.oneex.thmtools", this::getPrefix))
            .build()
    );

    public final Setting<SettingColor> rightColor = sgPrefix.add(new ColorSetting.Builder()
            .name("right-color")
            .description("Color display for the right bracket.")
            .defaultValue(new SettingColor(150, 150, 150, 255))
            .onChanged(p -> ChatUtils.registerCustomPrefix("me.oneex.thmtools", this::getPrefix))
            .build()
    );

    public THMSystem() {
        super("thm-tools");
        ChatUtils.registerCustomPrefix("me.oneex.thmtools", this::getPrefix);
    }

    public static THMSystem get() {
        return Systems.get(THMSystem.class);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putString("version", THMTools.VERSION);
        tag.put("settings", settings.toTag());

        return tag;
    }

    @Override
    public THMSystem fromTag(NbtCompound tag) {
        tag.getCompound("settings").ifPresent(settings::fromTag);
        return this;
    }

    // Utils

    public Text getPrefix() {
        MutableText logo = Text.literal(prefix.get()).formatted(prefixFormat.get().formatting);
        MutableText left = Text.literal(leftBracket.get());
        MutableText right = Text.literal(rightBracket.get());

        if (formatBrackets.get()) {
            left = left.formatted(prefixFormat.get().formatting);
            right = right.formatted(prefixFormat.get().formatting);
        }

        logo = logo.withColor(prefixColor.get().getPacked());
        left = left.withColor(leftColor.get().getPacked());
        right = right.withColor(rightColor.get().getPacked());

        return Text.empty().append(left).append(logo).append(right).append(" ");
    }

    public enum Format {
        Normal(Formatting.RESET),
        Heavy(Formatting.BOLD),
        Italic(Formatting.ITALIC),
        Underline(Formatting.UNDERLINE),
        Crossed(Formatting.STRIKETHROUGH),
        Cursed(Formatting.OBFUSCATED);

        final Formatting formatting;

        Format(Formatting formatting) {
            this.formatting = formatting;
        }
    }
}