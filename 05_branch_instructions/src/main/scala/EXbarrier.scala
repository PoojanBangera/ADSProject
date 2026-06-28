// ADS I Class Project
// Pipelined RISC-V Core - EX Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
EX-Barrier: pipeline register between Execute and Memory stages

Internal Registers:
    aluResult: ALU computation result
    rd: destination register index
    exception: exception flag

Inputs:
    inAluResult: computation result from EX stage
    inRD: destination register from EX stage
    inXcptInvalid: exception flag from EX stage

Outputs:
    outAluResult: result to MEM stage
    outRD: destination register to MEM stage
    outXcptInvalid: exception flag to MEM stage

Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._
import uopc._

// -----------------------------------------
// EX-Barrier
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here 
//The EXBarrier is completely unchanged for forwarding. Its only job is to store the outputs of the EX stage for one clock cycle and pass 
//them to the MEM stage.
class EXBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult   = Input(UInt(32.W))  //ALU result from EX stage.
    val inRD          = Input(UInt(5.W))  //Destination register number.
    val inXcptInvalid = Input(Bool())   //Exception flag from EX stage.

    val outAluResult   = Output(UInt(32.W))  //Same values sent to MEM stage one cycle later.
    val outRD          = Output(UInt(5.W))
    val outXcptInvalid = Output(Bool())

    val inUOP  = Input(uopc())
val outUOP = Output(uopc())

    val inBranchTaken  = Input(Bool())
val inBranchTarget = Input(UInt(32.W))
val inLinkAddress  = Input(UInt(32.W))

val outBranchTaken  = Output(Bool())
val outBranchTarget = Output(UInt(32.W))
val outLinkAddress  = Output(UInt(32.W))
  })

  val aluReg  = RegInit(0.U(32.W))  //Stores ALU result.
  val rdReg   = RegInit(0.U(5.W))  //Stores destination register.
  val excReg  = RegInit(false.B)  //Stores exception flag.
  val uopReg = RegInit(uopc.NOP)

  val branchTakenReg  = RegInit(false.B)
val branchTargetReg = RegInit(0.U(32.W))
val linkAddressReg  = RegInit(0.U(32.W))

  aluReg := io.inAluResult  //Save ALU result.
  rdReg  := io.inRD           //saves destination register
  excReg := io.inXcptInvalid    //Save exception status.
  uopReg := io.inUOP

  io.outAluResult   := aluReg  //Forward stored ALU result.
  io.outRD          := rdReg    //Forward stored destination register.
  io.outXcptInvalid := excReg  //Forward stored exception flag.
  io.outUOP := uopReg

  branchTakenReg  := io.inBranchTaken
branchTargetReg := io.inBranchTarget
linkAddressReg  := io.inLinkAddress

io.outBranchTaken  := branchTakenReg
io.outBranchTarget := branchTargetReg
io.outLinkAddress  := linkAddressReg
}

