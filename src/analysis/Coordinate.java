package analysis;

/** Simple data class for holding fixation coordinate information. */
public class Coordinate {
	/** X-coordinate */
	public final double x;
	/** Y-coordinate */
	public final double y;
	/** Fixation Id */
	public final int fid;

	public Coordinate(double x, double y, int fid) {
		this.x = x;
		this.y = y;
		this.fid = fid;
	}
}
