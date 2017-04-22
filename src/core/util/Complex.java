/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core.util;
/*
 *  =================================================================
 *  Complex.java: A library of methods for complex number arithmetic
 *  The algoritms for arithmetic are taken from the text "Numerical
 *  Recipies in C."
 *
 *  Written By : Mark Austin                                 May 1998
 *  =================================================================
 */

import java.lang.Math;

public class Complex {
   protected double dReal, dImaginary;

   // Constructor methods ....

   public Complex() {}

   public Complex( double dReal, double dImaginary ) {
      this.dReal      = dReal;
      this.dImaginary = dImaginary;
   }

   // Convert complex number to a string ...

   public String toString() {
      if (dImaginary >= 0)
          return dReal + "+" +  dImaginary + "i";
      else
          return dReal + "-" + -dImaginary + "i";
   }

   // ==============================
   // Complex number arithmetic ...
   // ==============================

   // Complex negative of complex number ...

   public Complex Negate() {
   Complex negate = new Complex();

       negate.dReal      = - dReal;
       negate.dImaginary = - dImaginary;

       return (negate);
   }

   // Compute sum of two complex numbers cA + cB.....

   public Complex Add( Complex cB ) {
   Complex sum = new Complex();

       sum.dReal      = dReal      + cB.dReal;
       sum.dImaginary = dImaginary + cB.dImaginary;

       return (sum);
   }

   // Compute difference of two complex numbers cA - cB.....

   public Complex Sub( Complex cB ) {
   Complex diff = new Complex();

       diff.dReal      = dReal      - cB.dReal;
       diff.dImaginary = dImaginary - cB.dImaginary;

       return (diff);
   }

   // Compute product of two complex numbers cA * cB.....

   public Complex Mult( Complex cB ) {
   Complex prod = new Complex();

       prod.dReal      = dReal*cB.dReal      - dImaginary*cB.dImaginary;
       prod.dImaginary = dImaginary*cB.dReal + dReal*cB.dImaginary;

       return (prod);
   }

   // Compute divisor of two complex numbers cA / cB.....

   public Complex Div( Complex cB ) {
   Complex div = new Complex();
   double dR, dDen;
 
       if(Math.abs( cB.dReal ) >= Math.abs( cB.dImaginary )) {
          dR   = cB.dImaginary/cB.dReal;
          dDen = cB.dReal + dR*cB.dImaginary;
          div.dReal      = (dReal      + dR*dImaginary)/dDen;
          div.dImaginary = (dImaginary - dR*dReal)/dDen;
       } else {
          dR   = cB.dReal/cB.dImaginary;
          dDen = cB.dImaginary + dR*cB.dReal;
          div.dReal      = (dR*dReal      + dImaginary)/dDen;
          div.dImaginary = (dR*dImaginary - dReal)/dDen;
       }

       return (div);
   }

   // Scale complex number by double precision number.....

   public Complex Scale( double dFactor ) {
   Complex scale = new Complex();

      scale.dReal      = dFactor*dReal;
      scale.dImaginary = dFactor*dImaginary;

      return (scale);
   }

   // Compute complex number conjugate....

   public Complex Conjugate() {
   Complex conj = new Complex();

      conj.dReal      =  dReal;
      conj.dImaginary = -dImaginary;

      return (conj);
   }

   // Compute absolute value of complex number ....

   public double Abs() {
   double dX, dY, dTemp, dAnswer;

      dX = Math.abs( dReal );
      dY = Math.abs( dImaginary );
      if(dX == 0)
         dAnswer = dY;
      else if(dY == 0)
         dAnswer = dX;
      else if(dX > dY) {
         dTemp = dY/dX;
         dAnswer = dX*Math.sqrt((double) (1.0 + dTemp*dTemp));
      } else {
         dTemp = dX/dY;
         dAnswer = dY*Math.sqrt((double) (1.0 + dTemp*dTemp));
      } 

      return ( dAnswer );
   }

   // Compute square root of complex number ....

   public Complex Sqrt() {
   Complex csqrt = new Complex();
   double dX, dY, dW, dR;

      if((dReal == 0) && (dImaginary == 0.0)) {
          csqrt.dReal      = 0.0;
          csqrt.dImaginary = 0.0;
          return (csqrt);
      } 

      dX = Math.abs(dReal);
      dY = Math.abs(dImaginary);

      if( dX >= dY ) {
          dR = dY/dX;
          dW = Math.sqrt(dX)*Math.sqrt(0.5*(1.0 + Math.sqrt(1+dR*dR)));
      } else {
          dR = dX/dY;
          dW = Math.sqrt(dY)*Math.sqrt(0.5*(dR + Math.sqrt(1+dR*dR)));
      }

      if(dReal >= 0.0) {
         csqrt.dReal = dW;
         csqrt.dImaginary = dImaginary/(2.0*dW);
      } else {
         csqrt.dImaginary = (dImaginary > 0.0) ? dW : -dW;
         csqrt.dReal      =  dImaginary/(2.0*csqrt.dImaginary);
      }

      return (csqrt);
   }

   // Exercise methods in Complex class ....

  /* public static void main ( String args[] ) {

      System.out.println("Complex number test program");
      System.out.println("===========================");

      // Setup and print two complex numbers .....

      Complex cA = new Complex( 1.0, 2.0 );
      Complex cB = new Complex( 3.0, 4.0 );

      System.out.println("Complex number cA = " + cA.toString() );
      System.out.println("Complex number cB = " + cB.toString() );

      // Test complex addition and substraction .....

      Complex cC = cA.Add( cB );
      System.out.println("Complex   cA + cB = " + cC.toString() );
      Complex cD = cA.Sub( cB );
      System.out.println("Complex   cA - cB = " + cD.toString() );

      // Test complex multiplication and division .....

      Complex cE = cA.Mult( cB );
      System.out.println("Complex   cA * cB = " + cE.toString() );
      Complex cF = cA.Div( cB );
      System.out.println("Complex   cA / cB = " + cF.toString() );

      // Test complex scale function .....

      Complex cG = cA.Scale( 5.0 );
      System.out.println("Complex    5 * cA = " + cG.toString() );

      // Test absolute value function .....

      System.out.println("Complex  cA.Abs() = " + cA.Abs() );
      System.out.println("Complex  cB.Abs() = " + cB.Abs() );

      // Test complex sqrt function .....

      Complex cH = cA.Sqrt();
      System.out.println("Complex  sqrt(cA) = " + cH.toString() );
      Complex cI = cH.Mult(cH);
      System.out.println("Complex  sqrt(cA)*sqrt(cA) = " + cI.toString() );
   }*/
}
