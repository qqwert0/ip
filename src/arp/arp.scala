// // package ip
// package project_foo
// import common.Math
// import chisel3._
// import chisel3.util._
// import common.axi._
// import common.storage._
// import chisel3.experimental.{DataMirror, Direction, requireIsChiselType}

// class arp_in extends Bundle {
//     val Tar_protocol_addr     = Wire(UInt(32.W))
//     val Tar_hardware_addr     = Wire(UInt(48.W))
//     val Send_protocol_addr    = Wire(UInt(32.W))
//     val Send_hardware_addr    = Wire(UInt(48.W))
//     val operation             = Wire(UInt(16.W))
//     val Protocol_len          = Wire(UInt(8.W))
//     val Hardware_len          = Wire(UInt(8.W))
//     val Protocol_type         = Wire(UInt(16.W))
//     val Hardware_type         = Wire(UInt(16.W))
//     val Tehernet_type         = Wire(UInt(16.W))
//     val mac_source            = Wire(UInt(48.W))
//     val mac_destination       = Wire(UInt(48.W))
// }
// class arp_table_entries extends Bundle{
//     val mac_addr            = UInt(48.W)
//     val ip_addr             = UInt(32.W)
//     val valid               = UInt(1.W)
// } 
// class mac_out extends Bundle{
//     val mac_addr            = Input(UInt(48.W))
//     val hit               = Input(UInt(1.W))
// }
// //336 bit
// class arp (
// 	mymac 	:	UInt,//(48.W),
// 	myip	:	UInt//(32.W)
// ) extends Module{
//     val io = IO(new Bundle{
//         val net_rx          =   Flipped(Decoupled(new AXIS(512)))
//         val net_tx          =   Decoupled(new AXIS(512))
//         val arp_req1        =   Flipped(Decoupled(UInt(32.W)))
//         val arp_req2        =   Flipped(Decoupled(UInt(32.W)))
//         val arp_rsp1        =   Decoupled(new mac_out)
//         val arp_rsp2        =   Decoupled(new mac_out)
// 	})

//     val arp_net_rx             = new arp_in
//     val net_tx_valid		= RegInit(UInt(1.W),0.U)
//     val rsp1_valid  		= RegInit(UInt(1.W),0.U)
//     val rsp2_valid			= RegInit(UInt(1.W),0.U)
//     arp_net_rx.Tar_protocol_addr    := io.net_rx.bits.data(511, 480)
//     arp_net_rx.Tar_hardware_addr    := io.net_rx.bits.data(479, 432)
//     arp_net_rx.Send_protocol_addr   := io.net_rx.bits.data(431, 400)
//     arp_net_rx.Send_hardware_addr   := io.net_rx.bits.data(399, 352)
//     arp_net_rx.operation            := io.net_rx.bits.data(351, 336)
//     arp_net_rx.Protocol_len         := io.net_rx.bits.data(335, 328)
//     arp_net_rx.Hardware_len         := io.net_rx.bits.data(327, 320)
//     arp_net_rx.Protocol_type        := io.net_rx.bits.data(319, 304)
//     arp_net_rx.Hardware_type        := io.net_rx.bits.data(303, 288)
//     arp_net_rx.Tehernet_type        := io.net_rx.bits.data(287, 272)
//     arp_net_rx.mac_source           := io.net_rx.bits.data(271, 224)
//     arp_net_rx.mac_destination      := io.net_rx.bits.data(223, 176)
//     val arp_net_tx             = new arp_in
//     val temp                   = Wire(UInt(176.W))
//     val temp2                  = Wire(UInt(16.W))
//     temp                            := 0.U
//     temp2                            := 0.U
//     io.net_tx.bits.data             := Cat(arp_net_tx.asTypeOf(UInt(336.W)),temp)
//     io.net_tx.bits.keep             := Cat(0xffff.U, 0xffff.U,0xffc0.U,temp2)//0XFFFFFFFFFFC00000.U
//     io.net_tx.bits.last             := 1.U

    
//     val mac_addr_out_a         = RegInit(UInt(48.W),0.U)
//     val ip_addr_out_a          = RegInit(UInt(32.W),0.U)
//     val valid_a                = RegInit(UInt(1.W),0.U)
//     val mac_addr_out_b         = RegInit(UInt(48.W),0.U)
//     val ip_addr_out_b          = RegInit(UInt(32.W),0.U)
//     val valid_b                = RegInit(UInt(1.W),0.U)
//     val arp_req1_bits          = RegInit(UInt(32.W),0.U)
//     val arp_req2_bits          = RegInit(UInt(32.W),0.U)


//     io.arp_rsp1.bits.mac_addr           :=  mac_addr_out_a
//     io.arp_rsp2.bits.mac_addr           :=  mac_addr_out_b
//     io.arp_rsp1.bits.hit                := valid_a & arp_req1_bits === ip_addr_out_a
//     io.arp_rsp2.bits.hit                := valid_b & arp_req2_bits === ip_addr_out_b
//     val Tar_protocol_addr     = RegInit(UInt(32.W),0.U)
//     val Tar_hardware_addr     = RegInit(UInt(48.W),0.U)
//     val Send_protocol_addr    = RegInit(UInt(32.W),0.U)
//     val Send_hardware_addr    = RegInit(UInt(48.W),0.U)
//     val operation             = RegInit(UInt(16.W),0.U)
//     val Protocol_len          = RegInit(UInt(8.W),0.U)
//     val Hardware_len          = RegInit(UInt(8.W),0.U)
//     val Protocol_type         = RegInit(UInt(16.W),0.U)
//     val Hardware_type         = RegInit(UInt(16.W),0.U)
//     val Tehernet_type         = RegInit(UInt(16.W),0.U)
//     val mac_source            = RegInit(UInt(48.W),0.U)
//     val mac_destination       = RegInit(UInt(48.W),0.U)

//     val net_rx_last           = RegInit(UInt(1.W),1.U)
//     val net_rx_be_fire        = RegInit(UInt(1.W),0.U)
//     when(io.net_rx.fire()){
//         net_rx_last     := io.net_rx.bits.last
//         net_rx_be_fire  := 1.U
//     }.otherwise{
//         net_rx_be_fire  := 0.U
//     }
//     val net_rx_use_ram                  =  Wire(UInt(1.W))
//     net_rx_use_ram                      := io.net_rx.fire() & net_rx_last
//     io.net_rx.ready                     := ~(net_tx_valid & ~io.net_tx.ready) //1.U 
//     io.arp_req1.ready                   := ~net_rx_use_ram & ~(rsp1_valid & ~io.arp_rsp1.ready)
//     io.arp_req2.ready                   := ~net_rx_use_ram & ~io.arp_req1.valid & ~(rsp2_valid & ~io.arp_rsp2.ready)
    

//     arp_net_tx.Tar_hardware_addr    := mymac
//     arp_net_tx.operation            := 2.U
//     arp_net_tx.Tar_protocol_addr    := Tar_protocol_addr
//     arp_net_tx.Send_hardware_addr   := Send_hardware_addr
//     arp_net_tx.Send_protocol_addr   := Send_protocol_addr
//     arp_net_tx.Protocol_len         := Protocol_len
//     arp_net_tx.Hardware_len         := Hardware_len
//     arp_net_tx.Protocol_type        := Protocol_type
//     arp_net_tx.Hardware_type        := Hardware_type
//     arp_net_tx.Tehernet_type        := Tehernet_type
//     arp_net_tx.mac_source           := mac_source
//     arp_net_tx.mac_destination      := mac_destination


//     val arp_table           =   XRam(UInt(81.W), 256, "auto", 1, 0, "none")
//     val currRntrya          =   Wire(UInt(81.W))
//     val currRntryb          =   Wire(UInt(81.W))
//     val arp_table_write_data=   Wire(UInt(81.W))
//     arp_table.io.data_in_a  :=  arp_table_write_data
//     currRntrya              :=  arp_table.io.data_out_a
//     currRntryb              :=  arp_table.io.data_out_b

//     val resquest            =  1.U
//     val reply               =  2.U

//     io.net_tx.valid         := net_tx_valid | (io.arp_rsp1.fire & ~io.arp_rsp1.bits.hit) | (io.arp_rsp2.fire & ~io.arp_rsp2.bits.hit)
//     io.arp_rsp1.valid       := rsp1_valid
//     io.arp_rsp2.valid       := rsp2_valid

//     net_tx_valid  := 0.U
//     rsp1_valid    := 0.U
//     rsp2_valid    := 0.U

//     when(arp_net_rx.operation === resquest && net_rx_use_ram === 1.U/*io.net_rx.fire() === 1.U*/){       
//         arp_table.io.addr_a :=  arp_net_rx.Send_protocol_addr(31,25)
//         arp_table.io.addr_b :=  0.U//arp_net_rx.Tar_protocol_addr(31,25)
//         arp_table.io.wr_en_a:=  1.U
//         arp_table_write_data:=  Cat(arp_net_rx.Send_hardware_addr, arp_net_rx.Send_protocol_addr, 1.U)
        

//         Tar_protocol_addr    := arp_net_rx.Tar_protocol_addr
//         Send_hardware_addr   := arp_net_rx.Send_hardware_addr
//         Send_protocol_addr   := arp_net_rx.Send_protocol_addr
//         Protocol_len         := arp_net_rx.Protocol_len
//         Hardware_len         := arp_net_rx.Hardware_len
//         Protocol_type        := arp_net_rx.Protocol_type
//         Hardware_type        := arp_net_rx.Hardware_type
//         Tehernet_type        := arp_net_rx.Tehernet_type
//         mac_source           := arp_net_rx.mac_source
//         mac_destination      := arp_net_rx.mac_destination

//         when(arp_net_rx.Tar_protocol_addr === myip){
//             net_tx_valid  := 1.U
//         }.otherwise{
//             net_tx_valid  := 0.U
//         }
//     }.elsewhen(arp_net_rx.operation === reply && net_rx_use_ram === 1.U/*io.net_rx.fire() === 1.U*/){
//         arp_table.io.wr_en_a :=  1.U
//         arp_table.io.addr_a  := arp_net_rx.Tar_protocol_addr(31,25)
//         arp_table.io.addr_b  := 0.U
//         arp_table_write_data :=  Cat(arp_net_rx.Tar_hardware_addr, arp_net_rx.Tar_protocol_addr, 1.U)
//     }.elsewhen( io.arp_req1.fire() === 1.U || io.arp_req2.fire() === 1.U){
//         arp_table.io.wr_en_a :=  0.U
//         arp_table.io.addr_a  := io.arp_req1.bits(31,25)
//         arp_table.io.addr_b  := io.arp_req2.bits(31,25)
//         arp_table_write_data :=  0.U
//         mac_addr_out_a       := currRntrya(80,33)//mac_addr
//         ip_addr_out_a        := currRntrya(32,1)//.ip_addr
//         valid_a              := currRntrya(0)//.valid
//         mac_addr_out_b       := currRntryb(80,33)//.mac_addr
//         ip_addr_out_b        := currRntryb(32,1)//.ip_addr
//         valid_b              := currRntryb(0)//.valid
//         when(io.arp_req1.fire() === 1.U){
//             rsp1_valid           :=  1.U
//             arp_req1_bits        :=  io.arp_req1.bits
//             Tar_protocol_addr     := io.arp_req1.bits
//             Tar_hardware_addr     := 0.U
//             Send_protocol_addr    := myip
//             Send_hardware_addr    := mymac
//             operation             := 1.U
//             Protocol_len          := 4.U
//             Hardware_len          := 6.U
//             Protocol_type         := 0x0008.U
//             Hardware_type         := 0x0100.U
//             Tehernet_type         := 0x0608.U
//             mac_source            := 0.U
//             mac_destination       := 0.U
//             // when((valid_a===1.U & io.arp_req1.bits === ip_addr_out_a)){
//             //     net_tx_valid  := 1.U
//             // }
//         }.elsewhen(io.arp_req2.fire() === 1.U){
//             rsp2_valid           :=  1.U
//             arp_req2_bits        :=  io.arp_req2.bits
//             Tar_protocol_addr     := io.arp_req2.bits
//             Tar_hardware_addr     := 0.U
//             Send_protocol_addr    := myip
//             Send_hardware_addr    := mymac
//             operation             := 1.U
//             Protocol_len          := 4.U
//             Hardware_len          := 6.U
//             Protocol_type         := 0x0008.U
//             Hardware_type         := 0x0100.U
//             Tehernet_type         := 0x0608.U
//             mac_source            := 0.U
//             mac_destination       := 0.U
//             // when((valid_b===1.U && io.arp_req2.bits === ip_addr_out_b)){
//             //     net_tx_valid  := 1.U
//             // }
//         }.otherwise{
//             rsp1_valid           :=  0.U
//             rsp2_valid           :=  0.U
//         }
//     }.otherwise{
//         arp_table.io.wr_en_a := 0.U
//         arp_table.io.addr_a  := 0.U
//         arp_table.io.addr_b  := 0.U
//         arp_table_write_data := 0.U
//     }


// }