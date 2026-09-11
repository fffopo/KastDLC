package kastdlc.client.module.impl.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import kastdlc.client.module.Module;
import kastdlc.client.module.settings.BooleanSetting;
import kastdlc.client.module.settings.ModeSetting;
import kastdlc.client.module.settings.NumberSetting;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ESP extends Module {

    private static final Minecraft MC =
            Minecraft.getInstance();

    private static final FontDescription CUSTOM_FONT =
            new FontDescription.Resource(
                    Identifier.fromNamespaceAndPath(
                            "kastdlc",
                            "captura_now"
                    )
            );

    private static final long COMBAT_TIME =
            3000L;

    private static final float FILL_ALPHA =
            0.18F;

    private static final long TICK_NANOS =
            50_000_000L;

    private final ModeSetting target =
            addSetting(
                    new ModeSetting(
                            "Target",
                            "Players",
                            "Players",
                            "Mobs",
                            "Animals",
                            "Monsters",
                            "All"
                    )
            );

    private final ModeSetting mode =
            addSetting(
                    new ModeSetting(
                            "Mode",
                            "Box",
                            "Box",
                            "Outline",
                            "Nametag",
                            "Box + Nametag"
                    )
            );

    private final BooleanSetting health =
            addSetting(
                    new BooleanSetting(
                            "Health",
                            true
                    )
            );

    private final BooleanSetting distance =
            addSetting(
                    new BooleanSetting(
                            "Distance",
                            true
                    )
            );

    private final BooleanSetting armor =
            addSetting(
                    new BooleanSetting(
                            "Armor",
                            false
                    )
            );

    private final BooleanSetting combatStatus =
            addSetting(
                    new BooleanSetting(
                            "Combat Status",
                            true
                    )
            );

    private final NumberSetting range =
            addSetting(
                    new NumberSetting(
                            "Range",
                            64.0,
                            8.0,
                            128.0,
                            1.0
                    )
            );

    private final Map<UUID, Float> lastHealth =
            new HashMap<>();

    private final Map<UUID, Long> lastDamage =
            new HashMap<>();

    private UUID combatTarget;

    private long lastClientTickNanos =
            System.nanoTime();

    public ESP() {

        super(
                "ESP",
                Category.RENDER
        );

        ClientTickEvents.END_CLIENT_TICK.register(
                client -> {

                    if (client != MC) {
                        return;
                    }

                    lastClientTickNanos =
                            System.nanoTime();
                }
        );

        WorldRenderEvents.AFTER_ENTITIES.register(
                this::onWorldRender
        );
    }

    @Override
    protected void onEnable() {

        lastHealth.clear();
        lastDamage.clear();

        combatTarget = null;

        lastClientTickNanos =
                System.nanoTime();

        if (MC.player != null) {

            lastHealth.put(
                    MC.player.getUUID(),
                    MC.player.getHealth()
            );
        }
    }

    @Override
    protected void onDisable() {

        lastHealth.clear();
        lastDamage.clear();

        combatTarget = null;
    }

    private void onWorldRender(
            WorldRenderContext context
    ) {

        if (!isEnabled()) {
            return;
        }

        if (MC.level == null) {
            return;
        }

        if (MC.player == null) {
            return;
        }

        MultiBufferSource consumers =
                context.consumers();

        if (consumers == null) {
            return;
        }

        PoseStack matrices =
                context.matrices();

        Camera camera =
                MC.gameRenderer
                        .getMainCamera();

        Vec3 cameraPosition =
                camera.position();

        double maxRange =
                range.getValue();

        AABB searchBox =
                MC.player
                        .getBoundingBox()
                        .inflate(maxRange);

        updateCombatTracking();

        float partialTick =
                getVisualPartialTick();

        for (LivingEntity entity :
                MC.level.getEntitiesOfClass(
                        LivingEntity.class,
                        searchBox
                )) {

            if (!shouldRender(entity)) {
                continue;
            }

            if (mode.is("Box")
                    || mode.is("Box + Nametag")
                    || mode.is("Outline")) {

                renderBox(
                        matrices,
                        consumers,
                        entity,
                        cameraPosition,
                        partialTick
                );
            }

            if (mode.is("Nametag")
                    || mode.is("Box + Nametag")) {

                renderNametag(
                        matrices,
                        consumers,
                        entity,
                        cameraPosition,
                        partialTick
                );
            }
        }
    }

    private float getVisualPartialTick() {

        long elapsed =
                System.nanoTime()
                        - lastClientTickNanos;

        if (elapsed < 0L) {
            return 0.0F;
        }

        float partial =
                elapsed
                        / (float) TICK_NANOS;

        return Math.max(
                0.0F,
                Math.min(
                        1.0F,
                        partial
                )
        );
    }

    private boolean shouldRender(
            LivingEntity entity
    ) {

        if (entity == MC.player) {
            return false;
        }

        if (!entity.isAlive()) {
            return false;
        }

        if (entity.isSpectator()) {
            return false;
        }

        if (!matchesTarget(entity)) {
            return false;
        }

        double distance =
                MC.player.distanceTo(entity);

        return distance <= range.getValue();
    }

    private boolean matchesTarget(
            LivingEntity entity
    ) {

        if (target.is("All")) {
            return true;
        }

        if (target.is("Players")) {
            return entity instanceof Player;
        }

        if (target.is("Animals")) {
            return entity instanceof Animal;
        }

        if (target.is("Monsters")) {
            return entity instanceof Monster;
        }

        if (target.is("Mobs")) {

            return !(entity instanceof Player)
                    && !(entity instanceof Animal)
                    && !(entity instanceof Monster);
        }

        return false;
    }

    private void updateCombatTracking() {

        if (MC.player == null) {
            return;
        }

        if (MC.level == null) {
            return;
        }

        trackEntityDamage(
                MC.player
        );

        double maxRange =
                range.getValue();

        AABB searchBox =
                MC.player
                        .getBoundingBox()
                        .inflate(maxRange);

        for (LivingEntity entity :
                MC.level.getEntitiesOfClass(
                        LivingEntity.class,
                        searchBox
                )) {

            if (entity == MC.player) {
                continue;
            }

            trackEntityDamage(
                    entity
            );
        }

        if (combatTarget == null) {
            return;
        }

        long now =
                System.currentTimeMillis();

        Long targetDamage =
                lastDamage.get(
                        combatTarget
                );

        Long playerDamage =
                lastDamage.get(
                        MC.player.getUUID()
                );

        boolean targetRecentlyDamaged =
                targetDamage != null
                        && now - targetDamage
                        <= COMBAT_TIME;

        boolean playerRecentlyDamaged =
                playerDamage != null
                        && now - playerDamage
                        <= COMBAT_TIME;

        if (!targetRecentlyDamaged
                && !playerRecentlyDamaged) {

            combatTarget = null;
        }
    }

    private void trackEntityDamage(
            LivingEntity entity
    ) {

        UUID uuid =
                entity.getUUID();

        float currentHealth =
                entity.getHealth();

        Float previousHealth =
                lastHealth.put(
                        uuid,
                        currentHealth
                );

        if (previousHealth == null) {
            return;
        }

        if (currentHealth < previousHealth) {

            lastDamage.put(
                    uuid,
                    System.currentTimeMillis()
            );

            if (entity != MC.player) {

                combatTarget =
                        uuid;
            }
        }
    }

    private boolean isInCombat(
            LivingEntity entity
    ) {

        if (!combatStatus.isEnabled()) {
            return false;
        }

        if (combatTarget == null) {
            return false;
        }

        if (!combatTarget.equals(
                entity.getUUID()
        )) {

            return false;
        }

        long now =
                System.currentTimeMillis();

        Long targetDamage =
                lastDamage.get(
                        entity.getUUID()
                );

        Long playerDamage =
                lastDamage.get(
                        MC.player.getUUID()
                );

        boolean targetRecentlyDamaged =
                targetDamage != null
                        && now - targetDamage
                        <= COMBAT_TIME;

        boolean playerRecentlyDamaged =
                playerDamage != null
                        && now - playerDamage
                        <= COMBAT_TIME;

        return targetRecentlyDamaged
                || playerRecentlyDamaged;
    }

    private String getCombatStatus(
            LivingEntity entity
    ) {

        if (!isInCombat(entity)) {
            return "";
        }

        float playerHealth =
                MC.player.getHealth();

        float targetHealth =
                entity.getHealth();

        if (playerHealth > targetHealth) {
            return "Winning";
        }

        if (playerHealth < targetHealth) {
            return "Losing";
        }

        return "Draw";
    }

    private int getCombatColor(
            LivingEntity entity
    ) {

        String status =
                getCombatStatus(entity);

        if (status.equals("Winning")) {
            return 0xFF55FF55;
        }

        if (status.equals("Losing")) {
            return 0xFFFF5555;
        }

        if (status.equals("Draw")) {
            return 0xFFFFFF55;
        }

        return 0xFFFFFFFF;
    }

    private AABB getInterpolatedBox(
            LivingEntity entity,
            float partialTick
    ) {

        double x =
                entity.xo
                        + (
                        entity.getX()
                                - entity.xo
                )
                        * partialTick;

        double y =
                entity.yo
                        + (
                        entity.getY()
                                - entity.yo
                )
                        * partialTick;

        double z =
                entity.zo
                        + (
                        entity.getZ()
                                - entity.zo
                )
                        * partialTick;

        double offsetX =
                x - entity.getX();

        double offsetY =
                y - entity.getY();

        double offsetZ =
                z - entity.getZ();

        return entity.getBoundingBox()
                .move(
                        offsetX,
                        offsetY,
                        offsetZ
                );
    }

    private void renderBox(
            PoseStack matrices,
            MultiBufferSource consumers,
            LivingEntity entity,
            Vec3 cameraPosition,
            float partialTick
    ) {

        AABB box =
                getInterpolatedBox(
                        entity,
                        partialTick
                ).move(
                        -cameraPosition.x,
                        -cameraPosition.y,
                        -cameraPosition.z
                );

        int color =
                getCombatColor(entity);

        float red =
                ((color >> 16) & 0xFF)
                        / 255.0F;

        float green =
                ((color >> 8) & 0xFF)
                        / 255.0F;

        float blue =
                (color & 0xFF)
                        / 255.0F;

        float width =
                mode.is("Outline")
                        ? 2.5F
                        : 1.5F;

        renderFilledBox(
                matrices,
                consumers,
                box,
                red,
                green,
                blue
        );

        renderBoxOutline(
                matrices,
                consumers,
                box,
                red,
                green,
                blue,
                width
        );
    }

    private void renderFilledBox(
            PoseStack matrices,
            MultiBufferSource consumers,
            AABB box,
            float red,
            float green,
            float blue
    ) {

        VertexConsumer buffer =
                consumers.getBuffer(
                        RenderTypes.debugFilledBox()
                );

        PoseStack.Pose pose =
                matrices.last();

        float alpha =
                FILL_ALPHA;

        drawFilledQuad(
                buffer,
                pose,
                box.minX,
                box.minY,
                box.minZ,
                box.maxX,
                box.minY,
                box.maxZ,
                red,
                green,
                blue,
                alpha
        );

        drawFilledQuad(
                buffer,
                pose,
                box.minX,
                box.maxY,
                box.maxZ,
                box.maxX,
                box.maxY,
                box.minZ,
                red,
                green,
                blue,
                alpha
        );

        drawFilledQuad(
                buffer,
                pose,
                box.minX,
                box.minY,
                box.maxZ,
                box.maxX,
                box.minY,
                box.minZ,
                red,
                green,
                blue,
                alpha
        );

        drawFilledQuad(
                buffer,
                pose,
                box.minX,
                box.maxY,
                box.minZ,
                box.maxX,
                box.maxY,
                box.maxZ,
                red,
                green,
                blue,
                alpha
        );

        drawFilledQuad(
                buffer,
                pose,
                box.minX,
                box.minY,
                box.minZ,
                box.minX,
                box.maxY,
                box.maxZ,
                red,
                green,
                blue,
                alpha
        );

        drawFilledQuad(
                buffer,
                pose,
                box.maxX,
                box.minY,
                box.maxZ,
                box.maxX,
                box.maxY,
                box.minZ,
                red,
                green,
                blue,
                alpha
        );
    }

    private void drawFilledQuad(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            double x1,
            double y1,
            double z1,
            double x2,
            double y2,
            double z2,
            float red,
            float green,
            float blue,
            float alpha
    ) {

        if (x1 == x2 && y1 == y2 && z1 == z2) {
            return;
        }

        double minX =
                Math.min(x1, x2);

        double maxX =
                Math.max(x1, x2);

        double minY =
                Math.min(y1, y2);

        double maxY =
                Math.max(y1, y2);

        double minZ =
                Math.min(z1, z2);

        double maxZ =
                Math.max(z1, z2);

        if (y1 == y2) {

            buffer.addVertex(
                            pose,
                            (float) minX,
                            (float) y1,
                            (float) minZ
                    )
                    .setColor(
                            red,
                            green,
                            blue,
                            alpha
                    );

            buffer.addVertex(
                            pose,
                            (float) maxX,
                            (float) y1,
                            (float) minZ
                    )
                    .setColor(
                            red,
                            green,
                            blue,
                            alpha
                    );

            buffer.addVertex(
                            pose,
                            (float) maxX,
                            (float) y1,
                            (float) maxZ
                    )
                    .setColor(
                            red,
                            green,
                            blue,
                            alpha
                    );

            buffer.addVertex(
                            pose,
                            (float) minX,
                            (float) y1,
                            (float) maxZ
                    )
                    .setColor(
                            red,
                            green,
                            blue,
                            alpha
                    );

            return;
        }

        if (z1 == z2) {

            buffer.addVertex(
                            pose,
                            (float) minX,
                            (float) minY,
                            (float) z1
                    )
                    .setColor(
                            red,
                            green,
                            blue,
                            alpha
                    );

            buffer.addVertex(
                            pose,
                            (float) maxX,
                            (float) minY,
                            (float) z1
                    )
                    .setColor(
                            red,
                            green,
                            blue,
                            alpha
                    );

            buffer.addVertex(
                            pose,
                            (float) maxX,
                            (float) maxY,
                            (float) z1
                    )
                    .setColor(
                            red,
                            green,
                            blue,
                            alpha
                    );

            buffer.addVertex(
                            pose,
                            (float) minX,
                            (float) maxY,
                            (float) z1
                    )
                    .setColor(
                            red,
                            green,
                            blue,
                            alpha
                    );

            return;
        }

        buffer.addVertex(
                        pose,
                        (float) x1,
                        (float) minY,
                        (float) minZ
                )
                .setColor(
                        red,
                        green,
                        blue,
                        alpha
                );

        buffer.addVertex(
                        pose,
                        (float) x1,
                        (float) maxY,
                        (float) minZ
                )
                .setColor(
                        red,
                        green,
                        blue,
                        alpha
                );

        buffer.addVertex(
                        pose,
                        (float) x2,
                        (float) maxY,
                        (float) maxZ
                )
                .setColor(
                        red,
                        green,
                        blue,
                        alpha
                );

        buffer.addVertex(
                        pose,
                        (float) x2,
                        (float) minY,
                        (float) maxZ
                )
                .setColor(
                        red,
                        green,
                        blue,
                        alpha
                );
    }

    private void renderBoxOutline(
            PoseStack matrices,
            MultiBufferSource consumers,
            AABB box,
            float red,
            float green,
            float blue,
            float width
    ) {

        VertexConsumer buffer =
                consumers.getBuffer(
                        RenderTypes.lines()
                );

        PoseStack.Pose pose =
                matrices.last();

        drawLine(
                buffer,
                pose,
                box.minX,
                box.minY,
                box.minZ,
                box.maxX,
                box.minY,
                box.minZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.maxX,
                box.minY,
                box.minZ,
                box.maxX,
                box.minY,
                box.maxZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.maxX,
                box.minY,
                box.maxZ,
                box.minX,
                box.minY,
                box.maxZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.minX,
                box.minY,
                box.maxZ,
                box.minX,
                box.minY,
                box.minZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.minX,
                box.maxY,
                box.minZ,
                box.maxX,
                box.maxY,
                box.minZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.maxX,
                box.maxY,
                box.minZ,
                box.maxX,
                box.maxY,
                box.maxZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.maxX,
                box.maxY,
                box.maxZ,
                box.minX,
                box.maxY,
                box.maxZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.minX,
                box.maxY,
                box.maxZ,
                box.minX,
                box.maxY,
                box.minZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.minX,
                box.minY,
                box.minZ,
                box.minX,
                box.maxY,
                box.minZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.maxX,
                box.minY,
                box.minZ,
                box.maxX,
                box.maxY,
                box.minZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.maxX,
                box.minY,
                box.maxZ,
                box.maxX,
                box.maxY,
                box.maxZ,
                red,
                green,
                blue,
                width
        );

        drawLine(
                buffer,
                pose,
                box.minX,
                box.minY,
                box.maxZ,
                box.minX,
                box.maxY,
                box.maxZ,
                red,
                green,
                blue,
                width
        );
    }

    private void drawLine(
            VertexConsumer buffer,
            PoseStack.Pose pose,
            double x1,
            double y1,
            double z1,
            double x2,
            double y2,
            double z2,
            float red,
            float green,
            float blue,
            float width
    ) {

        float nx =
                (float) (x2 - x1);

        float ny =
                (float) (y2 - y1);

        float nz =
                (float) (z2 - z1);

        float length =
                (float) Math.sqrt(
                        nx * nx
                                + ny * ny
                                + nz * nz
                );

        if (length > 0.0F) {

            nx /= length;
            ny /= length;
            nz /= length;

        } else {

            nx = 0.0F;
            ny = 1.0F;
            nz = 0.0F;
        }

        buffer.addVertex(
                        pose,
                        (float) x1,
                        (float) y1,
                        (float) z1
                )
                .setColor(
                        red,
                        green,
                        blue,
                        1.0F
                )
                .setNormal(
                        pose,
                        nx,
                        ny,
                        nz
                )
                .setLineWidth(width);

        buffer.addVertex(
                        pose,
                        (float) x2,
                        (float) y2,
                        (float) z2
                )
                .setColor(
                        red,
                        green,
                        blue,
                        1.0F
                )
                .setNormal(
                        pose,
                        nx,
                        ny,
                        nz
                )
                .setLineWidth(width);
    }

    private void renderNametag(
            PoseStack matrices,
            MultiBufferSource consumers,
            LivingEntity entity,
            Vec3 cameraPosition,
            float partialTick
    ) {

        double x =
                entity.xo
                        + (
                        entity.getX()
                                - entity.xo
                )
                        * partialTick;

        double y =
                entity.yo
                        + (
                        entity.getY()
                                - entity.yo
                )
                        * partialTick;

        double z =
                entity.zo
                        + (
                        entity.getZ()
                                - entity.zo
                )
                        * partialTick;

        Vec3 position =
                new Vec3(
                        x,
                        y,
                        z
                ).subtract(
                        cameraPosition
                );

        matrices.pushPose();

        matrices.translate(
                position.x,
                position.y
                        + entity.getBbHeight()
                        + 0.55,
                position.z
        );

        Camera camera =
                MC.gameRenderer
                        .getMainCamera();

        matrices.mulPose(
                camera.rotation()
        );

        matrices.scale(
                -0.025F,
                -0.025F,
                0.025F
        );

        StringBuilder text =
                new StringBuilder(
                        getEntityName(entity)
                );

        if (health.isEnabled()) {

            text.append("  ");

            text.append(
                    formatHealth(entity)
            );
        }

        if (distance.isEnabled()) {

            text.append("  ");

            text.append(
                    String.format(
                            "%.1fm",
                            MC.player.distanceTo(
                                    entity
                            )
                    )
            );
        }

        if (armor.isEnabled()
                && entity instanceof Player player) {

            text.append("  Armor ");

            text.append(
                    getArmorCount(player)
            );
        }

        Component nameText =
                Component.literal(
                        text.toString()
                ).withStyle(
                        Style.EMPTY.withFont(
                                CUSTOM_FONT
                        )
                );

        Font font =
                MC.font;

        int nameWidth =
                font.width(
                        nameText
                );

        font.drawInBatch(
                nameText,
                -nameWidth / 2.0F,
                0.0F,
                getCombatColor(entity),
                false,
                matrices.last().pose(),
                consumers,
                Font.DisplayMode.NORMAL,
                0,
                15728880
        );

        String status =
                getCombatStatus(entity);

        if (!status.isEmpty()) {

            Component statusText =
                    Component.literal(
                            status
                    ).withStyle(
                            Style.EMPTY.withFont(
                                    CUSTOM_FONT
                            )
                    );

            int statusWidth =
                    font.width(
                            statusText
                    );

            font.drawInBatch(
                    statusText,
                    -statusWidth / 2.0F,
                    10.0F,
                    getStatusColor(status),
                    false,
                    matrices.last().pose(),
                    consumers,
                    Font.DisplayMode.NORMAL,
                    0,
                    15728880
            );
        }

        matrices.popPose();
    }

    private String getEntityName(
            LivingEntity entity
    ) {

        if (entity.hasCustomName()) {

            Component customName =
                    entity.getCustomName();

            if (customName != null) {
                return customName.getString();
            }
        }

        return entity.getDisplayName()
                .getString();
    }

    private int getStatusColor(
            String status
    ) {

        if (status.equals("Winning")) {
            return 0xFF55FF55;
        }

        if (status.equals("Losing")) {
            return 0xFFFF5555;
        }

        return 0xFFFFFF55;
    }

    private String formatHealth(
            LivingEntity entity
    ) {

        float current =
                Math.max(
                        0.0F,
                        entity.getHealth()
                );

        float max =
                Math.max(
                        1.0F,
                        entity.getMaxHealth()
                );

        return String.format(
                "%.1f/%.1f",
                current,
                max
        );
    }

    private int getArmorCount(
            Player player
    ) {

        int count = 0;

        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        };

        for (EquipmentSlot slot : slots) {

            if (!player
                    .getItemBySlot(slot)
                    .isEmpty()) {

                count++;
            }
        }

        return count;
    }
}