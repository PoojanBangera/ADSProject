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

// -----------------------------------------
// MEM-Barrier
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here 
class MEMBarrier extends Module {
  val io = IO(new Bundle {
    val inAluResult   = Input(UInt(32.W))  //Receives ALU result.
    val inRD          = Input(UInt(5.W))  //Receives destination register number.
    val inException   = Input(Bool())     //Receives exception status.
 
    val outAluResult   = Output(UInt(32.W))   //Sent to WB stage next cycle.
    val outRD          = Output(UInt(5.W))
    val outException   = Output(Bool())
  })

  val aluReg = RegInit(0.U(32.W))  //Stores ALU result.
  val rdReg  = RegInit(0.U(5.W))  //Stores destination register
  val excReg = RegInit(false.B)   //Stores exception flag

  aluReg := io.inAluResult    //Save ALU result.
  rdReg  := io.inRD             //Save destination register
  excReg := io.inException      //Save exception status

  io.outAluResult := aluReg     //Send result to WB
  io.outRD        := rdReg      //Send destination register to WB.
  io.outException := excReg     //Send exception status to WB
}
