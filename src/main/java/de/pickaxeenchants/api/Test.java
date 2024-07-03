package de.pickaxeenchants.api;


import org.apfloat.Apfloat;
import org.apfloat.ApfloatMath;

public class Test {


    public static void main(String[] args) {


        int base = 50;

        int f = 20;

        int amount = 0;

        int level = 10000000;
        int cur_level = 10;

        amount = level*base+f*2*level+base*level;

        System.out.println(amount);



    }


}
