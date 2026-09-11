package kastdlc.client.module.settings;

public class NumberSetting extends Setting<Double> {

    private final double min;
    private final double max;
    private final double increment;

    public NumberSetting(
            String name,
            double value,
            double min,
            double max,
            double increment
    ) {
        super(name, value);

        this.min = min;
        this.max = max;
        this.increment = increment;
    }

    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }

    public double getIncrement() {
        return increment;
    }

    public void setValue(double value) {

        value = Math.max(min, value);
        value = Math.min(max, value);

        value = Math.round(
                value / increment
        ) * increment;

        super.setValue(value);
    }
}