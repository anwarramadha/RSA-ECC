import java.util.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.lang.Math;
import java.math.BigInteger; 
import java.io.*;

public class RSA 
{
	BigInteger p;
	BigInteger q;
	BigInteger n;
	BigInteger toitent;
	BigInteger privateKey;
	BigInteger publicKey;
	String plaintext;
	String ciphertext; 

	public RSA() {
		p = generate_prime_number();
		q = generate_prime_number();
		while (p.compareTo(q) == 0) {
			q = generate_prime_number();
		}
	}

	public BigInteger generate_prime_number() {
		Random rand = new Random();
		int bitLength = 512;
		return BigInteger.probablePrime(bitLength, rand);
	}

	public void calculateN() {
		n = p.multiply(q);
	}

	public void calculateToitentEuler() {
		toitent = p.add(BigInteger.ONE.negate()).multiply(q.add(BigInteger.ONE.negate()));
	}

	public void generatePublicKey() {
		Random rand = new Random();
		int bitLength = 512;

		BigInteger e = BigInteger.probablePrime(bitLength, rand);
		while (e.gcd(toitent).compareTo(BigInteger.ONE) > 0 && e.compareTo(toitent) < 0) {
			e = e.add(BigInteger.ONE);
		}

		publicKey = e;
	}

	public void generatePrivateKey() {
		privateKey = publicKey.modInverse(toitent);
	}

	public void setPlaintext(String plaintext) {
		this.plaintext = plaintext;
	}

	public void setCiphertext(String ciphertext) {
		this.ciphertext = ciphertext;
	}

	public void setPrivateKey(BigInteger privateKey) {
		this.privateKey = privateKey;
	}

	public void setN(BigInteger n) {
		this.n = n;
	}

	private String stringToASCII(String msg) {
		StringBuilder asciiString = new StringBuilder();
		char[] msgString = msg.toCharArray();

		for (int i = 0; i < msg.length(); i++) {
			int ascii = (int) (char)msgString[i];
			String asciiInString = String.valueOf(ascii);

			if (ascii < 10)
				asciiInString = "00"+String.valueOf(ascii);
			else if (ascii >= 10 && ascii < 100)
				asciiInString = "0"+String.valueOf(ascii);

			asciiString.append(asciiInString);

		}
		return asciiString.toString();
	}

	private String[] groupASCII(String msg) {
		double blockSize = Math.ceil((double) msg.length()/3);
		String [] asciiGroup = new String[(int)blockSize];
		char[] msgString = msg.toCharArray();

		for (int i = 0; i < blockSize; i++)
			asciiGroup[i] = "";

		int i = 0, blockIdx = 0;

		while (blockIdx < blockSize) {
			if (i + 3 <= msg.length()){
				for (int it = 0; it < 3; it ++) {
					asciiGroup[blockIdx] += String.valueOf(msgString[i]);
					i++;
				}
			}
			else{
				int it0 = (i + 3) - msg.length();
				for (int add0 = 0; add0 < it0; add0 ++){
					asciiGroup[blockIdx] += "0";
				}
				
				int itchar = msg.length() % 3;
				for (int addChar = 0; addChar < itchar; addChar++) {
					asciiGroup[blockIdx] += msgString[i];
					i++;
				}
			}
			System.out.println(asciiGroup[blockIdx]);
			blockIdx ++;
		}

		return asciiGroup;
	}

	public void encrypt() {

		String[] asciiGroup = groupASCII(stringToASCII(ciphertext));
		double blockSize = asciiGroup.length;
		String[] ciphertextASCII = new String[(int)blockSize];

		for (int i = 0; i < blockSize; i++) {
			BigInteger cipher = new BigInteger(asciiGroup[i]).modPow(publicKey, n);
			String asciiInString = String.valueOf(cipher.toString(16));

			if (cipher.compareTo(BigInteger.valueOf(10)) < 0)
				asciiInString = "00"+String.valueOf(cipher);
			else if (cipher.compareTo(BigInteger.valueOf(10)) >= 0 && cipher.compareTo(BigInteger.valueOf(100)) < 0)
				asciiInString = "0"+String.valueOf(cipher);

			ciphertextASCII[i] = asciiInString;
		}

		ciphertext = String.join(" ", ciphertextASCII);
	}

	public void decrypt() {
		String[] asciiGroup = ciphertext.split(" ");

		double blockSize = asciiGroup.length;
		char[] plaintextChar = new char[(int)blockSize];

		for (int i = 0; i < blockSize - 1; i++) {
			BigInteger cipher = new BigInteger(asciiGroup[i]).modPow(privateKey, n);
			String asciiInString = String.valueOf(cipher);

			plaintextChar[i] = (char)Integer.parseInt(asciiInString);
		}

		plaintext = new String(plaintextChar);
	}

	private static void writer(String filename, String text) {
		FileWriter out = null;

		try {
			
			File file = new File(filename);
			out = new FileWriter(file);
			out.write(text);
		}
		catch(IOException e) {
			System.out.println(e);
		}
		finally {
			try {
				out.close();
			}
			catch(Exception e) {

			}
		}
	}

	private static String reader(String filename) {
		BufferedReader br = null;
		FileReader fr = null;


		String text = "";
		try {

			//br = new BufferedReader(new FileReader(FILENAME));
			fr = new FileReader(filename);
			br = new BufferedReader(fr);

			String sCurrentLine;

			while ((sCurrentLine = br.readLine()) != null) {
				text = sCurrentLine;
			}

			return text;

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (br != null)
					br.close();

				if (fr != null)
					fr.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}

		}

		return text;
	}

	public static void writeKey(String key, int keytype) { // 1 for pub, 2 for pri
		String ext = keytype == 1 ? "public.pub" : "private.pri";
		// DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

		// // Get the date today using Calendar object.
		// Date today = Calendar.getInstance().getTime();        
		// // Using DateFormat format method we can create a string 
		// // representation of a date with the defined format.
		// String reportDate = df.format(today);
		RSA.writer(ext, key);
		
	}

	public static void writeText(String text) { // 1 for pub, 2 for pri
		FileWriter out = null;
		// Create an instance of SimpleDateFormat used for formatting 
		// the string representation of date (month/day/year)
		// DateFormat df = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");

		// // Get the date today using Calendar object.
		// Date today = Calendar.getInstance().getTime();        
		// // Using DateFormat format method we can create a string 
		// // representation of a date with the defined format.
		// String reportDate = df.format(today);
		
		RSA.writer("ciphertext", text);
	}

	public void readKey(String filename, int keytype) {
		String [] keyPair = RSA.reader(filename).split(","); 
		if (keytype == 1 ) {
			publicKey =  new BigInteger(keyPair[0], 16);
		}
		else if (keytype == 2) {
			privateKey = new BigInteger(keyPair[0], 16);
		}

		n = new BigInteger(keyPair[1], 16);
	}

	public void readText(String filename, int texttype) {
		String textFromFile = RSA.reader(filename);
		String [] textArr = textFromFile.split(" ");
		String text = "";

		for (int i = 0; i < textArr.length; i++) {
			BigInteger textInNumber = new BigInteger(textArr[i], 16);
			text += textInNumber.toString() + " ";
		}
		if (texttype == 1)
			plaintext = text;
		else if (texttype == 2)
			ciphertext = text;
	}

	public static void main(String[] args) {
		RSA rsa = new RSA();
		rsa.calculateN();
		rsa.calculateToitentEuler();
		rsa.generatePublicKey();
		rsa.generatePrivateKey();

		System.out.println("P: "+rsa.p);
		System.out.println("Q: "+rsa.q);
		System.out.println("N: "+rsa.n);
		System.out.println("toitent: "+rsa.toitent);
		System.out.println("Private: "+rsa.privateKey);
		System.out.println("public: "+rsa.publicKey);
		RSA.writeKey(rsa.publicKey.toString(16) + "," + rsa.n.toString(16), 1);
		RSA.writeKey(rsa.privateKey.toString(16) + "," + rsa.n.toString(16), 2);
		rsa.ciphertext = "In JavaSW, there are a multitude of ways to write a String to a File. Perhaps the cleanest, most succinct solution to write a String to a File is through the use of the FileWriter. With this class, you pass its constructor the File object that you'd like to write to, and then you call its write method to write strings of data to the file. When done, you can flush and close the FileWriter, and you're done. In the example below, the WriteStringToFile1 class writes 'This is a test' to a file called 'test1.txt'.";
		rsa.encrypt();
		System.out.println(rsa.ciphertext);
		RSA.writeText(rsa.ciphertext);
		rsa.readText("ciphertext", 2);
		rsa.readKey("private.pri", 2);
		rsa.readKey("public.pub", 1);
		rsa.decrypt();
		System.out.println(rsa.plaintext);
		rsa.decrypt();
		System.out.println(rsa.plaintext);
	}
}