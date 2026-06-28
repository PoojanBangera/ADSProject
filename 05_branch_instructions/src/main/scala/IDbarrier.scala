// ADS I Class Project
// Pipelined RISC-V Core - ID Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
ID-Barrier: pipeline register between Decode and Execute stages

Internal Registers:
    uop: micro-operation code (from uopc enum)
    rd: destination register index, initialized to 0
    operandA: first source operand, initialized to 0
    operandB: second operand/immediate, initialized to 0

Inputs:
    inUOP: micro-operation code from ID stage
    inRD: destination register from ID stage
    inOperandA: first operand from ID stage
    inOperandB: second operand/immediate from ID stage
    inXcptInvalid: exception flag from ID stage

Outputs:
    outUOP: micro-operation code to EX stage
    outRD: destination register to EX stage
    outOperandA: first operand to EX stage
    outOperandB: second operand to EX stage
    outXcptInvalid: exception flag to EX stage
Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._
import uopc._

// -----------------------------------------
// ID-Barrier
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here 

class IDBarrier extends Module {

  val io = IO(new Bundle {
//Inputs from ID Stage
    val inUOP         = Input(uopc())
    val inRD          = Input(UInt(5.W))

    val inRS1         = Input(UInt(5.W))
    val inRS2         = Input(UInt(5.W))

    val inPC  = Input(UInt(32.W))
    val inImm = Input(UInt(32.W))

    val inOperandA    = Input(UInt(32.W))
    val inOperandB    = Input(UInt(32.W))
    val inXcptInvalid = Input(Bool())

    val flush = Input(Bool())


//Outputs to EX Stage
    val outUOP         = Output(uopc())
    val outRD          = Output(UInt(5.W))

    val outRS1         = Output(UInt(5.W))  //These receive the source register numbers (rs1 and rs2) from the ID stage.
    val outRS2         = Output(UInt(5.W))

    val outPC  = Output(UInt(32.W))
    val outImm = Output(UInt(32.W))

    val outOperandA    = Output(UInt(32.W))
    val outOperandB    = Output(UInt(32.W))
    val outXcptInvalid = Output(Bool())


  })
//Internal registers used to store ID stage outputs for one clock cycle.
  val uopReg      = RegInit(uopc.NOP)
  val rdReg       = RegInit(0.U(5.W))

  val rs1Reg      = RegInit(0.U(5.W)) //These store the register numbers for one clock cycle, just like the other pipeline signals.
  val rs2Reg      = RegInit(0.U(5.W))

  val pcReg  = RegInit(0.U(32.W))
  val immReg = RegInit(0.U(32.W))

  val operandAReg = RegInit(0.U(32.W))
  val operandBReg = RegInit(0.U(32.W))
  val excReg      = RegInit(false.B)

  when(io.flush) {
  uopReg      := uopc.NOP
  rdReg       := 0.U
  rs1Reg      := 0.U
  rs2Reg      := 0.U
  pcReg       := 0.U
  immReg      := 0.U
  operandAReg := 0.U
  operandBReg := 0.U
  excReg      := false.B
}.otherwise {
  uopReg      := io.inUOP
  rdReg       := io.inRD
  rs1Reg      := io.inRS1
  rs2Reg      := io.inRS2
  pcReg       := io.inPC
  immReg      := io.inImm
  operandAReg := io.inOperandA
  operandBReg := io.inOperandB
  excReg      := io.inXcptInvalid
}

 
//Capture and store incoming values at each clock edge.
 /* uopReg      := io.inUOP
  rdReg       := io.inRD

  rs1Reg      := io.inRS1  //This saves the source register numbers when the clock ticks.
  rs2Reg      := io.inRS2

  pcReg  := io.inPC
  immReg := io.inImm

  operandAReg := io.inOperandA
  operandBReg := io.inOperandB
  excReg      := io.inXcptInvalid */


//Forward the stored values to the Execute stage.
  io.outUOP         := uopReg
  io.outRD          := rdReg

  io.outRS1         := rs1Reg  //These outputs are connected to the Forwarding Unit.
  io.outRS2         := rs2Reg

  io.outPC  := pcReg
  io.outImm := immReg

  io.outOperandA    := operandAReg
  io.outOperandB    := operandBReg
  io.outXcptInvalid := excReg


}

/*Why was this needed?

The Forwarding Unit compares:

rs1_EX, rs2_EX

with

rd_MEM, rd_WB

to detect data hazards.*/
