import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;

public class Vigenere {
	private String mKey;

	public Vigenere(String key) {
		this.mKey = key;
	}

	public String encrypt(String M) {
		String C = "";

		for (int i = 0; i < M.length(); i++) {
			int t = (M.charAt(i) - 'a' + mKey.charAt(i % mKey.length()) - 'a') % 26 + 'a';
			C += alp.charAt((alp.indexOf(M.charAt(i)) + alp.indexOf(mKey.charAt(i%mKey.length())))%alp.length());
		}

		return C;

	}

	public String decrypt(String C) {
		String M = "";

		for (int i = 0; i < C.length(); i++) {
			int t = (C.charAt(i) - 'a' - mKey.charAt(i % mKey.length()) + 'a' + 26) % 26 + 'a';
			M += alp.charAt((alp.length() + alp.indexOf(C.charAt(i)) - alp.indexOf(mKey.charAt(i%mKey.length())))%alp.length());
		}

		return M;
	}

	public static String getRandomKey(int length) {
		String base = "abcdefghijklmnopqrstuvwxyz";
		Random random = new Random();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int number = random.nextInt(base.length());
			sb.append(base.charAt(number));
		}
		return sb.toString();
	}

	public static String getKey(String C) {
		int keyLen = findKeyLen1(C);

		double[][] prob = probability(C, keyLen);

		char[] key = new char[keyLen];

		for (int i = 0; i < keyLen; i++) {
			double[] sumA = new double[26];

			for (int n = 0; n < 26; n++) {
				double cost = 0;

				for (int k = 0; k < 26; k++) {
					double local = Math.abs(prop[k] - prob[i][(k + n) % 26]);
					cost += local;
				}
				sumA[n] = cost;
			}

			int end = 0;
			for (int b = 1; b < 26; b++) {
				if (sumA[b] < sumA[end]) {
					end = b;
				}
			}
			key[i] = alp.charAt(end);
		}

		return new String(key);
	}

	public static final double[] prop = new double[] { 8.167, 1.492, 2.782,
			4.253, 12.702, 2.228, 2.015, 6.094, 6.966, 0.153, 0.772, 4.025,
			2.406, 6.749, 7.507, 1.929, 0.095, 5.987, 6.327, 9.056, 2.758,
			0.978, 2.360, 0.150, 1.974, 0.074 };
	public static final String alp = "abcdefghijklmnopqrstuvwxyz";

	public static double[][] probability(String C, int keyLen) {
		String[] subStr = new String[keyLen];
		for (int i = 0; i < C.length(); i++) {
			int k = i % keyLen;

			if (i < keyLen) {
				subStr[k] = "" + C.charAt(i);
			} else {
				subStr[k] += "" + C.charAt(i);
			}
		}

		// 计算每个字母的频率
		HashMap<Integer, TreeMap<Character, Integer>> rollBack = new HashMap<Integer, TreeMap<Character, Integer>>();
		// 分组
		for (int i = 0; i < subStr.length; i++) {
			TreeMap<Character, Integer> m = new TreeMap<Character, Integer>();
			for (int n = 0; n < subStr[i].length(); n++) {
				char c = subStr[i].charAt(n);
				int value;
				if (m.containsKey(c)) {
					value = m.get(c);
				} else {
					value = 0;
				}
				value++;
				m.put(c, value);
			}
			rollBack.put(i, m);
		}

		// 计算百分比
		HashMap<Integer, TreeMap<Character, Double>> percen = new HashMap<Integer, TreeMap<Character, Double>>();
		for (Entry<Integer, TreeMap<Character, Integer>> entry : rollBack
				.entrySet()) {
			int n = entry.getKey();
			TreeMap<Character, Integer> m = entry.getValue();
			int sumM = 0;
			for (Entry<Character, Integer> c : m.entrySet()) {
				sumM += c.getValue();
			}
			TreeMap<Character, Double> zaVrsto = new TreeMap<Character, Double>();
			for (Entry<Character, Integer> c : m.entrySet()) {
				double result = (double) c.getValue() / sumM;
				result = result * 100;
				BigDecimal bd = new BigDecimal(result).setScale(3,
						RoundingMode.HALF_EVEN);
				result = bd.doubleValue();
				zaVrsto.put(c.getKey(), result);
			}
			percen.put(n, zaVrsto);
		}

		double[][] table = new double[keyLen][26];

		for (Entry<Integer, TreeMap<Character, Double>> entry : percen
				.entrySet()) {
			int type = entry.getKey();
			TreeMap<Character, Double> odstotki = entry.getValue();
			for (int i = 0; i < alp.length(); i++) {
				Character m = Character.valueOf(alp.charAt(i));
				double value;
				if (odstotki.containsKey(m)) {
					value = odstotki.get(m);
				} else {
					value = 0;
				}
				table[type][i] = value;
			}
		}

		return table;
	}

	public static boolean isMlRpt(String repeation, String C) {
		int lastIdx = 0;
		int count = 0;

		while (lastIdx != -1) {
			lastIdx = C.indexOf(repeation, lastIdx);

			if (lastIdx != -1) {
				count++;
				lastIdx += repeation.length();
			}
		}
		if (count > 1) {
			return true;
		} else {
			return false;
		}
	}

	public static int findKeyLen(String C) {
		ArrayList<String> subString = new ArrayList<String>();

		// 将重复放入ArrayList
		for (int i = 0; i < C.length() - 3; i++) {
			String repeation = C.substring(i, i + 3);

			// 检查是否重复多次
			boolean isMlRpt = isMlRpt(repeation, C);

			if (isMlRpt) {
				int n = 1;
				// 检查是否为子串
				while (isMlRpt) {
					if (i + 3 + n < C.length()) {
						String newIteration = C.substring(i, i + 3 + n);
						boolean newIsMlRpt = isMlRpt(newIteration, C);

						if (newIsMlRpt) {
							repeation = newIteration;
							n++;
						} else {
							break;
						}
					} else {
						break;
					}

				}
				// 子串添加
				if (!(subString.contains(repeation))) {
					subString.add(repeation);
				}
			}
		}

		// 找距离
		ArrayList<Integer> distance = new ArrayList<Integer>();

		for (int n = 0; n < subString.size(); n++) {
			ArrayList<Integer> dump = new ArrayList<Integer>();

			String word = subString.get(n);

			for (int i = -1; (i = C.indexOf(word, i + 1)) != -1;) {
				// 存距离
				int k = i + 1;
				dump.add(k);
			}

			// 计算差
			int dis = Math.abs(dump.get(1) - dump.get(0));
			distance.add(dis);

		}

		ArrayList<Integer> dDistance = new ArrayList<Integer>();

		for (int num : distance) {
			int integer = num;

			// 求余
			for (int count = 2; count <= integer; count++) {
				if (integer % count == 0) {
					dDistance.add(count);
				}
			}
		}

		// 对所有的进行计算
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

		// 遍历
		for (int i = 0; i < dDistance.size(); i++) {
			Integer count = map.get(dDistance.get(i));
			map.put(dDistance.get(i), count == null ? 1 : count + 1);
		}

		// 转化为可编辑的树
		TreeMap<Integer, Integer> tree = new TreeMap<Integer, Integer>();

		for (Entry<Integer, Integer> entry : map.entrySet()) {
			int MapKey = entry.getKey();
			int MapValue = entry.getValue();
			tree.put(MapKey, MapValue);
		}

		// 找最可能的key
		int maxK = -1;
		int maxV = -1;

		// 遍历树
		for (Entry<Integer, Integer> entry : tree.entrySet()) {
			// 键长度在4-20间，重复次数多
			if (entry.getKey() >= 4 && entry.getKey() <= 20
					&& entry.getValue() >= 10) {

				if (entry.getValue() > maxV) {
					maxV = entry.getValue();
					maxK = entry.getKey();
				}

			}
		}
		return maxK;

	}

	public static int[] countAlp(String text) {
		int[] count = new int[26];

		for (int i = 0; i < 26; i++) {
			count[i] = 0;
		}

		for (int i = 0; i < text.length(); i++) {
			if (-1 == alp.indexOf(text.charAt(i))) {
				System.out.println(text.charAt(i));
			}
			count[alp.indexOf(text.charAt(i))]++;
		}
		return count;
	}

	public static double calIndex(int[] count) {
		int sum = 0;
		for (int temp : count) {
			sum += temp;
		}

		double idx = 0;

		double tt[] = new double[26];

		for (int i = 0; i < 26; i++) {
			tt[i] = (double) count[i] / sum;
			idx += tt[i] * tt[i];
		}

		System.out.println("The probability distribution: "
				+ Arrays.toString(tt));
		System.out.println("The calculated index: " + idx);
		return idx;
	}
	
	public static double calIndex1(int[] count) {
		int sum = 0;
		for (int temp : count) {
			sum += temp;
		}

		double idx = 0;

		double tt[] = new double[26];

		for (int i = 0; i < 26; i++) {
			tt[i] = (double) count[i] / sum;
			idx += tt[i] * tt[i];
		}

		//System.out.println("The probability distribution: "
		//		+ Arrays.toString(tt));
		//System.out.println("The calculated index: " + idx);
		return idx;
	}

	public static int[] countStr(String text, int k, int start){
		int[] count = new int[26];
		for(int i=0;i<26;i++)
			count[i]=0;
		for(int i=start;i<text.length();){
			count[alp.indexOf(text.charAt(i))]++;
			i+=k;
		}
		return count;
	}
	
	public static int findKeyLen1(String C){
		for(int i = 1 ;i<C.length();i++){
			double value = calIndex1(countStr(C,i,0));
			if(Math.abs(value-0.065)<0.003)
				return i;
		}
		return -1;
	}

//	public static String getKey1(String C, int keyLen){
//		String key = "";
//		double[] 
//	}
}
