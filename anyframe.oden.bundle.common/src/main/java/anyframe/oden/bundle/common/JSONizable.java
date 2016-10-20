package anyframe.oden.bundle.common;

/**
 * If there some classes implementing this, it means it can generate
 * JSON output.
 * 
 * @author joon1k
 *
 */
public interface JSONizable {
	public Object jsonize();
}
