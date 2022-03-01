package dkarlsso.commons.raspberry.relay;

import com.pi4j.wiringpi.Gpio;
import dkarlsso.commons.raspberry.enums.GPIOPins;
import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;

import static com.pi4j.wiringpi.Gpio.*;

public class OptoRelay implements RelayInterface {

    private int pin;

    private boolean isHigh;

    // Some relays does the exakt oposite than other ones, meaning low turns them on, and some turns off on low
    private boolean isMirrored = false;

    public OptoRelay(int pin) {
        this.pin = pin;
        Gpio.pinMode(pin,OUTPUT);
        Gpio.digitalWrite(pin,HIGH);
        isHigh = false;
    }

    public OptoRelay(GPIOPins gpioPin) {
        this(gpioPin.getWiringPin());
    }

    public OptoRelay(GPIOPins gpioPin, final boolean isMirrored) {
        this(gpioPin.getWiringPin());
        this.isMirrored = isMirrored;
    }

    @Override
    public void setHigh() {
        Gpio.digitalWrite(pin,LOW);
        isHigh = true;
        if (isMirrored) {
            Gpio.digitalWrite(pin, HIGH);
        }
        else {
            Gpio.digitalWrite(pin,LOW);
        }
    }

    @Override
    public void setLow() {
        isHigh = false;
        if (isMirrored) {
            Gpio.digitalWrite(pin,LOW);
        }
        else {
            Gpio.digitalWrite(pin,HIGH);
        }
    }

    @Override
    public void setState(boolean state) {
        if(state) {
            this.setHigh();
        } else {
            this.setLow();
        }
    }

    @Override
    public void switchState() {
        if(isHigh) {
            this.setLow();
        } else {
            this.setHigh();
        }
    }

    @Override
    public boolean isHigh() {
        return isHigh;
    }
}
