package kastdlc.client.module.impl.movement;

import kastdlc.client.module.Module;
import net.minecraft.client.Minecraft;

public class Sprint extends Module {

    private final Minecraft minecraft = Minecraft.getInstance();

    public Sprint() {
        super("Sprint", Category.MOVEMENT);
    }

    @Override
    protected void onEnable() {
    }

    @Override
    protected void onDisable() {
        if (minecraft.player != null) {
            minecraft.player.setSprinting(false);
        }
    }

    public void onTick() {
        if (minecraft.player == null) {
            return;
        }

        if (minecraft.player.isMovingSlowly()) {
            minecraft.player.setSprinting(false);
            return;
        }

        if (minecraft.player.getDeltaMovement().horizontalDistanceSqr() > 0.0) {
            minecraft.player.setSprinting(true);
        }
    }
}
