// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chiseltest._
import org.scalatest.flatspec.AnyFlatSpec


/** 
  *read serial tester
  */
class ReadSerialTester extends AnyFlatSpec with ChiselScalatestTester {

  "ReadSerial" should "work" in {
    test(new ReadSerial).withAnnotations(Seq(WriteVcdAnnotation)) { dut =>

        dut.io.rst.poke(true.B)
        dut.clock.step()

        dut.io.rst.poke(false.B)

        // Start bit
        dut.io.rxd.poke(false.B)
        dut.clock.step()

        // Send 8 bits: 10110010

        dut.io.rxd.poke(true.B)
        dut.clock.step()

        dut.io.rxd.poke(false.B)
        dut.clock.step()

        dut.io.rxd.poke(true.B)
        dut.clock.step()

        dut.io.rxd.poke(true.B)
        dut.clock.step()

        dut.io.rxd.poke(false.B)
        dut.clock.step()

        dut.io.rxd.poke(false.B)
        dut.clock.step()

        dut.io.rxd.poke(true.B)
        dut.clock.step()

        dut.io.rxd.poke(false.B)
        dut.clock.step()
        //dut.clock.step()

        dut.io.valid.expect(true.B)
        }
    } 
}

