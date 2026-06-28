// ADS I Class Project
// Pipelined RISC-V Core - IF Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
The Instruction Fetch (IF) stage is the first stage of the pipeline and handles instruction retrieval from memory.

Memory:
    IMem: instruction memory with 4096 32-bit unsigned integer entires, loaded from a binary file at compile time

Internal Registers:
    PC: 32-bit unsigned integer register, initialized to 0 holding the current program counter address

Internal Signals:
    none

Functionality:
    Fetch the instruction at the current PC (word-aligned addressing)
    Increment the PC (word-aligned) each clock cycle to fetch the next sequential instruction

Parameters:
    BinaryFile: String - path to the binary file to load into instruction memory

Inputs:
    none

Outputs:
    instr: send the fetched instruction to IF Barrier
*/

package core_tile

import chisel3._
import chisel3.util.experimental.loadMemoryFromFile

// -----------------------------------------
// Fetch Stage
// -----------------------------------------

class IF (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    // ToDo: Add I/O ports
    val instr = Output(UInt(32.W))  //this is the output ( fetched instruction)
    val pc = Output(UInt(32.W))

    val redirectPC     = Input(Bool())
val redirectTarget = Input(UInt(32.W))
  })

//ToDo: Add your implementation according to the specification above here 
 // Instruction Memory
  val IMem = Mem(4096, UInt(32.W))  //means 4096 instructions 32 bits each //4096 entries because 4096 (2¹²) is a convenient power-of-two size commonly used in digital hardware
  loadMemoryFromFile(IMem, BinaryFile)  //loads BinaryFile_pipelined into memory

  // Program Counter
  val PC = RegInit(0.U(32.W))  //Program counter

  io.pc := PC

  // Fetch instruction
  io.instr := IMem(PC)  //Read instruction at current PC

  // Increment PC
  ///PC := PC + 1.U  //increment PC to fetch the instructions in sequence

  when(io.redirectPC) {
  PC := io.redirectTarget
}.otherwise {
  PC := PC + 1.U
}
  
}
