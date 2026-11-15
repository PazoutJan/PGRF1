package interaction;

public final class Selection {
    public enum Target {
        NONE,
        POLYGON_POINT,
        LINE_ENDPOINT,
        CLIPPING_LINE_ENDPOINT
    }

    public enum Endpoint {
        START,
        END
    }

    private static final Selection NONE = new Selection(Target.NONE, -1, null);

    private final Target target;
    private final int index;
    private final Endpoint endpoint;

    private Selection(Target target, int index, Endpoint endpoint) {
        this.target = target;
        this.index = index;
        this.endpoint = endpoint;
    }

    public static Selection none() {
        return NONE;
    }

    public static Selection polygonPoint(int index) {
        return new Selection(Target.POLYGON_POINT, index, null);
    }

    public static Selection lineEndpoint(int index, Endpoint endpoint) {
        return new Selection(Target.LINE_ENDPOINT, index, endpoint);
    }

    public static Selection clippingLineEndpoint(int index, Endpoint endpoint) {
        return new Selection(Target.CLIPPING_LINE_ENDPOINT, index, endpoint);
    }

    public Target getTarget() {
        return target;
    }

    public int getIndex() {
        return index;
    }

    public Endpoint getEndpoint() {
        return endpoint;
    }

    public boolean isSelected() {
        return target != Target.NONE;
    }
}
