package wrime.ast;

import wrime.Location;

public abstract class Locatable {
    private Location location;

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
