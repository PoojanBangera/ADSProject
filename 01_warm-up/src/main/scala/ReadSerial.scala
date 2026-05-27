// ADS I Class Project
// Chisel Introduction
//
// Chair of Electronic Design Automation, RPTU in Kaiserslautern
// File created on 18/10/2022 by Tobias Jauch (@tojauch)

package readserial

import chisel3._
import chisel3.util._


/** controller class */
class Controller extends Module{
  
  val io = IO(new Bundle {
// TODO: Define IO ports of a the component as stated in the documentation
    val rxd = Input(Bool())
    val done = Input(Bool())
    val rst = Input(Bool())

    // Outputs
    val enable = Output(Bool())
    val valid = Output(Bool())
    })

  // internal variables
//TODO: Define internal variables (registers and/or wires), if needed

 val idle :: receive :: Nil = Enum(2)

  // State register
  val state = RegInit(idle)

  // Default outputs
  io.enable := false.B //Default outputs are OFF
  io.valid := false.B //Default outputs are OFF

  

  // state machine
// TODO: Describe functionality if the controller as a state machine
 switch(state){

    is(idle){

      when(io.rst){
        state := idle
      }

      .elsewhen(io.rxd === false.B){
        state := receive
      }
    }

    is(receive){

      io.enable := true.B

      when(io.rst){
        state := idle  //Reset immediately stops reception
      }

      .elsewhen(io.done){
        io.valid := true.B //If counter says 8 bits completed
        state := idle
      }
    }
  }


}


/** counter class */
class Counter extends Module{
  
  val io = IO(new Bundle {
  //TODO: Define IO ports of a the component as stated in the documentation
        // Inputs
    val enable = Input(Bool())
    val rst = Input(Bool())

    // Outputs
    val done = Output(Bool())
    })

  // internal variables
// TODO: Define internal variables (registers and/or wires), if needed
    val count = RegInit(0.U(4.W))  //Creates 4-bit register initialized to 0.

  // state machine
 //TODO: Describe functionality if the counter as a state machine
    io.done := false.B

  when(io.rst){
    count := 0.U
  }

  .elsewhen(io.enable){

    when(count === 7.U){
      io.done := true.B
      count := 0.U
    }

    .otherwise{
      count := count + 1.U
    }
  }


}

/** shift register class */
class ShiftRegister extends Module{
  
  val io = IO(new Bundle {
  // TODO: Define IO ports of a the component as stated in the documentation
    val enable = Input(Bool())
    val rst = Input(Bool())
    val rxd = Input(Bool())

    // Output
    val data = Output(UInt(8.W))
    })

  // internal variables
//TODO: Define internal variables (registers and/or wires), if needed
   val shiftReg = RegInit(0.U(8.W))

  // functionality
//TODO: Describe functionality if the shift register
// Shift functionality
  when(io.rst){
    shiftReg := 0.U
  }

  .elsewhen(io.enable){

    shiftReg := Cat(
      shiftReg(6,0), //Takes lower 7 bits of old register.
      io.rxd   //Appends newest incoming serial bit.
    )
  }

  // Output connection
  io.data := shiftReg  //Connects internal register to output port.
   
}

/** 
  * The last warm-up task deals with a more complex component. Your goal is to design a serial receiver.
  * It scans an input line (“serial bus”) named rxd for serial transmissions of data bytes. A transmission 
  * begins with a start bit ‘0’ followed by 8 data bits. The most significant bit (MSB) is transmitted first. 
  * There is no parity bit and no stop bit. After the last data bit has been transferred a new transmission 
  * (beginning with a start bit, ‘0’) may immediately follow. If there is no new transmission the bus line 
  * goes high (‘1’, this is considered the “idle” bus signal). In this case the receiver waits until the next 
  * transmission begins. The outputs of the design are an 8-bit parallel data signal and a valid signal. 
  * The valid signal goes high (‘1’) for one clock cycle after the last serial bit has been transmitted, 
  * indicating that a new data byte is ready.
  */
class ReadSerial extends Module{
  
  val io = IO(new Bundle {
  // TODO: Define IO ports of a the component as stated in the documentation
         // Inputs
    val rxd = Input(Bool())
    val rst = Input(Bool())

    // Outputs
    val data = Output(UInt(8.W))
    val valid = Output(Bool())
    })


  // instanciation of modules
 //TODO: Instanciate the modules that you need
   val controller = Module(new Controller())  
  val counter = Module(new Counter())
  val shiftReg = Module(new ShiftRegister())
   

  // connections between modules
// TODO: connect the signals between the modules
  // Controller connections
  controller.io.rxd := io.rxd //Pass serial input to controller.
  controller.io.done := counter.io.done  //Counter tells controller when 8 bits finished.
  controller.io.rst := io.rst //Connect reset to controller.


  // Counter connections
  counter.io.enable := controller.io.enable //Controller decides when counter should run.
  counter.io.rst := io.rst  //Reset connected to counter.


  // Shift Register connections
  shiftReg.io.enable := controller.io.enable //Controller decides when shifting happens.
  shiftReg.io.rst := io.rst  //Reset connected to counter.
  shiftReg.io.rxd := io.rxd  //Incoming serial bit goes into shift register.

   

  // global I/O 
// TODO: Describe output behaviour based on the input values and the internal signals
    // Global outputs
  io.data := shiftReg.io.data
  io.valid := controller.io.valid

}
