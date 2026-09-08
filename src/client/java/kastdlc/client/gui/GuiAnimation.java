package kastdlc.client.gui;

public final class GuiAnimation {

    private GuiAnimation() {
    }

    public static float animate(
            float current,
            float target,
            float speed
    ) {
        float difference = target - current;

        if (Math.abs(difference) < 0.001f) {
            return target;
        }

        return current + difference * speed;
    }

    public static float ease(float value) {
        value = Math.max(0.0f, Math.min(1.0f, value));

        return value * value * (3.0f - 2.0f * value);
    }

    public static float easeOut(float value) {
        value = Math.max(0.0f, Math.min(1.0f, value));

        float inverse = 1.0f - value;

        return 1.0f - inverse * inverse;
    }
}