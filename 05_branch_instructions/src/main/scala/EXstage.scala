// ADS I Class Project
// Pipelined RISC-V Core - EX Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Instruction Execute (EX) Stage: ALU operations and exception detection

Instantiated Modules:
    ALU: Integrate your module from Assignment02 for arithmetic/logical operations

ALU Interface:
    alu.io.operandA: first operand input
    alu.io.operandB: second operand input
    alu.io.operation: operation code controlling ALU function
    alu.io.aluResult: computation result output

Internal Signals:
    Map uopc codes to ALUOp values

Functionality:
    Map instruction uop to ALU operation code
    Pass operands to ALU
    Output results to pipeline

Outputs:
    aluResult: computation result from ALU
    exception: pass exception flag
*/

//package Assignment02
package core_tile

import chisel3._
import chisel3.util._
import Assignment02.{ALU, ALUOp}
import uopc._
import core_tile.uopc._

// -----------------------------------------
// Execute Stage
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here 

class EX extends Module {

  val io = IO(new Bundle {

  val uop          = Input(uopc())

  val operandA     = Input(UInt(32.W))
  val operandB     = Input(UInt(32.W))

  val pc  = Input(UInt(32.W))
  val imm = Input(UInt(32.W))

  val forwardA     = Input(UInt(2.W))  //forwardA and forwardB tell the EX stage whether forwarding is needed.
  val forwardB     = Input(UInt(2.W))

  val memResult    = Input(UInt(32.W))  //fwd//carries the ALU result from the MEM stage.
  val wbResult     = Input(UInt(32.W))  //fwd// carries the ALU result from the WB stage.

  val xcptInvalid  = Input(Bool())

  val aluResult    = Output(UInt(32.W))
  val exception    = Output(Bool())

  val branchTaken  = Output(Bool())
val branchTarget = Output(UInt(32.W))
val linkAddress  = Output(UInt(32.W))

  
  })

  val alu = Module(new ALU)  //Instantiate ALU module.

val opA = Wire(UInt(32.W))  //By default, the ALU uses the operands coming from the register file.
val opB = Wire(UInt(32.W))

val branchTaken = Wire(Bool())
val branchTarget = Wire(UInt(32.W))

branchTaken := false.B
branchTarget := io.pc + io.imm

opA := io.operandA
opB := io.operandB

when(io.forwardA === "b10".U) {     //use the result from the MEM stage instead of the register file.
  opA := io.memResult
}.elsewhen(io.forwardA === "b01".U) {   //use the result from the WB stage.  //use the original register value.
  opA := io.wbResult
}

when(io.forwardB === "b10".U) {    /// |||ly ofr B   //b10,b01 --> control codes chosen by the designer
  opB := io.memResult
}.elsewhen(io.forwardB === "b01".U) {
  opB := io.wbResult
} 

switch(io.uop) {

  is(BEQ) {
    branchTaken := (opA === opB)
  }

  is(BNE) {
    branchTaken := (opA =/= opB)
  }

  is(BLT) {
    branchTaken := (opA.asSInt < opB.asSInt)
  }

  is(BGE) {
    branchTaken := (opA.asSInt >= opB.asSInt)
  }

  is(BLTU) {
    branchTaken := (opA < opB)
  }

  is(BGEU) {
    branchTaken := (opA >= opB)
  }
  is(JAL) {
  branchTaken := true.B
  branchTarget := io.pc + io.imm
}

is(JALR) {
  branchTaken := true.B
  branchTarget := (opA + io.imm) & "hFFFFFFFE".U
}

}
/*when(
  io.uop === ADD  ||
  io.uop === SUB  ||
  io.uop === AND  ||
  io.uop === OR   ||
  io.uop === XOR  ||
  io.uop === SLL  ||
  io.uop === SRL  ||
  io.uop === SRA  ||
  io.uop === SLT  ||
  io.uop === SLTU
) {

  when(io.forwardA === "b10".U) {
    opA := io.memResult
  }.elsewhen(io.forwardA === "b01".U) {
    opA := io.wbResult
  }

  when(io.forwardB === "b10".U) {
    opB := io.memResult
  }.elsewhen(io.forwardB === "b01".U) {
    opB := io.wbResult
  }
}*/
printf(p"opA=${opA} opB=${opB} mem=${io.memResult} wb=${io.wbResult} fA=${io.forwardA} fB=${io.forwardB}\n")

alu.io.operandA := opA
alu.io.operandB := opB

  alu.io.operation := ALUOp.ADD  //Safe default if no case matches.

  switch(io.uop) {                          //Mapping Section

    is(ADD)  { alu.io.operation := ALUOp.ADD }   //is(ADD)  { alu.io.operation := ALUOp.ADD }
    is(ADDI) { alu.io.operation := ALUOp.ADD }   //is(ADDI) { alu.io.operation := ALUOp.ADD }

    is(SUB)  { alu.io.operation := ALUOp.SUB }

    is(AND)  { alu.io.operation := ALUOp.AND }
    is(ANDI) { alu.io.operation := ALUOp.AND }

    is(OR)   { alu.io.operation := ALUOp.OR }
    is(ORI)  { alu.io.operation := ALUOp.OR }

    is(XOR)  { alu.io.operation := ALUOp.XOR }
    is(XORI) { alu.io.operation := ALUOp.XOR }

    is(SLL)  { alu.io.operation := ALUOp.SLL }
    is(SLLI) { alu.io.operation := ALUOp.SLL }

    is(SRL)  { alu.io.operation := ALUOp.SRL }
    is(SRLI) { alu.io.operation := ALUOp.SRL }

    is(SRA)  { alu.io.operation := ALUOp.SRA }
    is(SRAI) { alu.io.operation := ALUOp.SRA }

    is(SLT)  { alu.io.operation := ALUOp.SLT }
    is(SLTI) { alu.io.operation := ALUOp.SLT }

    is(SLTU)  { alu.io.operation := ALUOp.SLTU }
    is(SLTIU) { alu.io.operation := ALUOp.SLTU }
  }

io.branchTaken  := branchTaken
io.branchTarget := branchTarget
io.linkAddress  := io.pc + 4.U

  io.aluResult := alu.io.aluResult  //Forward ALU result.
  io.exception := io.xcptInvalid    //Pass exception unchanged.
}