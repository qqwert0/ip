// package ip
package project_foo
import common.Math
import chisel3._
import chisel3.util._
import common.axi._
import common.storage._


class route_by_eth extends Module{
    val io = IO(new Bundle{
        val data_in          =   Flipped(Decoupled(new AXIS(512)))
        val etherType        =   Input(UInt(16.W))
        val ARP_out          =   Decoupled(new AXIS(512))
        val ipv4_out          =   Decoupled(new AXIS(512))
        
	})
    io.data_in.ready   := io.ipv4_out.ready
    io.ARP_out.bits    := io.data_in.bits
    io.ipv4_out.bits   := io.data_in.bits
    io.ARP_out.valid   := 0.U
    io.ipv4_out.valid  := 0.U
    val type1 = RegInit(UInt(16.W), 0.U)
    val last = RegInit(UInt(1.W), 1.U)
    
    when(io.data_in.fire()){
        when(last === 1.U){
            type1 := io.etherType
            last  := 0.U
            when(io.etherType === 0x0608.U){//ARP
                io.ARP_out.valid   := 1.U
            }.elsewhen(io.etherType === 0x8000.U){//ipv4
                io.ipv4_out.valid   := 1.U
            }
        }.elsewhen(last === 0.U){
            when(type1 === 0x0608.U){//ARP
                io.ARP_out.valid   := 1.U
            }.elsewhen(type1 === 0x8000.U){//ipv4
                io.ipv4_out.valid   := 1.U
            }
        }
        when(io.data_in.bits.last === 1.U){
            last := 1.U
        }
    }

    
}