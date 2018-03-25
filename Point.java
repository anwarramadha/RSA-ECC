import java.math.BigInteger;

public class Point 
{
	private BigInteger x;
	private BigInteger y;
	public Point() {
		x = BigInteger.valueOf(0);
		y = BigInteger.valueOf(0);
	}

	public void setX(BigInteger x) {
		this.x = x;
	}

	public void setY(BigInteger y) {
		this.y = y;
	}

	public BigInteger getX() {
		return x;
	}

	public BigInteger getY() {
		return y;
	}
}