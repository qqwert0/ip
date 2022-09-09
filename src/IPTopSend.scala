package project_foo

import chisel3._
import chisel3.util._
import common._
import cmac._
import common.storage._
import common.axi._
import common.ToZero
import common.connection._

class IPTopSend extends RawModule{
	val cmac_pin		= IO(new CMACPin())
	val led 			= IO(Output(UInt(1.W)))
	val sys_100M_0_p	= IO(Input(Clock()))
  	val sys_100M_0_n	= IO(Input(Clock()))

	led := 0.U

	

	val mmcm = Module(new MMCME4_ADV_Wrapper(
		CLKFBOUT_MULT_F 		= 20,
		MMCM_DIVCLK_DIVIDE		= 2,
		MMCM_CLKOUT0_DIVIDE_F	= 4,
		MMCM_CLKOUT1_DIVIDE_F	= 10,
		
		MMCM_CLKIN1_PERIOD 		= 10
	))	
	mmcm.io.CLKIN1	:= IBUFDS(sys_100M_0_p, sys_100M_0_n)
	mmcm.io.RST		:= 0.U

	val dbg_clk 	= BUFG(mmcm.io.CLKOUT1)
	dontTouch(dbg_clk)

	val user_clk = BUFG(mmcm.io.CLKOUT0)
	val user_rstn = mmcm.io.LOCKED

	val cmac = Module(new XCMAC())
	cmac.getTCL("/home/yza/vivado_2021_1/IP/IP.srcs/sources_1/ip")

	cmac_pin		<> cmac.io.pin
	
	cmac.io.m_net_rx.ready := 1.U

  	class ila_rx0(seq:Seq[Data]) extends BaseILA(seq)
  	val mod_rx0 = Module(new ila_rx0(Seq(	
		cmac.io.m_net_rx.valid,
	  	cmac.io.m_net_rx.ready,
    	cmac.io.m_net_rx.bits.data,
    	cmac.io.m_net_rx.bits.last
  	)))
  	mod_rx0.connect(user_clk)


	class ila_tx0(seq:Seq[Data]) extends BaseILA(seq)	  
  	val tx0 = Module(new ila_tx0(Seq(	
		cmac.io.s_net_tx.valid,
	  	cmac.io.s_net_tx.ready,
		cmac.io.s_net_tx.bits.data,
    	cmac.io.s_net_tx.bits.last
  	)))
  	tx0.connect(user_clk)

  	val send = Wire(Bool())
	
	class vio_net(seq:Seq[Data]) extends BaseVIO(seq)
  	val mod_vio = Module(new vio_net(Seq(
    	send
  	)))
  	mod_vio.connect(user_clk)


	withClockAndReset(user_clk,!user_rstn){
		// val mac_ip_encode = Module(new mac_ip_encode("h11112222".U, "hE59D02350A00".U))
		val mac_ip_encode = Module(new mac_ip_encode("h33112222".U, "hE69D02350A00".U))
		val ip_handler = Module(new ip_handler("h33112222".U))
		// val arp = Module(new arp("hE59D02350A00".U, "h11112222".U))
		val arp = Module(new arp("hE69D02350A00".U, "h33112222".U))

		class ila_rx(seq:Seq[Data]) extends BaseILA(seq)
		val arp_rx = Module(new ila_rx(Seq(
			arp.io.net_tx.valid,
			arp.io.net_tx.ready,
			arp.io.net_tx.bits.data,
			arp.io.net_tx.bits.last
		)))
		arp_rx.connect(user_clk)
		class ila_rx1(seq:Seq[Data]) extends BaseILA(seq)
		val arp_rsp2 = Module(new ila_rx1(Seq(
			arp.io.arp_rsp2.bits,
			arp.io.arp_rsp2.valid,
			arp.io.arp_rsp2.ready
		)))
		arp_rsp2.connect(user_clk)
		class ila_rx2(seq:Seq[Data]) extends BaseILA(seq)
		val tcp_out = Module(new ila_rx2(Seq(
			ip_handler.io.tcp_out.valid,
			ip_handler.io.tcp_out.ready,
			ip_handler.io.tcp_out.bits.data,
			ip_handler.io.tcp_out.bits.last
		)))
		tcp_out.connect(user_clk)
		class ila_rx3(seq:Seq[Data]) extends BaseILA(seq)
		val udp_out = Module(new ila_rx3(Seq(
			ip_handler.io.udp_out.valid,
			ip_handler.io.udp_out.ready,
			ip_handler.io.udp_out.bits.data,
			ip_handler.io.udp_out.bits.last
		)))
		udp_out.connect(user_clk)
		class ila_rx4(seq:Seq[Data]) extends BaseILA(seq)
		val icmp_out = Module(new ila_rx4(Seq(
			ip_handler.io.icmp_out.valid,
			ip_handler.io.icmp_out.ready,
			ip_handler.io.icmp_out.bits.data,
			ip_handler.io.icmp_out.bits.last
		)))
		icmp_out.connect(user_clk)
		class ila_rx5(seq:Seq[Data]) extends BaseILA(seq)
		val arp_rsp1 = Module(new ila_rx5(Seq(
			arp.io.arp_rsp1.valid,
			arp.io.arp_rsp1.ready,
			arp.io.arp_rsp1.bits
		)))
		arp_rsp1.connect(user_clk)
		class ila_rx6(seq:Seq[Data]) extends BaseILA(seq)
		val ipencodeout = Module(new ila_rx6(Seq(
			mac_ip_encode.io.data_out.valid,
			mac_ip_encode.io.data_out.ready,
			mac_ip_encode.io.data_out.bits.data,
			mac_ip_encode.io.data_out.bits.last
		)))
		ipencodeout.connect(user_clk)
		class ila_rx7(seq:Seq[Data]) extends BaseILA(seq)
		val arp_out = Module(new ila_rx7(Seq(
			arp.io.net_tx.valid,
			arp.io.net_tx.ready,
			arp.io.net_tx.bits.data,
			arp.io.net_tx.bits.last,
		)))
		arp_out.connect(user_clk)



		class ila_tx(seq:Seq[Data]) extends BaseILA(seq)  
		val data_in = Module(new ila_tx(Seq(	
			ip_handler.io.data_in.valid,
			ip_handler.io.data_in.ready,
			ip_handler.io.data_in.bits.data,
			ip_handler.io.data_in.bits.last
		)))
		data_in.connect(user_clk)
		class ila_tx1(seq:Seq[Data]) extends BaseILA(seq)
		val req2_in = Module(new ila_tx1(Seq(	
			arp.io.arp_req2.valid,
			arp.io.arp_req2.ready,
			arp.io.arp_req2.bits
		)))
		req2_in.connect(user_clk)
		class ila_tx2(seq:Seq[Data]) extends BaseILA(seq)
		val arp_in = Module(new ila_tx2(Seq(	
			arp.io.net_rx.valid,
			arp.io.net_rx.ready,
			arp.io.net_rx.bits.data,
			arp.io.net_rx.bits.last,
			
		)))
		arp_in.connect(user_clk)
		class ila_tx3(seq:Seq[Data]) extends BaseILA(seq)
		val req1_in = Module(new ila_tx3(Seq(	
			arp.io.arp_req1.valid,
			arp.io.arp_req1.ready,
			arp.io.arp_req1.bits
		)))
		req1_in.connect(user_clk)
		class ila_tx4(seq:Seq[Data]) extends BaseILA(seq)
		val ipencodein = Module(new ila_tx4(Seq(	
			mac_ip_encode.io.data_in.valid,
			mac_ip_encode.io.data_in.ready,
			mac_ip_encode.io.data_in.bits.data,
			mac_ip_encode.io.data_in.bits.last
		)))
		ipencodein.connect(user_clk)
		// val send = Wire(Bool())
		
		// class vio_net(seq:Seq[Data]) extends BaseVIO(seq)
		// val mod_vio = Module(new vio_net(Seq(
		// 	send
		// )))
		// mod_vio.connect(user_clk)
		cmac.io.sys_reset 	<> !user_rstn
		val data_cnt = RegInit(0.U(16.W))
		val tx_valid = RegInit(0.U(1.W))
		when(ip_handler.io.data_in.fire()){
			when(data_cnt === 20.U){
				data_cnt	:= 0.U
			}.otherwise{
				data_cnt	:= data_cnt + 1.U;
			}
		}

		when((!RegNext(send))&send){
			tx_valid	:= 1.U
		}.elsewhen(data_cnt === 20.U){
			tx_valid	:= 0.U
		}.otherwise{
			tx_valid	:= tx_valid
		}

		// ip_handler.io.data_in				 <> cmac.io.m_net_rx
		
		arp.io.net_rx						 <> ip_handler.io.arp_out
		arp.io.arp_req1						 <> mac_ip_encode.io.arp_tableout
		mac_ip_encode.io.arp_tablein		 <> arp.io.arp_rsp1
		val arbiter							= SerialArbiter(AXIS(512), 2)
		// val arbiterin						= Vec(2, AXIS(512))
		arbiter.io.in(0)						<> mac_ip_encode.io.data_out
		arbiter.io.in(1)						<> arp.io.net_tx
		// arbiter.io.in							<> arbiterin
		arbiter.io.out							<> cmac.io.s_net_tx
		// cmac.io.s_net_tx.bits						:= arbiter.io.out.bits
		// cmac.io.s_net_tx.valid						:= tx_valid
		// arbiter.io.out.ready					:= 1.U
		


		ip_handler.io.icmp_out.ready 		 := 1.U
		ip_handler.io.tcp_out.ready 		 := 1.U
		ip_handler.io.udp_out.ready			 := 1.U
		ip_handler.io.roce_out.ready		 := 1.U
		

		when(data_cnt === 11.U){
			mac_ip_encode.io.data_in.valid		:= 0.U
			mac_ip_encode.io.data_in.bits.data	:= "h1111_1111_0000_0015_0000_0000_0000_0100_0000_0000_0000_0000_8000_E69D02350A00_E59D02350A00".U
			mac_ip_encode.io.data_in.bits.keep	:= "hffffffffffffffff".U
			mac_ip_encode.io.data_in.bits.last	:= tx_valid
			arp.io.arp_req2.valid               := 0.U
			
			arp.io.arp_req2.bits				:= "h111122222".U
			ip_handler.io.data_in.valid			:= tx_valid
			ip_handler.io.data_in.bits.data		:= "hb33112222_E69D02350A00_33112222_E69D02350A00_02000000_0000_0000_0608_E69D02350A00_E59D02350A00".U
			/**/
			// ip_handler.io.data_in.bits.data			:= "h3311222200000000000011112222E69D02350A00_010004060008_0100_0806_E59D02350A00_ffff_ffff_ffff".U
			ip_handler.io.data_in.bits.keep		:= "hffffffffffffffff".U
			ip_handler.io.data_in.bits.last		:= 1.U
			
		}.elsewhen(data_cnt === 12.U){
			arp.io.arp_req2.valid               := 0.U
			arp.io.arp_req2.bits				:= "h111122222".U
			mac_ip_encode.io.data_in.valid		:= 0.U
			mac_ip_encode.io.data_in.bits.data	:= "h1111_1111_0000_0015_0000_0000_0000_0100_0000_0000_0000_0000_0000_E69D02350A00_E59D02350A00".U
			mac_ip_encode.io.data_in.bits.keep	:= "hffffffffffffffff".U
			mac_ip_encode.io.data_in.bits.last	:= 1.U
			ip_handler.io.data_in.valid			:= tx_valid
			ip_handler.io.data_in.bits.data		:= "hb33112222_E69D02350A00_33112222_E69D02350A00_01000000_0000_0000_0608_E69D02350A00_E59D02350A00".U
			// ip_handler.io.data_in.bits.data		:= "hc11112222_E59D02350A00_11112222_E59D02350A00_02000000_0000_0000_0806_E69D02350A00_E59D02350A00".U
			ip_handler.io.data_in.bits.keep		:= "hffffffffffffffff".U
			ip_handler.io.data_in.bits.last		:= 1.U
		}.elsewhen(data_cnt === 13.U){
			arp.io.arp_req2.valid                := 0.U
			arp.io.arp_req2.bits				 := "h11112222".U
			mac_ip_encode.io.data_in.valid		:= tx_valid
			mac_ip_encode.io.data_in.bits.data	:= "h002e726578656c65_4d206f6c6c65480e_0000de95ffff1050_d2b8719bb1012356_2cdfcd2b_11112222_33112222_b5760640_0000000037000045".U
			mac_ip_encode.io.data_in.bits.keep	:= "h000000ffffffffff".U
			mac_ip_encode.io.data_in.bits.last	:= 1.U
			ip_handler.io.data_in.valid			:= tx_valid
			ip_handler.io.data_in.bits.data		:= "hd1111_1111_0000_0015_0000_0000_0000_0600_0000_0000_0000_0000_0080_E69D02350A00_E59D02350A00".U
			ip_handler.io.data_in.bits.keep		:= "hffffffffffffffff".U
			ip_handler.io.data_in.bits.last		:= 1.U
		}.elsewhen(data_cnt === 14.U){
			arp.io.arp_req2.valid                := tx_valid
			arp.io.arp_req2.bits				 := "h33112222".U
			mac_ip_encode.io.data_in.valid		:= 0.U
			mac_ip_encode.io.data_in.bits.data	:= "h0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_0000_11112222_E59D02350A00_55556666_E79D02350A00_02000000_0000_0000_0000_E69D02350A00_E59D02350A00".U
			mac_ip_encode.io.data_in.bits.keep	:= "hffffffffffffffff".U
			mac_ip_encode.io.data_in.bits.last	:= 1.U
			ip_handler.io.data_in.valid			:= tx_valid
			ip_handler.io.data_in.bits.data		:= "hd1111_1111_0000_0015_0000_0000_0000_0600_0000_0000_0000_0000_8000_E69D02350A00_E59D02350A00".U
			ip_handler.io.data_in.bits.keep		:= "hffffffffffffffff".U
			ip_handler.io.data_in.bits.last		:= 1.U
		}.elsewhen(data_cnt === 15.U){
			arp.io.arp_req2.valid                := 0.U
			arp.io.arp_req2.bits				 := "h11112222".U
			mac_ip_encode.io.data_in.valid		:= 1.U
			mac_ip_encode.io.data_in.bits.data	:= "h002e726578656c65_4d206f6c6c65480e_0000de95ffff1050_d2b8719bb1012356_2cdfcd2b_11112222_33112222_b5760640_0000000037000045".U
			mac_ip_encode.io.data_in.bits.keep	:= "hffffffffffffffff".U
			mac_ip_encode.io.data_in.bits.last	:= 1.U
			ip_handler.io.data_in.valid			:= tx_valid
			ip_handler.io.data_in.bits.data		:= "hd1111_1111_0000_0015_0000_0000_0000_0600_0000_0000_0000_0000_8000_E69D02350A00_E59D02350A00".U
			ip_handler.io.data_in.bits.keep		:= "hffffffffffffffff".U
			ip_handler.io.data_in.bits.last		:= 1.U
		}.elsewhen(data_cnt === 20.U){
			arp.io.arp_req2.valid                := 0.U
			arp.io.arp_req2.bits				 := "h11112222".U
			mac_ip_encode.io.data_in.valid		:= tx_valid
			mac_ip_encode.io.data_in.bits.data	:= "h002e726578656c65_4d206f6c6c65480e_0000de95ffff1050_d2b8719bb1012356_2cdfcd2b_11112222_33112222_b5760640_0000000037000045".U
			mac_ip_encode.io.data_in.bits.keep	:= "h000000ffffffffff".U
			mac_ip_encode.io.data_in.bits.last	:= 1.U
			ip_handler.io.data_in.valid			:= tx_valid
			ip_handler.io.data_in.bits.data		:= "hd1111_1111_0000_0015_0000_0000_0000_0600_0000_0000_0000_0000_0080_E69D02350A00_E59D02350A00".U
			ip_handler.io.data_in.bits.keep		:= "hffffffffffffffff".U
			ip_handler.io.data_in.bits.last		:= 1.U
		}.otherwise{
			mac_ip_encode.io.data_in.valid      := 0.U
			mac_ip_encode.io.data_in.bits.data  := 0.U
			mac_ip_encode.io.data_in.bits.keep	:= "hffffffffffffffff".U
			mac_ip_encode.io.data_in.bits.last	:= 1.U
			ip_handler.io.data_in.valid			:= tx_valid
			ip_handler.io.data_in.bits.data		:= Cat(data_cnt, "h3333_3333_0000_0015_0000_0000_0000_0600_0000_0000_0000_0000_8000_0000_0000_0000_0000_0000_0000".U)
			ip_handler.io.data_in.bits.keep		:= "hffffffffffffffff".U
			ip_handler.io.data_in.bits.last		:= 1.U
		}

		// mac_ip_encode.io.data_in.valid      := 0.U
		// mac_ip_encode.io.data_in.bits.data  := 0.U
		// mac_ip_encode.io.data_in.bits.keep	:= "hffffffffffffffff".U
		// mac_ip_encode.io.data_in.bits.last	:= 1.U

		// ip_handler.io.data_in                <>  cmac.io.m_net_rx

		arp.io.arp_req2.valid                := 0.U
		arp.io.arp_req2.bits				 := "h111122222".U
		arp.io.net_tx.ready					:= 1.U
		arp.io.arp_rsp2.ready				:= 1.U

		mac_ip_encode.io.regdefaultgateway := "h33112222".U
		mac_ip_encode.io.regsubnetmask	   := "h00ffffff".U
		mac_ip_encode.io.data_out.ready	   := 1.U

		
	}	  

	cmac.io.drp_clk         := dbg_clk
	cmac.io.user_clk	    := user_clk
	cmac.io.user_arstn	    := user_rstn
}

// h12345678_11112222_33112222_0000_0610_0000_0000_0200_0054