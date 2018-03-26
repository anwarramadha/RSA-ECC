import java.util.*;
import java.math.*;

public class ECC 
{

	private static final BigInteger TWO = BigInteger.valueOf(2);
	private ArrayList<Point> pointList;
	private BigInteger a;
	private BigInteger b;
	private BigInteger mod;
	private int bitLen = 160;
	private BigInteger privateKey;
	private Point publicKey;
	private Point base;

	public ECC() {
		pointList = new ArrayList<Point>();
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
		mod = BigInteger.valueOf(5);
		a = BigInteger.valueOf(2);
		b = BigInteger.valueOf(1);

		// cek infinity
		while (BigInteger.valueOf(4).multiply(a.pow(3)).add(BigInteger.valueOf(27).multiply(b.pow(2))).compareTo(BigInteger.ZERO) == 0) {
			a = new BigInteger(8, rand);
			b = new BigInteger(8, rand);
		}
	}

	public void createGaloisField() {
		Random rand = new Random();

		BigInteger x = BigInteger.ZERO;

		while (x.compareTo(mod) < 0){
			Point p = new Point();
			
			p.setX(x);
			BigInteger ySquare = p.getX().pow(3).add(a.multiply(p.getX())).add(b);
			BigInteger yRootSquare = sqrt(ySquare);

			if (yRootSquare.pow(2).compareTo(ySquare) == 0) {
				p.setY(yRootSquare.mod(mod));
				pointList.add(p);

				System.out.println("X: "+p.getX()+" Y: "+p.getY());
				p.setY(yRootSquare.negate().mod(mod));

				pointList.add(p);
				System.out.println("X: "+p.getX()+" Y: "+p.getY());
			}
			else {
				BigInteger yRootSquareWithMod = sqrt(ySquare.mod(mod));
				if (yRootSquareWithMod.pow(2).compareTo(ySquare.mod(mod)) == 0) {
					p.setY(yRootSquareWithMod.mod(mod));
					pointList.add(p);

					System.out.println("X: "+p.getX()+" Y: "+p.getY());
					p.setY(yRootSquareWithMod.negate().mod(mod));

					pointList.add(p);
					System.out.println("X: "+p.getX()+" Y: "+p.getY());
				}
			}

			x = x.add(BigInteger.ONE);
			
		}

		// System.out.println("Point list");
		// printPointList();
	}

	public void printPointList() {
		for (int i = 0; i < pointList.size(); i++) {
			System.out.println("X: "+pointList.get(i).getX()+" Y: "+pointList.get(i).getY());
		}
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
		Random rand = new Random();
		int idx = rand.nextInt(pointList.size() - 1);

		base = pointList.get(idx);
		System.out.println("X: "+pointList.get(idx).getX()+" Y: "+pointList.get(idx).getY());
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

	public static void main(String [] args) {
		ECC e = new ECC();
		e.generateEquation();
		e.createGaloisField();
		// if (ECC.isSqrt(BigInteger.valueOf(16), BigInteger.ONE.shiftLeft(BigInteger.valueOf(16).bitLength() / 2))) System.out.println("not ");
		// System.out.println(e.sqrt(BigInteger.valueOf(15)));
		Point p = new Point();
		Point q = new Point();
		p.setX(BigInteger.valueOf(0));
		p.setY(BigInteger.valueOf(1));
		q.setX(BigInteger.valueOf(5));
		q.setY(BigInteger.valueOf(9));
		Point r = new Point();
		// System.out.println("X: "+r.getX()+" Y: "+r.getY());
		r = e.add(p, p);
		r = e.add(r, p);
		System.out.println("X: "+r.getX()+" Y: "+r.getY());
		e.createPrivateKey();
		System.out.println(e.getPrivateKey());
		e.selectBase();
		e.createPublicKey();
		System.out.println("X: "+e.getPublicKey().getX()+" Y: "+e.getPublicKey().getY());
	}
}