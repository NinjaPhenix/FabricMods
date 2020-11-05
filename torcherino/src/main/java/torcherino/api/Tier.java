package torcherino.api;

public class Tier
{
    private final int MAX_SPEED;
    private final int XZ_RANGE;
    private final int Y_RANGE;

    public Tier(final int maxSpeed, final int xzRange, final int yRange)
    {
        this.MAX_SPEED = maxSpeed;
        this.XZ_RANGE = xzRange;
        this.Y_RANGE = yRange;
    }

    public int getMaxSpeed() { return MAX_SPEED; }

    public int getXZRange() { return XZ_RANGE; }

    public int getYRange() { return Y_RANGE; }
}