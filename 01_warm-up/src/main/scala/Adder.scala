// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package adder

import chisel3._
import chisel3.util._


/** 
  * Half Adder Class 
  * 
  * Your task is to implement a basic half adder as presented in the lecture.
  * Each signal should only be one bit wide (inputs and outputs).
  * There should be no delay between input and output signals, we want to have
  * a combinational behaviour of the component.
  */
class HalfAdder extends Module{
  
  val io = IO(new Bundle {
     //TODO: Define IO ports of a half adder as presented in the lecture
     val a = Input(Bool())
     val b = Input(Bool())

     //outputs
     val sum =Output(Bool())
     val carry=Output(Bool())
     
    })

 //TODO: Describe output behaviour based on the input values
  io.sum := io.a ^ io.b
  io.carry := io.a & io.b  

}

/** 
  * Full Adder Class 
  * 
  * Your task is to implement a basic full adder. The component's behaviour should 
  * match the characteristics presented in the lecture. In addition, you are only allowed 
  * to use two half adders (use the class that you already implemented) and basic logic 
  * operators (AND, OR, ...).
  * Each signal should only be one bit wide (inputs and outputs).
  * There should be no delay between input and output signals, we want to have
  * a combinational behaviour of the component.
  */
class FullAdder extends Module{

  val io = IO(new Bundle {
  // TODO: Define IO ports of a half adder as presented in the lecture
  //inputs
  val a = Input(Bool())
  val b = Input(Bool())
  val cin = Input(Bool())

  //outputs 
 val sum = Output(Bool())
 val carry = Output(Bool())
    })


 //TODO: Instanciate the two half adders you want to use based on your HalfAdder class
  val ha1 = Module(new HalfAdder())
  val ha2 = Module(new HalfAdder())

  // First Half Adder
  ha1.io.a := io.a
  ha1.io.b := io.b

  // Second Half Adder
  ha2.io.a := ha1.io.sum
  ha2.io.b := io.cin

// TODO: Describe output behaviour based on the input values and the internal signals
  // Final Outputs
  io.sum := ha2.io.sum
  io.carry := ha1.io.carry | ha2.io.carry

}

/** 
  * 4-bit Adder class 
  * 
  * Your task is to implement a 4-bit ripple-carry-adder. The component's behaviour should 
  * match the characteristics presented in the lecture.  Remember: An n-bit adder can be 
  * build using one half adder and n-1 full adders.
  * The inputs and the result should all be 4-bit wide, the carry-out only needs one bit.
  * There should be no delay between input and output signals, we want to have
  * a combinational behaviour of the component.
  */
class FourBitAdder extends Module{

  val io = IO(new Bundle {
   //TODO: Define IO ports of a 4-bit ripple-carry-adder as presented in the lecture
      val a = Input(UInt(4.W))
    val b = Input(UInt(4.W))

    // Outputs
    val sum = Output(UInt(4.W))
    val carry = Output(Bool())

    })

//* TODO: Instanciate the full adders and one half adderbased on the previously defined classes
    val ha = Module(new HalfAdder())

  val fa1 = Module(new FullAdder())
  val fa2 = Module(new FullAdder())
  val fa3 = Module(new FullAdder())

  // First stage (LSB)
  ha.io.a := io.a(0)
  ha.io.b := io.b(0)

  // Second stage
  fa1.io.a := io.a(1)
  fa1.io.b := io.b(1)
  fa1.io.cin := ha.io.carry

  // Third stage
  fa2.io.a := io.a(2)
  fa2.io.b := io.b(2)
  fa2.io.cin := fa1.io.carry

  // Fourth stage (MSB)
  fa3.io.a := io.a(3)
  fa3.io.b := io.b(3)
  fa3.io.cin := fa2.io.carry


  //TODO: Describe output behaviour based on the input values and the internal 
    // Output connections
  io.sum := Cat(
  fa3.io.sum,
  fa2.io.sum,
  fa1.io.sum,
  ha.io.sum
)

  io.carry := fa3.io.carry
}
