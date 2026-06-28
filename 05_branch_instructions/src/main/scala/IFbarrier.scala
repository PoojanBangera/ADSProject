// ADS I Class Project
// Pipelined RISC-V Core - IF Barrier
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
IF-Barrier: pipeline register between Fetch and Decode stages

Internal Registers:
    instrReg: holds instruction between pipeline stages, initialized to 0

Inputs:
    inInstr: fetched instruction from IF stage

Outputs:
    outInstr: instruction to ID stage

Functionality:
    Save all input signals to a register and output them in the following clock cycle
*/

package core_tile

import chisel3._

// -----------------------------------------
// IF-Barrier
// -----------------------------------------

class IFBarrier extends Module {
  val io = IO(new Bundle {
    //ToDo: Add I/O ports
    val inInstr  = Input(UInt(32.W))  //Input instruction from IF stage (32-bit instruction)
    val outInstr = Output(UInt(32.W))  //Output instruction to ID stage.
    val flush = Input(Bool())

    val inPC  = Input(UInt(32.W))
val outPC = Output(UInt(32.W))
  })

//ToDo: Add your implementation according to the specification above here 
val instrReg = RegInit(0.U(32.W))  //Creates a 32-bit register initialized to 0.

val pcReg = RegInit(0.U(32.W))

//instrReg := io.inInstr  //On every clock edge: Copy input instruction into instrReg.
//pcReg := io.inPC
when(io.flush) {
  instrReg := 0.U
  pcReg := 0.U
}.otherwise {
  instrReg := io.inInstr
  pcReg := io.inPC
}

io.outPC := pcReg

io.outInstr := instrReg //Send the stored instruction to the next stage (ID).

}
