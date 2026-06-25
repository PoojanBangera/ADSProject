// ADS I Class Project
// Pipelined RISC-V Core - Forwarding Unit
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 05/09/2026 by Tobias Jauch (@tojauch)

/*
Forwarding Unit: resolves data hazards by forwarding results from later pipeline stages to the ID stage

Functionality (cf. slide 6-24ff of the lecture slides):
    Detects data hazards by comparing source registers in the EX stage with destination registers in MEM and WB stages (EX and MEM barriers).
    Generates control signals for the multiplexers in the EX stage to select the correct data source for the ALU inputs
    Handles cases where multiple hazards occur simultaneously (e.g., forwarding from both MEM and WB stages)

Inputs:
    rs1_EX: source register 1 in EX stage
    rs2_EX: source register 2 in EX stage
    rd_MEM: destination register in MEM stage
    rd_WB: destination register in WB stage
    wrEn_MEM: write enable signal for MEM stage
    wrEn_WB: write enable signal for WB stage

Outputs:
    forwardA: control signal for selecting source of operand A in EX stage
    forwardB: control signal for selecting source of operand B in EX stage

*/

package core_tile

import chisel3._
import chisel3.util._
import uopc._

// -----------------------------------------
// Forwarding Unit
// -----------------------------------------

class ForwardingUnit extends Module {
  val io = IO(new Bundle {
    // Add I/O ports according to the specification above here

    val rs1_EX   = Input(UInt(5.W))  //This is the first source register of the instruction currently in the EX stage.
    val rs2_EX   = Input(UInt(5.W))

    val rd_MEM   = Input(UInt(5.W))  //This is the destination register of the instruction currently in the MEM stage.
    val rd_WB    = Input(UInt(5.W))  //Destination register of the instruction in WB stage.

    val wrEn_MEM = Input(Bool())  //Indicates whether the MEM-stage instruction will write to the register file.
    val wrEn_WB  = Input(Bool())  //Same thing for the WB stage.

    val forwardA = Output(UInt(2.W))  //This is the control signal sent to the EX stage telling it where Operand A should come from.
    val forwardB = Output(UInt(2.W))
  /* 00 → Use Register File

10 → Forward from MEM stage

01 → Forward from WB stage*/ 
  })

  //ToDo: Add your implementation according to the specification above here 
  io.forwardA := "b00".U  //No forwarding required.
  io.forwardB := "b00".U

  when(io.wrEn_MEM && (io.rd_MEM =/= 0.U) && (io.rd_MEM === io.rs1_EX)) {  //This checks three conditions. //io.wrEn_MEM=The MEM instruction must actually write a register.//3rd confition-->checks whether the register being written by the instruction in the MEM stage is the same register needed as source operand rs1 by the instruction in the EX stage, indicating a potential data hazard.(raw hazard)
    io.forwardA := "b10".U
  }.elsewhen(io.wrEn_WB && (io.rd_WB =/= 0.U) && (io.rd_WB === io.rs1_EX)) {
    io.forwardA := "b01".U
  }

  when(io.wrEn_MEM && (io.rd_MEM =/= 0.U) && (io.rd_MEM === io.rs2_EX)) {
    io.forwardB := "b10".U
  }.elsewhen(io.wrEn_WB && (io.rd_WB =/= 0.U) && (io.rd_WB === io.rs2_EX)) {
    io.forwardB := "b01".U 
  }

  printf(p"rs1=${io.rs1_EX} rs2=${io.rs2_EX} rdMEM=${io.rd_MEM} rdWB=${io.rd_WB} fA=${io.forwardA} fB=${io.forwardB}\n")

}