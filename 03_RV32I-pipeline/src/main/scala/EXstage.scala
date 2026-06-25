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

    val uop          = Input(uopc())  //Instruction type from ID stage.//ex: add,sub,or, addi
    val operandA     = Input(UInt(32.W))  //operands to be processed
    val operandB     = Input(UInt(32.W))
    val xcptInvalid  = Input(Bool())  //exception flag from id stage

    val aluResult    = Output(UInt(32.W))  //computation result
    val exception    = Output(Bool())  //Exception passed to next stage.
  })

  val alu = Module(new ALU)  //Instantiate ALU module.

  alu.io.operandA := io.operandA  //Feed operands into ALU.
  alu.io.operandB := io.operandB

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

  io.aluResult := alu.io.aluResult  //Forward ALU result.
  io.exception := io.xcptInvalid    //Pass exception unchanged.
}