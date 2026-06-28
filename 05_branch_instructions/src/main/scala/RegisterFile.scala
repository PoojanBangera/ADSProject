// ADS I Class Project
// Pipelined RISC-V Core - Register File
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 01/09/2026 by Tobias Jauch (@tojauch)

package core_tile

import chisel3._

/*
Register File Module: 32x32-bit dual-read single-write register file

Memory:
    regFile: Register file according to the RISC-V 32I specification

Ports:
    req_1, resp_1: first read port
        req_1.addr: read address for register x[0-31]
        resp_1.data: register data output
    req_2, resp_2: second read port
        req_2.addr: read address for register x[0-31]
        resp_2.data: register data output
    req_3: write port
        req_3.addr: write destination address
        req_3.data: data to write
        req_3.wr_en: write enable signal

Functionality:
    Two read ports allow simultaneous reading of two operands
    Synchronous write updates register if wr_en is asserted
*/

// -----------------------------------------
// Register File
// -----------------------------------------

class regFileReadReq extends Bundle {  //Tells the register file which register to read//if addr = 1 --> read x1
    //ToDo: implement bundle for read request
    val addr = UInt(5.W)
}

class regFileReadResp extends Bundle {  //Returns the value stored in the register// if x1=4 --> data =4    
    //ToDo: implement bundle for read response
    val data = UInt(32.W)
}

class regFileWriteReq extends Bundle {  //Tells the register file what to write and where.//addr  = 3, data  = 9, wr_en = 1 --> Write 9 into x3
    //ToDo: implement bundle for write request
    val addr  = UInt(5.W)
    val data  = UInt(32.W)
    val wr_en = Bool()
}

class regFile extends Module {
  val io = IO(new Bundle {
    //ToDo: Add I/O ports 
    val req_1  = Input(new regFileReadReq)  // used to read rs1
    val resp_1 = Output(new regFileReadResp)

    val req_2  = Input(new regFileReadReq)  //used to read rs2
    val resp_2 = Output(new regFileReadResp)

    val req_3  = Input(new regFileWriteReq)  //Used by WB stage to write results.
})

//ToDo: Add your implementation according to the specification above here 
val regs = RegInit(VecInit(Seq.fill(32)(0.U(32.W))))  //created 32 registers and initializez them to zero

//io.resp_1.data := regs(io.req_1.addr)  //read port A
//io.resp_2.data := regs(io.req_2.addr)  // read port B //two ports becauseto read  two operands simultaneously

/*. If the register being read is also being written in the same cycle, the register file returns the write data directly instead 
of the old stored value. This avoids a register file RAW conflict and complements the pipeline forwarding logic*/

io.resp_1.data := Mux(
  io.req_3.wr_en && (io.req_3.addr === io.req_1.addr) && (io.req_1.addr =/= 0.U),
  io.req_3.data,
  regs(io.req_1.addr)
)

io.resp_2.data := Mux(
  io.req_3.wr_en && (io.req_3.addr === io.req_2.addr) && (io.req_2.addr =/= 0.U),
  io.req_3.data,
  regs(io.req_2.addr)
)

when(io.req_3.wr_en && (io.req_3.addr =/= 0.U)) {  //write only if wr_en = true and addr !=0--> because x0 is hardwired to zero
    printf(p"WRITE x${io.req_3.addr} = ${io.req_3.data}\n")
    regs(io.req_3.addr) := io.req_3.data  //write port  //Because one instruction writes only to one destination register
}
printf(p"READ rs1=${io.req_1.addr} -> ${io.resp_1.data}, rs2=${io.req_2.addr} -> ${io.resp_2.data}\n")

}

