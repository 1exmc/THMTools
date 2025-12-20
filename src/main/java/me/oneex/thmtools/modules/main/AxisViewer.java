package me.oneex.thmtools.modules.main;

import it.unimi.dsi.fastutil.ints.IntList;
import me.oneex.thmtools.THMTools;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.player.PlayerUtils;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.meteorclient.utils.world.Dimension;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.util.math.Vec3d;

public class AxisViewer extends Module {
    private final SettingGroup sgOverworld = settings.createGroup("Overworld");
    private final SettingGroup sgNether = settings.createGroup("Nether");
    private final SettingGroup sgEnd = settings.createGroup("End");

    // Overworld

    private final Setting<AxisType> overworldAxisTypes = sgOverworld.add(new EnumSetting.Builder<AxisType>()
            .name("render")
            .description("Which axes to display.")
            .defaultValue(AxisType.Both)
            .build()
    );

    private final Setting<Integer> overworldY = sgOverworld.add(new IntSetting.Builder()
            .name("height")
            .description("Y position of the line.")
            .defaultValue(63)
            .sliderMin(-64)
            .sliderMax(319)
            .visible(() -> overworldAxisTypes.get() != AxisType.None)
            .build()
    );

    private final Setting<SettingColor> overworldColor = sgOverworld.add(new ColorSetting.Builder()
            .name("color")
            .description("The line color.")
            .defaultValue(new SettingColor(25, 25, 225, 255))
            .visible(() -> overworldAxisTypes.get() != AxisType.None)
            .build()
    );

    // Nether

    private final Setting<Boolean> netherCardinal = sgNether.add(new BoolSetting.Builder()
            .name("render Cardinal: ")
            .description("Draw cardinal highways")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> netherDiagonal = sgNether.add(new BoolSetting.Builder()
            .name("render Diagonal: ")
            .description("Draw diagonal highways")
            .defaultValue(true)
            .build()
    );

    private final Setting<Boolean> netherRing = sgNether.add(new BoolSetting.Builder()
            .name("render Ring: ")
            .description("Draw ring highways")
            .defaultValue(false)
            .build()
    );

    private final Setting<Boolean> netherDiamond = sgNether.add(new BoolSetting.Builder()
            .name("render Diamond: ")
            .description("Draw diamond highways")
            .defaultValue(false)
            .build()
    );

    private final Setting<Integer> netherY = sgNether.add(new IntSetting.Builder()
            .name("height")
            .description("Y position of the line.")
            .defaultValue(120)
            .sliderMin(0)
            .sliderMax(255)
            .build()
    );

    private final Setting<SettingColor> netherColor = sgNether.add(new ColorSetting.Builder()
            .name("color")
            .description("The line color.")
            .defaultValue(new SettingColor(225, 25, 25, 255))
            .build()
    );

    // End

    private final Setting<AxisType> endAxisTypes = sgEnd.add(new EnumSetting.Builder<AxisType>()
            .name("render")
            .description("Which axes to display.")
            .defaultValue(AxisType.Both)
            .build()
    );

    private final Setting<Integer> endY = sgEnd.add(new IntSetting.Builder()
            .name("height")
            .description("Y position of the line.")
            .defaultValue(64)
            .sliderMin(0)
            .sliderMax(255)
            .visible(() -> endAxisTypes.get() != AxisType.None)
            .build()
    );

    private final Setting<SettingColor> endColor = sgEnd.add(new ColorSetting.Builder()
            .name("color")
            .description("The line color.")
            .defaultValue(new SettingColor(225, 25, 25, 255))
            .visible(() -> endAxisTypes.get() != AxisType.None)
            .build()
    );

    public AxisViewer() {
        super(THMTools.CATEGORY, "axis-viewer", "Displays world axes.");
    }

    private static final IntList RING_ROADS = IntList.of(
            200,
            500,
            750,
            1000,
            1500,
            2000,
            2500,
            5000,
            7500,
            10000,
            15000,
            20000,
            25000,
            50000,
            55000,
            62500,
            75000,
            100000,
            125000,
            250000,
            500000,
            750000,
            1000000,
            1250000,
            1568852,
            1875000,
            2500000,
            3750000
    );

    private static final IntList DIAMONDS = IntList.of(
            1000,
            2000,
            2500,
            5000,
            25000,
            50000,
            125000,
            250000,
            500000,
            3750000
    );

    @EventHandler
    private void onRender3D(Render3DEvent event) {
        if (mc.options.hudHidden) return;

        AxisType axisType;
        int y;
        Color lineColor;
        boolean netherCardinalLocal = false;
        boolean netherDiagonalLocal = false;
        boolean netherRingLocal = false;
        boolean netherDiamondLocal = false;

        switch (PlayerUtils.getDimension()) {
            case Overworld -> {
                axisType = overworldAxisTypes.get();
                y = overworldY.get();
                lineColor = overworldColor.get();
            }
            case Nether -> {
                axisType = AxisType.None;
                netherCardinalLocal = netherCardinal.get();
                netherDiagonalLocal = netherDiagonal.get();
                netherRingLocal = netherRing.get();
                netherDiamondLocal = netherDiamond.get();
                y = netherY.get();
                lineColor = netherColor.get();
            }
            case End -> {
                axisType = endAxisTypes.get();
                y = endY.get();
                lineColor = endColor.get();
            }
            default -> throw new IllegalStateException("Unexpected value: " + PlayerUtils.getDimension());
        }

        if (axisType == AxisType.None && PlayerUtils.getDimension() != Dimension.Nether) return;

        double renderY = y;

        // Render cardinal lines
        if (axisType.cardinals() || netherCardinalLocal) {
            // X axis
            drawSegmentedLine(event,
                    new Vec3d(0, renderY, 0),
                    new Vec3d(30_000_000, renderY, 0),
                    lineColor
            );
            drawSegmentedLine(event,
                    new Vec3d(0, renderY, 0),
                    new Vec3d(-30_000_000, renderY, 0),
                    lineColor
            );

            // Z axis
            drawSegmentedLine(event,
                    new Vec3d(0, renderY, 0),
                    new Vec3d(0, renderY, 30_000_000),
                    lineColor
            );
            drawSegmentedLine(event,
                    new Vec3d(0, renderY, 0),
                    new Vec3d(0, renderY, -30_000_000),
                    lineColor
            );
        }

        // Render diagonal lines
        if (axisType.diagonals() || netherDiagonalLocal) {
            // x = z
            drawSegmentedLine(event,
                    new Vec3d(-30_000_000, renderY, -30_000_000),
                    new Vec3d( 30_000_000, renderY,  30_000_000),
                    lineColor
            );

            // x = -z
            drawSegmentedLine(event,
                    new Vec3d(-30_000_000, renderY,  30_000_000),
                    new Vec3d( 30_000_000, renderY, -30_000_000),
                    lineColor
            );
        }

        // Render ring lines
        if (PlayerUtils.getDimension() == Dimension.Nether && netherRingLocal) {
            for (int r : RING_ROADS) {
                drawRing(event, renderY, r, lineColor);
            }
        }

        // Render diamond lines
        if (PlayerUtils.getDimension() == Dimension.Nether && netherDiamondLocal) {
            for (int d : DIAMONDS) {
                drawDiamond(event, renderY, d, lineColor);
            }
        }
    }

    private void drawRing(Render3DEvent event, double y, int r, Color color) {
        // bottom
        drawSegmentedLine(event,
                new Vec3d(-r, y, -r),
                new Vec3d(r,  y, -r),
                color
        );

        // top
        drawSegmentedLine(event,
                new Vec3d(-r, y, r),
                new Vec3d(r,  y, r),
                color
        );

        // left
        drawSegmentedLine(event,
                new Vec3d(-r, y, -r),
                new Vec3d(-r, y, r),
                color
        );

        // right
        drawSegmentedLine(event,
                new Vec3d(r, y, -r),
                new Vec3d(r, y, r),
                color
        );
    }

    private boolean shouldRenderRing(Vec3d cam, int r, double maxDistSq) {
        double dx = Math.max(Math.abs(cam.x) - r, 0);
        double dz = Math.max(Math.abs(cam.z) - r, 0);
        return dx * dx + dz * dz <= maxDistSq;
    }

    private void drawDiamond(Render3DEvent event, double y, int d, Color color) {
        drawSegmentedLine(event,
                new Vec3d( d, y,  0),
                new Vec3d( 0, y,  d),
                color
        );

        drawSegmentedLine(event,
                new Vec3d( 0, y,  d),
                new Vec3d(-d, y,  0),
                color
        );

        drawSegmentedLine(event,
                new Vec3d(-d, y,  0),
                new Vec3d( 0, y, -d),
                color
        );

        drawSegmentedLine(event,
                new Vec3d( 0, y, -d),
                new Vec3d( d, y,  0),
                color
        );
    }

    private boolean shouldRenderDiamond(Vec3d cam, int d, double maxDistSq) {
        double u = Math.abs(cam.x + cam.z);
        double v = Math.abs(cam.x - cam.z);
        double du = Math.max(u - d, 0);
        double dv = Math.max(v - d, 0);
        return du * du + dv * dv <= maxDistSq;
    }

    private void drawSegmentedLine(Render3DEvent event, Vec3d start, Vec3d end, Color color) {
        double segmentLength = 100_000; // Length of each segment to avoid rendering issues
        Vec3d direction = end.subtract(start).normalize();
        double totalLength = start.distanceTo(end);
        int segments = (int) (totalLength / segmentLength);

        Vec3d currentStart = start;
        for (int i = 0; i < segments; i++) {
            Vec3d currentEnd = currentStart.add(direction.multiply(segmentLength));
            drawLine(event, currentStart, currentEnd, color);
            currentStart = currentEnd;
        }
        // Draw remaining part
        drawLine(event, currentStart, end, color);
    }

    private void drawLine(Render3DEvent event, Vec3d start, Vec3d end, Color color) {
        event.renderer.line(start.getX(), start.getY(), start.getZ(),
                end.getX(), end.getY(), end.getZ(), color);
    }

    public enum AxisType {
        Both,
        Cardinals,
        Diagonals,
        None;

        boolean cardinals() {
            return this == Both || this == Cardinals;
        }

        boolean diagonals() {
            return this == Both || this == Diagonals;
        }
    }
}
