package me.oneex.thmtools.modules.hud;

import meteordevelopment.meteorclient.systems.hud.*;
import meteordevelopment.meteorclient.utils.render.color.Color;

import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

public class DailyStatsHud extends HudElement {
    public static final HudElementInfo<DailyStatsHud> INFO =
            new HudElementInfo<>(Hud.GROUP, "daily-stats", "Shows today's session stats.", DailyStatsHud::new);

    private static final Color WHITE = new Color();

    private final List<SessionEntry> todaySessions = new ArrayList<>();
    private long lastReload = 0;

    public DailyStatsHud() {
        super(INFO);
    }

    @Override
    public void tick(HudRenderer renderer) {
        // Reload max once per second
        if (System.currentTimeMillis() - lastReload < 1000) return;
        lastReload = System.currentTimeMillis();

        todaySessions.clear();

        Path file = Paths.get("stats.csv");
        if (!Files.exists(file)) return;

        String today = LocalDate.now().toString();

        try {
            List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
            for (int i = 1; i < lines.size(); i++) { // skip header
                String[] c = lines.get(i).split(",");

                if (!c[0].equals(today)) continue;

                todaySessions.add(new SessionEntry(
                        Integer.parseInt(c[7]),   // broken
                        Integer.parseInt(c[8]),   // placed
                        Integer.parseInt(c[9]),   // total
                        Double.parseDouble(c[6]), // duration
                        Double.parseDouble(c[10]),// bps
                        Double.parseDouble(c[11]) // distance
                ));
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void render(HudRenderer renderer) {
        double x = this.x;
        double y = this.y;
        double scale = Hud.get().getTextScale();
        double lineH = renderer.textHeight(true, scale);

        if (todaySessions.isEmpty()) {
            renderer.text("No stats today", x, y, WHITE, true, scale);
            setSize(renderer.textWidth("No stats today", true, scale), lineH);
            return;
        }

        int sessions = todaySessions.size();
        int broken = 0, placed = 0, total = 0;
        double time = 0, dist = 0;

        for (SessionEntry e : todaySessions) {
            broken += e.blocksBroken();
            placed += e.blocksPlaced();
            total  += e.blocksTotal();
            time   += e.durationSec();
            dist   += e.distance();
        }

        double avgBps = time > 0 ? total / time : 0;

        renderer.text("Today", x, y, WHITE, true, scale); y += lineH;
        renderer.text("Sessions: " + sessions, x, y, WHITE, true, scale); y += lineH;
        renderer.text("Blocks: " + total + " (B:" + broken + " | P:" + placed + ")", x, y, WHITE, true, scale); y += lineH;
        renderer.text(String.format(Locale.US, "Time: %.0f s", time), x, y, WHITE, true, scale); y += lineH;
        renderer.text(String.format(Locale.US, "Avg BPS: %.2f", avgBps), x, y, WHITE, true, scale); y += lineH;
        renderer.text(String.format(Locale.US, "Distance: %.0f", dist), x, y, WHITE, true, scale);

        setSize(
                renderer.textWidth("Blocks: " + total, true, scale),
                lineH * 6
        );
    }

    private record SessionEntry(
            int blocksBroken,
            int blocksPlaced,
            int blocksTotal,
            double durationSec,
            double bps,
            double distance
    ) {}
}
