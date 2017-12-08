
import mains.Assignment1;
import mains.Assignment2;
import mains.Assignment3;
import mains.Assignment4;

import java.util.Scanner;

/**
 * @author Francisco Lopes 76406
 * @author Pedro Gusmão 77867
 */
public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        String op = "";

        while (!op.equals("y") && !op.equals("n") && !op.equals("yes") && !op.equals("no")) {
            System.out.println("Run Current Assignment? (y/n)\n(No allows to choose previous assignments.)");
            op = sc.nextLine();
            op = op.toLowerCase();

            switch (op) {
                case "y":
                case "yes":
                    System.out.println();
                    Assignment4.main();
                    break;
                case "n":
                case "no":
                    String assignment = "";
                    while (!assignment.equals("1") && !assignment.equals("2") && !assignment.equals("3") && !assignment.equals("4") && !assignment.equals("5")) {
                        System.out.println("Choose what assignment to run.");
                        System.out.println("1 - Assignment 1.");
                        System.out.println("2 - Assignment 2.");
                        System.out.println("3 - Assignment 3.");
                        System.out.println("4 - Assignment 4.");
                        System.out.println("5 - Exit.");
                        assignment = sc.nextLine();

                        switch (assignment) {
                            case "1":
                                System.out.println();
                                Assignment1.main();
                                break;
                            case "2":
                                System.out.println();
                                Assignment2.main();
                                break;
                            case "3":
                                System.out.println();
                                Assignment3.main();
                                break;
                            case "4":
                                System.out.println();
                                Assignment4.main();
                                break;
                            case "5":
                                System.exit(0);
                                break;
                            default:
                                System.err.println("Invalid option. Choose option 1 to 5");
                                break;
                        }


                    }

                    break;
                default:
                    System.out.println("Invalid option. Only yes or no answer.");
                    break;
            }

        }






    }
}
