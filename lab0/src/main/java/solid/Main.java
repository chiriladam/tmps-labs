package solid;

import solid.dip.DipDemo;
import solid.ocp.OcpDemo;
import solid.srp.SrpDemo;

/**
 * Entry point: runs the demos for all 3 principles.
 * Each demo can also be run on its own (each has its own main method).
 */
public class Main {
    public static void main(String[] args) {
        SrpDemo.main(args);
        System.out.println();
        OcpDemo.main(args);
        System.out.println();
        DipDemo.main(args);
    }
}
