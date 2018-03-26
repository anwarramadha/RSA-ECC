import java.util.*;
import java.math.*;

public class ECC 
{
	private List pointList;
	private static final BigInteger TWO = BigInteger.valueOf(2);
	private BigInteger a;
	private BigInteger b;
	private BigInteger mod;
	private int bitLen = 160;

	public ECC() {
		pointList = new ArrayList();
	}

	public static BigInteger sqrt(BigInteger n)
	{
	    if (n.signum() >= 0)
	    {
	        final int bitLength = n.bitLength();
	        BigInteger root = BigInteger.ONE.shiftLeft(bitLength / 2);

	        while (!isSqrt(n, root))
	        {
	            root = root.add(n.divide(root)).divide(TWO);
	        }
	        return root;
	    }
	    else
	    {
	        throw new ArithmeticException("square root of negative number");
	    }
	}


	public static boolean isSqrt(BigInteger n, BigInteger root)
	{
	    final BigInteger lowerBound = root.pow(2);
	    final BigInteger upperBound = root.add(BigInteger.ONE).pow(2);
	    return lowerBound.compareTo(n) <= 0
	        && n.compareTo(upperBound) < 0;
	}

	public void generateEquation() {
		Random rand = new Random();
		// mod = BigInteger.probablePrime(bitLen, rand);
		// a = new BigInteger(8, rand);
		// b = new BigInteger(8, rand);
		mod = BigInteger.valueOf(11);
		a = BigInteger.ONE;
		b = BigInteger.valueOf(6);

		// cek infinity
		while (BigInteger.valueOf(4).multiply(a.pow(3)).add(BigInteger.valueOf(27).multiply(b.pow(2))).compareTo(BigInteger.ZERO) == 0) {
			a = new BigInteger(8, rand);
			b = new BigInteger(8, rand);
		}
	}

	public void createGaloisField() {
		Point [] points = new Point[2];
		Point p = new Point();
		Random rand = new Random();

		BigInteger x = BigInteger.ZERO;

		while (x.compareTo(mod) < 0){
			p.setX(x);
			BigInteger ySquare = p.getX().pow(3).add(a.multiply(p.getX())).add(b);
			BigInteger yRootSquare = sqrt(ySquare);

			if (yRootSquare.pow(2).compareTo(ySquare) == 0) {
				p.setY(yRootSquare.mod(mod));
				points[0] = p;

				System.out.println("X: "+p.getX()+" Y: "+p.getY());
				p.setY(yRootSquare.negate().mod(mod));
				points[1] = p;

				pointList.add(points);
				System.out.println("X: "+p.getX()+" Y: "+p.getY());
			}
			else {
				BigInteger yRootSquareWithMod = sqrt(ySquare.mod(mod));
				if (yRootSquareWithMod.pow(2).compareTo(ySquare.mod(mod)) == 0) {
					p.setY(yRootSquareWithMod.mod(mod));
					points[0] = p;

					System.out.println("X: "+p.getX()+" Y: "+p.getY());
					p.setY(yRootSquareWithMod.negate().mod(mod));
					points[1] = p;

					pointList.add(points);
					System.out.println("X: "+p.getX()+" Y: "+p.getY());
				}
			}

			x = x.add(BigInteger.ONE);
			// i++;
		}
	}

	public Point add(Point p, Point q) {
		BigInteger gradien = p.getY().subtract(q.getY()).multiply(p.getX().subtract(q.getX()).modInverse(mod)).mod(mod);
		Point r = new Point();

		r.setX(gradien.pow(2).subtract(p.getX()).subtract(q.getX()).mod(mod));
		r.setY(gradien.multiply(p.getX().subtract(r.getX())).subtract(p.getY()).mod(mod));
		
		return r;
	}

	public Point minus(Point p, Point q) {
		Point newQ = new Point();
		newQ.setX(q.getX());
		newQ.setY(q.getY().negate().mod(mod));

		Point r = add(p, newQ);

		return r;
	}

	public Point doublePoint(Point p) {
		BigInteger gradien = BigInteger.valueOf(3).multiply(p.getX().pow(2)).add(a).multiply(BigInteger.valueOf(2).multiply(p.getY()).modInverse(mod)).mod(mod);
		Point r = new Point();
		System.out.println(gradien);
		r.setX(gradien.pow(2).subtract(BigInteger.valueOf(2).multiply(p.getX())).mod(mod));
		r.setY(gradien.multiply(p.getX().subtract(r.getX())).subtract(p.getY()).mod(mod));

		System.out.println(r.getX());

		return r;
	} 

	public void createPrivateKey() {

	}

	public void createPublicKey() {
		
	}

	public static void main(String [] args) {
		ECC e = new ECC();
		e.generateEquation();
		e.createGaloisField();
		// if (ECC.isSqrt(BigInteger.valueOf(16), BigInteger.ONE.shiftLeft(BigInteger.valueOf(16).bitLength() / 2))) System.out.println("not ");
		// System.out.println(e.sqrt(BigInteger.valueOf(15)));
		// Point p = new Point();
		// Point q = new Point();
		// p.setX(BigInteger.valueOf(2));
		// p.setY(BigInteger.valueOf(4));
		// q.setX(BigInteger.valueOf(5));
		// q.setY(BigInteger.valueOf(9));
		// Point r = new Point();
		// // System.out.println("X: "+r.getX()+" Y: "+r.getY());
		// r = e.add(q, p);
		// System.out.println("X: "+r.getX()+" Y: "+r.getY());

	}
}