// ADS I Class Project
// Pipelined RISC-V Core
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/15/2023 by Tobias Jauch (@tojauch)

/*
The goal of this task is to implement a 5-stage pipeline that features a subset of RV32I (all R-type and I-type instructions). 

    Instruction Memory:
        The CPU has an instruction memory (IMem) with 4096 words, each of 32 bits.
        The content of IMem is loaded from a binary file specified during the instantiation of the MultiCycleRV32Icore module.

    CPU Registers:
        The CPU has a program counter (PC) and a register file (regFile) with 32 registers, each holding a 32-bit value.
        Register x0 is hard-wired to zero.

    Microarchitectural Registers / Wires:
        Various signals are defined as either registers or wires depending on whether they need to be used in the same cycle or in a later cycle.

    Processor Stages:
        The FSM of the processor has five stages: fetch, decode, execute, memory, and writeback.
        All stages are active at the same time and process different instructions simultaneously.

        Fetch Stage:
            The instruction is fetched from the instruction memory based on the current value of the program counter (PC).

        Decode Stage:
            Instruction fields such as opcode, rd, funct3, and rs1 are extracted.
            For R-type instructions, additional fields like funct7 and rs2 are extracted.
            Control signals (isADD, isSUB, etc.) are set based on the opcode and funct3 values.
            Operands (operandA and operandB) are determined based on the instruction type.

        Execute Stage:
            Arithmetic and logic operations are performed based on the control signals and operands.
            The result is stored in the aluResult register.

        Memory Stage:
            No memory operations are implemented in this basic CPU.

        Writeback Stage:
            The result of the operation (writeBackData) is written back to the destination register (rd) in the register file.

    Check Result:
        The final result (writeBackData) is output to the io.check_res signal.
        The exception signal is also passed to the wrapper module. It indicates whether an invalid instruction has been encountered.
        In the fetch stage, a default value of 0 is assigned to io.check_res.
*/

package core_tile

import chisel3._
import chisel3.util._
import chisel3.util.experimental.loadMemoryFromFile
//import Assignment02.{ALU, ALUOp}
import uopc._


class PipelinedRV32Icore (BinaryFile: String) extends Module {
  val io = IO(new Bundle {
    //ToDo: Add I/O ports
    val check_res = Output(UInt(32.W))  //final outouts of the cpu : check_res = instruction result
    val exception = Output(Bool())  //exception = invalid instruction flag
  })

//ToDo: Add your implementation according to the specification above here 
/////// Instantiate all pipeline stages, pipeline registers, and the register file to build the complete 5-stage pipelined RV32I processor.
  val ifStage    = Module(new IF(BinaryFile))  //Fetches instructions from BinaryFile.
  val ifBarrier  = Module(new IFBarrier)  //Stores instruction between IF and ID.
 
  val idStage    = Module(new ID)  //Decodes instruction.
  val idBarrier  = Module(new IDBarrier)  //Stores decoded information.

  val exStage    = Module(new EX)  //Executes ALU operations.
  val exBarrier  = Module(new EXBarrier)  //Stores ALU result.

  val memBarrier = Module(new MEMBarrier)  //Stores result before WB.

  val wbStage    = Module(new WB)  //Writes result into Register File.
  val wbBarrier  = Module(new WBBarrier)  //Final pipeline register.

  val regFileInst = Module(new regFile)  // register file : x0 ... x31

  // -----------------------------------------
  // IF -> IF Barrier
  // -----------------------------------------

  ifBarrier.io.inInstr := ifStage.io.instr  //fetched instruction is stored in if barrier

  // -----------------------------------------
  // IF Barrier -> ID
  // -----------------------------------------

  idStage.io.instr := ifBarrier.io.outInstr  //Instruction enters Decode stage

  // Register File Read Connections
  regFileInst.io.req_1 <> idStage.io.regFileReq_A  //read rs1
  idStage.io.regFileResp_A <> regFileInst.io.resp_1

  regFileInst.io.req_2 <> idStage.io.regFileReq_B  //read rs2 | <> -> is bulk connect :Automatically connects matching signals.
  idStage.io.regFileResp_B <> regFileInst.io.resp_2

  // -----------------------------------------
  // ID -> ID Barrier
  // -----------------------------------------
//////////// Transfer all decoded instruction information from the ID stage to the IDBarrier for storage and forwarding to the EX stage.
  idBarrier.io.inUOP         := idStage.io.uop  //Store decoded instruction.
  idBarrier.io.inRD          := idStage.io.rd   //Store destination register.
  idBarrier.io.inOperandA    := idStage.io.operandA  //Store operand A.
  idBarrier.io.inOperandB    := idStage.io.operandB  //Store operand B
  idBarrier.io.inXcptInvalid := idStage.io.XcptInvalid  //Store exception flag.

  // -----------------------------------------
  // ID Barrier -> EX
  // -----------------------------------------

  exStage.io.uop         := idBarrier.io.outUOP  //Instruction enters Execute stage.
  exStage.io.operandA    := idBarrier.io.outOperandA
  exStage.io.operandB    := idBarrier.io.outOperandB
  exStage.io.xcptInvalid := idBarrier.io.outXcptInvalid

  // -----------------------------------------
  // EX -> EX Barrier
  // -----------------------------------------

  exBarrier.io.inAluResult   := exStage.io.aluResult  //Store ALU result.
  exBarrier.io.inRD          := idBarrier.io.outRD  //Store destination register. ALU result alone is useless. Need to know where to write it later.
  exBarrier.io.inXcptInvalid := exStage.io.exception  //Store exception flag.

  // -----------------------------------------
  // EX Barrier -> MEM Barrier
  // -----------------------------------------

  memBarrier.io.inAluResult := exBarrier.io.outAluResult  //Pass result.
  memBarrier.io.inRD        := exBarrier.io.outRD   //pass destination register
  memBarrier.io.inException := exBarrier.io.outXcptInvalid  //Pass exception.

  // -----------------------------------------
  // MEM Barrier -> WB
  // -----------------------------------------

  wbStage.io.aluResult := memBarrier.io.outAluResult  //Result enters WB.
  wbStage.io.rd        := memBarrier.io.outRD  //Destination register enters WB.

  // Register File Write Connection
  regFileInst.io.req_3 := wbStage.io.regFileReq  //This is where: x1 , x2 , x3  actually get written.

  // -----------------------------------------
  // WB -> WB Barrier
  // -----------------------------------------

  wbBarrier.io.inCheckRes    := wbStage.io.check_res  //store result
  wbBarrier.io.inXcptInvalid := memBarrier.io.outException  //store exception

  // -----------------------------------------
  // Outputs
  // -----------------------------------------

  io.check_res := wbBarrier.io.outCheckRes  //result sent to testbench 
  io.exception := wbBarrier.io.outXcptInvalid  //exception sent to testbench    

}
