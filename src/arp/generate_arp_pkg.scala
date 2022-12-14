package project_foo
import common.Math
import chisel3._
import chisel3.util._
import common.axi._
import common.storage._
import chisel3.experimental.{DataMirror, Direction, requireIsChiselType}

 class arp_in extends Bundle {
    val Tar_protocol_addr     = Wire(UInt(32.W))
    val Tar_hardware_addr     = Wire(UInt(48.W))
    val Send_protocol_addr    = Wire(UInt(32.W))
    val Send_hardware_addr    = Wire(UInt(48.W))
    val operation             = Wire(UInt(16.W))
    val Protocol_len          = Wire(UInt(8.W))
    val Hardware_len          = Wire(UInt(8.W))
    val Protocol_type         = Wire(UInt(16.W))
    val Hardware_type         = Wire(UInt(16.W))
    val Tehernet_type         = Wire(UInt(16.W))
    val mac_source            = Wire(UInt(48.W))
    val mac_destination       = Wire(UInt(48.W))
}

class generate_arp_pkg extends Module{
    val io = IO(new Bundle{
        val replymeta        =   Flipped(Decoupled(UInt(128.W)))
        val requestmeta      =   Flipped(Decoupled(UInt(32.W)))
        val data_out         =   Decoupled(new AXIS(512))
        val mymac            =   Input(UInt(48.W))
        val myip             =   Input(UInt(32.W))
	})
    
    io.replymeta.ready      := 1.U
    io.requestmeta.ready    := 1.U
    val temp                = Wire(UInt(16.W))
    val temp2                = Wire(UInt(144.W))
    val temp3                = Wire(UInt(32.W))
    temp                    := 0.U
    temp2                   := 0.U
    val data_out            = new arp_in()
    io.data_out.bits.data   := Cat(temp3,temp2,data_out.asTypeOf(UInt(336.W)))
    io.data_out.bits.last   := 1.U
    io.data_out.valid       := 0.U
    io.data_out.bits.keep   := Cat(temp,0x003f.U,0xffff.U, 0xffff.U)
    
    when(io.replymeta.fire()){
        io.data_out.valid           := 1.U
        io.requestmeta.ready        := 0.U
        io.data_out.bits.last       := 1.U
        io.data_out.bits.keep       := Cat(temp,0x003f.U,0xffff.U, 0xffff.U)
        data_out.mac_destination    := io.replymeta.bits(127, 80)
        data_out.mac_source         := io.mymac
        data_out.Tehernet_type      := 0x0608.U
        data_out.Hardware_type      := 0x0100.U
        data_out.Protocol_type      := 0x0008.U
        data_out.Hardware_len       := 6.U
        data_out.Protocol_len       := 4.U
        data_out.operation          := 512.U
        data_out.Send_hardware_addr := io.mymac
        data_out.Send_protocol_addr := io.myip
        data_out.Tar_hardware_addr  := io.replymeta.bits(79,32)
        data_out.Tar_protocol_addr  := io.replymeta.bits(31,0)
        temp3                       := 0.U//0x08b1fc58.U//0.U
    }.elsewhen(io.requestmeta.fire()){
        io.data_out.valid := 1.U
        io.data_out.bits.last  := 1.U
        io.data_out.bits.keep  := Cat(temp,0x003f.U,0xffff.U, 0xffff.U)
        data_out.mac_destination    := Cat(0xFFFF.U, 0xFFFF.U,0xFFFF.U)
        data_out.mac_source         := io.mymac
        data_out.Tehernet_type      := 0x0608.U
        data_out.Hardware_type      := 0x0100.U
        data_out.Protocol_type      := 0x0008.U
        data_out.Hardware_len       := 6.U
        data_out.Protocol_len       := 4.U
        data_out.operation          := 256.U
        data_out.Send_hardware_addr := io.mymac
        data_out.Send_protocol_addr := io.myip
        data_out.Tar_hardware_addr  := 0.U
        data_out.Tar_protocol_addr  := io.requestmeta.bits
        temp3                       := 0.U  //a2809866
    }.otherwise{
        temp3                       := 0x669880a2.U  //a2809866
        data_out.mac_destination    := Cat(0xFFFF.U, 0xFFFF.U,0xFFFF.U)
        data_out.mac_source         := io.mymac
        data_out.Tehernet_type      := 0x0608.U
        data_out.Hardware_type      := 0x0100.U
        data_out.Protocol_type      := 0x0008.U
        data_out.Hardware_len       := 6.U
        data_out.Protocol_len       := 4.U
        data_out.operation          := 256.U
        data_out.Send_hardware_addr := io.mymac
        data_out.Send_protocol_addr := io.myip
        data_out.Tar_hardware_addr  := 0.U
        data_out.Tar_protocol_addr  := io.requestmeta.bits   
    }
    
}