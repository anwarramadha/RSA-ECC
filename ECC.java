import java.util.*;
import java.math.BigInteger;

public class ECC 
{
	private List pointList;

	public ECC() {
		pointList = new ArrayList();
	}

	public boolean checkSquareRoot(double number) {
		return Math.sqrt(number) % 1 == 0;
	}

	public void createGaloisField() {
		Point [] points = new Point[2];
		Point p = new Point();
		Random rand = new Random();
		int bitLen = 160;

		for(int i = 0; i < 10; i ++) {
			p.setX(BigInteger.valueOf(i));
			double d = p.getX().pow(3).add(p.getX()).add(BigInteger.valueOf(6)).doubleValue();
			System.out.println(p.getX().pow(3).add(p.getX()).add(BigInteger.valueOf(6)).doubleValue());

			if (checkSquareRoot(d)) {
				p.setY(BigInteger.valueOf((int) Math.sqrt(d)).mod(BigInteger.valueOf(11)));
				points[0] = p;
				p.setY(BigInteger.valueOf((int) Math.sqrt(d)).negate().mod(BigInteger.valueOf(11)));
				points[1] = p;
			}
			else {
				p.setY(BigInteger.ZERO);
				points[0] = p;
				points[1] = p;
			}

			pointList.add(points);
		}
	}

	public void calculateQ() {

	}
	
	public static void main(String [] args) {
		ECC e = new ECC();
		e.createGaloisField();

	}
}