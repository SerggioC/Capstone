package com.sergiocruz.capstone;

import org.junit.Test;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UnitTests {



    @Test
    public void sequenceTest() {
        int limit = 10000;
        int count = 0;
        int sequence = 3004;
        while (sequence != 1) {
            if (sequence % 2 == 0) {
                sequence /= 2;
                System.out.println("Halving: " + sequence);
            } else {
                sequence = 3 * sequence + 1;
                System.out.println("Triple+1: " + sequence);
            }
            count++;
            if (count >= limit){
                break;
            }
        }
        System.out.println("Operations: " + count);

    }

    @Test
    public void testit() {
        int i = 4;
        int j = 1;
        int k = 0;
        for (int l = 0; l < 3; l++) {
            k += j;
            j = 1 - j;
        }
        System.out.print(k);
    }

    @Test
    public void leetCodeStringToInt() {
        String input = "  jhyg  -123456789101112";
        input = "-91283472332";
        String regex = "^([ ]*([+-])?[0-9]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher match = pattern.matcher(input);

        String result = "";
        while (match.find()) {
            result = match.group(0).trim();
        }
        System.out.println("result =" + result);

        if (result == null || result.length() == 0) {
            return; // return 0
        }


        BigInteger bigNumber = new BigInteger(result);
        Integer number;
        BigInteger maxValue = BigInteger.valueOf(Integer.MAX_VALUE);
        BigInteger minValue = BigInteger.valueOf(Integer.MIN_VALUE);
        if (bigNumber.compareTo(maxValue) > 0) {
            number = Integer.MAX_VALUE;
        } else if (bigNumber.compareTo(minValue) < 0) {
            number = Integer.MIN_VALUE;
        } else {
            number = bigNumber.intValue();
        }

        System.out.println("Int.min =" + Integer.MIN_VALUE);
        System.out.println("number =" + number);


    }


    @Test
    public void isEmailValid() {
        String email = "sergio@mail.pt";
        email = "12-*/-*/3@123.12.´+º´º´+3";
        Pattern pattern = Pattern.compile(".+@.+\\..+");
        boolean matches = pattern.matcher(email).matches();
        System.out.println("matches = " + matches + "\n" +
                email + " is " + (matches ? "valid" : "invalid"));

    }

    public void utopianTree(int n) {
        boolean duplicate = true;
        int height = 1;

        for (int i = 0; i < n; i++) {
            if (duplicate) {
                height = height * 2;
            } else {
                height = height + 1;
            }
            duplicate = !duplicate;
        }
    }


    @Test /* Easy */
    public void wordHighlightArea() {
        String word = "abcdefghi";

        // char a -97

        int[] asciis = word.chars().toArray();
        //Stream.of(word.toCharArray()).map(chars -> (int[]) chars);

        int[] h = new int[]{1, 2, 4, 5, 70, 1, 2, 4, 5, 7, 1, 2, 4, 5, 7, 1, 2, 4, 5, 7, 1, 2, 4, 5, 7, 7};


        int max = h[0];
        for (int ascii : asciis) {
            if (h[ascii - 97] > max) {
                max = h[ascii - 97];
            }
        }
        System.out.println(max);
        int area = max * word.length();


    }


    @Test /* Easy */
    public void hurdleRace() {

        int k = 1;
        int[] height = new int[]{1, 2, 3};
        Arrays.sort(height);

        int potions = height[0] - k;

    }

    @Test
    public void climbing() {
        int[] scores = new int[]{100, 100, 50, 40, 40, 20, 10};
        int[] uniqueScores = IntStream.of(scores).distinct().toArray();
        int[] alice = new int[]{99, 98};
        int[] aliceRanks = new int[alice.length];
        int r = 0;
        int i = uniqueScores.length - 1;
        for (int score : alice) {
            while (i >= 0) {
                if (score >= uniqueScores[i]) i--;
                else {
                    int rank = i + 2;
                    aliceRanks[r] = rank;
                    System.out.println(rank);
                    break;
                }
            }
            if (i < 0) {
                aliceRanks[r] = 1;
                System.out.println(1);
            }
            r++;
        }

    }

    @Test
    public void climbingLeaderBoard() {
        int[] scores = new int[]{100, 100, 50, 40, 40, 20, 10};
        //int[] scores = new int[]{100, 100, 100};
        //int[] alice = new int[]{21, 21, 21};
        //int[] scores = new int[]{100, 20, 20, 20};
        int[] alice = new int[]{99, 98};
        int highScore = scores[0];
        int lowestScore = scores[scores.length - 1];
        int[] aliceRanks = new int[alice.length];

        for (int o = 0; o < alice.length; o++) {
            int aliceScore = alice[o];
            int aliceRank = 1;
            int rank = 1;

            if (aliceScore > highScore) {
                aliceRank = 1;
            } else if (aliceScore < lowestScore && scores.length == 1) {
                aliceRank = 2;
            } else {
                int previousHigh = highScore;
                for (int i = 0; i < scores.length; i++) {
                    int thisScore = scores[i];

                    if (thisScore < previousHigh) {
                        previousHigh = thisScore;
                        rank++;
                    }

                    if (aliceScore < thisScore) {
                        aliceRank = rank + 1;
                    } else if (aliceScore > thisScore) {
                        if (previousHigh != thisScore)
                            aliceRank = rank - 1;
                        break;
                    } else if (aliceScore == thisScore) {
                        aliceRank = rank;
                        break;
                    }
                }

            }

            aliceRanks[o] = aliceRank;
            System.out.println("aliceRank = " + aliceRank);

        }


    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    public void EOF_Test() {

        String input = "Hello World! 3 + 3.0 = 6";

        // create a new scanner with the specified String Object
        Scanner scanner = new Scanner(input);

        int line = 0;

        while (scanner.hasNext()) {
            System.out.println("line " + line + " " + scanner.nextLine());
            line++;
        }

    }

    @Test
    public void scanTypes() {
        Scanner scan = new Scanner(System.in);
        String s = scan.next();
        double d = scan.nextDouble();
        int i = scan.nextInt();
    }


    @Test
    public void superDigit() {
        int soma = digitSum("148", 3);
        System.out.println("soma = " + soma);
    }

    public int digitSum(String integerStr, int k) {
        if (k < 1) {
            String concatenate = integerStr;
            for (int i = 1; i < k; i++) {
                integerStr += concatenate;
            }
        }

        if (integerStr.length() == 1) {
            return Integer.parseInt(integerStr);
        }


        Long sum = 0L;

        char[] digits = String.valueOf(integerStr).toCharArray();

        for (int i = 0; i < digits.length; i++) {
            sum = sum + k * Integer.parseInt(String.valueOf(digits[i]));
        }

        if (sum >= 10) {
            return digitSum(String.valueOf(sum), 1);
        } else {
            return sum.intValue();
        }

    }


    @Test
    public void calendar() {


        String input_date = "01/08/2012";
        SimpleDateFormat format = new SimpleDateFormat("dd MM yyyy");
        Date date = null;
        try {
            date = format.parse(input_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        DateFormat format2 = new SimpleDateFormat("EEEE");
        String week = format2.format(date);

        System.out.println(week);

    }


}


