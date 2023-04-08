package dkarlsso.commons.raspberry.relay;

import dkarlsso.commons.raspberry.relay.interfaces.RelayInterface;

/**
 * Forces a certain relay to always be on
 */
public class AlwaysOnRelay implements RelayInterface {

    private final RelayInterface relayInterface;

    public AlwaysOnRelay(final RelayInterface relayInterface) {
        this.relayInterface = relayInterface;
        this.relayInterface.setHigh();
    }

    @Override
    public void setHigh() {
    }

    @Override
    public void setLow() {
    }

    @Override
    public void setState(boolean state) {
    }

    @Override
    public void switchState() {

    }

    @Override
    public boolean isHigh() {
        return true;
    }
}
