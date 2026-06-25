// ToDo: Add your ALU implementation from Assignment02 here
// ADS I Class Project
// Assignment 02: Arithmetic Logic Unit and UVM Testbench
//
// Chair of Electronic Design Automation, RPTU University Kaiserslautern-Landau
// File created on 09/21/2025 by Tharindu Samarakoon (gug75kex@rptu.de)
// File updated on 10/29/2025 by Tobias Jauch (tobias.jauch@rptu.de)

package Assignment02

import chisel3._
import chisel3.util._
import chisel3.experimental.ChiselEnum

//ToDo: define AluOp Enum
object ALUOp extends ChiselEnum {
  val ADD, SUB, AND, OR, XOR,
      SLL, SRL, SRA,
      SLT, SLTU,
      PASSB = Value
}

class ALU extends Module {
  
  val io = IO(new Bundle {
    //ToDo: define IOs
    val operandA = Input(UInt(32.W))
    val operandB = Input(UInt(32.W))
    val operation = Input(ALUOp())

    val aluResult = Output(UInt(32.W))
  })

  //ToDo: implement ALU functionality according to the task specification
  io.aluResult := 0.U //set default value to zero

switch(io.operation) {

  is(ALUOp.ADD) {
    io.aluResult := io.operandA + io.operandB
  }

  is(ALUOp.SUB) {
    io.aluResult := io.operandA - io.operandB
  }

  is(ALUOp.AND) {
    io.aluResult := io.operandA & io.operandB  //bitwise and
  }

  is(ALUOp.OR) {
    io.aluResult := io.operandA | io.operandB
  }

  is(ALUOp.XOR) {
    io.aluResult := io.operandA ^ io.operandB
  }

  is(ALUOp.SLL) {
    io.aluResult := io.operandA << io.operandB(4,0) //logical left shift (shift left logical)
  }

  is(ALUOp.SRL) {
    io.aluResult := io.operandA >> io.operandB(4,0)
  }

  is(ALUOp.SRA) {
    io.aluResult := (io.operandA.asSInt >> io.operandB(4,0)).asUInt //shift right arithmetic
  }

  is(ALUOp.SLT) {
    io.aluResult := (io.operandA.asSInt < io.operandB.asSInt).asUInt //set less than : 1 if a<b ; 0 otherwise  //asUInt → Convert Boolean result to UInt for ALU output
  }

  is(ALUOp.SLTU) {
    io.aluResult := (io.operandA < io.operandB).asUInt //set less than unsigned; Both operands are treated as unsigned integers.;
  }

  is(ALUOp.PASSB) {
    io.aluResult := io.operandB //no computation is performed, value of operand a is ignored , value of operand b is copied to the result
  }
}

}

