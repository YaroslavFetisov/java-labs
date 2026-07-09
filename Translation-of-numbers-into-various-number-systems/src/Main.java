import java.util.Scanner;

public class Main {

    public final static char[] numbers = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static void main(String args[]) {

        Scanner scan = new Scanner(System.in);
        System.out.print("p: ");
        int p = scan.nextInt();
        System.out.print("q: ");
        int q = scan.nextInt();
        System.out.print("R: ");
        String data = scan.next();
        boolean hasMinus = false;

        if (data.startsWith("-")) {
            hasMinus = true;
            data = data.substring(1);
        }

        String[] dataSpl = data.split("[,.]");
        String dataInt = dataSpl[0];

        if (dataSpl.length == 1) {
            char[] dataIntArray = dataInt.toCharArray();
            int res10Int = 0;
            int numberValue = 0;

            for (int i = 0; i < dataIntArray.length; i++) {
                for (int j = 0; j < numbers.length; j++) {
                    if (numbers[j] == dataIntArray[i]) {
                        numberValue = j;
                    }
                }
                res10Int += numberValue * Math.pow(p, (dataIntArray.length - i - 1));
            }

            StringBuilder sb = new StringBuilder();

            for (; res10Int / q > 0; res10Int /= q) {
                sb.append(numbers[res10Int % q]);
            }
            sb.reverse();
            sb.insert(0, numbers[res10Int % q]);
            System.out.println("Answer: " + sb);

        } else {
            char[] dataIntArray = dataInt.toCharArray();
            int res10Int = 0;
            int numberValue = 0;

            for (int i = 0; i < dataIntArray.length; i++) {
                for (int j = 0; j < numbers.length; j++) {
                    if (numbers[j] == dataIntArray[i]) {
                        numberValue = j;
                    }
                }
                res10Int += numberValue * Math.pow(p, dataIntArray.length - i - 1);
            }

            StringBuilder sb = new StringBuilder();

            for (; res10Int / q > 0; res10Int /= q) {
                sb.append(numbers[res10Int % q]);
            }
            sb.reverse();
            sb.insert(0, numbers[res10Int % q]);
            String numberQInt = sb.toString();

            String dataFrac = dataSpl[1];
            char[] dataFracArray = dataFrac.toCharArray();
            double res10Frac = 0;
            numberValue = 0;

            for (int i = 0; i < dataFracArray.length; i++) {
                for (int j = 0; j < numbers.length; j++) {
                    if (numbers[j] == dataFracArray[i]) {
                        numberValue = j;
                    }
                }
                res10Frac += numberValue * Math.pow(p, -i - 1);
            }

            StringBuilder sb2 = new StringBuilder();

            for (int i = 0; i < 15; i++) {
                res10Frac = (res10Frac * q - (int) res10Frac * q);
                sb2.append(numbers[((int) res10Frac)]);
            }
            String numberQFrac = sb2.toString();

            if (hasMinus) {
                numberQInt = "-" + numberQInt;
            }
            System.out.println("Answer: " + numberQInt + "." + numberQFrac);
        }
    }
}
