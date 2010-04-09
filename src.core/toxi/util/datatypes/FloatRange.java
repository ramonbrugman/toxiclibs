package toxi.util.datatypes;

import java.util.Random;

import javax.xml.bind.annotation.XmlAttribute;

import toxi.math.MathUtils;

public class FloatRange {

    @XmlAttribute
    public float min, max;

    @XmlAttribute(name = "default")
    public float currValue;

    protected Random random = new Random();

    public FloatRange() {
        this(0f, 1f);
    }

    public FloatRange(float min, float max) {
        this.min = min;
        this.max = max;
        this.currValue = min;
    }

    public float adjustCurrentBy(float val) {
        return setCurrent(currValue + val);
    }

    public FloatRange copy() {
        FloatRange range = new FloatRange(min, max);
        range.currValue = currValue;
        range.random = random;
        return range;
    }

    public float getCurrent() {
        return currValue;
    }

    public float getMedian() {
        return (min + max) * 0.5f;
    }

    public boolean isValueInRange(float val) {
        return val >= min && val <= max;
    }

    public float pickRandom() {
        currValue = MathUtils.random(random, min, max);
        return currValue;
    }

    public FloatRange seed(long seed) {
        random.setSeed(seed);
        return this;
    }

    public float setCurrent(float val) {
        currValue = MathUtils.clip(val, min, max);
        return currValue;
    }

    public FloatRange setRandom(Random rnd) {
        random = rnd;
        return this;
    }

    @Override
    public String toString() {
        return "FloatRange: " + min + " -> " + max;
    }
}
