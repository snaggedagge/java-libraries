package dkarlsso.commons.raspberry.sensor.temperature;

import dkarlsso.commons.raspberry.exception.NoConnectionException;

public class TemperatureSensorStub implements TemperatureSensor {

    private double temperature;

    public TemperatureSensorStub(double temperature) {
        this.temperature = temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    @Override
    public double readTemp() throws NoConnectionException {
        return temperature;
    }
}
