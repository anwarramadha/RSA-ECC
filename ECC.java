import java.util.*;
import java.math.*;

public class ECC 
{

	private static final BigInteger TWO = BigInteger.valueOf(2);
	private BigInteger a;
	private BigInteger b;
	private BigInteger mod;
	private int bitLen = 8;
	private BigInteger privateKey;
	private Point publicKey;
	private Point base;
	private BigInteger k;

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
		mod = BigInteger.probablePrime(bitLen, rand);
		a = new BigInteger(8, rand);
		b = new BigInteger(8, rand);
		// mod = BigInteger.valueOf(5);
		// a = BigInteger.valueOf(2);
		// b = BigInteger.valueOf(1);

		// cek infinity
		while (BigInteger.valueOf(4).multiply(a.pow(3)).add(BigInteger.valueOf(27).multiply(b.pow(2))).compareTo(BigInteger.ZERO) == 0) {
			a = new BigInteger(8, rand);
			b = new BigInteger(8, rand);
		}
	}

	// public void createGaloisField() {
	// 	Random rand = new Random();

	// 	BigInteger x = BigInteger.ZERO;

	// 	while (x.compareTo(mod) < 0){
	// 		Point p = new Point();

	// 		p.setX(x);
	// 		BigInteger ySquare = p.getX().pow(3).add(a.multiply(p.getX())).add(b);
	// 		BigInteger yRootSquare = sqrt(ySquare);

	// 		if (yRootSquare.pow(2).compareTo(ySquare) == 0) {
	// 			p.setY(yRootSquare.mod(mod));
	// 			pointList.add(p);

	// 			System.out.println("X: "+p.getX()+" Y: "+p.getY());
	// 			p.setY(yRootSquare.negate().mod(mod));

	// 			pointList.add(p);
	// 			System.out.println("X: "+p.getX()+" Y: "+p.getY());
	// 		}
	// 		else {
	// 			BigInteger yRootSquareWithMod = sqrt(ySquare.mod(mod));
	// 			if (yRootSquareWithMod.pow(2).compareTo(ySquare.mod(mod)) == 0) {
	// 				p.setY(yRootSquareWithMod.mod(mod));
	// 				pointList.add(p);

	// 				System.out.println("X: "+p.getX()+" Y: "+p.getY());
	// 				p.setY(yRootSquareWithMod.negate().mod(mod));

	// 				pointList.add(p);
	// 				System.out.println("X: "+p.getX()+" Y: "+p.getY());
	// 			}
	// 		}

	// 		x = x.add(BigInteger.ONE);
			
	// 	}

	// 	// System.out.println("Point list");
	// 	// printPointList();
	// }

	public BigInteger solveY(BigInteger x) {

		BigInteger ySquare = x.pow(3).add(a.multiply(x)).add(b);
		BigInteger yRootSquare = sqrt(ySquare);

		if (yRootSquare.pow(2).compareTo(ySquare) == 0) {
			return yRootSquare.mod(mod);
		}
		else {
			BigInteger yRootSquareWithMod = sqrt(ySquare.mod(mod));
			if (yRootSquareWithMod.pow(2).compareTo(ySquare.mod(mod)) == 0) {
				return yRootSquareWithMod.mod(mod);
			}
		}

		return BigInteger.valueOf(-1);
	}

	public Point add(Point p, Point q) {
		if(p.getX().compareTo(q.getX()) == 0 && p.getY().compareTo(q.getY()) == 0){
			return doublePoint(p);
		}

		if(p.getX().compareTo(q.getX()) == 0) {
			Point r = new Point();
			return r;
		}

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

	private void selectBase() {
		boolean found = false;
		BigInteger x = BigInteger.ZERO;
		BigInteger y;
		base = new Point();

		while (!found || x.compareTo(mod) < 0) {
			y = solveY(x);
			if (y.compareTo(BigInteger.valueOf(-1)) != 0) {
				base.setX(x);
				base.setY(y);
				found = true;
			}

			x = x.add(BigInteger.ONE);
		}
	}

	public void createPrivateKey() {
		Random rand = new Random();
		privateKey = new BigInteger(mod.bitLength(), rand);
		System.out.println(privateKey);
		while (privateKey.compareTo(mod) > 0) {
			privateKey = privateKey.subtract(BigInteger.ONE);
		}
	}

	public void createPublicKey() {
		BigInteger i = BigInteger.ZERO;
		publicKey = new Point();
		System.out.println("X: "+base.getX()+" Y: "+base.getY());
		while (i.compareTo(privateKey) < 0) {
			publicKey = add(publicKey, base);
			i = i.add(BigInteger.ONE);
		}
	}

	public BigInteger getPrivateKey() {
		return privateKey;
	}

	public Point getPublicKey() {
		return publicKey;
	}

	public Point getBase() {
		return base;
	}

	public void selectK() {
		Random rand = new Random();
		k = new BigInteger(bitLen, rand);

		while (k.compareTo(mod) > 0) {
			k = k.subtract(BigInteger.ONE);
		}
	}

	public static void main(String [] args) {
		ECC e = new ECC();
		e.generateEquation();
		e.selectBase();
		e.selectK();
		System.out.println("X: "+e.getBase().getX()+" Y: "+e.getBase().getY());
	}
}