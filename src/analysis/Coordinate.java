package analysis;

/** Simple data class for holding fixation coordinate information. */
public class Coordinate {
	/** X-coordinate */
	public double x;
	/** Y-coordinate */
	public double y;
	/** Fixation Id */
	public int fid;

	public Coordinate(double x, double y, int fid) {
		this.x = x;
		this.y = y;
		this.fid = fid;
	}
}
