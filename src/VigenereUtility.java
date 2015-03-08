import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

public class VigenereUtility {

	public final static String mFile = "D:/M.txt";
	public final static String CFile = "d:/C.txt";
	public final static String dFile = "d:/D.txt";

	public static void main(String[] args) {
		String M="";
		//读源文件
		try{
			File f1 = new File(mFile);
			//System.out.println(f1.exists());
			FileReader fr1 = new FileReader(f1);
			BufferedReader br1 = new BufferedReader (fr1);
			String temp="";
			while((temp = br1.readLine())!=null){
				temp = temp.replaceAll("[^a-zA-Z]","");
				temp = temp.toLowerCase();
				M+=temp;
				//System.out.println(temp);
			}
			//System.out.println(M.substring(0, 100));
			br1.close();
			fr1.close();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		//输入key长度
		System.out.println("please enter the length of key");
		Scanner sc = new Scanner(System.in);
		int keyLength = sc.nextInt();
		String key = Vigenere.getRandomKey(keyLength);
		System.out.println("Key:"+key);
		
		//加密
		Vigenere v = new Vigenere(key);
		String C = v.encrypt(M);
		
		//将加密后的文字写入文件
		try{
			File f2 = new File(CFile);
			FileWriter fw1= new FileWriter(f2);
			BufferedWriter bw1 = new BufferedWriter(fw1);
			bw1.write(C);
			bw1.close();
			fw1.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		System.out.println("Unencrypted text:");
		int[] MC = Vigenere.countAlp(M);
		Vigenere.calIndex(MC);
		
		System.out.println("Encrypted text:");
		int[] CC = Vigenere.countAlp(C);
		Vigenere.calIndex(CC);
		
		//解密
		
		String D = v.decrypt(C);
		
		//将解密后的文字写入文件
		try{
			File f3 = new File(dFile);
			FileWriter fw2= new FileWriter(f3);
			BufferedWriter bw2 = new BufferedWriter(fw2);
			bw2.write(D);
			bw2.close();
			fw2.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//破解过程
		System.out.println("Press enter \"attack\" to attack");
		sc.nextLine();
		
		String crackedKey = Vigenere.getKey(C);
		System.out.println(crackedKey);

	}

	

}
