package com.jackd.exchickens.util;

public class Averager {

    private int numValues;
    private float value;

    public void add(float newValue) {
        if(this.numValues == 0) {
            this.value = newValue;
        } else {
            float weight = 1.0f / (this.numValues + 1);
            this.value = this.value * (1.0f - weight) + newValue * weight;
        }
        this.numValues++;
    }

    public float getAverage() {
        return this.value;
    }

}
