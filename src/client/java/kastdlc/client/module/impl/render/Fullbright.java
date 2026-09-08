
package kastdlc.client.module.impl.render;

import kastdlc.client.module.Module;
import net.minecraft.client.Minecraft;

public class Fullbright extends Module {

    private final Minecraft minecraft = Minecraft.getInstance();

    private double previousGamma = 0.0;

    public Fullbright() {
        super("Fullbright", Category.RENDER);
    }

    @Override
    protected void onEnable() {
        previousGamma = minecraft.options.gamma().get();

        minecraft.options.gamma().set(16.0);
    }

    @Override
    protected void onDisable() {
        minecraft.options.gamma().set(previousGamma);
    }
}
