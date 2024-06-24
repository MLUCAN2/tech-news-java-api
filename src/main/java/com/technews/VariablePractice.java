package com.technews;

public class VariablePractice {
    public static void main(String[] args){
        int a= 365;
        double b= 4.2;
        String c= "-Time in days it takes to process your VA claim";

        //calculating int and double will give you a double type for your sum
        double sum= a+b;

        //combining a string with an int will give you a concat variable
        String concat= a+c;

        //Method to log
        System.out.println(sum);

        System.out.println(concat);
    }
}
