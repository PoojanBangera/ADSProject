// ADS I Class Project
// Pipelined RISC-V Core - MEM Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
MEM-Barrier: pipeline register between Memory and Writeback stages

Internal Registers:
    aluResult: computation result (or future load data)
    rd: destination register index
    exception: exception flag

Inputs:
    inAluResult: result from MEM stage
    inRD: destination register from MEM stage
    inException: exception flag from MEM stage

Outputs:
    outAluResult: result to WB stage
    outRD: destination register to WB stage
    outException: exception flag to WB stage

Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._
import uopc._

// -----------------------------------------
// MEM-Barrier
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here 
class MEMBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult   = Input(UInt(32.W))  //Receives ALU result.
    val inRD          = Input(UInt(5.W))  //Receives destination register number.
    val inException   = Input(Bool())     //Receives exception status.

    val inUOP  = Input(uopc())
val outUOP = Output(uopc())

    val inBranchTaken  = Input(Bool())
val inBranchTarget = Input(UInt(32.W))
val inLinkAddress  = Input(UInt(32.W))
 
    val outAluResult   = Output(UInt(32.W))   //Sent to WB stage next cycle.
    val outRD          = Output(UInt(5.W))
    val outException   = Output(Bool())

    val outBranchTaken  = Output(Bool())
val outBranchTarget = Output(UInt(32.W))
val outLinkAddress  = Output(UInt(32.W))
  })

  val aluReg = RegInit(0.U(32.W))  //Stores ALU result.
  val rdReg  = RegInit(0.U(5.W))  //Stores destination register
  val excReg = RegInit(false.B)   //Stores exception flag

  val uopReg = RegInit(uopc.NOP)

  val branchTakenReg  = RegInit(false.B)
val branchTargetReg = RegInit(0.U(32.W))
val linkAddressReg  = RegInit(0.U(32.W))

  aluReg := io.inAluResult    //Save ALU result.
  rdReg  := io.inRD             //Save destination register
  excReg := io.inException      //Save exception status
  uopReg := io.inUOP

  io.outAluResult := aluReg     //Send result to WB
  io.outRD        := rdReg      //Send destination register to WB.
  io.outException := excReg     //Send exception status to WB

  branchTakenReg  := io.inBranchTaken
branchTargetReg := io.inBranchTarget
linkAddressReg  := io.inLinkAddress

io.outBranchTaken  := branchTakenReg
io.outBranchTarget := branchTargetReg
io.outLinkAddress  := linkAddressReg

io.outUOP := uopReg
}
