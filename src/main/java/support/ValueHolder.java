package support;

/**
 * Class to hold primitive type double value as a Reference Data Type
 */
public class ValueHolder {
    private double value;

    public ValueHolder() {
        value = 0;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
