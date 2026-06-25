// ADS I Class Project
// Pipelined RISC-V Core - ID Stage
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

/*
Instruction Decode (ID) Stage: decoding and operand fetch

Extracted Fields from 32-bit Instruction (see RISC-V specification for reference):
    opcode: instruction format identifier
    funct3: selects variant within instruction format
    funct7: further specifies operation type (R-type only)
    rd: destination register address
    rs1: first source register address
    rs2: second source register address
    imm: 12-bit immediate value (I-type, sign-extended)

Register File Interfaces:
    regFileReq_A, regFileResp_A: read port for rs1 operand
    regFileReq_B, regFileResp_B: read port for rs2 operand

Internal Signals:
    Combinational decoders for instructions

Functionality:
    Decode opcode to determine instruction and identify operation (ADD, SUB, XOR, ...)
    Output: uop (operation code), rd, operandA (from rs1), operandB (rs2 or immediate)

Outputs:
    uop: micro-operation code (identifies instruction type)
    rd: destination register index
    operandA: first operand
    operandB: second operand 
    XcptInvalid: exception flag for invalid instructions
*/

package core_tile

import chisel3._
import chisel3.util._
import uopc._
import core_tile.uopc._

// -----------------------------------------
// Decode Stage
// -----------------------------------------

//ToDo: Add your implementation according to the specification above here 


class ID extends Module {

  val io = IO(new Bundle {

    val instr = Input(UInt(32.W))  //Receives the 32-bit instruction from IFBarrier.

    val regFileReq_A  = Output(new regFileReadReq)  //Sends the address of rs1 to the Register File.
    val regFileResp_A = Input(new regFileReadResp)  //Receives the value stored in rs1.

    val regFileReq_B  = Output(new regFileReadReq)  //Sends the address of rs2 to the Register File.
    val regFileResp_B = Input(new regFileReadResp)  //Receives the value stored in rs2.

    val uop         = Output(uopc())  //Sends decoded operation to EX stage.
    val rd          = Output(UInt(5.W))  //Sends destination register number.

    val rs1         = Output(UInt(5.W))
    val rs2         = Output(UInt(5.W))

    val operandA    = Output(UInt(32.W))  //First operand sent to EX stage.
    val operandB    = Output(UInt(32.W))  //Second operand sent to EX stage.
    val XcptInvalid = Output(Bool())  //Indicates whether instruction is invalid.
  })

  val opcode = io.instr(6,0)  //Extract instruction fields.
  val rd     = io.instr(11,7)
  val funct3 = io.instr(14,12)
  val rs1    = io.instr(19,15)  //rs1 and rs2 These extract the register numbers from the 32-bit instruction( not values..only addresses)
  val rs2    = io.instr(24,20)
  val funct7 = io.instr(31,25)

  val imm = Cat(Fill(20, io.instr(31)), io.instr(31,20))  //Extract immediate value for I-type instructions.

  io.regFileReq_A.addr := rs1  //Ask Register File for values of rs1 and rs2.
  io.regFileReq_B.addr := rs2

  io.rd := rd  //Send the extracted destination register number to the next stage.

  io.rs1 := rs1 //Take the register numbers I extracted from the instruction and send them outside the ID stage.
  io.rs2 := rs2    

  io.operandA := io.regFileResp_A.data  //Get actual register values.
  io.operandB := io.regFileResp_B.data  //Take operandB from rs2 register.

  io.uop := NOP  //Default operation = do nothing.
  io.XcptInvalid := false.B

  switch(opcode) {   //Determine instruction type.(0110011 → R-Type 0010011 → I-Type)

    // R-Type
    is("b0110011".U) {

      switch(funct3) {

        is("b000".U) {
          when(funct7 === "b0000000".U) {
            io.uop := ADD
          }.elsewhen(funct7 === "b0100000".U) {
            io.uop := SUB
          }.otherwise {
            io.XcptInvalid := true.B
          }
        }

        is("b100".U) { io.uop := XOR }
        is("b110".U) { io.uop := OR }
        is("b111".U) { io.uop := AND }

        is("b001".U) { io.uop := SLL }

        is("b101".U) {
          when(funct7 === "b0000000".U) {
            io.uop := SRL
          }.elsewhen(funct7 === "b0100000".U) {
            io.uop := SRA
          }.otherwise {
            io.XcptInvalid := true.B
          }
        }

        is("b010".U) { io.uop := SLT }
        is("b011".U) { io.uop := SLTU }

      }
    }

    // I-Type
    is("b0010011".U) {

      io.operandB := imm  //For I-type: there is no rs2. so operandB = immediate

      switch(funct3) {

        is("b000".U) { io.uop := ADDI }

        is("b100".U) { io.uop := XORI }

        is("b110".U) { io.uop := ORI }

        is("b111".U) { io.uop := ANDI }

        is("b001".U) { io.uop := SLLI }

        is("b101".U) {
          when(funct7 === "b0000000".U) {
            io.uop := SRLI
          }.elsewhen(funct7 === "b0100000".U) {
            io.uop := SRAI
          }.otherwise {
            io.XcptInvalid := true.B
          }
        }

        is("b010".U) { io.uop := SLTI }

        is("b011".U) { io.uop := SLTIU }

      }
    }

    /*otherwise {
      io.XcptInvalid := true.B
    }*/
  }
}