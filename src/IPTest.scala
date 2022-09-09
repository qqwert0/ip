package project_foo

import chisel3._
import chisel3.util._
import common._
import cmac._
import common.storage._
import common.axi._
import common.ToZero
import common.connection._

class IPTest extends Module{
    val io = IO(new Bundle{
        val data_in         =   Flipped(Decoupled(new AXIS(512)))
        val mac_ip_in         =   Flipped(Decoupled(new AXIS(512)))
        val data_out        =   Decoupled(new AXIS(512))
        val arp_req2        =   Flipped(Decoupled(UInt(32.W)))
        val arp_rsp2        =   Decoupled(new mac_out)
	})
		// ip_handler.io.data_in				 <> cmac.io.m_net_rx
		val mac_ip_encode = Module(new mac_ip_encode("h11112222".U, "hE59D02350A00".U))
		// val mac_ip_encode = Module(new mac_ip_encode("h33112222".U, "hE69D02350A00".U))
		val ip_handler = Module(new ip_handler("h11112222".U))
		val arp = Module(new arp("hE59D02350A00".U, "h11112222".U))
		// val arp = Module(new arp("hE69D02350A00".U, "h33112222".U))
		arp.io.net_rx						 <> ip_handler.io.arp_out
		arp.io.arp_req1						 <> mac_ip_encode.io.arp_tableout
		mac_ip_encode.io.arp_tablein		 <> arp.io.arp_rsp1
		val arbiter							= SerialArbiter(AXIS(512), 2)
		arbiter.io.in(0)						<> mac_ip_encode.io.data_out
		arbiter.io.in(1)						<> arp.io.net_tx
		arbiter.io.out							<> io.data_out
        io.data_in                              <> ip_handler.io.data_in
        arp.io.arp_req2                         <> io.arp_req2
        arp.io.arp_rsp2                         <> io.arp_rsp2
        mac_ip_encode.io.data_in                <> io.mac_ip_in
        ip_handler.io.data_in                   <> io.data_in

		ip_handler.io.icmp_out.ready 		 := 1.U
		ip_handler.io.tcp_out.ready 		 := 1.U
		ip_handler.io.udp_out.ready			 := 1.U
		ip_handler.io.roce_out.ready		 := 1.U



		mac_ip_encode.io.regdefaultgateway := "h33112222".U
		mac_ip_encode.io.regsubnetmask	   := "h00ffffff".U

}

