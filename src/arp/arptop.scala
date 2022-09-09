package project_foo
import common.Math
import chisel3._
import chisel3.util._
import common.axi._
import common.storage._
import chisel3.experimental.{DataMirror, Direction, requireIsChiselType}

class mac_out extends Bundle{
    val mac_addr            = Input(UInt(48.W))
    val hit               = Input(UInt(1.W))
}

class arp (
	mymac 	:	UInt,//(48.W),
	myip	:	UInt//(32.W)
) extends Module{
    val io = IO(new Bundle{
        val net_rx          =   Flipped(Decoupled(new AXIS(512)))
        val net_tx          =   Decoupled(new AXIS(512))
        val arp_req1        =   Flipped(Decoupled(UInt(32.W)))
        val arp_req2        =   Flipped(Decoupled(UInt(32.W)))
        val arp_rsp1        =   Decoupled(new mac_out)
        val arp_rsp2        =   Decoupled(new mac_out)
	})
    val arp_table = Module(new arp_table)
    val generate_arp_pkg = Module(new generate_arp_pkg)
    val process_arp_pkg = Module(new process_arp_pkg)

    process_arp_pkg.io.data_in <> io.net_rx
    process_arp_pkg.io.mymac   := mymac
    process_arp_pkg.io.myip    := myip

    generate_arp_pkg.io.replymeta   <> process_arp_pkg.io.replymeta
    generate_arp_pkg.io.requestmeta <> arp_table.io.requestmeta
    generate_arp_pkg.io.mymac       := mymac
    generate_arp_pkg.io.myip        := myip
    generate_arp_pkg.io.data_out    <> io.net_tx

    arp_table.io.arpinsert          <> process_arp_pkg.io.arpinsert
    arp_table.io.arp_req1           <> io.arp_req1
    arp_table.io.arp_req2           <> io.arp_req2
    arp_table.io.mymac              := mymac
    arp_table.io.myip               := myip
    
    io.arp_rsp1                     <> arp_table.io.arp_rsp1
    io.arp_rsp2                     <> arp_table.io.arp_rsp2 
    



}