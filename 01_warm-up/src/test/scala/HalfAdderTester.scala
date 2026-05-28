// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package adder

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


/** 
  * Half adder tester
  * Use the truth table from the exercise sheet to test all possible input combinations and the corresponding results exhaustively
  */
class HalfAdderTester extends AnyFlatSpec with ChiselScalatestTester {

  "HalfAdder" should "work" in {
    test(new HalfAdder).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

          dut.io.a.poke(false.B)
          dut.io.b.poke(false.B)
          dut.clock.step()
          dut.io.sum.expect(false.B)
          dut.io.carry.expect(false.B)

          dut.io.a.poke(false.B)
          dut.io.b.poke(true.B)
          dut.clock.step()
          dut.io.sum.expect(true.B)
          dut.io.carry.expect(false.B)

          dut.io.a.poke(true.B)
          dut.io.b.poke(false.B)
          dut.clock.step()
          dut.io.sum.expect(true.B)
          dut.io.carry.expect(false.B)

          dut.io.a.poke(true.B)
          dut.io.b.poke(true.B)
          dut.clock.step()
          dut.io.sum.expect(false.B)
          dut.io.carry.expect(true.B)

        }
    } 
}

