// package common
// import common.Math
// import chisel3._
// import chisel3.util._
// import common.axi._
// import common.storage._
// import chisel3.experimental.{DataMirror, Direction, requireIsChiselType}

// // class arp_in extends Bundle {
// //     val Tar_protocol_addr     = Input(UInt(32.W))
// //     val Tar_hardware_addr     = Input(UInt(48.W))
// //     val Send_protocol_addr    = Input(UInt(32.W))
// //     val Send_hardware_addr    = Input(UInt(48.W))
// //     val operation             = Input(UInt(16.W))
// //     val Protocol_len          = Input(UInt(8.W))
// //     val Hardware_len          = Input(UInt(8.W))
// //     val Protocol_type         = Input(UInt(16.W))
// //     val Hardware_type         = Input(UInt(16.W))
// //     val Tehernet_type         = Input(UInt(16.W))
// //     val mac_source            = Input(UInt(48.W))
// //     val mac_destination       = Input(UInt(48.W))
// // }
// // class arp_table_entries extends Bundle{
// //     val mac_addr            = UInt(48.W)
// //     val ip_addr             = UInt(32.W)
// //     val valid               = UInt(1.W)
// // } 
// // class mac_out extends Bundle{
// //     val mac_addr            = Input(UInt(48.W))
// //     val hit               = Input(UInt(1.W))
// // }
// //336 bit

// class tt extends Bundle{
//     val aa            = Wire(UInt(335.W))
//     val bb               = Wire(UInt(1.W))
// }
// class axitest (
// 	mymac 	:	UInt,//(48.W),
// 	myip	:	UInt//(32.W)
// ) extends Module{
//     val io = IO(new Bundle{
//         val net_rx          =   Flipped(Decoupled(new AXIS(512)))
//         val net_tx          =   Decoupled(new AXIS(512))
// 	})
//     io.net_tx.valid        := 1.U
//     io.net_rx.ready        := 1.U
//     val a =  io.net_rx.bits.data(511, 176)//.asTypeOf(new arp_in)
//     val b = new tt
//     b.aa := a(335,1)
//     b.bb := a(0)
//     io.net_tx.bits.data   :=    Cat(a.asTypeOf(UInt(336.W)), 0.U)
//     val temp1 = Wire(UInt(32.W))
//     val temp2 = Wire(UInt(32.W))
//     temp1 := Cat(0xffff.U, 0xffff.U)
//     temp2 := 0.U
//     io.net_tx.bits.keep   :=   Cat(0xffff.U, 0xffff.U, temp2)
//     io.net_tx.bits.last   :=    b.bb


// }